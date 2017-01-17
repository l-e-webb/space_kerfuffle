package com.udacity.gamedev.spacekerfuffle;

import com.badlogic.gdx.Game;
import com.udacity.gamedev.spacekerfuffle.util.UiConstants;

public class SpaceKerfuffleGame extends Game {
	
	@Override
	public void create () {
		UiConstants.initSkin();
		setScreen(new OpeningScreen(this));
	}
}
