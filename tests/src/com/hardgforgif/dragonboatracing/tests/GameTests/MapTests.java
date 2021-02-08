package com.hardgforgif.dragonboatracing.tests.GameTests;

import com.hardgforgif.dragonboatracing.core.Map;
import com.hardgforgif.dragonboatracing.tests.GdxTestRunner;
import com.hardgforgif.dragonboatracing.tests.TestBase;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class MapTests extends TestBase {

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

}
