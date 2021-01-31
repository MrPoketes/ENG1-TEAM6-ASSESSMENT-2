package com.hardgforgif.dragonboatracing.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.hardgforgif.dragonboatracing.GameData;
import com.hardgforgif.dragonboatracing.core.Player;

public class MenuUI extends UI {

    //Sets the dimensions for all the UI components
    private static final int LOGO_WIDTH = 400;
    private static final int LOGO_HEIGHT = 200;
    private static final int LOGO_Y = 450;

    private static final int PLAY_BUTTON_WIDTH = 300;
    private static final int PLAY_BUTTON_HEIGHT = 120;
    private static final int PLAY_BUTTON_Y = 300;

    private static final int EXIT_BUTTON_WIDTH = 250;
    private static final int EXIT_BUTTON_HEIGHT = 120;
    private static final int EXIT_BUTTON_Y = 170;

    private static final int OPTIONS_BUTTON_WIDTH = 200;
    private static final int OPTIONS_BUTTON_HEIGHT = 120;
    private static final int OPTIONS_BUTTON_Y = 10;
    // Added code start
    private static final int INFO_BUTTON_WIDTH = 200;
    private static final int INFO_BUTTON_HEIGHT = 120;
    private static final int INFO_BUTTON_Y = 10;

    private static final int CONTINUE_BUTTON_WIDTH = 300;
    private static final int CONTINUE_BUTTON_HEIGHT = 120;
    private static final int CONTINUE_BUTTON_Y = 170;

    Texture infoButtonActive;
    Texture infoButtonInactive;
    Texture optionsButtonActive;
    Texture optionsButtonInactive;
    Texture continueButtonInactive;
    Texture continueButtonActive;
    Preferences prefs;
    // Added code end
    Texture playButtonActive;
    Texture playButtonInactive;
    Texture exitButtonActive;
    Texture exitButtonInactive;
    Texture logo;

    ScrollingBackground scrollingBackground = new ScrollingBackground();


    public MenuUI() {
        scrollingBackground.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        scrollingBackground.setSpeedFixed(true);
        scrollingBackground.setSpeed(ScrollingBackground.DEFAULT_SPEED);

        playButtonActive = new Texture("PlaySelected.png");
        playButtonInactive = new Texture("PlayUnselected.png");
        exitButtonActive = new Texture("ExitSelected.png");
        exitButtonInactive = new Texture("ExitUnselected.png");
        // Added code start
        optionsButtonActive = new Texture("SettingsSelected.png");
        optionsButtonInactive = new Texture("SettingsUnselected.png");
        infoButtonActive = new Texture("InfoSelected.png");
        infoButtonInactive = new Texture("InfoUnselected.png");
        continueButtonInactive = new Texture("ContinueUnselected.png");
        continueButtonActive = new Texture("ContinueSelected.png");
        // Added code end
        logo = new Texture("Title.png");

    }

    @Override
    public void drawUI(Batch batch, Vector2 mousePos, float screenWidth, float delta) {
        batch.begin();
        scrollingBackground.updateAndRender(delta, batch);
        batch.draw(logo, screenWidth / 2 - LOGO_WIDTH / 2, LOGO_Y, LOGO_WIDTH, LOGO_HEIGHT);
        prefs = Gdx.app.getPreferences("savedData");
        // If the mouse is not hovered over the buttons, draw the unselected buttons
        float x = screenWidth / 2 - PLAY_BUTTON_WIDTH / 2;
        if (
                mousePos.x < x + PLAY_BUTTON_WIDTH && mousePos.x > x &&
                        // cur pos < top_height
                        mousePos.y < PLAY_BUTTON_Y + PLAY_BUTTON_HEIGHT &&
                        mousePos.y > PLAY_BUTTON_Y
        ) {
            batch.draw(playButtonActive, x, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
        } else {
            batch.draw(playButtonInactive, x, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
        }
        // Added code start
        if (prefs.contains("playerRobustness")) {
            x = screenWidth / 2 - EXIT_BUTTON_WIDTH / 2;
            if (
                    mousePos.x < x + EXIT_BUTTON_WIDTH && mousePos.x > x &&
                            mousePos.y < EXIT_BUTTON_Y - 120 + EXIT_BUTTON_HEIGHT &&
                            mousePos.y > EXIT_BUTTON_Y - 120
            ) {
                batch.draw(exitButtonActive, x, EXIT_BUTTON_Y - 120, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
            } else {
                batch.draw(exitButtonInactive, x, EXIT_BUTTON_Y - 120, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
            }
            x = screenWidth / 2 - CONTINUE_BUTTON_WIDTH / 2;
            if (
                    mousePos.x < x + CONTINUE_BUTTON_WIDTH && mousePos.x > x &&
                            mousePos.y < CONTINUE_BUTTON_Y + CONTINUE_BUTTON_HEIGHT &&
                            mousePos.y > CONTINUE_BUTTON_Y
            ) {
                batch.draw(continueButtonActive, x, CONTINUE_BUTTON_Y, CONTINUE_BUTTON_WIDTH, CONTINUE_BUTTON_HEIGHT);
            } else {
                batch.draw(continueButtonInactive, x, CONTINUE_BUTTON_Y, CONTINUE_BUTTON_WIDTH, CONTINUE_BUTTON_HEIGHT);
            }
        } else {
            x = screenWidth / 2 - EXIT_BUTTON_WIDTH / 2;
            if (
                    mousePos.x < x + EXIT_BUTTON_WIDTH && mousePos.x > x &&
                            mousePos.y < EXIT_BUTTON_Y + EXIT_BUTTON_HEIGHT &&
                            mousePos.y > EXIT_BUTTON_Y
            ) {
                batch.draw(exitButtonActive, x, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
            } else {
                batch.draw(exitButtonInactive, x, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
            }
        }

        // Added code start
        x = 7 * screenWidth / 8 - OPTIONS_BUTTON_WIDTH / 2;
        if (
                mousePos.x < x + OPTIONS_BUTTON_WIDTH && mousePos.x > x &&
                        mousePos.y < OPTIONS_BUTTON_Y + OPTIONS_BUTTON_HEIGHT &&
                        mousePos.y > OPTIONS_BUTTON_Y) {
            batch.draw(optionsButtonActive, x, OPTIONS_BUTTON_Y, OPTIONS_BUTTON_WIDTH, OPTIONS_BUTTON_HEIGHT);
        } else {
            batch.draw(optionsButtonInactive, x, OPTIONS_BUTTON_Y, OPTIONS_BUTTON_WIDTH, OPTIONS_BUTTON_HEIGHT);
        }
        x = screenWidth / 8 - INFO_BUTTON_WIDTH / 2;
        if (mousePos.x < x + INFO_BUTTON_WIDTH && mousePos.x > x &&
                mousePos.y < INFO_BUTTON_Y + INFO_BUTTON_HEIGHT &&
                mousePos.y > INFO_BUTTON_Y) {
            batch.draw(infoButtonActive, x, INFO_BUTTON_Y, INFO_BUTTON_WIDTH, INFO_BUTTON_HEIGHT);
        } else {
            batch.draw(infoButtonInactive, x, INFO_BUTTON_Y, INFO_BUTTON_WIDTH, INFO_BUTTON_HEIGHT);
        }
        batch.end();

        playMusic();
    }

    @Override
    public void drawPlayerUI(Batch batch, Player playerBoat) {

    }

    @Override
    public void getInput(float screenWidth, Vector2 clickPos) {
        // If the play button is clicked
        float x = screenWidth / 2 - PLAY_BUTTON_WIDTH / 2;
        if (
                clickPos.x < x + PLAY_BUTTON_WIDTH && clickPos.x > x &&
                        // cur pos < top_height
                        clickPos.y < PLAY_BUTTON_Y + PLAY_BUTTON_HEIGHT &&
                        clickPos.y > PLAY_BUTTON_Y
        ) {
            // Switch to the choosing state
            GameData.mainMenuState = false;
            GameData.chooseDifficultyState = true;
            GameData.currentUI = new ChooseDifficultyUI();
        }
        // Added code start
        prefs = Gdx.app.getPreferences("savedData");
        if (prefs.contains("playerRobustness")) {
            // If the exit button is clicked, close the game
            x = screenWidth / 2 - EXIT_BUTTON_WIDTH / 2;
            if (clickPos.x < x + EXIT_BUTTON_WIDTH && clickPos.x > x &&
                    clickPos.y < EXIT_BUTTON_Y - 120 + EXIT_BUTTON_HEIGHT &&
                    clickPos.y > EXIT_BUTTON_Y - 120
            ) {
                Gdx.app.exit();
            }
            if (clickPos.x < x + CONTINUE_BUTTON_WIDTH && clickPos.x > x &&
                    clickPos.y < CONTINUE_BUTTON_Y + CONTINUE_BUTTON_HEIGHT &&
                    clickPos.y > CONTINUE_BUTTON_Y - 120
            ) {
                GameData.mainMenuState = false;
                GameData.gamePlayState = true;
                GameData.fromSave = true;
                GameData.currentUI = new GamePlayUI();
            }
        }

        x = 7 * screenWidth / 8 - OPTIONS_BUTTON_WIDTH / 2;
        if (clickPos.x < x + OPTIONS_BUTTON_WIDTH && clickPos.x > x &&
                clickPos.y < OPTIONS_BUTTON_Y + OPTIONS_BUTTON_HEIGHT &&
                clickPos.y > OPTIONS_BUTTON_Y) {
            GameData.mainMenuState = false;
            GameData.optionsState = true;
            GameData.currentUI = new OptionsUI();
        }
        x = screenWidth / 8 - INFO_BUTTON_WIDTH / 2;
        if (clickPos.x < x + INFO_BUTTON_WIDTH && clickPos.x > x &&
                clickPos.y < INFO_BUTTON_Y + INFO_BUTTON_HEIGHT &&
                clickPos.y > INFO_BUTTON_Y) {
            GameData.mainMenuState = false;
            GameData.infoState = true;
            GameData.currentUI = new InfoUI();
        }

    }
}
