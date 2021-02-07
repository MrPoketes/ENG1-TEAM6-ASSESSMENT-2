package com.hardgforgif.dragonboatracing.tests.GameTests;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.hardgforgif.dragonboatracing.core.*;
import com.hardgforgif.dragonboatracing.tests.GdxTestRunner;
import com.hardgforgif.dragonboatracing.tests.TestBase;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(GdxTestRunner.class)
public class SimpleCollisionTest extends TestBase {

    Lane laneMock = mock(Lane.class);
    boolean passTest = false;

    @Test
    public void BoatCollidesWithObstacle(){
        /*
        Create a box2d world, to put the objects in.
        Add a collision handler that sets a flag to pass the test if a collision occurs.
        Create a boat - this will involve mocking a lane.
        Create an obstacle.
        Use moveBoat() to collide the boat into the obstacle.
         */
        World world;
        world = new World(new Vector2(0f, 0f), true);
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) { passTest = true; }
            @Override
            public void endContact(Contact contact) {}
            @Override
            public void preSolve(Contact contact, Manifold manifold) {}
            @Override
            public void postSolve(Contact contact, ContactImpulse contactImpulse) {}
        });

        Boat boat = new Boat(120f, 110f, 100f, 80f, 0, laneMock, 0f, 120f);
        boat.createBoatBody(world, 0, 0, "Boat1.json");
        Obstacle obstacle = new Obstacle("Obstacles/Obstacle1.png");
        obstacle.createObstacleBody(world, 0, 2, "Obstacles/Obstacle1.json", 0f);

        //Check that the boat isn't starting inside the obstacle.
        world.step(1f / 60f, 6, 2);
        assertFalse(passTest);

        //Move the boat forward for a while.
        for (int i = 0; i <= 60*5; i++) {
            world.step(1f / 60f, 6, 2);
            boat.moveBoat();
        }
        System.out.println("Boat is at x: " + boat.boatBody.getPosition().x + " y: " + boat.boatBody.getPosition().y);
        System.out.println("Obstacle is at x: " + obstacle.obstacleBody.getPosition().x + " y: " + obstacle.obstacleBody.getPosition().y);

        assertTrue(passTest);
    }

    @Test
    public void BoatDamagedByObstacle() {
        /*
        Use a mocked save file to load a map with just a boat and an obstacle (same positions as above test).
        Do exact same moveBoat() actions to collide boat into obstacle.
        Pass the test if the boat's robustness has decreased.
         */
        try {
            this.setPreferences("singleObstacle");
        }
        catch (FileNotFoundException e) {
            assertTrue(false);
        }
        //Create the map from load.
        Map map = new Map("Map1/Map1.tmx", 1280f, this.preferences.getString("gameDifficulty"));
        map.createLanesFromLoad(0);
        //Create the player from load.
        Player player = new Player(preferences.getFloat("playerRobustness"),
                preferences.getFloat("playerSpeed"),
                preferences.getFloat("playerAcceleration"),
                preferences.getFloat("playerManeuverability"),
                preferences.getInteger("playerBoatType"),
                map.lanes[0],
                preferences.getFloat("playerCurrentSpeed"),
                preferences.getFloat("playerStamina"));
        player.createBoatBody(map.world,
                preferences.getFloat("playerPosX"),
                preferences.getFloat("playerPosY"),
                "Boat1.json");
        player.boatSprite.setRotation(preferences.getFloat("playerAngle"));

        //Create the opponents from load.
        AI[] opponents = new AI[3];
        for (int i = 0; i != 3; i++) {
            float oRobustness = preferences.getFloat("opponent" + i + "Robustness");
            float oSpeed = preferences.getFloat("opponent" + i + "Speed");
            float oAcceleration = preferences.getFloat("opponent" + i + "Acceleration");
            float oManeuverability = preferences.getFloat("opponent" + i + "Maneuverability");
            int oBoatType = preferences.getInteger("opponent" + i + "BoatType");
            float oCurrentSpeed = preferences.getFloat("opponent" + i + "CurrentSpeed");
            float oStamina = preferences.getFloat("opponent" + i + "Stamina");
            float oPosX = preferences.getFloat("opponent" + i + "PosX");
            float oPosY = preferences.getFloat("opponent" + i + "PosY");
            float oAngle = preferences.getFloat("opponent" + i + "Angle");
            opponents[i] = new AI(oRobustness, oSpeed, oAcceleration, oManeuverability, oBoatType, map.lanes[i + 1], oCurrentSpeed, oStamina);
            opponents[i].createBoatBody(map.world, oPosX, oPosY, "Boat1.json");
            opponents[i].boatSprite.setRotation(oAngle);
        }

        //Check that the boat isn't starting inside the obstacle.
        map.stepWorld(player, opponents);
        assertEquals(120f, player.robustness);

        //Move the boat forward for a while.
        for (int i = 0; i <= 60*5; i++) {
            map.stepWorld(player, opponents);
            player.moveBoat();
        }
        System.out.println("Boat is at x: " + player.boatBody.getPosition().x + " y: " + player.boatBody.getPosition().y);
        if (map.lanes[0].obstacles[0].obstacleBody != null) {
            System.out.println("Obstacle is at x: " + map.lanes[0].obstacles[0].obstacleBody.getPosition().x + " y: " + map.lanes[0].obstacles[0].obstacleBody.getPosition().y);
        }
        assertNotEquals(120f, player.robustness);
    }

}