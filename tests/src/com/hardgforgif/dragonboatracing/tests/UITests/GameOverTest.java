package com.hardgforgif.dragonboatracing.tests.UITests;

import com.badlogic.gdx.math.Vector2;
import com.hardgforgif.dragonboatracing.GameData;
import com.hardgforgif.dragonboatracing.UI.ChooseDifficultyUI;
import com.hardgforgif.dragonboatracing.UI.GameOverUI;
import com.hardgforgif.dragonboatracing.tests.GdxTestRunner;
import com.hardgforgif.dragonboatracing.tests.TestBase;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(GdxTestRunner.class)
public class GameOverTest extends TestBase {

    @Test
    public void TEST_GOTO_MAIN_MENU_UI() {
        /*
        Set the state to GameOver, and create a GameOverUI.
        Click anywhere on the screen.
        Test that the game's state has been marked to be reset.
         */
        GameData.mainMenuState = false;
        GameData.GameOverState = true;
        GameOverUI testUI = new GameOverUI();
        GameData.currentUI = testUI;

        assertTrue(GameData.GameOverState);
        assertFalse(GameData.resetGameState);

        Vector2 clickPosition = new Vector2(1f,1f);
        testUI.getInput(1000, clickPosition);

        assertFalse(GameData.GameOverState);
        assertTrue(GameData.resetGameState);
    }

}