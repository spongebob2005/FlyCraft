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

    // Advanced flight physics parameters
    private static final double BASE_THRUST = 0.05;     // base thrust acceleration
    private static final double MAX_SPEED = 3.2;        // terminal velocity
    private static final double SMOOTHNESS = 0.25;      // motion blending factor
    private static final float CAMERA_ROLL_INTENSITY = 3.0f;
    private static final double DRAG_COEFFICIENT = 0.02; // air resistance
    private static final double LIFT_COEFFICIENT = 0.03; // lift force
    private static final double STALL_ANGLE = 0.8;      // critical angle of attack
    private static final double BANKING_FORCE = 0.015;  // lateral force during turns
    private static final double MIN_SPEED = 0.8;        // minimum speed before stall

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level().isClientSide || event.phase != TickEvent.Phase.END) return;
        LocalPlayer player = (LocalPlayer) event.player;

        if (player.isFallFlying() && ClientEvents.isJetMode()) {

            Vec3 look = player.getLookAngle();
            Vec3 currentVel = player.getDeltaMovement();
            double speed = currentVel.length();

            // Calculate angle of attack (angle between velocity and look direction)
            double angleOfAttack = Math.acos(currentVel.normalize().dot(look));
            
            // Stall mechanics - lose lift if angle too high
            double liftMultiplier = angleOfAttack > STALL_ANGLE ? 0.2 : 1.0;
            
            // Thrust vector - reduced at high angles of attack
            double thrustPower = BASE_THRUST * (1.0 - (angleOfAttack / Math.PI) * 0.7);
            Vec3 thrust = look.scale(thrustPower);
            
            // Lift force - perpendicular to velocity, scaled by speed
            Vec3 up = new Vec3(0, 1, 0);
            Vec3 liftDir = currentVel.cross(up).cross(currentVel).normalize();
            Vec3 lift = liftDir.scale(LIFT_COEFFICIENT * speed * speed * liftMultiplier);
            
            // Air resistance (drag) - increases with square of speed
            Vec3 drag = currentVel.normalize().scale(-DRAG_COEFFICIENT * speed * speed);
            
            // Banking forces during turns
            Vec3 sideForce = look.cross(up).scale(BANKING_FORCE * speed);
            
            // Combine all forces
            Vec3 totalForce = thrust.add(lift).add(drag).add(sideForce);
            Vec3 boosted = currentVel.add(totalForce);

            // Speed limits
            if (boosted.length() > MAX_SPEED) {
                boosted = boosted.normalize().scale(MAX_SPEED);
            } else if (boosted.length() < MIN_SPEED && player.getY() > player.level().getMinBuildHeight()) {
                // Gradual stall at low speeds
                boosted = boosted.normalize().scale(MIN_SPEED);
                boosted = boosted.add(new Vec3(0, -0.08, 0)); // Start falling if too slow
            }

            // Smooth interpolation for fluid motion
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
        Vec3 velocity = player.getDeltaMovement();
        double speed = velocity.length();
        Vec3 look = player.getLookAngle();

        // Calculate bank angle based on lateral velocity
        Vec3 lateralVel = velocity.multiply(1, 0, 1);
        double lateralSpeed = lateralVel.length();
        Vec3 right = look.cross(new Vec3(0, 1, 0));
        float bankAngle = (float) (right.dot(lateralVel.normalize()) * lateralSpeed * CAMERA_ROLL_INTENSITY);

        // Apply realistic roll based on banking
        event.setRoll(bankAngle);

        // Dynamic pitch based on angle of attack and speed
        double angleOfAttack = Math.acos(velocity.normalize().dot(look));
        float pitchEffect = (float) (speed * 1.5 + angleOfAttack * 20);
        event.setPitch(event.getPitch() - pitchEffect);

        // Add slight camera shake at high speeds
        if (speed > MAX_SPEED * 0.8) {
            float shake = (float) (Math.sin(tick * speed) * 0.3 * (speed / MAX_SPEED));
            event.setRoll(event.getRoll() + shake);
            event.setYaw(event.getYaw() + shake * 0.5f);
        }

    // Note: ViewportEvent.ComputeCameraAngles does not provide a FOV setter
    // in the Forge mappings; FOV adjustments should be applied via the
    // separate ComputeFov event if needed. For now we only adjust pitch/roll.
    }
}
