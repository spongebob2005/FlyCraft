package com.sameeran.flycraft;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Server-side jet mode support.
 * Flight physics live entirely in FlightController (client-only).
 * This class only prevents fall-damage accumulation on the server, for
 * players whose client reported jet mode via FlyCraftNetwork.
 */
@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID)
public class JetFlightHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        Player player = event.player;

        if (player.isFallFlying()
                && FlightModeManager.getMode(player.getUUID()) == FlightModeManager.FlightMode.JET) {
            player.fallDistance = 0;
        }
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        FlightModeManager.clear(event.getEntity().getUUID());
    }
}
