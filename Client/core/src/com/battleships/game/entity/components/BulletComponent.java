package com.battleships.game.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class BulletComponent implements Component, Poolable{
	public int ownerId;
	public float xVel = 0;
	public float yVel = 0;
	public boolean isDead = false;
	public float initalBulletX;
	public float initalBulletY;
	public int damage;
	
	@Override
	public void reset() {
		ownerId = 0;
		xVel = 0;
		yVel = 0;
		isDead = false;
	}
}