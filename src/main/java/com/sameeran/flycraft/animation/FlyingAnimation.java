package com.sameeran.flycraft.animation;

import net.minecraft.client.model.PlayerModel;

/**
 * Animation for flying gesture - raises arms to sides
 */
public class FlyingAnimation extends Animation {

    public FlyingAnimation(float duration) {
        super(duration, false);
    }

    @Override
    public void apply(PlayerModel<?> model, float progress) {
        float eased = ease(EasingFunction.Easing.EASE_IN_OUT, progress);

        // Raise both arms
        model.rightArm.xRot = -1.5f * eased;
        model.leftArm.xRot = -1.5f * eased;

        // Rotate arms outward
        model.rightArm.zRot = 0.5f * eased;
        model.leftArm.zRot = -0.5f * eased;
    }
}
