package com.battleships.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.battleships.game.B2DModel;
import com.battleships.game.Battleships;
import com.battleships.game.controller.KeyboardController;

public class MainScreen implements Screen {
    private final Box2DDebugRenderer debugRenderer;
    private SpriteBatch sb;
    private Object playerTex;
    private OrthographicCamera cam;
    private KeyboardController controller;
    private Battleships parent;
    private B2DModel model;
    private TiledMapRenderer tiledMapRenderer;
    TiledMap tiledMap;

    public MainScreen(Battleships battleships){
        parent = battleships;
        cam = new OrthographicCamera(16, 16);
        controller = new KeyboardController();
        model = new B2DModel(controller,cam,parent.assMan);
        debugRenderer = new Box2DDebugRenderer(true,true,true,true,true,true);
        sb = new SpriteBatch();
        sb.setProjectionMatrix(cam.combined);

        // tells our asset manger that we want to load the images set in loadImages method
        parent.assMan.queueAddImages();
        // tells the asset manager to load the images and wait until finished loading.
        parent.assMan.manager.finishLoading();
        // gets the images as a texture
        playerTex = parent.assMan.manager.get("images/player.png");

        // load tiled map
        // tiledMap = new TmxMapLoader().load("map/WorldMap.tmx");
        // tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);
    }


    @Override
    public void render(float delta) {
        model.logicStep(delta);
        Gdx.gl.glClearColor(0, 41, 58, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugRenderer.render(model.world, cam.combined);

        sb.begin();
        sb.draw((Texture) playerTex,model.player.getPosition().x -1,model.player.getPosition().y -1,2,2);
        sb.end();

        cam.update();
        // tiledMapRenderer.setView(cam);
        // tiledMapRenderer.render();
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
