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
 * Handles all jet physics and camera effects while flying with Elytra.
 */
@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT)
public class FlightController {

    // speed and responsiveness settings
    private static final double BASE_THRUST = 0.05;     // forward acceleration each tick
    private static final double MAX_SPEED = 2.8;        // terminal jet speed
    private static final double SMOOTHNESS = 0.25;      // motion blending
    private static final float CAMERA_ROLL_INTENSITY = 3.0f;
    // FOV adjustments are handled via a separate event; reserved constant removed

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level().isClientSide || event.phase != TickEvent.Phase.END) return;
        LocalPlayer player = (LocalPlayer) event.player;

        if (player.isFallFlying() && ClientEvents.isJetMode()) {

            Vec3 look = player.getLookAngle();
            Vec3 currentVel = player.getDeltaMovement();

            // add continuous forward thrust (no fireworks)
            Vec3 boosted = currentVel.add(look.scale(BASE_THRUST));
            if (boosted.length() > MAX_SPEED)
                boosted = boosted.normalize().scale(MAX_SPEED);

            // smooth interpolation for fluid motion
            Vec3 newVel = currentVel.lerp(boosted, SMOOTHNESS);
            player.setDeltaMovement(newVel);

            // particle jet exhaust
            if (player.tickCount % 2 == 0) {
                player.level().addParticle(ParticleTypes.SMOKE,
                        player.getX() - look.x * 0.6,
                        player.getY() + 0.1,
                        player.getZ() - look.z * 0.6,
                        -look.x * 0.05, 0.02, -look.z * 0.05);
            }

            // light engine-like sound (not spammed)
            if (player.tickCount % 40 == 0) {
                player.level().playSound(player, player.blockPosition(),
                        SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS,
                        0.4f, 1.2f);
            }
        }
    }

    /**
     * Adjust camera to simulate jet dynamics.
     */
    @SubscribeEvent
    public static void onCameraUpdate(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        // Use local variable for nullable player reference
        LocalPlayer player = mc.player;
        if (player == null || !player.isFallFlying() || !ClientEvents.isJetMode()) return;

        float tick = (float) (player.tickCount + event.getPartialTick());
        double speed = player.getDeltaMovement().length();

        // roll based on horizontal velocity
        float roll = (float) (Math.sin(tick / 10.0) * CAMERA_ROLL_INTENSITY);
        event.setRoll(roll);

        // dynamic pitch (nose down slightly at high speed)
        event.setPitch(event.getPitch() - (float) (speed * 1.5));

    // Note: ViewportEvent.ComputeCameraAngles does not provide a FOV setter
    // in the Forge mappings; FOV adjustments should be applied via the
    // separate ComputeFov event if needed. For now we only adjust pitch/roll.
    }
}
