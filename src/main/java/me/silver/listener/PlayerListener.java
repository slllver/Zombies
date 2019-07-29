package me.silver.listener;


import me.silver.Zombies;
import me.silver.util.PlayerSpeed;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;

public class PlayerListener implements Listener {

    private static Zombies plugin = Zombies.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            PlayerSpeed playerSpeed = PlayerSpeed.getPlayerSpeed(player.getName());
            playerSpeed.setSpeed(playerSpeed.getSpeed().setY(0.0));

            Zombies.sendActionBar(player, new DecimalFormat("#.###").format(playerSpeed.getSpeed().length()));
        }, 0, 1);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Vector from = event.getFrom().toVector();
        Vector to = event.getTo().toVector();
        Vector speed = to.subtract(from);

        // Set the player's speed to 0 after 5 ticks if the player hasn't moved
        int removeSpeedTask = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (PlayerSpeed.containsPlayer(player.getName())) {
                PlayerSpeed playerSpeed = PlayerSpeed.getPlayerSpeed(player.getName());
                playerSpeed.setSpeed(new Vector(0, 0, 0));
            }
        }, 5L);

        if (PlayerSpeed.containsPlayer(player.getName())) {
            PlayerSpeed playerSpeed = PlayerSpeed.getPlayerSpeed(player.getName());

            if (plugin.getServer().getScheduler().isQueued(playerSpeed.getTaskId())) {
                plugin.getServer().getScheduler().cancelTask(playerSpeed.getTaskId());
            }

            playerSpeed.setSpeed(speed);
            playerSpeed.setTaskId(removeSpeedTask);

//            Vector notY = new Vector(speed.getX(), 0, speed.getZ());
//            if (notY.length() > 0) {
//                plugin.getLogger().info(new DecimalFormat("#.###").format(notY.length()));
//            }
        } else {
            PlayerSpeed.add(new PlayerSpeed(player.getName(), speed, removeSpeedTask));
        }

    }
}
