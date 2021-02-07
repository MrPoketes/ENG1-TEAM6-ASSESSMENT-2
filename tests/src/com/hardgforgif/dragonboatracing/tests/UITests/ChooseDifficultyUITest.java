package com.hardgforgif.dragonboatracing.tests.UITests;

import com.badlogic.gdx.math.Vector2;
import com.hardgforgif.dragonboatracing.GameData;
import com.hardgforgif.dragonboatracing.UI.ChooseDifficultyUI;
import com.hardgforgif.dragonboatracing.UI.ChoosingUI;
import com.hardgforgif.dragonboatracing.UI.GamePlayUI;
import com.hardgforgif.dragonboatracing.tests.GdxTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(GdxTestRunner.class)
public class ChooseDifficultyUITest {

    //constants regarding the button positioning
    int SCREEN_WIDTH = 1000;
    int BUTTON_WIDTH = 200;
    int BUTTON_Y = 300;
    int BUTTON_HEIGHT = 100;

    private void clickButton(ChooseDifficultyUI testUI, int buttonNumber, String newDifficulty){
        //Ensure that the difficulty is not already the difficulty given.
        assertNotEquals(newDifficulty, GameData.gameDifficulty);
        //Create a click position at a point within the button's area (in this case, exactly central).
        Vector2 clickPosition = new Vector2(buttonNumber * SCREEN_WIDTH/4, BUTTON_Y + BUTTON_HEIGHT/2);
        testUI.getInput(SCREEN_WIDTH, clickPosition);
        //test that clickPosition is within area
        assertTrue(clickPosition.x < buttonNumber * SCREEN_WIDTH / 4 - BUTTON_WIDTH / 2 + BUTTON_WIDTH);
        assertTrue(clickPosition.x > buttonNumber * SCREEN_WIDTH / 4 - BUTTON_WIDTH / 2);
        assertTrue(clickPosition.y < BUTTON_Y + BUTTON_HEIGHT);
        assertTrue(clickPosition.y > BUTTON_Y);
        //Test that the difficulty has been correctly changed.
        assertEquals(newDifficulty, GameData.gameDifficulty);
    }

    @Test
    public void clickingDifficultyButtonsWorks() {
        /*
        Directly set the gameDifficulty in GameData to something like "test".
        Create a ChooseDifficultyUI, and getInput() one of the buttons.
        Check that the difficulty in GameData has been set to the correct value.
        Repeat for all other buttons.
         */
        GameData.gameDifficulty = "unchanged";
        ChooseDifficultyUI testUI = new ChooseDifficultyUI();
        clickButton(testUI, 1, "easy");
        clickButton(testUI, 2, "medium");
        clickButton(testUI, 3, "hard");
    }

    @Test
    public void clickingPlayButtonWorks() {
        /*
        Create a ChooseDifficultyUI.
        getInput() the play button.
        Check that the game's UI and state has been changed.
         */
        GameData.mainMenuState = false;
        GameData.chooseDifficultyState = true;
        ChooseDifficultyUI testUI = new ChooseDifficultyUI();
        GameData.currentUI = testUI;

        assertFalse(GameData.currentUI.getClass() == ChoosingUI.class);
        assertFalse(GameData.chooseDifficultyState == false);
        assertFalse(GameData.choosingBoatState == true);
        //Create a click position at a point within the button's area (in this case, exactly central).
        Vector2 clickPosition = new Vector2(SCREEN_WIDTH/2, 100 + BUTTON_HEIGHT/2);
        testUI.getInput(SCREEN_WIDTH, clickPosition);
        //test that clickPosition is within area
        assertTrue(clickPosition.x < SCREEN_WIDTH / 2 - BUTTON_WIDTH / 2 + BUTTON_WIDTH);
        assertTrue(clickPosition.x > SCREEN_WIDTH / 2 - BUTTON_WIDTH / 2);
        assertTrue(clickPosition.y < 100 + BUTTON_HEIGHT);
        assertTrue(clickPosition.y > 100);
        //Check changes have been made.
        assertTrue(GameData.currentUI.getClass() == ChoosingUI.class);
        assertTrue(GameData.chooseDifficultyState == false);
        assertTrue(GameData.choosingBoatState == true);
    }

}