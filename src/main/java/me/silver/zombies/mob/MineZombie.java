package me.silver.zombies.mob;

import me.silver.zombies.Zombies;
import me.silver.zombies.util.EquipmentUtils;
import me.silver.zombies.util.PathfinderGoalTargetBySpeed;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

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

    private void log(String message) {
        Zombies.getInstance().getLogger().info(message);
    }

    // Additional code called on spawn (setting gear, spawning as baby/with chicken, etc)
    @Override
    public GroupDataEntity prepare(DifficultyDamageScaler dds, GroupDataEntity gde) {
        gde = super.prepare(dds, gde);

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
            }

            // Tell zombie thing not to drop its equipment because it's still in the inventory
            this.dropChanceArmor = new float[]{0F, 0F, 0F, 0F};
            this.dropChanceHand[EnumItemSlot.MAINHAND.b()] = 0F;
        }

//        ItemStack itemStack = new ItemStack(org.bukkit.Material.CHAINMAIL_HELMET);
//        this.setSlot(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(itemStack));
//
//        System.out.println("Made it here");


        // Set to adult and remove mount
        // bJ() returns the entity that the current Zombie is riding
        this.setBaby(false);
        Entity mount = this.bJ();

        if (mount != null) {
            this.world.removeEntity(mount);
        }

        return gde;
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

//    private void equipItems(String type) {
//        ItemStack itemToEquip = null;
//        int highestValue = -1;
//
//        for (ItemStack itemStack : inventory.getContents()) {
//            String materialName = itemStack.getType().toString();
//            org.bukkit.Material material = itemStack.getType();
//
//            if (materialName.contains(type)) {
//                int value = EquipmentUtils.getTier(material);
//
//                if (value > highestValue) {
//                    highestValue = value;
//                    itemToEquip = itemStack;
//                }
//            }
//        }
//
//        EnumItemSlot slot = EquipmentUtils.getSlot(type);
//
//        if (itemToEquip != null && slot != null) {
//            this.setSlot(slot, CraftItemStack.asNMSCopy(itemToEquip));
//        }
//
//    }

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

    // Prevent default drops from loot table?
    // TODO: Figure out custom loot tables
    @Override
    protected MinecraftKey J() {
        return null;
    }
}
