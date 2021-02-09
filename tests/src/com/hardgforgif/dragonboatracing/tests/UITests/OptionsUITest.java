package com.hardgforgif.dragonboatracing.tests.UITests;

import com.badlogic.gdx.math.Vector2;
import com.hardgforgif.dragonboatracing.GameData;
import com.hardgforgif.dragonboatracing.UI.OptionsUI;
import com.hardgforgif.dragonboatracing.tests.GdxTestRunner;
import com.hardgforgif.dragonboatracing.tests.TestBase;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(GdxTestRunner.class)
public class OptionsUITest extends TestBase {

    //constants regarding the button positioning
    int SCREEN_WIDTH = 1000;

    int MUSIC_BUTTON_HEIGHT = 50;
    int MUSIC_BUTTON_Y = 550;

    int CONTROLS_BUTTON_HEIGHT = 50;
    int CONTROLS_BUTTON_Y = 450;

    @Test
    public void TEST_UR_CHANGE_SETTINGS() {
        /*
        Test that increasing the music works, and can't go above 100%.
        Check that decreasing the music works, and can't go below 0% (must be exactly 0%).
        Check that WASD and Arrow Keys buttons work.
         */
        OptionsUI testUI = new OptionsUI();

        Vector2 clickPosition = new Vector2(3 * SCREEN_WIDTH / 5, MUSIC_BUTTON_Y + MUSIC_BUTTON_HEIGHT/2);
        GameData.musicVolume = 0.9f;
        testUI.getInput(SCREEN_WIDTH, clickPosition);
        assertEquals(1f, GameData.musicVolume, 0.01);
        testUI.getInput(SCREEN_WIDTH, clickPosition);
        assertEquals(1f, GameData.musicVolume, 0.01);

        clickPosition = new Vector2(2 * SCREEN_WIDTH / 5, MUSIC_BUTTON_Y + MUSIC_BUTTON_HEIGHT/2);
        GameData.musicVolume = 0.1f;
        testUI.getInput(SCREEN_WIDTH, clickPosition);
        assertEquals(0f, GameData.musicVolume, 0.01);
        testUI.getInput(SCREEN_WIDTH, clickPosition);
        assertEquals(0f, GameData.musicVolume, 0f);

        clickPosition = new Vector2(2 * SCREEN_WIDTH / 5, CONTROLS_BUTTON_Y + CONTROLS_BUTTON_HEIGHT/2);
        GameData.switchControls = true;
        testUI.getInput(SCREEN_WIDTH, clickPosition);
        assertFalse(GameData.switchControls);
        clickPosition = new Vector2(3 * SCREEN_WIDTH / 5, CONTROLS_BUTTON_Y + CONTROLS_BUTTON_HEIGHT/2);
        testUI.getInput(SCREEN_WIDTH, clickPosition);
        assertTrue(GameData.switchControls);
    }

}
