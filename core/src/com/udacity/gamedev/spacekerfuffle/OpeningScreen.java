package com.udacity.gamedev.spacekerfuffle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.udacity.gamedev.spacekerfuffle.level.LevelBackground;
import com.udacity.gamedev.spacekerfuffle.util.Assets;
import com.udacity.gamedev.spacekerfuffle.util.GameplayConstants;
import com.udacity.gamedev.spacekerfuffle.util.UiConstants;

/**
 * Screen class delegated to for opening title menu.
 */
public class OpeningScreen extends ScreenAdapter {

    public static final String LOG_TAG = OpeningScreen.class.getSimpleName();

    private Game game;
    private LevelBackground background;
    private Stage stage;
    private ExtendViewport viewport;
    private SpriteBatch batch;

    public OpeningScreen(Game game) {
        super();
        this.game = game;
    }

    @Override
    public void show() {
        Gdx.app.log(LOG_TAG, "Loading Assets");
        AssetManager assetManager = new AssetManager();
        Assets.instance.init(assetManager);
        Gdx.app.log(LOG_TAG, "Assets loaded.");

        viewport = new ExtendViewport(
                GameplayConstants.WORLD_WIDTH,
                GameplayConstants.WORLD_HEIGHT,
                GameplayConstants.WORLD_WIDTH,
                Float.MAX_VALUE
        );
        background = new LevelBackground(viewport);
        stage = new Stage(viewport);
        batch = new SpriteBatch();
        Table table = new Table();
        table.setSkin(UiConstants.UI_SKIN);
        table.add(new Label(UiConstants.TITLE_TEXT_LINE_ONE, UiConstants.UI_SKIN, "title")).center();
        table.row();
        table.add(new Label(UiConstants.TITLE_TEXT_LINE_TWO, UiConstants.UI_SKIN, "title")).center();
        table.row();
        Label playButton = new Label(UiConstants.PLAY_BUTTON_TEXT, UiConstants.UI_SKIN, "button");
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                play();
            }
        });
        playButton.setAlignment(Align.center);
        table.add(playButton).center().minWidth(viewport.getWorldWidth() * UiConstants.MIN_BUTTON_WIDTH_RATIO);
        table.row();
        table.add(UiConstants.CONTROLS).center();
        table.row();
        table.add(UiConstants.MOVE_CONTROL_LABEL).center();
        table.row();
        table.add(UiConstants.FIRE_CONTROL_LABEL).center();
        table.row();
        table.add(UiConstants.MISSILE_CONTROL_LABEL).center();
        table.setWidth(table.getPrefWidth());
        table.setHeight(table.getPrefHeight());
        table.setPosition(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, Align.center);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        background.update(delta);

        Gdx.gl.glClearColor(
                GameplayConstants.CLEAR_COLOR.r,
                GameplayConstants.CLEAR_COLOR.g,
                GameplayConstants.CLEAR_COLOR.b,
                GameplayConstants.CLEAR_COLOR.a
        );
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        background.render(batch);
        batch.end();
    }

    /**
     * Method connected to Play button, begins the game.
     */
    public void play() {
        game.setScreen(new GameplayScreen(viewport, background));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        background.resizeUpdate();
    }

}
