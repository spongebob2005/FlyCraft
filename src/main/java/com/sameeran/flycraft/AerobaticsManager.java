package com.sameeran.flycraft;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Manages aerobatic maneuvers and stunt tracking.
 * Detects barrel rolls, loops, and other maneuvers; applies speed boosts on success.
 */
public class AerobaticsManager {

    // Stunt tracking
    private static int barrelRollTicks = 0;
    private static int loopTicks = 0;
    private static int stuntCooldown = 0;
    private static double stuntMultiplier = 1.0;

    private static final double ROLL_THRESHOLD = 0.05;      // rotation speed for roll detection
    private static final double LOOP_VERTICAL_THRESHOLD = 0.8; // upward momentum for loop
    private static final int STUNT_COOLDOWN_TICKS = 60;    // cooldown between stunts
    private static final double STUNT_SPEED_BOOST = 1.3;   // speed multiplier after stunt

    /**
     * Update stunt detection each tick.
     */
    public static void updateStunts(LocalPlayer player) {
        if (!player.isFallFlying() || !ClientEvents.isJetMode()) {
            resetStunts();
            return;
        }

        Vec3 currentVel = player.getDeltaMovement();
        double speed = currentVel.length();

        // Detect barrel roll (lateral rotation)
        Vec3 lateralVel = currentVel.multiply(1, 0, 1).normalize();
        Vec3 look = player.getLookAngle();
        Vec3 lookLateral = look.multiply(1, 0, 1).normalize();
        
        double rollAngle = Math.acos(Math.max(-1, Math.min(1, lateralVel.dot(lookLateral))));
        if (rollAngle > ROLL_THRESHOLD && speed > 1.5) {
            barrelRollTicks++;
            if (barrelRollTicks > 20) { // Stunt completion threshold
                onStuntSuccess("barrel_roll", player);
                barrelRollTicks = 0;
            }
        } else {
            barrelRollTicks = Math.max(0, barrelRollTicks - 2);
        }

        // Detect loop (upward then downward movement)
        double verticalComponent = currentVel.y;
        if (verticalComponent > LOOP_VERTICAL_THRESHOLD && speed > 2.0) {
            loopTicks++;
            if (loopTicks > 40) { // Longer stunt requirement
                onStuntSuccess("loop", player);
                loopTicks = 0;
            }
        } else {
            loopTicks = Math.max(0, loopTicks - 3);
        }

        // Manage cooldown
        if (stuntCooldown > 0) {
            stuntCooldown--;
            // Decay speed multiplier
            stuntMultiplier = Math.max(1.0, stuntMultiplier - 0.02);
        }
    }
    /**
     * Called when a stunt is successfully completed.
     */
    private static void onStuntSuccess(String stuntType, LocalPlayer player) {
        stuntCooldown = STUNT_COOLDOWN_TICKS;
        stuntMultiplier = STUNT_SPEED_BOOST;

        // Play feedback sound
        player.level().playSound(
                player,
                player.blockPosition(),
                net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,
                net.minecraft.sounds.SoundSource.PLAYERS,
                0.8f,
                1.5f
        );

        // Particle burst for visual feedback
        for (int i = 0; i < 8; i++) {
            double angle = (Math.PI * 2 * i) / 8;
            double vx = Math.cos(angle) * 0.15;
            double vz = Math.sin(angle) * 0.15;
            player.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.END_ROD,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    vx, 0.1, vz
            );
        }

        // Track stat
        StatsManager.recordStunt(stuntType);
    }

    /**
     * Get the current stunt speed multiplier.
     */
    public static double getStuntMultiplier() {
        return stuntMultiplier;
    }

    /**
     * Get remaining stunt cooldown ticks.
     */
    public static int getStuntCooldown() {
        return stuntCooldown;
    }

    /**
     * Get barrel roll progress (0-100).
     */
    public static int getBarrelRollProgress() {
        return Math.min(100, (barrelRollTicks * 100) / 20);
    }

    /**
     * Get loop progress (0-100).
     */
    public static int getLoopProgress() {
        return Math.min(100, (loopTicks * 100) / 40);
    }

    private static void resetStunts() {
        barrelRollTicks = 0;
        loopTicks = 0;
        stuntMultiplier = 1.0;
    }
}
