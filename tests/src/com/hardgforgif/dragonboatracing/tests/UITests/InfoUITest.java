package com.hardgforgif.dragonboatracing.tests.UITests;

import com.badlogic.gdx.math.Vector2;
import com.hardgforgif.dragonboatracing.GameData;
import com.hardgforgif.dragonboatracing.UI.ChooseDifficultyUI;
import com.hardgforgif.dragonboatracing.UI.ChoosingUI;
import com.hardgforgif.dragonboatracing.UI.InfoUI;
import com.hardgforgif.dragonboatracing.UI.MenuUI;
import com.hardgforgif.dragonboatracing.tests.GdxTestRunner;
import com.hardgforgif.dragonboatracing.tests.TestBase;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(GdxTestRunner.class)
public class InfoUITest extends TestBase {

    //constants regarding the button positioning
    int SCREEN_WIDTH = 1000;
    int BUTTON_Y = 50;
    int BUTTON_HEIGHT = 100;

    @Test
    public void TEST_INSTRUCTIONS_GOTO_MAIN_MENU() {
        /*
        Create a InfoUI.
        getInput() the back button.
        Check that the game's UI and state has been changed.
         */
        GameData.mainMenuState = false;
        GameData.infoState = true;
        InfoUI testUI = new InfoUI();
        GameData.currentUI = testUI;

        Vector2 clickPosition = new Vector2(7*SCREEN_WIDTH/8, BUTTON_Y + BUTTON_HEIGHT/2);
        testUI.getInput(SCREEN_WIDTH, clickPosition);
        //Check changes have been made.
        assertTrue(GameData.currentUI.getClass() == MenuUI.class);
        assertTrue(GameData.infoState == false);
        assertTrue(GameData.mainMenuState == true);
    }
}
