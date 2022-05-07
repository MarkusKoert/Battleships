package com.battleships.game.entity.components;

import Packets.PacketUpdatePlayerInfo;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class PlayerComponent implements Component {
    public boolean isDead = false;
    public OrthographicCamera cam = null;
    public float shootDelay = 2f;
    public float timeSinceLastShot = 0f;
    public int id;
    public int maxHealth = 100;
    public int currentHealth = 100;
    public PacketUpdatePlayerInfo lastUpdatePacket;
    public boolean needsUpdate = false;
    public int skinId;
    public boolean isThisClient = false;
    public int bulletDamage = 10;
    public int bulletSpeedMultiplier = 2;
    public float maxSpeed = 10f;
}
