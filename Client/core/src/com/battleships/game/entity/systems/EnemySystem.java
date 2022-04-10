package com.battleships.game.entity.systems;

import com.badlogic.gdx.math.Vector2;
import com.battleships.game.DFUtils;
import com.battleships.game.LevelFactory;
import com.battleships.game.entity.components.B2dBodyComponent;
import com.battleships.game.entity.components.BulletComponent;
import com.battleships.game.entity.components.EnemyComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class EnemySystem extends IteratingSystem {

	private ComponentMapper<EnemyComponent> em;
	private ComponentMapper<B2dBodyComponent> bodm;
	private LevelFactory levelFactory;
	private Entity player;

	@SuppressWarnings("unchecked")
	public EnemySystem(LevelFactory lvlf){
		super(Family.all(EnemyComponent.class).get());
		em = ComponentMapper.getFor(EnemyComponent.class);
		bodm = ComponentMapper.getFor(B2dBodyComponent.class);
		levelFactory = lvlf;
		player = levelFactory.player;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		EnemyComponent enemyCom = em.get(entity);		// get EnemyComponent
		B2dBodyComponent bodyCom = bodm.get(entity);	// get B2dBodyComponent

		// get distance of enemy from its original start position (pad center)
		float distFromOrig = Math.abs(enemyCom.xPosCenter - bodyCom.body.getPosition().x);

		// if distance > 1 swap direction
		enemyCom.isGoingLeft = (distFromOrig > 1)? !enemyCom.isGoingLeft:enemyCom.isGoingLeft;

		// set speed base on direction
		float speed = enemyCom.isGoingLeft?-0.01f:0.01f;

		// apply speed to body
		bodyCom.body.setTransform(bodyCom.body.getPosition().x + speed,
				bodyCom.body.getPosition().y,
				bodyCom.body.getAngle());


		//check if bullet is dead
		if(enemyCom.isDead){
			System.out.println("Enemy died");
		 	bodyCom.isDead = true;
		}
	}
}