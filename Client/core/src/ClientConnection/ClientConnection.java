package ClientConnection;

import Packets.PacketCreator;
import Packets.PacketRemovePlayer;
import Packets.PacketAddPlayer;
import Packets.PacketUpdatePlayerInfo;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.battleships.game.Battleships;
import com.battleships.game.GameInfo.ClientWorld;
import com.battleships.game.entity.components.B2dBodyComponent;
import com.battleships.game.factory.LevelFactory;
import com.battleships.game.entity.components.PlayerComponent;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import javax.swing.*;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;

public class ClientConnection {
    private Client client;
    private ClientWorld clientWorld;
    private String playerName;
    private Battleships gameClient;
    private LevelFactory lvlFactory;
    private OrthographicCamera cam;
    private int playerId;

    public ClientConnection() {
        String ip = "localhost";
        final int tcpPort = 54555, udpPort = 54777;

        client = new Client();
        client.start();

        // Register all packets that are sent over the network.
        client.getKryo().register(PacketAddPlayer.class);
        client.getKryo().register(PacketRemovePlayer.class);
        client.getKryo().register(PacketUpdatePlayerInfo.class);
        client.getKryo().register(Vector2.class);

        client.addListener(new Listener() {
            @Override
            public void received(final Connection connection, final Object object) {
                playerId = connection.getID();
                if (object instanceof PacketAddPlayer && clientWorld != null) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            PacketAddPlayer addPlayer = (PacketAddPlayer) object;
                            System.out.println(addPlayer.getPlayerId() + " Recieved packet player ID");

                            // Check if client world doesn't contain this player
                            if (!clientWorld.getPlayers().containsKey(addPlayer.getPlayerId()) && addPlayer.getPlayerId() != 0) {
                                Entity player = lvlFactory.createPlayer(cam);
                                player.getComponent(PlayerComponent.class).id = addPlayer.getPlayerId();
                                clientWorld.addPlayer(addPlayer.getPlayerId(), player);
                            }
                        }
                    });
                }  else if (object instanceof PacketRemovePlayer) {
                    PacketRemovePlayer removePlayer = (PacketRemovePlayer) object;

                    // Remove player from clientWorld map.
                    for (Map.Entry<Integer, Entity> player : clientWorld.getPlayers().entrySet()) {
                        if (player.getKey() == removePlayer.getId()) {
                            clientWorld.removePlayer(player.getKey());
                        }
                    }

                    // Remove player entity from client engine
                    for (Entity entity : lvlFactory.engine.getEntities()) {
                        try {
                            if (entity.getComponent(PlayerComponent.class).id == removePlayer.getId()) {
                                entity.removeAll();
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (object instanceof PacketUpdatePlayerInfo) {
                    if (((PacketUpdatePlayerInfo) object).getId() != connection.getID()) {
                        for (Map.Entry<Integer, Entity> entry : clientWorld.getPlayers().entrySet()) {
                            if (entry.getKey() == ((PacketUpdatePlayerInfo) object).getId()) {
                                System.out.println("matching entity, updating...");
                                PlayerComponent playerCom = entry.getValue().getComponent(PlayerComponent.class);
                                B2dBodyComponent bodyCom = entry.getValue().getComponent(B2dBodyComponent.class);
                                playerCom.health = ((PacketUpdatePlayerInfo) object).getHealth();
                                bodyCom.body.setTransform(((PacketUpdatePlayerInfo) object).getX(), ((PacketUpdatePlayerInfo) object).getY(), ((PacketUpdatePlayerInfo) object).getAngle());
                            }
                        }
                    }
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

    public void sendPacketConnect() {
        PacketAddPlayer packetConnect = PacketCreator.createPacketAddPlayer(playerName, playerId);
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

    public String getPlayerName() {
        return playerName;
    }

    public void setGameClient(Battleships client) {
        this.gameClient = client;
    }

    public static void main(String[] args) {
        new ClientConnection();
    }

    public int getThisClientId() {
        return playerId;
    }

    public Client getClient() {
        return client;
    }
}
