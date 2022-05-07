package ClientConnection;

import Packets.*;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.battleships.game.gameinfo.ClientWorld;
import com.battleships.game.factory.LevelFactory;
import com.battleships.game.entity.components.PlayerComponent;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import javax.swing.JOptionPane;
import java.io.IOException;
import java.util.Map;

/**
 * This class manages the client's connection to the server.
 */
public class ClientConnection {
    private final Client client;
    private ClientWorld clientWorld;
    private String playerName;
    private LevelFactory lvlFactory;
    private OrthographicCamera cam;
    private int playerId;
    private int playerskinId;
    private boolean isConnected = false;

    public ClientConnection() {
        String ip = "193.40.156.219";
        final int tcpPort = 8081, udpPort = 8082;

        client = new Client(49152, 49152);
        client.start();

        // Register all packets that are sent over the network.
        client.getKryo().register(PacketAddPlayer.class);
        client.getKryo().register(PacketRemovePlayer.class);
        client.getKryo().register(PacketUpdatePlayerInfo.class);
        client.getKryo().register(Vector2.class);
        client.getKryo().register(PacketAddBullet.class);
        client.getKryo().register(PacketAddLoot.class);
        client.getKryo().register(PacketRemoveLoot.class);

        client.addListener(new Listener() {
            @Override
            public void received(final Connection connection, final Object object) {
                isConnected = true;
                playerId = connection.getID();
                if (object instanceof PacketAddPlayer && clientWorld != null) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            PacketAddPlayer addPlayer = (PacketAddPlayer) object;

                            // Check if client world doesn't contain this player
                            if (!clientWorld.getPlayers().containsKey(addPlayer.getPlayerId()) && addPlayer.getPlayerId() != 0) {
                                Entity player = lvlFactory.createPlayer(cam, addPlayer.getSkinId(), playerId);
                                player.getComponent(PlayerComponent.class).id = addPlayer.getPlayerId();
                                if (clientWorld.getPlayers().size() == 0) {
                                    player.getComponent(PlayerComponent.class).isThisClient = true;
                                }
                                clientWorld.addPlayer(addPlayer.getPlayerId(), player);
                            }
                        }
                    });
                }
                else if (object instanceof PacketRemovePlayer) {
                    PacketRemovePlayer removePlayer = (PacketRemovePlayer) object;
                    Entity playerEntity = clientWorld.getPlayers().get(removePlayer.getId());
                    lvlFactory.removeEntity(playerEntity);
                }
                else if (object instanceof PacketUpdatePlayerInfo) {
                    if (((PacketUpdatePlayerInfo) object).getId() != connection.getID()) {
                        for (Map.Entry<Integer, Entity> entry : clientWorld.getPlayers().entrySet()) {
                            if (entry.getKey() == ((PacketUpdatePlayerInfo) object).getId()) {
                                PlayerComponent playerCom = entry.getValue().getComponent(PlayerComponent.class);
                                if (playerCom != null) {
                                    playerCom.currentHealth = ((PacketUpdatePlayerInfo) object).getCurrentHealth();
                                    playerCom.maxHealth = ((PacketUpdatePlayerInfo) object).getMaxHealth();
                                    playerCom.maxSpeed = ((PacketUpdatePlayerInfo) object).getMaxSpeed();
                                    playerCom.shootDelay = ((PacketUpdatePlayerInfo) object).getShootDelay();
                                    playerCom.bulletDamage = ((PacketUpdatePlayerInfo) object).getBulletDamage();
                                    playerCom.bulletSpeedMultiplier = ((PacketUpdatePlayerInfo) object).getBulletSpeedMultiplier();
                                    playerCom.lastUpdatePacket = (PacketUpdatePlayerInfo) object;
                                    playerCom.needsUpdate = true;
                                }
                            }
                        }
                    }
                }
                else if (object instanceof PacketAddBullet) {
                    int id = ((PacketAddBullet) object).getOwnerId();
                    if (id != connection.getID()) {
                        float x = ((PacketAddBullet) object).getX();
                        float y = ((PacketAddBullet) object).getY();
                        float xVel = ((PacketAddBullet) object).getxVel();
                        float yVel = ((PacketAddBullet) object).getyVel();
                        lvlFactory.createBullet(x, y, xVel, yVel, id);
                    }
                }
                else if (object instanceof PacketAddLoot) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            float x = ((PacketAddLoot) object).getX();
                            float y = ((PacketAddLoot) object).getY();
                            int id = ((PacketAddLoot) object).getId();
                            Entity loot = lvlFactory.createLoot(x, y, id);
                            clientWorld.addLoot(id, loot);
                        }
                    });
                }
                else if (object instanceof PacketRemoveLoot) {
                    PacketRemoveLoot removeLoot = (PacketRemoveLoot) object;
                    Entity lootEntity = clientWorld.getLoot().get(removeLoot.getId());
                    lvlFactory.removeEntity(lootEntity);
                }
            }
        });

        try {
            // Connected to the server - wait 5000ms before failing.
            client.connect(5000, ip, tcpPort, udpPort);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(null, "Can not connect to the Server.");
        }
    }

    /**
     * Send a PacketAddPlayer to server
     */
    public void sendPacketConnect() {
        PacketAddPlayer packetConnect = PacketCreator.createPacketAddPlayer(playerName, playerId, playerskinId);
        client.sendTCP(packetConnect);
    }

    public void setLvlFactory(LevelFactory lvlFactory) {
        this.lvlFactory = lvlFactory;
    }

    public void setCam(OrthographicCamera cam) {
        this.cam = cam;
    }

    public void setClientWorld(ClientWorld world) {
        this.clientWorld = world;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getThisClientId() {
        return playerId;
    }

    public Client getClient() {
        return client;
    }

    public void setPlayerSkinId(int playerskinId) {
        this.playerskinId = playerskinId;
    }

    public boolean getIsConnected() {
        return isConnected;
    }

    public static void main(String[] args) {
        new ClientConnection();
    }
}
