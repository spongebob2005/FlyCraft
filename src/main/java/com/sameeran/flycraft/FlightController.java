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
 * Handles all jet physics and camera effects while flying with an Elytra.
 */
@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT)
public class FlightController {

    static final double MAX_SPEED          = 3.2;
    private static final double BASE_THRUST       = 0.05;
    private static final double SMOOTHNESS        = 0.25;
    private static final float  CAMERA_ROLL       = 3.0f;
    private static final double DRAG_COEFF        = 0.02;
    private static final double LIFT_COEFF        = 0.03;
    private static final double STALL_ANGLE       = 0.8;
    private static final double BANKING_FORCE     = 0.015;
    private static final double MIN_SPEED         = 0.8;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level().isClientSide || event.phase != TickEvent.Phase.END) return;
        LocalPlayer player = (LocalPlayer) event.player;

        if (!player.isFallFlying() || !ClientEvents.isJetMode()) return;

        Vec3 look       = player.getLookAngle();
        Vec3 currentVel = player.getDeltaMovement();
        double speed    = currentVel.length();

        // Angle of attack — guard against zero-length velocity to avoid NaN
        double angleOfAttack = (speed > 0.001)
            ? Math.acos(Math.max(-1, Math.min(1, currentVel.normalize().dot(look))))
            : 0;

        double liftMult   = angleOfAttack > STALL_ANGLE ? 0.2 : 1.0;
        double thrustPow  = BASE_THRUST * (1.0 - (angleOfAttack / Math.PI) * 0.7);

        Vec3 up      = new Vec3(0, 1, 0);
        Vec3 thrust  = look.scale(thrustPow);

        Vec3 liftRaw = currentVel.cross(up).cross(currentVel);
        Vec3 lift    = (liftRaw.lengthSqr() > 1e-6)
            ? liftRaw.normalize().scale(LIFT_COEFF * speed * speed * liftMult)
            : Vec3.ZERO;

        Vec3 drag    = (speed > 0.001)
            ? currentVel.normalize().scale(-DRAG_COEFF * speed * speed)
            : Vec3.ZERO;

        Vec3 side    = look.cross(up).scale(BANKING_FORCE * speed);

        Vec3 boosted = currentVel.add(thrust).add(lift).add(drag).add(side);

        // Apply any active stunt speed bonus
        double stuntMult = AerobaticsManager.getStuntMultiplier();
        if (stuntMult > 1.0 && boosted.lengthSqr() > 1e-6) {
            double target = Math.min(boosted.length() * stuntMult, MAX_SPEED);
            boosted = boosted.normalize().scale(target);
        }

        if (boosted.length() > MAX_SPEED) {
            boosted = boosted.normalize().scale(MAX_SPEED);
        } else if (boosted.length() < MIN_SPEED && player.getY() > player.level().getMinBuildHeight()) {
            boosted = boosted.normalize().scale(MIN_SPEED).add(new Vec3(0, -0.08, 0));
        }

        player.setDeltaMovement(currentVel.lerp(boosted, SMOOTHNESS));

        // Exhaust particles
        if (player.tickCount % 2 == 0) {
            player.level().addParticle(ParticleTypes.SMOKE,
                player.getX() - look.x * 0.6, player.getY() + 0.1, player.getZ() - look.z * 0.6,
                -look.x * 0.05, 0.02, -look.z * 0.05);
        }
        if (player.tickCount % 40 == 0) {
            player.level().playSound(player, player.blockPosition(),
                SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.4f, 1.2f);
        }

        // Wire up previously unused systems
        AerobaticsManager.updateStunts(player);
        StatsManager.updateStats(player);
    }

    @SubscribeEvent
    public static void onCameraUpdate(ViewportEvent.ComputeCameraAngles event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isFallFlying() || !ClientEvents.isJetMode()) return;

        float  tick      = (float) (player.tickCount + event.getPartialTick());
        Vec3   velocity  = player.getDeltaMovement();
        double speed     = velocity.length();
        Vec3   look      = player.getLookAngle();
        Vec3   up        = new Vec3(0, 1, 0);

        // Bank angle from lateral drift
        Vec3 lateralVel = velocity.multiply(1, 0, 1);
        Vec3 right      = look.cross(up);
        float bank      = (lateralVel.lengthSqr() > 1e-6 && right.lengthSqr() > 1e-6)
            ? (float) (right.dot(lateralVel.normalize()) * lateralVel.length() * CAMERA_ROLL)
            : 0f;
        event.setRoll(bank);

        // Mild pitch tilt — old value (speed*1.5 + angle*20) was far too aggressive
        event.setPitch(event.getPitch() - (float) (speed * 0.5));

        // Subtle high-speed shake
        if (speed > MAX_SPEED * 0.8) {
            float shake = (float) (Math.sin(tick * speed) * 0.3 * (speed / MAX_SPEED));
            event.setRoll(event.getRoll() + shake);
            event.setYaw(event.getYaw() + shake * 0.5f);
        }
    }
}
