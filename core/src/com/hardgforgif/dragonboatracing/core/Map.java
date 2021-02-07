package com.hardgforgif.dragonboatracing.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.hardgforgif.dragonboatracing.GameData;
import com.hardgforgif.dragonboatracing.UI.ResultsUI;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Map {
    public Lane[] lanes = new Lane[4];

    // Added code start
    public World world;
    public String gameDifficulty;
    // Added code end

    // Map components
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    // The size of the screen we will render the map on
    private float screenWidth;
    // The width and the height of the map in tiles, used to calculate ratios
    private int mapWidth;
    private int mapHeight;
    //Added code start
    private final float FINISH_LINE_HEIGHT = 9000f;
    //Added code end
    // The width of each tile in Pixels
    private float unitScale;
    private Texture finishLineTexture;
    private Sprite finishLineSprite;
    // Added code start
    private int nrObstacles;
    private int nrPowerUps;
    // ArrayLists for obstacle collisions
    private ArrayList<Body> toBeRemovedBodies = new ArrayList<>();
    private ArrayList<Body> toUpdateHealth = new ArrayList<>();
    // ArrayLists for powerUps
    private ArrayList<Body> toHealBody = new ArrayList<>();
    private ArrayList<Body> toRestoreStamina = new ArrayList<>();
    private ArrayList<Body> toIncreaseSpeed = new ArrayList<>();
    private ArrayList<Body> toReduceTime = new ArrayList<>();
    private ArrayList<Body> toIncreaseAcceleration = new ArrayList<>();
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    // Added code end

    public Map(String tmxFile, float width, String gameDifficulty) {
        this.gameDifficulty = gameDifficulty;
        tiledMap = new TmxMapLoader().load(tmxFile);
        screenWidth = width;

        MapProperties prop = tiledMap.getProperties();
        mapWidth = prop.get("width", Integer.class);
        mapHeight = prop.get("height", Integer.class);
        unitScale = screenWidth / mapWidth / 32f;

        // Added code start
        world = new World(new Vector2(0f, 0f), true);
        createContactListener();

        createMapCollisions("CollisionLayerLeft");
        createMapCollisions("CollisionLayerRight");

        // Calculate the ratio between pixels, meters and tiles
        GameData.TILES_TO_METERS = getTilesToMetersRatio();
        GameData.PIXELS_TO_TILES = 1 / (GameData.METERS_TO_PIXELS * GameData.TILES_TO_METERS);

        if (gameDifficulty.equals("easy")) {
            nrObstacles = 20;
            nrPowerUps = 15;
        } else if (gameDifficulty.equals("medium")) {
            nrObstacles = 40;
            nrPowerUps = 10;

        } else if (gameDifficulty.equals("hard")) {
            nrObstacles = 80;
            nrPowerUps = 5;
        }
        // Added code end
    }

    // Modified code start
    /**
     * This method creates new ContactListener who's methods are executed when objects collide in the world.
     */
    private void createContactListener() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                // Remove obstacle or powerUp after collision

                if (fixtureA.getBody().getUserData() instanceof Obstacle || fixtureA.getBody().getUserData() instanceof PowerUp) {
                    toBeRemovedBodies.add(fixtureA.getBody());
                } else if (fixtureB.getBody().getUserData() instanceof Obstacle || fixtureB.getBody().getUserData() instanceof PowerUp) {
                    toBeRemovedBodies.add(fixtureB.getBody());
                }
                // Update health of boat if it collided with a obstacle
                if (fixtureA.getBody().getUserData() instanceof Boat && !(fixtureB.getBody().getUserData() instanceof PowerUp)) {
                    toUpdateHealth.add(fixtureA.getBody());
                } else if (fixtureB.getBody().getUserData() instanceof Boat && !(fixtureA.getBody().getUserData() instanceof PowerUp)) {
                    toUpdateHealth.add(fixtureB.getBody());
                }
                if (fixtureA.getBody().getUserData() instanceof PowerUp &&
                        fixtureB.getBody().getUserData() instanceof Boat
                ) {
                    String powerUpName = ((PowerUp) fixtureA.getBody().getUserData()).powerupName;
                    if (powerUpName.equals("healthBoost")) {
                        toHealBody.add(fixtureB.getBody());
                    } else if (powerUpName.equals("staminaBoost")) {
                        toRestoreStamina.add(fixtureB.getBody());
                    } else if (powerUpName.equals("speedBoost")) {
                        toIncreaseSpeed.add(fixtureB.getBody());
                    } else if (powerUpName.equals("accelerationBoost")) {
                        toIncreaseAcceleration.add(fixtureB.getBody());
                    } else if (powerUpName.equals("timeReduction")) {
                        toReduceTime.add(fixtureB.getBody());
                    }

                } else if (fixtureB.getBody().getUserData() instanceof PowerUp &&
                        fixtureA.getBody().getUserData() instanceof Boat
                ) {
                    String powerUpName = ((PowerUp) fixtureB.getBody().getUserData()).powerupName;
                    if (powerUpName.equals("healthBoost")) {
                        toHealBody.add(fixtureA.getBody());
                    } else if (powerUpName.equals("staminaBoost")) {
                        toRestoreStamina.add(fixtureA.getBody());
                    } else if (powerUpName.equals("speedBoost")) {
                        toIncreaseSpeed.add(fixtureA.getBody());
                    } else if (powerUpName.equals("accelerationBoost")) {
                        toIncreaseAcceleration.add(fixtureA.getBody());
                    } else if (powerUpName.equals("timeReduction")) {
                        toReduceTime.add(fixtureA.getBody());
                    }
                }

            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold manifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse contactImpulse) {
            }
        });
    }

    /**
     * This method updates the world by one step.
     * It first handles and empties the lists of objects that are queued for some action (powerups, removal, etc).
     * It then steps the world once.
     */
    public void stepWorld(Player player, AI[] opponents) {
        // Iterate through the bodies that need to be removed from the world after a collision
        for (Body body : toBeRemovedBodies) {
            // Find the obstacle that has this body and mark it as null
            // so it's sprite doesn't get rendered in future frames
            for (Lane lane : lanes) {
                for (Obstacle obstacle : lane.obstacles) {
                    if (obstacle.obstacleBody == body) {
                        obstacle.obstacleBody = null;
                    }
                }
                // Added code start
                for (PowerUp powerup : lane.powerUps) {
                    if (powerup.powerupBody == body) {
                        powerup.powerupBody = null;
                    }
                }
                // Added code end
            }

            // Remove the body from the world to avoid other collisions with it
            world.destroyBody(body);
        }

        // Iterate through the bodies marked to be damaged after a collision
        //Added code start
        //Under rare circumstances a boat can hit an obstacle more than once in a single physics update.
        //We keep a list of bodies that have already been hit to avoid this.
        ArrayList<Body> processedBodies = new ArrayList<>();
        //Added code end
        for (Body body : toUpdateHealth) {
            //Added code start
            if (processedBodies.contains(body)) {
                continue;
            }
            else {
                processedBodies.add(body);
            }
            //Added code end
            // if it's the player body
            if (player.boatBody == body && !player.hasFinished()) {
                // Reduce the health and the speed
                player.robustness -= 10f;
                player.current_speed -= 30f;

                // If all the health is lost
                if (player.robustness <= 0 && GameData.results.size() < 4) {
                    // Remove the body from the world, but keep it's sprite in place
                    world.destroyBody(player.boatBody);

                    // Add a DNF result
                    GameData.results.add(new Float[]{0f, Float.MAX_VALUE});

                    // Transition to the show result screen
                    GameData.showResultsState = true;
                    GameData.currentUI = new ResultsUI();
                }
            }
            // Otherwise, one of the AI has to be updated similarly
            else {
                for (int i = 0; i < 3; i++) {
                    if (opponents[i].boatBody == body && !opponents[i].hasFinished()) {

                        opponents[i].robustness -= 10f;
                        opponents[i].current_speed -= 30f;

                        if (opponents[i].robustness < 0 && GameData.results.size() < 4) {
                            world.destroyBody(opponents[i].boatBody);
                            GameData.results.add(new Float[]{Float.valueOf(i + 1), Float.MAX_VALUE});
                        }
                    }

                }
            }
        }

        handlePowerUp(player, opponents);

        toBeRemovedBodies.clear();
        toUpdateHealth.clear();

        // Added code start
        toHealBody.clear();
        toIncreaseAcceleration.clear();
        toRestoreStamina.clear();
        toReduceTime.clear();
        toIncreaseSpeed.clear();
        // Added code end

        // Advance the game world physics
        world.step(1f / 60f, 6, 2);
    }
    // Modified code end

    // Added code start
    /**
     * Applies picked up powerUp boosts to either the player or AI
     */
    public void handlePowerUp(Player player, AI[] opponents) {
        // A runnable for player boat
        Runnable decreaseAccelerationTaskPlayer = new Runnable() {
            @Override
            public void run() {
                player.speed -= 30f;
            }
        };

        // A runnable for AI. We need a different runnable to handle different logic for decreasing speed for AI boats
        Runnable decreaseAccelerationTaskAI = new Runnable() {
            @Override
            public void run() {
                int nr = GameData.reduceAIAccelerationList.pop();
                opponents[nr].speed -= 30f;
            }
        };
        // Iterate through the bodies marked to heal after picking up health powerUp
        for (Body body : toHealBody) {
            if (player.boatBody == body && !player.hasFinished()) {
                player.robustness += 20f;
            }
            // If the AI picked up the powerUp
            else {
                for (int i = 0; i < 3; i++) {
                    if (opponents[i].boatBody == body && !opponents[i].hasFinished()) {
                        opponents[i].robustness += 20f;
                    }
                }
            }
        }
        // Iterate through the bodies marked to restore some stamina after stamina boost powerUp
        for (Body body : toRestoreStamina) {
            if (player.boatBody == body && !player.hasFinished()) {
                player.stamina += 10f;
            }
            // If the AI picked up the powerUp
            else {
                for (int i = 0; i < 3; i++) {
                    if (opponents[i].boatBody == body && !opponents[i].hasFinished()) {
                        opponents[i].stamina += 10f;
                    }
                }
            }
        }
        /**
         *  Iterate through the bodies marked to reduce time
         *  We add the the reduced time to a GameData variable and after the leg is done we deduce this time
         *  and show how much was deducted in the leaderboard
         */
        for (Body body : toReduceTime) {
            if (player.boatBody == body && !player.hasFinished()) {
                GameData.timeReductions[0] += 2f;
            }
            // If the AI picked up the powerUp
            else {
                for (int i = 0; i < 3; i++) {
                    if (opponents[i].boatBody == body && !opponents[i].hasFinished()) {
                        GameData.timeReductions[i + 1] += 2f;
                    }
                }
            }
        }
        /**
         *  Iterate through the bodies marked to increase acceleration.
         *  We create a scheduler to run after 5 seconds and remove the effect.
         */

        for (Body body : toIncreaseAcceleration) {
            if (player.boatBody == body && !player.hasFinished()) {
                player.speed += 30f;
                player.acceleration += 50f;
                scheduler.schedule(decreaseAccelerationTaskPlayer, 5, TimeUnit.SECONDS);
            }
            // If the AI picked up the powerUp
            else {
                for (int i = 0; i < 3; i++) {
                    if (opponents[i].boatBody == body && !opponents[i].hasFinished()) {
                        GameData.reduceAIAccelerationList.push(i);
                        opponents[i].speed += 30f;
                        opponents[i].acceleration += 50f;
                        scheduler.schedule(decreaseAccelerationTaskAI, 5, TimeUnit.SECONDS);
                    }
                }
            }
        }
        // Iterate through the bodies marked to increase speed
        for (Body body : toIncreaseSpeed) {
            if (player.boatBody == body && !player.hasFinished()) {
                player.speed += 10f;
            }
            // If the AI picked up the powerUp
            else {
                for (int i = 0; i < 3; i++) {
                    if (opponents[i].boatBody == body && !opponents[i].hasFinished()) {
                        opponents[i].speed += 10f;
                    }
                }
            }
        }
    }
    // Added code end

    /**
     * @return The ratio between a tile and a meter in the game world
     */
    public float getTilesToMetersRatio() {
        return ((this.screenWidth / GameData.METERS_TO_PIXELS) / this.mapWidth);
    }

    /**
     * Creates bodies on the edges of the river, based on a pre-made layer of objects in Tiled
     *
     * @param collisionLayerName Name of the Tiled layer with the rectangle objects
     */
    public void createMapCollisions(String collisionLayerName) {
        // Get the objects from the object layer in the tilemap
        MapLayer collisionLayer = tiledMap.getLayers().get(collisionLayerName);
        MapObjects objects = collisionLayer.getObjects();

        // Iterate through the rectangles and create their physic bodies
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            // Find where we need to place the physics body
            float positionX = (rectangle.getX() * unitScale / GameData.METERS_TO_PIXELS) +
                    (rectangle.getWidth() * unitScale / GameData.METERS_TO_PIXELS / 2);
            float positionY = (rectangle.getY() * unitScale / GameData.METERS_TO_PIXELS) +
                    (rectangle.getHeight() * unitScale / GameData.METERS_TO_PIXELS / 2);
            bodyDef.position.set(positionX, positionY);

            Body objectBody = world.createBody(bodyDef);

            // Create the objects fixture, aka shape and physical properties
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(rectangle.getWidth() * unitScale / GameData.METERS_TO_PIXELS / 2,
                    rectangle.getHeight() * unitScale / GameData.METERS_TO_PIXELS / 2);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 0f;
            fixtureDef.restitution = 0f;
            fixtureDef.friction = 0f;
            Fixture fixture = objectBody.createFixture(fixtureDef);

            shape.dispose();
        }
    }

    /**
     * Renders the map on the screen
     */
    public void renderMap(OrthographicCamera camera, Batch batch) {
        // Modified code start
        // This was previously part of the constructor, but was moved so Maps could be created in the testing environment.
        if (tiledMapRenderer == null) {
            tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);
        }
        // Modified code end
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        batch.begin();
        batch.draw(finishLineSprite, finishLineSprite.getX(), finishLineSprite.getY(), finishLineSprite.getOriginX(),
                finishLineSprite.getOriginY(),
                finishLineSprite.getWidth(), finishLineSprite.getHeight(), finishLineSprite.getScaleX(),
                finishLineSprite.getScaleY(), finishLineSprite.getRotation());
        batch.end();
    }

    /**
     * Instantiates the lane array and spawns obstacles on each of the lanes
     */
    public void createLanes() {
        MapLayer leftLayer = tiledMap.getLayers().get("CollisionLayerLeft");
        MapLayer rightLayer = tiledMap.getLayers().get("Lane1");
        // Modified code start
        lanes[0] = new Lane(mapHeight, leftLayer, rightLayer, nrObstacles, nrPowerUps);
        lanes[0].constructBoundries(unitScale);
        lanes[0].spawnObstacles(world, FINISH_LINE_HEIGHT);
        lanes[0].spawnPowerUps(world, FINISH_LINE_HEIGHT);

        leftLayer = tiledMap.getLayers().get("Lane1");
        rightLayer = tiledMap.getLayers().get("Lane2");

        lanes[1] = new Lane(mapHeight, leftLayer, rightLayer, nrObstacles, nrPowerUps);
        lanes[1].constructBoundries(unitScale);
        lanes[1].spawnObstacles(world, FINISH_LINE_HEIGHT);
        lanes[1].spawnPowerUps(world, FINISH_LINE_HEIGHT);

        leftLayer = tiledMap.getLayers().get("Lane2");
        rightLayer = tiledMap.getLayers().get("Lane3");

        lanes[2] = new Lane(mapHeight, leftLayer, rightLayer, nrObstacles, nrPowerUps);
        lanes[2].constructBoundries(unitScale);
        lanes[2].spawnObstacles(world, FINISH_LINE_HEIGHT);
        lanes[2].spawnPowerUps(world, FINISH_LINE_HEIGHT);

        leftLayer = tiledMap.getLayers().get("Lane3");
        rightLayer = tiledMap.getLayers().get("CollisionLayerRight");

        lanes[3] = new Lane(mapHeight, leftLayer, rightLayer, nrObstacles, nrPowerUps);
        lanes[3].constructBoundries(unitScale);
        lanes[3].spawnObstacles(world, FINISH_LINE_HEIGHT);
        lanes[3].spawnPowerUps(world, FINISH_LINE_HEIGHT);
        // Modified code end
    }

    /**
     * Instantiates the lane array and spawns obstacles on each of the lanes
     *
     * @param mapIndex leg number
     */
    // Added code start
    public void createLanesFromLoad(int mapIndex) {
        Preferences prefs = Gdx.app.getPreferences("savedData");
        MapLayer leftLayer = tiledMap.getLayers().get("CollisionLayerLeft");
        MapLayer rightLayer = tiledMap.getLayers().get("Lane1");

        Obstacle[] loadedObstacles = getObstaclesFromLoad(mapIndex, 0, prefs);
        PowerUp[] loadedPowerUps = getPowerUpsFromLoad(mapIndex, 0, prefs);
        lanes[0] = new Lane(mapHeight, leftLayer, rightLayer, nrObstacles, nrPowerUps, loadedPowerUps, loadedObstacles);
        lanes[0].constructBoundries(unitScale);

        leftLayer = tiledMap.getLayers().get("Lane1");
        rightLayer = tiledMap.getLayers().get("Lane2");

        loadedObstacles = getObstaclesFromLoad(mapIndex, 1, prefs);
        loadedPowerUps = getPowerUpsFromLoad(mapIndex, 1, prefs);

        lanes[1] = new Lane(mapHeight, leftLayer, rightLayer, nrObstacles, nrPowerUps, loadedPowerUps, loadedObstacles);
        lanes[1].constructBoundries(unitScale);

        leftLayer = tiledMap.getLayers().get("Lane2");
        rightLayer = tiledMap.getLayers().get("Lane3");

        loadedObstacles = getObstaclesFromLoad(mapIndex, 2, prefs);
        loadedPowerUps = getPowerUpsFromLoad(mapIndex, 2, prefs);

        lanes[2] = new Lane(mapHeight, leftLayer, rightLayer, nrObstacles, nrPowerUps, loadedPowerUps, loadedObstacles);
        lanes[2].constructBoundries(unitScale);

        leftLayer = tiledMap.getLayers().get("Lane3");
        rightLayer = tiledMap.getLayers().get("CollisionLayerRight");

        loadedObstacles = getObstaclesFromLoad(mapIndex, 3, prefs);
        loadedPowerUps = getPowerUpsFromLoad(mapIndex, 3, prefs);

        lanes[3] = new Lane(mapHeight, leftLayer, rightLayer, nrObstacles, nrPowerUps, loadedPowerUps, loadedObstacles);
        lanes[3].constructBoundries(unitScale);
    }

    /**
     * Method to get all the obstacles from load file
     *
     * @param mapIndex  leg number
     * @param laneIndex lane number
     * @param prefs     load file
     * @return an array of obstacles
     */
    public Obstacle[] getObstaclesFromLoad(int mapIndex, int laneIndex, Preferences prefs) {
        int nrObstacles = prefs.getInteger("noObstacles" + laneIndex);
        Obstacle[] loadedObstacles = new Obstacle[nrObstacles];
        for (int i = 0; i != nrObstacles; i++) {
            String oTextureName = prefs.getString("Map" + mapIndex + "Lane" + laneIndex + "Obstacle" + i + "TextureName");
            float oScale = prefs.getFloat("Map" + mapIndex + "Lane" + laneIndex + "Obstacle" + i + "Scale");
            String oBodyFile = prefs.getString("Map" + mapIndex + "Lane" + laneIndex + "Obstacle" + i + "BodyFile");
            float oPosX = prefs.getFloat("Map" + mapIndex + "Lane" + laneIndex + "Obstacle" + i + "PosX");
            float oPosY = prefs.getFloat("Map" + mapIndex + "Lane" + laneIndex + "Obstacle" + i + "PosY");
            Obstacle obstacle = new Obstacle(oTextureName);
            obstacle.createObstacleBody(world, oPosX, oPosY, oBodyFile, oScale);
            loadedObstacles[i] = obstacle;
        }
        return loadedObstacles;
    }

    /**
     * Method to get all the powerUps from load file
     *
     * @param mapIndex  leg number
     * @param laneIndex lane number
     * @param prefs     load file
     * @return an array of powerUps
     */
    public PowerUp[] getPowerUpsFromLoad(int mapIndex, int laneIndex, Preferences prefs) {
        int nrPowerUps = prefs.getInteger("noPowerUps" + laneIndex);
        PowerUp[] loadedPowerUps = new PowerUp[nrPowerUps];
        for (int i = 0; i != nrPowerUps; i++) {
            String pTextureName = prefs.getString("Map" + mapIndex + "Lane" + laneIndex + "PowerUp" + i + "TextureName");
            float pScale = prefs.getFloat("Map" + mapIndex + "Lane" + laneIndex + "PowerUp" + i + "Scale");
            String pBodyFile = prefs.getString("Map" + mapIndex + "Lane" + laneIndex + "PowerUp" + i + "BodyFile");
            float pPosX = prefs.getFloat("Map" + mapIndex + "Lane" + laneIndex + "PowerUp" + i + "PosX");
            float pPosY = prefs.getFloat("Map" + mapIndex + "Lane" + laneIndex + "PowerUp" + i + "PosY");
            PowerUp powerUp = new PowerUp(pTextureName);
            powerUp.createPowerUpBody(world, pPosX, pPosY, pBodyFile, pScale);
            loadedPowerUps[i] = powerUp;
        }
        return loadedPowerUps;
    }
    // Added code end

    /**
     * Creates the finish line at a fixed position
     *
     * @param textureFile The texture oof the finish line
     */
    public void createFinishLine(String textureFile) {
        // Create the texture and the sprite of the finish line
        finishLineTexture = new Texture(textureFile);
        finishLineSprite = new Sprite(finishLineTexture);

        // Find out where it's going to start at, and how wide it will be, based on the limits of the edge lanes
        //Modified code start
        float startpoint = lanes[0].getLimitsAt(FINISH_LINE_HEIGHT)[0];
        float width = lanes[3].getLimitsAt(FINISH_LINE_HEIGHT)[1] - startpoint;

        // Set it's new found position and width
        finishLineSprite.setPosition(startpoint, FINISH_LINE_HEIGHT);
        //Modified code end
        finishLineSprite.setSize(width, 100);
    }

}
