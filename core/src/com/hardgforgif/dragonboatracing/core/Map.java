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
import com.badlogic.gdx.physics.box2d.*;
import com.hardgforgif.dragonboatracing.GameData;

public class Map {
    public Lane[] lanes = new Lane[4];

    // Added code start
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
    // The width of each tile in Pixels
    private float unitScale;
    private Texture finishLineTexture;
    private Sprite finishLineSprite;
    // Added code start
    private int nrObstacles;
    private int nrPowerUps;
    // Added code end

    public Map(String tmxFile, float width, String gameDifficulty) {
        this.gameDifficulty = gameDifficulty;
        tiledMap = new TmxMapLoader().load(tmxFile);
        screenWidth = width;

        MapProperties prop = tiledMap.getProperties();
        mapWidth = prop.get("width", Integer.class);
        mapHeight = prop.get("height", Integer.class);

        unitScale = screenWidth / mapWidth / 32f;
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);

        // Added code start
        if (gameDifficulty == "easy") {
            nrObstacles = 30;
            nrPowerUps = 15;
        } else if (gameDifficulty == "medium") {
            nrObstacles = 50;
            nrPowerUps = 10;

        } else if (gameDifficulty == "hard") {
            nrObstacles = 80;
            nrPowerUps = 2;
        }
        // Added code end
    }

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
     * @param world              World to spawn the bodies in
     */
    public void createMapCollisions(String collisionLayerName, World world) {
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
     *
     * @param world World to spawn the obstacles in
     */
    public void createLanes(World world) {
        MapLayer leftLayer = tiledMap.getLayers().get("CollisionLayerLeft");
        MapLayer rightLayer = tiledMap.getLayers().get("Lane1");
        // Modified code start
        lanes[0] = new Lane(mapHeight, leftLayer, rightLayer, nrObstacles, nrPowerUps);
        lanes[0].constructBoundries(unitScale);
        lanes[0].spawnObstacles(world, mapHeight / GameData.PIXELS_TO_TILES);
        lanes[0].spawnPowerUps(world, mapHeight / GameData.PIXELS_TO_TILES);

        leftLayer = tiledMap.getLayers().get("Lane1");
        rightLayer = tiledMap.getLayers().get("Lane2");

        lanes[1] = new Lane(mapHeight, leftLayer, rightLayer, nrObstacles, nrPowerUps);
        lanes[1].constructBoundries(unitScale);
        lanes[1].spawnObstacles(world, mapHeight / GameData.PIXELS_TO_TILES);
        lanes[1].spawnPowerUps(world, mapHeight / GameData.PIXELS_TO_TILES);

        leftLayer = tiledMap.getLayers().get("Lane2");
        rightLayer = tiledMap.getLayers().get("Lane3");

        lanes[2] = new Lane(mapHeight, leftLayer, rightLayer, nrObstacles, nrPowerUps);
        lanes[2].constructBoundries(unitScale);
        lanes[2].spawnObstacles(world, mapHeight / GameData.PIXELS_TO_TILES);
        lanes[2].spawnPowerUps(world, mapHeight / GameData.PIXELS_TO_TILES);

        leftLayer = tiledMap.getLayers().get("Lane3");
        rightLayer = tiledMap.getLayers().get("CollisionLayerRight");

        lanes[3] = new Lane(mapHeight, leftLayer, rightLayer, nrObstacles, nrPowerUps);
        lanes[3].constructBoundries(unitScale);
        lanes[3].spawnObstacles(world, mapHeight / GameData.PIXELS_TO_TILES);
        lanes[3].spawnPowerUps(world, mapHeight / GameData.PIXELS_TO_TILES);
        // Modified code end
    }

    /**
     * Instantiates the lane array and spawns obstacles on each of the lanes
     *
     * @param world    World to spawn the obstacles in
     * @param mapIndex leg number
     */
    // Added code start
    public void createLanesFromLoad(World world, int mapIndex) {
        Preferences prefs = Gdx.app.getPreferences("savedData");
        MapLayer leftLayer = tiledMap.getLayers().get("CollisionLayerLeft");
        MapLayer rightLayer = tiledMap.getLayers().get("Lane1");

        Obstacle[] loadedObstacles = getObstaclesFromLoad(world, mapIndex, 0, prefs);
        PowerUp[] loadedPowerUps = getPowerUpsFromLoad(world, mapIndex, 0, prefs);
        lanes[0] = new Lane(mapHeight, leftLayer, rightLayer, nrObstacles, nrPowerUps, loadedPowerUps, loadedObstacles);
        lanes[0].constructBoundries(unitScale);

        leftLayer = tiledMap.getLayers().get("Lane1");
        rightLayer = tiledMap.getLayers().get("Lane2");

        loadedObstacles = getObstaclesFromLoad(world, mapIndex, 1, prefs);
        loadedPowerUps = getPowerUpsFromLoad(world, mapIndex, 1, prefs);

        lanes[1] = new Lane(mapHeight, leftLayer, rightLayer, nrObstacles, nrPowerUps, loadedPowerUps, loadedObstacles);
        lanes[1].constructBoundries(unitScale);

        leftLayer = tiledMap.getLayers().get("Lane2");
        rightLayer = tiledMap.getLayers().get("Lane3");

        loadedObstacles = getObstaclesFromLoad(world, mapIndex, 2, prefs);
        loadedPowerUps = getPowerUpsFromLoad(world, mapIndex, 2, prefs);

        lanes[2] = new Lane(mapHeight, leftLayer, rightLayer, nrObstacles, nrPowerUps, loadedPowerUps, loadedObstacles);
        lanes[2].constructBoundries(unitScale);

        leftLayer = tiledMap.getLayers().get("Lane3");
        rightLayer = tiledMap.getLayers().get("CollisionLayerRight");

        loadedObstacles = getObstaclesFromLoad(world, mapIndex, 3, prefs);
        loadedPowerUps = getPowerUpsFromLoad(world, mapIndex, 3, prefs);

        lanes[3] = new Lane(mapHeight, leftLayer, rightLayer, nrObstacles, nrPowerUps, loadedPowerUps, loadedObstacles);
        lanes[3].constructBoundries(unitScale);
    }

    /**
     * Method to get all the obstacles from load file
     *
     * @param world     World to spawn the obstacles in
     * @param mapIndex  leg number
     * @param laneIndex lane number
     * @param prefs     load file
     * @return an array of obstacles
     */
    public Obstacle[] getObstaclesFromLoad(World world, int mapIndex, int laneIndex, Preferences prefs) {
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
     * @param world     World to spawn the obstacles in
     * @param mapIndex  leg number
     * @param laneIndex lane number
     * @param prefs     load file
     * @return an array of powerUps
     */
    public PowerUp[] getPowerUpsFromLoad(World world, int mapIndex, int laneIndex, Preferences prefs) {
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
        float startpoint = lanes[0].getLimitsAt(9000f)[0];
        float width = lanes[3].getLimitsAt(9000f)[1] - startpoint;

        // Set it's new found position and width
        finishLineSprite.setPosition(startpoint, 9000f);
        finishLineSprite.setSize(width, 100);
    }

}
