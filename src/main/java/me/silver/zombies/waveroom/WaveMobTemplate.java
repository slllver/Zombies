package me.silver.zombies.waveroom;

import me.silver.zombies.mob.MineZombie;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class WaveMobTemplate {

//    private T mobType;

    public static HashMap<String, WaveMobTemplate> templates = new HashMap<>();

    private World world;
    private Inventory inventory;
    private boolean isBaby;
    private double health;
    private double speed;
    private double attackDamage;

    public WaveMobTemplate(World world, Inventory inventory, boolean isBaby, double health, double speed, double attackDamage) {
        this.world = world;
        this.inventory = cloneInventory(inventory);
        this.isBaby = isBaby;
        this.health = health;
        this.speed = speed;
        this.attackDamage = attackDamage;
    }

    @SuppressWarnings("UnusedReturnValue")
    public MineZombie spawnMob(double x, double y, double z) {
        MineZombie zombie = new MineZombie(world);
        zombie.setup(x, y, z, isBaby, health, speed, attackDamage, cloneInventory(inventory));

        return zombie;
    }

    private static Inventory cloneInventory(Inventory inventory) {
        if (inventory == null) return null;

        Inventory newInventory = Bukkit.createInventory(null, inventory.getSize());

        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null) {
                newInventory.addItem(new ItemStack(itemStack));
            }
        }

        return newInventory;
    }

}
