package com.udacity.gamedev.spacekerfuffle.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.udacity.gamedev.spacekerfuffle.SpaceKerfuffleGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(480, 640);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new SpaceKerfuffleGame();
        }
}