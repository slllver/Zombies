package me.silver.zombies.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.silver.zombies.waveroom.RoomGenerator;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("gen")
public class DungeonGenerator extends BaseCommand {

    @Default
    @Subcommand("rooms")
    public static void generateRooms(Player player, int xRadius, int zRadius, int count) {
        Location location = player.getLocation();

        RoomGenerator.generateRooms(location, xRadius, zRadius, count);
    }
}
