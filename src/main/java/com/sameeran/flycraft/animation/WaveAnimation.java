package com.sameeran.flycraft.animation;

import net.minecraft.client.model.PlayerModel;

/**
 * Animation for wave/greeting
 */
public class WaveAnimation extends Animation {

    public WaveAnimation(float duration) {
        super(duration, false);
    }

    @Override
    public void apply(PlayerModel<?> model, float progress) {
        float wave = (float) Math.sin(progress * Math.PI * 3) * 0.5f;

        // Wave right arm
        model.rightArm.xRot = -1.5f + wave;
        model.rightArm.zRot = 0.5f;

        // Keep left arm down
        model.leftArm.xRot = 0;
        model.leftArm.zRot = 0;
    }
}
