package com.battleships.game.entity.systems;

import Packets.PacketCreator;
import Packets.PacketRemoveLoot;
import Packets.PacketUpdatePlayerInfo;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.audio.Sound;
import com.battleships.game.factory.LevelFactory;
import com.battleships.game.entity.components.*;
import com.battleships.game.gameinfo.ClientWorld;

import java.util.Random;

public class CollisionSystem extends IteratingSystem {
	private ClientWorld clientWorld;
	ComponentMapper<CollisionComponent> cm;
	ComponentMapper<PlayerComponent> pm;
	private final Sound[] impactSounds = new Sound[3];

	public CollisionSystem(LevelFactory lvlFactory) {
		super(Family.all(CollisionComponent.class).get());
		this.clientWorld = lvlFactory.getClientWorld();

		cm = ComponentMapper.getFor(CollisionComponent.class);
		pm = ComponentMapper.getFor(PlayerComponent.class);

		// tells our asset manger that we want to load the sounds
		lvlFactory.assman.queueAddSounds();
		// tells the asset manager to load the sounds and wait until finsihed loading.
		lvlFactory.assman.manager.finishLoading();
		// loads the cannonball sounds
		Sound cannonImpact1 = lvlFactory.assman.manager.get("sounds/cannonImpact1.mp3", Sound.class);
		Sound cannonImpact2 = lvlFactory.assman.manager.get("sounds/cannonImpact2.mp3", Sound.class);
		Sound cannonImpact3 = lvlFactory.assman.manager.get("sounds/cannonImpact3.mp3", Sound.class);
		impactSounds[0] = cannonImpact1;
		impactSounds[1] = cannonImpact2;
		impactSounds[2] = cannonImpact3;
	}

	/**
	 * @param array - array of sounds
	 * @return - random sound from array
	 */
	public static Sound getRandomSound(Sound[] array) {
		int rnd = new Random().nextInt(array.length);
		return array[rnd];
	}

	private void upgradePlayer(PlayerComponent pl, LootComponent loot) {
		switch (loot.lootType) {
			case HealthPack:
				if (pl.currentHealth <= pl.maxHealth - 25) {
					pl.currentHealth += 25;
				} else {
					pl.currentHealth = pl.maxHealth;
				}
				break;
			case CannonUpgrade:
				pl.bulletDamage += 10;
				break;
			case HealthUpgrade:
				pl.maxHealth += 25;
				pl.currentHealth += 25;
				break;
			case SpeedUpgrade:
				pl.maxSpeed += 2f;
				break;
			case ReloadUpgrade:
				pl.shootDelay -= 0.2f;
				break;
			case BulletSpeedUpgrade:
				pl.bulletSpeedMultiplier += 1;
				break;
		}
		loot.isDead = true;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// get collision for this entity
		CollisionComponent cc = cm.get(entity);
		//get collided entity
		Entity collidedEntity = cc.collisionEntity;

		TypeComponent thisType = entity.getComponent(TypeComponent.class);

		// Do Player Collisions
		if (thisType.type == TypeComponent.PLAYER) {
			PlayerComponent pl = pm.get(entity);
			if(collidedEntity != null) {
				TypeComponent type = collidedEntity.getComponent(TypeComponent.class);
				if(type != null) {
					switch(type.type) {
					case TypeComponent.ENEMY:
						//do player hit enemy thing
						System.out.println("player hit enemy");
						break;
					case TypeComponent.LOOT:
						LootComponent loot = collidedEntity.getComponent(LootComponent.class);
						B2dBodyComponent lootBody = collidedEntity.getComponent(B2dBodyComponent.class);
						upgradePlayer(pl, loot);
						lootBody.isDead = true;
						System.out.println(loot.lootType);
						System.out.println("player hit loot");
						sendRemoveLootPacket(loot.getId());
						break;
					case TypeComponent.OTHER:
						//do player hit other thing
						System.out.println("player hit other");
						break;
					case TypeComponent.BULLET:
						BulletComponent bullet = Mapper.bulletCom.get(collidedEntity);
						// can't shoot yourself
						if(bullet.ownerId != pl.id) {
							bullet.isDead = true;
							getRandomSound(impactSounds).play(0.4f);
							pl.currentHealth -= bullet.damage;
							if (pl.currentHealth <= 0) {
								pl.isDead = true;
							}
							System.out.println("A player just got shot.");
						}
						break;
					default:
						System.out.println("No matching type found");
					}
					cc.collisionEntity = null; // collision handled reset component
				}
			}
		}
		else if(thisType.type == TypeComponent.ENEMY) {
			if(collidedEntity != null) {
				TypeComponent type = collidedEntity.getComponent(TypeComponent.class);
				if(type != null) {
					switch(type.type) {
					case TypeComponent.PLAYER:
						System.out.println("enemy hit player");
						break;
					case TypeComponent.ENEMY:
						System.out.println("enemy hit enemy");
						break;
					case TypeComponent.LOOT:
						System.out.println("enemy hit scenery");
						break;
					case TypeComponent.OTHER:
						System.out.println("enemy hit other");
						break;
					case TypeComponent.BULLET:
						EnemyComponent enemy = Mapper.enemyCom.get(entity);
						BulletComponent bullet = Mapper.bulletCom.get(collidedEntity);
						bullet.isDead = true;
						getRandomSound(impactSounds).play(0.4f);
						enemy.health -= bullet.damage;
						if (enemy.health <= 0) {
							enemy.isDead = true;
						}
						System.out.println("enemy got shot");
						break;
					default:
						System.out.println("No matching type found");
					}
					cc.collisionEntity = null; // collision handled reset component
				}
			}
		}
		else{
			cc.collisionEntity = null;
		}
	}

	private void sendRemoveLootPacket(int id) {
		PacketRemoveLoot packetRemoveLoot = PacketCreator.createPacketRemoveLoot(id);
		clientWorld.getClientConnection().getClient().sendTCP(packetRemoveLoot);
	}

}
