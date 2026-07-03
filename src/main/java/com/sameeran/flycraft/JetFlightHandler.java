package com.sameeran.flycraft;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Server-side jet mode support.
 * Flight physics live entirely in FlightController (client-only).
 * This class only prevents fall-damage accumulation on the server.
 *
 * BUG FIXED: previous version set velocity to lookVec.scale(1.8) every tick,
 * completely overriding all of FlightController's sophisticated physics.
 */
@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID)
public class JetFlightHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player == null) return;

        if (player.isFallFlying()
                && FlightModeManager.getMode(player.getUUID()) == FlightModeManager.FlightMode.JET) {
            player.fallDistance = 0;
        }
    }
}
