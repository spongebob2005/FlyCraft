package com.sameeran.flycraft.animation;

import net.minecraft.client.model.PlayerModel;

/**
 * Animation for jumping boost
 */
public class JumpBoostAnimation extends Animation {

    public JumpBoostAnimation(float duration) {
        super(duration, false);
    }

    @Override
    public void apply(PlayerModel<?> model, float progress) {
        float eased = ease(EasingFunction.Easing.BOUNCE, progress);
        // Dip down then spring back up (0 at both ends, deepest mid-way)
        float crouch = (float) Math.sin(eased * Math.PI);

        // Crouch: lower the upper body (model +y is down); legs stay planted
        float drop = crouch * 2;
        model.body.y += drop;
        model.head.y += drop;
        model.hat.y += drop;
        model.rightArm.y += drop;
        model.leftArm.y += drop;

        // Arms prepare for jump
        model.rightArm.xRot = -0.8f * (1 - eased);
        model.leftArm.xRot = -0.8f * (1 - eased);
    }
}
