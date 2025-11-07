package com.sameeran.flycraft;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Server-side handler that enhances Elytra flight when a player is in JET mode.
 * Registered on the Forge event bus; handler methods are static so Forge will
 * discover them automatically.
 */
@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID)
public class JetFlightHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;

        if (player == null) return;

        if (player.isFallFlying() &&
            FlightModeManager.getMode(player.getUUID()) == FlightModeManager.FlightMode.JET) {

            Vec3 lookVec = player.getLookAngle();
            // Apply a higher forward velocity — tweak the multiplier (1.8) to taste.
            player.setDeltaMovement(lookVec.scale(1.8));

            // mark for motion sync if available; guarded to avoid reflection or mapping issues
            try {
                player.hurtMarked = true;
            } catch (Throwable ignored) { }

            // prevent fall damage while flying
            player.fallDistance = 0;
        }
    }
}
