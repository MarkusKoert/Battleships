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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.battleships.game.Battleships;
import com.battleships.game.factory.BodyFactory;

import javax.swing.*;


public class ConnectScreen implements Screen {
    private BodyFactory player;
    private final Sound buttonClick;
    private final Sound buttonHover;
    private final Battleships parent;
    private final Stage stage;
    private boolean playing;
    private final SpriteBatch sb;
    private final Object backGroundTexture;
    private String username;

    public ConnectScreen(Battleships battleships) {
        parent = battleships;

        // Create a new stage and set it as the input processor
        stage = new Stage(new ScreenViewport());

        // tells our asset manger that we want to load the sounds
        parent.assMan.queueAddSounds();
        // tells the asset manager to load the sounds and wait until finsihed loading.
        parent.assMan.manager.finishLoading();
        // loads the 2 sounds we use
        buttonClick = parent.assMan.manager.get("sounds/button_click.wav", Sound.class);
        buttonHover = parent.assMan.manager.get("sounds/button_hover.mp3", Sound.class);

        // tells our asset manger that we want to load the images
        parent.assMan.queueAddImages();
        // tells the asset manager to load the images and wait until finished loading.
        parent.assMan.manager.finishLoading();
        // gets the images as a texture
        backGroundTexture = parent.assMan.manager.get("images/water.png");
        sb = new SpriteBatch();
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);

        // Create table that fills the screen. Everything else goes inside this table.
        Table table = new Table();
        table.setFillParent(true);
        // table.setDebug(true);
        stage.addActor(table);

        // Assign new skin to use for buttons
        Skin skin = new Skin(Gdx.files.internal("skin/game-ui-skin.json"));

        // Title of screen and text field for user input "username"
        final TextField usernameField = new TextField("Username", skin, "default");
        Label titleLabel = new Label("Your nickname", skin, "subtitle");

        // Button for returning to main menu
        final TextButton backButton = new TextButton("back", skin);

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buttonClick.play(0.7F);
                parent.changeScreen(Battleships.MENU);
            }
        });
        backButton.addListener(new InputListener() {
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


        // Button for connect to the server
        final TextButton connectButton = new TextButton("Connect", skin);

        connectButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                username = usernameField.getText();
                if (username.replaceAll("\\s", "").equals("") || username.equals("Username")) {
                    buttonHover.play();
                    playing = true;
                    JOptionPane.showMessageDialog(null, "Please write your player name");
                    return false;
                } else {
                    // change screen

                    buttonHover.play();
                    playing = true;
                    System.out.println("Username: " + username);
                    parent.changeScreen(Battleships.APPLICATION);
                    return super.touchDown(event, x, y, pointer, button);
                }
            }
        });

        table.add(titleLabel).colspan(2);
        table.row().pad(25, 0, 0, 10);
        table.add(usernameField).colspan(2).width(350f);
        table.row().pad(10, 0, 0, 10);
        table.add(backButton).left();
        table.add(connectButton).right();

    }

    public String getUsername() {
        return username;
    }

    @Override
    public void render(float delta) {
        // clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sb.begin();
        sb.draw((Texture) backGroundTexture, 0, 0, 1024, 576);
        sb.end();

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
        // TODO Auto-generated method stub
    }

    public String getPlayer() {
        return username;
    }
}
