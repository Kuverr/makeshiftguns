package com.kuver.makeshiftguns.entity;

import com.mrcrayfish.guns.Config;
import com.mrcrayfish.guns.entity.GrenadeEntity;
import com.mrcrayfish.guns.entity.ThrowableGrenadeEntity;
import com.mrcrayfish.guns.init.ModEntities;
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

public class ThrowablePipeBombEntity extends ThrowableGrenadeEntity {
    public float rotation;
    public float prevRotation;

    public ThrowablePipeBombEntity(EntityType<? extends ThrowableGrenadeEntity> entityType, Level worldIn) {
        super(entityType, worldIn);
    }

    public ThrowablePipeBombEntity(EntityType<? extends ThrowableGrenadeEntity> entityType, Level world, LivingEntity entity) {
        super(entityType, world, entity);
        this.setShouldBounce(true);
        this.setGravityVelocity(0.05F);
        this.setItem(new ItemStack(ItemInit.PIPE_BOMB.get()));
        this.setMaxLife(20 * 3);
    }

    public ThrowablePipeBombEntity(Level world, LivingEntity entity, int timeLeft) {
        super(ModEntities.THROWABLE_GRENADE.get(), world, entity);
        this.setShouldBounce(true);
        this.setGravityVelocity(0.05F);
        this.setItem(new ItemStack(ItemInit.PIPE_BOMB.get()));
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
            this.level.addParticle(ParticleTypes.SMOKE, true, this.getX(), this.getY() + 0.25, this.getZ(), 0, 0, 0);
        }
    }

    public void onDeath() {
        GrenadeEntity.createExplosion(this, Config.COMMON.grenades.explosionRadius.get().floatValue(), true);
        //chat message for testing
//        TextComponent msg = new TextComponent("test message");
//        UUID testuuid = Minecraft.getInstance().player.getUUID();
//        Minecraft.getInstance().player.sendMessage(msg,testuuid);
    }
}