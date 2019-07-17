package me.silver.mob;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.inventory.Inventory;

public class MineZombie extends EntityZombie {

    private Inventory inventory;

    public MineZombie(World world) {
        super(world);
    }

    public MineZombie(World world, Inventory inventory) {
        this(world);

        this.inventory = inventory;
    }

    // Initialize AI tasks
    @Override
    protected void r() {

    }

    // Additional code called on spawn (setting gear, spawning as baby/with chicken, etc)
    @Override
    public GroupDataEntity prepare(DifficultyDamageScaler dds, GroupDataEntity gde) {
        gde = super.prepare(dds, gde);

        // Set to adult and remove mount
        // bJ() returns the entity that the current Zombie is riding
//        this.setBaby(false);
        Entity mount = this.bJ();

        if (mount != null) {
            this.world.removeEntity(mount);
        }

        return gde;
    }

    // Set mob attributes
    @Override
    protected void initAttributes() {
        super.initAttributes();

    }

    // Disable burning in daylight
    @Override
    public boolean p() {
        return false;
    }
}
