package com.udacity.gamedev.spacekerfuffle.entities;

import com.badlogic.gdx.math.Vector2;
import com.udacity.gamedev.spacekerfuffle.util.GameplayConstants;

/**
 * Special explosion caused by missiles that destroys nearby enemies.
 */
public class MissileExplosion extends Explosion {

    //Keeps track of enemies killed by explosion (not initial missile impact).  Player gets
    //bonus points for killing multiple enemies with one missile.
    private int extraEnemiesKilled;

    public MissileExplosion(float x, float y) {
        super(x, y);
        setType(EntityType.MISSILE_EXPLOSION);
    }

    public MissileExplosion(AbstractEntity entity) {
        super(entity);
        setType(EntityType.MISSILE_EXPLOSION);
    }

    /**
     * Test whether a given entity is within the blast radius and thus should be destroyed.
     * @param  entity  AbstractEntity of interest
     * @return  true if the enemy should be destroyed, false otherwise.
     */
    public boolean consume(AbstractEntity entity) {
        if (hit(entity.getX(), entity.getY())) {
            extraEnemiesKilled++;
            return true;
        }
        return false;
    }

    /**
     * Calculates bonus points based on number of enemies destroyed by this explosion.
     * @return  Bonus point total.
     */
    public int bonusPoints() {
        int points = 0;
        for (int i = 0; i < extraEnemiesKilled; i++) {
            points += GameplayConstants.SCORE_ENEMY_KILL_ADDITION * i * i;
        }
        return points;
    }

    @Override
    public boolean hit(float x, float y) {
        //Explosions have circular hit area.
        return (Vector2.dst(getX(), getY(), x, y) < getHitWidth() / 2);
    }

    @Override
    public void init() {
        super.init();
        extraEnemiesKilled = 0;
    }

    @Override
    public float getWidth() {
        return GameplayConstants.MISSILE_EXPLOSION_WIDTH;
    }

    @Override
    public float getHeight() {
        return GameplayConstants.MISSILE_EXPLOSION_HEIGHT;
    }

    @Override
    public float getHitWidth() {
        return GameplayConstants.MISSILE_EXPLOSION_HIT_WIDTH;
    }

    @Override
    public float getHitHeight() { return getHitWidth(); }
}
