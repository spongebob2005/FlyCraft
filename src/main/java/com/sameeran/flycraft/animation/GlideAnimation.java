package com.sameeran.flycraft.animation;

import net.minecraft.client.model.PlayerModel;

/**
 * Animation for falling/gliding
 */
public class GlideAnimation extends Animation {

    public GlideAnimation(float duration) {
        super(duration, true);
    }

    @Override
    public void apply(PlayerModel<?> model, float progress) {
        // Gentle wave motion for arms while gliding
        float wave = (float) Math.sin(progress * Math.PI * 2) * 0.3f;

        model.rightArm.xRot = -0.5f + wave;
        model.leftArm.xRot = -0.5f + wave;
        model.rightArm.zRot = 0.2f;
        model.leftArm.zRot = -0.2f;

        // Body lean
        model.body.xRot = 0.3f;
    }
}
