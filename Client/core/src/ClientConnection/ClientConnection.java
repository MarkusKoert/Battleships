package ClientConnection;

import Packets.PacketCreator;
import Packets.PacketRemovePlayer;
import Packets.PacketUpdatePlayerInfo;
import Packets.PacketAddPlayer;
//import com.bigeggs.client.gameInfo.GameClient;
//import com.bigeggs.client.models.GameCharacter;
//import com.bigeggs.client.models.Player;
import com.badlogic.gdx.Gdx;
import com.battleships.game.Battleships;
import com.battleships.game.BodyFactory;
import com.battleships.game.views.MainScreen;
import com.battleships.game.GameInfo.ClientWorld;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
//import com.bigeggs.client.world.ClientWorld;

import javax.swing.*;
import java.io.IOException;


public class ClientConnection {
    private Client client;
    private MainScreen mainScreen;
    private Battleships battleship;
    private ClientWorld clientWorld;
    private String playerName;

    public ClientConnection() {
        String ip = "localhost";
        final int tcpPort = 54555, udpPort = 54777;

        //bodyFactory = BodyFactory.getInstance(world);

        client = new Client();
        client.start();

        // Register all packets that are sent over the network.
        client.getKryo().register(PacketAddPlayer.class);
/*      client.getKryo().register(PacketRemovePlayer.class);
        client.getKryo().register(PacketCreator.class);
        client.getKryo().register(PacketUpdatePlayerInfo.class);
        client.getKryo().register(Battleships.class);
        client.getKryo().register(MainScreen.class);*/
        // need to register packets of GameWorld and Player

        client.addListener(new Listener() {
            @Override
            public void received(final Connection connection, final Object object) {
                if (object instanceof PacketAddPlayer && clientWorld != null) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            PacketAddPlayer successConnect = (PacketAddPlayer) object;
                            if (successConnect.getId() != connection.getID()
                                    || clientWorld.getPlayers().containsKey(successConnect.getId())) {
                                MainScreen.createPlayer();

                            }
                            System.out.println(successConnect.getPlayerName() + " connected!");

                        }
                    });
                } else if (object instanceof PacketUpdatePlayerInfo && clientWorld != null) {
                    final PacketUpdatePlayerInfo playerInfo = (PacketUpdatePlayerInfo) object;
                    if (clientWorld.getPlayers().containsKey(playerInfo.getId())) {
                        MainScreen.createPlayer();
                        // here need to add new player: Position, Angle, Direction and etc.

                    }
                } else if (object instanceof PacketRemovePlayer) {
                    PacketRemovePlayer removePlayer = (PacketRemovePlayer) object;
                    System.out.println("Player disconnected: " + connection.getID());

                    clientWorld.removePlayer(removePlayer.getId());
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

    public void updatePlayer(float x, float y, float angle, String direction, int health) {
        PacketUpdatePlayerInfo updatePlayerInfo = PacketCreator.createPacketUpdatePlayer(x, y, angle, direction, health, client.getID());
        client.sendTCP(updatePlayerInfo);
    }

    public void sendPacketConnect() {
        PacketAddPlayer packetConnect = PacketCreator.createPacketAddPlayer(playerName);
        client.sendTCP(packetConnect);
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }


    public Client getClient() {
        return client;
    }

   /* public void setClient(Battleships client) {
        this.client = client;
    }*/

/*
    public ClientWorld getClientWorld() {
        return clientWorld;
    }

    public void setClientWorld(ClientWorld clientWorld) {
        this.clientWorld = clientWorld;
    }
*/

    public static void main(String[] args) {
        new ClientConnection();
    }
}

