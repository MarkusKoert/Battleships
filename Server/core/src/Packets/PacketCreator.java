package Packets;

import com.battleships.game.Battleships;

import java.util.HashMap;
import java.util.Map;

public class PacketCreator {


    public static PacketAddPlayer createPacketAddPlayer(String name) {
        PacketAddPlayer packetConnect = new PacketAddPlayer();
        packetConnect.setPlayerName(name);
        return packetConnect;
    }

    public static PacketUpdatePlayerInfo createPacketUpdatePlayer(float x, float y, float angle, String direction, int health, int id) {
        PacketUpdatePlayerInfo packetPlayerInfo = new PacketUpdatePlayerInfo();
        packetPlayerInfo.setX(x);
        packetPlayerInfo.setY(y);
        packetPlayerInfo.setAngle(angle);
        packetPlayerInfo.setDirection(direction);
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
