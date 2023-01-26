package com.kuver.makeshiftguns.entity;

import com.kuver.makeshiftguns.init.EntityInit;
import com.mrcrayfish.guns.Config;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.entity.GrenadeEntity;
import com.mrcrayfish.guns.entity.ThrowableGrenadeEntity;
import com.mrcrayfish.guns.entity.ThrowableItemEntity;
import com.mrcrayfish.guns.init.ModEntities;
import com.mrcrayfish.guns.item.GunItem;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.kuver.makeshiftguns.init.ItemInit;
import net.minecraft.world.level.block.Blocks;

import java.util.UUID;

public class ThrowableSmokeGrenadeEntity extends ThrowableItemEntity {
    public float rotation;
    public float prevRotation;

    public ThrowableSmokeGrenadeEntity(EntityType<? extends ThrowableItemEntity> entityType, Level worldIn) {
        super(entityType, worldIn);
    }

    public ThrowableSmokeGrenadeEntity(EntityType<? extends ThrowableItemEntity> entityType, Level world, LivingEntity entity) {
        super(entityType, world, entity);
        this.setShouldBounce(true);
        this.setGravityVelocity(0.05F);
        this.setItem(new ItemStack(ItemInit.SMOKE_GRENADE.get()));
        this.setMaxLife(20 * 3);
    }

    public ThrowableSmokeGrenadeEntity(Level world, LivingEntity entity, int timeLeft) {
        super(EntityInit.THROWABLE_SMOKE_GRENADE.get(), world, entity);
        this.setShouldBounce(true);
        this.setGravityVelocity(0.05F);
        this.setItem(new ItemStack(ItemInit.SMOKE_GRENADE.get()));
        this.setMaxLife(timeLeft);
    }

    protected void defineSynchedData() {
    }

    public void tick() {
        super.tick();
        this.prevRotation = this.rotation;
        double speed = this.getDeltaMovement().length();
        if (speed > 0.1) {
            this.rotation += speed * 50;
        }
        if (this.level.isClientSide) {
            this.level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, true, this.getX(), this.getY() + 0.25, this.getZ(), 0, 0, 0);
        }
    }

    public void onDeath() {
//        chat message for testing
        TextComponent msg = new TextComponent("smoke test message");
        UUID testuuid = Minecraft.getInstance().player.getUUID();
        Minecraft.getInstance().player.sendMessage(msg,testuuid);
        this.level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, true, this.getX(), this.getY() + 0.25, this.getZ(), 0, 0, 0);
    }
}