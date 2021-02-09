package com.hardgforgif.dragonboatracing.tests.UITests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.hardgforgif.dragonboatracing.GameData;
import com.hardgforgif.dragonboatracing.UI.*;
import com.hardgforgif.dragonboatracing.tests.GdxTestRunner;
import com.hardgforgif.dragonboatracing.tests.TestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(GdxTestRunner.class)
public class MenuUITest extends TestBase {

    //constants regarding the button positioning
    int SCREEN_WIDTH = 1000;
    int BUTTON_HEIGHT = 120;

    @Test
    public void TEST_UR_MENU_WITH_SAVE() {
        /*
        Load a save file.
        Test the play button, the continue button, the info button and the options button.
         */
        try {
            setPreferences("unmodifiedSave");
        }
        catch (FileNotFoundException e) {
            assertTrue(false);
        }
        clickPlay(new Vector2(SCREEN_WIDTH/2, 300 + BUTTON_HEIGHT/2));
        clickContinue(new Vector2(SCREEN_WIDTH/2, 170 + BUTTON_HEIGHT/2));
        // Currently can't be tested.
        //clickExit(new Vector2(SCREEN_WIDTH/2, 50 + BUTTON_HEIGHT/2));
        clickInfo(new Vector2(SCREEN_WIDTH/8, 10 + BUTTON_HEIGHT/2));
        clickOptions(new Vector2(7*SCREEN_WIDTH/8, 10 + BUTTON_HEIGHT/2));
    }

    @Test
    public void TEST_UR_MENU_WITHOUT_SAVE() {
        /*
        Don't load a save file.
        Test the play button, the continue button, the info button and the options button.
         */
        clickPlay(new Vector2(SCREEN_WIDTH/2, 300 + BUTTON_HEIGHT/2));
        // Currently can't be tested.
        //clickExit(new Vector2(SCREEN_WIDTH/2, 170 + BUTTON_HEIGHT/2));
        clickInfo(new Vector2(SCREEN_WIDTH/8, 10 + BUTTON_HEIGHT/2));
        clickOptions(new Vector2(7*SCREEN_WIDTH/8, 10 + BUTTON_HEIGHT/2));
    }

    private void clickOptions(Vector2 input) {
        GameData.currentUI = new MenuUI();
        MenuUI menuUI = (MenuUI) GameData.currentUI;
        GameData.mainMenuState = true;
        GameData.optionsState = false;

        menuUI.getInput(SCREEN_WIDTH, input);
        assertFalse(GameData.mainMenuState);
        assertTrue(GameData.optionsState);
        assertTrue(GameData.currentUI.getClass() == OptionsUI.class);

        //cleanup
        GameData.optionsState = false;
    }

    private void clickInfo(Vector2 input) {
        GameData.currentUI = new MenuUI();
        MenuUI menuUI = (MenuUI) GameData.currentUI;
        GameData.mainMenuState = true;
        GameData.infoState = false;

        menuUI.getInput(SCREEN_WIDTH, input);
        assertFalse(GameData.mainMenuState);
        assertTrue(GameData.infoState);
        assertTrue(GameData.currentUI.getClass() == InfoUI.class);

        //cleanup
        GameData.infoState = false;
    }

    private void clickExit(Vector2 input) {
        // This results in an error - we need to access preferences during this test, so we can't mock Gdx.app
        // as we rely on its complex behaviour.
        Gdx.app = mock(Gdx.app.getClass());
        GameData.currentUI = new MenuUI();
        MenuUI menuUI = (MenuUI) GameData.currentUI;

        menuUI.getInput(SCREEN_WIDTH, input);
        verify(Gdx.app, times(1)).exit();
    }

    private void clickContinue(Vector2 input) {
        GameData.currentUI = new MenuUI();
        MenuUI menuUI = (MenuUI) GameData.currentUI;
        GameData.mainMenuState = true;
        GameData.gamePlayState = false;

        menuUI.getInput(SCREEN_WIDTH, input);
        assertFalse(GameData.mainMenuState);
        assertTrue(GameData.gamePlayState);
        assertTrue(GameData.currentUI.getClass() == GamePlayUI.class);

        //cleanup
        GameData.gamePlayState = false;
    }

    private void clickPlay(Vector2 input) {
        GameData.currentUI = new MenuUI();
        MenuUI menuUI = (MenuUI) GameData.currentUI;
        GameData.mainMenuState = true;
        GameData.chooseDifficultyState = false;

        menuUI.getInput(SCREEN_WIDTH, input);
        assertFalse(GameData.mainMenuState);
        assertTrue(GameData.chooseDifficultyState);
        assertTrue(GameData.currentUI.getClass() == ChooseDifficultyUI.class);

        //cleanup
        GameData.chooseDifficultyState = false;
    }


}
