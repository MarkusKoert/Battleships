package com.battleships.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.battleships.game.Battleships;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class MenuScreen implements Screen {

    private final Stage stage;
    private final Skin skin;
    private final Sound buttonClick;
    private final Sound buttonHover;
    private Battleships parent;
    private boolean playing;
    private SpriteBatch sb;
    private OrthographicCamera cam;

    public MenuScreen(Battleships battleships){
        parent = battleships;
        cam = new OrthographicCamera(1024, 576);

        // Create a new stage and set it as the input processor
        stage = new Stage(new ScreenViewport());


        parent.assMan.queueAddSkin();  //new
        parent.assMan.manager.finishLoading(); // new
        skin = parent.assMan.manager.get("skin/game-ui-skin.json"); // new

        // tells our asset manger that we want to load the sounds
        parent.assMan.queueAddSounds();
        // tells the asset manager to load the sounds and wait until finsihed loading.
        parent.assMan.manager.finishLoading();
        // loads the 2 sounds we use
        buttonClick = parent.assMan.manager.get("sounds/button_click.wav", Sound.class);
        buttonHover = parent.assMan.manager.get("sounds/button_hover.mp3", Sound.class);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // Create table that fills the screen. Everything else goes inside this table.
        Table table = new Table();
        table.setFillParent(true);
        // table.setDebug(true);
        stage.addActor(table);

        Label titleLabel = new Label("BATTLESHIPS", skin, "title");
        TextButton newGame = new TextButton("New Game", skin);
        TextButton preferences = new TextButton("Settings", skin);
        TextButton exit = new TextButton("Exit", skin);

        table.add(titleLabel).fillX().uniformX();
        table.row().pad(50,0,10,0);
        table.add(newGame).fillX().uniformX();
        table.row().pad(10,0,10,0);
        table.add(preferences).fillX().uniformX();
        table.row().pad(10,0,10,0);
        table.add(exit).fillX().uniformX();

        // Create listeners for menu screen buttons
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buttonClick.play(0.7F);
                Gdx.app.exit();
            }
        });
        exit.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (!playing) {
                    buttonHover.play();
                    playing = true;
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                playing = false;
            }
        });

        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buttonClick.play(0.7F);
                parent.changeScreen(Battleships.APPLICATION);
            }
        });
        newGame.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (!playing) {
                    buttonHover.play();
                    playing = true;
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                playing = false;
            }
        });

        preferences.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buttonClick.play(0.7F);
                parent.changeScreen(Battleships.PREFERENCES);
            }
        });
        preferences.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (!playing) {
                    buttonHover.play();
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

        // tell stage to do actions and draw
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // change stage's viewport according ot screen size
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

