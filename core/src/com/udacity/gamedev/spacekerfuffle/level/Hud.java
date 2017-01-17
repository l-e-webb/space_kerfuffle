package com.udacity.gamedev.spacekerfuffle.level;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.udacity.gamedev.spacekerfuffle.GameplayScreen;
import com.udacity.gamedev.spacekerfuffle.entities.PlayerShip;
import com.udacity.gamedev.spacekerfuffle.util.Assets;
import com.udacity.gamedev.spacekerfuffle.util.GameplayConstants;
import com.udacity.gamedev.spacekerfuffle.util.UiConstants;
import com.udacity.gamedev.spacekerfuffle.util.Utilities;

/**
 * Renders heads-up display showing score, lives, missile ammo, and weapon status.  Also renders
 * victory and game over messages.
 */
public class Hud extends Stage {

    private GameplayScreen screen;
    private Label scoreLabel;
    private Label scoreCountLabel;
    private Label improvedLaserLabel;
    private Label missileAmmoLabel;
    private Label lifeCountLabel;
    private Label winLabel;
    private Label loseLabel;
    private Label playAgainButton;

    public Hud(GameplayScreen screen) {
        super(screen.viewport);
        this.screen = screen;

        scoreCountLabel = new Label("", UiConstants.UI_SKIN);
        improvedLaserLabel = new Label("", UiConstants.UI_SKIN);
        missileAmmoLabel = new Label("", UiConstants.UI_SKIN);
        lifeCountLabel = new Label("", UiConstants.UI_SKIN);
        winLabel = new Label(UiConstants.WIN_TEXT, UiConstants.UI_SKIN);
        loseLabel = new Label(UiConstants.GAME_OVER_TEXT, UiConstants.UI_SKIN);
        scoreLabel = new Label(UiConstants.SCORE_TEXT, UiConstants.UI_SKIN);
        playAgainButton = new Label(UiConstants.PLAY_AGAIN_TEXT, UiConstants.UI_SKIN, "button");
        playAgainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playAgain();
            }
        });
        playAgainButton.setAlignment(Align.center);
    }

    /**
     * Initializes HUD to show gameplay overlay.
     */
    public void init() {
        clear();

        Image improvedLaserIcon = new Image(Assets.instance.otherAssets.empoweredLasers);
        Image missileAmmoIcon = new Image(Assets.instance.otherAssets.missileAmmo);
        Image lifeCountIcon = new Image(Assets.instance.otherAssets.extraLife);

        Table table = new Table();
        table.right().bottom();
        //Uncomment to see table wireframe.
        //table.setDebug(true);
        table.setFillParent(true);
        table.add(scoreLabel).colspan(2);
        table.row();
        table.add(scoreCountLabel).top().colspan(2).expandY();
        table.row();
        table.add(improvedLaserIcon, improvedLaserLabel);
        table.row();
        table.add(missileAmmoIcon, missileAmmoLabel);
        table.row();
        table.add(lifeCountIcon, lifeCountLabel);
        table.pack();

        addActor(table);
    }

    /**
     * Removes HUD and adds victory message.
     */
    public void win() {
        createEndTable(winLabel);
    }

    /**
     * Removes HUD and adds game over message.
     */
    public void lose() { createEndTable(loseLabel); }

    /**
     * Creates either victory or game over message based on label parameter.  (Other than GAME OVER
     * or YOU WIN! it displays the current score.)
     * @param  winOrLoseLabel  Label to be displayed above final score.
     */
    private void createEndTable(Label winOrLoseLabel) {
        clear();
        Table table = new Table();
        table.center().setFillParent(true);
        table.add(winOrLoseLabel);
        table.row();
        table.add(scoreLabel);
        table.row();
        table.add(scoreCountLabel);
        table.row();
        table.add(playAgainButton).minWidth(getViewport().getWorldWidth() * UiConstants.MIN_BUTTON_WIDTH_RATIO);
        table.pack();
        addActor(table);
    }

    /**
     * Updates HUD text to show correct score, missile ammo, lives, & weapon status.
     * @param  player  PlayerShip object to retrieve lives, missile ammo, and weapon status from.
     * @param  score  int score representing score from killing enemies and getting powerups.  Note
     *                that this is not the rendered score!  This is also a score component that
     *                comes from time
     */
    public void updateUiText(PlayerShip player, int score, int timeSinceStart) {
        score += timeSinceStart * GameplayConstants.SCORE_PER_SECOND;
        scoreCountLabel.setText(Utilities.formatWithLeadingZeros(score, 7));
        improvedLaserLabel.setText(
                (player.improvedLasers) ? UiConstants.EMPOWERED_LASERS_ON_TEXT : UiConstants.EMPOWERED_LASERS_OFF_TEXT
        );
        missileAmmoLabel.setText(Utilities.formatWithLeadingZeros(player.missiles, 3));
        lifeCountLabel.setText(Utilities.formatWithLeadingZeros(player.lives, 3));
    }

    /**
     * Method connected to Play Again button, reinitializes level.
     */
    public void playAgain() {
        screen.initLevel();
    }
}
