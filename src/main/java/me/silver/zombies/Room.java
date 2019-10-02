package me.silver.zombies;

import me.silver.zombies.mob.MineZombie;
import me.silver.zombies.waveroom.WaveMobTemplate;

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
        this.cornerOne = cornerOne;
        this.cornerTwo = cornerTwo;

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
