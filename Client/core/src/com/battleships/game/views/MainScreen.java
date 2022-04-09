package com.battleships.game.views;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.battleships.game.Battleships;
import com.battleships.game.BodyFactory;
import com.battleships.game.LevelFactory;
import com.battleships.game.controller.KeyboardController;
import com.battleships.game.entity.systems.*;

public class MainScreen implements Screen {
    private PooledEngine engine;
    private SpriteBatch sb;
    private OrthographicCamera cam;
    private KeyboardController controller;
    private Battleships parent;
    private TiledMapRenderer tiledMapRenderer;
    public static TiledMap tiledMap;
    public float MAP_UNIT_SCALE = 1/12f;
    private Entity player;
    private LevelFactory lvlFactory;

    /**
     * @param battleships - Game instance
     */
    public MainScreen(Battleships battleships){
        parent = battleships;

        // loads and gets images with asset manager
        parent.assMan.queueAddImages();
        parent.assMan.manager.finishLoading();

        //create a pooled engine
        engine = new PooledEngine();
        controller = new KeyboardController();

        //create a level factory (factory for making bullets, enemies, players etc)
        lvlFactory = new LevelFactory(engine,parent.assMan);

        // load tiled map
        tiledMap = new TmxMapLoader().load("map/WorldMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, MAP_UNIT_SCALE);

        // make spritebatch
        sb = new SpriteBatch();
        RenderingSystem renderingSystem = new RenderingSystem(sb);
        cam = renderingSystem.getCamera();
        sb.setProjectionMatrix(cam.combined);

        // add all the relevant systems our engine should run
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new PhysicsSystem(LevelFactory.world));
        engine.addSystem(renderingSystem);
        // engine.addSystem(new PhysicsDebugSystem(LevelFactory.world, renderingSystem.getCamera()));
        engine.addSystem(new CollisionSystem(lvlFactory));
        engine.addSystem(new PlayerControlSystem(controller,lvlFactory));
        player = lvlFactory.createPlayer(cam);
        engine.addSystem(new EnemySystem(lvlFactory));
        engine.addSystem(new BulletSystem(player));
        engine.addSystem(new TiledMapCollisionSystem(engine));
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
}
