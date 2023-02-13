package com.kuver.makeshiftguns.client.render.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.mrcrayfish.guns.Config;
import com.mrcrayfish.guns.client.handler.ReloadHandler;
import com.mrcrayfish.guns.client.render.pose.AimPose;
import com.mrcrayfish.guns.client.render.pose.LimbPose;
import com.mrcrayfish.guns.client.render.pose.WeaponPose;
import com.mrcrayfish.guns.client.util.RenderUtil;
import com.mrcrayfish.guns.common.GripType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HandgunPose extends WeaponPose {
    public HandgunPose() {
    }
    // todo fix this piece of crap pose
    protected AimPose getUpPose() {
        AimPose upPose = new AimPose();
        upPose.getIdle().setRenderYawOffset(0F).setItemRotation(new Vector3f(40.0F, 0.0F, 30.0F))
                .setRightArm((new LimbPose()).setRotationAngleX(-140.0F).setRotationAngleY(-55.0F).setRotationPointX(-5.0F).setRotationPointY(3.0F).setRotationPointZ(0.0F))
                .setLeftArm((new LimbPose()).setRotationAngleX(-170.0F).setRotationAngleY(-20.0F).setRotationAngleZ(-35.0F).setRotationPointY(1.0F).setRotationPointZ(0.0F));

        upPose.getAiming().setRenderYawOffset(0F).setItemRotation(new Vector3f(40.0F, 0.0F, 30.0F))
                .setRightArm((new LimbPose()).setRotationAngleX(-140.0F).setRotationAngleY(-55.0F).setRotationPointX(-5.0F).setRotationPointY(3.0F).setRotationPointZ(0.0F))
                .setLeftArm((new LimbPose()).setRotationAngleX(-170.0F).setRotationAngleY(-20.0F).setRotationAngleZ(-35.0F).setRotationPointY(1.0F).setRotationPointZ(0.0F));
        return upPose;
    }

    protected AimPose getForwardPose() {
        AimPose forwardPose = new AimPose();
        forwardPose.getIdle().setRenderYawOffset(0F).setItemRotation(new Vector3f(5.0F, -35.0F, 0.0F))
                .setRightArm((new LimbPose()).setRotationAngleX(-85.0F).setRotationAngleY(-35.0F).setRotationAngleZ(0.0F).setRotationPointX(-5.0F).setRotationPointY(2.0F))
                .setLeftArm((new LimbPose()).setRotationAngleX(-90.0F).setRotationAngleY(35.0F).setRotationAngleZ(0.0F).setRotationPointY(2.0F).setRotationPointZ(0.0F));

        forwardPose.getAiming().setRenderYawOffset(0F).setItemRotation(new Vector3f(5.0F, -35.0F, 0.0F))
                .setRightArm((new LimbPose()).setRotationAngleX(-85.0F).setRotationAngleY(-35.0F).setRotationAngleZ(0.0F).setRotationPointX(-5.0F).setRotationPointY(2.0F))
                .setLeftArm((new LimbPose()).setRotationAngleX(-90.0F).setRotationAngleY(35.0F).setRotationAngleZ(0.0F).setRotationPointY(2.0F).setRotationPointZ(0.0F));
        return forwardPose;
    }

    protected AimPose getDownPose() {
        AimPose downPose = new AimPose();
        downPose.getIdle().setRenderYawOffset(0F).setItemRotation(new Vector3f(-20.0F, -5.0F, -10.0F)).setItemTranslate(new Vector3f(0.0F, -0.5F, 1.0F))
                .setRightArm((new LimbPose()).setRotationAngleX(-30.0F).setRotationAngleY(-65.0F).setRotationAngleZ(0.0F).setRotationPointX(-5.0F).setRotationPointY(1.0F))
                .setLeftArm((new LimbPose()).setRotationAngleX(-10.0F).setRotationAngleY(-20.0F).setRotationAngleZ(30.0F).setRotationPointY(5.0F).setRotationPointZ(0.0F));

        downPose.getAiming().setRenderYawOffset(0F).setItemRotation(new Vector3f(-20.0F, -5.0F, -10.0F)).setItemTranslate(new Vector3f(0.0F, -0.5F, 1.0F))
                .setRightArm((new LimbPose()).setRotationAngleX(-30.0F).setRotationAngleY(-65.0F).setRotationAngleZ(0.0F).setRotationPointX(-5.0F).setRotationPointY(1.0F))
                .setLeftArm((new LimbPose()).setRotationAngleX(-10.0F).setRotationAngleY(-20.0F).setRotationAngleZ(30.0F).setRotationPointY(5.0F).setRotationPointZ(0.0F));
        return downPose;
    }

    @OnlyIn(Dist.CLIENT)
    public void applyPlayerModelRotation(Player player, ModelPart rightArm, ModelPart leftArm, ModelPart head, InteractionHand hand, float aimProgress) {
        super.applyPlayerModelRotation(player, rightArm, leftArm, head, hand, aimProgress);
        float angle = this.getPlayerPitch(player);
        head.xRot = (float) Math.toRadians((double) angle > 0.0 ? (double) (angle * 70.0F) : (double) (angle * 90.0F));
    }

    @OnlyIn(Dist.CLIENT)
    public void applyPlayerPreRender(Player player, InteractionHand hand, float aimProgress, PoseStack poseStack, MultiBufferSource buffer) {
        if ((Boolean) Config.CLIENT.display.oldAnimations.get()) {
            boolean right = Minecraft.getInstance().options.mainHand == HumanoidArm.RIGHT ? hand == InteractionHand.MAIN_HAND : hand == InteractionHand.OFF_HAND;
            player.yBodyRotO = player.yRotO + (right ? 25.0F : -25.0F) + aimProgress * (right ? 20.0F : -20.0F);
            player.yBodyRot = player.getYRot() + (right ? 25.0F : -25.0F) + aimProgress * (right ? 20.0F : -20.0F);
        } else {
            super.applyPlayerPreRender(player, hand, aimProgress, poseStack, buffer);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void applyHeldItemTransforms(Player player, InteractionHand hand, float aimProgress, PoseStack poseStack, MultiBufferSource buffer) {
        if ((Boolean) Config.CLIENT.display.oldAnimations.get()) {
            if (hand == InteractionHand.MAIN_HAND) {
                boolean right = Minecraft.getInstance().options.mainHand == HumanoidArm.RIGHT ? hand == InteractionHand.MAIN_HAND : hand == InteractionHand.OFF_HAND;
                poseStack.translate(0.0, 0.0, 0.05);
                float invertRealProgress = 1.0F - aimProgress;
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(25.0F * invertRealProgress * (right ? 1.0F : -1.0F)));
                poseStack.mulPose(Vector3f.YP.rotationDegrees((30.0F * invertRealProgress + aimProgress * -20.0F) * (right ? 1.0F : -1.0F)));
                poseStack.mulPose(Vector3f.XP.rotationDegrees(25.0F * invertRealProgress + aimProgress * 5.0F));
            }
        } else {
            super.applyHeldItemTransforms(player, hand, aimProgress, poseStack, buffer);
        }

    }

    public void renderFirstPersonArms(Player player, HumanoidArm hand, ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, int light, float partialTicks) {
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, player.level, player, 0);
        float translateX = model.getTransforms().firstPersonRightHand.translation.x();
        int side = hand.getOpposite() == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate((double) (translateX * (float) side), 0.0, 0.0);
        boolean slim = Minecraft.getInstance().player.getModelName().equals("slim");
        float armWidth = slim ? 3.0F : 4.0F;
        poseStack.pushPose();

        float reloadProgress = ReloadHandler.get().getReloadProgress(partialTicks);
        // use this in a bolt action rifle pose! shit works great
        ItemCooldowns tracker = Minecraft.getInstance().player.getCooldowns();
        float cooldown = tracker.getCooldownPercent(stack.getItem(), Minecraft.getInstance().getFrameTime());


        cooldown = (float) ease(cooldown);
        //left arm renders here
//        poseStack.translate((double) reloadProgress * 0.5, (double) (-reloadProgress), (double) (-reloadProgress) * 0.5);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.translate(0.3 * (double) side, -0.1, -0.5);
        poseStack.translate((double) armWidth / 2.0 * 0.0625 * (double) side, 0.0, 0.0);
        poseStack.translate(-0.3125 * (double) side, -0.1, -0.4375);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(80.0F));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(15.0F * (float) (-side)));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(15.0F * (float) (-side)));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-35.0F));
        RenderUtil.renderFirstPersonArm((LocalPlayer) player, hand.getOpposite(), poseStack, buffer, light);
        poseStack.popPose();
        poseStack.pushPose();
        //right arm renders here
        poseStack.translate(0.0, 0.14, -0.59);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.translate(-0.25 * (double) side, 0.0, 0.0);
        poseStack.translate(-((double) armWidth / 2.0) * 0.0625 * (double) side, 0.0, 0.0);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(80.0F));

        //this is the part that makes the arm move after shooting
//        poseStack.translate(0.0, (double) (-cooldown) * 0.2, (double) (-cooldown) * 0.2);
        RenderUtil.renderFirstPersonArm((LocalPlayer) player, hand, poseStack, buffer, light);
        poseStack.popPose();
    }

    public boolean applyOffhandTransforms(Player player, PlayerModel model, ItemStack stack, PoseStack poseStack, float partialTicks) {
        return GripType.applyBackTransforms(player, poseStack);
    }

    private double ease(double x) {
        return 1 - Math.pow(1 - (2 * x), 4);
    }
}