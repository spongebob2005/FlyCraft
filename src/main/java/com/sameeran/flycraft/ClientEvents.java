package com.sameeran.flycraft;

import com.sameeran.flycraft.animation.AnimationController;
import com.sameeran.flycraft.animation.AnimationKeybinds;
import com.sameeran.flycraft.animation.ParticleEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    private static boolean jetMode = false;

    public static boolean isJetMode() { return jetMode; }

    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (KeybindHandler.TOGGLE_MODE_KEY.consumeClick()) {
            if (player == null) return;

            // Require elytra in chest slot to activate jet mode
            boolean hasElytra = player.getInventory().getArmor(2).getItem() == Items.ELYTRA;
            if (!jetMode && !hasElytra) {
                player.displayClientMessage(
                    Component.literal("§c[FlyCraft] Equip an Elytra to use Jet Mode!"), true);
                return;
            }

            jetMode = !jetMode;
            // Sync with server-side FlightModeManager
            FlightModeManager.setMode(
                player.getUUID(),
                jetMode ? FlightModeManager.FlightMode.JET : FlightModeManager.FlightMode.NORMAL);

            player.displayClientMessage(Component.literal(
                jetMode ? "§6✈ §aJet Mode §lON" : "§7🪂 Jet Mode §lOFF"), true);
            FlyCraftMod.LOGGER.debug("Jet Mode toggled to {}", jetMode);
        }

        if (player == null) return;

        if (AnimationKeybinds.WAVE_KEY.consumeClick()) {
            AnimationController.playAnimation("wave");
            ParticleEffects.sparkleEffect(player, 10);
        }
        if (AnimationKeybinds.CLAP_KEY.consumeClick()) {
            AnimationController.playAnimation("clap");
            ParticleEffects.burstEffect(player, 12);
        }
        if (AnimationKeybinds.DANCE_KEY.consumeClick()) {
            AnimationController.playAnimation("dance");
            ParticleEffects.dustEffect(player, 15);
        }
        if (AnimationKeybinds.SPIN_KEY.consumeClick()) {
            AnimationController.playAnimation("spin");
            ParticleEffects.spiralEffect(player, 20);
        }
        if (AnimationKeybinds.JUMP_BOOST_KEY.consumeClick()) {
            AnimationController.playAnimation("jump_boost");
            ParticleEffects.waveEffect(player);
        }
    }
}
