package com.sameeran.flycraft;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import com.sameeran.flycraft.animation.AnimationKeybinds;

@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeybindHandler {

    public static final String CATEGORY = "key.categories.flycraft";
    public static final KeyMapping TOGGLE_MODE_KEY = new KeyMapping(
            "key.flycraft.toggle_mode",
            GLFW.GLFW_KEY_R,
            CATEGORY
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_MODE_KEY);

        // Register animation keybinds
        for (KeyMapping keybind : AnimationKeybinds.getAllKeybinds()) {
            event.register(keybind);
        }
    }
}
