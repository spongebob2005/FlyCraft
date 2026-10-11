package com.sameeran.flycraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Core jet physics and camera effects while flying with an Elytra.
 * Keybind flight patterns (hover / boost / barrel roll) are layered on top
 * via FlightPatternManager.
 */
@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT)
public class FlightController {

    public  static final double MAX_SPEED     = 3.2;
    private static final double BASE_THRUST   = 0.05;
    private static final double SMOOTHNESS    = 0.25;
    private static final float  CAMERA_ROLL   = 3.0f;
    private static final double DRAG_COEFF    = 0.02;
    private static final double LIFT_COEFF    = 0.03;
    private static final double STALL_ANGLE   = 0.8;
    private static final double BANKING_FORCE = 0.015;
    private static final double MIN_SPEED     = 0.8;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level().isClientSide || event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof LocalPlayer player)) return;

        FlightPatternManager.tick(); // cooldowns drain even while grounded

        if (!player.isFallFlying() || !ClientEvents.isJetMode()) {
            // Landing or leaving jet mode ends any active pattern (no stuck hover)
            if (FlightPatternManager.getActive() != FlightPatternManager.Pattern.NONE) {
                FlightPatternManager.cancel();
            }
            return;
        }

        Vec3   look       = player.getLookAngle();
        Vec3   currentVel = player.getDeltaMovement();
        double speed      = currentVel.length();
        Vec3   up         = new Vec3(0, 1, 0);

        // ── Base physics ──────────────────────────────────────────────────
        double angleOfAttack = speed > 0.001
            ? Math.acos(Math.max(-1, Math.min(1, currentVel.normalize().dot(look)))) : 0;
        double liftMult  = angleOfAttack > STALL_ANGLE ? 0.2 : 1.0;
        double thrustPow = BASE_THRUST * (1.0 - (angleOfAttack / Math.PI) * 0.7);

        Vec3 thrust  = look.scale(thrustPow);
        Vec3 liftRaw = currentVel.cross(up).cross(currentVel);
        Vec3 lift    = liftRaw.lengthSqr() > 1e-6
            ? liftRaw.normalize().scale(LIFT_COEFF * speed * speed * liftMult) : Vec3.ZERO;
        Vec3 drag    = speed > 0.001
            ? currentVel.normalize().scale(-DRAG_COEFF * speed * speed) : Vec3.ZERO;
        Vec3 side    = look.cross(up).scale(BANKING_FORCE * speed);

        Vec3 target = currentVel.add(thrust).add(lift).add(drag).add(side);

        double stuntMult = AerobaticsManager.getStuntMultiplier();
        if (stuntMult > 1.0 && target.lengthSqr() > 1e-6) {
            target = target.normalize().scale(Math.min(target.length() * stuntMult, MAX_SPEED));
        }

        // ── Flight pattern layer ──────────────────────────────────────────
        // Capture before applying: a timed pattern may expire inside applyPattern
        FlightPatternManager.Pattern pattern = FlightPatternManager.getActive();
        if (pattern != FlightPatternManager.Pattern.NONE) {
            target = FlightPatternManager.applyPattern(target, look);
        }

        // ── Speed limits ──────────────────────────────────────────────────
        double maxSpeed = pattern == FlightPatternManager.Pattern.BOOST
            ? FlightPatternManager.BOOST_MAX_SPEED : MAX_SPEED;

        if (target.length() > maxSpeed) {
            target = target.normalize().scale(maxSpeed);
        } else if (pattern == FlightPatternManager.Pattern.NONE
                && target.length() < MIN_SPEED
                && target.lengthSqr() > 1e-6
                && player.getY() > player.level().getMinBuildHeight()) {
            // Stall behaviour only applies to free flight, not hover
            target = target.normalize().scale(MIN_SPEED).add(0, -0.08, 0);
        }

        player.setDeltaMovement(currentVel.lerp(target, SMOOTHNESS));

        // ── Effects ───────────────────────────────────────────────────────
        if (player.tickCount % 2 == 0) {
            if (pattern == FlightPatternManager.Pattern.BOOST) {
                player.level().addParticle(ParticleTypes.FLAME,
                    player.getX() - look.x * 0.8, player.getY() + 0.1, player.getZ() - look.z * 0.8,
                    -look.x * 0.1, 0.02, -look.z * 0.1);
            } else if (pattern != FlightPatternManager.Pattern.HOVER) {
                player.level().addParticle(ParticleTypes.SMOKE,
                    player.getX() - look.x * 0.6, player.getY() + 0.1, player.getZ() - look.z * 0.6,
                    -look.x * 0.05, 0.02, -look.z * 0.05);
            }
        }
        if (pattern == FlightPatternManager.Pattern.HOVER && player.tickCount % 4 == 0) {
            player.level().addParticle(ParticleTypes.CLOUD,
                player.getX(), player.getY() - 0.3, player.getZ(), 0, -0.05, 0);
        }
        if (pattern == FlightPatternManager.Pattern.BARREL_ROLL) {
            double a = player.tickCount * 0.6;
            player.level().addParticle(ParticleTypes.END_ROD,
                player.getX() + Math.cos(a) * 0.6, player.getY() + 0.5, player.getZ() + Math.sin(a) * 0.6,
                0, 0.02, 0);
        }
        if (player.tickCount % 40 == 0) {
            player.level().playSound(player, player.blockPosition(),
                pattern == FlightPatternManager.Pattern.BOOST
                    ? SoundEvents.FIREWORK_ROCKET_LAUNCH : SoundEvents.FIRECHARGE_USE,
                SoundSource.PLAYERS, 0.4f, 1.2f);
        }

        AerobaticsManager.updateStunts(player);
        StatsManager.updateStats(player);
    }

    @SubscribeEvent
    public static void onCameraUpdate(ViewportEvent.ComputeCameraAngles event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isFallFlying() || !ClientEvents.isJetMode()) return;

        FlightPatternManager.Pattern pattern = FlightPatternManager.getActive();

        // Barrel roll: full 360 degree camera roll over the pattern's duration
        if (pattern == FlightPatternManager.Pattern.BARREL_ROLL) {
            float remaining = FlightPatternManager.getPatternTicks() - (float) event.getPartialTick();
            float progress  = 1f - Math.max(0f, remaining) / FlightPatternManager.BARREL_ROLL_TICKS;
            event.setRoll(progress * 360f);
            return;
        }
        // Hover: level camera
        if (pattern == FlightPatternManager.Pattern.HOVER) {
            event.setRoll(0f);
            return;
        }

        float  tick     = (float) (player.tickCount + event.getPartialTick());
        Vec3   velocity = player.getDeltaMovement();
        double speed    = velocity.length();
        Vec3   look     = player.getLookAngle();
        Vec3   right    = look.cross(new Vec3(0, 1, 0));
        Vec3   lateral  = velocity.multiply(1, 0, 1);

        float bank = (lateral.lengthSqr() > 1e-6 && right.lengthSqr() > 1e-6)
            ? (float) (right.dot(lateral.normalize()) * lateral.length() * CAMERA_ROLL) : 0f;
        event.setRoll(bank);
        event.setPitch(event.getPitch() - (float) (speed * 0.5));

        if (speed > MAX_SPEED * 0.8) {
            float shake = (float) (Math.sin(tick * speed) * 0.3 * Math.min(speed / MAX_SPEED, 1.6));
            event.setRoll(event.getRoll() + shake);
            event.setYaw(event.getYaw() + shake * 0.5f);
        }
    }
}
