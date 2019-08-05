package me.silver;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Random;

public class Room {

    private World world;

    private Vector cornerOne;
    private Vector cornerTwo;

    private Random random;

    public Room(World world, Vector cornerOne, Vector cornerTwo) {
        this.world = world;
        this.cornerOne = cornerOne;
        this.cornerTwo = cornerTwo;

        this.random = new Random();

        // TODO: Verify that room doesn't overlap with other rooms?
    }



    public World getWorld() {
        return world;
    }

    public Vector getCornerOne() {
        return cornerOne;
    }

    public Vector getCornerTwo() {
        return cornerTwo;
    }
}
