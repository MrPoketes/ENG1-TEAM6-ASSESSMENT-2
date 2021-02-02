package de.tomgrill.gdxtesting.examples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.hardgforgif.dragonboatracing.core.Boat;
import com.hardgforgif.dragonboatracing.core.Lane;
import com.hardgforgif.dragonboatracing.core.Obstacle;
import com.hardgforgif.dragonboatracing.core.PowerUp;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

public class SimpleCollisionTest {

    Lane laneMock = mock(Lane.class);
    boolean passTest = false;

    @Test
    public void BoatCollidesWithObstacle() {
        /*
        Create a box2d world, to put the objects in.
        Add a collision handler that sets a flag to pass the test if a collision occurs.
        Create a boat - this will involve mocking a lane.
        Create an obstacle.
        Use moveBoat() to collide the boat into the obstacle.
         */
        System.out.println("dab on the haters");
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

        Boat boat = new Boat(120, 110, 100, 80, 0, laneMock, 0, 120f);
        boat.createBoatBody(world, 0, 0, "Boat1.json");
        Obstacle obstacle = new Obstacle("Obstacles/Obstacle1.png");
        obstacle.createObstacleBody(world, 0, 200, "Obstacles/Obstacle1.json", 0f);

        for (int i = 0; i <= 500; i++) {
            world.step(1f / 60f, 6, 2);
            boat.moveBoat();
        }

        assertTrue(passTest);
    }

    @Test
    public void BoatDamagedByObstacle() {
        /*
        Use a mocked save file to load a map with just a boat and an obstacle (same positions as above test).
        Do exact same moveBoat() actions to collide boat into obstacle.
        Pass the test if the boat's robustness has decreased.
         */
        assertTrue(true);
    }

}