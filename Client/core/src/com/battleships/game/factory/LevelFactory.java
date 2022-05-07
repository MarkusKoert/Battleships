package com.battleships.game.factory;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.battleships.game.Battleships;
import com.battleships.game.utility.B2dContactListener;
import com.battleships.game.gameinfo.ClientWorld;
import com.battleships.game.entity.components.*;
import com.battleships.game.loader.B2dAssetManager;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

import java.util.*;

public class LevelFactory {
	public static World world;
	private ClientWorld clientWorld;
	private BodyFactory bodyFactory;
	public PooledEngine engine;
	private TextureRegion playerTex;
	private TextureRegion enemyTex;
	private TextureRegion bulletTex;
	private TextureRegion lootTex1;
	private TextureAtlas atlas;
	private TextureAtlas lootAtlas;
	public B2dAssetManager assman;
	private Sound[] cannonSounds = new Sound[5];
	public Battleships parent;
	Map<Integer, List<Integer>> spawnMap = new HashMap<Integer, List<Integer>>(); // storing the spawnpoints

	public LevelFactory(PooledEngine en, Battleships battleships, ClientWorld clientWorld){
		engine = en;
		this.clientWorld = clientWorld;
		this.assman = battleships.assMan;
		this.parent = battleships;

		// loads and gets images with asset manager
		assman.queueAddImages();
		assman.manager.finishLoading();

		this.atlas = assman.manager.get("images/ships.atlas", TextureAtlas.class);
		this.lootAtlas = assman.manager.get("images/loot.atlas", TextureAtlas.class);

		bulletTex = atlas.findRegion("cannonBall");
		playerTex = atlas.findRegion("ship (1)");
		enemyTex = atlas.findRegion("ship (2)");
		lootTex1 = lootAtlas.findRegion("crate front");
		world = new World(new Vector2(0,0), true);
		world.setContactListener(new B2dContactListener());
		bodyFactory = BodyFactory.getInstance(world);

		// tells our asset manger that we want to load the sounds
		assman.queueAddSounds();
		// tells the asset manager to load the sounds and wait until finsihed loading.
		assman.manager.finishLoading();
		// loads the cannonball sounds
		Sound cannonBlast1 = assman.manager.get("sounds/CannonBlast1.mp3", Sound.class);
		Sound cannonBlast2 = assman.manager.get("sounds/CannonBlast2.mp3", Sound.class);
		Sound cannonBlast3 = assman.manager.get("sounds/CannonBlast3.mp3", Sound.class);
		Sound cannonBlast4 = assman.manager.get("sounds/CannonBlast4.mp3", Sound.class);
		Sound cannonBlast5 = assman.manager.get("sounds/CannonBlast5.mp3", Sound.class);
		cannonSounds[0] = cannonBlast1;
		cannonSounds[1] = cannonBlast2;
		cannonSounds[2] = cannonBlast3;
		cannonSounds[3] = cannonBlast4;
		cannonSounds[4] = cannonBlast5;
	}

	/**
	 * @param x - x coordinate for new enemy
	 * @param y - y coordinate for new enemy
	 * @return enemy entity
	 */
	public Entity createEnemy(float x, float y){
		Entity entity = engine.createEntity();
		B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		EnemyComponent enemy = engine.createComponent(EnemyComponent.class);
		TypeComponent type = engine.createComponent(TypeComponent.class);
		CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
		
		b2dbody.body = bodyFactory.makeBoxPolyBody(x,y,9.5f, 6, BodyFactory.SHIP, BodyDef.BodyType.DynamicBody,false);
		position.position.set(x,y,0);
		texture.region = enemyTex;
		enemy.xPosCenter = x;
		type.type = TypeComponent.ENEMY;
		b2dbody.body.setUserData(entity);
		
		entity.add(colComp);
		entity.add(b2dbody);
		entity.add(position);
		entity.add(texture);
		entity.add(enemy);
		entity.add(type);

		engine.addEntity(entity);

		return entity;
	}

	/**
	 * @param cam - clients camera
	 * @return - player entity
	 */
	public Entity createPlayer(OrthographicCamera cam, int skinId, int playerId){
		// Create the Entity and all the components that will go in the entity
		Entity entity = engine.createEntity();
		B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		PlayerComponent player = engine.createComponent(PlayerComponent.class);
		CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
		TypeComponent type = engine.createComponent(TypeComponent.class);
		StateComponent stateCom = engine.createComponent(StateComponent.class);
		player.cam = cam;

		// get position of spawn points
		float posx = takeSpawnPoints(playerId).get(0);
		float posy = takeSpawnPoints(playerId).get(1);

		// create the data for the components and add them to the components
		b2dbody.body = bodyFactory.makeBoxPolyBody(posx,posy,9.5f, 6, BodyFactory.SHIP, BodyDef.BodyType.DynamicBody,false);
		position.position.set(10,10,1);
		texture.region = playerTex;
		type.type = TypeComponent.PLAYER;
		stateCom.set(StateComponent.STATE_NORMAL);
		b2dbody.body.setUserData(entity);
		player.skinId = skinId;

		// add the components to the entity
		entity.add(b2dbody);
		entity.add(position);
		entity.add(texture);
		entity.add(player);
		entity.add(colComp);
		entity.add(type);
		entity.add(stateCom);

		// add the entity to the engine
		engine.addEntity(entity);

		return entity;
	}

	/**
	 * @param positionMapCounter
	 * @return value of positions based on the player count
	 */
	public List<Integer> takeSpawnPoints(Integer positionMapCounter){
		Integer key = positionMapCounter;
		List<Integer> listOne = Arrays.asList(50, 50);
		List<Integer> listTwo = Arrays.asList(170, 50);
		List<Integer> listThree = Arrays.asList(170, 170);
		List<Integer> listFour = Arrays.asList(25, 320);
		List<Integer> listFive = Arrays.asList(125, 310);
		List<Integer> listSix = Arrays.asList(100, 210);
		List<Integer> listSeven = Arrays.asList(305, 50);
		List<Integer> listEight = Arrays.asList(270, 170);
		List<Integer> listNine = Arrays.asList(310, 310);
		List<Integer> listTen = Arrays.asList(15, 310);
		spawnMap.put(1, listOne);
		spawnMap.put(2, listTwo);
		spawnMap.put(3, listThree);
		spawnMap.put(4, listFour);
		spawnMap.put(5, listFive);
		spawnMap.put(6, listSix);
		spawnMap.put(7, listSeven);
		spawnMap.put(8, listEight);
		spawnMap.put(9, listNine);
		spawnMap.put(10, listTen);
		if (key > 10) {
			key = key % 10;
		}
		if (key==0) {
			key++;
		}
		return spawnMap.get(key);
	}

	/**
	 * @param x - bullets initial x coordinate
	 * @param y - bullets initial y coordinate
	 * @param xVel - bullets initial x velocity
	 * @param yVel - bullets initial y velocity
	 * @param playerId - owner
	 * @return - bullet Entity
	 */
	public Entity createBullet(float x, float y, float xVel, float yVel, int playerId){
		System.out.println("Making bullet"+x+":"+y+":"+xVel+":"+yVel);
		Entity entity = engine.createEntity();
		B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		StateComponent stateCom = engine.createComponent(StateComponent.class);
		TypeComponent type = engine.createComponent(TypeComponent.class);
		CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
		BulletComponent bul = engine.createComponent(BulletComponent.class);

		bul.ownerId = playerId;
		bul.damage = clientWorld.getPlayers().get(playerId).getComponent(PlayerComponent.class).bulletDamage;

		b2dbody.body = bodyFactory.makeCirclePolyBody(x,y,0.5f, BodyFactory.BULLET, BodyType.DynamicBody,true);
		b2dbody.body.setBullet(true); // increase physics computation to limit body travelling through other objects
		bodyFactory.makeAllFixturesSensors(b2dbody.body); // make bullets sensors so they don't move player
		position.position.set(x,y,0);
		texture.region = bulletTex;

		type.type = TypeComponent.BULLET;
		b2dbody.body.setUserData(entity);
		bul.xVel = xVel;
		bul.yVel = yVel;

		bul.initalBulletX = x;
		bul.initalBulletY = y;

		entity.add(bul);
		entity.add(colComp);
		entity.add(b2dbody);
		entity.add(position);
		entity.add(texture);
		entity.add(stateCom);
		entity.add(type);

		engine.addEntity(entity);
		// create a bullet
		if (parent.getPreferences().isSoundEffectsEnabled()) getRandomSound(cannonSounds).play(parent.getPreferences().getSoundVolume());
		return entity;
	}

	public Entity createLoot(float x, float y, int id){
		System.out.println("making loot");
		Entity entity = engine.createEntity();
		B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		TypeComponent type = engine.createComponent(TypeComponent.class);
		CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
		LootComponent loot = engine.createComponent(LootComponent.class);

		b2dbody.body = bodyFactory.makeBoxPolyBody(x,y,2f, 2f, BodyFactory.SHIP, BodyType.DynamicBody,false);
		bodyFactory.makeAllFixturesSensors(b2dbody.body); // make body to sensors so they don't move player
		position.position.set(x,y,0);
		texture.region = lootTex1;
		loot.id = id;

		type.type = TypeComponent.LOOT;
		b2dbody.body.setUserData(entity);

		entity.add(colComp);
		entity.add(b2dbody);
		entity.add(position);
		entity.add(texture);
		entity.add(type);
		entity.add(loot);

		engine.addEntity(entity);

		return entity;
	}

	public static Sound getRandomSound(Sound[] array) {
		int rnd = new Random().nextInt(array.length);
		return array[rnd];
	}

	/**
	 * @param ent - removes entity from game engine
	 */
	public void removeEntity(Entity ent){
		engine.removeEntity(ent);
	}

	public TextureAtlas getShipAtlas() {
		return atlas;
	}

	public ClientWorld getClientWorld() {
		return clientWorld;
	}
}
