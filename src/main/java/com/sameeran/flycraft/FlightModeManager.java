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

    public static void toggleMode(UUID uuid) {
        FlightMode current = getMode(uuid);
        FlightMode next = (current == FlightMode.NORMAL) ? FlightMode.JET : FlightMode.NORMAL;
        playerModes.put(uuid, next);
    }
}
