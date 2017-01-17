package com.udacity.gamedev.spacekerfuffle.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.udacity.gamedev.spacekerfuffle.util.Assets;
import com.udacity.gamedev.spacekerfuffle.util.GameplayConstants;
import com.udacity.gamedev.spacekerfuffle.util.Utilities;

/**
 * Represents missiles fired by the player, which cause explosions that destroy nearby enemies when
 * they hit an enemy.
 */
public class Missile extends AbstractEntity {

    private Motion motion;
    private long ascendTime;

    public Missile(float x, float y, Vector2 heading) {
        super(x, y);
        this.heading.set(heading);
        motion = Motion.LATERAL;
        init();
        setType(EntityType.MISSILE);
    }

    @Override
    public void updateHeading(float delta) {
        if (motion == Motion.LATERAL && Utilities.secondsSince(spawnTime) > GameplayConstants.MISSILE_LATERAL_MOTION_TIME) {
            heading.set(GameplayConstants.NORTH);
            ascendTime = TimeUtils.nanoTime();
            motion = Motion.VERTICAL;
        }
        super.updateHeading(delta);
    }

    @Override
    public float getSpeed() {
        switch (motion) {
            //Below speed calculations work because math.
            case LATERAL:
                return GameplayConstants.MISSILE_INITIAL_LATERAL_SPEED -
                        Utilities.secondsSince(spawnTime) * GameplayConstants.MISSILE_LATERAL_DECAY_FACTOR;
            case VERTICAL: default:
                return GameplayConstants.MISSILE_INITIAL_LATERAL_SPEED / 4 +
                        Utilities.secondsSince(ascendTime) * GameplayConstants.MISSILE_ACCEL_FACTOR * GameplayConstants.MISSILE_ACCEL_FACTOR;
        }
    }

    @Override
    public float getWidth() {
        return GameplayConstants.MISSILE_WIDTH;
    }

    @Override
    public float getHeight() {
        return GameplayConstants.MISSILE_HEIGHT;
    }

    @Override
    public TextureRegion getTextureRegion() {
        return Assets.instance.otherAssets.missileAnimation.getKeyFrame(spawnTime);
    }

    public enum Motion {
        LATERAL,
        VERTICAL
    }
}
