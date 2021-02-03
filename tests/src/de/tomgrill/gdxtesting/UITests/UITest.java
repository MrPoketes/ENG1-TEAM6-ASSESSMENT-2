package de.tomgrill.gdxtesting.UITests;

import com.badlogic.gdx.math.Vector2;
import com.hardgforgif.dragonboatracing.GameData;
import com.hardgforgif.dragonboatracing.UI.ChooseDifficultyUI;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UITest {


    @Test
    //Note, currently only tests easy button.
    public void ClickingDifficultyButtonsWorks() {
        /*
        Directly set the gameDifficulty in GameData to something like "test".
        Create a ChooseDifficultyUI, and getInput() one of the buttons.
        Check that the difficulty in GameData has been set to the correct value.
        Repeat for all four other buttons.
         */
        int SCREEN_WIDTH = 1000;
        int BUTTON_WIDTH = 200;
        int BUTTON_Y = 300;
        int BUTTON_HEIGHT = 100;

        GameData.gameDifficulty = "unchanged";
        ChooseDifficultyUI testUI = new ChooseDifficultyUI();
        //Create a click position at a point within the button's area (in this case, exactly central).
        Vector2 clickPosition = new Vector2(SCREEN_WIDTH/4, BUTTON_Y + BUTTON_HEIGHT/2);
        testUI.getInput(SCREEN_WIDTH, clickPosition);
        //test that clickPosition is within area
        assertTrue(clickPosition.x < SCREEN_WIDTH / 4 - BUTTON_WIDTH / 2 + BUTTON_WIDTH);
        assertTrue(clickPosition.x > SCREEN_WIDTH / 4 - BUTTON_WIDTH / 2);
        assertTrue(clickPosition.y < BUTTON_Y + BUTTON_HEIGHT);
        assertTrue(clickPosition.y > BUTTON_Y);
        //test difficulty was changed
        assertEquals("easy", GameData.gameDifficulty);

    }

}