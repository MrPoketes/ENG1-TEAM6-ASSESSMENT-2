package com.hardgforgif.dragonboatracing;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.hardgforgif.dragonboatracing.UI.GamePlayUI;
import com.hardgforgif.dragonboatracing.UI.MenuUI;
import com.hardgforgif.dragonboatracing.UI.ResultsUI;
import com.hardgforgif.dragonboatracing.UI.UI;
import com.hardgforgif.dragonboatracing.core.*;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game extends ApplicationAdapter implements InputProcessor {
    // Added code start
    private static final int PAUSED_BACKGROUND_WIDTH = 800;
    private static final int PAUSED_BACKGROUND_HEIGHT = 500;
    private static final int PLAY_BUTTON_WIDTH = 200;
    private static final int PLAY_BUTTON_HEIGHT = 150;
    private static final int SAVE_BUTTON_WIDTH = 200;
    private static final int SAVE_BUTTON_HEIGHT = 150;

    Texture pausedBackground;
    Texture playButton;
    Texture saveButton;

    Preferences prefs;
    // Added code end
    private Player player;
    private AI[] opponents = new AI[3];
    private Map[] map = new Map[3];
    private Batch batch;
    private Batch UIbatch;
    private OrthographicCamera camera;
    private Vector2 mousePosition = new Vector2();
    private Vector2 clickPosition = new Vector2();
    private boolean[] pressedKeys = new boolean[4]; // W, A, S, D buttons status
    // Added code start
    private boolean isPaused;
    // Added code end

    @Override
    public void create() {
        // Initialize the sprite batches
        batch = new SpriteBatch();
        UIbatch = new SpriteBatch();

        // Get the values of the screen dimensions
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Removed code

        // Initialize the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);

        // Set the app's input processor
        Gdx.input.setInputProcessor(this);

        // Added code start
        isPaused = false;
        pausedBackground = new Texture("Background.png");
        playButton = new Texture("PlaySelected.png");
        saveButton = new Texture("SaveSelected.png");
        // Added code end


    }

    /**
     * Sets the camera y position at the y position of a player's sprite
     *
     * @param player The target player
     */
    private void updateCamera(Player player) {
        camera.position.set(camera.position.x, player.boatSprite.getY() + 600, 0);
        camera.update();
    }

    /**
     * Updates the GameData.standings array by comparing boats positions
     */
    private void updateStandings() {
        // If the player hasn't finished the race...
        if (!player.hasFinished()) {
            // Reset his position
            GameData.standings[0] = 1;

            // For every AI that is ahead, increment by 1
            for (Boat boat : opponents)
                if (boat.boatSprite.getY() + boat.boatSprite.getHeight() / 2 > player.boatSprite.getY() + player.boatSprite.getHeight() / 2) {
                    GameData.standings[0]++;
                }

        }

        // Iterate through all the AIs to update their standings too
        for (int i = 0; i < 3; i++)
            // If the AI hasn't finished the race...
            if (!opponents[i].hasFinished()) {
                // Reset his position
                GameData.standings[i + 1] = 1;

                // If the player is ahead, increment the standing by 1
                if (player.boatSprite.getY() > opponents[i].boatSprite.getY())
                    GameData.standings[i + 1]++;

                // For every other AI that is ahead, increment by 1
                for (int j = 0; j < 3; j++)
                    if (opponents[j].boatSprite.getY() > opponents[i].boatSprite.getY())
                        GameData.standings[i + 1]++;
            }
    }

    /**
     * Updates the GameData.results list by adding a new result every time a boat finishes the game
     */
    private void checkForResults() {
        // If the player has finished and we haven't added his result already...
        if (player.hasFinished() && player.acceleration > 0 && GameData.results.size() < 4) {
            // Add the result to the list with key 0, the player's lane
            GameData.results.add(new Float[]{0f, GameData.currentTimer});

            // Transition to the results UI
            GameData.showResultsState = true;
            GameData.currentUI = new ResultsUI();

            // Change the player's acceleration so the boat stops moving
            player.acceleration = -200f;
        }

        // Iterate through the AI to see if any of them finished the race
        for (int i = 0; i < 3; i++) {
            // If the AI has finished and we haven't added his result already...
            if (opponents[i].hasFinished() && opponents[i].acceleration > 0 && GameData.results.size() < 4) {
                // Add the result to the list with the his lane numer as key
                GameData.results.add(new Float[]{Float.valueOf(i + 1), GameData.currentTimer});

                // Change the AI's acceleration so the boat stops moving
                opponents[i].acceleration = -200f;
            }
        }
    }

     /**
     * This method marks all the boats that haven't finished the race as dnfs
     */
    private void dnfRemainingBoats() {
        // If the player hasn't finished
        if (!player.hasFinished() && player.robustness > 0 && GameData.results.size() < 4) {
            // Add a dnf result
            GameData.results.add(new Float[]{0f, Float.MAX_VALUE});

            // Transition to the showResult screen
            GameData.showResultsState = true;
            GameData.currentUI = new ResultsUI();
        }

        // Iterate through the AI and add a dnf result for any who haven't finished
        for (int i = 0; i < 3; i++) {
            if (!opponents[i].hasFinished() && opponents[i].robustness > 0 && GameData.results.size() < 4)
                GameData.results.add(new Float[]{Float.valueOf(i + 1), Float.MAX_VALUE});
        }
    }

    @Override
    public void render() {

        // Reset the screen
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // If the game is in one of the static state
        if (GameData.mainMenuState || GameData.choosingBoatState || GameData.GameOverState || GameData.optionsState ||
                GameData.infoState || GameData.chooseDifficultyState) {
            // Draw the UI and wait for the input
            GameData.currentUI.drawUI(UIbatch, mousePosition, Gdx.graphics.getWidth(), Gdx.graphics.getDeltaTime());
            GameData.currentUI.getInput(Gdx.graphics.getWidth(), clickPosition);

        }
        // Otherwise, if we are in the game play state
        else if (GameData.gamePlayState) {
            // Added code start
            if (isPaused) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    isPaused = false;
                }
                Gdx.gl.glClearColor(0, 0, 255, 0);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                float x = camera.position.x - PAUSED_BACKGROUND_WIDTH / 2;
                float y = camera.position.y - PAUSED_BACKGROUND_HEIGHT / 2;
                batch.begin();
                batch.draw(pausedBackground, x, y, PAUSED_BACKGROUND_WIDTH, PAUSED_BACKGROUND_HEIGHT);
                batch.draw(playButton, x * 2.2f, y + 300, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
                batch.draw(saveButton, x * 2.2f, y + 150, SAVE_BUTTON_WIDTH, SAVE_BUTTON_HEIGHT);
                batch.end();
                handlePausedInput(x, y);
            }
            else {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !GameData.showResultsState) {
                    isPaused = true;
                }
                // Added code end

                // If it's the first iteration in this state, the boats need to be created at their starting positions
                if (player == null) {
                    // Added code start
                    prefs = GameData.preferences;
                    // Check if there is any saved data
                    if (GameData.fromSave && prefs.contains("playerRobustness")) {
                        handleLoadingGame();
                    } else {
                        //Added code end
                        startLeg(GameData.currentLeg);
                        // Create the player boat
                        int playerBoatType = GameData.boatTypes[0];
                        player = new Player(GameData.boatsStats[playerBoatType][0], GameData.boatsStats[playerBoatType][1],
                                GameData.boatsStats[playerBoatType][2], GameData.boatsStats[playerBoatType][3],
                                playerBoatType, map[GameData.currentLeg].lanes[0]);
                        // Modified code start
                        player.createBoatBody(map[GameData.currentLeg].world, GameData.startingPoints[0][0], GameData.startingPoints[0][1], "Boat1.json");
                        // Modified code end
                        // Create the AI boats
                        for (int i = 1; i <= 3; i++) {
                            int AIBoatType = GameData.boatTypes[i];
                            opponents[i - 1] = new AI(GameData.boatsStats[AIBoatType][0], GameData.boatsStats[AIBoatType][1],
                                    GameData.boatsStats[AIBoatType][2], GameData.boatsStats[AIBoatType][3],
                                    AIBoatType, map[GameData.currentLeg].lanes[i]);
                            // Modified code start
                            opponents[i - 1].createBoatBody(map[GameData.currentLeg].world, GameData.startingPoints[i][0], GameData.startingPoints[i][1], "Boat1.json");
                            // Modified code end
                        }
                    }

                }
                //Step the world's physics, and also handle collisions and queues for collision actions.
                map[GameData.currentLeg].stepWorld(player, opponents);

                // Update the timer
                GameData.currentTimer += Gdx.graphics.getDeltaTime();

                // Update the player's and the AI's movement
                player.updatePlayer(pressedKeys, Gdx.graphics.getDeltaTime());
                for (AI opponent : opponents)
                    opponent.updateAI(Gdx.graphics.getDeltaTime());

                // Set the camera as the batches projection matrix
                batch.setProjectionMatrix(camera.combined);

                // Render the map
                map[GameData.currentLeg].renderMap(camera, batch);

                // Render the player and the AIs
                player.drawBoat(batch);
                for (AI opponent : opponents)
                    opponent.drawBoat(batch);

                // Render the objects that weren't destroyed yet
                for (Lane lane : map[GameData.currentLeg].lanes) {
                    for (Obstacle obstacle : lane.obstacles) {
                        if (obstacle.obstacleBody != null) {
                            obstacle.drawObstacle(batch);
                        }
                    }
                    // Added code start
                    for (PowerUp powerup : lane.powerUps) {
                        if (powerup.powerupBody != null) {
                            powerup.drawPowerUp(batch);
                        }
                    }
                    // Added code end
                }


                // Update the camera at the player's position
                updateCamera(player);

                //Modified code start
                map[GameData.currentLeg].updatePenalties(player, opponents);
                //Modified code end

                // Update the standings of each boat
                updateStandings();

                // If it's been 15 seconds since the winner completed the race, dnf all boats who haven't finished yet
                // Then transition to the result state
                if (GameData.results.size() > 0 && GameData.results.size() < 4 &&
                        GameData.currentTimer > GameData.results.get(0)[1] + 15f) {
                    dnfRemainingBoats();
                    GameData.showResultsState = true;
                    GameData.currentUI = new ResultsUI();
                }
                // Otherwise keep checking for new results
                else {
                    checkForResults();
                }


                // Choose which UI to display based on the current state
                if (!GameData.showResultsState)
                    GameData.currentUI.drawPlayerUI(UIbatch, player);
                else {
                    GameData.currentUI.drawUI(UIbatch, mousePosition, Gdx.graphics.getWidth(), Gdx.graphics.getDeltaTime());
                    GameData.currentUI.getInput(Gdx.graphics.getWidth(), clickPosition);
                }
            }

        }
        // Otherwise we need need to reset elements of the game to prepare for the next race
        else if (GameData.resetGameState) {
            player = null;
            for (int i = 0; i < 3; i++)
                opponents[i] = null;
            GameData.results.clear();
            GameData.currentTimer = 0f;
            GameData.penalties = new float[4];
            //Added code start
            GameData.timeReductions = new float[4];
            //Added code end

            // If we're coming from the result screen, then we need to advance to the next leg
            if (GameData.showResultsState) {
                GameData.currentLeg += 1;
                if (GameData.fromSave) {
                    prefs.putInteger("currentLeg", GameData.currentLeg);
                    GameData.fromSave = false;
                }

                GameData.showResultsState = false;
                GameData.gamePlayState = true;
                GameData.currentUI = new GamePlayUI();

            }
            // Otherwise we're coming from the endgame screen so we need to return to the main menu
            else {
                camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
                camera.update();
                // Reset everything for the next game
                // Modified code start
                map = new Map[3];
                // Modified code end

                GameData.currentLeg = 0;
                GameData.fromSave = false;
                GameData.mainMenuState = true;
                GameData.currentUI = new MenuUI();
            }
            GameData.resetGameState = false;

        }

        // If we haven't clicked anywhere in the last frame, reset the click position
        if (clickPosition.x != 0f && clickPosition.y != 0f)
            clickPosition.set(0f, 0f);
    }

    // Modified code start
    /**
     * Creates a Map for the leg specified, and updates the currentLeg.
     * Assumes that map[] has already been created.
     *
     * @param newLegNumber Leg to change map to.
     */
    public void startLeg(int newLegNumber){
        GameData.currentLeg = newLegNumber;
        // Initialize the map
        map[newLegNumber] = new Map("Map1/Map1.tmx", Gdx.graphics.getWidth(), GameData.gameDifficulty);

        // Modified code start
        // Create the lanes, and the obstacles in the physics game world
        map[newLegNumber].createLanes();

        // Create the finish line
        map[newLegNumber].createFinishLine("finishLine.png");
        // Modified code end
    }
    // Modified code end

    public void dispose() {
        // Modified code start
        map[GameData.currentLeg].world.dispose();
        // Modified code end
    }

    @Override
    public boolean keyDown(int keycode) {
        if (GameData.switchControls) {
            if (keycode == Input.Keys.UP)
                pressedKeys[0] = true;
            if (keycode == Input.Keys.LEFT)
                pressedKeys[1] = true;
            if (keycode == Input.Keys.DOWN)
                pressedKeys[2] = true;
            if (keycode == Input.Keys.RIGHT)
                pressedKeys[3] = true;
            // Added code start
        } else {
            if (keycode == Input.Keys.W)
                pressedKeys[0] = true;
            if (keycode == Input.Keys.A)
                pressedKeys[1] = true;
            if (keycode == Input.Keys.S)
                pressedKeys[2] = true;
            if (keycode == Input.Keys.D)
                pressedKeys[3] = true;
            // Added code end
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (GameData.switchControls) {
            if (keycode == Input.Keys.UP)
                pressedKeys[0] = false;
            if (keycode == Input.Keys.LEFT)
                pressedKeys[1] = false;
            if (keycode == Input.Keys.DOWN)
                pressedKeys[2] = false;
            if (keycode == Input.Keys.RIGHT)
                pressedKeys[3] = false;
            //Added code start
        } else {
            if (keycode == Input.Keys.W)
                pressedKeys[0] = false;
            if (keycode == Input.Keys.A)
                pressedKeys[1] = false;
            if (keycode == Input.Keys.S)
                pressedKeys[2] = false;
            if (keycode == Input.Keys.D)
                pressedKeys[3] = false;
            //Added code end
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 position = camera.unproject(new Vector3(screenX, screenY, 0));
        clickPosition.set(position.x, position.y);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 position = camera.unproject(new Vector3(screenX, screenY, 0));
        mousePosition.set(position.x, position.y);
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void resize(int width, int height) {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    // Added code start

    /**
     * @param x background button x coordinate
     * @param y background button y coordinate
     */
    public void handlePausedInput(float x, float y) {
        float actualX = x * 2.2f;
        if (
                clickPosition.x < actualX + PLAY_BUTTON_HEIGHT && clickPosition.x > actualX &&
                        clickPosition.y < y + 300 + PLAY_BUTTON_HEIGHT &&
                        clickPosition.y > y + 300
        ) {
            isPaused = false;
        } else if (
                clickPosition.x < actualX + SAVE_BUTTON_HEIGHT && clickPosition.x > actualX &&
                        clickPosition.y < y + 150 + SAVE_BUTTON_HEIGHT &&
                        clickPosition.y > y + 150
        ) {
            saveGame();
            isPaused = false;
            GameData.gamePlayState = false;
            GameData.resetGameState = true;
            GameData.currentUI = new MenuUI();
        }
    }

    public void saveGame() {
        prefs = GameData.preferences;
        prefs.clear();
        // Saving players data
        prefs.putFloat("playerRobustness", player.robustness);
        prefs.putFloat("playerSpeed", player.speed);
        prefs.putFloat("playerAcceleration", player.acceleration);
        prefs.putFloat("playerManeuverability", player.maneuverability);
        prefs.putInteger("playerBoatType", player.boatType);
        prefs.putFloat("playerCurrentSpeed", player.current_speed);
        prefs.putFloat("playerStamina", player.stamina);
        Vector2 playerPosition = player.boatBody.getPosition();
        prefs.putFloat("playerPosX", playerPosition.x);
        prefs.putFloat("playerPosY", playerPosition.y);
        prefs.putFloat("playerAngle", (float) Math.toDegrees(player.boatBody.getAngle()));
        prefs.putInteger("playerStandings", GameData.standings[0]);
        prefs.putFloat("playerPenalties", GameData.penalties[0]);
        prefs.putFloat("playerTimeReductions", GameData.timeReductions[0]);

        // Saving AI data
        for (int i = 0; i != opponents.length; i++) {
            prefs.putFloat("opponent" + i + "Robustness", opponents[i].robustness);
            prefs.putFloat("opponent" + i + "Speed", opponents[i].speed);
            prefs.putFloat("opponent" + i + "Acceleration", opponents[i].acceleration);
            prefs.putFloat("opponent" + i + "Maneuverability", opponents[i].maneuverability);
            prefs.putInteger("opponent" + i + "BoatType", opponents[i].boatType);
            prefs.putFloat("opponent" + i + "CurrentSpeed", opponents[i].current_speed);
            prefs.putFloat("opponent" + i + "Stamina", opponents[i].stamina);
            Vector2 opponentPosition = opponents[i].boatBody.getPosition();
            prefs.putFloat("opponent" + i + "PosX", opponentPosition.x);
            prefs.putFloat("opponent" + i + "PosY", opponentPosition.y);
            prefs.putFloat("opponent" + i + "Angle", (float) Math.toDegrees(opponents[i].boatBody.getAngle()));
            prefs.putFloat("opponent" + i + "Penalties", GameData.penalties[i + 1]);
            prefs.putFloat("opponent" + i + "TimeReductions", GameData.timeReductions[i + 1]);
        }

        // Saving map data
        int i = GameData.currentLeg;
        Lane lanes[] = map[i].lanes;
        // Loop to save all lane data
        for (int j = 0; j != lanes.length; j++) {
            Obstacle obstacles[] = lanes[j].obstacles;
            // Loop to save all the obstacles in a lane
            for (int k = 0; k != obstacles.length; k++) {
                prefs.putString("Map" + i + "Lane" + j + "Obstacle" + k + "TextureName", obstacles[k].textureName);
                prefs.putFloat("Map" + i + "Lane" + j + "Obstacle" + k + "Scale", obstacles[k].scale);
                prefs.putString("Map" + i + "Lane" + j + "Obstacle" + k + "BodyFile", obstacles[k].bodyFile);
                prefs.putFloat("Map" + i + "Lane" + j + "Obstacle" + k + "PosX", obstacles[k].posX);
                prefs.putFloat("Map" + i + "Lane" + j + "Obstacle" + k + "PosY", obstacles[k].posY);
            }
            PowerUp powerUps[] = lanes[j].powerUps;
            // Loop to save all the powerUps in a lane
            for (int l = 0; l != powerUps.length; l++) {
                prefs.putString("Map" + i + "Lane" + j + "PowerUp" + l + "TextureName", powerUps[l].textureName);
                prefs.putString("Map" + i + "Lane" + j + "PowerUp" + l + "PowerUpName", powerUps[l].powerupName);
                prefs.putFloat("Map" + i + "Lane" + j + "PowerUp" + l + "Scale", powerUps[l].scale);
                prefs.putString("Map" + i + "Lane" + j + "PowerUp" + l + "BodyFile", powerUps[l].bodyFile);
                prefs.putFloat("Map" + i + "Lane" + j + "PowerUp" + l + "PosX", powerUps[l].posX);
                prefs.putFloat("Map" + i + "Lane" + j + "PowerUp" + l + "PosY", powerUps[l].posY);
            }
            prefs.putInteger("noObstacles" + j, obstacles.length);
            prefs.putInteger("noPowerUps" + j, powerUps.length);
        }
        prefs.putString("gameDifficulty", map[0].gameDifficulty);
        // Saving camera data
        Vector3 cameraPosition = camera.position;
        prefs.putFloat("cameraPosX", camera.position.x);
        prefs.putFloat("cameraPosY", cameraPosition.y);
        prefs.putInteger("currentLeg", GameData.currentLeg);
        prefs.putFloat("currentTime", GameData.currentTimer);
        GameData.fromSave = false;


        prefs.flush();
    }

    /**
     * Helper function that calls other methods to load all the data from load file
     */
    public void handleLoadingGame() {
        GameData.currentLeg = prefs.getInteger("currentLeg");
        GameData.currentTimer = prefs.getFloat("currentTime");
        updateBoatTypeFromLoad();
        updateTimeReductionFromLoad();
        updatePenaltiesFromLoad();
        createMapFromLoad();
        createPlayerFromLoad();
        createOpponentsFromLoad();
        updateCamera(player);
    }

    /**
     *
     */
    public void updateBoatTypeFromLoad() {
        GameData.boatTypes[0] = prefs.getInteger("playerBoatType");
        GameData.boatTypes[1] = prefs.getInteger("opponent0BoatType");
        GameData.boatTypes[2] = prefs.getInteger("opponent1BoatType");
        GameData.boatTypes[3] = prefs.getInteger("opponent2BoatType");
    }

    /**
     * Assigns penalties from save file to global variable
     */
    public void updatePenaltiesFromLoad() {
        GameData.penalties[0] = prefs.getFloat("playerPenalties");
        GameData.penalties[1] = prefs.getFloat("opponent0Penalties");
        GameData.penalties[2] = prefs.getFloat("opponent1Penalties");
        GameData.penalties[3] = prefs.getFloat("opponent2Penalties");

    }

    /**
     * Assigns time reductions from save file to global variable
     */
    public void updateTimeReductionFromLoad() {
        GameData.timeReductions[0] = prefs.getFloat("playerTimeReductions");
        GameData.timeReductions[1] = prefs.getFloat("opponent0TimeReductions");
        GameData.timeReductions[2] = prefs.getFloat("opponent1TimeReductions");
        GameData.timeReductions[3] = prefs.getFloat("opponent2TimeReductions");
    }

    /**
     * Method for creating a map from a load file
     */
    public void createMapFromLoad() {
        map = new Map[3];
        float width = Gdx.graphics.getWidth();
        int i = GameData.currentLeg;
        // Initialize the map
        map[i] = new Map("Map1/Map1.tmx", width, prefs.getString("gameDifficulty"));

        // Create the lanes, and the obstacles in the physics game world
        map[i].createLanesFromLoad(i);

        // Create the finish line
        map[i].createFinishLine("finishLine.png");
    }

    /**
     * Method to load player data from load file
     */
    public void createPlayerFromLoad() {
        float pRobustness = prefs.getFloat("playerRobustness");
        float pSpeed = prefs.getFloat("playerSpeed");
        float pAcceleration = prefs.getFloat("playerAcceleration");
        float pManeuverability = prefs.getFloat("playerManeuverability");
        int pBoatType = prefs.getInteger("playerBoatType");
        float pCurrentSpeed = prefs.getFloat("playerCurrentSpeed");
        float pStamina = prefs.getFloat("playerStamina");
        float pPosX = prefs.getFloat("playerPosX");
        float pPosY = prefs.getFloat("playerPosY");
        float pAngle = prefs.getFloat("playerAngle");
        int currentLeg = prefs.getInteger("currentLeg");
        player = new Player(pRobustness, pSpeed, pAcceleration, pManeuverability, pBoatType, map[currentLeg].lanes[0], pCurrentSpeed, pStamina);
        player.createBoatBody(map[currentLeg].world, pPosX, pPosY, "Boat1.json");
        player.boatSprite.setRotation(pAngle);
    }

    /**
     * Method to load all opponents data from load file
     */
    public void createOpponentsFromLoad() {
        for (int i = 0; i != 3; i++) {
            float oRobustness = prefs.getFloat("opponent" + i + "Robustness");
            float oSpeed = prefs.getFloat("opponent" + i + "Speed");
            float oAcceleration = prefs.getFloat("opponent" + i + "Acceleration");
            float oManeuverability = prefs.getFloat("opponent" + i + "Maneuverability");
            int oBoatType = prefs.getInteger("opponent" + i + "BoatType");
            float oCurrentSpeed = prefs.getFloat("opponent" + i + "CurrentSpeed");
            float oStamina = prefs.getFloat("opponent" + i + "Stamina");
            float oPosX = prefs.getFloat("opponent" + i + "PosX");
            float oPosY = prefs.getFloat("opponent" + i + "PosY");
            float oAngle = prefs.getFloat("opponent" + i + "Angle");
            int currentLeg = prefs.getInteger("currentLeg");
            opponents[i] = new AI(oRobustness, oSpeed, oAcceleration, oManeuverability, oBoatType, map[currentLeg].lanes[i + 1], oCurrentSpeed, oStamina);
            opponents[i].createBoatBody(map[currentLeg].world, oPosX, oPosY, "Boat1.json");
            opponents[i].boatSprite.setRotation(oAngle);
        }
    }
    // Added code end
}
