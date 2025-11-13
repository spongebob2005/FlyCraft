package com.sameeran.flycraft.animation;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;

/**
 * Animation for falling/gliding
 */
public class GlideAnimation extends Animation {
    private PlayerModel<?> model;

    public GlideAnimation(float duration, AbstractClientPlayer player, PlayerModel<?> model) {
        super(duration, true);
        this.model = model;
    }

    @Override
    protected void onUpdate(float progress) {
        if (model == null) return;

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
