package com.udacity.gamedev.spacekerfuffle.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

/**
 * Text/UI-rendering constants.
 */
public class UiConstants {

    public static final float UI_TEXT_SCALE = 1;
    public static final float TITLE_TEXT_SCALE = 1;
    public static final float MIN_BUTTON_WIDTH_RATIO = 0.2f;
    public static final Color UI_TEXT_COLOR = Color.WHITE;

    public static final String TITLE_TEXT_LINE_ONE = "Space";
    public static final String TITLE_TEXT_LINE_TWO = "Kerfuffle!";
    public static final String PLAY_BUTTON_TEXT = "Play";
    public static final String CONTROLS = "-Controls-";
    public static final String MOVE_CONTROL_LABEL = "Move: arrow keys";
    public static final String FIRE_CONTROL_LABEL = "Fire Laser: Z";
    public static final String MISSILE_CONTROL_LABEL = "Fire Missile: X";
    public static final String SCORE_TEXT = "SCORE";
    public static final String EMPOWERED_LASERS_ON_TEXT = "ON";
    public static final String EMPOWERED_LASERS_OFF_TEXT = "OFF";
    public static final String WIN_TEXT = "YOU WIN!";
    public static final String GAME_OVER_TEXT = "GAME OVER";
    public static final String PLAY_AGAIN_TEXT = "Play again?";

    public static Skin UI_SKIN;

    /**
     * Creates the scene2d.ui Skin object for the UI objects, including defining fonts and styles.
     */
    public static void initSkin() {
        UI_SKIN = new Skin();
        BitmapFont font = new BitmapFont();
        font.getData().setScale(UI_TEXT_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        BitmapFont titleFont = new BitmapFont(Gdx.files.internal("font.fnt"), false);
        titleFont.getData().setScale(TITLE_TEXT_SCALE);
        titleFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, UI_TEXT_COLOR);
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, UI_TEXT_COLOR);
        Label.LabelStyle buttonStyle = new Label.LabelStyle(labelStyle);
        buttonStyle.background = new NinePatchDrawable(new NinePatch(
                new Texture("button.png"),
                5, 5, 5, 5
        ));
        UI_SKIN.add("default", labelStyle);
        UI_SKIN.add("button", buttonStyle);
        UI_SKIN.add("title", titleStyle);
    }

}
