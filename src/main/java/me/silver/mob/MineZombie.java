package me.silver.mob;

import me.silver.util.PathfinderGoalTargetBySpeed;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;

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
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(2, new PathfinderGoalZombieAttack(this, 1.0D, false));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.a(2, new PathfinderGoalTargetBySpeed<>(this, EntityHuman.class, true));
    }

    // Additional code called on spawn (setting gear, spawning as baby/with chicken, etc)
    @Override
    public GroupDataEntity prepare(DifficultyDamageScaler dds, GroupDataEntity gde) {
        gde = super.prepare(dds, gde);

        if (this.inventory != null) {
            // Set inventory to that one
        }

        // Set to adult and remove mount
        // bJ() returns the entity that the current Zombie is riding
        this.setBaby(false);
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
