package com.sameeran.flycraft.animation;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/**
 * Keybinds for all animations
 */
public class AnimationKeybinds {
    public static final KeyMapping WAVE_KEY = new KeyMapping(
            "key.flycraft.wave",
            GLFW.GLFW_KEY_V,
            "key.categories.movement"
    );

    public static final KeyMapping CLAP_KEY = new KeyMapping(
            "key.flycraft.clap",
            GLFW.GLFW_KEY_C,
            "key.categories.movement"
    );

    public static final KeyMapping DANCE_KEY = new KeyMapping(
            "key.flycraft.dance",
            GLFW.GLFW_KEY_B,
            "key.categories.movement"
    );

    public static final KeyMapping SPIN_KEY = new KeyMapping(
            "key.flycraft.spin",
            GLFW.GLFW_KEY_X,
            "key.categories.movement"
    );

    public static final KeyMapping JUMP_BOOST_KEY = new KeyMapping(
            "key.flycraft.jump_boost",
            GLFW.GLFW_KEY_Z,
            "key.categories.movement"
    );

    // Register all keybinds
    public static KeyMapping[] getAllKeybinds() {
        return new KeyMapping[]{
                WAVE_KEY,
                CLAP_KEY,
                DANCE_KEY,
                SPIN_KEY,
                JUMP_BOOST_KEY
        };
    }
}
