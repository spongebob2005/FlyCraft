package com.sameeran.flycraft;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlightModeManager {
    public enum FlightMode { NORMAL, JET }

    private static final Map<UUID, FlightMode> playerModes = new HashMap<>();

    public static FlightMode getMode(UUID uuid) {
        return playerModes.getOrDefault(uuid, FlightMode.NORMAL);
    }

    /** Added missing setMode — was required by ClientEvents but didn't exist. */
    public static void setMode(UUID uuid, FlightMode mode) {
        playerModes.put(uuid, mode);
    }

    public static void toggleMode(UUID uuid) {
        setMode(uuid, getMode(uuid) == FlightMode.NORMAL ? FlightMode.JET : FlightMode.NORMAL);
    }
}
