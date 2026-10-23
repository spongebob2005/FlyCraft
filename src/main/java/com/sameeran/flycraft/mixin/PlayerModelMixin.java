package com.sameeran.flycraft.mixin;

import com.sameeran.flycraft.animation.AnimationController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Applies FlyCraft animations after vanilla has posed the player model.
 *
 * Vanilla's setupAnim resets every limb each frame, so poses set anywhere
 * earlier (tick events, RenderPlayerEvent.Pre) are overwritten before drawing.
 */
@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin {

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void flycraft$applyAnimations(LivingEntity entity, float limbSwing, float limbSwingAmount,
                                          float ageInTicks, float netHeadYaw, float headPitch,
                                          CallbackInfo ci) {
        // The model instance is shared by every player renderer — only animate ourselves
        if (entity != Minecraft.getInstance().player) return;

        PlayerModel<?> model = (PlayerModel<?>) (Object) this;
        if (!AnimationController.applyPose(model, Minecraft.getInstance().getPartialTick())) return;

        // Outer skin layers normally copy the limbs inside setupAnim; redo it with our pose
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightSleeve.copyFrom(model.rightArm);
        model.leftPants.copyFrom(model.leftLeg);
        model.rightPants.copyFrom(model.rightLeg);
        model.jacket.copyFrom(model.body);
    }
}
