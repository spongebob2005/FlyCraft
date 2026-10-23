package com.sameeran.flycraft.animation;

import net.minecraft.client.model.PlayerModel;

/**
 * Animation for spinning/dashing.
 *
 * Rotates the whole player rather than individual limbs, so the turn is
 * applied to the render pose stack by AnimationController (relative to the
 * player's current heading) instead of in apply().
 */
public class SpinAnimation extends Animation {

    public SpinAnimation(float duration) {
        super(duration, false);
    }

    @Override
    public void apply(PlayerModel<?> model, float progress) {
        // No limb changes — see getYawDegrees()
    }

    /**
     * Extra yaw (degrees) to add on top of the player's own rotation.
     */
    public float getYawDegrees(float progress) {
        return 360 * ease(EasingFunction.Easing.EASE_OUT, progress);
    }
}
