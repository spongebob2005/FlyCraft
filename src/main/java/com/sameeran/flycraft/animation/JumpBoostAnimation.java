package com.sameeran.flycraft.animation;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;

/**
 * Animation for jumping boost
 */
public class JumpBoostAnimation extends Animation {
    private PlayerModel<?> model;

    public JumpBoostAnimation(float duration, AbstractClientPlayer player, PlayerModel<?> model) {
        super(duration, false);
        this.model = model;
    }

    @Override
    protected void onUpdate(float progress) {
        if (model == null) return;

        float eased = easeProgress(EasingFunction.Easing.BOUNCE);

        // Crouch down then spring up
        model.body.y = -eased * 2;
        model.rightLeg.y = eased * 2;
        model.leftLeg.y = eased * 2;

        // Arms prepare for jump
        model.rightArm.xRot = -0.8f * (1 - eased);
        model.leftArm.xRot = -0.8f * (1 - eased);
    }
}
