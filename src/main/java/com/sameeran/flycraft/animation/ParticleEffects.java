package com.sameeran.flycraft.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

/**
 * Handles particle effects for animations
 */
public class ParticleEffects {
    private static final Minecraft MC = Minecraft.getInstance();

    /**
     * Create a burst of particles around the player
     */
    public static void burstEffect(LocalPlayer player, int count) {
        Vec3 pos = player.position();
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI * i) / count;
            double x = pos.x + Math.cos(angle) * 0.5;
            double z = pos.z + Math.sin(angle) * 0.5;
            MC.particleEngine.createParticle(ParticleTypes.EXPLOSION,
                    x, pos.y + 1, z,
                    Math.cos(angle) * 0.3, 0.1, Math.sin(angle) * 0.3);
        }
    }

    /**
     * Create a spiral of particles
     */
    public static void spiralEffect(LocalPlayer player, int count) {
        Vec3 pos = player.position();
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI * i) / count;
            double height = (double) i / count;
            double x = pos.x + Math.cos(angle) * (0.2 + height * 0.3);
            double y = pos.y + 1 + height;
            double z = pos.z + Math.sin(angle) * (0.2 + height * 0.3);
            MC.particleEngine.createParticle(ParticleTypes.MYCELIUM,
                    x, y, z,
                    Math.cos(angle) * 0.1, 0, Math.sin(angle) * 0.1);
        }
    }

    /**
     * Create trailing particles
     */
    public static void trailEffect(LocalPlayer player) {
        Vec3 pos = player.position();
        MC.particleEngine.createParticle(ParticleTypes.FLAME,
                pos.x, pos.y, pos.z,
                0, -0.05, 0);
    }

    /**
     * Create dust cloud effect
     */
    public static void dustEffect(LocalPlayer player, int count) {
        Vec3 pos = player.position();
        for (int i = 0; i < count; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double x = pos.x + Math.cos(angle) * Math.random() * 0.5;
            double y = pos.y + Math.random();
            double z = pos.z + Math.sin(angle) * Math.random() * 0.5;
            MC.particleEngine.createParticle(ParticleTypes.CLOUD,
                    x, y, z,
                    Math.cos(angle) * 0.2 * Math.random(),
                    0.05,
                    Math.sin(angle) * 0.2 * Math.random());
        }
    }

    /**
     * Create sparkle effect
     */
    public static void sparkleEffect(LocalPlayer player, int count) {
        Vec3 pos = player.position();
        for (int i = 0; i < count; i++) {
            double x = pos.x + (Math.random() - 0.5) * 1.5;
            double y = pos.y + 0.5 + Math.random() * 1.5;
            double z = pos.z + (Math.random() - 0.5) * 1.5;
            MC.particleEngine.createParticle(ParticleTypes.END_ROD,
                    x, y, z,
                    (Math.random() - 0.5) * 0.2,
                    (Math.random() - 0.5) * 0.2,
                    (Math.random() - 0.5) * 0.2);
        }
    }

    /**
     * Create wave effect expanding from player
     */
    public static void waveEffect(LocalPlayer player) {
        Vec3 pos = player.position();
        int count = 20;
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI * i) / count;
            double x = pos.x + Math.cos(angle);
            double z = pos.z + Math.sin(angle);
            MC.particleEngine.createParticle(ParticleTypes.LARGE_SMOKE,
                    x, pos.y + 0.5, z,
                    Math.cos(angle) * 0.3, 0, Math.sin(angle) * 0.3);
        }
    }
}
