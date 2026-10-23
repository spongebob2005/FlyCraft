package com.sameeran.flycraft;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-side record of which players are in jet mode (set by FlyCraftNetwork).
 */
public class FlightModeManager {
    public enum FlightMode { NORMAL, JET }

    private static final Map<UUID, FlightMode> playerModes = new ConcurrentHashMap<>();

    public static FlightMode getMode(UUID uuid) {
        return playerModes.getOrDefault(uuid, FlightMode.NORMAL);
    }

    public static void setMode(UUID uuid, FlightMode mode) {
        playerModes.put(uuid, mode);
    }

    public static void clear(UUID uuid) {
        playerModes.remove(uuid);
    }
}
