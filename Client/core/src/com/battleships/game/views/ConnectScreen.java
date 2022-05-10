package com.battleships.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.battleships.game.Battleships;
import javax.swing.JOptionPane;

public class ConnectScreen implements Screen {
    private final Sound buttonClick;
    private final Sound buttonHover;
    private final Battleships parent;
    private final Stage stage;
    private boolean playing;
    private final SpriteBatch sb;
    private final Object backGroundTexture;
    private String username;
    private ButtonGroup buttonGroup;
    private boolean isSkinSelected = false;

    public ConnectScreen(Battleships battleships) {
        parent = battleships;

        // Create a new stage and set it as the input processor
        stage = new Stage(new ScreenViewport());

        // tells our asset manger that we want to load the sounds
        parent.assMan.queueAddSounds();
        // tells the asset manager to load the sounds and wait until finished loading.
        parent.assMan.manager.finishLoading();
        // loads the 2 sounds we use
        buttonClick = parent.assMan.manager.get("sounds/button_click.wav", Sound.class);
        buttonHover = parent.assMan.manager.get("sounds/button_hover.mp3", Sound.class);

        // tells our asset manger that we want to load the images
        parent.assMan.queueAddImages();
        // tells the asset manager to load the images and wait until finished loading.
        parent.assMan.manager.finishLoading();
        // gets the images as a texture
        backGroundTexture = parent.assMan.manager.get("images/waterConnect.png");
        sb = new SpriteBatch();

    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);

        // Create table that fills the screen. Everything else goes inside this table.
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Assign new skin to use for buttons
        Skin skin = new Skin(Gdx.files.internal("skin/game-ui-skin.json"));

        // Title of screen and text field for user input "username"
        final TextField usernameField = new TextField("Username", skin, "default");
        Label titleLabel = new Label("Your nickname", skin, "subtitle");
        Label chooseLabel = new Label("Choose a skin for your ship:", skin, "subtitle");

        // Making buttons
        final TextButton backButton = new TextButton("back", skin);
        final TextButton connectButton = new TextButton("Connect", skin);

        // Listeners for buttons
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buttonClick.play(parent.getPreferences().getSoundVolume());
                parent.changeScreen(Battleships.MENU);
            }
        });
        backButton.addListener(new InputListener() {
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

        // Delete username on click
        usernameField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                usernameField.setText("");
            }
        });

        connectButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                username = usernameField.getText();
                if (parent.getPreferences().isSoundEffectsEnabled()) buttonHover.play(parent.getPreferences().getSoundVolume());
                playing = true;
                if (username.replaceAll("\\s", "").equals("")
                        || username.equals("Username") ) {
                    JOptionPane.showMessageDialog(null, "Please write your player name");
                    return false;
                }
                else if (!isSkinSelected) {
                    JOptionPane.showMessageDialog(null, "Please choose the skin for your ship");
                    return false;
                }
                else {
                    parent.createClient(parent.getClientWorld());

                    // wait for a response from server
                    try
                    {
                        Thread.sleep(3000);
                    }
                    catch(InterruptedException ex)
                    {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println("IS GAME IN PROGRESS:" + parent.getClientWorld().gameInProgess);
                    System.out.println("PLAYERS IN GAME:" + parent.getClientWorld().currentPlayerCount);

                    if (parent.getClientWorld().currentPlayerCount >= parent.getLobbyScreen().getLobbyPlayerCount() || parent.getClientWorld().gameInProgess) {
                        JOptionPane.showMessageDialog(null, "A game is currently in progress, try again later.");
                        return false;
                    }

                    parent.getClientWorld().getClientConnection().sendPacketConnect();
                    if (!parent.getClientWorld().getClientConnection().gotConnection) {
                        return false;
                    }

                    parent.changeScreen(Battleships.LOBBY);
                    return super.touchDown(event, x, y, pointer, button);
                }
            }
        });

        // Checkbox skin choosing
        final CheckBox skinOneCheckbox = new CheckBox(null, skin);
        final CheckBox skinTwoCheckbox = new CheckBox(null, skin);
        final CheckBox skinThreeCheckbox = new CheckBox(null, skin);
        final CheckBox skinFourCheckbox = new CheckBox(null, skin);
        buttonGroup = new ButtonGroup(skinOneCheckbox, skinTwoCheckbox, skinThreeCheckbox, skinFourCheckbox);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);

        skinOneCheckbox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                isSkinSelected = skinOneCheckbox.isChecked();
                // asd
                return false;
            }
        });
        skinOneCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (parent.getPreferences().isSoundEffectsEnabled()) buttonClick.play(parent.getPreferences().getSoundVolume());
            }
        });

        skinTwoCheckbox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                isSkinSelected = skinTwoCheckbox.isChecked();
                // do stuff
                return false;
            }
        });
        skinTwoCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (parent.getPreferences().isSoundEffectsEnabled()) buttonClick.play(parent.getPreferences().getSoundVolume());
            }
        });

        skinThreeCheckbox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                isSkinSelected = skinThreeCheckbox.isChecked();
                // do stuff
                return false;
            }
        });
        skinThreeCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (parent.getPreferences().isSoundEffectsEnabled()) buttonClick.play(parent.getPreferences().getSoundVolume());
            }
        });

        skinFourCheckbox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                isSkinSelected = skinFourCheckbox.isChecked();
                // do stuff
                return false;
            }
        });
        skinFourCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (parent.getPreferences().isSoundEffectsEnabled()) buttonClick.play(parent.getPreferences().getSoundVolume());
            }
        });

        table.add(titleLabel).colspan(4);
        table.row().pad(25, 0, 0, 10);
        table.add(usernameField).colspan(4).width(350f);
        table.row().pad(10, 0, 0, 10);
        table.add(backButton).colspan(2).left();
        table.add(connectButton).colspan(2).right();
        table.row().pad(50, 0, 0, 10);
        table.add(chooseLabel).colspan(4);
        table.row().pad(100, 0, 0, 10);
        table.add(skinOneCheckbox);
        table.add(skinTwoCheckbox);
        table.add(skinThreeCheckbox);
        table.add(skinFourCheckbox);
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
        stage.dispose();
        stage.clear();
    }

    public String getPlayer() {
        return username;
    }

    /**
     * @return the chosen player skin index.
     */
    public int getCheckedSkin() {
        return buttonGroup.getCheckedIndex();
    }
}
