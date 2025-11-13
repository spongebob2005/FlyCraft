package com.sameeran.flycraft.animation;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;

/**
 * Animation for flying gesture - raises arms to sides
 */
public class FlyingAnimation extends Animation {
    private PlayerModel<?> model;

    public FlyingAnimation(float duration, AbstractClientPlayer player, PlayerModel<?> model) {
        super(duration, false);
        this.model = model;
    }

    @Override
    protected void onUpdate(float progress) {
        if (model == null) return;

        float eased = easeProgress(EasingFunction.Easing.EASE_IN_OUT);

        // Raise both arms
        model.rightArm.xRot = -1.5f * eased;
        model.leftArm.xRot = -1.5f * eased;

        // Rotate arms outward
        model.rightArm.zRot = 0.5f * eased;
        model.leftArm.zRot = -0.5f * eased;
    }
}
