package com.battleships.game.entity.components;

import ClientConnection.ClientConnection;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class PlayerComponent implements Component {
    public boolean isDead = false;
    public OrthographicCamera cam = null;
    public float shootDelay = 2f;
    public float timeSinceLastShot = 0f;

}
