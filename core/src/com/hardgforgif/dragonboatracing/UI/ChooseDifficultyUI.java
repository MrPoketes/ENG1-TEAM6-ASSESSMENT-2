package com.hardgforgif.dragonboatracing.UI;
// Added code start

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.hardgforgif.dragonboatracing.GameData;
import com.hardgforgif.dragonboatracing.core.Player;

public class ChooseDifficultyUI extends UI {

    private static final int EASY_BUTTON_WIDTH = 200;
    private static final int EASY_BUTTON_HEIGHT = 100;
    private static final int EASY_BUTTON_Y = 300;

    private static final int MEDIUM_BUTTON_WIDTH = 200;
    private static final int MEDIUM_BUTTON_HEIGHT = 100;
    private static final int MEDIUM_BUTTON_Y = 300;

    private static final int HARD_BUTTON_WIDTH = 200;
    private static final int HARD_BUTTON_HEIGHT = 100;
    private static final int HARD_BUTTON_Y = 300;

    private static final int PLAY_BUTTON_WIDTH = 200;
    private static final int PLAY_BUTTON_HEIGHT = 100;
    private static final int PLAY_BUTTON_Y = 100;

    Texture easyButton;
    Texture mediumButton;
    Texture hardButton;
    Texture playButtonInactive;
    Texture playButtonActive;

    ScrollingBackground scrollingBackground = new ScrollingBackground();

    BitmapFont chosenDifficulty;

    public ChooseDifficultyUI() {
        scrollingBackground.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        scrollingBackground.setSpeedFixed(true);
        scrollingBackground.setSpeed(ScrollingBackground.DEFAULT_SPEED);

        easyButton = new Texture("EasyButton.png");
        mediumButton = new Texture("MediumButton.png");
        hardButton = new Texture("HardButton.png");
        playButtonInactive = new Texture("PlayUnselected.png");
        playButtonActive = new Texture("PlaySelected.png");

        chosenDifficulty = new BitmapFont();
        chosenDifficulty.getData().setScale(2f);
        chosenDifficulty.setColor(Color.BLACK);
    }

    @Override
    public void drawUI(Batch batch, Vector2 mousePos, float screenWidth, float delta) {
        batch.begin();
        scrollingBackground.updateAndRender(delta, batch);

        float x = screenWidth / 4 - EASY_BUTTON_WIDTH / 2;
        batch.draw(easyButton, x, EASY_BUTTON_Y, EASY_BUTTON_WIDTH, EASY_BUTTON_HEIGHT);
        x = 2 * screenWidth / 4 - MEDIUM_BUTTON_WIDTH / 2;
        batch.draw(mediumButton, x, MEDIUM_BUTTON_Y, MEDIUM_BUTTON_WIDTH, MEDIUM_BUTTON_HEIGHT);
        x = 3 * screenWidth / 4 - HARD_BUTTON_WIDTH / 2;
        batch.draw(hardButton, x, HARD_BUTTON_Y, HARD_BUTTON_WIDTH, HARD_BUTTON_HEIGHT);
        x = screenWidth / 2 - PLAY_BUTTON_WIDTH / 2;
        if (
                mousePos.x < x + PLAY_BUTTON_WIDTH && mousePos.x > x &&
                        mousePos.y < PLAY_BUTTON_Y + PLAY_BUTTON_HEIGHT &&
                        mousePos.y > PLAY_BUTTON_Y) {
            batch.draw(playButtonActive, x, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
        } else {
            batch.draw(playButtonInactive, x, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
        }


        chosenDifficulty.draw(batch, "Chosen difficulty - " + GameData.gameDifficulty, x - 50f, 500);
        batch.end();
        playMusic();
    }

    @Override
    public void drawPlayerUI(Batch batch, Player playerBoat) {

    }

    @Override
    public void getInput(float screenWidth, Vector2 clickPos) {
        float x = screenWidth / 4 - EASY_BUTTON_WIDTH / 2;
        if (
                clickPos.x < x + EASY_BUTTON_HEIGHT && clickPos.x > x &&
                        clickPos.y < EASY_BUTTON_Y + EASY_BUTTON_HEIGHT &&
                        clickPos.y > EASY_BUTTON_Y
        ) {
            GameData.gameDifficulty = "easy";
        }
        x = 2 * screenWidth / 4 - MEDIUM_BUTTON_WIDTH / 2;
        if (
                clickPos.x < x + MEDIUM_BUTTON_HEIGHT && clickPos.x > x &&
                        clickPos.y < MEDIUM_BUTTON_Y + MEDIUM_BUTTON_HEIGHT &&
                        clickPos.y > MEDIUM_BUTTON_Y
        ) {
            GameData.gameDifficulty = "medium";
        }
        x = 3 * screenWidth / 4 - HARD_BUTTON_WIDTH / 2;
        if (
                clickPos.x < x + HARD_BUTTON_HEIGHT && clickPos.x > x &&
                        clickPos.y < HARD_BUTTON_Y + HARD_BUTTON_HEIGHT &&
                        clickPos.y > HARD_BUTTON_Y
        ) {
            GameData.gameDifficulty = "hard";
        }
        x = screenWidth / 2 - PLAY_BUTTON_WIDTH / 2;
        if (
                clickPos.x < x + PLAY_BUTTON_HEIGHT && clickPos.x > x &&
                        clickPos.y < PLAY_BUTTON_Y + PLAY_BUTTON_HEIGHT &&
                        clickPos.y > PLAY_BUTTON_Y
        ) {
            GameData.difficultyChanged = true;
            GameData.chooseDifficultyState = false;
            GameData.choosingBoatState = true;
            GameData.currentUI = new ChoosingUI();
        }
    }
}
// Added code end
