package com.sameeran.flycraft;

import com.sameeran.flycraft.animation.AnimationKeybinds;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeybindHandler {

    public static final String CATEGORY = "key.categories.flycraft";

    public static final KeyMapping TOGGLE_MODE_KEY = new KeyMapping(
            "key.flycraft.toggle_mode", GLFW.GLFW_KEY_R, CATEGORY);

    // Flight patterns - G/H/J chosen to avoid vanilla F (swap hand) and Q (drop item)
    public static final KeyMapping HOVER_KEY = new KeyMapping(
            "key.flycraft.hover", GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping BOOST_KEY = new KeyMapping(
            "key.flycraft.boost", GLFW.GLFW_KEY_H, CATEGORY);
    public static final KeyMapping BARREL_ROLL_KEY = new KeyMapping(
            "key.flycraft.barrel_roll", GLFW.GLFW_KEY_J, CATEGORY);

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_MODE_KEY);
        event.register(HOVER_KEY);
        event.register(BOOST_KEY);
        event.register(BARREL_ROLL_KEY);
        for (KeyMapping key : AnimationKeybinds.getAllKeybinds()) {
            event.register(key);
        }
    }
}
