package me.silver.zombies.waveroom;

import me.silver.zombies.Room;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Random;

public class RoomGenerator {

    public static void generateRooms(Location origin, int xRadius, int zRadius, int count) {
        ArrayList<Room> generatedRooms = new ArrayList<>();

        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < count; i++) {
            int originX = random.nextInt(xRadius * 2 + 1) - xRadius;
            int originZ = random.nextInt(zRadius * 2 + 1) - zRadius;

            int sizeX = random.nextInt(5) + 5;
            int sizeZ = random.nextInt(5) + 5;

//            Bukkit.getLogger().info( originX + " " + originZ + " " + sizeX + " " + sizeZ);

            Location cornerOne = new Location(origin.getWorld(), origin.getX() + originX + sizeX, origin.getY(), origin.getZ() + originZ + sizeZ);
            Location cornerTwo = new Location(origin.getWorld(), origin.getX() + originX - sizeX, origin.getY(), origin.getZ() + originZ - sizeZ);

//            Bukkit.getLogger().info(cornerOne.toString() + " |||| " + cornerTwo.toString());

            for (int x = cornerTwo.getBlockX(); x < cornerOne.getBlockX(); x++) {
                for (int z = cornerTwo.getBlockZ(); z < cornerOne.getBlockZ(); z++) {
//                    Bukkit.getLogger().info(x + " " + z);
                    origin.getWorld().getBlockAt(x, origin.getBlockY(), z).setType(Material.WOOD);
                }
            }
        }
    }
}
