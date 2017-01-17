package com.udacity.gamedev.spacekerfuffle.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.udacity.gamedev.spacekerfuffle.util.Assets;
import com.udacity.gamedev.spacekerfuffle.util.GameplayConstants;

/**
 * Laser projectiles fired by both the player and enemies.
 */
public class Laser extends AbstractEntity {

    public boolean playerLaser;

    private final LaserType TYPE;

    public Laser(float x, float y, Vector2 heading, boolean playerLaser, LaserType type) {
        super(x, y);
        this.heading.set(heading);
        this.playerLaser = playerLaser;
        TYPE = type;
        trackHeading = true;
        setType(playerLaser ? EntityType.PLAYER_LASER : EntityType.ENEMY_LASER);
    }

    public Laser(float x, float y, Vector2 heading, boolean playerLaser) {
        this(x, y, heading, playerLaser,
                (playerLaser) ? LaserType.BLUE : LaserType.RED
        );
    }

    public Laser(float x, float y, Vector2 heading) {
        this(x, y, heading, true);
    }


    @Override
    public float getWidth() { return GameplayConstants.LASER_WIDTH; }

    @Override
    public float getHeight() { return GameplayConstants.LASER_HEIGHT; }

    @Override
    public float getSpeed() {
        return (playerLaser) ? GameplayConstants.PLAYER_LASER_SPEED : GameplayConstants.ENEMY_LASER_SPEED;
    }

    @Override
    public TextureRegion getTextureRegion() {
        switch (TYPE) {
            case BLUE:default:
                return Assets.instance.otherAssets.blueLaser;
            case RED:
                return Assets.instance.otherAssets.redLaser;
            case POINTY:
                return Assets.instance.otherAssets.pointyLaser;
        }
    }

    public enum LaserType {
        BLUE,
        RED,
        POINTY
    }
}
