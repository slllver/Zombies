package me.silver.zombies.util;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.Objects;

public class PathfinderGoalTargetBySpeed<T extends EntityLiving> extends PathfinderGoalTarget {

    private final Class<T> targetEntityType;
    private EntityLiving targetEntity;

    public PathfinderGoalTargetBySpeed(EntityCreature entityCreature, Class<T> targetEntityType) {
        this(entityCreature, targetEntityType, false);
    }

    public PathfinderGoalTargetBySpeed(EntityCreature entitycreature, Class<T> targetEntityType, boolean flag) {
        this(entitycreature, targetEntityType, flag, false);
    }

    private PathfinderGoalTargetBySpeed(EntityCreature entitycreature, Class<T> targetEntityType, boolean flag, boolean flag1) {
        super(entitycreature, flag, flag1);

        this.targetEntityType = targetEntityType;
    }

    @Override
    public boolean a() {
        if (this.targetEntityType == EntityHuman.class || this.targetEntityType == EntityPlayer.class) {
            EntityHuman entityHuman = this.e.world.a(this.e.locX, (double) this.e.getHeadHeight(), this.e.locZ, this.i(), this.i(), targetPlayer -> {
                double modifier = 1.0D;

                // Reduce visibility if player is wearing a zombie head
                // ItemStack.i() returns the item damage value, with 2 being the value for zombie head
                ItemStack helmet = Objects.requireNonNull(targetPlayer).getEquipment(EnumItemSlot.HEAD);
                modifier *= (PathfinderGoalTargetBySpeed.this.e instanceof EntityZombie && helmet.getItem() == Items.SKULL
                    && helmet.i() == 2) ? 0.5D : 1.0D;

                // Undo reduced visibility caused by player sneaking
                if (targetPlayer.isSneaking()) modifier /= 0.8D;
//                if (targetPlayer.isSilent());

                return modifier;
            }, null);

            if (entityHuman != null) {
                Player player = (Player)entityHuman.getBukkitEntity();
                PlayerSpeed playerSpeed = PlayerSpeed.getPlayerSpeed(player.getName());
                double speed = playerSpeed.getSpeed().length();

                if (speed > 0.1) {
                    this.targetEntity = entityHuman;

                    return true;
                }
            }
        }

        return false;
    }


    @Override
    public void c() {
        this.e.setGoalTarget(this.targetEntity, EntityTargetEvent.TargetReason.CUSTOM, true);
        super.c();
    }

}
