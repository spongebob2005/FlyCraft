package com.sameeran.flycraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.sameeran.flycraft.animation.AnimationController;
import com.sameeran.flycraft.animation.AnimationKeybinds;
import com.sameeran.flycraft.animation.ParticleEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    private static boolean jetMode = false;

    public static boolean isJetMode() {
        return jetMode;
    }

    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        if (KeybindHandler.TOGGLE_MODE_KEY.consumeClick()) {
            jetMode = !jetMode;
            System.out.println("[FlyCraft] Jet Mode toggled: " + jetMode);
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        // Animation keybinds
        if (AnimationKeybinds.WAVE_KEY.consumeClick()) {
            AnimationController.playAnimation("wave");
            ParticleEffects.sparkleEffect(player, 10);
            System.out.println("[FlyCraft] Wave animation playing");
        }

        if (AnimationKeybinds.CLAP_KEY.consumeClick()) {
            AnimationController.playAnimation("clap");
            ParticleEffects.burstEffect(player, 12);
            System.out.println("[FlyCraft] Clap animation playing");
        }

        if (AnimationKeybinds.DANCE_KEY.consumeClick()) {
            AnimationController.playAnimation("dance");
            ParticleEffects.dustEffect(player, 15);
            System.out.println("[FlyCraft] Dance animation playing");
        }

        if (AnimationKeybinds.SPIN_KEY.consumeClick()) {
            AnimationController.playAnimation("spin");
            ParticleEffects.spiralEffect(player, 20);
            System.out.println("[FlyCraft] Spin animation playing");
        }

        if (AnimationKeybinds.JUMP_BOOST_KEY.consumeClick()) {
            AnimationController.playAnimation("jump_boost");
            ParticleEffects.waveEffect(player);
            System.out.println("[FlyCraft] Jump boost animation playing");
        }
    }
}
