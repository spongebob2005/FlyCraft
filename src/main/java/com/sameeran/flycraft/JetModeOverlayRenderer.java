package com.sameeran.flycraft;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Renders jet mode texture overlay effects (vignette, speed lines, etc.)
 * similar to Actions and Stuff mod visual styling.
 */
@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT)
public class JetModeOverlayRenderer {

    // Note: These texture resource locations are reserved for future texture-based rendering.
    // Currently the mod uses procedural rendering for overlay effects.
    // private static final ResourceLocation JET_VIGNETTE = ResourceLocation.withDefaultNamespace("textures/overlay/jet_vignette.png");
    // private static final ResourceLocation SPEED_LINES = ResourceLocation.withDefaultNamespace("textures/overlay/speed_lines.png");
    // private static final ResourceLocation G_FORCE_BLUR = ResourceLocation.withDefaultNamespace("textures/overlay/g_force_blur.png");

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null || !player.isFallFlying() || !ClientEvents.isJetMode()) {
            return;
        }

        // Get current speed to scale visual effects
        double speed = player.getDeltaMovement().length();
        double maxSpeed = 2.8; // Must match FlightController.MAX_SPEED
        float speedFraction = Math.min((float) (speed / maxSpeed), 1.0f);

        // Render overlays based on speed intensity
        renderJetVignette(event, speedFraction);
        renderSpeedLines(event, speedFraction, player.tickCount);
        renderGForceBlur(event, speedFraction);
    }

    /**
     * Render a dark vignette around screen edges to simulate intense flight focus.
     */
    private static void renderJetVignette(RenderGuiEvent.Post event, float speedFraction) {
        PoseStack poseStack = event.getGuiGraphics().pose();
        Minecraft mc = Minecraft.getInstance();

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // Vignette opacity increases with speed
        int alpha = (int) (80 + speedFraction * 120); // 80 to 200 alpha
        int vignetteColor = (alpha << 24) | 0x1a1a1a; // dark gray with alpha

        // Draw semi-transparent dark rectangles at screen edges for vignette effect
        poseStack.pushPose();
        RenderSystem.enableBlend();

        // Top edge
        event.getGuiGraphics().fill(0, 0, screenWidth, 40, vignetteColor);
        // Bottom edge
        event.getGuiGraphics().fill(0, screenHeight - 40, screenWidth, screenHeight, vignetteColor);
        // Left edge
        event.getGuiGraphics().fill(0, 0, 40, screenHeight, vignetteColor);
        // Right edge
        event.getGuiGraphics().fill(screenWidth - 40, 0, screenWidth, screenHeight, vignetteColor);

        poseStack.popPose();
    }

    /**
     * Render horizontal speed lines that move faster as speed increases.
     */
    private static void renderSpeedLines(RenderGuiEvent.Post event, float speedFraction, int tickCount) {
        if (speedFraction < 0.3f) return; // Only show lines at decent speed

        PoseStack poseStack = event.getGuiGraphics().pose();
        Minecraft mc = Minecraft.getInstance();

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Calculate animation offset based on tick and speed
        int lineAlpha = (int) (50 * speedFraction); // Alpha scales with speed

        // Draw horizontal speed lines
        int lineColor = (lineAlpha << 24) | 0x00ccff; // Cyan with alpha
        int lineCount = 8;
        int lineSpacing = screenHeight / lineCount;

        for (int i = 0; i < lineCount; i++) {
            int y = (int) ((i * lineSpacing + tickCount * 2 * speedFraction) % screenHeight);
            event.getGuiGraphics().fill(0, y, screenWidth, y + 2, lineColor);
        }

        poseStack.popPose();
    }

    /**
     * Render a subtle blur/motion effect to simulate G-force during high-speed maneuvers.
     */
    private static void renderGForceBlur(RenderGuiEvent.Post event, float speedFraction) {
        if (speedFraction < 0.5f) return; // Only at higher speeds

        PoseStack poseStack = event.getGuiGraphics().pose();
        Minecraft mc = Minecraft.getInstance();

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Center flash/blur effect
        int blurAlpha = (int) (30 * (speedFraction - 0.5f) * 2); // Only shows after 50% speed

        // Radial gradient-like effect by drawing concentric rectangles
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        int maxRadius = Math.max(screenWidth, screenHeight) / 2;

        for (int r = maxRadius; r > 0; r -= 20) {
            int alpha = (int) (blurAlpha * (1.0f - (float) r / maxRadius));
            int color = (alpha << 24) | 0xffffff;
            event.getGuiGraphics().fill(
                centerX - r, centerY - r / 2,
                centerX + r, centerY + r / 2,
                color
            );
        }

        poseStack.popPose();
    }
}
