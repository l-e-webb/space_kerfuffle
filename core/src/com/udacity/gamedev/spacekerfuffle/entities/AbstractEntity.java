package com.udacity.gamedev.spacekerfuffle.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Abstract class that provides general functionality of game entities.  Parent of ships, powerups,
 * projectiles.
 */
public abstract class AbstractEntity implements Comparable<AbstractEntity> {

    public final String LOG_TAG = this.getClass().getName();

    private EntityType type;

    protected Vector2 position;
    protected Vector2 heading;
    protected boolean trackHeading = false;
    protected float rotation = 0;

    protected long spawnTime;

    protected Viewport viewport;

    public AbstractEntity() {
        position = new Vector2();
        heading = new Vector2();
    }

    public AbstractEntity(float x, float y) {
        position = new Vector2(x, y);
        heading = new Vector2();
    }

    /**
     * Draws the object at current position using texture from getTextureRegion() method.
     * @param  batch  Batch to draw the object with.
     */
    public void render(Batch batch) {
        TextureRegion region = getTextureRegion();
        batch.draw(
                region.getTexture(),
                position.x - getWidth() / 2,
                position.y - getHeight() / 2,
                getWidth() / 2,
                getHeight() / 2,
                getWidth(),
                getHeight(),
                1, 1,
                rotation,
                region.getRegionX(),
                region.getRegionY(),
                region.getRegionWidth(),
                region.getRegionHeight(),
                getIsReflectedHorizontal(),
                false
        );
    }

    /**
     * Updates current heading, then updates position based on heading, speed, and time since last
     * frame.
     * @param  delta  Seconds since last frame as float.
     */
    public void update(float delta) {
        updateHeading(delta);
        move();
    }

    /**
     * Moves object by current heading vector.  Note that to be sure the heading vector has the
     * appropriate length, updateHeading() must be called first with the seconds since last frame
     * as an argument.  move() should only be called directly after updateHeading() in the update()
     * method.
     */
    public void move() {
        position.add(heading);
    }

    /**
     * Tests whether the object is in on screen based on viewport instance variable.
     * @return  true if object has left screen, false otherwise.
     */
    public boolean offScreen() {
        return offScreen(viewport);
    }

    /**
     * Tests whether the object is on screen based on parameter viewport.
     * @param  viewport  viewport defining current screen area.
     * @return  true if object has left screen, false otherwise.
     */
    public boolean offScreen(Viewport viewport) {
        return (position.x < -getWidth() / 2 ||
                position.x > viewport.getWorldWidth() + getWidth() / 2 ||
                position.y < -getHeight() / 2 ||
                position.y > viewport.getWorldHeight() + getHeight() / 2);
    }

    /**
     * Update heading based on time since last frame to represent current motion.  After this method
     * is called and only after this method is called should the heading vector be considered
     * equal to the objects motion this frame.  Rotation is updated as well for objects whose
     * rotation tracks their heading.
     * @param  delta  Seconds since last frame as float.
     */
    public void updateHeading(float delta) {
        if (heading.equals(Vector2.Zero)) return;
        heading.setLength(getSpeed() * delta);
        if (trackHeading) {
            rotation = heading.angle() - 90;
        }
    }

    /**
     * Tests whether a given point is within the region defined by this object.
     * @param  x  x coordinate of interest.
     * @param  y  y coordinate of interest.
     * @return  true if (x,y) is inside this object's hit region, false otherwise.
     */
    public boolean hit(float x, float y) {
        return (x > getX() - getHitWidth() / 2 &&
                x < getX() + getHitWidth() / 2 &&
                y > getY() - getHitHeight() / 2 &&
                y < getY() + getHitHeight() / 2);
    }

    /**
     * Tests whether a given point is within the region defined by this object.
     * @param  location  Vector2 representing point of interest.
     * @return  true if locations is inside this object's hit region, false otherwise.
     */
    public boolean hit(Vector2 location) {
        return hit(location.x, location.y);
    }

    /**
     * Returns an array of Lasers fired by the object this frame.
     * @return  Array of Lasers.
     */
    public Array<Laser> getLasers() { return null; }

    public abstract float getSpeed();

    /**
     * Returns width of the object as used to render sprite.
     * @return  Width as float.
     */
    public abstract float getWidth();

    /**
     * Returns width of object's hit region (same as render width by default).
     * @return  Width as float.
     */
    public float getHitWidth() { return getWidth(); }

    /**
     * Returns height of the object as used to render sprite.
     * @return  Height as float.
     */
    public abstract float getHeight();

    /**
     * Returns height of object's hit region (same as render height by default).
     * @return Height as float.
     */
    public float getHitHeight() { return getHeight(); }

    /**
     * Get x-position.
     * @return  Object's x-coordinate.
     */
    public float getX() { return position.x; }

    /**
     * Get y-position.
     * @return  Object's y-coordinate.
     */
    public float getY() { return position.y; }

    /**
     * Set the entity's position.
     * @param x  Desired x-coordinate.
     * @param y  Desired y-coordinate.
     */
    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    /**
     * Returns TextureRegion used to render this object.
     * @return  TextureRegion.
     */
    public abstract TextureRegion getTextureRegion();

    /**
     * Returns whether this object should be horizontally reflected when rendered.
     * @return  True for reflection, false for not reflected, false by default.
     */
    public boolean getIsReflectedHorizontal() { return false; }

    /**
     * Initializes the entity right before they enter the game, setting their spawnTime.
     */
    public void init() {
        spawnTime = TimeUtils.nanoTime();
    }

    /**
     * Implementation of compareTo() that sorts by y value.  Used to sort enemy and powerup
     * lists ensure they are in position order.
     * @param  entity  Other AbstractEntity to compare to.
     * @return  Vertical difference between objects.
     */
    public int compareTo(AbstractEntity entity) {
        return (int) (getY() - entity.getY());
    }

    /**
     * Get the entity type, see enumeration.
     * @return  Entity's type.
     */
    public EntityType getType() {
        return type;
    }

    /**
     * Set entity's type, called in constructors of child classes.
     * @param type  The desired type.
     */
    public void setType(EntityType type) {
        this.type = type;
    }

    public enum EntityType {
        PLAYER,
        BOSS,
        ENEMY_SHIP,
        PLAYER_LASER,
        ENEMY_LASER,
        MISSILE,
        POWERUP,
        EXPLOSION,
        MISSILE_EXPLOSION
    }
}
