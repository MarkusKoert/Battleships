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
    public final String gameImages = "images/ships.atlas";
    public final String effectImages = "images/effects.atlas";
    public final String lootImages = "images/loot.atlas";
    public final String backGroundimage = "images/water.png";
    public final String connectGroundimage = "images/waterConnect.png";
    // Skin
    public final String skin = "skin/game-ui-skin.json";
    // Sounds
    public final String buttonClick = "sounds/button_click.wav";
    public final String buttonHover = "sounds/button_hover.mp3";
    public final String cannonBlast1 = "sounds/CannonBlast1.mp3";
    public final String cannonBlast2 = "sounds/CannonBlast2.mp3";
    public final String cannonBlast3 = "sounds/CannonBlast3.mp3";
    public final String cannonBlast4 = "sounds/CannonBlast4.mp3";
    public final String cannonBlast5 = "sounds/CannonBlast5.mp3";
    public final String cannonImpact1 = "sounds/cannonImpact1.mp3";
    public final String cannonImpact2 = "sounds/cannonImpact2.mp3";
    public final String cannonImpact3 = "sounds/cannonImpact3.mp3";
    // music
    public final String northSea = "sounds/north_sea.mp3";
    public final String soundTrack = "sounds/main_soundtrack.mp3";

    public void queueAddImages(){
        manager.load(gameImages, TextureAtlas.class);
        manager.load(effectImages, TextureAtlas.class);
        manager.load(lootImages, TextureAtlas.class);
        manager.load(backGroundimage, Texture.class);
        manager.load(connectGroundimage, Texture.class);
    }

    public void queueAddSkin(){
        SkinParameter params = new SkinParameter("skin/game-ui-skin.atlas");
        manager.load(skin, Skin.class, params);
    }

    public void queueAddSounds(){
        manager.load(buttonClick, Sound.class);
        manager.load(buttonHover, Sound.class);
        manager.load(cannonBlast1, Sound.class);
        manager.load(cannonBlast2, Sound.class);
        manager.load(cannonBlast3, Sound.class);
        manager.load(cannonBlast4, Sound.class);
        manager.load(cannonBlast5, Sound.class);
        manager.load(cannonImpact1, Sound.class);
        manager.load(cannonImpact2, Sound.class);
        manager.load(cannonImpact3, Sound.class);
    }

    public void queueAddMusic(){
        manager.load(northSea, Music.class);
        manager.load(soundTrack, Music.class);
    }
}
