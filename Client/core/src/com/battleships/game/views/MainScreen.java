package com.battleships.game.views;

import ClientConnection.ClientConnection;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.battleships.game.B2dContactListener;
import com.battleships.game.Battleships;
import com.battleships.game.BodyFactory;
import com.battleships.game.controller.KeyboardController;
import com.battleships.game.entity.components.*;
import com.battleships.game.entity.systems.*;
import com.battleships.game.world.ClientWorld;

public class MainScreen implements Screen {
    private final World world;
    private static TextureAtlas atlas;
    private static PooledEngine engine;
    private static BodyFactory bodyFactory;
    private SpriteBatch sb;
    private OrthographicCamera cam;
    private KeyboardController controller;
    private Battleships parent;
    private TiledMapRenderer tiledMapRenderer;
    public TiledMap tiledMap;
    public float MAP_UNIT_SCALE = 1/32f;
    private ClientConnection clientConnection;
    private ClientWorld clientWorld;

    public MainScreen(Battleships battleships){
        parent = battleships;
        controller = new KeyboardController();
        world = new World(new Vector2(0,0), true);
        world.setContactListener(new B2dContactListener());
        bodyFactory = BodyFactory.getInstance(world);

        // loads and gets images with asset manager
        parent.assMan.queueAddImages();
        parent.assMan.manager.finishLoading();
        atlas = parent.assMan.manager.get("images/Battleships-pack1.atlas", TextureAtlas.class);

        // load tiled map
        tiledMap = new TmxMapLoader().load("map/WorldMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, MAP_UNIT_SCALE);

        sb = new SpriteBatch();
        RenderingSystem renderingSystem = new RenderingSystem(sb);
        cam = renderingSystem.getCamera();
        sb.setProjectionMatrix(cam.combined);

        //create a pooled engine
        engine = new PooledEngine();

        // add all the relevant systems our engine should run
        engine.addSystem(new AnimationSystem());
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PhysicsDebugSystem(world, renderingSystem.getCamera()));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem(controller, renderingSystem));

        // create some game objects
       // createPlayer();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // for loading map
        tiledMapRenderer.setView(cam);
        tiledMapRenderer.render();

        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
        clientWorld.setClientConnection(this.clientConnection);
    }


    public static void createPlayer(){
        // Create the Entity and all the components that will go in the entity
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        PlayerComponent player = engine.createComponent(PlayerComponent.class);
        CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        StateComponent stateCom = engine.createComponent(StateComponent.class);

        // create the data for the components and add them to the components
        b2dbody.body = bodyFactory.makeCirclePolyBody(10,10,1, BodyFactory.STONE, BodyDef.BodyType.DynamicBody,true);
        // set object position (x,y,z) z used to define draw order 0 first drawn
        position.position.set(10,10,0);
        texture.region = atlas.findRegion("player");
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

        // add the entity to the engine
        engine.addEntity(entity);
    }

}
