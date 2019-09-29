package me.silver.zombies.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.silver.zombies.Room;
import me.silver.zombies.mob.MineZombie;
import me.silver.zombies.mob.MineZombiePigman;
import me.silver.zombies.util.Pair;
import me.silver.zombies.waveroom.WaveMobTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Rename command class to reduce ambiguity between WaveRoom.java and Room.java
@CommandAlias("wr")
public class WaveRoomCommand extends BaseCommand {

    // Might be used again with individual corner setting command
//    private static HashMap<Player, Location[]> corners = new HashMap<>();
    private static HashMap<String, Room> rooms = new HashMap<>();

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
    public static void spawn(CommandSender sender, String roomName, int count, @Optional String queryString) {
        Room room = rooms.get(roomName);

        if (room != null) {

            if (queryString == null) {
                for (int i = 0; i < count; i++) {
                    room.spawnZombie(null);
                }
            } else if (WaveMobTemplate.templates.containsKey(queryString)) {
                for (int i = 0; i < count; i++) {
                    room.spawnZombie(WaveMobTemplate.templates.get(queryString));
                }
            } else {
                List<Pair<Integer, String>> numbers = new ArrayList<>();
                List<Pair<Integer, String>> percents = new ArrayList<>();

                int totalPercent = 0;
                int mobsToSpawn = 0;

                Pattern pattern = Pattern.compile("^(\\d+)(%?)([A-Za-z]+)?$");

                for (String s : queryString.split(",")) {
                    Matcher matcher = pattern.matcher(s);

                    if (matcher.find()) {
                        int number = Integer.parseInt(matcher.group(1));
                        String template = matcher.group(3);

                        if (matcher.group(2).contains("%")) {
                            percents.add(new Pair<>(number, template));
                            totalPercent += number;
                        } else {
                            numbers.add(new Pair<>(number, template));
                            mobsToSpawn += number;
                        }
                    } else {
                        sender.sendMessage("Error: '" + s + "' is not a valid query");
                        return;
                    }
                }

                if ((mobsToSpawn != count && totalPercent != 100) || mobsToSpawn > count) {
                    sender.sendMessage("Error: Spawn count doesn't add up");
                    return;
                } else {
                    for (Pair<Integer, String> pair : numbers) {
                        for (int i = 0; i < pair.getLeft(); i++) {
                            room.spawnZombie(WaveMobTemplate.templates.get(pair.getRight()));
                        }
                    }

                    int remaining = (count - mobsToSpawn);

                    for (Pair<Integer, String> pair : percents) {
                        for (int i = 0; i < remaining * ((double)pair.getLeft() / 100d); i++) {
                            room.spawnZombie(WaveMobTemplate.templates.get(pair.getRight()));
                        }
                    }
                }
            }

            sender.sendMessage("Spawned zombie(s)");
        } else {
            sender.sendMessage("Error: Couldn't find room " + roomName);
        }
    }



    @Subcommand("template")
    @Syntax("[templateName] [mobType] [x] [y] [z] [isBaby] [health] [speed] [damage] <options>")
    @Description("Creates a custom mob template with an inventory at the given location")
    public static void createTemplate(CommandSender sender, String id, String mobType, int x, int y, int z, boolean isBaby, double health, double speed, double attackDamage, @Optional String... options) {
        World world;

        if (sender instanceof Player) {
            world = ((Player)sender).getWorld();
        } else if (sender instanceof BlockCommandSender) {
            world = ((BlockCommandSender)sender).getBlock().getWorld();
        } else {
            world = Bukkit.getWorld("world");
        }

        Block target = world.getBlockAt(x, y, z);

        if (target.getType().equals(Material.CHEST)) {
            Chest chest = (Chest) target.getState();
            sender.sendMessage(createTemplate(id, mobType, isBaby, health, speed, attackDamage, chest.getInventory(), options));
        } else {
            sender.sendMessage("Error: target block must be a chest/trapped chest");
        }
    }

    // TODO: Update so that options are processed by the mob when spawned rather than the command
    @Subcommand("template")
    @Syntax("[templateName] [mobType] [isBaby] [health] [speed] [damage] <options>")
    @Description("Creates a custom mob template")
    public static void createTemplate(CommandSender sender, String id, String mobType, boolean isBaby, double health, double speed, double attackDamage, @Optional String... options) {
        sender.sendMessage(createTemplate(id, mobType, isBaby, health, speed, attackDamage, (Object) options));
    }

    // Having this return a string is kind of a dumb way to do this, but oh well
    private static String createTemplate(String id, String mobType, boolean isBaby, double health, double speed, double attackDamage, Object... options) {
        if (WaveMobTemplate.templates.containsKey(id)) {
            return "Error: template already exists with name: " + id;
        }

        WaveMobTemplate template;

        switch (mobType.toLowerCase()) {
            case "z":
            case "zombie":
            case "mine_zombie":
            case "minezombie":
                template = new WaveMobTemplate<>(MineZombie.class, isBaby, health, speed, attackDamage, options);
                break;
            case "p":
            case "pigzombie":
            case "mine_zombie_pigman":
            case "minezombiepigman":
            case "zombiepigman":
                template = new WaveMobTemplate<>(MineZombiePigman.class, isBaby, health, speed, attackDamage, options);
                break;
            default:
                return "Error: could not infer mob type from '" + mobType + "'";
        }

        WaveMobTemplate.templates.put(id, template);
        return "Successfully created mob template with name: " + id;
    }

    @Subcommand("template")
    public static void removeTemplate(CommandSender sender, String templateName) {
        WaveMobTemplate.templates.remove(templateName);

        sender.sendMessage("Removed template '" + templateName + "'");
    }

    @CatchUnknown
    public static void onUnknownCommand(CommandSender sender, String[] args) {
        if (args.length > 0) {
            String subCommand = args[0];

            if (subCommand.toLowerCase().equals("template")) {
                String helpMessage = "Usage: \n" +
                        "/waveroom template [templateName] [mobType] [isBaby] [health] [speed] [damage] <options>\n" +
                        "/waveroom template [templateName] [mobType] [x] [y] [z] [isBaby] [health] [speed] [damage]\n" +
                        "/waveroom remove [templateName]";

                sender.sendMessage(helpMessage);
            } else {
                sender.sendMessage("Unknown command: /waveroom " + args[0]);
            }
        }
    }

}
