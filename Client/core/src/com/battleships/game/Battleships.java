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
	private Music playingSong;
	private LevelFactory lvlFactory;
	private PooledEngine engine;
	private KeyboardController controller;
	private ClientWorld clientWorld;

	@Override
	public void create () {
		loadingScreen = new LoadingScreen(this);
		preferences = new AppPreferences();
		setScreen(loadingScreen);

		// tells our asset manger that we want to load the images set in loadImages method
		assMan.queueAddMusic();
		// tells the asset manager to load the images and wait until finished loading.
		assMan.manager.finishLoading();
		// loads the 2 sounds we use
		playingSong = assMan.manager.get("sounds/north_sea.mp3");
		playingSong.setVolume(0.5F);
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
		lvlFactory = new LevelFactory(engine,this.assMan, clientWorld);

		// add all the relevant systems our engine should run
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new PhysicsSystem(LevelFactory.world));
		engine.addSystem(renderingSystem);
		// engine.addSystem(new PhysicsDebugSystem(LevelFactory.world, renderingSystem.getCamera()));
		engine.addSystem(new CollisionSystem(lvlFactory));
		engine.addSystem(new PlayerControlSystem(controller,lvlFactory));
		engine.addSystem(new EnemySystem(lvlFactory));
		engine.addSystem(new BulletSystem());
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
		clientConnection.setPlayerskinId(connectScreen.getCheckedSkin());
		clientConnection.sendPacketConnect();
		clientWorld.setClientConnection(clientConnection);
	}

	public ClientConnection getClientConnection() {
		return clientConnection;
	}

	public ClientConnection clientConnection () {
		return clientConnection;
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
