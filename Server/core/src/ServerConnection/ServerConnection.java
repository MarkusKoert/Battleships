package ServerConnection;

import Packets.PacketAddPlayer;
import Packets.PacketCreator;
import Packets.PacketRemovePlayer;
import com.battleships.game.server.ServerWorld;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ServerConnection extends Listener {
    //Server object
    private Server server;
    //Ports to listen on
    static final int tcpPort = 54555, udpPort = 54777;
    private final ServerWorld serverWorld = new ServerWorld();
    static Map<Integer, String> players = new HashMap<Integer, String>();

    public ServerConnection() {
        try {
            server = new Server();
            server.start(); // handle incoming connections, reading/writing to the socket, and notifying listeners
            server.bind(tcpPort, udpPort);
            System.out.println("[INFO] The server is running.");
        } catch (IOException exception) {
            // If catch IO exception, then show error message.
            JOptionPane.showMessageDialog(null, "[ERROR] Can not start the Server.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Register all packets from package Packets that match with the ClientConnection
        // and are sent over the network.
        // register() from kryo:3.0.1 library
        server.getKryo().register(PacketAddPlayer.class);
        server.getKryo().register(PacketRemovePlayer.class);
      //  server.getKryo().register(PacketCreator.class);
      //  server.getKryo().register(PacketUpdatePlayerInfo.class);

        // Listener to handle receiving objects
        server.addListener(new Listener() {


            public void received (Connection connection, Object object) {
                if (object instanceof PacketAddPlayer) {
                    PacketAddPlayer connect = (PacketAddPlayer) object;
                    addPlayer(connect.getId(), connect.getPlayerName());
                    System.out.println(connect.getPlayerName() + " is connected to the server.");

                }
            }

            @Override
            public void disconnected(Connection connection) {
                PacketRemovePlayer removePlayer = PacketCreator.createPacketRemovePlayer(connection.getID());
                serverWorld.removeId(connection.getID());
                System.out.println(players.get(connection.getID() - 1) + " is disconnected from the server.");

                server.sendToAllTCP(removePlayer);
            }
        });
    }

    private void addPlayer(Integer id, String username) {
        players.put(id, username);
    }

/*    public static void main(String[] args) {
        new ServerConnection();
    }*/
}
