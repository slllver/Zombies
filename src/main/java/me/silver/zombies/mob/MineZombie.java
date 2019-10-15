package me.silver.zombies.mob;

import me.silver.zombies.util.EquipmentUtils;
import me.silver.zombies.mob.ai.PathfinderGoalTargetBySpeed;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class MineZombie extends EntityZombie implements iCustomMob {

    public Inventory inventory;
    public boolean isBaby;

    public MineZombie(World world) {
        super(world);
    }

    public MineZombie(World world, Inventory inventory, boolean isBaby) {
        this(world);

        this.inventory = inventory;
        this.isBaby = isBaby;
    }

    @Override
    public void setup(double x, double y, double z, boolean isBaby, double health, double speed, double attackDamage, Object... objects) {
        this.inventory = (objects.length > 0 && objects[0] instanceof Inventory) ? EquipmentUtils.cloneInventory((Inventory) objects[0]) : null;
        this.isBaby = isBaby;

        this.prepare(this.world.D(new BlockPosition(this)), null);
        this.setPosition(x, y, z);
        this.setAttributes(health, speed, attackDamage);
        this.world.addEntity(this);
    }

    // This method will likely never be used again
    public static MineZombie spawn(World world, double x, double y, double z, Inventory inventory, boolean isBaby, double health, double speed, double attackDamage) {
        MineZombie zombie = new MineZombie(world, inventory, isBaby);

        zombie.prepare(world.D(new BlockPosition(zombie)), null);
        zombie.setLocation(x, y, z, 0, 0);
        zombie.setAttributes(health, speed, attackDamage);
        world.addEntity(zombie);

        return zombie;
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

        // Clear any armor/weapons that the zombie may have spawned with
        // aO() is the obf name for getHeldEquipment()
        ((NonNullList)this.aO()).clear();
        ((NonNullList)this.getArmorItems()).clear();

        // Attempt to equip best armor/weapon in the given inventory
        if (this.inventory != null) {
            HashMap<EnumItemSlot, ItemStack> items = new HashMap<>();
            HashMap<EnumItemSlot, Integer> values = new HashMap<>();

            for (ItemStack itemStack : inventory.getContents()) {
                if (itemStack != null) {
                    EnumItemSlot slot = EquipmentUtils.getSlot(itemStack.getType());

                    if (slot != null) {
                        int tier = EquipmentUtils.getTier(itemStack.getType());

                        if (values.containsKey(slot)) {
                            if (tier > values.get(slot)) {
                                values.replace(slot, tier);
                                items.replace(slot, itemStack);
                            }
                        } else {
                            values.put(slot, tier);
                            items.put(slot, itemStack);
                        }
                    }
                }
            }

            for (EnumItemSlot slot : items.keySet()) {
                this.setSlot(slot, CraftItemStack.asNMSCopy(items.get(slot)));
//                inventory.remove(items.get(slot));
            }

            // Tell zombie thing not to drop its equipment because it's still in the inventory
            this.dropChanceArmor = new float[]{0F, 0F, 0F, 0F};
            this.dropChanceHand[EnumItemSlot.MAINHAND.b()] = 0F;
        }

        // Set to adult and remove mount
        // bJ() returns the entity that the current Zombie is riding
        this.setBaby(isBaby);
        Entity mount = this.bJ();

        if (mount != null) {
            this.world.removeEntity(mount);
        }

        return gde;
    }

    public void setEquipmentDropChance(float chance) {
        if (chance > 1.0) {
            chance = 1F;
        } else if (chance < 0) {
            chance = 0F;
        }

        this.dropChanceArmor = new float[]{chance, chance, chance, chance};
        this.dropChanceHand[EnumItemSlot.MAINHAND.b()] = chance;
    }

    // Drop all items on death
    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);

        org.bukkit.World world = this.getWorld().getWorld();
        Location location = new Location(world, this.locX, this.locY, this.locZ);

        if (inventory != null) {
            for (ItemStack itemStack : inventory.getContents()) {
                if (itemStack != null) {
                    world.dropItemNaturally(location, itemStack);
                }
            }
        }
    }

    // Prevent damage taken if the source is an exploding pigman
    @Override
    public boolean damageEntity(DamageSource damageSource, float v) {
        return (!damageSource.translationIndex.equals("explosion.pigman")) && super.damageEntity(damageSource, v);
    }

    // Set mob attributes
    private void setAttributes(double health, double speed, double attackDamage) {
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(health);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(speed);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(attackDamage);
        this.getAttributeInstance(a).setValue(0D); // Zombie reinforcements chance
    }

    // Prevent experience orb drops
    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        return 0;
    }

    // Disable burning in daylight
    @Override
    public boolean p() {
        return false;
    }

    // Prevent default drops from loot table?
    // TODO: Figure out custom loot tables
    @Override
    protected MinecraftKey J() {
        return null;
    }

}
