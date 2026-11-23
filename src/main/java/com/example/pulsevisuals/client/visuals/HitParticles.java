package com.example.pulsevisuals.client.visuals;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;

import java.util.Random;

public final class HitParticles {

    private HitParticles() {}

    private static final Random RANDOM = new Random();

    public static void spawnBurst(ClientWorld world, double x, double y, double z, boolean critical) {
        int count = critical ? 18 : 10;

        for (int i = 0; i < count; i++) {
            double angle = RANDOM.nextDouble() * Math.PI * 2;
            double speed = 0.08 + RANDOM.nextDouble() * 0.12;
            double vx = Math.cos(angle) * speed;
            double vz = Math.sin(angle) * speed;
            double vy = 0.05 + RANDOM.nextDouble() * 0.12;

            world.addParticle(ParticleTypes.GLOW, x, y, z, vx, vy, vz);
        }

        for (int i = 0; i < 6; i++) {
            double ox = (RANDOM.nextDouble() - 0.5) * 0.4;
            double oy = RANDOM.nextDouble() * 0.4;
            double oz = (RANDOM.nextDouble() - 0.5) * 0.4;
            world.addParticle(ParticleTypes.CRIT, x + ox, y + oy, z + oz, 0, 0.05, 0);
        }

        for (int i = 0; i < 4; i++) {
            double ox = (RANDOM.nextDouble() - 0.5) * 0.3;
            double oz = (RANDOM.nextDouble() - 0.5) * 0.3;
            world.addParticle(ParticleTypes.DAMAGE_INDICATOR, x + ox, y + 0.2, z + oz, 0, 0.1, 0);
        }

        if (critical) {
            for (int i = 0; i < 3; i++) {
                double ox = (RANDOM.nextDouble() - 0.5) * 0.3;
                double oz = (RANDOM.nextDouble() - 0.5) * 0.3;
                world.addParticle(ParticleTypes.HEART, x + ox, y + 0.3, z + oz, 0, 0.05, 0);
            }
        }
    }
}
