package com.battleships.game.entity.systems;

import Packets.PacketAddBullet;
import Packets.PacketCreator;
import Packets.PacketUpdatePlayerInfo;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.battleships.game.DFUtils;
import com.battleships.game.GameInfo.ClientWorld;
import com.battleships.game.factory.LevelFactory;
import com.battleships.game.controller.KeyboardController;
import com.battleships.game.entity.components.B2dBodyComponent;
import com.battleships.game.entity.components.PlayerComponent;
import com.battleships.game.entity.components.StateComponent;

import java.util.Random;

public class PlayerControlSystem extends IteratingSystem {
	private final LevelFactory lvlFactory;
	ComponentMapper<PlayerComponent> pm;
	ComponentMapper<B2dBodyComponent> bodm;
	ComponentMapper<StateComponent> sm;
	KeyboardController controller;
	private float entityAcceleration = 0.2f;
	private float entityMaxSpeed = 10f;
	private int bulletSpeedMultiplier = 2;
	private Sound[] cannonSounds = new Sound[5];
	private ClientWorld clientWorld;

	public PlayerControlSystem(KeyboardController keyCon, LevelFactory lvlf) {
		super(Family.all(PlayerComponent.class).get());
		controller = keyCon;
		lvlFactory = lvlf;
		this.clientWorld = lvlf.getClientWorld();

		pm = ComponentMapper.getFor(PlayerComponent.class);
		bodm = ComponentMapper.getFor(B2dBodyComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);

		// tells our asset manger that we want to load the sounds
		lvlFactory.assman.queueAddSounds();
		// tells the asset manager to load the sounds and wait until finsihed loading.
		lvlFactory.assman.manager.finishLoading();
		// loads the cannonball sounds
		Sound cannonBlast1 = lvlFactory.assman.manager.get("sounds/CannonBlast1.mp3", Sound.class);
		Sound cannonBlast2 = lvlFactory.assman.manager.get("sounds/CannonBlast2.mp3", Sound.class);
		Sound cannonBlast3 = lvlFactory.assman.manager.get("sounds/CannonBlast3.mp3", Sound.class);
		Sound cannonBlast4 = lvlFactory.assman.manager.get("sounds/CannonBlast4.mp3", Sound.class);
		Sound cannonBlast5 = lvlFactory.assman.manager.get("sounds/CannonBlast5.mp3", Sound.class);
		cannonSounds[0] = cannonBlast1;
		cannonSounds[1] = cannonBlast2;
		cannonSounds[2] = cannonBlast3;
		cannonSounds[3] = cannonBlast4;
		cannonSounds[4] = cannonBlast5;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		B2dBodyComponent b2body = bodm.get(entity);
		PlayerComponent player = pm.get(entity);

		//check if player is dead
		if(player.isDead) {
			System.out.println("Player died");
			b2body.isDead = true;
		}

		// update other client entities
		if (this.clientWorld.getThisClientId() != player.id && player.needsUpdate) {
			PacketUpdatePlayerInfo object = player.lastUpdatePacket;
			b2body.body.setTransform(((PacketUpdatePlayerInfo) object).getX(), ((PacketUpdatePlayerInfo) object).getY(), ((PacketUpdatePlayerInfo) object).getAngle());
		}

		// Movement with controller, check is player is this client.
		if (this.clientWorld.getThisClientId() == player.id) {
			player.cam.position.set(b2body.body.getPosition().x, b2body.body.getPosition().y, 0);

			if(controller.left) {
				b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, -entityMaxSpeed, entityAcceleration),b2body.body.getLinearVelocity().y);
			}
			if(controller.right) {
				b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, entityMaxSpeed, entityAcceleration),b2body.body.getLinearVelocity().y);
			}
			if(controller.up) {
				b2body.body.setLinearVelocity(b2body.body.getLinearVelocity().x, MathUtils.lerp(b2body.body.getLinearVelocity().y, entityMaxSpeed, entityAcceleration));
			}
			if(controller.down) {
				b2body.body.setLinearVelocity(b2body.body.getLinearVelocity().x, MathUtils.lerp(b2body.body.getLinearVelocity().y, -entityMaxSpeed, entityAcceleration));
			}

			if(player.timeSinceLastShot > 0) {
				player.timeSinceLastShot -= deltaTime;
			}

			if(controller.isMouse1Down){
				// user wants to fire
				if(player.timeSinceLastShot <=0) {
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
							player.id);

					// send bullet to server
					sendBulletUpdatePackage(b2body.body.getPosition().x,
							b2body.body.getPosition().y,
							aim.x * bulletSpeedMultiplier,
							aim.y * bulletSpeedMultiplier,
							player.id);

					// reset timeSinceLastShot
					player.timeSinceLastShot = player.shootDelay;
				}
			}
			ChangeBodyAngle(b2body);
			// Send update package
			sendPlayerUpdatePackage(b2body, player);
		}
	}

	/**
	 * @param b2body - body component
	 * @param player - player component
	 */
	private void sendPlayerUpdatePackage(B2dBodyComponent b2body, PlayerComponent player) {
		float xSend = b2body.body.getPosition().x;
		float ySend = b2body.body.getPosition().y;
		float angleSend = b2body.body.getAngle();
		int healthSend = player.health;
		int playerId = clientWorld.getClientConnection().getThisClientId();
		PacketUpdatePlayerInfo packetUpdatePlayer = PacketCreator.createPacketUpdatePlayer(xSend, ySend, angleSend, healthSend, playerId);
		clientWorld.getClientConnection().getClient().sendTCP(packetUpdatePlayer);
	}

	/**
	 * @param x - bullets initial x coordinate
	 * @param y - bullets initial y coordinate
	 * @param xVel - bullets initial x velocity
	 * @param yVel - bullets initial y velocity
	 * @param ownerId - owner entity ID
	 */
	private void sendBulletUpdatePackage(float x, float y, float xVel, float yVel, int ownerId) {
		PacketAddBullet packetAddBullet = PacketCreator.createPacketAddBullet(x, y, xVel, yVel, ownerId);
		clientWorld.getClientConnection().getClient().sendTCP(packetAddBullet);
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
}