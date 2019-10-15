package me.silver.zombies.mob;

import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class WaveMobTemplate<T extends iCustomMob> {

    private Class<T> mobType;

    private boolean isBaby;
    private double health;
    private double speed;
    private double attackDamage;
    private Object[] parameters;

    public static HashMap<String, WaveMobTemplate> templates = new HashMap<>();

    public WaveMobTemplate(Class<T> mobType, boolean isBaby, double health, double speed, double attackDamage, Object... parameters) {
        this.mobType = mobType;
        this.isBaby = isBaby;
        this.health = health;
        this.speed = speed;
        this.attackDamage = attackDamage;
        this.parameters = parameters;
    }

    @SuppressWarnings("UnusedReturnValue")
    public iCustomMob spawnMob(Location location) {
        iCustomMob zombie;
        World world = ((CraftWorld)location.getWorld()).getHandle();

        try {
            Constructor constructor = mobType.getConstructor(World.class);
            zombie = (iCustomMob)constructor.newInstance(world);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        zombie.setup(location.getX(), location.getY(), location.getZ(), isBaby, health, speed, attackDamage, parameters);

        return zombie;
    }

}
