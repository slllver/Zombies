package me.silver.zombies;

import co.aikar.commands.PaperCommandManager;
import me.silver.zombies.command.CreateZombie;
import me.silver.zombies.command.Explode;
import me.silver.zombies.command.WaveRoomCommand;
import me.silver.zombies.listener.PlayerListener;
import me.silver.zombies.mob.MineZombie;
import me.silver.zombies.mob.MineZombiePigman;
import me.silver.zombies.util.NMSUtils;
import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Zombies extends JavaPlugin {

    private static Zombies plugin;

    @Override
    public void onEnable() {
        plugin = this;

        this.getCommand("createzombie").setExecutor(new CreateZombie());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        registerEntities();
        registerCommands();
    }

    private void registerEntities() {
        NMSUtils.registerEntity("mine_zombie", NMSUtils.Type.ZOMBIE, MineZombie.class, false);
        NMSUtils.registerEntity("mine_zombie_pigman", NMSUtils.Type.PIG_ZOMBIE, MineZombiePigman.class, false);
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new WaveRoomCommand());
        manager.registerCommand(new Explode());
    }

    public static void sendActionBar(Player player, String message) {
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, ChatMessageType.GAME_INFO);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(ppoc);
    }

    public static Zombies getInstance() {
        return plugin;
    }
}
