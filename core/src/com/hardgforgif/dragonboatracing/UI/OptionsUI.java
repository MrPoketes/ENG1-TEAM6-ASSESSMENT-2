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

public class OptionsUI extends UI {

    private static final int PLUS_BUTTON_WIDTH = 50;
    private static final int PLUS_BUTTON_HEIGHT = 50;
    private static final int PLUS_BUTTON_Y = 550;

    private static final int MINUS_BUTTON_WIDTH = 50;
    private static final int MINUS_BUTTON_HEIGHT = 50;
    private static final int MINUS_BUTTON_Y = 550;

    private static final int BACK_BUTTON_WIDTH = 150;
    private static final int BACK_BUTTON_HEIGHT = 100;
    private static final int BACK_BUTTON_Y = 50;

    private static final int WINDOWED_BUTTON_WIDTH = 100;
    private static final int WINDOWED_BUTTON_HEIGHT = 50;
    private static final int WINDOWED_BUTTON_Y = 450;

    private static final int FULLSCREEN_BUTTON_WIDTH = 100;
    private static final int FULLSCREEN_BUTTON_HEIGHT = 50;
    private static final int FULLSCREEN_BUTTON_Y = 450;

    Texture plusButton;
    Texture minusButton;

    Texture backButton;

    // Windowed and fullscreen button textures
    Texture windowedButton;
    Texture fullscreenButton;

    ScrollingBackground scrollingBackground = new ScrollingBackground();

    // Texts
    private BitmapFont volume;
    private BitmapFont volumeLabel;

    public OptionsUI() {
        scrollingBackground.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        scrollingBackground.setSpeedFixed(true);
        scrollingBackground.setSpeed(ScrollingBackground.DEFAULT_SPEED);

        plusButton = new Texture("PlusButton.png");
        minusButton = new Texture("MinusButton.png");

        backButton = new Texture("BackButton.png");

        windowedButton = new Texture("testPlusButton.png");
        fullscreenButton = new Texture("testMinusButton.png");

        // Volume initializers
        volume = new BitmapFont();
        volume.getData().setScale(1.4f);
        volume.setColor(Color.BLACK);
        volumeLabel = new BitmapFont();
        volumeLabel.getData().setScale(2f);
        volumeLabel.setColor(Color.BLACK);

    }

    @Override
    public void drawUI(Batch batch, Vector2 mousePos, float screenWidth, float delta) {
        batch.begin();
        scrollingBackground.updateAndRender(delta, batch);

        // Drawing volume items
        float x = 2 * screenWidth / 5 - MINUS_BUTTON_WIDTH / 2;
        batch.draw(minusButton, x, MINUS_BUTTON_Y, MINUS_BUTTON_WIDTH, MINUS_BUTTON_HEIGHT);
        x = 3 * screenWidth / 5 - PLUS_BUTTON_WIDTH / 2;
        batch.draw(plusButton, x, PLUS_BUTTON_Y, PLUS_BUTTON_WIDTH, PLUS_BUTTON_HEIGHT);
        x = screenWidth / 2 - PLUS_BUTTON_WIDTH / 2;
        volume.draw(batch, String.valueOf(Math.round(GameData.musicVolume * 100)), x, PLUS_BUTTON_Y + 40);
        volumeLabel.draw(batch, "Volume", x - 30, PLUS_BUTTON_Y + 100);

        // Drawing screen resolution buttons
        x = 2 * screenWidth / 5 - FULLSCREEN_BUTTON_WIDTH / 2;
        batch.draw(fullscreenButton, x, FULLSCREEN_BUTTON_Y, FULLSCREEN_BUTTON_WIDTH, FULLSCREEN_BUTTON_HEIGHT);
        x = 3 * screenWidth / 5 - WINDOWED_BUTTON_WIDTH / 2;
        batch.draw(windowedButton, x, WINDOWED_BUTTON_Y, WINDOWED_BUTTON_WIDTH, WINDOWED_BUTTON_HEIGHT);

        batch.draw(backButton, 100f, BACK_BUTTON_Y, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT);

        batch.end();

        playMusic();
    }

    @Override
    public void drawPlayerUI(Batch batch, Player playerBoat) {

    }

    @Override
    public void getInput(float screenWidth, Vector2 clickPos) {
        // Input handlers for volume buttons
        float x = 2 * screenWidth / 5 - MINUS_BUTTON_HEIGHT / 2;
        if (
                clickPos.x < x + MINUS_BUTTON_HEIGHT && clickPos.x > x &&
                        // cur pos < top_height
                        clickPos.y < MINUS_BUTTON_Y + MINUS_BUTTON_HEIGHT &&
                        clickPos.y > MINUS_BUTTON_Y
        ) {
            if (GameData.musicVolume > 0.1f) {
                GameData.musicVolume -= 0.1f;
                // For some reason setVolume(GameData.musicVolume - 0.1f) doesn't work
                GameData.music.setVolume(GameData.musicVolume);
            }

        }

        x = 3 * screenWidth / 5 - PLUS_BUTTON_WIDTH / 2;
        if (clickPos.x < x + PLUS_BUTTON_WIDTH && clickPos.x > x &&
                clickPos.y < PLUS_BUTTON_Y + PLUS_BUTTON_HEIGHT &&
                clickPos.y > PLUS_BUTTON_Y
        ) {
            if (GameData.musicVolume < 1) {
                GameData.musicVolume += 0.1f;
                GameData.music.setVolume(GameData.musicVolume);
            }
        }

        // Input handler for back button
        if (clickPos.x < 100f + BACK_BUTTON_WIDTH && clickPos.x > 100f &&
                clickPos.y < BACK_BUTTON_Y + BACK_BUTTON_HEIGHT &&
                clickPos.y > BACK_BUTTON_Y) {
            GameData.optionsState = false;
            GameData.mainMenuState = true;
            GameData.currentUI = new MenuUI();
        }

        // Input handlers for windowed/fullscreen buttons

        x = 2 * screenWidth / 5 - FULLSCREEN_BUTTON_HEIGHT / 2;
        if (clickPos.x < x + FULLSCREEN_BUTTON_WIDTH && clickPos.x > x &&
                clickPos.y < FULLSCREEN_BUTTON_Y + FULLSCREEN_BUTTON_HEIGHT &&
                clickPos.y > FULLSCREEN_BUTTON_Y) {
            // Doesn't work well
            GameData.fullscreen = true;
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }

        x = 3 * screenWidth / 5 - WINDOWED_BUTTON_WIDTH / 2;
        if (clickPos.x < x + WINDOWED_BUTTON_WIDTH && clickPos.x > x &&
                clickPos.y < WINDOWED_BUTTON_Y + WINDOWED_BUTTON_HEIGHT &&
                clickPos.y > WINDOWED_BUTTON_Y) {
            GameData.fullscreen = false;
            Gdx.graphics.setWindowedMode(1280, 720);
        }
    }
}
// Added code end