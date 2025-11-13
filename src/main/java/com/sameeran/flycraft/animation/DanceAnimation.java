package com.sameeran.flycraft.animation;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;

/**
 * Animation for dancing
 */
public class DanceAnimation extends Animation {
    private PlayerModel<?> model;

    public DanceAnimation(float duration, AbstractClientPlayer player, PlayerModel<?> model) {
        super(duration, true);
        this.model = model;
    }

    @Override
    protected void onUpdate(float progress) {
        if (model == null) return;

        float sway = (float) Math.sin(progress * Math.PI * 4) * 0.3f;
        float hop = (float) Math.sin(progress * Math.PI * 4) * 0.2f;

        // Sway body
        model.body.xRot = sway * 0.2f;
        model.body.zRot = sway;

        // Move legs for dancing
        model.rightLeg.y = hop;
        model.leftLeg.y = -hop;
        model.rightLeg.xRot = hop * 0.5f;
        model.leftLeg.xRot = -hop * 0.5f;

        // Arm motion
        model.rightArm.xRot = -0.5f + sway;
        model.leftArm.xRot = -0.5f - sway;
    }
}
