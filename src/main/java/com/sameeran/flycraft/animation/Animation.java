package com.sameeran.flycraft.animation;

/**
 * Base class representing an animation with duration and easing functions
 */
public abstract class Animation {
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
                currentTime = 0;
            } else {
                currentTime = duration;
                isPlaying = false;
            }
        }

        onUpdate(getProgress());
    }

    /**
     * Get the progress of the animation from 0 to 1
     */
    public float getProgress() {
        return Math.min(currentTime / duration, 1.0f);
    }

    /**
     * Apply easing function to progress
     */
    public float easeProgress(EasingFunction easing) {
        return easing.apply(getProgress());
    }

    /**
     * Called each frame to update animation state
     */
    protected abstract void onUpdate(float progress);

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
