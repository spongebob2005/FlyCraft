package com.sameeran.flycraft;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Tracks flight statistics: distance, max speed, stunts, tricks, etc.
 * Data can be reset or exported for leaderboards.
 */
public class StatsManager {

    private static double totalDistance = 0.0;
    private static double maxSpeed = 0.0;
    private static double maxAltitude = 0.0;
    private static long totalFlightTime = 0;
    private static int barrelRollCount = 0;
    private static int loopCount = 0;
    private static Vec3 lastPosition = Vec3.ZERO;

    /**
     * Update stats each tick.
     */
    public static void updateStats(LocalPlayer player) {
        if (!player.isFallFlying() || !ClientEvents.isJetMode()) {
            return;
        }

        Vec3 currentPos = player.position();
        Vec3 currentVel = player.getDeltaMovement();
        double speed = currentVel.length();

        // Track distance
        if (!lastPosition.equals(Vec3.ZERO)) {
            totalDistance += currentPos.distanceTo(lastPosition);
        }
        lastPosition = currentPos;

        // Track max speed
        if (speed > maxSpeed) {
            maxSpeed = speed;
        }

        // Track max altitude
        if (player.getY() > maxAltitude) {
            maxAltitude = player.getY();
        }

        // Track flight time
        totalFlightTime++;
    }

    /**
     * Record a stunt completion.
     */
    public static void recordStunt(String stuntType) {
        if ("barrel_roll".equals(stuntType)) {
            barrelRollCount++;
        } else if ("loop".equals(stuntType)) {
            loopCount++;
        }
    }

    /**
     * Reset all stats.
     */
    public static void resetStats() {
        totalDistance = 0.0;
        maxSpeed = 0.0;
        maxAltitude = 0.0;
        totalFlightTime = 0;
        barrelRollCount = 0;
        loopCount = 0;
        lastPosition = Vec3.ZERO;
    }

    // Getters
    public static double getTotalDistance() {
        return totalDistance;
    }

    public static double getMaxSpeed() {
        return maxSpeed;
    }

    public static double getMaxAltitude() {
        return maxAltitude;
    }

    public static long getTotalFlightTime() {
        return totalFlightTime;
    }

    public static int getBarrelRollCount() {
        return barrelRollCount;
    }

    public static int getLoopCount() {
        return loopCount;
    }

    public static int getTotalTricks() {
        return barrelRollCount + loopCount;
    }

    /**
     * Get formatted flight time as MM:SS.
     */
    public static String getFormattedFlightTime() {
        long seconds = totalFlightTime / 20; // Convert from ticks (20 ticks = 1 second)
        long minutes = seconds / 60;
        long secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }
}
