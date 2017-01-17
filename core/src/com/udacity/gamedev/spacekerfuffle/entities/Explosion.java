package com.udacity.gamedev.spacekerfuffle.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.udacity.gamedev.spacekerfuffle.util.Assets;
import com.udacity.gamedev.spacekerfuffle.util.GameplayConstants;
import com.udacity.gamedev.spacekerfuffle.util.Utilities;

/**
 * Class for animated explosions spawned when enemies or the player is destroyed.
 */
public class Explosion extends AbstractEntity {

    public Explosion(float x, float y) {
        position = new Vector2(x, y);
        init();
        setType(EntityType.EXPLOSION);
    }

    public Explosion(AbstractEntity entity) {
        this(entity.getX(), entity.getY());
    }

    /**
     * Tests whether animation has completed.
     * @return  true if animation is complete, false otherwise.
     */
    public boolean isOver() {
        return Assets.instance.otherAssets.explosionAnimation.isAnimationFinished(Utilities.secondsSince(spawnTime));
    }

    @Override
    public float getSpeed() {
        return 0;
    }

    @Override
    public float getWidth() {
        return GameplayConstants.EXPLOSION_WIDTH;
    }

    @Override
    public float getHeight() {
        return GameplayConstants.EXPLOSION_HEIGHT;
    }

    @Override
    public TextureRegion getTextureRegion() {
        return Assets.instance.otherAssets.explosionAnimation.getKeyFrame(Utilities.secondsSince(spawnTime));
    }
}
