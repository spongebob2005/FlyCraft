package com.sameeran.flycraft.animation;

import java.util.*;

/**
 * Manages all active animations for the player
 */
public class AnimationManager {
    // Insertion order is apply order: later animations override earlier ones,
    // so register ambient poses (glide) before gestures (wave, clap, ...)
    private static final Map<String, Animation> ACTIVE_ANIMATIONS = new LinkedHashMap<>();
    private static final List<String> ANIMATION_QUEUE = new ArrayList<>();

    public static void registerAnimation(String name, Animation animation) {
        ACTIVE_ANIMATIONS.put(name, animation);
    }

    public static void playAnimation(String name) {
        Animation animation = ACTIVE_ANIMATIONS.get(name);
        if (animation != null) {
            animation.reset();
            animation.start();
        }
    }

    public static void queueAnimation(String name) {
        if (!ANIMATION_QUEUE.contains(name)) {
            ANIMATION_QUEUE.add(name);
        }
    }

    public static void stopAnimation(String name) {
        Animation animation = ACTIVE_ANIMATIONS.get(name);
        if (animation != null) {
            animation.stop();
        }
    }

    public static void stopAllAnimations() {
        ACTIVE_ANIMATIONS.values().forEach(Animation::stop);
        ANIMATION_QUEUE.clear();
    }

    public static void updateAnimations(float deltaTime) {
        // Update all active animations
        ACTIVE_ANIMATIONS.values().forEach(animation -> animation.update(deltaTime));

        // Start the next queued animation once nothing else is playing
        if (!ANIMATION_QUEUE.isEmpty() && !isAnyAnimationPlaying()) {
            playAnimation(ANIMATION_QUEUE.remove(0));
        }
    }

    public static Animation getAnimation(String name) {
        return ACTIVE_ANIMATIONS.get(name);
    }

    public static boolean isAnimationPlaying(String name) {
        Animation animation = ACTIVE_ANIMATIONS.get(name);
        return animation != null && animation.isPlaying();
    }

    public static boolean isAnyAnimationPlaying() {
        for (Animation animation : ACTIVE_ANIMATIONS.values()) {
            if (animation.isPlaying()) return true;
        }
        return false;
    }

    public static void clearAnimations() {
        ACTIVE_ANIMATIONS.clear();
        ANIMATION_QUEUE.clear();
    }

    public static Collection<Animation> getAllAnimations() {
        return ACTIVE_ANIMATIONS.values();
    }
}
