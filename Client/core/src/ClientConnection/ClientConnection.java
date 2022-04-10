package ClientConnection;

import Packets.PacketCreator;
import Packets.PacketRemovePlayer;
import Packets.PacketAddPlayer;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.battleships.game.Battleships;
import com.battleships.game.GameInfo.ClientWorld;
import com.battleships.game.LevelFactory;
import com.battleships.game.entity.components.PlayerComponent;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;


public class ClientConnection {
    private Client client;
    private ClientWorld clientWorld;
    private String playerName;
    private Battleships gameClient;
    private LevelFactory lvlFactory;
    private OrthographicCamera cam;

    public ClientConnection() {
        String ip = "localhost";
        final int tcpPort = 54555, udpPort = 54777;

        client = new Client();
        client.start();

        // Register all packets that are sent over the network.
        client.getKryo().register(PacketAddPlayer.class);
        client.getKryo().register(PacketRemovePlayer.class);
        client.getKryo().register(Battleships.class);

        client.addListener(new Listener() {
            @Override
            public void received(final Connection connection, final Object object) {

                if (object instanceof PacketAddPlayer && clientWorld != null) {

                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            PacketAddPlayer addPlayer = (PacketAddPlayer) object;
                            // Check if client world not contains and packet if not equal current id

                            System.out.println(addPlayer.getId() + " != " + connection.getID());
                            // Check if client world not contains and packet if not equal current id
                            if (addPlayer.getId() != connection.getID() && !clientWorld.getPlayers().containsKey(addPlayer.getId())) {
                                Entity player = lvlFactory.createPlayer(cam);
                                lvlFactory.setPlayer(player);
                                player.getComponent(PlayerComponent.class).id = addPlayer.getId();
                                clientWorld.addPlayer(addPlayer.getId(), player);

                            }
                            System.out.println(addPlayer.getPlayerName() + " connected!");

                        }
                    });
                }  else if (object instanceof PacketRemovePlayer) {
                    // Get packet to remove player
                    PacketRemovePlayer removePlayer = (PacketRemovePlayer) object;
                    System.out.println("Player disconnected: " + connection.getID());

                    System.out.println("GET PLAYERS: " + clientWorld.getPlayers());
                    for (Map.Entry<Integer, Entity> client : clientWorld.getPlayers().entrySet()) {
                        System.out.println("Key: " + client.getKey() + " Value: "  + client.getValue());
                        if (client.getKey() == removePlayer.getId()) {
                            clientWorld.removePlayer(client.getKey());
                            System.out.println("TEST #1 " + client);
                        }
                    }
                    for (Entity entity : lvlFactory.engine.getEntities()) {
                        System.out.println("player id: " + removePlayer.getId());
                        try {
                            if (entity.getComponent(PlayerComponent.class).id == removePlayer.getId()) {
                                entity.removeAll();
                                System.out.println("TEST #2 " + entity);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
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

    public void setLvlFactory(LevelFactory lvlFactory) {
        this.lvlFactory = lvlFactory;
    }

    public void setCam(OrthographicCamera cam) {
        this.cam = cam;
    }

    public void setClientWorld(ClientWorld world) {
        System.out.println(world);
        this.clientWorld = world;
    }

    public void sendPacketConnect() {
        PacketAddPlayer packetConnect = PacketCreator.createPacketAddPlayer(playerName);
        client.sendTCP(packetConnect);
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setGameClient(Battleships client) {
        this.gameClient = client;
    }

    public Battleships getGameClient() {
        return gameClient;
    }

    public static void main(String[] args) {
        new ClientConnection();
    }
}

