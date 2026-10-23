package com.sameeran.flycraft.animation;

import net.minecraft.client.model.PlayerModel;

/**
 * Animation for dancing
 */
public class DanceAnimation extends Animation {

    public DanceAnimation(float duration) {
        super(duration, true);
    }

    @Override
    public void apply(PlayerModel<?> model, float progress) {
        float sway = (float) Math.sin(progress * Math.PI * 4) * 0.3f;
        float hop = (float) Math.sin(progress * Math.PI * 4) * 0.2f;

        // Sway body
        model.body.xRot = sway * 0.2f;
        model.body.zRot = sway;

        // Move legs for dancing (offsets from vanilla's resting position)
        model.rightLeg.y += hop;
        model.leftLeg.y -= hop;
        model.rightLeg.xRot = hop * 0.5f;
        model.leftLeg.xRot = -hop * 0.5f;

        // Arm motion
        model.rightArm.xRot = -0.5f + sway;
        model.leftArm.xRot = -0.5f - sway;
    }
}
