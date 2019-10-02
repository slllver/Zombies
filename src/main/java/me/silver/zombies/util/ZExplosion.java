package me.silver.zombies.util;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.EnderPearl;

import java.util.HashMap;
import java.util.List;

public class ZExplosion {

    private final World world;
    private final Entity sourceEntity;
    private final double x;
    private final double y;
    private final double z;
    private final double radius;
    private final double baseDamage;
    private final double xzKnockback;
    private final double yKnockback;

    public HashMap<Entity, Float> blockDensities = new HashMap<>();

    public ZExplosion(World world, Entity sourceEntity, double x, double y, double z, double radius, double baseDamage, double knockback) {
        this(world, sourceEntity, x, y, z, radius, baseDamage, knockback, knockback);
    }

    public ZExplosion(World world, Entity sourceEntity, double x, double y, double z, double radius, double baseDamage, double xzKnockback, double yKnockback) {
        this.world = world;
        this.sourceEntity = sourceEntity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.baseDamage = baseDamage;
        this.xzKnockback = xzKnockback;
        this.yKnockback = yKnockback;
    }

    public void damageEntities(String description) {
        int xMin = (int)(this.x - this.radius);
        int yMin = (int)(this.y - this.radius);
        int zMin = (int)(this.z - this.radius);
        int xMax = (int)(this.x + this.radius);
        int yMax = (int)(this.y + this.radius);
        int zMax = (int)(this.z + this.radius);
        List<Entity> nearbyEntities = this.world.getEntities(sourceEntity, new AxisAlignedBB(xMin, yMin, zMin, xMax, yMax, zMax));

        for (Entity entity : nearbyEntities) {
            if (!entity.bB() && (entity instanceof EntityLiving || entity instanceof EntityFallingBlock)) {
                double distance = entity.e(this.x, this.y, this.z) / radius;

                if (distance <= 1.0) {
                    distance = 1 - distance;

                    float blockDensity = this.world.a(new Vec3D(this.x, this.y, this.z), entity.getBoundingBox());
                    float damage = (float) (baseDamage * distance * (blockDensity + 1) / 2);
                    Bukkit.getLogger().info(baseDamage + " " + damage + " " + blockDensity);

                    boolean wasDamaged = entity.damageEntity(new EntityDamageSource(description, sourceEntity), damage);

                    if (wasDamaged) {
                        double kbX = entity.locX - this.x;
                        double kbY = entity.locY + entity.getHeadHeight() - this.y;
                        double kbZ = entity.locZ - this.z;
                        double eyeDistance = Math.sqrt(kbX * kbX + kbY * kbY + kbZ * kbZ);

                        kbX /= eyeDistance;
                        kbY /= eyeDistance;
                        kbZ /= eyeDistance;

                        // I kind of lost track of what I was doing by this point
                        entity.motX += kbX * distance * this.xzKnockback;
                        entity.motY += kbY * distance * this.yKnockback;
                        entity.motZ += kbZ * distance * this.xzKnockback;
                    }
                }
            }
        }
    }

    public void createParticlesAndEvent(float volume, float pitch, int explodeType) {
        this.world.a(null, this.x, this.y, this.z, SoundEffects.bV, SoundCategory.BLOCKS, volume, pitch);
//        this.world.addParticle(EnumParticle.EXPLOSION_HUGE, this.x, this.y, this.z, 1, 0, 0);
        org.bukkit.World bWorld = this.world.getWorld();

        if (explodeType == 1) {
            // Do pigman explosion
            bWorld.spawnParticle(Particle.FLAME, this.x, this.y, this.z, 250, 0, 0, 0, 0.5);
            bWorld.spawnParticle(Particle.EXPLOSION_NORMAL, this.x, this.y, this.z, 100, 0, 0, 0, 0.5);
            bWorld.spawnParticle(Particle.LAVA, this.x, this.y, this.z, 50, 0, 0, 0, 3);
            bWorld.spawnParticle(Particle.EXPLOSION_LARGE, this.x, this.y, this.z, 10, 0, 0, 0);
        } else {
            bWorld.spawnParticle(Particle.EXPLOSION_HUGE, this.x, this.y, this.z, 1, 0, 0, 0);
        }

        // TODO: Call event on explosion
    }
}
