package com.hardgforgif.dragonboatracing.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.hardgforgif.dragonboatracing.GameData;
import com.hardgforgif.dragonboatracing.core.AI;
import com.hardgforgif.dragonboatracing.core.Player;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * A base class which all tests inherit from.
 * It ensures a consistent starting point by temporarily resetting the preferences.
 * It also includes some helper functions for doing common actions, like replacing preferences with a custom file.
 */
public class TestBase {

    private Map oldPreferences;

    /**
     * Stores and clears the preferences, so the testing environment is unaffected by whatever preferences are currently
     * set on the testing machine.
     *
     * Also spoofs some gdx functions to give consistent results.
     */
    @Before
    public void before(){
        oldPreferences = GameData.preferences.get();
        GameData.preferences.clear();
        GameData.preferences.flush();

        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl30 = Mockito.mock(GL30.class);
        Gdx.graphics = Mockito.mock(Graphics.class);
        when(Gdx.graphics.getDeltaTime()).thenReturn(1/60f);
    }

    /**
     * Takes the location of an alternative preferences file, and reads it into preferences.
     * File must be in the same format as a real preferences file, and stored in tests/preferences.
     */
    public void setPreferences(String filepath) throws FileNotFoundException {
        GameData.preferences.clear();
        File file = new File("./preferences/" + filepath);
        Scanner myReader = new Scanner(file);
        while (myReader.hasNextLine()) {
            String line = myReader.nextLine();
            /*
            All the lines we want to read take the form:
            <entry key="Key">Value</entry>
            Any line that does not match this is ignored.
             */
            if (line.startsWith("<entry key=\"")) {
                String key = line.substring(12, line.substring(12).indexOf("\"")+12);
                String value = line.substring(line.indexOf(">")+1, line.substring(1).indexOf("<")+1);
                /*
                We store three types of values:
                Integer, Float and String
                Type data isn't stored in the preferences file so we need to infer it.
                 */
                if (value.matches("-?\\d+(\\.\\d+)?")) {
                    //Numeric
                    if (value.matches("-?\\d+")) {
                        //Integer
                        GameData.preferences.putInteger(key, Integer.parseInt(value));
                    }
                    else {
                        //Float
                        GameData.preferences.putFloat(key, Float.parseFloat(value));
                    }
                }
                else {
                    //String
                    GameData.preferences.putString(key, value);
                }
            }
        }
        myReader.close();
        GameData.preferences.flush();
    }

    /*
    Takes a filepath to load from.
    Returns an array of object containing (in order) a player, an array of AI opponents, and a map, loaded from the save.
    Fails the test if the filepath doesn't lead to the correct location.
     */
    public Object[] loadSave(String filepath) {
        try {
            setPreferences(filepath);
        }
        catch (FileNotFoundException e) {
            assertTrue(false);
        }
        com.hardgforgif.dragonboatracing.core.Map map = new com.hardgforgif.dragonboatracing.core.Map("Map1/Map1.tmx", 1280f, GameData.preferences.getString("gameDifficulty"));
        map.createLanesFromLoad(0);

        Player player = new Player(GameData.preferences.getFloat("playerRobustness"),
                GameData.preferences.getFloat("playerSpeed"),
                GameData.preferences.getFloat("playerAcceleration"),
                GameData.preferences.getFloat("playerManeuverability"),
                GameData.preferences.getInteger("playerBoatType"),
                map.lanes[0],
                GameData.preferences.getFloat("playerCurrentSpeed"),
                GameData.preferences.getFloat("playerStamina"));
        player.createBoatBody(map.world,
                GameData.preferences.getFloat("playerPosX"),
                GameData.preferences.getFloat("playerPosY"),
                "Boat1.json");
        player.boatSprite.setRotation(GameData.preferences.getFloat("playerAngle"));

        AI[] opponents = new AI[3];
        for (int i = 0; i != 3; i++) {
            float oRobustness = GameData.preferences.getFloat("opponent" + i + "Robustness");
            float oSpeed = GameData.preferences.getFloat("opponent" + i + "Speed");
            float oAcceleration = GameData.preferences.getFloat("opponent" + i + "Acceleration");
            float oManeuverability = GameData.preferences.getFloat("opponent" + i + "Maneuverability");
            int oBoatType = GameData.preferences.getInteger("opponent" + i + "BoatType");
            float oCurrentSpeed = GameData.preferences.getFloat("opponent" + i + "CurrentSpeed");
            float oStamina = GameData.preferences.getFloat("opponent" + i + "Stamina");
            float oPosX = GameData.preferences.getFloat("opponent" + i + "PosX");
            float oPosY = GameData.preferences.getFloat("opponent" + i + "PosY");
            float oAngle = GameData.preferences.getFloat("opponent" + i + "Angle");
            opponents[i] = new AI(oRobustness, oSpeed, oAcceleration, oManeuverability, oBoatType, map.lanes[i + 1], oCurrentSpeed, oStamina);
            opponents[i].createBoatBody(map.world, oPosX, oPosY, "Boat1.json");
            opponents[i].boatSprite.setRotation(oAngle);
        }
        Object[] returnObjects = new Object[3];
        returnObjects[0] = map;
        returnObjects[1] = player;
        returnObjects[2] = opponents;
        return returnObjects;
    }

    /**
     * Restores the user's preferences after the test is complete.
     */
    @After
    public void after(){
        GameData.preferences.clear();
        GameData.preferences.put(oldPreferences);
        GameData.preferences.flush();
    }
}
