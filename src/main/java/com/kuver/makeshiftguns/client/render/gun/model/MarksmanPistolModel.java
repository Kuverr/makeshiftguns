package com.kuver.makeshiftguns.client.render.gun.model;

import com.kuver.makeshiftguns.client.SpecialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.guns.client.render.gun.IOverrideModel;
import com.mrcrayfish.guns.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;

public class MarksmanPistolModel implements IOverrideModel {
    @Override
    public void render(
            float partialTicks,
            ItemTransforms.TransformType transformType,
            ItemStack stack,
            ItemStack parent,
            LivingEntity entity,
            PoseStack matrixStack,
            MultiBufferSource buffer,
            int light,
            int overlay) {

        RenderUtil.renderModel(SpecialModels.MARKSMAN_PISTOL_MAIN.getModel(), stack, matrixStack, buffer, light, overlay);

        if (entity.equals(Minecraft.getInstance().player)) {

            //Always push.
            matrixStack.pushPose();
            //Don't touch this, it's better to use the display options in Blockbench.
//            matrixStack.translate(0, -5.8 * 0.0625, 0);
            //Gets the cooldown tracker for the item. Items like swords and enderpearls also have this.
            ItemCooldowns tracker = Minecraft.getInstance().player.getCooldowns();
            float cooldown = tracker.getCooldownPercent(stack.getItem(), Minecraft.getInstance().getFrameTime());
            cooldown = (float) ease(cooldown);

            // keeps the bolt open after the last shot
            CompoundTag tag = stack.getTag();
            if (tag.getInt("AmmoCount") == 0 && cooldown <= 0.5) {
//                matrixStack.translate(0, 0, 0.05);
                cooldown = 0.5F;
            }

//            System.out.println(tag);

            /*
             * We are moving whatever part is moving.
             * X,Y,Z, use Z for moving back and forth.
             * The higher the number, the shorter the distance.
             */
            matrixStack.translate(0, 0, cooldown / 10);
//            matrixStack.translate(0, 5.8 * 0.0625, 0);
            //Renders the moving part of the gun.
            RenderUtil.renderModel(SpecialModels.MARKSMAN_PISTOL_BOLT.getModel(), stack, matrixStack, buffer, light, overlay);
            //Always pop
            matrixStack.popPose();

        }
    }

    private double ease(double x) {
        return 1 - Math.pow(1 - (2 * x), 4);
    }
}