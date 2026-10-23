package com.sameeran.flycraft;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Client settings, saved to config/flycraft-client.toml.
 */
public class FlyCraftConfig {

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue  MAX_SPEED;
    public static final ForgeConfigSpec.BooleanValue SHOW_HUD;
    public static final ForgeConfigSpec.BooleanValue SHOW_JET_OVERLAY;
    public static final ForgeConfigSpec.BooleanValue CAMERA_EFFECTS;
    public static final ForgeConfigSpec.BooleanValue EXHAUST_EFFECTS;
    public static final ForgeConfigSpec.BooleanValue TUTORIAL_HINTS;
    public static final ForgeConfigSpec.BooleanValue ANIMATION_PARTICLES;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.push("flight");
        MAX_SPEED = b.comment("Top speed in jet mode (blocks per tick)")
                     .defineInRange("maxSpeed", 3.2, 1.0, 5.0);
        b.pop();

        b.push("visuals");
        SHOW_HUD = b.comment("Show the speed / mode HUD")
                    .define("showHud", true);
        SHOW_JET_OVERLAY = b.comment("Show vignette, speed lines and G-force blur in jet mode")
                            .define("showJetOverlay", true);
        CAMERA_EFFECTS = b.comment("Camera banking and high-speed shake in jet mode")
                          .define("cameraEffects", true);
        EXHAUST_EFFECTS = b.comment("Exhaust smoke particles and engine sound in jet mode")
                           .define("exhaustEffects", true);
        TUTORIAL_HINTS = b.comment("Show contextual flight hints at the top of the screen")
                          .define("tutorialHints", true);
        ANIMATION_PARTICLES = b.comment("Spawn particles when playing an animation")
                               .define("animationParticles", true);
        b.pop();

        SPEC = b.build();
    }
}
