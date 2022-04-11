package com.battleships.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.audio.Sound;
import com.battleships.game.GameInfo.ClientWorld;
import com.battleships.game.factory.LevelFactory;
import com.battleships.game.entity.components.*;

import java.util.Random;

public class CollisionSystem extends IteratingSystem {
	ComponentMapper<CollisionComponent> cm;
	ComponentMapper<PlayerComponent> pm;
	private final Sound cannonImpact1;
	private final Sound cannonImpact2;
	private final Sound cannonImpact3;
	private Sound[] impactSounds = new Sound[3];
	private LevelFactory lvlFactory;

	@SuppressWarnings("unchecked")
	public CollisionSystem(LevelFactory lvlFactory) {
		super(Family.all(CollisionComponent.class).get());
		this.lvlFactory = lvlFactory;

		cm = ComponentMapper.getFor(CollisionComponent.class);
		pm = ComponentMapper.getFor(PlayerComponent.class);

		// tells our asset manger that we want to load the sounds
		lvlFactory.assman.queueAddSounds();
		// tells the asset manager to load the sounds and wait until finsihed loading.
		lvlFactory.assman.manager.finishLoading();
		// loads the cannonball sounds
		cannonImpact1 = lvlFactory.assman.manager.get("sounds/cannonImpact1.mp3", Sound.class);
		cannonImpact2 = lvlFactory.assman.manager.get("sounds/cannonImpact2.mp3", Sound.class);
		cannonImpact3 = lvlFactory.assman.manager.get("sounds/cannonImpact3.mp3", Sound.class);
		impactSounds[0] = cannonImpact1;
		impactSounds[1] = cannonImpact2;
		impactSounds[2] = cannonImpact3;
	}

	public static Sound getRandomSound(Sound[] array) {
		int rnd = new Random().nextInt(array.length);
		return array[rnd];
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
					case TypeComponent.SCENERY:
						//do player hit scenery thing
						System.out.println("player hit scenery");
						break;
					case TypeComponent.OTHER:
						//do player hit other thing
						System.out.println("player hit other");
						break;
					case TypeComponent.BULLET:
						BulletComponent bullet = Mapper.bulletCom.get(collidedEntity);
						// cant shoot yourself
						if(bullet.ownerId != pl.id) {
							bullet.isDead = true;
							getRandomSound(impactSounds).play(0.4f);
							pl.health -= 25;
							if (pl.health <= 0) {
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
		// Enemy type collisions (AI)
		}else if(thisType.type == TypeComponent.ENEMY) {
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
					case TypeComponent.SCENERY:
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
						enemy.health -= 25;
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
		}else{
			cc.collisionEntity = null;
		}
	}
}
