package me.silver.zombies.command;

import me.silver.zombies.Zombies;
import me.silver.zombies.mob.MineZombie;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.EntityZombie;
import net.minecraft.server.v1_12_R1.GenericAttributes;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CreateZombie implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Block targetBlock = player.getTargetBlock(null, 10);

            if (targetBlock.getType().equals(Material.CHEST) || targetBlock.getType().equals(Material.TRAPPED_CHEST)) {
                Chest chest = (Chest) targetBlock.getState();
                Inventory inventory = Bukkit.createInventory(null, chest.getInventory().getSize());

                for (ItemStack itemStack : chest.getInventory().getContents()) {
                    if (itemStack != null) {
                        inventory.addItem(new ItemStack(itemStack));
                        itemStack.setAmount(0);
                    }
                }

                targetBlock.setType(Material.AIR);
                World world = ((CraftWorld) player.getWorld()).getHandle();

                MineZombie zombie = MineZombie.spawn(world, targetBlock.getX() + 0.5, targetBlock.getY(),
                        targetBlock.getZ() + 0.5, inventory, false, 20, 0.23, 3);
//
//                EntityZombie zombie = new EntityZombie(world);
//                world.addEntity(zombie);
//                zombie.setLocation(targetBlock.getX() + 0.5, targetBlock.getY(), targetBlock.getZ() + 0.5, 0, 0);

                zombie.getAttributeInstance(GenericAttributes.maxHealth).setValue(2);

                player.sendMessage("Successfully created custom Zombie!");
                Zombies.getInstance().getLogger().info("Successfully created MineZ Zombie at " + zombie.locX + " " + zombie.locY + " " + zombie.locZ);

                return true;
            }
        }

        return false;
    }
}
