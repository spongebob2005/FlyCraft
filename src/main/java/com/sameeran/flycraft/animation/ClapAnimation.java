package com.sameeran.flycraft.animation;

import net.minecraft.client.model.PlayerModel;

/**
 * Animation for clapping
 */
public class ClapAnimation extends Animation {

    public ClapAnimation(float duration) {
        super(duration, false);
    }

    @Override
    public void apply(PlayerModel<?> model, float progress) {
        // Clap hands together repeatedly
        float clapPhase = (progress * 8) % 1.0f; // 4 claps during duration
        float clapDistance = clapPhase < 0.5f ? clapPhase * 2 : (1 - clapPhase) * 2;

        model.rightArm.xRot = -1.2f;
        model.rightArm.zRot = -0.5f + clapDistance * 1.5f;

        model.leftArm.xRot = -1.2f;
        model.leftArm.zRot = 0.5f - clapDistance * 1.5f;
    }
}
