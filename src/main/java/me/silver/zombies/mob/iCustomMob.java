package me.silver.zombies.mob;

import org.bukkit.inventory.Inventory;

public interface iCustomMob {

    void setup(double x, double y, double z, Inventory inventory, boolean isBaby, double health, double speed, double attackDamage);
}
