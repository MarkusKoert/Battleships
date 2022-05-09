package com.battleships.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.battleships.game.Battleships;


public class EndScreen implements Screen {
    private Battleships parent;
    private final Stage stage;
    private final Skin skin;
    private final Sound buttonClick;
    private final Sound buttonHover;
    private boolean playing;
    private SpriteBatch sb;
    private Object backGroundTexture;


    public EndScreen(Battleships battleships){
        parent = battleships;

        // Create a new stage and set it as the input processor
        stage = new Stage(new ScreenViewport());

        parent.assMan.queueAddSkin();  //new
        parent.assMan.manager.finishLoading(); // new
        skin = parent.assMan.manager.get("skin/game-ui-skin.json"); // new

        // tells our asset manger that we want to load the images
        parent.assMan.queueAddImages();
        // tells the asset manager to load the images and wait until finished loading.
        parent.assMan.manager.finishLoading();
        // gets the images as a texture
        backGroundTexture = parent.assMan.manager.get("images/water.png");

        sb = new SpriteBatch();
        // tells our asset manger that we want to load the sounds
        parent.assMan.queueAddSounds();
        // tells the asset manager to load the sounds and wait until finsihed loading.
        parent.assMan.manager.finishLoading();
        // loads the 2 sounds we use
        buttonClick = parent.assMan.manager.get("sounds/button_click.wav", Sound.class);
        buttonHover = parent.assMan.manager.get("sounds/button_hover.mp3", Sound.class);    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // Create table that fills the screen. Everything else goes inside this table.
        Table table = new Table();
        table.setFillParent(true);
        // table.setDebug(true);
        stage.addActor(table);

        String winOrLoss = "";

        if (parent.getLvlFactory().getAliveplayers() == 1) {
            winOrLoss = "You Won";
            parent.getClientWorld().getClientConnection().sendPacketGameState(false);
        } else {
            winOrLoss = "You Lost";
        }

        parent.getLvlFactory().engine.removeAllEntities();

        Label titleLabel = new Label("GAME OVER", skin, "title");
        Label wonLabel = new Label(winOrLoss, skin, "subtitle");
        TextButton mainMenu = new TextButton("Back to menu", skin);
        TextButton exit = new TextButton("Exit", skin);

        table.add(titleLabel).uniformX();
        table.row().pad(50,0,10,0);
        table.add(wonLabel).uniformX();
        table.row().pad(50,0,10,0);
        table.add(mainMenu).uniformX();
        table.row().pad(10,0,10,0);
        table.add(exit).uniformX();

        // Create listeners for menu screen buttons
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buttonClick.play(parent.getPreferences().getSoundVolume());
                Gdx.app.exit();
            }
        });

        mainMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (parent.getPreferences().isSoundEffectsEnabled()) buttonClick.play(parent.getPreferences().getSoundVolume());
                parent.getClientWorld().getClientConnection().getClient().stop();
                parent.changeScreen(Battleships.MENU);
            }
        });
        mainMenu.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (!playing) {
                    if (parent.getPreferences().isSoundEffectsEnabled()) buttonHover.play(parent.getPreferences().getSoundVolume());
                    playing = true;
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                playing = false;
            }
        });
    }

    @Override
    public void render(float delta) {
        // clear screen
        Gdx.gl.glClearColor(0f,0f,0f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sb.begin();
        sb.draw((Texture) backGroundTexture, 0, 0,1024,576);
        sb.end();

        // tell stage to do actions and draw
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
    }
}
