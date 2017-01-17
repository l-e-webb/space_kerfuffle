package com.udacity.gamedev.spacekerfuffle.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/**
 * Singleton for loading and managing assets.
 */
public class Assets implements Disposable, AssetErrorListener  {

    public static final String LOG_TAG = Assets.class.getName();
    public static final Assets instance = new Assets();

    public PlayerShipAssets playerShipAssets;
    public EnemyShipAssets enemyShipAssets;
    public BossAssets bossAssets;
    public OtherAssets otherAssets;

    private AssetManager assetManager;

    private Assets() {}

    public void init(AssetManager assetManager) {
        this.assetManager = assetManager;
        assetManager.setErrorListener(this);
        assetManager.load(GameplayConstants.ATLAS_PATH, TextureAtlas.class);
        assetManager.finishLoading();

        TextureAtlas atlas = assetManager.get(GameplayConstants.ATLAS_PATH);
        playerShipAssets = new PlayerShipAssets(atlas);
        enemyShipAssets = new EnemyShipAssets(atlas);
        bossAssets = new BossAssets(atlas);
        otherAssets = new OtherAssets(atlas);

    }

    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error(LOG_TAG, "Unable to load asset " + asset.fileName, throwable);
    }

    public void dispose() {
        assetManager.dispose();
    }

    public class PlayerShipAssets {

        public final Animation playerShipNeutral;
        public final Animation playerShipFiring;
        public final Animation playerShipTurning;
        public final Animation playerShipTurningFiring;

        public PlayerShipAssets(TextureAtlas atlas) {

            playerShipNeutral = new Animation(
                    GameplayConstants.THRUSTER_LOOP_DURATION,
                    atlas.findRegions(GameplayConstants.PLAYER_NEUTRAL),
                    Animation.PlayMode.LOOP
            );

            playerShipFiring = new Animation(
                    GameplayConstants.THRUSTER_LOOP_DURATION,
                    atlas.findRegions(GameplayConstants.PLAYER_FIRING),
                    Animation.PlayMode.LOOP
            );

            playerShipTurning = new Animation(
                    GameplayConstants.THRUSTER_LOOP_DURATION,
                    atlas.findRegions(GameplayConstants.PLAYER_TURNING_RIGHT),
                    Animation.PlayMode.LOOP
            );

            playerShipTurningFiring = new Animation(
                    GameplayConstants.THRUSTER_LOOP_DURATION,
                    atlas.findRegions(GameplayConstants.PLAYER_TURNING_RIGHT_FIRING),
                    Animation.PlayMode.LOOP
            );
        }
    }

    public class EnemyShipAssets {

        public final Animation enemyNeutral;
        public final Animation enemyFiring;
        public final Animation enemyTurning;
        public final Animation enemyTurningFiring;

        public EnemyShipAssets(TextureAtlas atlas) {

            enemyNeutral = new Animation(
                    GameplayConstants.THRUSTER_LOOP_DURATION,
                    atlas.findRegions(GameplayConstants.ENEMY_NEUTRAL),
                    Animation.PlayMode.LOOP
            );

            enemyFiring = new Animation(
                    GameplayConstants.THRUSTER_LOOP_DURATION,
                    atlas.findRegions(GameplayConstants.ENEMY_FIRING),
                    Animation.PlayMode.LOOP
            );

            enemyTurning = new Animation(
                    GameplayConstants.THRUSTER_LOOP_DURATION,
                    atlas.findRegions(GameplayConstants.ENEMY_TURNING_RIGHT),
                    Animation.PlayMode.LOOP
            );

            enemyTurningFiring = new Animation(
                    GameplayConstants.THRUSTER_LOOP_DURATION,
                    atlas.findRegions(GameplayConstants.ENEMY_TURNING_RIGHT_FIRING),
                    Animation.PlayMode.LOOP
            );
        }
    }

    public class BossAssets {

        public final TextureRegion turretNeutral;
        public final TextureRegion turretFiring;

        public final Animation bossWholeAnimation;
        public final Animation bossDamagedAnimation;
        public final Animation bossCoreAnimation;

        public BossAssets(TextureAtlas atlas) {
            turretNeutral = atlas.findRegion(GameplayConstants.BOSS_TURRET_NEUTRAL);
            turretFiring = atlas.findRegion(GameplayConstants.BOSS_TURRET_FIRING);

            bossWholeAnimation = new Animation(
                    GameplayConstants.THRUSTER_LOOP_DURATION,
                    atlas.findRegions(GameplayConstants.BOSS_WHOLE),
                    Animation.PlayMode.LOOP
            );
            bossDamagedAnimation = new Animation(
                    GameplayConstants.THRUSTER_LOOP_DURATION,
                    atlas.findRegions(GameplayConstants.BOSS_DAMAGED),
                    Animation.PlayMode.LOOP
            );
            bossCoreAnimation = new Animation(
                    GameplayConstants.THRUSTER_LOOP_DURATION,
                    atlas.findRegions(GameplayConstants.BOSS_CORE),
                    Animation.PlayMode.LOOP
            );
        }
    }

    public class OtherAssets {

        public final AtlasRegion blueLaser;
        public final AtlasRegion redLaser;
        public final AtlasRegion pointyLaser;

        public final Animation missileAnimation;

        public final AtlasRegion extraLife;
        public final AtlasRegion empoweredLasers;
        public final AtlasRegion missileAmmo;

        public final Animation explosionAnimation;

        public final TextureRegion smallWhite;
        public final TextureRegion mediumYellow;
        public final TextureRegion largeRed;

        public OtherAssets(TextureAtlas atlas) {
            blueLaser = atlas.findRegion(GameplayConstants.BLUE_LASER);
            redLaser = atlas.findRegion(GameplayConstants.RED_LASER);
            pointyLaser = atlas.findRegion(GameplayConstants.POINTY_LASER);

            missileAnimation = new Animation(
                    GameplayConstants.THRUSTER_LOOP_DURATION,
                    atlas.findRegions(GameplayConstants.MISSILE),
                    Animation.PlayMode.LOOP
            );

            extraLife = atlas.findRegion(GameplayConstants.POWERUP_EXTRA_LIFE);
            empoweredLasers = atlas.findRegion(GameplayConstants.POWERUP_IMPROVED_LASERS);
            missileAmmo = atlas.findRegion(GameplayConstants.POWERUP_MISSILE_AMMO);

            explosionAnimation = new Animation(
                    GameplayConstants.EXPLOSION_FRAME_TIME,
                    atlas.findRegions(GameplayConstants.EXPLOSION),
                    Animation.PlayMode.NORMAL
            );

            smallWhite = atlas.findRegion(GameplayConstants.STAR, 1);
            mediumYellow = atlas.findRegion(GameplayConstants.STAR, 2);
            largeRed = atlas.findRegion(GameplayConstants.STAR, 3);
        }

    }

}
