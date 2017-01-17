package com.udacity.gamedev.spacekerfuffle.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.udacity.gamedev.spacekerfuffle.entities.EnemyShip;
import com.udacity.gamedev.spacekerfuffle.entities.Powerup;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Script that parses JSON for level data such as enemy and powerup locations.
 */
public class LevelJSONParser {

    public static final String LOG_TAG = LevelJSONParser.class.getName();

    private static final int ENEMY_LAYER_INDEX = 1;
    private static final int POWERUP_LAYER_INDEX = 2;

    private static JsonValue root = new JsonReader().parse(Gdx.files.internal(GameplayConstants.LEVEL_JSON_FILEPATH));

    /**
     * Creates array of EnemyShip objects from level JSON.
     * @return  Array of positioned and initialized EnemyShip objects.
     */
    public static ArrayList<EnemyShip> getEnemyArray() {
        JsonValue enemyListJson = root.get("layers").get(ENEMY_LAYER_INDEX).get("objects");
        ArrayList<EnemyShip> enemies = new ArrayList<EnemyShip>();
        for (int i = 0; i < enemyListJson.size; i++) {
            enemies.add(parseEnemyObject(enemyListJson.get(i)));
        }
        Collections.sort(enemies);
        return enemies;
    }

    /**
     * Creates an EnemyShip object from the corresponding JSON object.
     * @param enemy  JsonValue containing enemy information.
     * @return  New EnempyShip object.
     */
    public static EnemyShip parseEnemyObject(JsonValue enemy) {
        EnemyShip.MovementPatterns movePattern;
        String movePatternStr = enemy.get("properties").get("move-pattern").asString();
        if (movePatternStr.equals("simple")) {
            movePattern = EnemyShip.MovementPatterns.SIMPLE;
        } else if (movePatternStr.equals("simple-pause")) {
            movePattern = EnemyShip.MovementPatterns.SIMPLE_PAUSE;
        } else if (movePatternStr.equals("simple-pause-long")) {
            movePattern = EnemyShip.MovementPatterns.SIMPLE_PAUSE_LONG;
        } else if (movePatternStr.equals("s-right")) {
            movePattern = EnemyShip.MovementPatterns.S_RIGHT;
        } else if (movePatternStr.equals("s-left")) {
            movePattern = EnemyShip.MovementPatterns.S_LEFT;
        } else if (movePatternStr.equals("strafe-s-right")) {
            movePattern = EnemyShip.MovementPatterns.STRAFE_S_RIGHT;
        } else if (movePatternStr.equals("strafe-s-left")) {
            movePattern = EnemyShip.MovementPatterns.STRAFE_S_LEFT;
        } else if (movePatternStr.equals("zig-zag-right")) {
            movePattern = EnemyShip.MovementPatterns.ZIG_ZAG_RIGHT;
        } else if (movePatternStr.equals("zig-zag-left")) {
            movePattern = EnemyShip.MovementPatterns.ZIG_ZAG_LEFT;
        } else {
            movePattern = EnemyShip.MovementPatterns.SIMPLE;
        }
        EnemyShip.FiringPattern firePattern;
        String firePatternStr = enemy.get("properties").get("firing-pattern").asString();
        if (firePatternStr.equals("simple")) {
            firePattern = EnemyShip.FiringPattern.SIMPLE;
        } else if (firePatternStr.equals("spray")) {
            firePattern = EnemyShip.FiringPattern.SPRAY;
        } else if (firePatternStr.equals("homing")) {
            firePattern = EnemyShip.FiringPattern.AIMED;
        } else {
            firePattern = EnemyShip.FiringPattern.SIMPLE;
        }
        EnemyShip.Speed speed;
        String speedStr = enemy.get("properties").get("speed").asString();
        if (speedStr.equals("slow")) {
            speed = EnemyShip.Speed.SLOW;
        } else if (speedStr.equals("medium")) {
            speed = EnemyShip.Speed.MEDIUM;
        } else if (speedStr.equals("fast")) {
            speed = EnemyShip.Speed.FAST;
        } else {
            speed = EnemyShip.Speed.MEDIUM;
        }
        EnemyShip.FireRate fireRate;
        String fireRateStr = enemy.get("properties").get("fire-rate").asString();
        if (fireRateStr.equals("slow")) {
            fireRate = EnemyShip.FireRate.SLOW;
        } else if (fireRateStr.equals("medium")) {
            fireRate = EnemyShip.FireRate.MEDIUM;
        } else if (fireRateStr.equals("fast")) {
            fireRate = EnemyShip.FireRate.FAST;
        } else {
            fireRate = EnemyShip.FireRate.MEDIUM;
        }
        EnemyShip.MovePhaseDuration phaseDuration;
        String phaseDurationStr = enemy.get("properties").get("phase-duration").asString();
        if (phaseDurationStr.equals("short")) {
            phaseDuration = EnemyShip.MovePhaseDuration.SHORT;
        } else if (phaseDurationStr.equals("medium")) {
            phaseDuration = EnemyShip.MovePhaseDuration.MEDIUM;
        } else if (phaseDurationStr.equals("long")) {
            phaseDuration = EnemyShip.MovePhaseDuration.LONG;
        } else {
            phaseDuration = EnemyShip.MovePhaseDuration.MEDIUM;
        }
        return new EnemyShip(
                enemy.get("x").asFloat(),
                enemy.get("y").asFloat(),
                movePattern,
                phaseDuration,
                speed,
                firePattern,
                fireRate
        );
    }

    /**
     * Get array of powerups from level JSON.
     * @return  Array of initialized & positioned Powerups.
     */
    public static ArrayList<Powerup> getPowerupArray() {
        JsonValue powerupArray = root.get("layers").get(POWERUP_LAYER_INDEX).get("objects");
        ArrayList<Powerup> powerups = new ArrayList<Powerup>();
        for (int i = 0; i < powerupArray.size; i++) {
            Powerup.PowerupType type;
            JsonValue powerup = powerupArray.get(i);
            String powerupTypeStr = powerup.
                    get("properties").
                    get("powerup-type").
                    asString();
            if (powerupTypeStr.equals("extra-life")) {
                type = Powerup.PowerupType.EXTRA_LIFE;
            } else if (powerupTypeStr.equals("improved-lasers")) {
                type = Powerup.PowerupType.IMPROVED_LASERS;
            } else if (powerupTypeStr.equals("extra-missiles")) {
                type = Powerup.PowerupType.EXTRA_MISSILES;
            } else {
                type = Powerup.PowerupType.EXTRA_LIFE;
            }
            powerups.add(new Powerup(
                    powerup.get("x").asFloat(),
                    powerup.get("y").asFloat(),
                    type
            ));
        }
        Collections.sort(powerups);
        return powerups;

    }

}
