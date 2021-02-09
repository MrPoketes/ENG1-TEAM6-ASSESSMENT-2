package com.hardgforgif.dragonboatracing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.hardgforgif.dragonboatracing.UI.MenuUI;
import com.hardgforgif.dragonboatracing.UI.UI;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameData {
    // Set the rations between the pixels, meters and tiles
    public final static float METERS_TO_PIXELS = 100f;
    // Create the game state variables
    public static boolean mainMenuState = true;
    // Added code start
    public static boolean optionsState = false;
    public static boolean infoState = false;
    public static boolean chooseDifficultyState = false;
    //Added code end
    public static boolean choosingBoatState = false;
    public static boolean gamePlayState = false;
    public static boolean showResultsState = false;
    public static boolean resetGameState = false;
    public static boolean GameOverState = false;
    // Create the game UI and the game music
    public static UI currentUI = new MenuUI();
    public static Music music = Gdx.audio.newMusic(Gdx.files.internal("Vibing.ogg"));
    public static float TILES_TO_METERS;
    public static float PIXELS_TO_TILES;
    // Create a list of possible boat stats
    // Ordered by: robustness, speed, acceleration, maneuverability
    public static float[][] boatsStats = new float[][]{{120, 110, 100, 80}, {55, 110, 130, 60},
            {90, 110, 100, 130}, {65, 120, 90, 55}};
    /*
    //Added code start
    //Comment the lines above and uncomment the lines below to get super fast boats for testing:
    //The red one is fastest!
    public static float[][] boatsStats = new float[][]{{10000, 2000, 2000, 80}, {10000, 1000, 1000, 60},
            {10000, 1000, 1000, 130}, {10000, 1000, 1000, 55}};
     */
    //Added code end
    // Store information about each lane's boat
    // Boat's starting location
    public static float[][] startingPoints = new float[][]{{2.3f, 4f}, {4f, 4f}, {7f, 4f}, {10f, 4f}};
    // Boat's type
    public static int[] boatTypes = new int[4];
    // Boat's standing
    public static int[] standings = new int[4];
    // Boat's penalties
    public static float[] penalties = new float[4];
    // Result of the boat as a Pair<lane number, result>
    public static List<Float[]> results = new ArrayList<>();
    // Current leg and the current timer in the leg
    public static int currentLeg = 0;
    public static float currentTimer = 0f;
    // Difficulty constants for the AI
    public static float[] difficulty = new float[]{0.92f, 0.97f, 1f};

    // Added code start
    // Music volume range [0,1]
    public static float musicVolume = 0f;
    // Variable for reducing time, if boat picked up time reduction powerUp
    public static float[] timeReductions = {0.0f, 0.0f, 0.0f, 0.0f};
    // Variable
    public static Stack<Integer> reduceAIAccelerationList = new Stack();
    // Sets the game difficulty. Available types - easy, medium, hard
    public static String gameDifficulty = "easy";
    public static boolean switchControls = false;
    public static boolean fromSave = false;

    public static Preferences preferences = Gdx.app.getPreferences("savedData");
    // Added code end
}
