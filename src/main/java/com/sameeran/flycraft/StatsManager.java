package com.sameeran.flycraft;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

/**
 * Tracks flight statistics: distance, max speed, stunts, tricks, etc.
 * Saved to config/flycraft-stats.properties after each flight and on logout;
 * view with /flycraft stats.
 */
public class StatsManager {

    private static double totalDistance = 0.0;
    private static double maxSpeed = 0.0;
    private static double maxAltitude = 0.0;
    private static long totalFlightTime = 0;
    private static int barrelRollCount = 0;
    private static int loopCount = 0;
    private static Vec3 lastPosition = null;

    private static boolean loaded = false;
    private static boolean dirty  = false;

    /**
     * Update stats each tick.
     */
    public static void updateStats(LocalPlayer player) {
        ensureLoaded();
        if (!player.isFallFlying() || !ClientEvents.isJetMode()) {
            // Flight ended: forget the position so the gap to the next flight isn't counted
            if (lastPosition != null) {
                lastPosition = null;
                save();
            }
            return;
        }

        Vec3 currentPos = player.position();
        Vec3 currentVel = player.getDeltaMovement();
        double speed = currentVel.length();

        // Track distance
        if (lastPosition != null) {
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
        dirty = true;
    }

    /**
     * Record a stunt completion.
     */
    public static void recordStunt(String stuntType) {
        ensureLoaded();
        if ("barrel_roll".equals(stuntType)) {
            barrelRollCount++;
        } else if ("loop".equals(stuntType)) {
            loopCount++;
        }
        dirty = true;
    }

    /**
     * Reset all stats.
     */
    public static void resetStats() {
        loaded = true; // don't let a later lazy load bring old values back
        totalDistance = 0.0;
        maxSpeed = 0.0;
        maxAltitude = 0.0;
        totalFlightTime = 0;
        barrelRollCount = 0;
        loopCount = 0;
        lastPosition = null;
        dirty = true;
    }

    // ── Persistence ───────────────────────────────────────────────────────

    private static Path file() {
        return FMLPaths.CONFIGDIR.get().resolve("flycraft-stats.properties");
    }

    private static void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        Path path = file();
        if (!Files.exists(path)) return;

        Properties p = new Properties();
        try (Reader in = Files.newBufferedReader(path)) {
            p.load(in);
            totalDistance   = Double.parseDouble(p.getProperty("totalDistance", "0"));
            maxSpeed        = Double.parseDouble(p.getProperty("maxSpeed", "0"));
            maxAltitude     = Double.parseDouble(p.getProperty("maxAltitude", "0"));
            totalFlightTime = Long.parseLong(p.getProperty("totalFlightTicks", "0"));
            barrelRollCount = Integer.parseInt(p.getProperty("barrelRolls", "0"));
            loopCount       = Integer.parseInt(p.getProperty("loops", "0"));
        } catch (IOException | NumberFormatException e) {
            FlyCraftMod.LOGGER.warn("Could not read flight stats from {}: {}", path, e.getMessage());
        }
    }

    public static void save() {
        if (!dirty) return;
        Properties p = new Properties();
        p.setProperty("totalDistance",    Double.toString(totalDistance));
        p.setProperty("maxSpeed",         Double.toString(maxSpeed));
        p.setProperty("maxAltitude",      Double.toString(maxAltitude));
        p.setProperty("totalFlightTicks", Long.toString(totalFlightTime));
        p.setProperty("barrelRolls",      Integer.toString(barrelRollCount));
        p.setProperty("loops",            Integer.toString(loopCount));

        try (Writer out = Files.newBufferedWriter(file())) {
            p.store(out, "FlyCraft flight stats");
            dirty = false;
        } catch (IOException e) {
            FlyCraftMod.LOGGER.warn("Could not save flight stats: {}", e.getMessage());
        }
    }

    /**
     * Human-readable stats for /flycraft stats.
     */
    public static List<String> summaryLines() {
        ensureLoaded();
        return List.of(
            "§6✈ FlyCraft flight stats",
            String.format("§7Distance flown: §f%.0f m", totalDistance),
            "§7Time in jet mode: §f" + getFormattedFlightTime(),
            String.format("§7Top speed: §f%.2f m/tick", maxSpeed),
            String.format("§7Highest altitude: §fY %.0f", maxAltitude),
            "§7Barrel rolls: §f" + barrelRollCount + "§7  Loops: §f" + loopCount);
    }

    // Getters
    public static double getTotalDistance() {
        ensureLoaded();
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
     * Get formatted flight time as H:MM:SS or M:SS.
     */
    public static String getFormattedFlightTime() {
        long seconds = totalFlightTime / 20; // Convert from ticks (20 ticks = 1 second)
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return hours > 0
            ? String.format("%d:%02d:%02d", hours, minutes, secs)
            : String.format("%d:%02d", minutes, secs);
    }
}
