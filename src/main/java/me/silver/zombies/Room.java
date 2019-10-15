package me.silver.zombies;

import me.silver.zombies.mob.MineZombie;
import me.silver.zombies.mob.WaveMobTemplate;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.util.Vector;

import java.util.Random;

public class Room {

    private World world;

    private Vector cornerOne;
    private Vector cornerTwo;

    private Random random;

    public Room(Location cornerOne, Location cornerTwo) {
        this(cornerOne.getWorld(), cornerOne.toVector(), cornerTwo.toVector());
    }

    public Room(World world, Vector cornerOne, Vector cornerTwo) {
        this.world = world;

        double maxX;
        double minX;
        double maxY;
        double minY;
        double maxZ;
        double minZ;

        if (cornerOne.getX() >= cornerTwo.getX()) {
            maxX = cornerOne.getX();
            minX = cornerTwo.getX();
        } else {
            maxX = cornerTwo.getX();
            minX = cornerOne.getX();
        }

        if (cornerOne.getY() >= cornerTwo.getY()) {
            maxY = cornerOne.getY();
            minY = cornerTwo.getY();
        } else {
            maxY = cornerTwo.getY();
            minY = cornerOne.getY();
        }

        if (cornerOne.getZ() >= cornerTwo.getZ()) {
            maxZ = cornerOne.getZ();
            minZ = cornerTwo.getZ();
        } else {
            maxZ = cornerTwo.getZ();
            minZ = cornerOne.getZ();
        }

        this.cornerOne = new Vector(maxX, maxY, maxZ);
        this.cornerTwo = new Vector(minX, minY, minZ);

        this.random = new Random();

        // TODO: Verify that room doesn't overlap with other rooms?
    }

//    public void spawnZombie(Class<? extends iCustomMob> clazz) {
    public void spawnZombie(WaveMobTemplate template) {
        net.minecraft.server.v1_12_R1.World nmsWorld = ((CraftWorld)world).getHandle();

        int x = (int)Math.min(cornerOne.getX(), cornerTwo.getX()) + random.nextInt((int)Math.abs(cornerOne.getX() - cornerTwo.getX()));
        int z = (int)Math.min(cornerOne.getZ(), cornerTwo.getZ()) + random.nextInt((int)Math.abs(cornerOne.getZ() - cornerTwo.getZ()));

        int airCount = 0;

        for (int y = (int)Math.min(cornerOne.getY(), cornerTwo.getY()); y < (int)Math.max(cornerOne.getY(),cornerTwo.getY()); y++) {
            Block block = world.getBlockAt(x, y, z);

            switch (block.getType()) {
                case FENCE:
                case NETHER_FENCE:
                case FENCE_GATE:
                case COBBLE_WALL:
                    airCount = -1;
                    break;
                default:
                    if (!block.getType().isSolid()) {
                        if (airCount < 2) {
                            airCount++;
                        } else {
                            if (template != null) {
                                template.spawnMob(new Location(world, x + 0.5, y - 1, z + 0.5));
                            } else {
                                MineZombie.spawn(nmsWorld, x + 0.5, y - 1, z + 0.5, null, false, 20, 0.23, 3);
                            }

                            return;
                        }
                    } else {
                        airCount = 0;
                    }
            }
        }

    }

    public boolean isIntersecting(Room otherRoom) {
        return this.cornerOne.getX() < otherRoom.cornerTwo.getX()
                && this.cornerTwo.getX() > otherRoom.cornerOne.getX()
                && this.cornerOne.getZ() < otherRoom.cornerTwo.getZ()
                && this.cornerTwo.getZ() > otherRoom.cornerOne.getZ();
    }

    public World getWorld() {
        return world;
    }
}
