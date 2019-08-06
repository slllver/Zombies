package me.silver.zombies.util;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.util.Vector;

public class PathfinderGoalNearestAttackableInRange<T extends EntityLiving> extends PathfinderGoalTarget {

    private final Class<T> targetEntityType;
    private EntityHuman targetEntity;


    public PathfinderGoalNearestAttackableInRange(EntityCreature entityCreature, Class<T> targetEntityType, boolean shouldCheckSight) {
        this(entityCreature, targetEntityType, shouldCheckSight, false);
    }

    public PathfinderGoalNearestAttackableInRange(EntityCreature entityCreature, Class<T> targetEntityType, boolean shouldCheckSight, boolean nearbyOnly) {
        super(entityCreature, shouldCheckSight, nearbyOnly);

        this.targetEntityType = targetEntityType;
    }

    @Override
    public boolean a() {
        if (this.targetEntityType == EntityHuman.class || this.targetEntityType == EntityPlayer.class) {
            EntityHuman entityHuman = this.e.world.b(this.e, 5.0);

            if (entityHuman != null) {
                this.targetEntity = entityHuman;

                return true;
            }
        }
        return false;
    }

    @Override
    public boolean b() {
        Vector currentEntityLocation = new Vector(this.e.locX, this.e.locY, this.e.locZ);
        Vector targetLocation = new Vector(this.targetEntity.locX, this.e.locY, this.targetEntity.locZ);
        return !(currentEntityLocation.distance(targetLocation) > 8.0);
    }

    @Override
    public void c() {
        this.e.setGoalTarget(this.targetEntity, EntityTargetEvent.TargetReason.CUSTOM, true);
        super.c();
    }
}
