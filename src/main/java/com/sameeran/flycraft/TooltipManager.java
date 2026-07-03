package com.sameeran.flycraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Shows contextual flight hints on the HUD.
 *
 * BUG FIXED: was using ScreenEvent.Render.Post (only fires when a menu is open)
 * with an early-return check for screen != null — so hints NEVER rendered.
 * Now uses RenderGuiEvent.Post, which fires every frame in-game.
 */
@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT)
public class TooltipManager {

    private static String currentTooltip    = "";
    private static int    tooltipDisplayTime = 0;
    private static final int TOOLTIP_DURATION = 200;

    private static boolean tutorialMode = true;

    public static void showTooltip(String message) {
        currentTooltip    = message;
        tooltipDisplayTime = TOOLTIP_DURATION;
    }

    public static void setTutorialMode(boolean enabled) { tutorialMode = enabled; }
    public static boolean isTutorialMode()              { return tutorialMode; }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        // Skip when any GUI screen is open
        if (mc.screen != null) return;

        LocalPlayer player = mc.player;
        if (player == null) return;

        GuiGraphics gfx = event.getGuiGraphics();
        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        // Fade-out timed tooltip
        if (tooltipDisplayTime > 0) {
            tooltipDisplayTime--;
            float alpha    = Math.min(1.0f, tooltipDisplayTime / 50.0f);
            int textColor  = ((int)(255 * alpha) << 24) | 0xFFFFFF;
            drawCentered(gfx, mc, currentTooltip, sw / 2, sh - 50, textColor);
        }

        // Contextual flight hints
        if (tutorialMode) {
            String hint = "";
            if (!player.isFallFlying() && !ClientEvents.isJetMode()) {
                hint = "Press R to toggle Jet Mode (requires Elytra)";
            } else if (player.isFallFlying() && ClientEvents.isJetMode()) {
                double speed = player.getDeltaMovement().length();
                if (speed < 0.8) {
                    hint = "\u26A0 Stalling! Dive to gain speed";
                } else if (AerobaticsManager.getBarrelRollProgress() > 0) {
                    hint = "Barrel Roll: " + AerobaticsManager.getBarrelRollProgress() + "%";
                } else if (AerobaticsManager.getLoopProgress() > 0) {
                    hint = "Loop: " + AerobaticsManager.getLoopProgress() + "%";
                }
            }
            if (!hint.isEmpty()) {
                drawCentered(gfx, mc, hint, sw / 2, 20, 0xFFFFFF);
            }
        }
    }

    private static void drawCentered(GuiGraphics gfx, Minecraft mc,
                                     String text, int cx, int y, int color) {
        int w = mc.font.width(text);
        gfx.drawString(mc.font, text, cx - w / 2, y, color, false);
    }
}
