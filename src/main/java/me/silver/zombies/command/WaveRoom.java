package me.silver.zombies.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.silver.zombies.Room;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

@CommandAlias("wr")
public class WaveRoom extends BaseCommand {

    private static HashMap<Player, Location[]> corners = new HashMap<>();
    private static HashMap<String, Room> rooms = new HashMap<>();

    @Default
    @Subcommand("create")
    public static void onCreate(CommandSender sender, String name) {
        if (sender instanceof Player) {
            Player player = (Player)sender;

            if (corners.containsKey(player)) {
                Location[] locations = corners.get(player);

                if (locations[0].getWorld() != locations[1].getWorld()) {
                    player.sendMessage("Error: Both points must be in the same world");
                    return;
                }

                if (rooms.containsKey(name)) {
                    player.sendMessage("Error: Room with name \"" + name + "\" already exists");
                } else if (locations[0] != null && locations[1] != null) {
                    Room room = new Room(locations[0], locations[1]);

                    rooms.put(name, room);
                    player.sendMessage("idk whatever " + name);
                }
            }
        }
    }

    // Totally not ripped off from World Edit
    @Subcommand("pos1")
    public static void setLeft(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player)sender;

            if (!corners.containsKey(player)) {
                corners.put(player, new Location[2]);
            }

            corners.get(player)[0] = player.getLocation();

            player.sendMessage("Set first point to " + player.getLocation().toString());
        } else {
            sender.sendMessage("Error: Command must be sent by a player");
        }
    }

    @Subcommand("pos2")
    public static void setRight(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player)sender;

            if (!corners.containsKey(player)) {
                corners.put(player, new Location[2]);
            }

            corners.get(player)[1] = player.getLocation();
            player.sendMessage("Set second point to " + player.getLocation().toString());
        } else {
            sender.sendMessage("Error: Command must be sent by a player");
        }
    }

}
