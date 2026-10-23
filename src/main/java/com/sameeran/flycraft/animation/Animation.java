package com.sameeran.flycraft.animation;

import net.minecraft.client.model.PlayerModel;

/**
 * Base class representing an animation with duration and easing functions.
 *
 * Timing advances on the client tick; the pose is applied every frame
 * (after vanilla's setupAnim) using the partial tick for smooth motion.
 */
public abstract class Animation {
    private static final float TICK_SECONDS = 0.05f;

    protected float duration;
    protected float currentTime;
    protected boolean isPlaying;
    protected boolean loop;

    public Animation(float duration, boolean loop) {
        this.duration = duration;
        this.loop = loop;
        this.currentTime = 0;
        this.isPlaying = false;
    }

    public void start() {
        this.isPlaying = true;
        this.currentTime = 0;
    }

    public void stop() {
        this.isPlaying = false;
    }

    public void update(float deltaTime) {
        if (!isPlaying) return;

        currentTime += deltaTime;

        if (currentTime >= duration) {
            if (loop) {
                currentTime %= duration;
            } else {
                currentTime = duration;
                isPlaying = false;
            }
        }
    }

    /**
     * Get the progress of the animation from 0 to 1
     */
    public float getProgress() {
        return Math.min(currentTime / duration, 1.0f);
    }

    /**
     * Progress interpolated between ticks, for rendering
     */
    public float getProgress(float partialTick) {
        float t = currentTime + partialTick * TICK_SECONDS;
        return loop ? (t % duration) / duration : Math.min(t / duration, 1.0f);
    }

    /**
     * Apply easing function to progress
     */
    protected static float ease(EasingFunction easing, float progress) {
        return easing.apply(progress);
    }

    /**
     * Pose the model for the given progress (0 to 1). Called every frame
     * after vanilla has set up the model, so limbs can be overridden freely.
     */
    public abstract void apply(PlayerModel<?> model, float progress);

    public boolean isPlaying() {
        return isPlaying;
    }

    public float getCurrentTime() {
        return currentTime;
    }

    public float getDuration() {
        return duration;
    }

    public void reset() {
        currentTime = 0;
        isPlaying = false;
    }
}
