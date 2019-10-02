package me.silver.zombies.util;

import net.minecraft.server.v1_12_R1.EnumItemSlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EquipmentUtils {

    public static int getTier(Material material) {
        int space = material.name().indexOf('_');

        if (space == -1) {
            return -1;
        }

        String type = material.name().substring(0, space);

        switch (type.toLowerCase()) {
            case "wood":
            case "gold":
            case "leather":
                return 0;
            case "stone":
            case "chainmail":
                return 1;
            case "iron":
                return 2;
            case "diamond":
                return 3;
            default:
                return -1;
        }
    }

    public static EnumItemSlot getSlot(Material material) {
        String name = material.name().toLowerCase();

        if (name.contains("helmet")) {
            return EnumItemSlot.HEAD;
        } else if (name.contains("chestplate")) {
            return EnumItemSlot.CHEST;
        } else if (name.contains("leggings")) {
            return EnumItemSlot.LEGS;
        } else if (name.contains("boots")) {
            return EnumItemSlot.FEET;
        } else if (name.contains("sword")) {
            return EnumItemSlot.MAINHAND;
        }

        return null;
    }

    public static Inventory cloneInventory(Inventory inventory) {
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
