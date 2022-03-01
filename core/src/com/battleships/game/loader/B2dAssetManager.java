package com.battleships.game.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class B2dAssetManager {
    public final AssetManager manager = new AssetManager();
    // Textures
    public final String gameImages = "images/Battleships-pack1.atlas";
    public final String playerImage = "images/player.png";
    public final String enemyImage = "images/enemy.png";
    public final String backGroundimage = "images/water.png";
    // Skin
    public final String skin = "skin/game-ui-skin.json";
    // Sounds
    public final String buttonClick = "sounds/button_click.wav";
    public final String buttonHover = "sounds/button_hover.mp3";
    // music
    public final String playingSong = "sounds/north_sea.mp3";

    public void queueAddImages(){
        manager.load(playerImage, Texture.class);
        manager.load(enemyImage, Texture.class);
        manager.load(gameImages, TextureAtlas.class);
        manager.load(backGroundimage, Texture.class);
    }

    public void queueAddSkin(){
        SkinParameter params = new SkinParameter("skin/game-ui-skin.atlas");
        manager.load(skin, Skin.class, params);
    }

    public void queueAddSounds(){
        manager.load(buttonClick, Sound.class);
        manager.load(buttonHover, Sound.class);
    }

    public void queueAddMusic(){
        manager.load(playingSong, Music.class);
    }
}
