package com.battleships.game.views;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.battleships.game.Battleships;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


public class PreferencesScreen implements Screen {

    private final Sound buttonClick;
    private final Sound buttonHover;
    private Battleships parent;
    private Stage stage;
    private Label titleLabel;
    private Label volumeMusicLabel;
    private Label volumeSoundLabel;
    private Label musicOnOffLabel;
    private Label soundOnOffLabel;
    private boolean playing;
    private SpriteBatch sb;
    private Object backGroundTexture;
    private final Skin skin;

    public PreferencesScreen(Battleships battleships){
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

        // Assign new skin to use for buttons
        skin = new Skin(Gdx.files.internal("skin/game-ui-skin.json"));
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

        // Slider for music volume
        final Slider volumeMusicSlider = new Slider(0f, 1f, 0.1f, false, skin);
        volumeMusicSlider.setValue(parent.getPreferences().getMusicVolume());
        volumeMusicSlider.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                parent.getPreferences().setMusicVolume(volumeMusicSlider.getValue());
                parent.northSea.setVolume(volumeMusicSlider.getValue());
                parent.playingSong.setVolume(volumeMusicSlider.getValue());
                return false;
            }
        });

        // Slider for sound volume
        final Slider volumeSoundSlider = new Slider(0f, 1f, 0.1f,false,skin);
        volumeSoundSlider.setValue(parent.getPreferences().getSoundVolume());
        volumeSoundSlider.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                parent.getPreferences().setSoundVolume(volumeSoundSlider.getValue());
                return false;
            }
        });

        // Checkbox for music volume
        final CheckBox musicCheckbox = new CheckBox(null, skin);
        musicCheckbox.setChecked(parent.getPreferences().isMusicEnabled());
        musicCheckbox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                boolean enabled = musicCheckbox.isChecked();
                parent.getPreferences().setMusicEnabled(enabled);
                if (enabled && !parent.playingSong.isPlaying()) {
                    parent.playingSong.play();
                } else if (!enabled && parent.playingSong.isPlaying()) {
                    parent.playingSong.stop();
                }
                return false;
            }
        });
        musicCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (parent.getPreferences().isSoundEffectsEnabled()) buttonClick.play(parent.getPreferences().getSoundVolume());
            }
        });

        // Checkbox for sound volume
        final CheckBox soundCheckbox = new CheckBox(null, skin);
        soundCheckbox.setChecked((parent.getPreferences().isSoundEffectsEnabled()));
        soundCheckbox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                boolean enabled = soundCheckbox.isChecked();
                parent.getPreferences().setSoundEffectsEnabled(enabled);
                return false;
            }
        });
        soundCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (parent.getPreferences().isSoundEffectsEnabled()) buttonClick.play(parent.getPreferences().getSoundVolume());
            }
        });

        // Button for returning to main menu
        final TextButton backButton = new TextButton("back", skin);

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (parent.getPreferences().isSoundEffectsEnabled()) buttonClick.play(parent.getPreferences().getSoundVolume());
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



        titleLabel = new Label("SETTINGS", skin, "subtitle");
        volumeMusicLabel = new Label("Music volume", skin);
        volumeSoundLabel = new Label("Sound volume", skin);
        musicOnOffLabel = new Label("Music", skin);
        soundOnOffLabel = new Label("Sound", skin);

        table.add(titleLabel).colspan(2);
        table.row().pad(25,0,0,10);
        table.add(volumeMusicLabel).left();
        table.add(volumeMusicSlider);
        table.row().pad(10,0,0,10);
        table.add(musicOnOffLabel).left();
        table.add(musicCheckbox);
        table.row().pad(10,0,0,10);
        table.add(volumeSoundLabel).left();
        table.add(volumeSoundSlider);
        table.row().pad(10,0,0,10);
        table.add(soundOnOffLabel).left();
        table.add(soundCheckbox);
        table.row().pad(10,0,0,10);
        table.add(backButton).colspan(2);
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
        skin.dispose();
    }
}
