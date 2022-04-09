package ClientConnection;

import Packets.PacketRemovePlayer;
import Packets.PacketAddPlayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;
import com.battleships.game.Battleships;
import com.battleships.game.views.MainScreen;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import javax.swing.*;
import java.io.IOException;


public class ClientConnection {
    private Client client;
    private World clientWorld;

    public ClientConnection() {
        String ip = "localhost";
        final int tcpPort = 54555, udpPort = 54777;

        client = new Client();
        client.start();
        System.out.println(client);

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
                            PacketAddPlayer successConnect = (PacketAddPlayer) object;
                            System.out.println(successConnect.getPlayerName() + " connected!");

                        }
                    });
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


    public void setClientWorld(World world) {
        System.out.println(world);
        this.clientWorld = world;
    }

    public static void main(String[] args) {
        new ClientConnection();
    }
}

