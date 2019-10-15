package me.silver.zombies.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.silver.zombies.util.ZExplosion;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.Vec3D;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;

@CommandAlias("explode")
@CommandPermission("zombies.command")
public class Explode extends BaseCommand {

    @Default
    @Subcommand("e")
    @CommandPermission("zombies.command.explode")
    public static void explode(Player sender, Float size) {
        World world = ((CraftWorld)sender.getWorld()).getHandle();
        Location location = sender.getLocation();

        world.createExplosion(null, location.getX(), location.getY(), location.getZ(), size, false, false);
    }

    @Subcommand("z")
    @CommandPermission("zombies.command.explode")
    public static void zExplode(Player sender, Float radius, float damage, float xzkb, float ykb) {
        World world = ((CraftWorld)sender.getWorld()).getHandle();
        Location location = sender.getLocation();

        ZExplosion explosion = new ZExplosion(world, null, location.getX(), location.getY(), location.getZ(), radius, damage, xzkb, ykb);
        explosion.damageEntities("explosion.pigman");
        explosion.createParticlesAndEvent(2f, 0.7f, 0);

//        for (Entity entity : explosion.blockDensities.keySet()) {
//            Bukkit.getLogger().info(String.valueOf(explosion.blockDensities.get(entity)));
//        }
    }
}
