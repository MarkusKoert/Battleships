package com.battleships.game;

import ClientConnection.ClientConnection;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.battleships.game.gameinfo.ClientWorld;
import com.battleships.game.controller.KeyboardController;
import com.battleships.game.entity.systems.*;
import com.battleships.game.factory.LevelFactory;
import com.battleships.game.loader.B2dAssetManager;
import com.battleships.game.views.*;

public class Battleships extends Game {
	private LoadingScreen loadingScreen;
	private PreferencesScreen preferencesScreen;
	private MenuScreen menuScreen;
	private MainScreen mainScreen;
	private EndScreen endScreen;
	private ConnectScreen connectScreen;
	private AppPreferences preferences;
	private LobbyScreen lobbyScreen;
	public B2dAssetManager assMan = new B2dAssetManager();
	public SpriteBatch sb;
	private OrthographicCamera cam;
	public RenderingSystem renderingSystem;
	private ClientConnection clientConnection;
	public final static int MENU = 0;
	public final static int PREFERENCES = 1;
	public final static int APPLICATION = 2;
	public final static int ENDGAME = 3;
	public final static int CONNECT = 4;
	public final static int LOBBY = 5;
	public Music playingSong;
	public Music northSea;
	private LevelFactory lvlFactory;
	private PooledEngine engine;
	private KeyboardController controller;
	private ClientWorld clientWorld;

	@Override
	public void create () {
		loadingScreen = new LoadingScreen(this);
		preferences = new AppPreferences();
		preferences.setMusicEnabled(true);
		preferences.setSoundEffectsEnabled(true);
		setScreen(loadingScreen);

		// tells our asset manger that we want to load the images set in loadImages method
		assMan.queueAddMusic();
		// tells the asset manager to load the images and wait until finished loading.
		assMan.manager.finishLoading();
		// loads the 2 sounds we use
		playingSong = assMan.manager.get("sounds/main_soundtrack.mp3");
		northSea = assMan.manager.get("sounds/north_sea.mp3");
		playingSong.setVolume(preferences.getMusicVolume());
		northSea.setVolume(preferences.getMusicVolume());
		playingSong.play();
		playingSong.setLooping(true);

		sb = new SpriteBatch();
		renderingSystem = new RenderingSystem(sb);
		cam = renderingSystem.getCamera();

		// create a pooled engine
		engine = new PooledEngine();
		controller = new KeyboardController();

		// create client world to hold game info
		clientWorld = new ClientWorld();

		// create a level factory (factory for making bullets, enemies, players etc)
		lvlFactory = new LevelFactory(engine,this, clientWorld);

		// add all the relevant systems our engine should run
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new PhysicsSystem(LevelFactory.world));
		engine.addSystem(renderingSystem);
		// engine.addSystem(new PhysicsDebugSystem(LevelFactory.world, renderingSystem.getCamera()));
		engine.addSystem(new CollisionSystem(lvlFactory));
		engine.addSystem(new PlayerControlSystem(controller,lvlFactory));
		engine.addSystem(new EnemySystem(lvlFactory));
		engine.addSystem(new BulletSystem());

		lobbyScreen = lobbyScreen = new LobbyScreen(this);
	}

	@Override
	public void dispose(){
		playingSong.dispose();
		assMan.manager.dispose();
	}

	/**
	 * @param screen - Screen
	 * Method changes current screen.
	 */
	public void changeScreen(int screen){
		switch(screen){
			case MENU:
				if(menuScreen == null) menuScreen = new MenuScreen(this);
				this.setScreen(menuScreen);
				break;
			case PREFERENCES:
				if(preferencesScreen == null) preferencesScreen = new PreferencesScreen(this);
				this.setScreen(preferencesScreen);
				break;
			case APPLICATION:
				if(mainScreen == null) mainScreen = new MainScreen(this);
				if (clientConnection.getIsConnected()) {
					playingSong.stop();
					if (preferences.isMusicEnabled()) {
						northSea.setVolume(preferences.getSoundVolume());
						if (getPreferences().isSoundEffectsEnabled()) northSea.play();
						northSea.setLooping(true);
					}
					this.setScreen(mainScreen);
				} else {
					this.setScreen(menuScreen);
				}
				break;
			case ENDGAME:
				if(endScreen == null) endScreen = new EndScreen(this);
				this.setScreen(endScreen);
				break;
			case CONNECT:
				if (connectScreen == null) connectScreen = new ConnectScreen(this);
				this.setScreen(connectScreen);
				break;
			case LOBBY:
				if (lobbyScreen == null) lobbyScreen = new LobbyScreen(this);
				this.setScreen(lobbyScreen);
				break;
		}
	}

	public AppPreferences getPreferences() {
		return this.preferences;
	}

	public void createClient(ClientWorld clientWorld) {
		clientConnection = new ClientConnection();
		clientConnection.setLvlFactory(lvlFactory);
		clientConnection.setCam(cam);
		clientConnection.setClientWorld(clientWorld);
		clientConnection.setPlayerName(connectScreen.getPlayer());
		clientConnection.setPlayerSkinId(connectScreen.getCheckedSkin());
		// clientConnection.sendPacketConnect();
		clientConnection.sendPacketAskPlayers();
		clientWorld.setClientConnection(clientConnection);
	}

	public LobbyScreen getLobbyScreen() {
		return lobbyScreen;
	}

	public ClientWorld getClientWorld() {
		return clientWorld;
	}

	public PooledEngine getEngine() {
		return engine;
	}

	public KeyboardController getController() {
		return controller;
	}

	public LevelFactory getLvlFactory() {
		return lvlFactory;
	}
}
