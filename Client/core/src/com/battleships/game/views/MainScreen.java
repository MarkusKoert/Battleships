package com.battleships.game.views;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.battleships.game.Battleships;
import com.battleships.game.entity.systems.TiledMapCollisionSystem;
import com.battleships.game.factory.LevelFactory;
import com.battleships.game.controller.KeyboardController;

public class MainScreen implements Screen {
    private KeyboardController controller;
    private PooledEngine engine;
    private LevelFactory lvlFactory;
    private OrthographicCamera cam;
    private Battleships parent;
    private TiledMapRenderer tiledMapRenderer;
    public static TiledMap tiledMap;
    public float MAP_UNIT_SCALE = 1/12f;

    /**
     * @param battleships - Game instance
     */
    public MainScreen(Battleships battleships){
        parent = battleships;

        // loads and gets images with asset manager
        parent.assMan.queueAddImages();
        parent.assMan.manager.finishLoading();

        // load tiled map
        tiledMap = new TmxMapLoader().load("map/WorldMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, MAP_UNIT_SCALE);

        cam = parent.renderingSystem.getCamera();
        parent.sb.setProjectionMatrix(cam.combined);

        controller = parent.getController();
        engine = parent.getEngine();
        engine.addSystem(new TiledMapCollisionSystem(engine));

        lvlFactory = parent.getLvlFactory();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.6901f, 0.9137f, 0.9882f, 1);
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
