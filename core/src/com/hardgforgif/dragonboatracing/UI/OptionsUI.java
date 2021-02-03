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

    private static final int WASDBUTTON_WIDTH = 100;
    private static final int WASDBUTTON_HEIGHT = 50;
    private static final int WASDBUTTON_Y = 450;

    private static final int ARROWKEYSBUTTON_WIDTH = 100;
    private static final int ARROWKEYSBUTTON_HEIGHT = 50;
    private static final int ARROWKEYSBUTTON_Y = 450;


    Texture plusButton;
    Texture minusButton;

    Texture backButton;


    Texture WASDButton;
    Texture ArrowKeysButton;

    ScrollingBackground scrollingBackground = new ScrollingBackground();

    // Texts
    private BitmapFont volume;
    private BitmapFont volumeLabel;
    private BitmapFont WASD;
    private BitmapFont Arrow;

    public OptionsUI() {
        scrollingBackground.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        scrollingBackground.setSpeedFixed(true);
        scrollingBackground.setSpeed(ScrollingBackground.DEFAULT_SPEED);

        plusButton = new Texture("PlusButton.png");
        minusButton = new Texture("MinusButton.png");

        backButton = new Texture("BackButton.png");

        WASDButton = new Texture("Background.png");
        ArrowKeysButton = new Texture("Background.png");

        // Volume initializers
        volume = new BitmapFont();
        volume.getData().setScale(1.4f);
        volume.setColor(Color.BLACK);
        volumeLabel = new BitmapFont();
        volumeLabel.getData().setScale(2f);
        volumeLabel.setColor(Color.BLACK);

        //WASD and Arrow Keys initializers
        WASD = new BitmapFont();
        WASD.setColor(Color.BLACK);
        Arrow = new BitmapFont();
        Arrow.setColor(Color.BLACK);

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

        batch.draw(backButton, 100f, BACK_BUTTON_Y, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT);

        //drawing WASD and Arrow key buttons
        x = 2 * screenWidth / 5 - WASDBUTTON_WIDTH / 2;
        batch.draw(WASDButton, x, WASDBUTTON_Y, WASDBUTTON_WIDTH, WASDBUTTON_HEIGHT);
        WASD.draw(batch, "WASD", x + 20, WASDBUTTON_Y + 30);

        x = 3 * screenWidth / 5 - ARROWKEYSBUTTON_WIDTH / 2;
        batch.draw(ArrowKeysButton, x, ARROWKEYSBUTTON_Y, ARROWKEYSBUTTON_WIDTH, ARROWKEYSBUTTON_HEIGHT);
        Arrow.draw(batch, "Arrow", x + 20, ARROWKEYSBUTTON_Y + 30);


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

        x = 2 * screenWidth / 5 - WASDBUTTON_HEIGHT / 2;
        if (clickPos.x < x + WASDBUTTON_WIDTH && clickPos.x > x &&
                clickPos.y < WASDBUTTON_Y + WASDBUTTON_HEIGHT &&
                clickPos.y > WASDBUTTON_Y) {
            WASD.setColor(Color.WHITE);
            Arrow.setColor(Color.BLACK);
            GameData.switchControls = false;
        }
        x = 3 * screenWidth / 5 - ARROWKEYSBUTTON_WIDTH / 2;
        if (clickPos.x < x + ARROWKEYSBUTTON_WIDTH && clickPos.x > x &&
                clickPos.y < ARROWKEYSBUTTON_Y + ARROWKEYSBUTTON_HEIGHT &&
                clickPos.y > ARROWKEYSBUTTON_Y) {
            WASD.setColor(Color.BLACK);
            Arrow.setColor(Color.WHITE);
            GameData.switchControls = true;
        }

    }
}
// Added code end