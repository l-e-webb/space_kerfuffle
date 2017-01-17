package com.udacity.gamedev.spacekerfuffle.level;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.udacity.gamedev.spacekerfuffle.entities.AbstractEntity;
import com.udacity.gamedev.spacekerfuffle.util.Assets;
import com.udacity.gamedev.spacekerfuffle.util.GameplayConstants;

/**
 * Class for rendering starfield background.
 */
public class LevelBackground {

    private Viewport viewport;
    private Density density;
    private Array<Star> stars;

    public LevelBackground(Viewport viewport) {
        this.viewport = viewport;
        density = Density.LOW;
    }

    /**
     * Renders the level background.
     * @param  batch  Batch to render background stars with.
     */
    public void render(Batch batch) {
        for (Star star : stars) {
            star.render(batch);
        }
    }

    /**
     * Update position of background stars based on time since last frame.
     * @param  delta  Seconds since last frame as float.
     */
    public void update(float delta) {
        for (Star star : stars) {
            if (star.speed != StarSpeed.STATIC) {
                star.update(delta);
            }
            if (star.offScreen(viewport)) {
                stars.removeValue(star, true);
                stars.add(createRandomStar(false, true));
            }
        }
    }

    /**
     * Creates the background starfield.
     */
    public void createStarfield() {
        float densityFactor;
        switch (density) {
            case LOW:
                densityFactor = GameplayConstants.LOW_STAR_DENSITY_FACTOR;
                break;
            case MEDIUM: default:
                densityFactor = GameplayConstants.MEDIUM_STAR_DENSITY_FACTOR;
                break;
            case HIGH:
                densityFactor = GameplayConstants.HIGH_STAR_DENSITY_FACTOR;
                break;
        }
        int starCount = (int) ((viewport.getWorldWidth() + viewport.getWorldHeight()) * densityFactor);
        stars = new Array<Star>(starCount);
        for (int i = 0; i < 2 * starCount / 3; i++) {
            stars.add(createRandomStar(true, false));

        }
        for (int i = 0; i < starCount / 3; i++) {
            stars.add(createRandomStar(false, false));
        }
    }

    /**
     * Creates a randomized Star object based on parameters defined in GameplayConstants class.
     * @param  isStatic  True if Star should be stationary, false if it should move.
     * @param  atTop  True is Star should spawn at the top of the screen, false if y coordinate
     *                should be randomized.
     * @return  new Star object.
     */
    public Star createRandomStar(boolean isStatic, boolean atTop) {
        StarType type;
        StarSpeed speed;
        float x;
        float y;
        float starSeed = MathUtils.random();
        if (starSeed < GameplayConstants.WHITE_STAR_PERCENTAGE) {
            type = StarType.SMALL_WHITE;
        } else if (starSeed < GameplayConstants.WHITE_STAR_PERCENTAGE + GameplayConstants.YELLOW_STAR_PERCENTAGE) {
            type = StarType.MEDIUM_YELLOW;
        } else if (starSeed > 1 - GameplayConstants.RED_STAR_PERCENTAGE) {
            type = StarType.LARGE_RED;
        } else {
            //Should be impossible.
            type = StarType.SMALL_WHITE;
        }
        if (isStatic) {
            speed = StarSpeed.STATIC;
        } else {
            if (type == StarType.SMALL_WHITE) {
                speed = (MathUtils.random(1) == 0) ? StarSpeed.FAST : StarSpeed.MEDIUM;
            } else {
                speed = (MathUtils.random(1) == 0) ? StarSpeed.MEDIUM : StarSpeed.SLOW;
            }
        }
        x = MathUtils.random(viewport.getWorldWidth());
        y = (atTop) ? viewport.getWorldHeight() : MathUtils.random(viewport.getWorldHeight());
        return new Star(new Vector2(x, y), type, speed);
    }

    /**
     * Adjusts starfield when viewport is resized.  At the moment, whole starfield is regenerated
     * rather than current starfield being somehow modified.
     */
    public void resizeUpdate() {
        createStarfield();
    }

    /**
     * Class representing individual background stars.  They can have three different shapes and
     * sizes.
     */
    private class Star extends AbstractEntity {

        StarType type;
        StarSpeed speed;

        public Star(Vector2 position, StarType _type, StarSpeed _speed) {
            setPosition(position.x, position.y);
            type = _type;
            speed = _speed;
            heading = new Vector2(GameplayConstants.SOUTH);
            init();
        }

        @Override
        public float getSpeed() {
            switch (speed) {
                case STATIC: default:
                    return 0;
                case SLOW:
                    return GameplayConstants.STAR_SPEED_SLOW;
                case MEDIUM:
                    return GameplayConstants.STAR_SPEED_MEDIUM;
                case FAST:
                    return GameplayConstants.STAR_SPEED_FAST;
            }
        }

        @Override
        public float getWidth() {
            switch (type) {
                case SMALL_WHITE:
                    return GameplayConstants.STAR_WIDTH_SMALL;
                case MEDIUM_YELLOW:
                    return GameplayConstants.STAR_WIDTH_MEDIUM;
                case LARGE_RED:
                    return GameplayConstants.STAR_WIDTH_LARGE;
                default:
                    return 0;
            }
        }

        @Override
        public float getHeight() {
            return getWidth();
        }

        @Override
        public TextureRegion getTextureRegion() {
            switch (type) {
                case SMALL_WHITE: default:
                    return Assets.instance.otherAssets.smallWhite;
                case MEDIUM_YELLOW:
                    return Assets.instance.otherAssets.mediumYellow;
                case LARGE_RED:
                    return Assets.instance.otherAssets.largeRed;
            }
        }
    }

    public enum Density {
        LOW,
        MEDIUM,
        HIGH
    }

    public enum StarType {
        SMALL_WHITE,
        MEDIUM_YELLOW,
        LARGE_RED
    }

    public enum StarSpeed {
        STATIC,
        SLOW,
        MEDIUM,
        FAST
    }
}
