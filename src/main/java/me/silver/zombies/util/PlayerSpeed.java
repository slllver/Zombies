package me.silver.zombies.util;

import me.silver.zombies.Zombies;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class PlayerSpeed {

    private static Zombies plugin = Zombies.getInstance();

    private String name;
    private Vector speed;
    private int taskId;

    private static ArrayList<PlayerSpeed> playerSpeeds = new ArrayList<>();

    public PlayerSpeed(String name, Vector speed, int taskId) {
        this.name = name;
        this.speed = speed;
        this.taskId = taskId;
    }

    public static void add(PlayerSpeed playerSpeed) {
        if (!containsPlayer(playerSpeed.getPlayer())) {
            playerSpeeds.add(playerSpeed);
        }
    }

    public static Boolean containsPlayer(String playerName) {
        for (PlayerSpeed playerSpeed : playerSpeeds) {
            if (playerSpeed.getPlayer().equals(playerName)) {
                return true;
            }
        }

        return false;
    }

    public static PlayerSpeed getPlayerSpeed(String playerName) {
        for (PlayerSpeed playerSpeed : playerSpeeds) {
            if (playerSpeed.getPlayer().equals(playerName)) {
                return playerSpeed;
            }
        }

        return new PlayerSpeed(playerName, new Vector(0, 0, 0), -1);
    }

    public double length() {
        return Math.sqrt(this.speed.getX() * this.speed.getX()  + this.speed.getZ() * this.speed.getZ());
    }

    public String getPlayer() {
        return name;
    }

    public Vector getSpeed() {
        return speed;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setSpeed(Vector speed) {
        this.speed = speed;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public String toString() {
        return name + ", <" + speed.toString() + ">, " + taskId;
    }
}

