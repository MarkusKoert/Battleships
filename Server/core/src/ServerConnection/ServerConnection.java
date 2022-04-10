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
import java.util.Map;

public class ServerConnection extends Listener {
    //Server object
    private Server server;
    //Ports to listen on
    static final int tcpPort = 54555, udpPort = 54777;
    private ServerWorld serverWorld;

    public ServerConnection() {
        try {
            server = new Server();
            server.start(); // handle incoming connections, reading/writing to the socket, and notifying listeners
            server.bind(tcpPort, udpPort);
            this.serverWorld = new ServerWorld();

        } catch (IOException exception) {
            // If catch IO exception, then show error message.
            JOptionPane.showMessageDialog(null, "[ERROR] Can not start the Server.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Register all packets from package Packets that match with the ClientConnection
        server.getKryo().register(PacketAddPlayer.class);
        server.getKryo().register(PacketRemovePlayer.class);

        // Listener to handle receiving objects
        server.addListener(new Listener() {
            @Override
            public void received (Connection connection, Object object) {
                if (object instanceof PacketAddPlayer) {
                    PacketAddPlayer connectPlayer = (PacketAddPlayer) object;
                    connectPlayer.setPlayerId(connection.getID());

                    // Add player to serverWorld and send packet to all clients.
                    serverWorld.addPlayer(connection.getID(), connectPlayer);
                    server.sendToAllTCP(connectPlayer);

                    // add existing players to newly connected players game.
                    for (Map.Entry<Integer, PacketAddPlayer> entry : serverWorld.getPlayers().entrySet()) {
                        PacketAddPlayer packetAddExistingPlayer = PacketCreator.createPacketAddPlayer(entry.getValue().getPlayerName(), entry.getValue().getPlayerId());
                        server.sendToTCP(connection.getID(), packetAddExistingPlayer);
                    }
                }
            }

            @Override
            public void disconnected(Connection connection) {
                PacketRemovePlayer removePlayer = PacketCreator.createPacketRemovePlayer(connection.getID());

                // Remove player from serverWorld and send packet to all clients
                serverWorld.removePlayer(connection.getID());
                server.sendToAllTCP(removePlayer);
            }
        });
    }

    public static void main(String[] args) {
        new ServerConnection();
    }
}
