package com.sameeran.flycraft;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight compatibility wrapper named `ClientRegistry` in the mod package.
 *
 * This class collects key mappings and provides a method to register them
 * during Forge's RegisterKeyMappingsEvent. This avoids depending on a
 * specific Forge helper API that may differ between versions.
 */
public final class ClientRegistry {

    private ClientRegistry() { }

    private static final List<KeyMapping> KEY_MAPPINGS = new ArrayList<>();

    /**
     * Collect a key mapping to be registered later.
     */
    public static void registerKeyBinding(KeyMapping keyBinding) {
        if (keyBinding == null) return;
        KEY_MAPPINGS.add(keyBinding);
    }

    /**
     * Register all previously-collected key mappings with Forge's event.
     * Call this from your client event subscriber for RegisterKeyMappingsEvent.
     */
    public static void registerAll(RegisterKeyMappingsEvent event) {
        for (KeyMapping km : KEY_MAPPINGS) {
            event.register(km);
        }
    }
}
