package me.silver.zombies.mob;

import me.silver.zombies.mob.ai.PathfinderGoalNearestAttackableInRange;
import me.silver.zombies.util.ZExplosion;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class MineZombiePigman extends EntityPigZombie implements iCustomMob {

    private WaveMobTemplate zombie;
    private double[] explosionParams;

    public MineZombiePigman(World world) {
        super(world);
    }

    @Override
    public void setup(double x, double y, double z, boolean isBaby, double health, double speed, double attackDamage, Object... options) {

        if (options.length > 0) {
            if (options[0] instanceof WaveMobTemplate) {
                zombie = (WaveMobTemplate) options[0];
            } else if (options[0] instanceof String) {
                String template = (String) options[0];

                if (WaveMobTemplate.templates.containsKey(template)) {
                    this.zombie = WaveMobTemplate.templates.get(template);
                }
            }

            // I'm almost sure there's a better way to do this
            if (options.length > 1) {
                double[] explosionParams = new double[] {5, 6, 5, 1.4};

                for (int i = 1; i < options.length; i++) {
                    if (options[i] instanceof Double) {
                        explosionParams[i - 1] = (Double) options[i];
                    } else if (options[i] instanceof String) {
                        try {
                            double value = Double.parseDouble((String) options[i]);
                            explosionParams[i - 1] = value;
                        } catch (NumberFormatException e) {
                            Bukkit.getLogger().info("Couldn't get number from String \"" + options[i] + ",\" using default value");
                        }
                    }
                }

                this.explosionParams = explosionParams;
            }
        }

//        this.prepare(this.world.D(new BlockPosition(this)), null);
        this.setBaby(isBaby);
        this.setPosition(x, y, z);
        this.world.addEntity(this);
        this.setAttributes(health, speed, attackDamage);
    }

    @Override
    protected void do_() {
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableInRange<>(this, EntityHuman.class, true, false));
    }

    //TODO: Hide the body?
    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);

        ZExplosion explosion;
        if (this.explosionParams != null) {
            explosion = new ZExplosion(this.world, this, this.locX, this.locY, this.locZ,
                    explosionParams[0], explosionParams[1], explosionParams[2], explosionParams[3]);
        } else {
            if (this.isBaby()) {
                explosion = new ZExplosion(this.world, this, this.locX, this.locY, this.locZ, 5, 6, 5, 1.4);
            } else {
                explosion = new ZExplosion(this.world, this, this.locX, this.locY, this.locZ, 6, 8, 7, 2);
            }
        }

        explosion.damageEntities("explosion.pigman");
        explosion.createParticlesAndEvent(2, 0.7f, 1);

        if (!this.isBaby() && this.zombie != null) {
            for (int i = 0; i < 4; i++) {
                this.zombie.spawnMob(new Location(this.world.getWorld(), this.locX, this.locY, this.locZ));
            }
        }
    }

    // Probably done
    @Override
    public boolean damageEntity(DamageSource damageSource, float v) {
        return (!damageSource.translationIndex.equals("explosion.pigman")) && super.damageEntity(damageSource, v);
    }

    // Probably done
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

    // Prevent default drops from loot table?
    // TODO: Figure out custom loot tables
    @Override
    protected MinecraftKey J() {
        return null;
    }

    // Prevent whatever that is
    @Override
    protected void M() {

    }
}
