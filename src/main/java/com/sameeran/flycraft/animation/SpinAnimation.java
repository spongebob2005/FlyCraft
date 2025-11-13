package com.sameeran.flycraft.animation;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;

/**
 * Animation for spinning/dashing
 */
public class SpinAnimation extends Animation {
    private AbstractClientPlayer player;

    public SpinAnimation(float duration, AbstractClientPlayer player, PlayerModel<?> model) {
        super(duration, false);
        this.player = player;
    }

    @Override
    protected void onUpdate(float progress) {
        if (player == null) return;

        float eased = easeProgress(EasingFunction.Easing.EASE_OUT);
        float rotation = 360 * eased;

        // This affects visual rotation - applied in rendering
        player.yBodyRotO = rotation;
    }
}
