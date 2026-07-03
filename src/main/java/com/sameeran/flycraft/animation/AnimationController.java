package com.sameeran.flycraft.animation;

import com.sameeran.flycraft.FlyCraftMod;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles animation rendering and tick updates.
 * System.out.println calls replaced with proper logger.
 */
@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT)
public class AnimationController {

    private static LocalPlayer lastPlayer   = null;
    private static boolean     initialized  = false;

    public static void initializePlayerAnimations(LocalPlayer player, PlayerModel<?> model) {
        AnimationManager.clearAnimations();
        AnimationManager.registerAnimation("flying",     new FlyingAnimation(0.5f,  player, model));
        AnimationManager.registerAnimation("glide",      new GlideAnimation(2.0f,   player, model));
        AnimationManager.registerAnimation("spin",       new SpinAnimation(1.0f,    player, model));
        AnimationManager.registerAnimation("jump_boost", new JumpBoostAnimation(0.4f, player, model));
        AnimationManager.registerAnimation("wave",       new WaveAnimation(1.0f,    player, model));
        AnimationManager.registerAnimation("clap",       new ClapAnimation(1.5f,    player, model));
        AnimationManager.registerAnimation("dance",      new DanceAnimation(2.0f,   player, model));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level().isClientSide || event.phase != TickEvent.Phase.END) return;
        LocalPlayer player = (LocalPlayer) event.player;

        if (!initialized || lastPlayer != player) {
            initialized = true;
            lastPlayer  = player;
            FlyCraftMod.LOGGER.debug("Animation system ready for {}", player.getName().getString());
        }
        AnimationManager.updateAnimations(0.05f);
    }

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        LocalPlayer player = (LocalPlayer) event.getEntity();
        lastPlayer = player;

        if (!initialized) {
            try {
                PlayerModel<?> model = (PlayerModel<?>) event.getRenderer().getModel();
                initializePlayerAnimations(player, model);
                initialized = true;
                FlyCraftMod.LOGGER.debug("Animations initialized via render event");
            } catch (Exception e) {
                FlyCraftMod.LOGGER.error("Failed to initialize animations: {}", e.getMessage());
            }
        }
    }

    public static void playAnimation(String name)            { AnimationManager.playAnimation(name); }
    public static void queueAnimation(String name)           { AnimationManager.queueAnimation(name); }
    public static void stopAnimation(String name)            { AnimationManager.stopAnimation(name); }
    public static void stopAllAnimations()                   { AnimationManager.stopAllAnimations(); }
    public static boolean isAnimationPlaying(String name)    { return AnimationManager.isAnimationPlaying(name); }
    public static LocalPlayer getLastPlayer()                { return lastPlayer; }
}
