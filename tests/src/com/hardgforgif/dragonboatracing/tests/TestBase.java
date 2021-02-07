package com.hardgforgif.dragonboatracing.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * A base class which all tests inherit from.
 * It ensures a consistent starting point by temporarily resetting the preferences.
 * It also includes some helper functions for doing common actions, like replacing preferences with a custom file.
 */
public class TestBase {

    private Preferences preferences = Gdx.app.getPreferences("savedData");
    private Map oldPreferences;

    /**
     * Stores and clears the preferences, so the testing environment is unaffected by whatever preferences are currently
     * set on the testing machine.
     */
    @Before
    public void before(){
        oldPreferences = preferences.get();
        preferences.clear();
        preferences.flush();
    }

    /**
     * Takes the location of an alternative preferences file, and reads it into preferences.
     * File must be in the same format as a real preferences file, and stored in tests/preferences.
     */
    public void setPreferences(String filepath) throws FileNotFoundException {
        preferences.clear();
        File myObj = new File("tests/preferences/" + filepath);
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
            String line = myReader.nextLine();
            /*
            All the lines we want to read take the form:
            <entry key="Key">Value</entry>
            Any line that does not match this is ignored.
             */
            if (line.startsWith("<entry key=\"")) {
                String key = line.substring(11, line.substring(12).indexOf("\""));
                String value = line.substring(line.indexOf(">"), line.substring(1).indexOf("<"));
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
