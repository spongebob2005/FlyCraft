package com.sameeran.flycraft;

import com.mojang.brigadier.Command;
import com.sameeran.flycraft.animation.AnimationController;
import com.sameeran.flycraft.animation.AnimationKeybinds;
import com.sameeran.flycraft.animation.ParticleEffects;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    private static boolean jetMode = false;

    public static boolean isJetMode() { return jetMode; }

    public static boolean hasElytraEquipped(LocalPlayer player) {
        return player.getInventory().getArmor(2).getItem() == Items.ELYTRA;
    }

    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (KeybindHandler.TOGGLE_MODE_KEY.consumeClick()) {
            if (player == null) return;

            // Require elytra in chest slot to activate jet mode
            if (!jetMode && !hasElytraEquipped(player)) {
                player.displayClientMessage(
                    Component.literal("§c[FlyCraft] Equip an Elytra to use Jet Mode!"), true);
                return;
            }

            boolean enabled = !jetMode;
            setJetMode(enabled);
            // Sync with server-side FlightModeManager
            FlightModeManager.setMode(
                player.getUUID(),
                enabled ? FlightModeManager.FlightMode.JET : FlightModeManager.FlightMode.NORMAL);

            if (!enabled) FlightPatternManager.cancel();
            player.displayClientMessage(Component.literal(
                jetMode ? "§6✈ §aJet Mode §lON" : "§7🪂 Jet Mode §lOFF"), true);
            FlyCraftMod.LOGGER.debug("Jet Mode toggled to {}", jetMode);
        }

        if (player == null) return;

        // Flight patterns - always consume clicks so presses don't queue up,
        // but only act while flying in jet mode
        boolean canPattern = jetMode && player.isFallFlying();
        if (KeybindHandler.HOVER_KEY.consumeClick()) {
            if (canPattern) FlightPatternManager.activate(FlightPatternManager.Pattern.HOVER, player);
            else player.displayClientMessage(Component.literal("§7[FlyCraft] Patterns need Jet Mode + flight"), true);
        }
        if (KeybindHandler.BOOST_KEY.consumeClick()) {
            if (canPattern) FlightPatternManager.activate(FlightPatternManager.Pattern.BOOST, player);
            else player.displayClientMessage(Component.literal("§7[FlyCraft] Patterns need Jet Mode + flight"), true);
        }
        if (KeybindHandler.BARREL_ROLL_KEY.consumeClick()) {
            if (canPattern) FlightPatternManager.activate(FlightPatternManager.Pattern.BARREL_ROLL, player);
            else player.displayClientMessage(Component.literal("§7[FlyCraft] Patterns need Jet Mode + flight"), true);
        }

        playOnClick(AnimationKeybinds.WAVE_KEY,       "wave",       p -> ParticleEffects.sparkleEffect(p, 10), player);
        playOnClick(AnimationKeybinds.CLAP_KEY,       "clap",       p -> ParticleEffects.burstEffect(p, 12),   player);
        playOnClick(AnimationKeybinds.DANCE_KEY,      "dance",      p -> ParticleEffects.dustEffect(p, 15),    player);
        playOnClick(AnimationKeybinds.SPIN_KEY,       "spin",       p -> ParticleEffects.spiralEffect(p, 20),  player);
        playOnClick(AnimationKeybinds.JUMP_BOOST_KEY, "jump_boost", ParticleEffects::waveEffect,               player);
    }

    private static void playOnClick(KeyMapping key, String animation,
                                    Consumer<LocalPlayer> particles, LocalPlayer player) {
        if (!key.consumeClick()) return;
        AnimationController.playAnimation(animation);
        if (FlyCraftConfig.ANIMATION_PARTICLES.get()) {
            particles.accept(player);
        }
    }

    private static void setJetMode(boolean enabled) {
        jetMode = enabled;
        // Let the server know (for fall-damage protection), if it runs FlyCraft
        var connection = Minecraft.getInstance().getConnection();
        if (connection != null && FlyCraftNetwork.CHANNEL.isRemotePresent(connection.getConnection())) {
            FlyCraftNetwork.CHANNEL.sendToServer(new FlyCraftNetwork.JetModePacket(enabled));
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        // Don't carry jet mode or a half-played animation into the next world/server
        jetMode = false;
        FlightPatternManager.cancel();
        AnimationController.stopAllAnimations();
        StatsManager.save();
    }

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("flycraft")
            .then(Commands.literal("stats").executes(ctx -> {
                StatsManager.summaryLines().forEach(line ->
                    ctx.getSource().sendSuccess(() -> Component.literal(line), false));
                return Command.SINGLE_SUCCESS;
            }))
            .then(Commands.literal("resetstats").executes(ctx -> {
                StatsManager.resetStats();
                StatsManager.save();
                ctx.getSource().sendSuccess(() -> Component.literal("§e[FlyCraft] Flight stats reset."), false);
                return Command.SINGLE_SUCCESS;
            })));
    }
}
