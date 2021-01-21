package com.hardgforgif.dragonboatracing.core;
// Added code start

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.hardgforgif.dragonboatracing.BodyEditorLoader;
import com.hardgforgif.dragonboatracing.GameData;

public class PowerUp {

    public Sprite powerupSprite;
    public Body powerupBody;
    public String powerupName;
    private Texture powerupTexture;

    public PowerUp(String textureName) {
        powerupTexture = new Texture(textureName);
        if (textureName.equals("PowerUps/PowerUp1.png")) {
            powerupName = "healthBoost";
        } else if (textureName.equals("PowerUps/PowerUp2.png")) {
            powerupName = "staminaBoost";
        } else if (textureName.equals("PowerUps/PowerUp3.png")) {
            powerupName = "speedBoost";
        } else if (textureName.equals("PowerUps/PowerUp4.png")) {
            powerupName = "accelerationBoost";
        } else if (textureName.equals("PowerUps/PowerUp5.png")) {
            powerupName = "timeReduction";
        }
    }

    /**
     * Creates a new powerup body
     *
     * @param world    World to create the body in
     * @param posX     x location of the body, in meters
     * @param posY     y location of the body, in meters
     * @param bodyFile the name of the box2D editor json file for the body fixture
     */
    public void createPowerUpBody(World world, float posX, float posY, String bodyFile, float scale) {
        powerupSprite = new Sprite(powerupTexture);
        powerupSprite.scale(scale);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        bodyDef.position.set(posX, posY);
        powerupBody = world.createBody(bodyDef);

        powerupBody.setUserData(this);

        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal(bodyFile));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0f;
        fixtureDef.restitution = 0f;
        fixtureDef.friction = 0f;

        scale = powerupSprite.getWidth() / GameData.METERS_TO_PIXELS * powerupSprite.getScaleX();
        loader.attachFixture(powerupBody, "Name", fixtureDef, scale);

        powerupSprite.setPosition((powerupBody.getPosition().x * GameData.METERS_TO_PIXELS) - powerupSprite.getWidth() / 2,
                (powerupBody.getPosition().y * GameData.METERS_TO_PIXELS) - powerupSprite.getHeight() / 2);
    }

    /**
     * Draw the powerUp
     *
     * @param batch Batch to draw on
     */
    public void drawPowerUp(Batch batch) {
        powerupSprite.setPosition((powerupBody.getPosition().x * GameData.METERS_TO_PIXELS) - powerupSprite.getWidth() / 2,
                (powerupBody.getPosition().y * GameData.METERS_TO_PIXELS) - powerupSprite.getHeight() / 2);
        batch.begin();
        batch.draw(powerupSprite, powerupSprite.getX(), powerupSprite.getY(), powerupSprite.getOriginX(),
                powerupSprite.getOriginY(),
                powerupSprite.getWidth(), powerupSprite.getHeight(), powerupSprite.getScaleX(),
                powerupSprite.getScaleY(), powerupSprite.getRotation());
        batch.end();
    }
}
// Added code end
