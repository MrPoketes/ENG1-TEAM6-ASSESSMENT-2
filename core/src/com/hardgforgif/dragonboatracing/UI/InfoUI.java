package com.hardgforgif.dragonboatracing.UI;
// Added code start

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.hardgforgif.dragonboatracing.GameData;
import com.hardgforgif.dragonboatracing.core.Player;

public class InfoUI extends UI {

    private static final int BACK_BUTTON_WIDTH = 150;
    private static final int BACK_BUTTON_HEIGHT = 100;
    private static final int BACK_BUTTON_Y = 50;

    Texture backButton;

    ScrollingBackground scrollingBackground = new ScrollingBackground();


    public InfoUI() {
        scrollingBackground.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        scrollingBackground.setSpeedFixed(true);
        scrollingBackground.setSpeed(ScrollingBackground.DEFAULT_SPEED);

        backButton = new Texture("BackButton.png");
    }

    @Override
    public void drawUI(Batch batch, Vector2 mousePos, float screenWidth, float delta) {
        batch.begin();
        scrollingBackground.updateAndRender(delta, batch);

        float x = 7 * screenWidth / 8 - BACK_BUTTON_WIDTH / 2;
        batch.draw(backButton, x, BACK_BUTTON_Y, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT);
        batch.end();
        playMusic();
    }

    @Override
    public void drawPlayerUI(Batch batch, Player playerBoat) {

    }

    @Override
    public void getInput(float screenWidth, Vector2 clickPos) {
        float x = 7 * screenWidth / 8 - BACK_BUTTON_WIDTH / 2;
        if (
                clickPos.x < x + BACK_BUTTON_HEIGHT && clickPos.x > x &&
                        // cur pos < top_height
                        clickPos.y < BACK_BUTTON_Y + BACK_BUTTON_HEIGHT &&
                        clickPos.y > BACK_BUTTON_Y
        ) {
            GameData.infoState = false;
            GameData.mainMenuState = true;
            GameData.currentUI = new MenuUI();
        }
    }
}
// Added code end
