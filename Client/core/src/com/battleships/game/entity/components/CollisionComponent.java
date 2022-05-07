package com.battleships.game.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;


/**
 *  Stores collision data such as entity that this entity has collided with
 */
public class CollisionComponent implements Component, Pool.Poolable {
	public Entity collisionEntity;

	@Override
	public void reset() {
		collisionEntity = null;
	}
}