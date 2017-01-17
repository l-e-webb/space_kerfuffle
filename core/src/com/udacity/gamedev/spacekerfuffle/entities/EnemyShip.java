package com.udacity.gamedev.spacekerfuffle.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.udacity.gamedev.spacekerfuffle.util.Assets;
import com.udacity.gamedev.spacekerfuffle.util.GameplayConstants;
import com.udacity.gamedev.spacekerfuffle.util.Utilities;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class representing enemies.
 */
public class EnemyShip extends AbstractEntity {

    private MovementPattern movePattern;
    private Motion motion;
    private FiringPattern firingPattern;
    private float speed;
    private float fireDelay;
    private long fireTime;
    private int fireCount;

    public EnemyShip(float x, float y,
                     MovementPatterns movePattern,
                     MovePhaseDuration duration,
                     Speed speed,
                     FiringPattern firePattern,
                     FireRate fireRate) {
        super(x, y);
        init(movePattern, duration, speed, firePattern, fireRate);
        setType(EntityType.ENEMY_SHIP);
    }

    @Override
    public void update(float delta) {
        movePattern.update();
        super.update(delta);
    }

    @Override
    public void updateHeading(float delta) {
        switch (motion) {
            case NORTH:
                heading.set(GameplayConstants.NORTH);
                break;
            case EAST:
                heading.set(GameplayConstants.EAST);
                break;
            case SOUTH:
                heading.set(GameplayConstants.SOUTH);
                break;
            case WEST:
                heading.set(GameplayConstants.WEST);
                break;
            case NORTHEAST:
                heading.set(GameplayConstants.NORTHEAST);
                break;
            case NORTHWEST:
                heading.set(GameplayConstants.NORTHWEST);
                break;
            case SOUTHEAST:
                heading.set(GameplayConstants.SOUTHEAST);
                break;
            case SOUTHWEST:
                heading.set(GameplayConstants.SOUTHWEST);
                break;
            case STILL:
                heading.set(Vector2.Zero);
                break;
            default:
                heading.set(GameplayConstants.SOUTH);
                break;
        }
        super.updateHeading(delta);
    }

    /**
     * Test whether the enemy is ready to fire based on firing pattern.
     * @return  true if enemy should fire this frame, false otherwise.
     */
    public boolean fire() {
        if (Utilities.secondsSince(fireTime) < fireDelay) {
            return false;
        }
        fireTime = TimeUtils.nanoTime();
        fireCount++;
        switch (firingPattern) {
            case SIMPLE:
                //Skips every fourth shot.
                if (fireCount % 4 == 0) return false;
                break;
            case BURST:
                //Skips 3 shots, fires three shots.  Works best with fast fire rate.
                if (fireCount % 6 < 3) return false;
                break;
        }
        return true;
    }

    /**
     * Get an array of lasers fired by the enemy this frame.
     * @param player  Player object, so that lasers can be directed at it.
     * @return  Array of lasers to be fired this frame.
     */
    public Array<Laser> getLasers(PlayerShip player) {
        Array<Laser> lasers = new Array<Laser>();
        if (!fire()) return lasers;
        Vector2 direction = new Vector2();
        Laser.LaserType type = Laser.LaserType.RED;
        switch (firingPattern) {
            case SIMPLE: case BURST: default:
                //Fires a laser straight down.
                direction = GameplayConstants.SOUTH;
                break;
            case SPRAY:
                //Fires lasers center right center left etc.
                switch (fireCount % 4) {
                    case 0:
                        direction = GameplayConstants.SOUTH;
                        break;
                    case 1:
                        direction = GameplayConstants.SOUTHEAST;
                        break;
                    case 2:
                        direction = GameplayConstants.SOUTH;
                        break;
                    case 3:
                        direction = GameplayConstants.SOUTHWEST;
                        break;
                }
                break;
            case AIMED:
                //Fires a laser towards the player's current position.
                direction = new Vector2(
                        player.getX() - getX(),
                        player.getY() - (getY() - getHeight() / 2)
                );
                type = Laser.LaserType.POINTY;
                break;
        }
        lasers.add(new Laser(
                getX(),
                getY() - getHeight() / 2,
                direction,
                false,
                type
        ));
        //Currently returns a single element array to allow for further implementation where
        //enemies fire multiple lasers simultaneously.
        return lasers;
    }

    @Override
    public float getWidth() { return GameplayConstants.ENEMY_SHIP_WIDTH; }

    @Override
    public float getHeight() { return GameplayConstants.ENEMY_SHIP_HEIGHT; }

    @Override
    public float getSpeed() { return speed; }

    @Override
    public TextureRegion getTextureRegion() {
        Animation anim;
        switch (motion) {
            case EAST: case NORTHEAST: case SOUTHEAST: case WEST: case NORTHWEST: case SOUTHWEST:
                anim = (Utilities.secondsSince(fireTime) < GameplayConstants.MUZZLE_FLASH_DURATION) ?
                        Assets.instance.enemyShipAssets.enemyTurningFiring :
                        Assets.instance.enemyShipAssets.enemyTurning;
                break;
            case NORTH: case SOUTH: case STILL:
                anim = (Utilities.secondsSince(fireTime) < GameplayConstants.MUZZLE_FLASH_DURATION) ?
                        Assets.instance.enemyShipAssets.enemyFiring :
                        Assets.instance.enemyShipAssets.enemyNeutral;
                break;
            default:
                anim = Assets.instance.enemyShipAssets.enemyNeutral;
                break;
        }
        return anim.getKeyFrame(Utilities.secondsSince(spawnTime));
    }

    @Override
    public boolean getIsReflectedHorizontal() {
        return (motion == Motion.NORTHWEST ||
            motion == Motion.WEST ||
            motion == Motion.SOUTHWEST
        );
    }

    /**
     * Initializes enemy instance varaibles.  init() with no arguments should be called again as
     * enemy is spawned.
     * @param  movePattern  Pattern from MovementPatterns enum
     * @param  duration  Duration from MovePhaseDurations enum
     * @param  speed  Speed from Speed enum
     * @param  firePattern  Pattern from FiringPattern enum
     * @param  fireRate  Rate from FireRate enum
     */
    public void init(MovementPatterns movePattern,
                     MovePhaseDuration duration,
                     Speed speed,
                     FiringPattern firePattern,
                     FireRate fireRate) {
        super.init();
        this.movePattern = new MovementPattern(movePattern, duration);
        this.firingPattern = firePattern;
        motion = this.movePattern.getCurrentMotion();
        switch (speed) {
            case SLOW:
                this.speed = GameplayConstants.ENEMY_SPEED_SLOW;
                break;
            case MEDIUM:
                this.speed = GameplayConstants.ENEMY_SPEED_MEDIUM;
                break;
            case FAST:
                this.speed = GameplayConstants.ENEMY_SPEED_FAST;
                break;
            default:
                this.speed = GameplayConstants.ENEMY_SPEED_MEDIUM;
                break;
        }
        switch (fireRate) {
            case SLOW:
                fireDelay = GameplayConstants.ENEMY_FIRE_DELAY_SLOW;
                break;
            case MEDIUM:
                fireDelay = GameplayConstants.ENEMY_FIRE_DELAY_MEDIUM;
                break;
            case FAST:
                fireDelay = GameplayConstants.ENEMY_FIRE_DELAY_FAST;
                break;
            default:
                fireDelay = GameplayConstants.ENEMY_FIRE_DELAY_MEDIUM;
                break;
        }
        fireTime = TimeUtils.nanoTime();
        fireCount = 0;
        this.movePattern.init();
    }

    @Override
    public void init() {
        super.init();
        movePattern.init();
    }

    /**
     * Class defining various movement patterns enemies can execute.
     */
    private class MovementPattern {

        private ArrayList<Motion> motionSequence;
        private int currentMotionIndex;
        private float phaseDuration;
        private long phaseTime;


        public MovementPattern(MovementPatterns pattern, MovePhaseDuration duration) {
            currentMotionIndex = 0;
            initPattern(pattern, duration);
        }

        public void init() {
            phaseTime = TimeUtils.nanoTime();
        }

        public void update() {
            if (phaseDuration == 0) return;
            if (Utilities.secondsSince(phaseTime) > phaseDuration) {
                phaseTime = TimeUtils.nanoTime();
                updatePhase();
            }
        }

        public void updatePhase() {
            currentMotionIndex = (currentMotionIndex + 1) % motionSequence.size();
            motion = getCurrentMotion();
        }

        public Motion getCurrentMotion() {
            return motionSequence.get(currentMotionIndex);
        }

        public void initPattern(MovementPatterns pattern, MovePhaseDuration duration) {
            //In an ideal world, these patterns could be described with more specificity in the
            //level design JSON or similar and constructed here, but set patterns are hardcoded here
            //for expedience.
            motionSequence = new ArrayList<Motion>();
            switch (duration) {
                case SHORT:
                    phaseDuration = GameplayConstants.ENEMY_PHASE_DURATION_SHORT;
                    break;
                case MEDIUM:
                    phaseDuration = GameplayConstants.ENEMY_PHASE_DURATION_MEDIUM;
                    break;
                case LONG:
                    phaseDuration = GameplayConstants.ENEMY_PHASE_DURATION_LONG;
                    break;
                default:
                    phaseDuration = GameplayConstants.ENEMY_PHASE_DURATION_MEDIUM;
            }
            switch (pattern) {
                case SIMPLE:
                    //Enemy moves straight down.
                    this.phaseDuration = 0;
                    motionSequence.add(Motion.SOUTH);
                    break;
                case SIMPLE_PAUSE:
                    //Enemy alternates moving and hovering.
                    Gdx.app.debug(LOG_TAG, "Initializing enemy with SIMPLE_PAUSE motion");
                    motionSequence.addAll(Arrays.asList(
                            Motion.SOUTH,
                            Motion.STILL
                    ));
                    break;
                case SIMPLE_PAUSE_LONG:
                    //Same as above, but hover is twice times as long as motion.
                    motionSequence.addAll(Arrays.asList(
                            Motion.SOUTH,
                            Motion.STILL,
                            Motion.STILL
                    ));
                    break;
                case S_RIGHT:
                    //Moves in a square s-curve starting going right (like a digital clock "2").
                    motionSequence.addAll(Arrays.asList(
                            Motion.SOUTH,
                            Motion.EAST,
                            Motion.SOUTH,
                            Motion.WEST
                    ));
                    break;
                case S_LEFT:
                    //Same as above except starts going left (like a digital clock "5").
                    motionSequence.addAll(Arrays.asList(
                            Motion.SOUTH,
                            Motion.WEST,
                            Motion.SOUTH,
                            Motion.EAST
                    ));
                    break;
                case STRAFE_S_RIGHT:
                    //Like S-curves except it spends more time going side to side before descending.
                    motionSequence.addAll(Arrays.asList(
                            Motion.SOUTH,
                            Motion.EAST,
                            Motion.WEST,
                            Motion.EAST,
                            Motion.SOUTH,
                            Motion.WEST,
                            Motion.EAST,
                            Motion.WEST
                    ));
                    break;
                case STRAFE_S_LEFT:
                    motionSequence.addAll(Arrays.asList(
                            Motion.SOUTH,
                            Motion.WEST,
                            Motion.EAST,
                            Motion.WEST,
                            Motion.SOUTH,
                            Motion.EAST,
                            Motion.WEST,
                            Motion.EAST
                    ));
                    break;
                case ZIG_ZAG_RIGHT:
                    motionSequence.addAll(Arrays.asList(
                            Motion.SOUTHEAST,
                            Motion.SOUTHWEST
                    ));
                    break;
                case ZIG_ZAG_LEFT:
                    motionSequence.addAll(Arrays.asList(
                            Motion.SOUTHWEST,
                            Motion.SOUTHEAST
                    ));
                    break;
            }
        }

    }

    public enum FiringPattern {
        SIMPLE,
        SPRAY,
        AIMED,
        BURST
    }

    public enum MovementPatterns {
        SIMPLE,
        SIMPLE_PAUSE,
        SIMPLE_PAUSE_LONG,
        S_RIGHT,
        S_LEFT,
        STRAFE_S_RIGHT,
        STRAFE_S_LEFT,
        ZIG_ZAG_LEFT,
        ZIG_ZAG_RIGHT
    }

    public enum Motion {
        NORTH,
        SOUTH,
        EAST,
        WEST,
        NORTHEAST,
        NORTHWEST,
        SOUTHEAST,
        SOUTHWEST,
        STILL
    }

    public enum MovePhaseDuration {
        SHORT,
        MEDIUM,
        LONG
    }

    public enum FireRate {
        SLOW,
        MEDIUM,
        FAST
    }

    public enum Speed {
        SLOW,
        MEDIUM,
        FAST
    }

}