package com.hardgforgif.dragonboatracing.tests.GameTests;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.hardgforgif.dragonboatracing.GameData;
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
public class CollisionTest extends TestBase {

    Lane laneMock = mock(Lane.class);
    boolean passTest = false;

    @Test
    public void BoatCollidesWithObstacle(){
        /*
        Preliminary test that ensures the object *is* actually being collided with, and any test failures for the
        subsequent tests are due to collision logic not working as intended.

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
    public void TEST_UR_DAMAGE() {
        /*
        Use a mocked save file to load a map with just a boat and an obstacle (same positions as preliminary test).
        Do exact same moveBoat() actions to collide boat into obstacle.
        Pass the test if the boat's robustness has decreased, and the obstacle is removed.
         */
        Object[] objects = loadSave("singleObstacle");
        Map map = (Map) objects[0];
        Player player = (Player) objects[1];
        AI[] opponents = (AI[]) objects[2];

        //Move the boat into the object.
        for (int i = 0; i <= 60*5; i++) {
            map.stepWorld(player, opponents);
            player.moveBoat();
        }
        System.out.println("Boat is at x: " + player.boatBody.getPosition().x + " y: " + player.boatBody.getPosition().y);
        if (map.lanes[0].obstacles[0].obstacleBody != null) {
            System.out.println("Obstacle is at x: " + map.lanes[0].obstacles[0].obstacleBody.getPosition().x + " y: " + map.lanes[0].obstacles[0].obstacleBody.getPosition().y);
            //Test fails as obstacle should be destroyed.
            assertEquals(map.lanes[0].obstacles[0].obstacleBody, null);
        }
        assertTrue(120f > player.robustness);
    }

    @Test
    public void TEST_FR_POWER_UP_ITEMS_HEALTH() {
        /*
        Use a mocked save file to load a map with just a boat and a health powerup (same positions as preliminary test).
        Do exact same moveBoat() actions to collide boat into powerup.
        Pass the test if the boat's robustness has increased, and the powerup is removed.
         */
        Object[] objects = loadSave("singleHealthPowerup");
        Map map = (Map) objects[0];
        Player player = (Player) objects[1];
        AI[] opponents = (AI[]) objects[2];

        //Move the boat into the object.
        for (int i = 0; i <= 60*5; i++) {
            map.stepWorld(player, opponents);
            player.moveBoat();
        }
        System.out.println("Boat is at x: " + player.boatBody.getPosition().x + " y: " + player.boatBody.getPosition().y);
        if (map.lanes[0].powerUps[0].powerupBody != null) {
            System.out.println("Powerup is at x: " + map.lanes[0].obstacles[0].obstacleBody.getPosition().x + " y: " + map.lanes[0].obstacles[0].obstacleBody.getPosition().y);
            //Test fails as powerup should be destroyed.
            assertEquals(map.lanes[0].powerUps[0].powerupBody, null);
        }
        assertEquals(140f, player.robustness);
    }

    @Test
    public void TEST_FR_POWER_UP_ITEMS_STAMINA() {
        /*
        Use a mocked save file to load a map with just a boat and a stamina powerup (same positions as preliminary test).
        Do exact same moveBoat() actions to collide boat into powerup.
        Every step, if the boat's stamina increases, check the powerup has been removed. If so, pass the test.
        If the boat does not collide with the boost, or the powerup isn't removed on collision, fail the test.
         */
        Object[] objects = loadSave("singleStaminaPowerup");
        Map map = (Map) objects[0];
        Player player = (Player) objects[1];
        AI[] opponents = (AI[]) objects[2];

        //Move the boat into the object.
        for (int i = 0; i <= 60*5; i++) {
            float previousStamina = player.stamina;
            map.stepWorld(player, opponents);
            player.moveBoat();
            // On the step the player's stamina increases:
            if (previousStamina < player.stamina) {
                System.out.println("Boat is at x: " + player.boatBody.getPosition().x + " y: " + player.boatBody.getPosition().y);
                if (map.lanes[0].powerUps[0].powerupBody != null) {
                    System.out.println("Powerup is at x: " + map.lanes[0].obstacles[0].obstacleBody.getPosition().x + " y: " + map.lanes[0].obstacles[0].obstacleBody.getPosition().y);
                    //Test fails as powerup should be destroyed.
                    assertEquals(map.lanes[0].powerUps[0].powerupBody, null);
                }
                //Test passes as the boat's stamina was increased and the body was destroyed.
                assertTrue(true);
                return;
            }
        }
        // The boat's stamina never increased, fail the test.
        assertTrue(false);
    }

    @Test
    public void TEST_FR_POWER_UP_ITEMS_SPEED() {
        /*
        Use a mocked save file to load a map with just a boat and a speed powerup (same positions as preliminary test).
        Do exact same moveBoat() actions to collide boat into powerup.
        Pass the test if the boat's speed has increased, and the powerup is removed.
         */
        Object[] objects = loadSave("singleSpeedPowerup");
        Map map = (Map) objects[0];
        Player player = (Player) objects[1];
        AI[] opponents = (AI[]) objects[2];

        //Move the boat into the object.
        for (int i = 0; i <= 60*5; i++) {
            map.stepWorld(player, opponents);
            player.moveBoat();
        }
        System.out.println("Boat is at x: " + player.boatBody.getPosition().x + " y: " + player.boatBody.getPosition().y);
        if (map.lanes[0].powerUps[0].powerupBody != null) {
            System.out.println("Powerup is at x: " + map.lanes[0].obstacles[0].obstacleBody.getPosition().x + " y: " + map.lanes[0].obstacles[0].obstacleBody.getPosition().y);
            //Test fails as powerup should be destroyed.
            assertEquals(map.lanes[0].powerUps[0].powerupBody, null);
        }
        assertEquals(120f, player.speed);
    }

    @Ignore
    @Test
    public void TEST_FR_POWER_UP_ITEMS_ACCELERATION() {
        /*
        Use a mocked save file to load a map with just a boat and an acceleration powerup (same positions as preliminary test).
        Do exact same moveBoat() actions to collide boat into powerup.
        First check that the boat's speed and acceleration have increased, and the powerup is removed.
        Then progress the scheduler time by 15 seconds to
         */
        Object[] objects = loadSave("singleAccelerationPowerup");
        Map map = (Map) objects[0];
        Player player = (Player) objects[1];
        AI[] opponents = (AI[]) objects[2];

        //Move the boat into the object.
        for (int i = 0; i <= 60*5; i++) {
            map.stepWorld(player, opponents);
            player.moveBoat();
        }
        System.out.println("Boat is at x: " + player.boatBody.getPosition().x + " y: " + player.boatBody.getPosition().y);
        if (map.lanes[0].powerUps[0].powerupBody != null) {
            System.out.println("Powerup is at x: " + map.lanes[0].obstacles[0].obstacleBody.getPosition().x + " y: " + map.lanes[0].obstacles[0].obstacleBody.getPosition().y);
            //Test fails as powerup should be destroyed.
            assertEquals(map.lanes[0].powerUps[0].powerupBody, null);
        }
        assertEquals(140f, player.speed);
        assertEquals(150f, player.acceleration);

        //wait 15 seconds somehow

        assertEquals(110f, player.speed);
        assertEquals(100f, player.acceleration);
    }

    @Test
    public void TEST_FR_POWER_UP_ITEMS_TIME() {
        /*
        Use a mocked save file to load a map with just a boat and a time reduction powerup (same positions as preliminary test).
        Do exact same moveBoat() actions to collide boat into powerup.
        Pass the test if the boat's speed has increased, and the powerup is removed.
         */
        Object[] objects = loadSave("singleTimePowerup");
        Map map = (Map) objects[0];
        Player player = (Player) objects[1];
        AI[] opponents = (AI[]) objects[2];

        //Move the boat into the object.
        for (int i = 0; i <= 60*5; i++) {
            map.stepWorld(player, opponents);
            player.moveBoat();
        }
        System.out.println("Boat is at x: " + player.boatBody.getPosition().x + " y: " + player.boatBody.getPosition().y);
        if (map.lanes[0].powerUps[0].powerupBody != null) {
            System.out.println("Powerup is at x: " + map.lanes[0].obstacles[0].obstacleBody.getPosition().x + " y: " + map.lanes[0].obstacles[0].obstacleBody.getPosition().y);
            //Test fails as powerup should be destroyed.
            assertEquals(map.lanes[0].powerUps[0].powerupBody, null);
        }
        assertEquals(2f, GameData.timeReductions[0]);
    }

}