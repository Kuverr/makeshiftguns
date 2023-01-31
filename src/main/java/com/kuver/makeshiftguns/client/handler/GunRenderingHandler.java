//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuver.makeshiftguns.client.handler;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mrcrayfish.guns.Config;
import com.mrcrayfish.guns.client.GunModel;
import com.mrcrayfish.guns.client.GunRenderType;
import com.mrcrayfish.guns.client.SwayType;
import com.mrcrayfish.guns.client.handler.AimingHandler;
import com.mrcrayfish.guns.client.handler.RecoilHandler;
import com.mrcrayfish.guns.client.handler.ReloadHandler;
import com.mrcrayfish.guns.client.render.gun.IOverrideModel;
import com.mrcrayfish.guns.client.render.gun.ModelOverrides;
import com.mrcrayfish.guns.client.util.PropertyHelper;
import com.mrcrayfish.guns.client.util.RenderUtil;
import com.mrcrayfish.guns.common.GripType;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.common.properties.SightAnimation;
import com.mrcrayfish.guns.event.GunFireEvent;
import com.mrcrayfish.guns.init.ModSyncedDataKeys;
import com.mrcrayfish.guns.item.GrenadeItem;
import com.mrcrayfish.guns.item.GunItem;
import com.mrcrayfish.guns.item.attachment.IAttachment;
import com.mrcrayfish.guns.item.attachment.IBarrel;
import com.mrcrayfish.guns.item.attachment.IAttachment.Type;
import com.mrcrayfish.guns.item.attachment.impl.Barrel;
import com.mrcrayfish.guns.item.attachment.impl.Scope;
import com.mrcrayfish.guns.util.GunEnchantmentHelper;
import com.mrcrayfish.guns.util.GunModifierHelper;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class GunRenderingHandler {
    private static GunRenderingHandler instance;
    public static final ResourceLocation MUZZLE_FLASH_TEXTURE = new ResourceLocation("cgm", "textures/effect/muzzle_flash.png");
    private final Random random = new Random();
    private final Set<Integer> entityIdForMuzzleFlash = new HashSet();
    private final Set<Integer> entityIdForDrawnMuzzleFlash = new HashSet();
    private final Map<Integer, Float> entityIdToRandomValue = new HashMap();
    private int sprintTransition;
    private int prevSprintTransition;
    private int sprintCooldown;
    private float sprintIntensity;
    private float offhandTranslate;
    private float prevOffhandTranslate;
    private Field equippedProgressMainHandField;
    private Field prevEquippedProgressMainHandField;
    private float immersiveRoll;
    private float prevImmersiveRoll;
    private float fallSway;
    private float prevFallSway;
    private boolean usedConfiguredFov;
    @Nullable
    private ItemStack renderingWeapon;

    public static GunRenderingHandler get() {
        if (instance == null) {
            instance = new GunRenderingHandler();
        }

        return instance;
    }

    private GunRenderingHandler() {
    }

    @Nullable
    public ItemStack getRenderingWeapon() {
        return this.renderingWeapon;
    }

    public void setUsedConfiguredFov(boolean value) {
        this.usedConfiguredFov = value;
    }

    public boolean getUsedConfiguredFov() {
        return this.usedConfiguredFov;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == Phase.END) {
            this.updateSprinting();
            this.updateMuzzleFlash();
            this.updateOffhandTranslate();
            this.updateImmersiveCamera();
        }
    }

    private void updateSprinting() {
        this.prevSprintTransition = this.sprintTransition;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.isSprinting() && !(Boolean)ModSyncedDataKeys.SHOOTING.getValue(mc.player) && !(Boolean)ModSyncedDataKeys.RELOADING.getValue(mc.player) && !AimingHandler.get().isAiming() && this.sprintCooldown == 0) {
            if (this.sprintTransition < 5) {
                ++this.sprintTransition;
            }
        } else if (this.sprintTransition > 0) {
            --this.sprintTransition;
        }

        if (this.sprintCooldown > 0) {
            --this.sprintCooldown;
        }

    }

    private void updateMuzzleFlash() {
        this.entityIdForMuzzleFlash.removeAll(this.entityIdForDrawnMuzzleFlash);
        this.entityIdToRandomValue.keySet().removeAll(this.entityIdForDrawnMuzzleFlash);
        this.entityIdForDrawnMuzzleFlash.clear();
        this.entityIdForDrawnMuzzleFlash.addAll(this.entityIdForMuzzleFlash);
    }

    private void updateOffhandTranslate() {
        this.prevOffhandTranslate = this.offhandTranslate;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            boolean down = false;
            ItemStack heldItem = mc.player.getMainHandItem();
            if (heldItem.getItem() instanceof GunItem) {
                Gun modifiedGun = ((GunItem)heldItem.getItem()).getModifiedGun(heldItem);
                down = !modifiedGun.getGeneral().getGripType().getHeldAnimation().canRenderOffhandItem();
            }

            float direction = down ? -0.3F : 0.3F;
            this.offhandTranslate = Mth.clamp(this.offhandTranslate + direction, 0.0F, 1.0F);
        }
    }

    @SubscribeEvent
    public void onGunFire(GunFireEvent.Post event) {
        if (event.isClient()) {
            this.sprintTransition = 0;
            this.sprintCooldown = 20;
            ItemStack heldItem = event.getStack();
            GunItem gunItem = (GunItem)heldItem.getItem();
            Gun modifiedGun = gunItem.getModifiedGun(heldItem);
            if (modifiedGun.getDisplay().getFlash() != null) {
                this.showMuzzleFlashForPlayer(Minecraft.getInstance().player.getId());
            }

        }
    }

    public void showMuzzleFlashForPlayer(int entityId) {
        this.entityIdForMuzzleFlash.add(entityId);
        this.entityIdToRandomValue.put(entityId, this.random.nextFloat());
    }

    @SubscribeEvent
    public void onComputeFov(EntityViewRenderEvent.FieldOfView event) {
        if (!this.usedConfiguredFov) {
            LocalPlayer player = (LocalPlayer)Objects.requireNonNull(Minecraft.getInstance().player);
            ItemStack heldItem = player.getMainHandItem();
            Item var5 = heldItem.getItem();
            if (var5 instanceof GunItem) {
                GunItem gunItem = (GunItem)var5;
                Gun modifiedGun = gunItem.getModifiedGun(heldItem);
                if (modifiedGun.canAimDownSight()) {
                    if (!(AimingHandler.get().getNormalisedAdsProgress() <= 0.0)) {
                        double time = AimingHandler.get().getNormalisedAdsProgress();
                        SightAnimation sightAnimation = PropertyHelper.getSightAnimations(heldItem, modifiedGun);
                        time = sightAnimation.getViewportCurve().apply(time);
                        double viewportFov = PropertyHelper.getViewportFov(heldItem, modifiedGun);
                        double newFov = viewportFov > 0.0 ? viewportFov : event.getFOV();
                        event.setFOV(Mth.lerp(time, event.getFOV(), newFov));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderHandEvent event) {
        PoseStack poseStack = event.getPoseStack();
        boolean right = Minecraft.getInstance().options.mainHand == HumanoidArm.RIGHT ? event.getHand() == InteractionHand.MAIN_HAND : event.getHand() == InteractionHand.OFF_HAND;
        HumanoidArm hand = right ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
        ItemStack heldItem = event.getItemStack();
        if (event.getHand() == InteractionHand.OFF_HAND) {
            if (heldItem.getItem() instanceof GunItem) {
                event.setCanceled(true);
                return;
            }

            float offhand = 1.0F - Mth.lerp(event.getPartialTicks(), this.prevOffhandTranslate, this.offhandTranslate);
            poseStack.translate(0.0, (double)(offhand * -0.6F), 0.0);
            Player player = Minecraft.getInstance().player;
            if (player != null && player.getMainHandItem().getItem() instanceof GunItem) {
                Gun modifiedGun = ((GunItem)player.getMainHandItem().getItem()).getModifiedGun(player.getMainHandItem());
                if (!modifiedGun.getGeneral().getGripType().getHeldAnimation().canRenderOffhandItem()) {
                    return;
                }
            }

            poseStack.translate(0.0, -1.0 * AimingHandler.get().getNormalisedAdsProgress(), 0.0);
        }

        Item var31 = heldItem.getItem();
        if (var31 instanceof GunItem gunItem) {
            event.setCanceled(true);
            ItemStack overrideModel = ItemStack.EMPTY;
            if (heldItem.getTag() != null && heldItem.getTag().contains("Model", 10)) {
                overrideModel = ItemStack.of(heldItem.getTag().getCompound("Model"));
            }

            LocalPlayer player = (LocalPlayer)Objects.requireNonNull(Minecraft.getInstance().player);
            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(overrideModel.isEmpty() ? heldItem : overrideModel, player.level, player, 0);
            float scaleX = model.getTransforms().firstPersonRightHand.scale.x();
            float scaleY = model.getTransforms().firstPersonRightHand.scale.y();
            float scaleZ = model.getTransforms().firstPersonRightHand.scale.z();
            float translateX = model.getTransforms().firstPersonRightHand.translation.x();
            float translateY = model.getTransforms().firstPersonRightHand.translation.y();
            float translateZ = model.getTransforms().firstPersonRightHand.translation.z();
            poseStack.pushPose();
            Gun modifiedGun = gunItem.getModifiedGun(heldItem);
            if (AimingHandler.get().getNormalisedAdsProgress() > 0.0 && modifiedGun.canAimDownSight() && event.getHand() == InteractionHand.MAIN_HAND) {
                double xOffset = (double)translateX;
                double yOffset = (double)translateY;
                double zOffset = (double)translateZ;
                xOffset -= 0.5 * (double)scaleX;
                yOffset -= 0.5 * (double)scaleY;
                zOffset -= 0.5 * (double)scaleZ;
                Vec3 gunOrigin = PropertyHelper.getModelOrigin(heldItem, PropertyHelper.GUN_DEFAULT_ORIGIN);
                xOffset += gunOrigin.x * 0.0625 * (double)scaleX;
                yOffset += gunOrigin.y * 0.0625 * (double)scaleY;
                zOffset += gunOrigin.z * 0.0625 * (double)scaleZ;
                Scope scope = Gun.getScope(heldItem);
                Vec3 ironSightCamera;
                if (modifiedGun.canAttachType(Type.SCOPE) && scope != null) {
                    ironSightCamera = PropertyHelper.getAttachmentPosition(heldItem, modifiedGun, Type.SCOPE).subtract(gunOrigin);
                    xOffset += ironSightCamera.x * 0.0625 * (double)scaleX;
                    yOffset += ironSightCamera.y * 0.0625 * (double)scaleY;
                    zOffset += ironSightCamera.z * 0.0625 * (double)scaleZ;
                    ItemStack scopeStack = Gun.getScopeStack(heldItem);
                    Vec3 scopeOrigin = PropertyHelper.getModelOrigin(scopeStack, PropertyHelper.ATTACHMENT_DEFAULT_ORIGIN);
                    Vec3 scopeCamera = PropertyHelper.getScopeCamera(scopeStack).subtract(scopeOrigin);
                    Vec3 scopeScale = PropertyHelper.getAttachmentScale(heldItem, modifiedGun, Type.SCOPE);
                    xOffset += scopeCamera.x * 0.0625 * (double)scaleX * scopeScale.x;
                    yOffset += scopeCamera.y * 0.0625 * (double)scaleY * scopeScale.y;
                    zOffset += scopeCamera.z * 0.0625 * (double)scaleZ * scopeScale.z;
                } else {
                    ironSightCamera = PropertyHelper.getIronSightCamera(heldItem, modifiedGun, gunOrigin).subtract(gunOrigin);
                    xOffset += ironSightCamera.x * 0.0625 * (double)scaleX;
                    yOffset += ironSightCamera.y * 0.0625 * (double)scaleY;
                    zOffset += ironSightCamera.z * 0.0625 * (double)scaleZ;
                    if (PropertyHelper.isLegacyIronSight(heldItem)) {
                        zOffset += 0.72;
                    }
                }

                float side = right ? 1.0F : -1.0F;
                double time = AimingHandler.get().getNormalisedAdsProgress();
                double transition = PropertyHelper.getSightAnimations(heldItem, modifiedGun).getSightCurve().apply(time);
                poseStack.translate(-0.56 * (double)side * transition, 0.52 * transition, 0.72 * transition);
                poseStack.translate(-xOffset * (double)side * transition, -yOffset * transition, -zOffset * transition);
            }

            this.applyBobbingTransforms(poseStack, event.getPartialTicks());
            float equipProgress = this.getEquipProgress(event.getPartialTicks());
            poseStack.mulPose(Vector3f.XP.rotationDegrees(equipProgress * -50.0F));
//            this.renderReloadArm(poseStack, event.getMultiBufferSource(), event.getPackedLight(), modifiedGun, heldItem, hand, translateX);
            int offset = right ? 1 : -1;
            poseStack.translate(0.56 * (double)offset, -0.52, -0.72);
            this.applyAimingTransforms(poseStack, heldItem, modifiedGun, translateX, translateY, translateZ, offset);
            this.applySwayTransforms(poseStack, modifiedGun, player, translateX, translateY, translateZ, event.getPartialTicks());
            this.applySprintingTransforms(modifiedGun, hand, poseStack, event.getPartialTicks());
            this.applyRecoilTransforms(poseStack, heldItem, modifiedGun);
            this.applyReloadTransforms(poseStack, event.getPartialTicks());
            this.applyShieldTransforms(poseStack, player, modifiedGun, event.getPartialTicks());
            int blockLight = player.isOnFire() ? 15 : player.level.getBrightness(LightLayer.BLOCK, new BlockPos(player.getEyePosition(event.getPartialTicks())));
            blockLight += this.entityIdForMuzzleFlash.contains(player.getId()) ? 3 : 0;
            blockLight = Math.min(blockLight, 15);
            int packedLight = LightTexture.pack(blockLight, player.level.getBrightness(LightLayer.SKY, new BlockPos(player.getEyePosition(event.getPartialTicks()))));
            poseStack.pushPose();
            modifiedGun.getGeneral().getGripType().getHeldAnimation().renderFirstPersonArms(Minecraft.getInstance().player, hand, heldItem, poseStack, event.getMultiBufferSource(), packedLight, event.getPartialTicks());
            poseStack.popPose();
            ItemTransforms.TransformType transformType = right ? TransformType.FIRST_PERSON_RIGHT_HAND : TransformType.FIRST_PERSON_LEFT_HAND;
            this.renderWeapon(Minecraft.getInstance().player, heldItem, transformType, event.getPoseStack(), event.getMultiBufferSource(), packedLight, event.getPartialTicks());
            poseStack.popPose();
        }
    }

    private void applyBobbingTransforms(PoseStack poseStack, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.bobView) {
            Entity var5 = mc.getCameraEntity();
            if (var5 instanceof Player) {
                Player player = (Player)var5;
                float deltaDistanceWalked = player.walkDist - player.walkDistO;
                float distanceWalked = -(player.walkDist + deltaDistanceWalked * partialTicks);
                float bobbing = Mth.lerp(partialTicks, player.oBob, player.bob);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(-(Math.abs(Mth.cos(distanceWalked * 3.1415927F - 0.2F) * bobbing) * 5.0F)));
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(-(Mth.sin(distanceWalked * 3.1415927F) * bobbing * 3.0F)));
                poseStack.translate((double)(-(Mth.sin(distanceWalked * 3.1415927F) * bobbing * 0.5F)), (double)(-(-Math.abs(Mth.cos(distanceWalked * 3.1415927F) * bobbing))), 0.0);
                bobbing = (float)((double)bobbing * (player.isSprinting() ? 8.0 : 4.0));
                bobbing = (float)((double)bobbing * (Double)Config.CLIENT.display.bobbingIntensity.get());
                double invertZoomProgress = 1.0 - AimingHandler.get().getNormalisedAdsProgress() * (double)this.sprintIntensity;
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.sin(distanceWalked * 3.1415927F) * bobbing * 3.0F * (float)invertZoomProgress));
                poseStack.mulPose(Vector3f.XP.rotationDegrees(Math.abs(Mth.cos(distanceWalked * 3.1415927F - 0.2F) * bobbing) * 5.0F * (float)invertZoomProgress));
            }
        }

    }

    private void applyAimingTransforms(PoseStack poseStack, ItemStack heldItem, Gun modifiedGun, float x, float y, float z, int offset) {
        if (!(Boolean)Config.CLIENT.display.oldAnimations.get()) {
            poseStack.translate((double)(x * (float)offset), (double)y, (double)z);
            poseStack.translate(0.0, -0.25, 0.25);
            float aiming = (float)Math.sin(Math.toRadians(AimingHandler.get().getNormalisedAdsProgress() * 180.0));
            aiming = PropertyHelper.getSightAnimations(heldItem, modifiedGun).getAimTransformCurve().apply(aiming);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(aiming * 10.0F * (float)offset));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(aiming * 5.0F));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(aiming * 5.0F * (float)offset));
            poseStack.translate(0.0, 0.25, -0.25);
            poseStack.translate((double)(-x * (float)offset), (double)(-y), (double)(-z));
        }

    }

    private void applySwayTransforms(PoseStack poseStack, Gun modifiedGun, LocalPlayer player, float x, float y, float z, float partialTicks) {
        if ((Boolean)Config.CLIENT.display.weaponSway.get() && player != null) {
            poseStack.translate((double)x, (double)y, (double)z);
            double zOffset = modifiedGun.getGeneral().getGripType().getHeldAnimation().getFallSwayZOffset();
            poseStack.translate(0.0, -0.25, zOffset);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(partialTicks, this.prevFallSway, this.fallSway)));
            poseStack.translate(0.0, 0.25, -zOffset);
            float bobPitch = Mth.rotLerp(partialTicks, player.xBobO, player.xBob);
            float headPitch = Mth.rotLerp(partialTicks, player.xRotO, player.getXRot());
            float swayPitch = headPitch - bobPitch;
            swayPitch = (float)((double)swayPitch * (1.0 - 0.5 * AimingHandler.get().getNormalisedAdsProgress()));
            poseStack.mulPose(((SwayType)Config.CLIENT.display.swayType.get()).getPitchRotation().rotationDegrees(swayPitch * ((Double)Config.CLIENT.display.swaySensitivity.get()).floatValue()));
            float bobYaw = Mth.rotLerp(partialTicks, player.yBobO, player.yBob);
            float headYaw = Mth.rotLerp(partialTicks, player.yHeadRotO, player.yHeadRot);
            float swayYaw = headYaw - bobYaw;
            swayYaw = (float)((double)swayYaw * (1.0 - 0.5 * AimingHandler.get().getNormalisedAdsProgress()));
            poseStack.mulPose(((SwayType)Config.CLIENT.display.swayType.get()).getYawRotation().rotationDegrees(swayYaw * ((Double)Config.CLIENT.display.swaySensitivity.get()).floatValue()));
            poseStack.translate((double)(-x), (double)(-y), (double)(-z));
        }

    }

    private void applySprintingTransforms(Gun modifiedGun, HumanoidArm hand, PoseStack poseStack, float partialTicks) {
        if ((Boolean)Config.CLIENT.display.sprintAnimation.get() && modifiedGun.getGeneral().getGripType().getHeldAnimation().canApplySprintingAnimation()) {
            float leftHanded = hand == HumanoidArm.LEFT ? -1.0F : 1.0F;
            float transition = ((float)this.prevSprintTransition + (float)(this.sprintTransition - this.prevSprintTransition) * partialTicks) / 5.0F;
            transition = (float)Math.sin((double)transition * Math.PI / 2.0);
            poseStack.translate(0, 0.2 * (double)transition, 0);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(25.0F * transition));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(5.0F * transition));
        }

    }

    private void applyReloadTransforms(PoseStack poseStack, float partialTicks) {
        float reloadProgress = ReloadHandler.get().getReloadProgress(partialTicks);
//        poseStack.translate(0.0, 0.35 * (double)reloadProgress, 0.0);
//        poseStack.translate(0.0, 0.0, -0.1 * (double)reloadProgress);
//        poseStack.mulPose(Vector3f.XP.rotationDegrees(45.0F * reloadProgress));
        poseStack.translate(-0.3 * (double)reloadProgress, -0.3 * (double)reloadProgress, 0.3 * (double)reloadProgress);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-25.0F * reloadProgress));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(-25.0F * reloadProgress));
    }

    private void applyRecoilTransforms(PoseStack poseStack, ItemStack item, Gun gun) {
        double recoilNormal = RecoilHandler.get().getGunRecoilNormal();
        if (Gun.hasAttachmentEquipped(item, gun, Type.SCOPE)) {
            recoilNormal -= recoilNormal * 0.5 * AimingHandler.get().getNormalisedAdsProgress();
        }

        float kickReduction = 1.0F - GunModifierHelper.getKickReduction(item);
        float recoilReduction = 1.0F - GunModifierHelper.getRecoilModifier(item);
        double kick = (double)gun.getGeneral().getRecoilKick() * 0.0625 * recoilNormal * RecoilHandler.get().getAdsRecoilReduction(gun);
        float recoilLift = (float)((double)gun.getGeneral().getRecoilAngle() * recoilNormal) * (float)RecoilHandler.get().getAdsRecoilReduction(gun);
        float recoilSwayAmount = (float)(2.0 + 1.0 * (1.0 - AimingHandler.get().getNormalisedAdsProgress()));
        float recoilSway = (float)((double)(RecoilHandler.get().getGunRecoilRandom() * recoilSwayAmount - recoilSwayAmount / 2.0F) * recoilNormal);
        poseStack.translate(0.0, 0.0, kick * (double)kickReduction);
        poseStack.translate(0.0, 0.0, 0.15);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(recoilSway * recoilReduction));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(recoilSway * recoilReduction));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(recoilLift * recoilReduction));
        poseStack.translate(0.0, 0.0, -0.15);
    }

    private void applyShieldTransforms(PoseStack poseStack, LocalPlayer player, Gun modifiedGun, float partialTick) {
        if (player.isUsingItem() && player.getOffhandItem().getItem() == Items.SHIELD && modifiedGun.getGeneral().getGripType() == GripType.ONE_HANDED) {
            double time = Mth.clamp((double)((float)player.getTicksUsingItem() + partialTick), 0.0, 4.0) / 4.0;
            poseStack.translate(0.0, 0.35 * time, 0.0);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(45.0F * (float)time));
        }

    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        if (!event.phase.equals(Phase.START)) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.isWindowActive()) {
                Player player = mc.player;
                if (player != null) {
                    if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
                        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
                        if (!heldItem.isEmpty()) {
                            float coolDown;
                            float scale;
                            Window window;
                            int i;
                            int j;
                            PoseStack stack;
                            int progress;
                            if (player.isUsingItem() && player.getUsedItemHand() == InteractionHand.MAIN_HAND && heldItem.getItem() instanceof GrenadeItem) {
                                if (((GrenadeItem)heldItem.getItem()).canCook()) {
                                    int duration = player.getTicksUsingItem();
                                    if (duration >= 10) {
                                        coolDown = 1.0F - (float)(duration - 10) / (float)(player.getUseItem().getUseDuration() - 10);
                                        if (coolDown > 0.0F) {
                                            scale = 3.0F;
                                            window = mc.getWindow();
                                            i = (int)((float)(window.getGuiScaledHeight() / 2 - 7 - 60) / scale);
                                            j = (int)Math.ceil((double)(((float)(window.getGuiScaledWidth() / 2) - 8.0F * scale) / scale));
                                            RenderSystem.enableBlend();
                                            RenderSystem.defaultBlendFunc();
                                            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                                            RenderSystem.setShader(GameRenderer::getPositionTexShader);
                                            RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
                                            stack = new PoseStack();
                                            stack.scale(scale, scale, scale);
                                            progress = (int)Math.ceil((double)(coolDown * 17.0F)) - 1;
                                            Screen.blit(stack, j, i, 36.0F, 94.0F, 16, 4, 256, 256);
                                            Screen.blit(stack, j, i, 52.0F, 94.0F, progress, 4, 256, 256);
                                            RenderSystem.disableBlend();
                                        }
                                    }

                                }
                            } else {
                                if ((Boolean)Config.CLIENT.display.cooldownIndicator.get() && heldItem.getItem() instanceof GunItem) {
                                    Gun gun = ((GunItem)heldItem.getItem()).getGun();
                                    if (!gun.getGeneral().isAuto()) {
                                        coolDown = player.getCooldowns().getCooldownPercent(heldItem.getItem(), event.renderTickTime);
                                        if (coolDown > 0.0F) {
                                            scale = 3.0F;
                                            window = mc.getWindow();
                                            i = (int)((float)(window.getGuiScaledHeight() / 2 - 7 - 60) / scale);
                                            j = (int)Math.ceil((double)(((float)(window.getGuiScaledWidth() / 2) - 8.0F * scale) / scale));
                                            RenderSystem.enableBlend();
                                            RenderSystem.defaultBlendFunc();
                                            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                                            RenderSystem.setShader(GameRenderer::getPositionTexShader);
                                            RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
                                            stack = new PoseStack();
                                            stack.scale(scale, scale, scale);
                                            progress = (int)Math.ceil(((double)coolDown + 0.05) * 17.0) - 1;
                                            Screen.blit(stack, j, i, 36.0F, 94.0F, 16, 4, 256, 256);
                                            Screen.blit(stack, j, i, 52.0F, 94.0F, progress, 4, 256, 256);
                                            RenderSystem.disableBlend();
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    public void applyWeaponScale(ItemStack heldItem, PoseStack stack) {
        if (heldItem.getTag() != null) {
            CompoundTag compound = heldItem.getTag();
            if (compound.contains("Scale", 5)) {
                float scale = compound.getFloat("Scale");
                stack.scale(scale, scale, scale);
            }
        }

    }

    public boolean renderWeapon(@Nullable LivingEntity entity, ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource renderTypeBuffer, int light, float partialTicks) {
        if (stack.getItem() instanceof GunItem) {
            poseStack.pushPose();
            ItemStack model = ItemStack.EMPTY;
            if (stack.getTag() != null && stack.getTag().contains("Model", 10)) {
                model = ItemStack.of(stack.getTag().getCompound("Model"));
            }

            RenderUtil.applyTransformType(stack, poseStack, transformType, entity);
            this.renderingWeapon = stack;
            this.renderGun(entity, transformType, model.isEmpty() ? stack : model, poseStack, renderTypeBuffer, light, partialTicks);
            this.renderAttachments(entity, transformType, stack, poseStack, renderTypeBuffer, light, partialTicks);
            this.renderMuzzleFlash(entity, poseStack, renderTypeBuffer, stack, transformType, partialTicks);
            this.renderingWeapon = null;
            poseStack.popPose();
            return true;
        } else {
            return false;
        }
    }

    private void renderGun(@Nullable LivingEntity entity, ItemTransforms.TransformType transformType, ItemStack stack, PoseStack poseStack, MultiBufferSource renderTypeBuffer, int light, float partialTicks) {
        if (ModelOverrides.hasModel(stack)) {
            IOverrideModel model = ModelOverrides.getModel(stack);
            if (model != null) {
                model.render(partialTicks, transformType, stack, ItemStack.EMPTY, entity, poseStack, renderTypeBuffer, light, OverlayTexture.NO_OVERLAY);
            }
        } else {
            BakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(stack);
            Minecraft.getInstance().getItemRenderer().render(stack, TransformType.NONE, false, poseStack, renderTypeBuffer, light, OverlayTexture.NO_OVERLAY, bakedModel);
        }

    }

    private void renderAttachments(@Nullable LivingEntity entity, ItemTransforms.TransformType transformType, ItemStack stack, PoseStack poseStack, MultiBufferSource renderTypeBuffer, int light, float partialTicks) {
        if (stack.getItem() instanceof GunItem) {
            Gun modifiedGun = ((GunItem)stack.getItem()).getModifiedGun(stack);
            CompoundTag gunTag = stack.getOrCreateTag();
            CompoundTag attachments = gunTag.getCompound("Attachments");
            Iterator var11 = attachments.getAllKeys().iterator();

            while(var11.hasNext()) {
                String tagKey = (String)var11.next();
                IAttachment.Type type = Type.byTagKey(tagKey);
                if (type != null && modifiedGun.canAttachType(type)) {
                    ItemStack attachmentStack = Gun.getAttachment(type, stack);
                    if (!attachmentStack.isEmpty()) {
                        poseStack.pushPose();
                        Vec3 origin = PropertyHelper.getModelOrigin(attachmentStack, PropertyHelper.ATTACHMENT_DEFAULT_ORIGIN);
                        poseStack.translate(-origin.x * 0.0625, -origin.y * 0.0625, -origin.z * 0.0625);
                        Vec3 gunOrigin = PropertyHelper.getModelOrigin(stack, PropertyHelper.GUN_DEFAULT_ORIGIN);
                        poseStack.translate(gunOrigin.x * 0.0625, gunOrigin.y * 0.0625, gunOrigin.z * 0.0625);
                        Vec3 translation = PropertyHelper.getAttachmentPosition(stack, modifiedGun, type).subtract(gunOrigin);
                        poseStack.translate(translation.x * 0.0625, translation.y * 0.0625, translation.z * 0.0625);
                        Vec3 scale = PropertyHelper.getAttachmentScale(stack, modifiedGun, type);
                        Vec3 center = origin.subtract(8.0, 8.0, 8.0).scale(0.0625);
                        poseStack.translate(center.x, center.y, center.z);
                        poseStack.scale((float)scale.x, (float)scale.y, (float)scale.z);
                        poseStack.translate(-center.x, -center.y, -center.z);
                        IOverrideModel model = ModelOverrides.getModel(attachmentStack);
                        if (model != null) {
                            model.render(partialTicks, transformType, attachmentStack, stack, entity, poseStack, renderTypeBuffer, light, OverlayTexture.NO_OVERLAY);
                        } else {
                            BakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(attachmentStack);
                            Minecraft.getInstance().getItemRenderer().render(attachmentStack, TransformType.NONE, false, poseStack, renderTypeBuffer, light, OverlayTexture.NO_OVERLAY, GunModel.wrap(bakedModel));
                        }

                        poseStack.popPose();
                    }
                }
            }
        }

    }

    private void renderMuzzleFlash(@Nullable LivingEntity entity, PoseStack poseStack, MultiBufferSource buffer, ItemStack weapon, ItemTransforms.TransformType transformType, float partialTicks) {
        Gun modifiedGun = ((GunItem)weapon.getItem()).getModifiedGun(weapon);
        if (modifiedGun.getDisplay().getFlash() != null) {
            if (transformType == TransformType.FIRST_PERSON_RIGHT_HAND || transformType == TransformType.THIRD_PERSON_RIGHT_HAND || transformType == TransformType.FIRST_PERSON_LEFT_HAND || transformType == TransformType.THIRD_PERSON_LEFT_HAND) {
                if (entity != null && this.entityIdForMuzzleFlash.contains(entity.getId())) {
                    float randomValue = (Float)this.entityIdToRandomValue.get(entity.getId());
                    this.drawMuzzleFlash(weapon, modifiedGun, randomValue, randomValue >= 0.5F, poseStack, buffer, partialTicks);
                }
            }
        }
    }

    private void drawMuzzleFlash(ItemStack weapon, Gun modifiedGun, float random, boolean flip, PoseStack poseStack, MultiBufferSource buffer, float partialTicks) {
        if (PropertyHelper.hasMuzzleFlash(weapon, modifiedGun)) {
            poseStack.pushPose();
            Vec3 weaponOrigin = PropertyHelper.getModelOrigin(weapon, PropertyHelper.GUN_DEFAULT_ORIGIN);
            Vec3 flashPosition = PropertyHelper.getMuzzleFlashPosition(weapon, modifiedGun).subtract(weaponOrigin);
            poseStack.translate(weaponOrigin.x * 0.0625, weaponOrigin.y * 0.0625, weaponOrigin.z * 0.0625);
            poseStack.translate(flashPosition.x * 0.0625, flashPosition.y * 0.0625, flashPosition.z * 0.0625);
            poseStack.translate(-0.5, -0.5, -0.5);
            ItemStack barrelStack = Gun.getAttachment(Type.BARREL, weapon);
            if (!barrelStack.isEmpty()) {
                Item var12 = barrelStack.getItem();
                if (var12 instanceof IBarrel) {
                    IBarrel barrel = (IBarrel)var12;
                    if (!PropertyHelper.isUsingBarrelMuzzleFlash(barrelStack)) {
                        Vec3 scale = PropertyHelper.getAttachmentScale(weapon, modifiedGun, Type.BARREL);
                        double length = (double)((Barrel)barrel.getProperties()).getLength();
                        poseStack.translate(0.0, 0.0, -length * 0.0625 * scale.z);
                    }
                }
            }

            poseStack.mulPose(Vector3f.ZP.rotationDegrees(360.0F * random));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(flip ? 180.0F : 0.0F));
            Vec3 flashScale = PropertyHelper.getMuzzleFlashScale(weapon, modifiedGun);
            float scaleX = (float)flashScale.x / 2.0F - (float)flashScale.x / 2.0F * (1.0F - partialTicks);
            float scaleY = (float)flashScale.y / 2.0F - (float)flashScale.y / 2.0F * (1.0F - partialTicks);
            poseStack.scale(scaleX, scaleY, 1.0F);
            float scaleModifier = (float)GunModifierHelper.getMuzzleFlashScale(weapon, 1.0);
            poseStack.scale(scaleModifier, scaleModifier, 1.0F);
            poseStack.translate(-0.5, -0.5, 0.0);
            float minU = weapon.isEnchanted() ? 0.5F : 0.0F;
            float maxU = weapon.isEnchanted() ? 1.0F : 0.5F;
            Matrix4f matrix = poseStack.last().pose();
            VertexConsumer builder = buffer.getBuffer(GunRenderType.getMuzzleFlash());
            builder.vertex(matrix, 0.0F, 0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(maxU, 1.0F).uv2(15728880).endVertex();
            builder.vertex(matrix, 1.0F, 0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(minU, 1.0F).uv2(15728880).endVertex();
            builder.vertex(matrix, 1.0F, 1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(minU, 0.0F).uv2(15728880).endVertex();
            builder.vertex(matrix, 0.0F, 1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(maxU, 0.0F).uv2(15728880).endVertex();
            poseStack.popPose();
        }
    }

//    private void renderReloadArm(PoseStack poseStack, MultiBufferSource buffer, int light, Gun modifiedGun, ItemStack stack, HumanoidArm hand, float translateX) {
//        Minecraft mc = Minecraft.getInstance();
//        if (mc.player != null && mc.player.tickCount >= ReloadHandler.get().getStartReloadTick() && ReloadHandler.get().getReloadTimer() == 5) {
//            Item item = (Item)ForgeRegistries.ITEMS.getValue(modifiedGun.getProjectile().getItem());
//            if (item != null) {
//                poseStack.pushPose();
//                int side = hand.getOpposite() == HumanoidArm.RIGHT ? 1 : -1;
//                poseStack.translate((double)(translateX * (float)side) + 0.25, 0.0, 0.0); //changes here + 0.25
//                float interval = (float)GunEnchantmentHelper.getReloadInterval(stack);
//                float reload = ((float)(mc.player.tickCount - ReloadHandler.get().getStartReloadTick()) + mc.getFrameTime()) % interval / interval;
//                float percent = 1.0F - reload;
//                if (percent >= 0.5F) {
//                    percent = 1.0F - percent;
//                }
//
//                percent *= 2.0F;
//                percent = (double)percent < 0.5 ? 2.0F * percent * percent : -1.0F + (4.0F - 2.0F * percent) * percent;
//                poseStack.translate(3.5 * (double)side * 0.0625, -0.5625, -0.5625);
//                poseStack.mulPose(Vector3f.YP.rotationDegrees(120.0F)); //changes here 180 to 120
//                poseStack.translate(0.0, -0.35 * (1.0 - (double)percent), 0.0);
//                poseStack.translate((double)side * 0.0625, 0.0, 0.0);
//                poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
//                poseStack.mulPose(Vector3f.YP.rotationDegrees(35.0F * (float)(-side)));
//                poseStack.mulPose(Vector3f.XP.rotationDegrees(-75.0F * percent));
//                poseStack.scale(0.5F, 0.5F, 0.5F);
//                RenderUtil.renderFirstPersonArm(mc.player, hand.getOpposite(), poseStack, buffer, light);
//                if (reload < 0.5F) {
//                    poseStack.pushPose();
//                    poseStack.translate((double)(-side * 5) * 0.0625, 0.9375, -0.0625);
//                    poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
//                    poseStack.scale(0.75F, 0.75F, 0.75F);
//                    ItemStack ammo = new ItemStack(item, modifiedGun.getGeneral().getReloadAmount());
//                    BakedModel model = RenderUtil.getModel(ammo);
//                    boolean isModel = model.isGui3d();
//                    this.random.setSeed((long)Item.getId(item));
//                    int count = Math.min(modifiedGun.getGeneral().getReloadAmount(), 5);
//
//                    for(int i = 0; i < count; ++i) {
//                        poseStack.pushPose();
//                        if (i > 0) {
//                            float x;
//                            float y;
//                            if (isModel) {
//                                x = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
//                                y = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
//                                float z = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
//                                poseStack.translate((double)x, (double)y, (double)z);
//                            } else {
//                                x = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
//                                y = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
//                                poseStack.translate((double)x, (double)y, 0.0);
//                            }
//                        }
//
//                        RenderUtil.renderModel(ammo, TransformType.THIRD_PERSON_LEFT_HAND, poseStack, buffer, light, OverlayTexture.NO_OVERLAY, (LivingEntity)null);
//                        poseStack.popPose();
//                        if (!isModel) {
//                            poseStack.translate(0.0, 0.0, 0.09375);
//                        }
//                    }
//
//                    poseStack.popPose();
//                }
//
//                poseStack.popPose();
//            }
//        }
//    }

    private float getEquipProgress(float partialTicks) {
        if (this.equippedProgressMainHandField == null) {
            this.equippedProgressMainHandField = ObfuscationReflectionHelper.findField(ItemInHandRenderer.class, "f_109302_");
            this.equippedProgressMainHandField.setAccessible(true);
        }

        if (this.prevEquippedProgressMainHandField == null) {
            this.prevEquippedProgressMainHandField = ObfuscationReflectionHelper.findField(ItemInHandRenderer.class, "f_109303_");
            this.prevEquippedProgressMainHandField.setAccessible(true);
        }

        ItemInHandRenderer firstPersonRenderer = Minecraft.getInstance().getItemInHandRenderer();

        try {
            float equippedProgressMainHand = (Float)this.equippedProgressMainHandField.get(firstPersonRenderer);
            float prevEquippedProgressMainHand = (Float)this.prevEquippedProgressMainHandField.get(firstPersonRenderer);
            return 1.0F - Mth.lerp(partialTicks, prevEquippedProgressMainHand, equippedProgressMainHand);
        } catch (IllegalAccessException var5) {
            var5.printStackTrace();
            return 0.0F;
        }
    }

    private void updateImmersiveCamera() {
        this.prevImmersiveRoll = this.immersiveRoll;
        this.prevFallSway = this.fallSway;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            ItemStack heldItem = mc.player.getMainHandItem();
            float targetAngle = heldItem.getItem() instanceof GunItem ? mc.player.input.leftImpulse : 0.0F;
            float speed = mc.player.input.leftImpulse != 0.0F ? 0.1F : 0.15F;
            this.immersiveRoll = Mth.lerp(speed, this.immersiveRoll, targetAngle);
            float deltaY = (float)Mth.clamp(mc.player.yo - mc.player.getY(), -1.0, 1.0);
            deltaY = (float)((double)deltaY * (1.0 - AimingHandler.get().getNormalisedAdsProgress()));
            deltaY = (float)((double)deltaY * (1.0 - (double)(Mth.abs(mc.player.getXRot()) / 90.0F)));
            this.fallSway = Mth.approach(this.fallSway, deltaY * 60.0F * ((Double)Config.CLIENT.display.swaySensitivity.get()).floatValue(), 10.0F);
            float intensity = mc.player.isSprinting() ? 0.75F : 1.0F;
            this.sprintIntensity = Mth.approach(this.sprintIntensity, intensity, 0.1F);
        }
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if ((Boolean)Config.CLIENT.display.cameraRollEffect.get()) {
            float roll = (float)Mth.lerp(event.getPartialTicks(), (double)this.prevImmersiveRoll, (double)this.immersiveRoll);
            roll = (float)Math.sin((double)roll * Math.PI / 2.0);
            roll *= ((Double)Config.CLIENT.display.cameraRollAngle.get()).floatValue();
            event.setRoll(-roll);
        }
    }
}