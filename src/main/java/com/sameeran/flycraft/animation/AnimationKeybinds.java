package com.sameeran.flycraft.animation;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/**
 * Keybinds for all FlyCraft animations.
 *
 * BUG FIXED: all keys were registered under vanilla's "key.categories.movement"
 * — they now live under "key.categories.flycraft" where they belong.
 */
public class AnimationKeybinds {

    private static final String CAT = "key.categories.flycraft";

    public static final KeyMapping WAVE_KEY = new KeyMapping(
            "key.flycraft.wave", GLFW.GLFW_KEY_V, CAT);

    public static final KeyMapping CLAP_KEY = new KeyMapping(
            "key.flycraft.clap", GLFW.GLFW_KEY_C, CAT);

    public static final KeyMapping DANCE_KEY = new KeyMapping(
            "key.flycraft.dance", GLFW.GLFW_KEY_B, CAT);

    public static final KeyMapping SPIN_KEY = new KeyMapping(
            "key.flycraft.spin", GLFW.GLFW_KEY_X, CAT);

    public static final KeyMapping JUMP_BOOST_KEY = new KeyMapping(
            "key.flycraft.jump_boost", GLFW.GLFW_KEY_Z, CAT);

    public static KeyMapping[] getAllKeybinds() {
        return new KeyMapping[]{ WAVE_KEY, CLAP_KEY, DANCE_KEY, SPIN_KEY, JUMP_BOOST_KEY };
    }
}
