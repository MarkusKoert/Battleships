package com.battleships.game.entity.components;
 
import com.badlogic.ashley.core.Component;

public class EnemyComponent implements Component {
	public boolean isDead = false;
	public float xPosCenter = -1;
	public boolean isGoingLeft = false;
	public int health = 100;
	public float shootDelay = 2f;
	public float timeSinceLastShot = 0f;
}