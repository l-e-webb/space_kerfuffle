package com.udacity.gamedev.spacekerfuffle.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.udacity.gamedev.spacekerfuffle.util.Assets;
import com.udacity.gamedev.spacekerfuffle.util.GameplayConstants;

/**
 * Powerup objects that the player can get by colliding with them that give extra missiles, extra
 * lives, or improved lasers.
 */
public class Powerup extends AbstractEntity {

    public PowerupType type;

    public Powerup(float x, float y, PowerupType type) {
        super(x, y);
        heading = new Vector2(0, -1);
        this.type = type;
        setType(EntityType.POWERUP);
    }

    /**
     * Apply powerup to player.
     * @param  playerShip  Player object to apply powerup.
     */
    public void apply(PlayerShip playerShip) {
        switch (type) {
            case EXTRA_LIFE:
                playerShip.lives++;
                return;
            case EXTRA_MISSILES:
                playerShip.addMissiles(GameplayConstants.EXTRA_MISSILE_AMMO);
                return;
            case IMPROVED_LASERS:
                playerShip.improveLasers();
                return;
            default:
                return;
        }
    }

    @Override
    public float getSpeed() { return GameplayConstants.POWERUP_SPEED; }

    @Override
    public float getWidth() { return GameplayConstants.POWERUP_WIDTH; }

    @Override
    public float getHeight() { return GameplayConstants.POWERUP_HEIGHT; }

    @Override
    public TextureRegion getTextureRegion() {
        switch (type) {
            case EXTRA_LIFE:
                return Assets.instance.otherAssets.extraLife;
            case EXTRA_MISSILES:
                return Assets.instance.otherAssets.missileAmmo;
            case IMPROVED_LASERS:
                return Assets.instance.otherAssets.empoweredLasers;
            default:
                return null;
        }
    }

    public enum PowerupType {
        EXTRA_LIFE,
        EXTRA_MISSILES,
        IMPROVED_LASERS
    }
}
