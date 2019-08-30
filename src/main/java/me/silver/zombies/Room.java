package me.silver.zombies;

import me.silver.zombies.mob.MineZombie;
import me.silver.zombies.mob.iCustomMob;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.EntityZombie;
import org.bukkit.Location;
import org.bukkit.Material;
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

    public void spawnZombie() {
        spawnZombie(1);
    }

//    public void spawnZombie(Class<? extends iCustomMob> clazz) {
    public void spawnZombie(int amount) {
        net.minecraft.server.v1_12_R1.World nmsWorld = ((CraftWorld)world).getHandle();

        for (int i = 0; i < amount; i++) {
            int x = (int)Math.min(cornerOne.getX(), cornerTwo.getX()) + random.nextInt((int)Math.abs(cornerOne.getX() - cornerTwo.getX()));
            int z = (int)Math.min(cornerOne.getZ(), cornerTwo.getZ()) + random.nextInt((int)Math.abs(cornerOne.getZ() - cornerTwo.getZ()));

            int airCount = 0;

            for (int y = (int)Math.min(cornerOne.getY(), cornerTwo.getY()); y < (int)Math.max(cornerOne.getY(),cornerTwo.getY()); y++) {
                Block block = world.getBlockAt(x, y, z);

                switch (block.getType()) {
//                case AIR:
//                    if (airCount < 2) {
//                        airCount++;
//                    } else {
////                        try {
////                            EntityZombie zombie = clazz.newInstance();
////
////                            zombie.prepare(nmsWorld.D(new BlockPosition(zombie)), null);
////                            zombie.setLocation(x + 0.5, y - 1, z + 0.5, 0, 0);
////                            nmsWorld.addEntity(zombie);
////                        } catch (InstantiationException | IllegalAccessException e) {
////                            e.printStackTrace();
////                        }
//
//                        MineZombie zombie = new MineZombie(nmsWorld);
//
//                        zombie.prepare(nmsWorld.D(new BlockPosition(zombie)), null);
//                        zombie.setLocation(x + 0.5, y - 1, z + 0.5, 0, 0);
//                        nmsWorld.addEntity(zombie);
//
//                        return;
//                    }
//                    break;
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
                                MineZombie zombie = new MineZombie(nmsWorld);

                                zombie.prepare(nmsWorld.D(new BlockPosition(zombie)), null);
                                zombie.setLocation(x + 0.5, y - 1, z + 0.5, 0, 0);
                                nmsWorld.addEntity(zombie);

                                return;
                            }
                        } else {
                            airCount = 0;
                        }
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
