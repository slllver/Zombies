package me.silver.zombies.mob;

import me.silver.zombies.util.ZExplosion;
import me.silver.zombies.waveroom.WaveMobTemplate;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;

public class MineZombiePigman extends EntityPigZombie implements iCustomMob {

    private WaveMobTemplate zombie;

    public MineZombiePigman(World world) {
        super(world);
    }

    @Override
    public void setup(double x, double y, double z, boolean isBaby, double health, double speed, double attackDamage, Object... options) {

        if (options.length > 0 && options[0] instanceof String) {
            String template = (String) options[0];

            if (WaveMobTemplate.templates.containsKey(template)) {
                this.zombie = WaveMobTemplate.templates.get(template);
            }
        }

//        this.prepare(this.world.D(new BlockPosition(this)), null);
        this.setPosition(x, y, z);
        this.world.addEntity(this);
        this.setAttributes(health, speed, attackDamage);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);

        //TODO: Modify death behavior (explosion, zombie spawning, etc) based on isBaby value
        ZExplosion explosion = new ZExplosion(this.world, this, this.locX, this.locY, this.locZ, 5, 6, 5, 1.4);
        explosion.damageEntities("explosion.pigman");
        explosion.createParticlesAndEvent(2, 0.7f);

        for (int i = 0; i < 4; i++) {
            this.zombie.spawnMob(new Location(this.world.getWorld(), this.locX, this.locY, this.locZ));
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
}
