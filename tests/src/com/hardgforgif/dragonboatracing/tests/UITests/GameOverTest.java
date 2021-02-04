package com.hardgforgif.dragonboatracing.tests.UITests;

import com.badlogic.gdx.math.Vector2;
import com.hardgforgif.dragonboatracing.GameData;
import com.hardgforgif.dragonboatracing.UI.ChooseDifficultyUI;
import com.hardgforgif.dragonboatracing.UI.GameOverUI;
import com.hardgforgif.dragonboatracing.tests.GdxTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(GdxTestRunner.class)
public class GameOverTest {

    @Test
    public void clickingExitsUI() {
        /*
        Directly set the gameDifficulty in GameData to something like "test".
        Create a ChooseDifficultyUI, and getInput() one of the buttons.
        Check that the difficulty in GameData has been set to the correct value.
        Repeat for all other buttons.
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