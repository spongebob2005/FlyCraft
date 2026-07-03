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
        LocalPlayer player = mc.player;
        if (player == null) return;

        RenderSystem.disableBlend();

        boolean jetMode = ClientEvents.isJetMode();
        double  speed   = player.getDeltaMovement().length();
        int x = 10, y = 10;

        // ── Mode indicator ────────────────────────────────────────────────
        String modeText  = jetMode ? "\u2708 Jet Mode" : "\uD83E\uDE82 Normal";
        int    modeColor = jetMode ? 0xFF6600 : 0x00FFAA;

        drawShadowed(event, mc, modeText, x, y, modeColor);

        // ── Speed bar ─────────────────────────────────────────────────────
        String speedText = String.format("Speed: %.2f / %.1f m/s", speed, FlightController.MAX_SPEED);
        drawShadowed(event, mc, speedText, x, y + 12, 0xFFFFFF);

        if (jetMode && player.isFallFlying()) {
            // ── Speed bar fill ────────────────────────────────────────────
            int barW    = 80;
            int barH    = 4;
            int barX    = x;
            int barY    = y + 26;
            float frac  = Math.min((float)(speed / FlightController.MAX_SPEED), 1f);
            int barColor = frac > 0.8f ? 0xFF2222 : frac > 0.5f ? 0xFFAA00 : 0x22FF66;

            event.getGuiGraphics().fill(barX, barY, barX + barW, barY + barH, 0x88000000);
            event.getGuiGraphics().fill(barX, barY, barX + (int)(barW * frac), barY + barH, barColor | 0xFF000000);

            // ── Afterburner flash ─────────────────────────────────────────
            if (player.tickCount % 20 < 10) {
                drawShadowed(event, mc, "AFTERBURNERS ACTIVE", x, y + 34, 0xFF4500);
            }

            // ── Stunt progress hints ──────────────────────────────────────
            int rollProg = AerobaticsManager.getBarrelRollProgress();
            int loopProg = AerobaticsManager.getLoopProgress();
            int yOff     = y + 46;

            if (rollProg > 0) {
                drawShadowed(event, mc, "Barrel Roll: " + rollProg + "%", x, yOff, 0xFFDD44);
                yOff += 12;
            }
            if (loopProg > 0) {
                drawShadowed(event, mc, "Loop: " + loopProg + "%", x, yOff, 0x44DDFF);
                yOff += 12;
            }

            // ── Stunt boost active indicator ──────────────────────────────
            double mult = AerobaticsManager.getStuntMultiplier();
            if (mult > 1.0) {
                drawShadowed(event, mc,
                    String.format("BOOST x%.1f", mult), x, yOff, 0xFFFF00);
            }

            // ── Stats summary (top-right corner) ──────────────────────────
            int sw = mc.getWindow().getGuiScaledWidth();
            String distText = String.format("Dist: %.0f m", StatsManager.getTotalDistance());
            String timeText = "Time: " + StatsManager.getFormattedFlightTime();
            int maxX = sw - 10;
            drawShadowedRight(event, mc, distText, maxX, 10, 0xCCCCCC);
            drawShadowedRight(event, mc, timeText,  maxX, 22, 0xCCCCCC);

            // ── Stall warning ─────────────────────────────────────────────
            if (speed < 0.8) {
                int mid = sw / 2;
                int sh  = mc.getWindow().getGuiScaledHeight();
                drawShadowedCentered(event, mc, "\u26A0 STALLING! Dive to regain speed", mid, sh / 2 - 20, 0xFF3333);
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private static void drawShadowed(RenderGuiEvent.Post e, Minecraft mc,
                                     String text, int x, int y, int color) {
        e.getGuiGraphics().drawString(mc.font, text, x + 1, y + 1, 0x202020, false);
        e.getGuiGraphics().drawString(mc.font, text, x,     y,     color,    false);
    }

    private static void drawShadowedRight(RenderGuiEvent.Post e, Minecraft mc,
                                          String text, int rightX, int y, int color) {
        int w = mc.font.width(text);
        drawShadowed(e, mc, text, rightX - w, y, color);
    }

    private static void drawShadowedCentered(RenderGuiEvent.Post e, Minecraft mc,
                                             String text, int cx, int y, int color) {
        int w = mc.font.width(text);
        drawShadowed(e, mc, text, cx - w / 2, y, color);
    }
}
