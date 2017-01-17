package com.udacity.gamedev.spacekerfuffle.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.udacity.gamedev.spacekerfuffle.entities.AbstractEntity;
import com.udacity.gamedev.spacekerfuffle.entities.Boss;
import com.udacity.gamedev.spacekerfuffle.entities.EnemyShip;
import com.udacity.gamedev.spacekerfuffle.entities.Explosion;
import com.udacity.gamedev.spacekerfuffle.entities.Laser;
import com.udacity.gamedev.spacekerfuffle.entities.Missile;
import com.udacity.gamedev.spacekerfuffle.entities.MissileExplosion;
import com.udacity.gamedev.spacekerfuffle.entities.PlayerShip;
import com.udacity.gamedev.spacekerfuffle.entities.Powerup;
import com.udacity.gamedev.spacekerfuffle.util.GameplayConstants;
import com.udacity.gamedev.spacekerfuffle.util.Utilities;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Manages/updates/renders all game objects in level and handles collision detection.
 */
public class Level {

    public static final String LOG_TAG = Level.class.getName();

    //Score variable represents score from things like enemy kills.  Total score also depends on
    //time since level start.
    public int score;
    public State state;

    private Array<AbstractEntity> entities;

    public PlayerShip playerShip;
    private Array<Laser> playerLasers;
    private Array<Missile> playerMissiles;

    private ArrayList<EnemyShip> enemiesToSpawn;
    private Array<EnemyShip> enemies;
    private Array<Laser> enemyLasers;

    private Boss boss;

    private ArrayList<Powerup> powerupsToSpawn;
    private Array<Powerup> powerups;

    private Array<Explosion> explosions;
    private Array<MissileExplosion> missileExplosions;

    private long levelTime;
    private float levelTopHeight;

    private Viewport viewport;

    public Level(Viewport viewport, ArrayList<EnemyShip> enemiesToSpawn, ArrayList<Powerup> powerupsToSpawn) {
        this.viewport = viewport;
        this.enemiesToSpawn = enemiesToSpawn;
        this.powerupsToSpawn = powerupsToSpawn;
        init();
    }

    /**
     * Renders all objects in the level.
     * @param  batch  Batch to render objects with.
     */
    public void render(Batch batch){
        if (state != State.PLAYING) return;

        for (AbstractEntity entity : entities) {
            entity.render(batch);
        }
    }

    /**
     * Update state of all objects currently in the level.
     * @param  delta  Seconds since last frame as float.
     */
    public void update(float delta) {
        if (state != State.PLAYING) return;

        float elapsedSeconds = secondsSinceStart();
        Array<AbstractEntity> entitiesToRemove = new Array<AbstractEntity>();

        levelTopHeight = GameplayConstants.INIT_LEVEL_Y - elapsedSeconds * GameplayConstants.LEVEL_ASCENT_SPEED;
        if (boss == null
                //Comment below line to skip to boss at level start.
                && levelTopHeight < 0
                ) {
            boss = new Boss(viewport.getWorldWidth() / 2, viewport.getWorldHeight(), viewport, playerShip);
            addEntity(boss);
        }

        for (Iterator<AbstractEntity> iterator = entities.iterator(); iterator.hasNext(); ) {
            AbstractEntity entity = iterator.next();
            entity.update(delta);
            if (entity.offScreen(viewport)) {
                entitiesToRemove.add(entity);
                iterator.remove();
                removeEntity(entity, false);
            }
        }

        if (boss != null) {
            addLasers(boss.getLasers(), false);
        }

        addLasers(playerShip.getLasers(), true);
        for (Missile missile : playerShip.getMissiles()) {
            addEntity(missile);
        }

        for (EnemyShip enemy : enemies) {
            addLasers(enemy.getLasers(playerShip), false);
        }

        for (Iterator<Explosion> iterator = explosions.iterator(); iterator.hasNext(); ) {
            Explosion explosion = iterator.next();
            if (explosion.isOver()) {
                entitiesToRemove.add(explosion);
                iterator.remove();
            }
        }

        for (Iterator<MissileExplosion> iterator = missileExplosions.iterator(); iterator.hasNext(); ) {
            MissileExplosion missileExplosion = iterator.next();
            if (missileExplosion.isOver()) {
                entitiesToRemove.add(missileExplosion);
                iterator.remove();
                score += missileExplosion.bonusPoints();
            }
        }

        detectCollisions(entitiesToRemove);

        entities.removeAll(entitiesToRemove, true);

        spawnEnemies();
        spawnPowerups();
    }

    /**
     * Detect collisions between projectiles and ships, destroy objects & spawn explosions as
     * necessary.
     * @param entitiesToRemove  Array to store entities that should be removed from the game.
     */
    private void detectCollisions(Array<AbstractEntity> entitiesToRemove) {

        LaserLoop:
        for (Iterator<Laser> pLaserIterator = playerLasers.iterator(); pLaserIterator.hasNext(); ) {
            Laser laser = pLaserIterator.next();
            for (Iterator<EnemyShip> enemyIterator = enemies.iterator(); enemyIterator.hasNext(); ) {
                EnemyShip enemy = enemyIterator.next();
                if (enemy.hit(laser.getX(), laser.getY())) {
                    entitiesToRemove.add(enemy);
                    enemyIterator.remove();
                    entitiesToRemove.add(laser);
                    pLaserIterator.remove();
                    addEntity(new Explosion(enemy));
                    score += GameplayConstants.SCORE_ENEMY_KILL_ADDITION;
                    continue LaserLoop;
                }
            }
            if (boss != null && boss.hit(laser.getX(), laser.getY())) {
                boolean partKilled = boss.takeDamage(laser.getX(), laser.getY());
                if (partKilled) checkForWin();
                entitiesToRemove.add(laser);
                pLaserIterator.remove();
            }
        }


        MissileLoop:
        for (Iterator<Missile> missileIterator = playerMissiles.iterator(); missileIterator.hasNext(); ) {
            Missile missile = missileIterator.next();
            for(Iterator<EnemyShip> enemyIterator = enemies.iterator(); enemyIterator.hasNext(); ) {
                EnemyShip enemy = enemyIterator.next();
                if (enemy.hit(missile.getX(), missile.getY())) {
                    entitiesToRemove.add(enemy);
                    enemyIterator.remove();
                    entitiesToRemove.add(missile);
                    missileIterator.remove();
                    addEntity(new Explosion(enemy));
                    addEntity(new MissileExplosion(missile));
                    score += GameplayConstants.SCORE_ENEMY_KILL_ADDITION;
                    continue MissileLoop;
                }
            }
            if (boss != null && boss.hit(missile.getX(), missile.getY())) {
                //Missiles deal double damage to boss, so takeDamage() is called twice.
                boolean partKilled = boss.takeDamage(missile.getX(), missile.getY());
                partKilled = boss.takeDamage(missile.getX(), missile.getY()) || partKilled;
                if (partKilled) checkForWin();
                entitiesToRemove.add(missile);
                missileIterator.remove();
                addEntity(new MissileExplosion(missile));
            }
        }

        for (Iterator<Laser> eLaserIterator = enemyLasers.iterator(); eLaserIterator.hasNext(); ) {
            Laser laser = eLaserIterator.next();
            if (playerShip.hit(laser.getX(), laser.getY())) {
                entitiesToRemove.add(laser);
                eLaserIterator.remove();
                score -= GameplayConstants.SCORE_DEATH_DEDUCTION;
                addEntity(new Explosion(playerShip));
                if (playerShip.die()) {
                    state = State.LOSE;
                }
            }
        }

        for (Iterator<Powerup> powerupIterator = powerups.iterator(); powerupIterator.hasNext(); ) {
            Powerup powerup = powerupIterator.next();
            if (playerShip.hit(powerup.getX(), powerup.getY())) {
                powerup.apply(playerShip);
                entitiesToRemove.add(powerup);
                powerupIterator.remove();
                score += GameplayConstants.SCORE_POWERUP_ADDITION;
            }
        }
        for (MissileExplosion missileExplosion : missileExplosions) {
            for (Iterator<EnemyShip> enemyIterator = enemies.iterator(); enemyIterator.hasNext(); ) {
                EnemyShip enemy = enemyIterator.next();
                if (missileExplosion.consume(enemy)) {
                    entitiesToRemove.add(enemy);
                    enemyIterator.remove();
                    addEntity(new Explosion(enemy));
                    score += GameplayConstants.SCORE_ENEMY_KILL_ADDITION;
                }
            }
        }

        for (Iterator<EnemyShip> enemyIterator = enemies.iterator(); enemyIterator.hasNext(); ) {
            EnemyShip enemy = enemyIterator.next();
            if (enemy.hit(playerShip.getX(), playerShip.getY())) {
                entitiesToRemove.add(enemy);
                enemyIterator.remove();
                addEntity(new Explosion(enemy));
                addEntity(new Explosion(playerShip));
                if (playerShip.die()) state = State.LOSE;
            }
        }
    }

    /**
     * Check enemiesToSpawn array to see if level has ascended to the point where new enemies should
     * appear and create them.
     */
    private void spawnEnemies() {
        while (true) {
            int lastIndex = enemiesToSpawn.size() - 1;
            if (lastIndex < 0) return;
            if (enemiesToSpawn.get(lastIndex).getY() < levelTopHeight) break;
            EnemyShip enemy = enemiesToSpawn.remove(lastIndex);
            enemy.setPosition(enemy.getX(), viewport.getWorldHeight());
            enemy.init();
            addEntity(enemy);
        }
    }

    /**
     * Same as spawnEnemies() but for new powerups.
     */
    private void spawnPowerups() {
        while (true) {
            int lastIndex = powerupsToSpawn.size() - 1;
            if (lastIndex < 0) return;
            if (powerupsToSpawn.get(lastIndex).getY() < levelTopHeight) break;
            Powerup powerup = powerupsToSpawn.remove(lastIndex);
            powerup.setPosition(powerup.getX(), viewport.getWorldHeight());
            powerup.init();
            addEntity(powerup);
        }
    }

    /**
     * Checks to see if boss is dead and updates game state if so.  Called when a piece of the boss
     * is killed.
     */
    private void checkForWin() {
        if (boss != null && boss.phase == Boss.Phase.DEAD) {
            state = State.WIN;
            score += GameplayConstants.SCORE_BOSS_KILL_ADDITION;
        }
        score += GameplayConstants.SCORE_BOSS_ARM_KILL_ADDITION;
    }

    /**
     * Initialize level state.
     */
    public void init() {
        score = 0;
        state = State.PLAYING;
        levelTime = TimeUtils.nanoTime();
        levelTopHeight = GameplayConstants.INIT_LEVEL_Y;
        entities = new Array<AbstractEntity>();
        playerShip = new PlayerShip(viewport);
        addEntity(playerShip);
        enemies = new Array<EnemyShip>();
        playerLasers = new Array<Laser>();
        enemyLasers = new Array<Laser>();
        playerMissiles = new Array<Missile>();
        powerups = new Array<Powerup>();
        explosions = new Array<Explosion>();
        missileExplosions = new Array<MissileExplosion>();
        Gdx.app.log(LOG_TAG, "Level initialized.");
    }

    /**
     * Adds an entity to the scene, inserting it in the general AbstractEntity array and the array
     * for that entity type.
     * @param entity   Entity to be added.
     */
    private void addEntity(AbstractEntity entity) {
        entities.add(entity);
        try {
            switch (entity.getType()) {
                case PLAYER:
                case BOSS:
                default:
                    return;
                case ENEMY_SHIP:
                    enemies.add((EnemyShip) entity);
                    break;
                case PLAYER_LASER:
                    playerLasers.add((Laser) entity);
                    break;
                case ENEMY_LASER:
                    enemyLasers.add((Laser) entity);
                    break;
                case MISSILE:
                    playerMissiles.add((Missile) entity);
                    break;
                case POWERUP:
                    powerups.add((Powerup) entity);
                    break;
                case EXPLOSION:
                    explosions.add((Explosion) entity);
                    break;
                case MISSILE_EXPLOSION:
                    missileExplosions.add((MissileExplosion) entity);
                    break;
            }
        } catch(Error e) {
            Gdx.app.log(LOG_TAG, "Attempted to add entity with incompatible type.");
        }
    }

    /**
     * Removes an entity from the scene.
     * @param entity  Entity to be removed.
     * @param removeFromGeneralArray  Boolean value: whether to remove from general abstract entity
     *                                array, or only from the typed array for that entity type.
     */
    private void removeEntity(AbstractEntity entity, boolean removeFromGeneralArray) {
        if (removeFromGeneralArray) entities.removeValue(entity, true);
        try {
            switch (entity.getType()) {
                case PLAYER:
                case BOSS:
                default:
                    return;
                case ENEMY_SHIP:
                    enemies.removeValue((EnemyShip) entity, true);
                    break;
                case PLAYER_LASER:
                    playerLasers.removeValue((Laser) entity, true);
                    break;
                case ENEMY_LASER:
                    enemyLasers.removeValue((Laser) entity, true);
                    break;
                case MISSILE:
                    playerMissiles.removeValue((Missile) entity, true);
                    break;
                case POWERUP:
                    powerups.removeValue((Powerup) entity, true);
                    break;
                case EXPLOSION:
                    explosions.removeValue((Explosion) entity, true);
                    break;
                case MISSILE_EXPLOSION:
                    missileExplosions.removeValue((MissileExplosion) entity, true);
                    break;
            }
        } catch(Error e) {
            Gdx.app.log(LOG_TAG, "Attempted to add entity with incompatible type.");
        }
    }

    /**
     * Adds a collection of lasers to the game, as given by enemies/the player.
     * @param lasers  Array of lasers to add.
     * @param playerLaser  Boolean determining identity of lasers: true for player lasers, false for
     *                     enemy lasers.
     */
    private void addLasers(Array<Laser> lasers, boolean playerLaser) {
        Array<Laser> levelLasers = (playerLaser) ? playerLasers : enemyLasers;
        levelLasers.addAll(lasers);
        entities.addAll(lasers);
    }

    /**
     * Determines seconds since the level began.
     * @return  Seconds since level start as float.
     */
    public float secondsSinceStart() {
        return Utilities.secondsSince(levelTime);
    }

    public enum State {
        PLAYING,
        WIN,
        LOSE
    }

}
