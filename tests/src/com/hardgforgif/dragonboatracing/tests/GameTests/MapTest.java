package com.hardgforgif.dragonboatracing.tests.GameTests;

import com.badlogic.gdx.Gdx;
import com.hardgforgif.dragonboatracing.GameData;
import com.hardgforgif.dragonboatracing.core.AI;
import com.hardgforgif.dragonboatracing.core.Map;
import com.hardgforgif.dragonboatracing.core.Player;
import com.hardgforgif.dragonboatracing.tests.GdxTestRunner;
import com.hardgforgif.dragonboatracing.tests.TestBase;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class MapTest extends TestBase {

    private int countObstacles(Map map) {
        int count = 0;
        for (int i = 0; i <= 3; i++) {
            count += map.lanes[i].obstacles.length;
        }
        return count;
    }

    private int countPowerups(Map map) {
        int count = 0;
        for (int i = 0; i <= 3; i++) {
            count += map.lanes[i].powerUps.length;
        }
        return count;
    }

    @Test
    public void TEST_FR_SELECT_DIFFICULTY_MAP() {
        /*
        Create an easy, medium and hard map.
        Count the number of obstacles and powerups in each of them.
        Pass the test if the harder maps have >= obstacles and <= powerups.
         */
        Map easyMap = new Map("Map1/Map1.tmx", 1000, "easy");
        easyMap.createLanes();
        Map mediumMap = new Map("Map1/Map1.tmx", 1000, "medium");
        mediumMap.createLanes();
        Map hardMap = new Map("Map1/Map1.tmx", 1000, "hard");
        hardMap.createLanes();

        assertTrue((countObstacles(hardMap) >= countObstacles(mediumMap)) && (countObstacles(mediumMap) >= countObstacles(easyMap)));
        assertTrue((countPowerups(hardMap) <= countPowerups(mediumMap)) && (countPowerups(mediumMap) <= countPowerups(easyMap)));
    }

    @Test
    public void TEST_UR_MAP_OBJECT_COUNT() {
        /*
        Create a map for each difficulty.
        Count the number of obstacles and powerups in each lane.
        Pass the test if the value is the same for each lane.
         */
        Map[] maps = new Map[3];
        maps[0] = new Map("Map1/Map1.tmx", 1000, "easy");
        maps[1] = new Map("Map1/Map1.tmx", 1000, "medium");
        maps[2] = new Map("Map1/Map1.tmx", 1000, "hard");
        for (int i = 0; i <= 2; i++) {
            Map map = maps[i];
            map.createLanes();
            int obstacleCount = map.lanes[0].obstacles.length;
            int powerupCount = map.lanes[0].powerUps.length;
            for (int j = 1; j <= 3; j++) {
                assertEquals(map.lanes[j].obstacles.length, obstacleCount);
                assertEquals(map.lanes[j].powerUps.length, powerupCount);
            }
        }
    }

    @Test
    public void TEST_UR_MAP_LENGTH() {
        /*
        Load a blank map.
        Step the world and the player's boat 60*30 times (30 seconds), and check that the player has not completed the race.
        Step the world 60*90 times, and check that the player has completed the race.
         */
        Object[] objects = loadSave("emptyRiver");
        Map map = (Map) objects[0];
        Player player = (Player) objects[1];
        AI[] opponents = (AI[]) objects[2];
        boolean[] NO_KEYS_PRESSED = {false, false, false, false};

        for (int i = 0; i < 60*30; i++) {
            map.stepWorld(player, opponents);
            player.updatePlayer(NO_KEYS_PRESSED, Gdx.graphics.getDeltaTime());
        }
        assertFalse(player.hasFinished());

        for (int i = 0; i < 60*30; i++) {
            map.stepWorld(player, opponents);
            player.updatePlayer(NO_KEYS_PRESSED, Gdx.graphics.getDeltaTime());
        }
        assertTrue(player.hasFinished());
    }

    @Test
    public void TEST_FR_SAVE_AND_LOAD_LOADING() {
        Object[] objects = loadSave("knownSave");
        Map map = (Map) objects[0];

        assertEquals(0, map.lanes[0].obstacles[0].posX, 0.1);
        assertEquals(2, map.lanes[0].obstacles[0].posY, 0.1);
        assertEquals("Obstacles/Obstacle5.png", map.lanes[0].obstacles[0].textureName);
        assertEquals("Obstacles/Obstacle5.json", map.lanes[0].obstacles[0].bodyFile);

        assertEquals(0, map.lanes[0].powerUps[0].posX, 0.1);
        assertEquals(2, map.lanes[0].powerUps[0].posY, 0.1);
        assertEquals("PowerUps/PowerUp1.png", map.lanes[0].powerUps[0].textureName);
        assertEquals("PowerUps/PowerUp1.json", map.lanes[0].powerUps[0].bodyFile);
        assertEquals("healthBoost", map.lanes[0].powerUps[0].powerupName);
    }

}
