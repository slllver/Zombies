package me.silver.zombies.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import me.silver.zombies.Room;
import me.silver.zombies.waveroom.WaveMobTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

// TODO: Rename command class to reduce ambiguity between WaveRoom.java and Room.java
@CommandAlias("wr")
public class WaveRoomCommand extends BaseCommand {

    private static HashMap<Player, Location[]> corners = new HashMap<>();
    private static HashMap<String, Room> rooms = new HashMap<>();
    private static HashMap<String, WaveMobTemplate> templates = new HashMap<>();

    @Default
    @Subcommand("create")
    public static void onCreate(CommandSender sender, String name, int x, int y, int z, int x2, int y2, int z2) {
        if (rooms.containsKey(name)) {
            sender.sendMessage("Error: Room with name \"" + name + "\" already exists");
        } else {
            World world;

            if (sender instanceof Player) {
                Player player = (Player)sender;
                world = player.getWorld();
            } else {
                // TODO: Also get world from commandblock/command minecart senders
                world = Bukkit.getServer().getWorld("world");
            }

            Location cornerOne = new Location(world, x, y, z);
            Location cornerTwo = new Location(world, x2, y2, z2);
            Room room = new Room(cornerOne, cornerTwo);

            rooms.put(name, room);
            sender.sendMessage("Successfully created room " + name);
        }

    }

    @Subcommand("remove")
    public static void onRemove(CommandSender sender, String roomName) {
        rooms.remove(roomName);

        sender.sendMessage("Successfully removed room " + roomName);
    }

    @Subcommand("spawn")
    public static void spawn(CommandSender sender, String roomName, int count) {
        Room room = rooms.get(roomName);

        if (room != null) {

            for (int i = 0; i < count; i++) {
                room.spawnZombie(null);
            }

            sender.sendMessage("Spawned zombie(s)");
        } else {
            sender.sendMessage("Couldn't find room " + roomName);
        }
    }

    @Subcommand("st")
    public static void spawn(CommandSender sender, String roomName, String templateName, int count) {
        Room room = rooms.get(roomName);
        WaveMobTemplate template = templates.get(templateName);

        if (room != null && template != null) {

            for (int i = 0; i < count; i++) {
                room.spawnZombie(template);
            }

            sender.sendMessage("Spawned zombie(s)");
        } else {
            sender.sendMessage("Couldn't find room " + roomName);
        }
    }

    @Subcommand("template")
    public static void createTemplate(CommandSender sender, String id, int x, int y, int z, boolean isBaby, double health, double speed, double attackDamage) {
        World world;

        if (templates.containsKey(id)) {
            sender.sendMessage("Error: template already exists with name: " + id);
            return;
        }

        if (sender instanceof Player) {
            world = ((Player)sender).getWorld();
        } else if (sender instanceof BlockCommandSender) {
            world = ((BlockCommandSender)sender).getBlock().getWorld();
        } else {
            world = Bukkit.getWorld("world");
        }

        Block target = world.getBlockAt(x, y, z);

        if (target.getType().equals(Material.CHEST)) {
            Chest chest = (Chest)target.getState();

            templates.put(id, new WaveMobTemplate(((CraftWorld)world).getHandle(), chest.getInventory(), isBaby, health, speed, attackDamage));
            sender.sendMessage("Successfully created mob template with name: " + id);
        } else {
            sender.sendMessage("Error: target block must be a chest/trapped chest");
        }
    }

}
