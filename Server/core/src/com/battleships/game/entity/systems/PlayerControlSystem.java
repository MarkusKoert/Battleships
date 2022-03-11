package com.battleships.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.battleships.game.controller.KeyboardController;
import com.battleships.game.entity.components.B2dBodyComponent;
import com.battleships.game.entity.components.PlayerComponent;
import com.battleships.game.entity.components.StateComponent;

public class PlayerControlSystem extends IteratingSystem{
 
	ComponentMapper<PlayerComponent> pm;
	ComponentMapper<B2dBodyComponent> bodm;
	ComponentMapper<StateComponent> sm;
	KeyboardController controller;
	private OrthographicCamera cam;

	@SuppressWarnings("unchecked")
	public PlayerControlSystem(KeyboardController keyCon, RenderingSystem renderingSystem) {
		super(Family.all(PlayerComponent.class).get());
		controller = keyCon;
		pm = ComponentMapper.getFor(PlayerComponent.class);
		bodm = ComponentMapper.getFor(B2dBodyComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
		cam = renderingSystem.getCamera();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		B2dBodyComponent b2body = bodm.get(entity);
		StateComponent state = sm.get(entity);
		
		if(controller.left){
			b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, -5f, 0.2f),b2body.body.getLinearVelocity().y);
			cam.position.set(b2body.body.getPosition().x, b2body.body.getPosition().y, 0);
		}
		if(controller.right){
			b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, 5f, 0.2f),b2body.body.getLinearVelocity().y);
			cam.position.set(b2body.body.getPosition().x, b2body.body.getPosition().y, 0);
		}
		if(controller.up){
			b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().y, 5f, 0.2f),b2body.body.getLinearVelocity().x);
			cam.position.set(b2body.body.getPosition().x, b2body.body.getPosition().y, 0);
		}
		if(controller.down){
			b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().y, -5f, 0.2f),b2body.body.getLinearVelocity().x);
			cam.position.set(b2body.body.getPosition().x, b2body.body.getPosition().y, 0);
		}
	}
}