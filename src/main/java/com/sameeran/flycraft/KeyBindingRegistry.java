package com.sameeran.flycraft;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import com.mojang.blaze3d.platform.InputConstants;

public class KeyBindingRegistry {

    public static void registerKeyBinding(RegisterKeyMappingsEvent event, KeyMapping keyBinding) {
        if (keyBinding != null) {
            event.register(keyBinding);
        }
    }

    public static KeyMapping createKeyBinding(String name, int keyCode, String category) {
        return new KeyMapping(
            name,
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            keyCode,
            category
        );
    }

    public static KeyMapping createKeyBinding(String name, KeyConflictContext context, KeyModifier modifier, InputConstants.Type type,int keyCode, String category) {
        return new KeyMapping(
            name,
            context,
            modifier,
            type,
            keyCode,
            category
        );
    }
}
