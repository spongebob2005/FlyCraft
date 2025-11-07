package com.sameeran.flycraft;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT)
public class HUDOverlay {

    @SubscribeEvent
    public static void onRender(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        // Use a local variable for the possibly-null player reference
        LocalPlayer player = mc.player;
        if (player == null) return;

        RenderSystem.disableBlend();

        boolean jetMode = ClientEvents.isJetMode();
    double speed = player.getDeltaMovement().length();

        String modeText = jetMode ? "✈ Jet Mode" : "🪂 Normal Mode";
        String speedText = String.format("Speed: %.2f m/s", speed);

        int modeColor = jetMode ? 0xFF6600 : 0x00FFAA;  // orange for jet, aqua for normal
        int speedColor = 0xFFFFFF;

        int x = 10;
        int y = 10;

        // Draw a faint shadow for readability
        event.getGuiGraphics().drawString(mc.font, modeText, x + 1, y + 1, 0x202020);
        event.getGuiGraphics().drawString(mc.font, speedText, x + 1, y + 16 + 1, 0x202020);

        // Draw the main HUD text
        event.getGuiGraphics().drawString(mc.font, modeText, x, y, modeColor);
        event.getGuiGraphics().drawString(mc.font, speedText, x, y + 16, speedColor);

        // Optional: small visual effect when in Jet Mode
        if (jetMode && player.tickCount % 20 < 10) {
            String status = "AFTERBURNERS ACTIVE";
            event.getGuiGraphics().drawString(mc.font, status, x, y + 32, 0xFF4500);
        }
    }
}
