package com.sameeran.flycraft;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Displays on-screen tutorial hints and tooltips for flight mechanics.
 */
@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT)
public class TooltipManager {

    private static int tooltipDisplayTime = 0;
    private static String currentTooltip = "";
    private static final int TOOLTIP_DURATION = 200; // ~10 seconds at 20 ticks/sec
    private static boolean tutorialMode = true;

    /**
     * Show a tooltip message on screen.
     */
    public static void showTooltip(String message) {
        currentTooltip = message;
        tooltipDisplayTime = TOOLTIP_DURATION;
    }

    /**
     * Toggle tutorial mode on/off.
     */
    public static void setTutorialMode(boolean enabled) {
        tutorialMode = enabled;
    }

    /**
     * Check if tutorial mode is active.
     */
    public static boolean isTutorialMode() {
        return tutorialMode;
    }

    /**
     * Update tooltip display each frame.
     */
    public static void updateTooltip() {
        if (tooltipDisplayTime > 0) {
            tooltipDisplayTime--;
        }
    }

    /**
     * Render tooltips and hints on-screen.
     */
    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        // Only render on HUD, not on menu screens
        if (event.getScreen() != null) return;

        if (tooltipDisplayTime > 0 && !currentTooltip.isEmpty()) {
            GuiGraphics guiGraphics = event.getGuiGraphics();
            int screenWidth = event.getWindow().getGuiScaledWidth();
            int screenHeight = event.getWindow().getGuiScaledHeight();

            // Render tooltip at bottom center with fade effect
            float alpha = Math.min(1.0f, tooltipDisplayTime / 50.0f);
            int textColor = (int) (255 * alpha) << 24 | 0xFFFFFF;

            int y = screenHeight - 50;
            drawCenteredString(guiGraphics, currentTooltip, screenWidth / 2, y, textColor);
        }

        // Display hints based on player state
        if (tutorialMode) {
            displayContextualHints(event);
        }
    }

    /**
     * Show context-sensitive hints based on flight state.
     */
    private static void displayContextualHints(ScreenEvent.Render.Post event) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = event.getWindow().getGuiScaledWidth();

        String hint = "";
        if (!player.isFallFlying() && !ClientEvents.isJetMode()) {
            hint = "Press R to toggle Jet Mode (requires Elytra)";
        } else if (player.isFallFlying() && ClientEvents.isJetMode()) {
            double speed = player.getDeltaMovement().length();
            if (speed < 0.8) {
                hint = "Warning: Stalling! Increase speed or altitude";
            } else if (AerobaticsManager.getBarrelRollProgress() > 0) {
                hint = "Barrel Roll: " + AerobaticsManager.getBarrelRollProgress() + "%";
            } else if (AerobaticsManager.getLoopProgress() > 0) {
                hint = "Loop: " + AerobaticsManager.getLoopProgress() + "%";
            }
        }

        if (!hint.isEmpty()) {
            drawCenteredString(guiGraphics, hint, screenWidth / 2, 20, 0xFFFFFF);
        }
    }

    /**
     * Helper to draw centered text.
     */
    private static void drawCenteredString(GuiGraphics guiGraphics, String text, int x, int y, int color) {
        net.minecraft.client.gui.Font font = net.minecraft.client.Minecraft.getInstance().font;
        int textWidth = font.width(text);
        guiGraphics.drawString(font, text, x - textWidth / 2, y, color);
    }
}
