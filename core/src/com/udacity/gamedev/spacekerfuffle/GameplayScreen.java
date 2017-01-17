package com.udacity.gamedev.spacekerfuffle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.udacity.gamedev.spacekerfuffle.entities.EnemyShip;
import com.udacity.gamedev.spacekerfuffle.entities.Powerup;
import com.udacity.gamedev.spacekerfuffle.level.Hud;
import com.udacity.gamedev.spacekerfuffle.level.Level;
import com.udacity.gamedev.spacekerfuffle.level.LevelBackground;
import com.udacity.gamedev.spacekerfuffle.util.GameplayConstants;
import com.udacity.gamedev.spacekerfuffle.util.LevelJSONParser;

import java.util.ArrayList;

/**
 * Screen class that is delegated to during gameplay.
 */
public class GameplayScreen extends ScreenAdapter {

    public static final String LOG_TAG = GameplayScreen.class.getName();

    private SpriteBatch batch;
    public ExtendViewport viewport;

    private Level level;
    private LevelBackground levelBackground;
    private Hud hud;

    public GameplayScreen(Viewport viewport, LevelBackground levelBackground) {
        super();
        this.viewport = (ExtendViewport) viewport;
        this.levelBackground = levelBackground;
    }

    @Override
    public void show() {

        hud = new Hud(this);
        //Input processor only used for button at end...
        Gdx.input.setInputProcessor(hud);

        batch = new SpriteBatch();

        initLevel();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        levelBackground.resizeUpdate();
    }

    @Override
    public void render(float delta) {
        viewport.apply();

        Gdx.gl.glClearColor(
                GameplayConstants.CLEAR_COLOR.r,
                GameplayConstants.CLEAR_COLOR.g,
                GameplayConstants.CLEAR_COLOR.b,
                GameplayConstants.CLEAR_COLOR.a
        );
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        levelBackground.update(delta);

        if (level.state == Level.State.PLAYING) {
            level.update(delta);
            hud.updateUiText(level.playerShip, level.score, (int) level.secondsSinceStart());
            if (level.state == Level.State.LOSE) {
                hud.lose();
            } else if (level.state == Level.State.WIN) {
                hud.win();
            }
        }

        hud.draw();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        levelBackground.render(batch);
        level.render(batch);
        batch.end();

    }

    /**
     * Loads and initializes the level.
     */
    public void initLevel() {
        Gdx.app.log(LOG_TAG, "Loading level data.");
        ArrayList<EnemyShip> enemies = LevelJSONParser.getEnemyArray();
        ArrayList<Powerup> powerups = LevelJSONParser.getPowerupArray();
        Gdx.app.log(LOG_TAG, "Level data loaded");

        level = new Level(viewport, enemies, powerups);
        //Level with no enemies for debugging:
        //level = new Level(viewport, new ArrayList<EnemyShip>(), new ArrayList<Powerup>());
        hud.init();
    }

    @Override
    public void hide() {
        super.hide();
    }
}
