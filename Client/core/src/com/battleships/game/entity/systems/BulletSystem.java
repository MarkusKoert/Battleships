package com.battleships.game.entity.systems;

import com.battleships.game.entity.components.B2dBodyComponent;
import com.battleships.game.entity.components.BulletComponent;
import com.battleships.game.entity.components.Mapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class BulletSystem extends IteratingSystem {
	private int bulletSpeedMultiplier = 3;
	private int bulletRange = 40;

	@SuppressWarnings("unchecked")
	public BulletSystem() {
		super(Family.all(BulletComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		//get box 2d body and bullet components
		B2dBodyComponent b2body = Mapper.b2dCom.get(entity);
		BulletComponent bullet = Mapper.bulletCom.get(entity);

		// apply bullet velocity to bullet body
		b2body.body.setLinearVelocity(bullet.xVel * bulletSpeedMultiplier, bullet.yVel * bulletSpeedMultiplier);

		//get bullet pos
		float bx = b2body.body.getPosition().x;
		float by = b2body.body.getPosition().y;

		// if bullet is 20 units away from player on any axis then it is probably off screen
		if(bx - bullet.initalBulletX > bulletRange || by - bullet.initalBulletY > bulletRange){
			bullet.isDead = true;
		}

		//check if bullet is dead
		if(bullet.isDead){
			System.out.println("Bullet died");
			b2body.isDead = true;
		}
	}
}
