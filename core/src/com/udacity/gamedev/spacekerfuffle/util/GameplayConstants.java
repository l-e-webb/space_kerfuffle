package com.udacity.gamedev.spacekerfuffle.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * Gameplay and rendering constants as static final variables.
 */
public class GameplayConstants {


    public static final int DESKTOP_WINDOW_WIDTH = 480;
    public static final int DESKTOP_WINDOW_HEIGHT = 640;

    public static final float WORLD_WIDTH = 480;
    public static final float WORLD_HEIGHT = 640;
    public static final Color CLEAR_COLOR = Color.BLACK;

    public static final float INIT_LEVEL_Y = 7680;
    public static final float LEVEL_ASCENT_SPEED = 80;

    public static final float PLAYER_SHIP_WIDTH = 50;
    public static final float PLAYER_SHIP_HEIGHT = 50;
    public static final float PLAYER_SHIP_HIT_WIDTH = 35;
    public static final float PLAYER_SHIP_SPEED = WORLD_WIDTH;
    public static final float PLAYER_LASER_FIRE_DELAY = 0.2f;
    public static final float PLAYER_MISSILE_FIRE_DELAY = 0.5f;
    public static final int STARTING_LIVES = 3;
    public static final int STARTING_MISSILES = 10;
    public static final float RESPAWN_INVULNERABILITY_DURATION = 1.25f;
    public static final float INVULNERABILITY_BLINK_DURATION = 0.1f;

    public static final float ENEMY_SHIP_WIDTH = 50;
    public static final float ENEMY_SHIP_HEIGHT = 50;
    public static final float ENEMY_SPEED_SLOW = 30;
    public static final float ENEMY_SPEED_MEDIUM = 50;
    public static final float ENEMY_SPEED_FAST = 90;
    public static final float ENEMY_FIRE_DELAY_SLOW = 4f;
    public static final float ENEMY_FIRE_DELAY_MEDIUM = 1.5f;
    public static final float ENEMY_FIRE_DELAY_FAST = 0.5f;
    public static final float ENEMY_PHASE_DURATION_SHORT = 1;
    public static final float ENEMY_PHASE_DURATION_MEDIUM = 2;
    public static final float ENEMY_PHASE_DURATION_LONG = 3;

    //Boss rendering dimensions:
    public static final float BOSS_ASPECT_RATIO = 112f / 64f;
    public static final float BOSS_WIDTH = 250;
    public static final float BOSS_HEIGHT = BOSS_WIDTH / BOSS_ASPECT_RATIO;
    //Boss core hit box:
    public static final float BOSS_CORE_WIDTH_PERCENTAGE = 44f / 112f;
    public static final float BOSS_CORE_WIDTH = BOSS_WIDTH * BOSS_CORE_WIDTH_PERCENTAGE;
    public static final float BOSS_CORE_HEIGHT_PERCENTAGE = 56f / 64f;
    public static final float BOSS_CORE_HEIGHT = BOSS_HEIGHT * BOSS_CORE_HEIGHT_PERCENTAGE;
    //Boss arm hit box:
    public static final float BOSS_ARM_WIDTH_PERCENTAGE = 34f / 112f;
    public static final float BOSS_ARM_WIDTH = BOSS_WIDTH * BOSS_ARM_WIDTH_PERCENTAGE;
    public static final float BOSS_ARM_HEIGHT_PERCENTAGE = 26f / 64f;
    public static final float BOSS_ARM_HEIGHT = BOSS_HEIGHT * BOSS_ARM_HEIGHT_PERCENTAGE;
    //Arm turret offsets from center:
    public static final float BOSS_ARM_TURRET_OFFSET_X_PERCENTAGE = 32f / 112f;
    public static final float BOSS_ARM_TURRET_OFFSET_X = BOSS_WIDTH * BOSS_ARM_TURRET_OFFSET_X_PERCENTAGE;
    public static final float BOSS_ARM_TURRET_OFFSET_Y_PERCENTAGE = 10f / 64f;
    public static final float BOSS_ARM_TURRET_OFFSET_Y = BOSS_HEIGHT * BOSS_ARM_TURRET_OFFSET_Y_PERCENTAGE;
    //Turret rendering dimensions:
    public static final float BOSS_TURRET_WIDTH = 35;
    public static final float BOSS_TURRET_HEIGHT = 35;
    //Boss arm center offset:
    public static final float BOSS_ARM_OFFSET = BOSS_CORE_WIDTH / 2 + BOSS_ARM_WIDTH / 2;
    public static final float BOSS_TOP_SCREEN_OFFSET = BOSS_HEIGHT / 2;
    public static final float BOSS_SIDE_SCREEN_OFFSET = BOSS_CORE_WIDTH / 2;
    //Boss gameplay statistics:
    public static final float BOSS_SPEED = 40;
    public static final int BOSS_ARM_STARTING_HEALTH = 50;
    public static final int BOSS_CORE_STARTING_HEALTH = 50;
    public static final float BOSS_ARM_FIRE_DELAY = 1f;
    public static final float BOSS_CORE_FIRE_DELAY_SIMPLE = BOSS_ARM_FIRE_DELAY;
    public static final float BOSS_CORE_FIRE_DELAY_AIMED = BOSS_ARM_FIRE_DELAY;
    public static final float BOSS_CORE_FIRE_DELAY_SPRAY = 0.75f;
    public static final float BOSS_CORE_FIRE_DELAY_SPRAY_FAST = 0.5f;
    public static final float BOSS_CORE_FIRE_DELAY_RAPID = 0.2f;
    public static final int BOSS_CORE_RAPID_FIRE_PERIOD = 20;
    public static final int BOSS_CORE_RAPID_FIRE_BURST_SIZE = 10;
    public static final float BOSS_BEHAVIOR_DURATION = 10;

    public static final float LASER_WIDTH = 15;
    public static final float LASER_HEIGHT = 30;
    public static final float PLAYER_LASER_SPEED = WORLD_HEIGHT;
    public static final Vector2 IMPROVED_LASER_LEFT_HEADING = new Vector2(-0.33f, 1);
    public static final Vector2 IMPROVED_LASER_RIGHT_HEADING = new Vector2(0.33f, 1);
    public static final float ENEMY_LASER_SPEED = PLAYER_LASER_SPEED / 4;

    public static final float MISSILE_WIDTH = 30;
    public static final float MISSILE_HEIGHT = 30;
    public static final float MISSILE_INITIAL_LATERAL_SPEED = 175;
    public static final float MISSILE_LATERAL_MOTION_TIME = 0.4f;
    //After MISSILE_LATERAL_MOTION_TIME seconds have passed, the missile speed will be one fourth
    //the initial lateral speed.
    public static final float MISSILE_LATERAL_DECAY_FACTOR = (3 * MISSILE_INITIAL_LATERAL_SPEED) / (4 * MISSILE_LATERAL_MOTION_TIME);
    public static final Vector2 MISSILE_LEFT_HEADING = new Vector2(-1, 0.33f);
    public static final Vector2 MISSILE_RIGHT_HEADING = new Vector2(1, 0.33f);
    public static final float MISSILE_ACCEL_FACTOR = WORLD_HEIGHT / 10;

    public static final float POWERUP_WIDTH = 30;
    public static final float POWERUP_HEIGHT = 30;
    public static final float POWERUP_SPEED = 75;
    public static final int EXTRA_MISSILE_AMMO = 5;
    public static final float IMPROVED_LASER_DURATION = 5.5f;

    public static final float EXPLOSION_WIDTH = 50;
    public static final float EXPLOSION_HEIGHT = 50;
    public static final float MISSILE_EXPLOSION_WIDTH = 100;
    public static final float MISSILE_EXPLOSION_HEIGHT = 100;
    public static final float MISSILE_EXPLOSION_HIT_WIDTH = 100;
    public static final float MISSILE_EXPLOSION_HIT_HEIGHT = 100;

    public static final float EXPLOSION_FRAME_TIME = 0.125f;

    public static final int SCORE_ENEMY_KILL_ADDITION = 500;
    public static final int SCORE_POWERUP_ADDITION = 200;
    public static final int SCORE_BOSS_ARM_KILL_ADDITION = 5000;
    public static final int SCORE_BOSS_KILL_ADDITION = SCORE_BOSS_ARM_KILL_ADDITION * 2;
    public static final int SCORE_DEATH_DEDUCTION = 1000;
    public static final int SCORE_PER_SECOND = 10;

    public static final Vector2 NORTH = new Vector2(0, 1);
    public static final Vector2 EAST = new Vector2(1, 0);
    public static final Vector2 SOUTH = new Vector2(0, -1);
    public static final Vector2 WEST = new Vector2(-1, 0);
    public static final Vector2 NORTHEAST = new Vector2(1, 1);
    public static final Vector2 NORTHWEST = new Vector2(-1, 1);
    public static final Vector2 SOUTHEAST = new Vector2(1, -1);
    public static final Vector2 SOUTHWEST = new Vector2(-1, -1);

    public static final Vector2 TOUCHSCREEN_CONTROL_OFFSET = new Vector2(0, -PLAYER_SHIP_HEIGHT);

    public static final String ATLAS_PATH = "images/spacekerfuffle.pack.atlas";

    public static final String LEVEL_JSON_FILEPATH = "level_json.json";

    //Strings for fetching regions from TextureAtlas.
    public static final String PLAYER_NEUTRAL = "player";
    public static final String PLAYER_FIRING = "playershoot";
    public static final String PLAYER_TURNING_RIGHT = "playerturn";
    public static final String PLAYER_TURNING_RIGHT_FIRING = "playerturnshoot";
    public static final String ENEMY_NEUTRAL = "enemy";
    public static final String ENEMY_FIRING = "enemyshoot";
    public static final String ENEMY_TURNING_RIGHT = "enemyturn";
    public static final String ENEMY_TURNING_RIGHT_FIRING = "enemyturnshoot";
    public static final String BOSS_WHOLE = "boss";
    public static final String BOSS_DAMAGED = "bossdamage";
    public static final String BOSS_CORE = "bosscore";
    public static final String BOSS_TURRET_NEUTRAL = "turret";
    public static final String BOSS_TURRET_FIRING = "turretshoot";
    public static final String POWERUP_EXTRA_LIFE = "extralife";
    public static final String POWERUP_MISSILE_AMMO = "missileammo";
    public static final String POWERUP_IMPROVED_LASERS = "triplelaser";
    public static final String BLUE_LASER = "bluelaser";
    public static final String RED_LASER = "redlaser";
    public static final String POINTY_LASER = "sharp";
    public static final String MISSILE = "missile";
    public static final String EXPLOSION = "explosion";
    public static final String STAR = "star";

    public static final float THRUSTER_LOOP_DURATION = 0.5f;
    public static final float MUZZLE_FLASH_DURATION = 0.25f;

    public static final float LOW_STAR_DENSITY_FACTOR = 0.25f;
    public static final float MEDIUM_STAR_DENSITY_FACTOR = 0.5f;
    public static final float HIGH_STAR_DENSITY_FACTOR = 0.75f;
    public static final float WHITE_STAR_PERCENTAGE = 0.90f;
    public static final float YELLOW_STAR_PERCENTAGE = 0.09f;
    public static final float RED_STAR_PERCENTAGE = 0.01f;
    public static final float STAR_WIDTH_SMALL = 5;
    public static final float STAR_WIDTH_MEDIUM = 8;
    public static final float STAR_WIDTH_LARGE = 10;
    public static final float STAR_SPEED_SLOW = 10;
    public static final float STAR_SPEED_MEDIUM = 20;
    public static final float STAR_SPEED_FAST = 50;

}
