package me.silver;

import org.bukkit.plugin.java.JavaPlugin;

public class Zombies extends JavaPlugin {

    private static Zombies plugin;

    @Override
    public void onEnable() {
        plugin = this;
    }

    public static Zombies getInstance() {
        return plugin;
    }
}
