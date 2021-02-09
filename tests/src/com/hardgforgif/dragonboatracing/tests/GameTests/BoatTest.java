package com.hardgforgif.dragonboatracing.tests.GameTests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.hardgforgif.dragonboatracing.GameData;
import com.hardgforgif.dragonboatracing.UI.ChooseDifficultyUI;
import com.hardgforgif.dragonboatracing.UI.ChoosingUI;
import com.hardgforgif.dragonboatracing.core.*;
import com.hardgforgif.dragonboatracing.tests.GdxTestRunner;
import com.hardgforgif.dragonboatracing.tests.TestBase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@RunWith(GdxTestRunner.class)
public class BoatTest extends TestBase {

    Lane laneMock = mock(Lane.class);
    boolean[] LEFT = {false, true, false, false};
    boolean[] RIGHT = {false, false, false, true};
    boolean[] NEITHER = {false, false, false, false};

    @Test
    public void TEST_UR_UNIQUE_BOATS() {
        /*
        Check that none of the boat stats in GameData.boatsStats are the same.
         */
        for (int i = 0; i >= 3; i++) {
            for (int j = 0; j >= 3; j++) {
                if (i != j) {
                    assertNotEquals(GameData.boatsStats[i], GameData.boatsStats[j]);
                }
            }
        }
    }

    @Test
    public void TEST_UR_DIFFICULTY() {
        /*
        Create an AI boat at every difficulty.
         */
        GameData.currentLeg = 0;
        AI easyAI = new AI(100f, 100f, 100f, 100f, 1, laneMock);
        GameData.currentLeg = 1;
        AI mediumAI = new AI(100f, 100f, 100f, 100f, 1, laneMock);
        GameData.currentLeg = 2;
        AI hardAI = new AI(100f, 100f, 100f, 100f, 1, laneMock);

        assertTrue(easyAI.robustness < mediumAI.robustness
                && easyAI.stamina < mediumAI.stamina
                && easyAI.speed < mediumAI.speed
                && easyAI.acceleration < mediumAI.acceleration
                && easyAI.maneuverability < mediumAI.maneuverability
        );
        assertTrue(mediumAI.robustness < hardAI.robustness
                && mediumAI.stamina < hardAI.stamina
                && mediumAI.speed < hardAI.speed
                && mediumAI.acceleration < hardAI.acceleration
                && mediumAI.maneuverability < hardAI.maneuverability
        );
    }

    @Test
    public void TEST_UR_CONTROLS() {
        /*
        Create a player boat in a completely empty world.
        Update it a few times with the left key pressed.
        Update it a few times with the right key pressed.
        Update it a few times with no key pressed.
        Pass if the boat turns in the correct direction.
         */
        World world;
        world = new World(new Vector2(0f, 0f), true);
        // Since this world has no lanes (and lanes can only exist in a map),
        // we need to create a believable mock so the boat doesn't error.
        laneMock.leftIterator = 0;
        laneMock.leftBoundry = new float[2][2];
        laneMock.rightIterator = 0;
        laneMock.rightBoundry = new float[2][2];
        Player boat = new Player(120f, 110f, 100f, 80f, 0, laneMock, 0f, 120f);
        boat.createBoatBody(world, 0, 0, "Boat1.json");

        //Move the boat forward for a while.
        for (int i = 0; i <= 60; i++) {
            world.step(1f / 60f, 6, 2);
            boat.updatePlayer(LEFT, Gdx.graphics.getDeltaTime());
        }
        assertTrue(boat.boatBody.getAngle() > 0);
        //Move the boat right for a while.
        for (int i = 0; i <= 120; i++) {
            world.step(1f / 60f, 6, 2);
            boat.updatePlayer(RIGHT, Gdx.graphics.getDeltaTime());
        }
        assertTrue(boat.boatBody.getAngle() < 0);
        //Press no keys for a while to return to neutral.
        for (int i = 0; i <= 240; i++) {
            world.step(1f / 60f, 6, 2);
            boat.updatePlayer(NEITHER, Gdx.graphics.getDeltaTime());
        }
        //Due to physics imprecision, we check that it's approximately bearing 0.
        assertTrue(boat.boatBody.getAngle() > -3 && boat.boatBody.getAngle() < 3);
    }

    @Test
    public void TEST_FR_PENALTY_IN_LANE() {
        /*
        Load an empty map with known lane borders (and the player inside their lane).
        Step the world a bit.
        Check that the player has not accumulated a penalty.
         */
        Object[] objects = loadSave("emptyRiver");
        Map map = (Map) objects[0];
        Player player = (Player) objects[1];
        AI[] opponents = (AI[]) objects[2];
        boolean[] NO_KEYS_PRESSED = {false, false, false, false};

        for (int i = 0; i < 60*5; i++) {
            map.stepWorld(player, opponents);
            player.updatePlayer(NO_KEYS_PRESSED, Gdx.graphics.getDeltaTime());
        }
        //Small delta as both should be zero.
        Assert.assertEquals(0, GameData.penalties[0], 0.001);
    }

    @Test
    public void TEST_FR_PENALTY_NOT_IN_LANE() {
        /*
        Load an empty map with known lane borders.
        Move the player to a location outside their lane.
        Step the world a bit.
        Check that the player has not accumulated a penalty.
         */
        Object[] objects = loadSave("emptyRiver");
        Map map = (Map) objects[0];
        Player player = (Player) objects[1];
        AI[] opponents = (AI[]) objects[2];
        boolean[] NO_KEYS_PRESSED = {false, false, false, false};

        //Move the player outside their lane.
        player.boatBody.setTransform(new Vector2(500, 0), 0);

        for (int i = 0; i < 60*5; i++) {
            map.stepWorld(player, opponents);
            player.updatePlayer(NO_KEYS_PRESSED, Gdx.graphics.getDeltaTime());
        }
        Assert.assertNotEquals(0, GameData.penalties[0]);
    }

}
