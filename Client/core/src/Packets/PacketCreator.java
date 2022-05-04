package Packets;

public class PacketCreator {

    public static PacketAddPlayer createPacketAddPlayer(String name, int id, int skinId) {
        PacketAddPlayer packetConnect = new PacketAddPlayer();
        packetConnect.setPlayerName(name);
        packetConnect.setSkinId(skinId);
        return packetConnect;
    }

    public static PacketUpdatePlayerInfo createPacketUpdatePlayer(float x, float y, float angle, int health, int id, int maxHealthSend, int bulletDamageSend, int bulletSpeedMultiplierSend, float maxSpeedSend, float shootDelaySend) {
        PacketUpdatePlayerInfo packetPlayerInfo = new PacketUpdatePlayerInfo();
        packetPlayerInfo.setX(x);
        packetPlayerInfo.setY(y);
        packetPlayerInfo.setAngle(angle);
        packetPlayerInfo.setCurrentHealth(health);
        packetPlayerInfo.setId(id);
        packetPlayerInfo.setBulletDamage(bulletDamageSend);
        packetPlayerInfo.setBulletSpeedMultiplier(bulletSpeedMultiplierSend);
        packetPlayerInfo.setMaxHealth(maxHealthSend);
        packetPlayerInfo.setShootDelay(shootDelaySend);
        packetPlayerInfo.setMaxSpeed(maxSpeedSend);
        return packetPlayerInfo;
    }

    public static PacketRemovePlayer createPacketRemovePlayer(int id) {
        PacketRemovePlayer removePlayer = new PacketRemovePlayer();
        removePlayer.setId(id);
        return removePlayer;
    }

    public static PacketAddLoot createPacketAddLoot(float x, float y, int id) {
        PacketAddLoot addLoot = new PacketAddLoot();
        addLoot.setX(x);
        addLoot.setY(y);
        addLoot.setId(id);
        return addLoot;
    }

    public static PacketRemoveLoot createPacketRemoveLoot(int id) {
        PacketRemoveLoot removeLoot = new PacketRemoveLoot();
        removeLoot.setId(id);
        return removeLoot;
    }

    public static PacketAddBullet createPacketAddBullet(float x, float y, float xVel, float yVel, int id) {
        PacketAddBullet packetAddBullet = new PacketAddBullet();
        packetAddBullet.setX(x);
        packetAddBullet.setY(y);
        packetAddBullet.setxVel(xVel);
        packetAddBullet.setyVel(yVel);
        packetAddBullet.setOwnerId(id);
        return packetAddBullet;
    }
}
