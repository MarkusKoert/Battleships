package com.battleships.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.battleships.game.DFUtils;
import com.battleships.game.LevelFactory;
import com.battleships.game.controller.KeyboardController;
import com.battleships.game.entity.components.B2dBodyComponent;
import com.battleships.game.entity.components.BulletComponent;
import com.battleships.game.entity.components.PlayerComponent;
import com.battleships.game.entity.components.StateComponent;

import java.util.Random;

public class PlayerControlSystem extends IteratingSystem{
	private final Sound cannonBlast1;
	private final Sound cannonBlast2;
	private final Sound cannonBlast3;
	private final Sound cannonBlast4;
	private final Sound cannonBlast5;
	private LevelFactory lvlFactory;
	ComponentMapper<PlayerComponent> pm;
	ComponentMapper<B2dBodyComponent> bodm;
	ComponentMapper<StateComponent> sm;
	KeyboardController controller;
	private float entityAcceleration = 0.2f;
	private float entityMaxSpeed = 10f;
	private int bulletSpeedMultiplier = 2;
	private Sound[] cannonSounds = new Sound[5];

	@SuppressWarnings("unchecked")
	public PlayerControlSystem(KeyboardController keyCon, LevelFactory lvlf) {
		super(Family.all(PlayerComponent.class).get());
		controller = keyCon;
		lvlFactory = lvlf;
		pm = ComponentMapper.getFor(PlayerComponent.class);
		bodm = ComponentMapper.getFor(B2dBodyComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);

		// tells our asset manger that we want to load the sounds
		lvlFactory.assman.queueAddSounds();
		// tells the asset manager to load the sounds and wait until finsihed loading.
		lvlFactory.assman.manager.finishLoading();
		// loads the cannonball sounds
		cannonBlast1 = lvlFactory.assman.manager.get("sounds/CannonBlast1.mp3", Sound.class);
		cannonBlast2 = lvlFactory.assman.manager.get("sounds/CannonBlast2.mp3", Sound.class);
		cannonBlast3 = lvlFactory.assman.manager.get("sounds/CannonBlast3.mp3", Sound.class);
		cannonBlast4 = lvlFactory.assman.manager.get("sounds/CannonBlast4.mp3", Sound.class);
		cannonBlast5 = lvlFactory.assman.manager.get("sounds/CannonBlast5.mp3", Sound.class);
		cannonSounds[0] = cannonBlast1;
		cannonSounds[1] = cannonBlast2;
		cannonSounds[2] = cannonBlast3;
		cannonSounds[3] = cannonBlast4;
		cannonSounds[4] = cannonBlast5;
	}

	protected void ChangeBodyAngle(B2dBodyComponent b2body) {
		Vector2 vec = b2body.body.getLinearVelocity();
		double angle = Math.atan2(vec.y, vec.x);
		b2body.body.setTransform(b2body.body.getPosition(), (float) angle);
	}

	public static Sound getRandomSound(Sound[] array) {
		int rnd = new Random().nextInt(array.length);
		return array[rnd];
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		B2dBodyComponent b2body = bodm.get(entity);
		StateComponent state = sm.get(entity);
		PlayerComponent player = pm.get(entity);

		player.cam.position.set(b2body.body.getPosition().x, b2body.body.getPosition().y, 0);

		if(controller.left){
			b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, -entityMaxSpeed, entityAcceleration),b2body.body.getLinearVelocity().y);
		}
		if(controller.right){
			b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, entityMaxSpeed, entityAcceleration),b2body.body.getLinearVelocity().y);
		}
		if(controller.up){
			b2body.body.setLinearVelocity(b2body.body.getLinearVelocity().x, MathUtils.lerp(b2body.body.getLinearVelocity().y, entityMaxSpeed, entityAcceleration));
		}
		if(controller.down){
			b2body.body.setLinearVelocity(b2body.body.getLinearVelocity().x, MathUtils.lerp(b2body.body.getLinearVelocity().y, -entityMaxSpeed, entityAcceleration));
		}

		if(player.timeSinceLastShot > 0){
			player.timeSinceLastShot -= deltaTime;
		}

		if(controller.isMouse1Down){ // if mouse button is pressed
			// System.out.println(player.timeSinceLastShot+" ls:sd "+player.shootDelay);
			// user wants to fire
			if(player.timeSinceLastShot <=0){ // check the player hasn't just shot
				//player can shoot so do player shoot
				Vector3 mousePos = new Vector3(controller.mouseLocation.x,controller.mouseLocation.y,0); // get mouse position
				player.cam.unproject(mousePos); // convert position from screen to box2d world position

				Vector2 aim = DFUtils.aimTo(b2body.body.getPosition(), mousePos);
				aim.scl(7);
				// create a bullet
				getRandomSound(cannonSounds).play();
				lvlFactory.createBullet(b2body.body.getPosition().x,
						b2body.body.getPosition().y,
						aim.x * bulletSpeedMultiplier,
						aim.y * bulletSpeedMultiplier,
						BulletComponent.Owner.PLAYER);
				//reset timeSinceLastShot
				player.timeSinceLastShot = player.shootDelay;
			}
		}

		ChangeBodyAngle(b2body);
	}
}