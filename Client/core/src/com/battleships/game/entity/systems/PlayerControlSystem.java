package com.battleships.game.entity.systems;

import Packets.PacketAddBullet;
import Packets.PacketCreator;
import Packets.PacketUpdatePlayerInfo;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.battleships.game.utility.DFUtils;
import com.battleships.game.entity.components.TextureComponent;
import com.battleships.game.gameinfo.ClientWorld;
import com.battleships.game.factory.LevelFactory;
import com.battleships.game.controller.KeyboardController;
import com.battleships.game.entity.components.B2dBodyComponent;
import com.battleships.game.entity.components.PlayerComponent;
import com.battleships.game.entity.components.StateComponent;

public class PlayerControlSystem extends IteratingSystem {
	private final LevelFactory lvlFactory;
	ComponentMapper<PlayerComponent> pm;
	ComponentMapper<B2dBodyComponent> bodm;
	ComponentMapper<StateComponent> sm;
	ComponentMapper<TextureComponent> txm;
	KeyboardController controller;
	private float entityAcceleration = 0.2f;
	private ClientWorld clientWorld;
	private TextureAtlas shipAtlas;

	public PlayerControlSystem(KeyboardController keyCon, LevelFactory lvlf) {
		super(Family.all(PlayerComponent.class).get());
		controller = keyCon;
		lvlFactory = lvlf;
		this.clientWorld = lvlf.getClientWorld();
		shipAtlas = lvlFactory.getShipAtlas();

		pm = ComponentMapper.getFor(PlayerComponent.class);
		bodm = ComponentMapper.getFor(B2dBodyComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
		txm = ComponentMapper.getFor(TextureComponent.class);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		B2dBodyComponent b2body = bodm.get(entity);
		PlayerComponent player = pm.get(entity);
		TextureComponent texture = txm.get(entity);

		// Changing player skin according to HP
		if (player.skinId == 0) {
			if (player.currentHealth > 50) {
				texture.region = shipAtlas.findRegion("ship (4)");
			} else if (player.currentHealth < 50 && player.currentHealth > 25) {
				texture.region = shipAtlas.findRegion("ship (10)");
			} else if (player.currentHealth <= 25) {
				texture.region = shipAtlas.findRegion("ship (16)");
			}
		} else if (player.skinId == 1) {
			if (player.currentHealth > 50) {
				texture.region = shipAtlas.findRegion("ship (5)");
			} else if (player.currentHealth < 50 && player.currentHealth > 25) {
				texture.region = shipAtlas.findRegion("ship (11)");
			} else if (player.currentHealth <= 25) {
				texture.region = shipAtlas.findRegion("ship (17)");
			}
		} else if (player.skinId == 2) {
			if (player.currentHealth > 50) {
				texture.region = shipAtlas.findRegion("ship (3)");
			} else if (player.currentHealth < 50 && player.currentHealth > 25) {
				texture.region = shipAtlas.findRegion("ship (9)");
			} else if (player.currentHealth <= 25) {
				texture.region = shipAtlas.findRegion("ship (15)");
			}
		} else if (player.skinId == 3) {
			if (player.currentHealth > 50) {
				texture.region = shipAtlas.findRegion("ship (6)");
			} else if (player.currentHealth < 50 && player.currentHealth > 25) {
				texture.region = shipAtlas.findRegion("ship (12)");
			} else if (player.currentHealth <= 25) {
				texture.region = shipAtlas.findRegion("ship (18)");
			}
		}

		//check if player is dead
		if(player.isDead) {
			System.out.println("Player died");
			b2body.isDead = true;
		}

		// update other client entities
		if (this.clientWorld.getThisClientId() != player.id && player.needsUpdate) {
			PacketUpdatePlayerInfo object = player.lastUpdatePacket;
			b2body.body.setTransform(object.getX(), object.getY(), object.getAngle());
		}

		// Movement with controller, check is player is this client.
		if (this.clientWorld.getThisClientId() == player.id) {
			player.cam.position.set(b2body.body.getPosition().x, b2body.body.getPosition().y, 0);

			if(controller.left) {
				b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, -player.maxSpeed, entityAcceleration),b2body.body.getLinearVelocity().y);
			}
			if(controller.right) {
				b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, player.maxSpeed, entityAcceleration),b2body.body.getLinearVelocity().y);
			}
			if(controller.up) {
				b2body.body.setLinearVelocity(b2body.body.getLinearVelocity().x, MathUtils.lerp(b2body.body.getLinearVelocity().y, player.maxSpeed, entityAcceleration));
			}
			if(controller.down) {
				b2body.body.setLinearVelocity(b2body.body.getLinearVelocity().x, MathUtils.lerp(b2body.body.getLinearVelocity().y, -player.maxSpeed, entityAcceleration));
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

					lvlFactory.createBullet(b2body.body.getPosition().x,
							b2body.body.getPosition().y,
							aim.x * player.bulletSpeedMultiplier,
							aim.y * player.bulletSpeedMultiplier,
							player.id);

					// send bullet to server
					sendBulletUpdatePackage(b2body.body.getPosition().x,
							b2body.body.getPosition().y,
							aim.x * player.bulletSpeedMultiplier,
							aim.y * player.bulletSpeedMultiplier,
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
		int currentHealthSend = player.currentHealth;
		int maxHealthSend = player.maxHealth;
		int bulletDamageSend = player.bulletDamage;
		int bulletSpeedMultiplierSend = player.bulletSpeedMultiplier;
		float maxSpeedSend = player.maxSpeed;
		float shootDelaySend = player.shootDelay;
		int playerId = clientWorld.getClientConnection().getThisClientId();
		PacketUpdatePlayerInfo packetUpdatePlayer = PacketCreator.createPacketUpdatePlayer(xSend, ySend, angleSend, currentHealthSend, playerId, maxHealthSend, bulletDamageSend, bulletSpeedMultiplierSend, maxSpeedSend, shootDelaySend);
		clientWorld.getClientConnection().getClient().sendTCP(packetUpdatePlayer);
	}

	/**
	 * @param x - bullets initial x coordinate
	 * @param y - bullets initial y coordinate
	 * @param xVel - bullets initial x velocity
	 * @param yVel - bullets initial y velocity
	 * @param id - owner id
	 */
	private void sendBulletUpdatePackage(float x, float y, float xVel, float yVel, int id) {
		PacketAddBullet packetAddBullet = PacketCreator.createPacketAddBullet(x, y, xVel, yVel, id);
		clientWorld.getClientConnection().getClient().sendTCP(packetAddBullet);
	}

	protected void ChangeBodyAngle(B2dBodyComponent b2body) {
		Vector2 vec = b2body.body.getLinearVelocity();
		double angle = Math.atan2(vec.y, vec.x);
		b2body.body.setTransform(b2body.body.getPosition(), (float) angle);
	}
}
