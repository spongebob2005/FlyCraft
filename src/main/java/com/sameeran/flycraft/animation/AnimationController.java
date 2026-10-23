package com.sameeran.flycraft.animation;

import com.mojang.math.Axis;
import com.sameeran.flycraft.FlyCraftMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Drives animation timing (client tick) and rendering.
 *
 * Limb poses are applied from PlayerModelMixin after vanilla's setupAnim;
 * the whole-body spin is applied to the render pose stack here.
 */
@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT)
public class AnimationController {

    private static final float TICK_SECONDS = 0.05f;

    private static boolean wasFallFlying  = false;
    private static boolean spinPushedPose = false;

    static {
        // Ambient flight poses first so gestures override them (see AnimationManager)
        AnimationManager.registerAnimation("flying",     new FlyingAnimation(0.5f));
        AnimationManager.registerAnimation("glide",      new GlideAnimation(2.0f));
        AnimationManager.registerAnimation("spin",       new SpinAnimation(1.0f));
        AnimationManager.registerAnimation("jump_boost", new JumpBoostAnimation(0.4f));
        AnimationManager.registerAnimation("wave",       new WaveAnimation(1.0f));
        AnimationManager.registerAnimation("clap",       new ClapAnimation(1.5f));
        AnimationManager.registerAnimation("dance",      new DanceAnimation(2.0f));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level().isClientSide || event.phase != TickEvent.Phase.END) return;
        // Other players tick on the client too (as RemotePlayer) — only animate ourselves
        if (!(event.player instanceof LocalPlayer player)) return;

        updateFlightAnimations(player);
        AnimationManager.updateAnimations(TICK_SECONDS);
    }

    /**
     * Arms-out "flying" pose on take-off, then a looping glide while airborne.
     */
    private static void updateFlightAnimations(LocalPlayer player) {
        boolean fallFlying = player.isFallFlying();

        if (fallFlying && !wasFallFlying) {
            AnimationManager.playAnimation("flying");
        } else if (fallFlying && !isAnimationPlaying("flying") && !isAnimationPlaying("glide")) {
            AnimationManager.playAnimation("glide");
        } else if (!fallFlying && wasFallFlying) {
            AnimationManager.stopAnimation("flying");
            AnimationManager.stopAnimation("glide");
        }
        wasFallFlying = fallFlying;
    }

    /**
     * Pose the local player's model with every playing animation.
     *
     * @return true if any animation changed the model
     */
    public static boolean applyPose(PlayerModel<?> model, float partialTick) {
        boolean applied = false;
        for (Animation animation : AnimationManager.getAllAnimations()) {
            if (animation.isPlaying()) {
                animation.apply(model, animation.getProgress(partialTick));
                applied = true;
            }
        }
        return applied;
    }

    // LOWEST so we only push once no other handler can still cancel Pre (which would skip Post)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (event.getEntity() != Minecraft.getInstance().player) return;
        if (!(AnimationManager.getAnimation("spin") instanceof SpinAnimation spin) || !spin.isPlaying()) return;

        event.getPoseStack().pushPose();
        event.getPoseStack().mulPose(Axis.YP.rotationDegrees(
            spin.getYawDegrees(spin.getProgress(event.getPartialTick()))));
        spinPushedPose = true;
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (spinPushedPose && event.getEntity() == Minecraft.getInstance().player) {
            event.getPoseStack().popPose();
            spinPushedPose = false;
        }
    }

    public static void playAnimation(String name)            { AnimationManager.playAnimation(name); }
    public static void queueAnimation(String name)           { AnimationManager.queueAnimation(name); }
    public static void stopAnimation(String name)            { AnimationManager.stopAnimation(name); }
    public static void stopAllAnimations()                   { AnimationManager.stopAllAnimations(); }
    public static boolean isAnimationPlaying(String name)    { return AnimationManager.isAnimationPlaying(name); }
}
