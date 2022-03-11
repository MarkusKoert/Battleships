package com.battleships.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.battleships.game.Battleships;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Battleships";
		config.useGL30 = true;
		config.width = 1024;
		config.height = 576;
		new LwjglApplication(new Battleships(), config);
	}
}
