package com.sameeran.flycraft.animation;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.sameeran.flycraft.FlyCraftMod;

/**
 * Handles animation rendering and updates for players
 */
@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT)
public class AnimationController {
    private static LocalPlayer lastPlayer = null;

    /**
     * Initialize animations for a player
     */
    public static void initializePlayerAnimations(LocalPlayer player, PlayerModel<?> model) {
        AnimationManager.clearAnimations();

        // Register all animations
        AnimationManager.registerAnimation("flying", new FlyingAnimation(0.5f, player, model));
        AnimationManager.registerAnimation("glide", new GlideAnimation(2.0f, player, model));
        AnimationManager.registerAnimation("spin", new SpinAnimation(1.0f, player, model));
        AnimationManager.registerAnimation("jump_boost", new JumpBoostAnimation(0.4f, player, model));
        AnimationManager.registerAnimation("wave", new WaveAnimation(1.0f, player, model));
        AnimationManager.registerAnimation("clap", new ClapAnimation(1.5f, player, model));
        AnimationManager.registerAnimation("dance", new DanceAnimation(2.0f, player, model));
    }

    /**
     * Update animations every tick
     */
    @SubscribeEvent
    public static void onPlayerTick(net.minecraftforge.event.TickEvent.PlayerTickEvent event) {
        if (event.player.level().isClientSide && event.phase == net.minecraftforge.event.TickEvent.Phase.END) {
            AnimationManager.updateAnimations(0.05f); // 50ms per tick
        }
    }

    /**
     * Apply animations during rendering
     */
    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        // Store player reference for animation application
        lastPlayer = (LocalPlayer) event.getEntity();
    }

    /**
     * Play an animation by name
     */
    public static void playAnimation(String animationName) {
        AnimationManager.playAnimation(animationName);
    }

    /**
     * Queue an animation to play after current animation
     */
    public static void queueAnimation(String animationName) {
        AnimationManager.queueAnimation(animationName);
    }

    /**
     * Stop a specific animation
     */
    public static void stopAnimation(String animationName) {
        AnimationManager.stopAnimation(animationName);
    }

    /**
     * Stop all active animations
     */
    public static void stopAllAnimations() {
        AnimationManager.stopAllAnimations();
    }

    /**
     * Check if animation is currently playing
     */
    public static boolean isAnimationPlaying(String animationName) {
        return AnimationManager.isAnimationPlaying(animationName);
    }

    /**
     * Get last player that was rendering
     */
    public static LocalPlayer getLastPlayer() {
        return lastPlayer;
    }
}
