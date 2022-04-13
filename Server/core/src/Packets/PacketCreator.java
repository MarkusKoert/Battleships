package Packets;

import com.badlogic.gdx.math.Vector2;

public class PacketCreator {

    public static PacketAddPlayer createPacketAddPlayer(String name, int id) {
        PacketAddPlayer packetConnect = new PacketAddPlayer();
        packetConnect.setPlayerName(name);
        return packetConnect;
    }

    public static PacketUpdatePlayerInfo createPacketUpdatePlayer(float x, float y, float angle, int health, int id) {
        PacketUpdatePlayerInfo packetPlayerInfo = new PacketUpdatePlayerInfo();
        packetPlayerInfo.setX(x);
        packetPlayerInfo.setY(y);
        packetPlayerInfo.setAngle(angle);
        packetPlayerInfo.setHealth(health);
        packetPlayerInfo.setId(id);
        return packetPlayerInfo;
    }

    public static PacketRemovePlayer createPacketRemovePlayer(int id) {
        PacketRemovePlayer removePlayer = new PacketRemovePlayer();
        removePlayer.setId(id);
        return removePlayer;
    }
}
