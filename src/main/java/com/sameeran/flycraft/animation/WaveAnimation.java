package com.sameeran.flycraft.animation;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;

/**
 * Animation for wave/greeting
 */
public class WaveAnimation extends Animation {
    private PlayerModel<?> model;

    public WaveAnimation(float duration, AbstractClientPlayer player, PlayerModel<?> model) {
        super(duration, false);
        this.model = model;
    }

    @Override
    protected void onUpdate(float progress) {
        if (model == null) return;

        float wave = (float) Math.sin(progress * Math.PI * 3) * 0.5f;

        // Wave right arm
        model.rightArm.xRot = -1.5f + wave;
        model.rightArm.zRot = 0.5f;

        // Keep left arm down
        model.leftArm.xRot = 0;
        model.leftArm.zRot = 0;
    }
}
