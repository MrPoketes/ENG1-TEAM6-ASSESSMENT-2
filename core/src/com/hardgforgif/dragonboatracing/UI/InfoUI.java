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

import javax.xml.soap.Text;

public class InfoUI extends UI {

    private static final int BACK_BUTTON_WIDTH = 150;
    private static final int BACK_BUTTON_HEIGHT = 100;
    private static final int BACK_BUTTON_Y = 50;
    private static final int BACKGROUND_WIDTH = 600;
    private static final int BACKGROUND_HEIGHT = 450;

    Texture backButton;
    Texture Background;
    BitmapFont font = new BitmapFont();
    String Header;
    String InstructionText;
    Color fontColor = Color.BLACK;


    ScrollingBackground scrollingBackground = new ScrollingBackground();


    public InfoUI() {
        scrollingBackground.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        scrollingBackground.setSpeedFixed(true);
        scrollingBackground.setSpeed(ScrollingBackground.DEFAULT_SPEED);

        backButton = new Texture("BackButton.png");
        Background = new Texture("Background.png");
        Header = "Instructions!";
        InstructionText = "The aim of the game is to race through the river against other opponents.\n"
                        + "While doing so you will need to avoid obstacles which will slow you down. \n" +
                        "There are also various abilities you can pick up to improve your chances of winning." + "\n"
                        + "There will be 3 racing legs where you will get a chance to qualify for the finals" + "\n"
                        + "The top 3 best times will be placed into the final leg!" + "\n" + "\n"
                        + "The arrow keys or WASD can be used to move the boat forward,backwards, left and" + "\n"
                        + "Right. The layout of controls can be selected in the option menu" + "\n"
                        +"Good luck! Enjoy the racing!";
    }

    @Override
    public void drawUI(Batch batch, Vector2 mousePos, float screenWidth, float delta) {
        batch.begin();
        scrollingBackground.updateAndRender(delta, batch);

        float x = 7 * screenWidth / 8 - BACK_BUTTON_WIDTH / 2;
        float TextHeight = 550;
        batch.draw(backButton, x, BACK_BUTTON_Y, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT);
        batch.draw(Background, screenWidth - 950, 200, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        font.draw(batch, Header, (float) (screenWidth/2  - 50.0), TextHeight);
        font.draw(batch, InstructionText, (screenWidth/2 - 280), TextHeight - 80);
        font.setColor(fontColor);
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
