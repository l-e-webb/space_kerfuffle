package com.udacity.gamedev.spacekerfuffle.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.udacity.gamedev.spacekerfuffle.util.Assets;
import com.udacity.gamedev.spacekerfuffle.util.GameplayConstants;
import com.udacity.gamedev.spacekerfuffle.util.Utilities;

/**
 * Class representing the end-of-level boss, with inner classes for different parts.
 */
public class Boss extends AbstractEntity {

    public Phase phase;

    private BossCore core;
    private BossArm leftArm;
    private BossArm rightArm;

    private BossTurret turret;

    //For aimed shots & staying above player.
    private PlayerShip player;

    private Behavior behavior;
    private long behaviorTime;
    private int behaviorIndex;
    private float behaviorDuration;

    private long coreFireTime;
    private long armFireTime;

    //Behavior arrays provide sequences of Behaviors for each phase.  Ideally these values would be
    //fetched from level design JSON or similar, but they are hardcoded here for expedience.
    private static final Behavior[] PHASE_1_BEHAVIORS = {
            new Behavior(
                    Motion.STRAFE,
                    ArmFiringPattern.ALTERNATING,
                    CoreFiringPattern.NONE,
                    1
            ),
            new Behavior(
                    Motion.TRACK_PLAYER,
                    ArmFiringPattern.SIMULTANEOUS,
                    CoreFiringPattern.SIMPLE,
                    1
            )
    };
    private static final Behavior[] PHASE_2_BEHAVIORS = {
            new Behavior(
                    Motion.STRAFE,
                    ArmFiringPattern.ALTERNATING,
                    CoreFiringPattern.AIMED,
                    1.2f
            ),
            new Behavior(
                    Motion.CENTER,
                    ArmFiringPattern.SIMULTANEOUS,
                    CoreFiringPattern.SPRAY,
                    1.2f
            ),
            new Behavior(
                    Motion.TRACK_PLAYER,
                    ArmFiringPattern.SIMULTANEOUS,
                    CoreFiringPattern.RAPID,
                    1.2f
            )
    };
    private static final Behavior[] PHASE_3_BEHAVIORS = {
            new Behavior(
                    Motion.TRACK_PLAYER,
                    ArmFiringPattern.NONE,
                    CoreFiringPattern.RAPID,
                    1.8f
            ),
            new Behavior(
                    Motion.CENTER,
                    ArmFiringPattern.NONE,
                    CoreFiringPattern.SPRAY,
                    1.8f
            )
    };

    public Boss(float x, float y, Viewport viewport, PlayerShip player) {
        super(x, y);
        this.viewport = viewport;
        this.player = player;
        behaviorDuration = GameplayConstants.BOSS_BEHAVIOR_DURATION;
        init();
        setType(EntityType.BOSS);
    }

    @Override
    public void move() {
        super.move();
        float minX = GameplayConstants.BOSS_SIDE_SCREEN_OFFSET;
        if (leftArm.active) minX += GameplayConstants.BOSS_ARM_WIDTH;
        float maxX = viewport.getWorldWidth() - GameplayConstants.BOSS_SIDE_SCREEN_OFFSET;
        if (rightArm.active) maxX -= GameplayConstants.BOSS_ARM_WIDTH;
        if (getX() < minX) {
            position.x = minX;
            heading.x = 1;
        } else if (getX() > maxX) {
            position.x = maxX;
            heading.x = -1;
        }
    }

    @Override
    public void render(Batch batch) {
        super.render(batch);
        turret.render(batch);
        if (leftArm.active) {
            leftArm.turret.render(batch);
        }
        if (rightArm.active) {
            rightArm.turret.render(batch);
        }
    }

    @Override
    public void update(float delta) {
        if (phase == Phase.DEAD) return;
        super.update(delta);
        turret.setPosition(getX(), getY());
        core.update(delta);
        leftArm.update(delta);
        rightArm.update(delta);
        updatePhase();
        if (phase == Phase.ENTRANCE) return;
        if (Utilities.secondsSince(behaviorTime) > behaviorDuration) {
            behaviorIndex++;
            updateBehavior(behaviorIndex);
        }
    }

    @Override
    public void updateHeading(float delta) {
        if (phase == Phase.DEAD) return;
        float moveDistance = getSpeed() * delta;
        switch (behavior.motion) {
            case DESCEND:
                heading.set(GameplayConstants.SOUTH);
                break;
            case STRAFE:
                heading.y = 0;
                if (heading.x == 0) heading.x = 1;
                break;
            case CENTER:
                float centerX = viewport.getWorldWidth() / 2;
                if (Math.abs(position.x - centerX) < moveDistance) {
                    position.x = centerX;
                    heading.set(Vector2.Zero);
                } else {
                    heading.y = 0;
                    heading.x = centerX - position.x;
                }
                break;
            case TRACK_PLAYER:
                if (Math.abs(position.x - player.getX()) < moveDistance) {
                    position.x = player.getX();
                    heading.set(Vector2.Zero);
                } else {
                    heading.y = 0;
                    heading.x = player.getX() - position.x;
                }
                break;
            default:
                heading.set(Vector2.Zero);
                Gdx.app.log(LOG_TAG, "No valid motion value for boss.");
                break;
        }
        heading.setLength(moveDistance);
    }

    /**
     * Updates the current phase of the boss based on certain conditions for each phase.
     */
    public void updatePhase() {
        switch (phase) {
            case ENTRANCE:
                if (position.y < viewport.getWorldHeight() - getHeight() / 2 - GameplayConstants.BOSS_TOP_SCREEN_OFFSET) {
                    phase = Phase.PHASE_1;
                    updateBehavior(0);
                }
            case PHASE_1:
                if (rightArm.health + leftArm.health < GameplayConstants.BOSS_ARM_STARTING_HEALTH) {
                    phase = Phase.PHASE_2;
                    updateBehavior(0);
                    behaviorIndex = 0;
                    behavior = PHASE_2_BEHAVIORS[behaviorIndex];
                }
                break;
            case PHASE_2:
                if (!rightArm.active && !leftArm.active) {
                    phase = Phase.PHASE_3;
                    behaviorIndex = 0;
                    behavior = PHASE_3_BEHAVIORS[behaviorIndex];
                }
                break;
            case DEAD: default:
                break;
        }
    }

    /**
     * Updates the behavior of the boss to the behavior at a certain index of the behavior array
     * for the current phase.
     * @param  index  Index of current phase behavior array to which behavior will be set.
     */
    public void updateBehavior(int index) {
        Behavior[] behaviors;
        switch (phase) {
            case ENTRANCE:
                return;
            case PHASE_1:
                behaviors = PHASE_1_BEHAVIORS;
                break;
            case PHASE_2:
                behaviors = PHASE_2_BEHAVIORS;
                break;
            case PHASE_3:
                behaviors = PHASE_3_BEHAVIORS;
                break;
            default:
                Gdx.app.log(LOG_TAG, "No valid phase value for boss.");
                return;
        }
        if (index >= behaviors.length) index = 0;
        behaviorIndex = index;
        behavior = behaviors[behaviorIndex];
        behaviorTime = TimeUtils.nanoTime();
    }

    @Override
    public Array<Laser> getLasers() {
        Array<Laser> lasers = new Array<Laser>();
        if (Utilities.secondsSince(armFireTime) > GameplayConstants.BOSS_ARM_FIRE_DELAY && behavior.armFiringPattern != ArmFiringPattern.NONE) {
            switch (behavior.armFiringPattern) {
                case ALTERNATING:
                    BossArm arm = (Utilities.secondsSince(behaviorTime) % (GameplayConstants.BOSS_ARM_FIRE_DELAY * 2) < GameplayConstants.BOSS_ARM_FIRE_DELAY) ?
                            rightArm : leftArm;
                    if (arm.active) {
                        lasers.add(new Laser(arm.turret.getX(), arm.turret.getY(), GameplayConstants.SOUTH, false));
                        arm.turret.fireTime = TimeUtils.nanoTime();
                    }
                    break;
                case SIMULTANEOUS:
                    if (rightArm.active) {
                        lasers.add(new Laser(rightArm.getX(), leftArm.getY(), GameplayConstants.SOUTH, false));
                        rightArm.turret.fireTime = TimeUtils.nanoTime();
                    }
                    if (leftArm.active) {
                        lasers.add(new Laser(leftArm.getX(), leftArm.getY(), GameplayConstants.SOUTH, false));
                        leftArm.turret.fireTime = TimeUtils.nanoTime();
                    }

                    break;
            }
            armFireTime = TimeUtils.nanoTime();
        }
        float secSinceCoreFire = Utilities.secondsSince(coreFireTime);
        boolean coreFire = false;
        switch (behavior.coreFiringPattern) {
            case NONE: default:
                break;
            case SIMPLE:
                if (secSinceCoreFire < GameplayConstants.BOSS_CORE_FIRE_DELAY_SIMPLE) break;
                lasers.add(new Laser(
                        core.getX(),
                        core.getY(),
                        GameplayConstants.SOUTH,
                        false
                ));
                coreFire = true;
                break;
            case AIMED:
                if (secSinceCoreFire < GameplayConstants.BOSS_CORE_FIRE_DELAY_AIMED) break;
                lasers.add(new Laser(
                        core.getX(),
                        core.getY(),
                        (new Vector2(player.position)).sub(core.getX(), core.getY()),
                        false,
                        Laser.LaserType.POINTY
                ));
                coreFire = true;
                break;
            case SPRAY:
                float fireDelay = (phase == Phase.PHASE_3) ? GameplayConstants.BOSS_CORE_FIRE_DELAY_SPRAY_FAST : GameplayConstants.BOSS_CORE_FIRE_DELAY_SPRAY;
                if (secSinceCoreFire < fireDelay) break;
                Vector2 direction;
                int sprayShotCount = (int) ((Utilities.secondsSince(behaviorTime) % (fireDelay * 4)) / fireDelay);
                switch (sprayShotCount) {
                    case 0: case 2: default:
                        direction = GameplayConstants.SOUTH;
                        break;
                    case 1:
                        direction = GameplayConstants.SOUTHEAST;
                        break;
                    case 3:
                        direction = GameplayConstants.SOUTHWEST;
                        break;
                }
                lasers.add(new Laser(core.getX(), core.getY(), direction, false));
                coreFire = true;
                break;
            case RAPID:
                if (secSinceCoreFire < GameplayConstants.BOSS_CORE_FIRE_DELAY_RAPID) break;
                int rapidShotCount = (int) ((Utilities.secondsSince(behaviorTime) %
                        (GameplayConstants.BOSS_CORE_FIRE_DELAY_RAPID * GameplayConstants.BOSS_CORE_RAPID_FIRE_PERIOD)) / GameplayConstants.BOSS_CORE_FIRE_DELAY_RAPID);
                if (rapidShotCount < GameplayConstants.BOSS_CORE_RAPID_FIRE_BURST_SIZE) {
                    lasers.add(new Laser(core.getX(), core.getY(), GameplayConstants.SOUTH, false));
                }
                coreFire = true;
                break;
        }
        if (coreFire) {
            coreFireTime = TimeUtils.nanoTime();
            core.turret.fireTime = coreFireTime;
        }
        return lasers;
    }

    /**
     * Applies damage based on the location of a laser and returns whether a piece of the boss
     * was destroyed (for scoring purposes).
     * @param  x  The x coordinate of the laser.
     * @param  y  The y coordinate of the laser.
     * @return  true if a piece was destroyed, false otherwise.
     */
    public boolean takeDamage(float x, float y) {
        //Boss is invulnerable as it descends.
        if (phase == Phase.ENTRANCE) return false;
        if (rightArm.hit(x, y)) {
            rightArm.takeDamage();
            return !rightArm.active;
        } else if (leftArm.hit(x, y)) {
            leftArm.takeDamage();
            return !leftArm.active;
        } else if (core.hit(x, y) && !rightArm.active && !leftArm.active) {
            core.takeDamage();
            if (core.health <= 0) {
                phase = Phase.DEAD;
                return true;
            }
        }
        return false;
    }

    /**
     * Initializes the core, arms, and behavior settings of the boss.
     */
    public void init() {
        core = initCore();
        //True argument inits right arm, false inits left.
        rightArm = initArm(true);
        leftArm = initArm(false);
        turret = new BossTurret();
        phase = Phase.ENTRANCE;
        behavior = new Behavior(
                Motion.DESCEND,
                ArmFiringPattern.NONE,
                CoreFiringPattern.NONE,
                0.75f
        );
        coreFireTime = TimeUtils.nanoTime();
        armFireTime = TimeUtils.nanoTime();
    }

    /**
     * Creates boss core object.
     * @return  new BossCore
     */
    public BossCore initCore() {
        return new BossCore();
    }

    /**
     * Creates boss arm object.
     * @param  isRight  boolean parameter, true to make right arm, false to make left.
     * @return  new BossArm
     */
    public BossArm initArm(boolean isRight) {
        return new BossArm(isRight);
    }

    @Override
    public boolean hit(float x, float y) {
        return core.hit(x, y) ||
                leftArm.hit(x, y) ||
                rightArm.hit(x, y);
    }

    @Override
    public float getSpeed() {
        return GameplayConstants.BOSS_SPEED * behavior.speedMultiplier;
    }

    @Override
    public float getWidth() {
        return GameplayConstants.BOSS_WIDTH;
    }

    @Override
    public float getHeight() { return GameplayConstants.BOSS_HEIGHT; }

    @Override
    public TextureRegion getTextureRegion() {
        Animation anim;
        if (leftArm.active && rightArm.active) {
            anim = Assets.instance.bossAssets.bossWholeAnimation;
        } else if (!leftArm.active && !rightArm.active) {
            anim = Assets.instance.bossAssets.bossCoreAnimation;
        } else {
            anim = Assets.instance.bossAssets.bossDamagedAnimation;
        }
        return anim.getKeyFrame(Utilities.secondsSince(spawnTime));
    }

    @Override
    public boolean getIsReflectedHorizontal() {
        //Reflect horizontally only if the left arm is destroyed but the right arm is not.
        return (leftArm.active && !rightArm.active);
    }

    /**
     * Class representing the boss' core body.  Remains at the same position of the parent Boss
     * object, cannot take damage until the arms are inactive.
     */
    private class BossCore extends AbstractEntity {

        int health;

        BossTurret turret;

        public BossCore() {
            super(Boss.this.getX(), Boss.this.getY());
            turret = new BossTurret();
            health = GameplayConstants.BOSS_CORE_STARTING_HEALTH;
        }

        @Override
        public void update(float delta) {
            setPosition(Boss.this.getX(), Boss.this.getY());
            turret.setPosition(getX(), getY());
        }

        public void takeDamage() {
            health--;
        }

        @Override
        public float getSpeed() {
            return Boss.this.getSpeed();
        }

        @Override
        public float getWidth() { return GameplayConstants.BOSS_CORE_WIDTH; }

        @Override
        public float getHeight() { return GameplayConstants.BOSS_CORE_HEIGHT; }


        @Override
        public TextureRegion getTextureRegion() { return null; }
    }

    /**
     * Class representing the boss' two wings.  When health drops to zero, active instance variable
     * is set to false.
     */
    private class BossArm extends AbstractEntity {

        boolean isRight;
        boolean active;

        int health;

        BossTurret turret;

        public BossArm(boolean isRight) {
            super();
            this.isRight = isRight;
            this.health = GameplayConstants.BOSS_ARM_STARTING_HEALTH;
            active = true;
            position = new Vector2();
            turret = new BossTurret();
            update(0);
        }

        /**
         * Applies damage and sets arm to inactive if health is below zero.
         */
        public void takeDamage() {
            health--;
            active = health > 0;
        }

        @Override
        public void update(float delta) {
            setPosition(Boss.this.getX(), Boss.this.getY());
            position.add((isRight) ? GameplayConstants.BOSS_ARM_OFFSET : -GameplayConstants.BOSS_ARM_OFFSET, 0);
            float turretXOffset = (isRight) ? GameplayConstants.BOSS_ARM_TURRET_OFFSET_X : -GameplayConstants.BOSS_ARM_TURRET_OFFSET_X;
            turret.setPosition(Boss.this.getX() + turretXOffset, Boss.this.getY() - GameplayConstants.BOSS_ARM_TURRET_OFFSET_Y);
        }

        @Override
        public boolean hit(float x, float y) {
            return active && super.hit(x, y);
        }

        @Override
        public float getSpeed() { return Boss.this.getSpeed(); }

        @Override
        public float getWidth() { return GameplayConstants.BOSS_ARM_WIDTH; }

        @Override
        public float getHeight() { return GameplayConstants.BOSS_ARM_HEIGHT; }

        @Override
        public TextureRegion getTextureRegion() { return null; }

    }

    /**
     * Class for turrets that fire lasers, mounted on wings and in center.
     */
    private class BossTurret extends AbstractEntity {

        long fireTime;

        public BossTurret() {
            super();
            fireTime = TimeUtils.nanoTime() - 1000;
        }

        @Override
        public float getSpeed() { return Boss.this.getSpeed(); }

        @Override
        public float getWidth() { return GameplayConstants.BOSS_TURRET_WIDTH; }

        @Override
        public float getHeight() { return GameplayConstants.BOSS_TURRET_HEIGHT; }

        @Override
        public TextureRegion getTextureRegion() {
            return (Utilities.secondsSince(fireTime) < GameplayConstants.MUZZLE_FLASH_DURATION) ?
                Assets.instance.bossAssets.turretFiring : Assets.instance.bossAssets.turretNeutral;
        }
    }

    /**
     * Defines boss behavior including motion, speed, and firing pattern for arms and core.  This is
     * purely a data class and has no methods.
     */
    private static class Behavior {

        Motion motion;
        ArmFiringPattern armFiringPattern;
        CoreFiringPattern coreFiringPattern;
        float speedMultiplier;

        public Behavior(Motion motion, ArmFiringPattern armFiringPattern, CoreFiringPattern coreFiringPattern, float speedMultiplier) {
            this.motion = motion;
            this.armFiringPattern = armFiringPattern;
            this.coreFiringPattern = coreFiringPattern;
            this.speedMultiplier = speedMultiplier;
        }
    }

    public enum Motion {
        DESCEND,
        STRAFE,
        CENTER,
        TRACK_PLAYER
    }

    public enum ArmFiringPattern {
        ALTERNATING,
        SIMULTANEOUS,
        NONE
    }

    public enum CoreFiringPattern {
        SIMPLE,
        SPRAY,
        AIMED,
        RAPID,
        NONE
    }

    public enum Phase {
        ENTRANCE,
        PHASE_1,
        PHASE_2,
        PHASE_3,
        DEAD
    }
}
