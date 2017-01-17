package com.udacity.gamedev.spacekerfuffle.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.udacity.gamedev.spacekerfuffle.SpaceKerfuffleGame;
import com.udacity.gamedev.spacekerfuffle.util.GameplayConstants;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = GameplayConstants.DESKTOP_WINDOW_WIDTH;
        config.height = GameplayConstants.DESKTOP_WINDOW_HEIGHT;
		new LwjglApplication(new SpaceKerfuffleGame(), config);
	}
}
