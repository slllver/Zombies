package me.silver.zombies.event;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

public class ZExplodeEvent extends EntityExplodeEvent {
    public ZExplodeEvent(Entity source, Location location) {
        super(source, location, null, 0);
    }
}
