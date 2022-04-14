package ServerConnection;

import Packets.*;
import com.badlogic.gdx.math.Vector2;
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
        server.getKryo().register(PacketUpdatePlayerInfo.class);
        server.getKryo().register(Vector2.class);
        server.getKryo().register(PacketAddBullet.class);

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
                        server.sendToTCP(connection.getID(), entry.getValue());
                    }
                } else if (object instanceof PacketUpdatePlayerInfo) {
                    // update existing players clients x, y, angle, vel etc with update package
                    server.sendToAllTCP(object);
                } else if (object instanceof PacketAddBullet) {
                    server.sendToAllTCP(object);
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
