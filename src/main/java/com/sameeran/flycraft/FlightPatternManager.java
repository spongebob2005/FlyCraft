package com.sameeran.flycraft;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

/**
 * Keybind-activated flight patterns (jet mode + flying only).
 *
 *   HOVER       (G) - toggle: bleed off speed and hold altitude
 *   BOOST       (H) - 3 s speed burst above normal max, 10 s cooldown
 *   BARREL_ROLL (J) - 1 s automated 360 degree roll
 */
public class FlightPatternManager {

    public enum Pattern { NONE, HOVER, BOOST, BARREL_ROLL }

    public  static final int    BOOST_DURATION    = 60;   // 3 s
    public  static final int    BARREL_ROLL_TICKS = 20;   // 1 s
    private static final int    BOOST_COOLDOWN    = 200;  // 10 s
    public  static final double BOOST_MAX_SPEED   = FlightController.MAX_SPEED * 1.6;

    private static Pattern activePattern = Pattern.NONE;
    private static int     patternTicks  = 0;
    private static int     boostCooldown = 0;

    /** Called from ClientEvents when a pattern key is pressed. */
    public static void activate(Pattern pattern, LocalPlayer player) {
        if (pattern == Pattern.NONE) { cancel(); return; }

        if (pattern == Pattern.HOVER && activePattern == Pattern.HOVER) {
            cancel();
            player.displayClientMessage(Component.literal("§7[FlyCraft] Hover OFF"), true);
            return;
        }
        if (pattern == Pattern.BOOST && boostCooldown > 0) {
            int secs = (boostCooldown + 19) / 20;
            player.displayClientMessage(
                Component.literal("§c[FlyCraft] Boost recharging - " + secs + "s"), true);
            return;
        }
        if (pattern == Pattern.BARREL_ROLL && activePattern == Pattern.BARREL_ROLL) return;

        activePattern = pattern;
        patternTicks  = switch (pattern) {
            case BOOST       -> BOOST_DURATION;
            case BARREL_ROLL -> BARREL_ROLL_TICKS;
            default          -> 0; // HOVER has no timer; runs until toggled off
        };
        if (pattern == Pattern.BOOST) boostCooldown = BOOST_COOLDOWN;

        String msg = switch (pattern) {
            case HOVER       -> "§b[FlyCraft] Hover ON";
            case BOOST       -> "§6[FlyCraft] BOOST!";
            case BARREL_ROLL -> "§d[FlyCraft] Barrel Roll!";
            default          -> "";
        };
        player.displayClientMessage(Component.literal(msg), true);
        FlyCraftMod.LOGGER.debug("Flight pattern activated: {}", pattern);
    }

    /** Applied every flying tick from FlightController; returns the modified target velocity. */
    public static Vec3 applyPattern(Vec3 targetVel, Vec3 look) {
        Vec3 result = switch (activePattern) {
            case HOVER -> {
                // Strong horizontal damping + small lift to counter gravity
                double vy = targetVel.y * 0.5 + 0.04;
                yield new Vec3(targetVel.x * 0.6, vy, targetVel.z * 0.6);
            }
            case BOOST -> {
                Vec3 v = targetVel.add(look.scale(0.18));
                yield v.length() > BOOST_MAX_SPEED ? v.normalize().scale(BOOST_MAX_SPEED) : v;
            }
            case BARREL_ROLL -> {
                double progress = 1.0 - (double) patternTicks / BARREL_ROLL_TICKS;
                double angle    = 2 * Math.PI * progress;
                Vec3 up    = new Vec3(0, 1, 0);
                Vec3 cross = look.cross(up);
                Vec3 right = cross.lengthSqr() > 1e-6 ? cross.normalize() : new Vec3(1, 0, 0);
                Vec3 realUp = right.cross(look).normalize();
                Vec3 offset = right.scale(Math.cos(angle) * 0.25)
                                   .add(realUp.scale(Math.sin(angle) * 0.25));
                double fwd  = Math.max(targetVel.length(), FlightController.MAX_SPEED * 0.8);
                yield look.scale(fwd).add(offset);
            }
            default -> targetVel;
        };

        // Timed patterns count down and expire; HOVER stays until toggled
        if (activePattern == Pattern.BOOST || activePattern == Pattern.BARREL_ROLL) {
            if (--patternTicks <= 0) cancel();
        }
        return result;
    }

    /** Ends any active pattern (landing, leaving jet mode, or toggle off). */
    public static void cancel() {
        activePattern = Pattern.NONE;
        patternTicks  = 0;
    }

    /** Drains the boost cooldown; call every client tick. */
    public static void tick() {
        if (boostCooldown > 0) boostCooldown--;
    }

    public static Pattern getActive()        { return activePattern; }
    public static int     getPatternTicks()  { return patternTicks; }
    public static int     getBoostCooldown() { return boostCooldown; }
    public static boolean isBoostReady()     { return boostCooldown == 0; }
}
