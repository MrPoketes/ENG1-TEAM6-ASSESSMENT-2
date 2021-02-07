package com.hardgforgif.dragonboatracing.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
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

import static org.mockito.Mockito.when;

/**
 * A base class which all tests inherit from.
 * It ensures a consistent starting point by temporarily resetting the preferences.
 * It also includes some helper functions for doing common actions, like replacing preferences with a custom file.
 */
public class TestBase {

    public Preferences preferences = Gdx.app.getPreferences("savedData");
    private Map oldPreferences;

    /**
     * Stores and clears the preferences, so the testing environment is unaffected by whatever preferences are currently
     * set on the testing machine.
     *
     * Also spoofs some gdx functions to give consistent results.
     */
    @Before
    public void before(){
        oldPreferences = preferences.get();
        preferences.clear();
        preferences.flush();

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
        preferences.clear();
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
                        preferences.putInteger(key, Integer.parseInt(value));
                    }
                    else {
                        //Float
                        preferences.putFloat(key, Float.parseFloat(value));
                    }
                }
                else {
                    //String
                    preferences.putString(key, value);
                }
            }
        }
        myReader.close();
        preferences.flush();
    }

    /**
     * Restores the user's preferences after the test is complete.
     */
    @After
    public void after(){
        preferences.clear();
        preferences.put(oldPreferences);
        preferences.flush();
    }
}
