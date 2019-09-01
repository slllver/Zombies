package me.silver.zombies.mob;

import net.minecraft.server.v1_12_R1.World;
import org.bukkit.inventory.Inventory;

public interface iCustomMob {

    iCustomMob setup(double x, double y, double z, Inventory inventory, boolean isBaby, double health, double speed, double attackDamage);
}
