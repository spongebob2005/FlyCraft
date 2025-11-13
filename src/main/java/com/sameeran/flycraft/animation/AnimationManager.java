package com.sameeran.flycraft.animation;

import java.util.*;

/**
 * Manages all active animations for the player
 */
public class AnimationManager {
    private static final Map<String, Animation> ACTIVE_ANIMATIONS = new HashMap<>();
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

        // Process animation queue
        if (!ANIMATION_QUEUE.isEmpty() && ANIMATION_QUEUE.get(0) != null) {
            String nextAnimation = ANIMATION_QUEUE.get(0);
            Animation animation = ACTIVE_ANIMATIONS.get(nextAnimation);
            if (animation != null && !animation.isPlaying()) {
                playAnimation(nextAnimation);
                ANIMATION_QUEUE.remove(0);
            }
        }
    }

    public static Animation getAnimation(String name) {
        return ACTIVE_ANIMATIONS.get(name);
    }

    public static boolean isAnimationPlaying(String name) {
        Animation animation = ACTIVE_ANIMATIONS.get(name);
        return animation != null && animation.isPlaying();
    }

    public static void clearAnimations() {
        ACTIVE_ANIMATIONS.clear();
        ANIMATION_QUEUE.clear();
    }

    public static Collection<Animation> getAllAnimations() {
        return ACTIVE_ANIMATIONS.values();
    }
}
