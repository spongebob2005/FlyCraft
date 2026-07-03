package com.sameeran.flycraft;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Detects barrel rolls and loops; grants a temporary speed boost on success.
 */
public class AerobaticsManager {

    private static int    barrelRollTicks  = 0;
    private static int    loopTicks        = 0;
    private static int    stuntCooldown    = 0;
    private static double stuntMultiplier  = 1.0;

    private static final double ROLL_THRESHOLD          = 0.05;
    private static final double LOOP_VERTICAL_THRESHOLD = 0.8;
    private static final int    STUNT_COOLDOWN_TICKS    = 60;
    private static final double STUNT_SPEED_BOOST       = 1.3;

    public static void updateStunts(LocalPlayer player) {
        if (!player.isFallFlying() || !ClientEvents.isJetMode()) {
            resetStunts();
            return;
        }

        Vec3 vel   = player.getDeltaMovement();
        double spd = vel.length();

        // Barrel roll detection
        Vec3 latVel  = vel.multiply(1, 0, 1);
        Vec3 latLook = player.getLookAngle().multiply(1, 0, 1);
        if (latVel.lengthSqr() > 1e-6 && latLook.lengthSqr() > 1e-6) {
            double dot  = latVel.normalize().dot(latLook.normalize());
            double roll = Math.acos(Math.max(-1, Math.min(1, dot)));
            if (roll > ROLL_THRESHOLD && spd > 1.5) {
                if (++barrelRollTicks > 20) { onStuntSuccess("barrel_roll", player); barrelRollTicks = 0; }
            } else {
                barrelRollTicks = Math.max(0, barrelRollTicks - 2);
            }
        }

        // Loop detection
        if (vel.y > LOOP_VERTICAL_THRESHOLD && spd > 2.0) {
            if (++loopTicks > 40) { onStuntSuccess("loop", player); loopTicks = 0; }
        } else {
            loopTicks = Math.max(0, loopTicks - 3);
        }

        if (stuntCooldown > 0) {
            stuntCooldown--;
            stuntMultiplier = Math.max(1.0, stuntMultiplier - 0.02);
        }
    }

    private static void onStuntSuccess(String type, LocalPlayer player) {
        stuntCooldown   = STUNT_COOLDOWN_TICKS;
        stuntMultiplier = STUNT_SPEED_BOOST;

        player.level().playSound(player, player.blockPosition(),
            net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,
            net.minecraft.sounds.SoundSource.PLAYERS, 0.8f, 1.5f);

        for (int i = 0; i < 8; i++) {
            double a = (Math.PI * 2 * i) / 8;
            player.level().addParticle(net.minecraft.core.particles.ParticleTypes.END_ROD,
                player.getX(), player.getY() + 0.5, player.getZ(),
                Math.cos(a) * 0.15, 0.1, Math.sin(a) * 0.15);
        }
        StatsManager.recordStunt(type);
        FlyCraftMod.LOGGER.debug("Stunt: {}", type);
    }

    public static double getStuntMultiplier()    { return stuntMultiplier; }
    public static int    getStuntCooldown()      { return stuntCooldown; }
    public static int    getBarrelRollProgress() { return Math.min(100, (barrelRollTicks * 100) / 20); }
    public static int    getLoopProgress()       { return Math.min(100, (loopTicks * 100) / 40); }

    private static void resetStunts() {
        barrelRollTicks = 0;
        loopTicks       = 0;
        stuntMultiplier = 1.0;
        stuntCooldown   = 0; // FIX: was never reset, causing ghost cooldowns after landing
    }
}
