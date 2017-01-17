package com.udacity.gamedev.spacekerfuffle.entities;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
 * Class representing the player's ship.
 */
public class PlayerShip extends AbstractEntity {

    public int lives;
    public int missiles;
    public boolean improvedLasers;
    public boolean invulnerable;

    private Motion motion;

    private long laserFireTime;
    private long missileFireTime;
    private long improvedLaserTime;
    
    public PlayerShip(Viewport viewport) {
        super(0,0);
        this.viewport = viewport;
        init(true);
        setType(EntityType.PLAYER);
    }

    @Override
    public void init() {
        init(true);
    }

    /**
     * Initializes the player, centering them on the screen, making them invulnerable
     * and reseting their fire timers.
     * @param  stageStart  true argument will initialize the player's missile ammo and lives,
     *                     false argument will leave them the same.
     */
    public void init(boolean stageStart) {
        //position = new Vector2(viewport.getWorldWidth() / 2, GameplayConstants.PLAYER_SHIP_HEIGHT);
        position = new Vector2(GameplayConstants.WORLD_WIDTH / 2, GameplayConstants.PLAYER_SHIP_HEIGHT);
        heading = new Vector2();
        motion = Motion.NEUTRAL;
        spawnTime = TimeUtils.nanoTime();
        laserFireTime = TimeUtils.nanoTime() - 1000;
        missileFireTime = TimeUtils.nanoTime() - 1000;
        improvedLasers = false;
        invulnerable = true;
        if (stageStart) {
            lives = GameplayConstants.STARTING_LIVES;
            missiles = GameplayConstants.STARTING_MISSILES;
        }

    }

    @Override
    public void update(float delta){
        super.update(delta);
        if (invulnerable) {
            invulnerable = Utilities.secondsSince(spawnTime) < GameplayConstants.RESPAWN_INVULNERABILITY_DURATION;
        }
        position.x = Math.max(position.x, getWidth() / 2);
        position.x = Math.min(position.x, viewport.getWorldWidth() - getWidth() / 2);
        position.y = Math.max(position.y, getHeight() / 2);
        position.y = Math.min(position.y, viewport.getWorldHeight() - getHeight() / 2);
        if (improvedLasers &&
            Utilities.secondsSince(improvedLaserTime) > GameplayConstants.IMPROVED_LASER_DURATION) {
            improvedLasers = false;
        }
    }

    @Override
    public void updateHeading(float delta) {
        float movement = getSpeed() * delta;

        if (Gdx.app.getType() == Application.ApplicationType.Android
                || Gdx.app.getType() == Application.ApplicationType.iOS
                ) {
            if (Gdx.input.isTouched()) {
                Vector2 touchPos = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
                touchPos.sub(GameplayConstants.TOUCHSCREEN_CONTROL_OFFSET);
                touchPos.sub(position);
                heading.set(touchPos);
                if (heading.len() > movement) heading.setLength(movement);
            } else {
                heading.set(Vector2.Zero);
            }
        } else {
            float x = 0;
            float y = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                x -= 1;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                x += 1;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                y += 1;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                y -= 1;
            }
            heading.set(x, y).setLength(movement);
        }
        if (heading.x < 0) motion = Motion.ROLL_LEFT;
        else if (heading.x > 0) motion = Motion.ROLL_RIGHT;
        else motion = Motion.NEUTRAL;
    }

    /**
     * Tests whether the player is ready to fire and resets fireTimer if so.
     * @param weapon  indicating laser or missiles.
     * @return  true if the player is ready to fire, false if not.
     */
    public boolean fire(Weapon weapon) {
        switch (weapon) {
            case LASER:
                if (Utilities.secondsSince(laserFireTime) < GameplayConstants.PLAYER_LASER_FIRE_DELAY) {
                    return false;
                }
                laserFireTime = TimeUtils.nanoTime();
                return true;
            case MISSILE:
                if (missiles <= 0 ||
                    Utilities.secondsSince(missileFireTime) < GameplayConstants.PLAYER_MISSILE_FIRE_DELAY) {
                    return false;
                }
                missileFireTime = TimeUtils.nanoTime();
                missiles--;
                return true;
            default:
                return false;
        }
    }

    @Override
    public Array<Laser> getLasers() {
        boolean mobile = Gdx.app.getType() == Application.ApplicationType.Android
                || Gdx.app.getType() == Application.ApplicationType.iOS;
        Array<Laser> lasers = new Array<Laser>();
        if ((Gdx.input.isKeyPressed(Input.Keys.Z) || mobile) && fire(PlayerShip.Weapon.LASER)) {
            lasers.add(new Laser(
                    getX(),
                    getY(),
                    GameplayConstants.NORTH
            ));
            if (improvedLasers) {
                lasers.add(new Laser(
                        getX(),
                        getY(),
                        GameplayConstants.IMPROVED_LASER_LEFT_HEADING
                ));
                lasers.add(new Laser(
                        getX(),
                        getY(),
                        GameplayConstants.IMPROVED_LASER_RIGHT_HEADING
                ));
            }
        }
        return lasers;
    }

    /**
     * As getLasers but gets array of Missiles.
     * @return  Array of Missiles fired this frame.
     */
    public Array<Missile> getMissiles() {
        boolean mobile = Gdx.app.getType() == Application.ApplicationType.Android
                || Gdx.app.getType() == Application.ApplicationType.iOS;
        Array<Missile> missiles = new Array<Missile>();
        if ((Gdx.input.isKeyPressed(Input.Keys.X) || mobile) && fire(PlayerShip.Weapon.MISSILE)) {
            missiles.add(new Missile(
                    getX(),
                    getY(),
                    GameplayConstants.MISSILE_LEFT_HEADING
            ));
            missiles.add(new Missile(
                    getX(),
                    getY(),
                    GameplayConstants.MISSILE_RIGHT_HEADING
            ));
        }
        return missiles;
    }

    /**
     * Decrease the player's lives and return whether the game is over.
     * @return  true if the player is out of lives, false otherwise.
     */
    public boolean die() {
        lives--;
        init(false);
        return (lives <= 0);
    }

    /**
     * Adds missiles to missle ammo count.
     * @param  extraMissiles  Missiles to be added.
     */
    public void addMissiles(int extraMissiles) {
        missiles += extraMissiles;
    }

    /**
     * Turns on improved lasers.
     */
    public void improveLasers() {
        improvedLasers = true;
        improvedLaserTime = TimeUtils.nanoTime();
    }

    @Override
    public boolean hit(float x, float y) {
        return !invulnerable && super.hit(x, y);
    }

    @Override
    public void render(Batch batch) {
        if (invulnerable && Utilities.secondsSince(spawnTime) % GameplayConstants.INVULNERABILITY_BLINK_DURATION * 2 < GameplayConstants.INVULNERABILITY_BLINK_DURATION) {
            return;
        }
        super.render(batch);
    }

    @Override
    public float getWidth() { return GameplayConstants.PLAYER_SHIP_WIDTH; }

    @Override
    public float getHitWidth() { return GameplayConstants.PLAYER_SHIP_HIT_WIDTH; }

    @Override
    public float getHeight() { return GameplayConstants.PLAYER_SHIP_HEIGHT; }

    @Override
    public float getSpeed() { return GameplayConstants.PLAYER_SHIP_SPEED; }

    @Override
    public TextureRegion getTextureRegion() {
        Animation anim;
        switch (motion) {
            case ROLL_LEFT: case ROLL_RIGHT:
                anim = (Utilities.secondsSince(laserFireTime) < GameplayConstants.MUZZLE_FLASH_DURATION ||
                    Utilities.secondsSince(missileFireTime) < GameplayConstants.MUZZLE_FLASH_DURATION) ?
                        Assets.instance.playerShipAssets.playerShipTurningFiring :
                        Assets.instance.playerShipAssets.playerShipTurning;
                break;
            case NEUTRAL:
                anim = (Utilities.secondsSince(laserFireTime) < GameplayConstants.MUZZLE_FLASH_DURATION ||
                    Utilities.secondsSince(missileFireTime) < GameplayConstants.MUZZLE_FLASH_DURATION) ?
                        Assets.instance.playerShipAssets.playerShipFiring :
                        Assets.instance.playerShipAssets.playerShipNeutral;
                break;
            default:
                anim = Assets.instance.playerShipAssets.playerShipNeutral;
                break;
        }
        return anim.getKeyFrame(Utilities.secondsSince(spawnTime));
    }

    @Override
    public boolean getIsReflectedHorizontal() {
        return (motion == Motion.ROLL_LEFT);
    }

    public enum Weapon {
        LASER,
        MISSILE
    }

    public enum Motion {
        ROLL_LEFT,
        ROLL_RIGHT,
        NEUTRAL
    }
}
