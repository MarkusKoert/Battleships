package com.battleships.game.factory;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.battleships.game.B2dContactListener;
import com.battleships.game.GameInfo.ClientWorld;
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

public class LevelFactory {
	public static World world;
	private ClientWorld clientWorld;
	private BodyFactory bodyFactory;
	public PooledEngine engine;
	private TextureRegion playerTex;
	private TextureRegion enemyTex;
	private TextureRegion bulletTex;
	private TextureAtlas atlas;
	public B2dAssetManager assman;

	public LevelFactory(PooledEngine en, B2dAssetManager assMan, ClientWorld clientWorld){
		engine = en;
		this.clientWorld = clientWorld;

		// loads and gets images with asset manager
		assMan.queueAddImages();
		assMan.manager.finishLoading();

		this.atlas = assMan.manager.get("images/ships.atlas", TextureAtlas.class);
		this.assman = assMan;

		bulletTex = atlas.findRegion("cannonBall");
		playerTex = atlas.findRegion("ship (5)");
		enemyTex = atlas.findRegion("ship (2)");
		world = new World(new Vector2(0,0), true);
		world.setContactListener(new B2dContactListener());
		bodyFactory = BodyFactory.getInstance(world);
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
	public Entity createPlayer(OrthographicCamera cam){
		// Create the Entity and all the components that will go in the entity
		Entity entity = engine.createEntity();
		B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		PlayerComponent player = engine.createComponent(PlayerComponent.class);
		CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
		TypeComponent type = engine.createComponent(TypeComponent.class);
		StateComponent stateCom = engine.createComponent(StateComponent.class);
		ConnectionComponent connCom = engine.createComponent(ConnectionComponent.class);
		System.out.println(cam);
		player.cam = cam;

		// create the data for the components and add them to the components
		b2dbody.body = bodyFactory.makeBoxPolyBody(50,50,9.5f, 6, BodyFactory.SHIP, BodyDef.BodyType.DynamicBody,false);

		// set object position (x,y,z) z used to define draw order 0 first drawn
		position.position.set(10,10,1);
		texture.region = playerTex;
		type.type = TypeComponent.PLAYER;
		stateCom.set(StateComponent.STATE_NORMAL);
		b2dbody.body.setUserData(entity);

		// add the components to the entity
		entity.add(b2dbody);
		entity.add(position);
		entity.add(texture);
		entity.add(player);
		entity.add(colComp);
		entity.add(type);
		entity.add(stateCom);
		entity.add(connCom);

		// add the entity to the engine
		engine.addEntity(entity);

		return entity;
	}

	/**
	 * @param x - bullets initial x coordinate
	 * @param y - bullets initial y coordinate
	 * @param xVel - bullets initial x velocity
	 * @param yVel - bullets initial y velocity
	 * @param ownerId - owner entity ID
	 * @return - bullet Entity
	 */
	public Entity createBullet(float x, float y, float xVel, float yVel, int ownerId){
		System.out.println("Making bullet"+x+":"+y+":"+xVel+":"+yVel);
		Entity entity = engine.createEntity();
		B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		AnimationComponent animCom = engine.createComponent(AnimationComponent.class);
		StateComponent stateCom = engine.createComponent(StateComponent.class);
		TypeComponent type = engine.createComponent(TypeComponent.class);
		CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
		BulletComponent bul = engine.createComponent(BulletComponent.class);

		bul.ownerId = ownerId;

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
		entity.add(animCom);
		entity.add(stateCom);
		entity.add(type);

		engine.addEntity(entity);
		return entity;
	}

	/**
	 * @param ent - removes entity from game engine
	 */
	public void removeEntity(Entity ent){
		engine.removeEntity(ent);
	}

	public ClientWorld getClientWorld() {
		return clientWorld;
	}

}
