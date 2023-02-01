package com.kuver.makeshiftguns.entity;

import com.kuver.makeshiftguns.init.EntityInit;
import com.kuver.makeshiftguns.init.ItemInit;
import com.mrcrayfish.guns.entity.ThrowableItemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class ThrowableMolotovEntity extends ThrowableItemEntity {
    public float rotation;
    public float prevRotation;

    public ThrowableMolotovEntity(EntityType<? extends ThrowableItemEntity> entityType, Level worldIn) {
        super(entityType, worldIn);
    }

    public ThrowableMolotovEntity(Level world, LivingEntity entity, int timeLeft) {
        super(EntityInit.THROWABLE_MOLOTOV.get(), world, entity);
        this.setShouldBounce(false);
        this.setGravityVelocity(0.05F);
        this.setItem(new ItemStack(ItemInit.MOLOTOV.get()));
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
            if (this.level.isClientSide) {
                this.level.addParticle(ParticleTypes.FLAME, true, this.getX(), this.getY() + 0.25, this.getZ(), 0, 0, 0);
            }
        }

        BlockPos blockBelow = new BlockPos(this.getX(), this.getY() - 0.2, this.getZ());
        if (this.level.getBlockState(blockBelow).getBlock() != Blocks.AIR) {
            this.level.addParticle(
                    ParticleTypes.FLAME, true,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    0 + (Math.random() * .5 - .25),
                    0 + (Math.random() * .25),
                    0 + (Math.random() * .5 - .25)
            );
        }
    }

    public void onDeath() {
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.GLASS_BREAK,
                net.minecraft.sounds.SoundSource.BLOCKS,
                1.0F,
                1.0F);
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.BLAZE_SHOOT,
                net.minecraft.sounds.SoundSource.BLOCKS,
                1.0F,
                1.0F);

        for (int h = -1; h < 2; h++) {
            float flameHeight = (float) (this.getY() + h);
            for (int i = 0; i < 8; i++) {
                float flameDistanceX = (float) (Math.random() * 5 - 2.5);
                float flameDistanceY = (float) (Math.random() * 5 - 2.5);
                BlockPos posBelow = new BlockPos(this.getX() + flameDistanceX, flameHeight - 1, this.getZ() + flameDistanceY);
                BlockPos flamePos = new BlockPos(this.getX() + flameDistanceX, flameHeight, this.getZ() + flameDistanceY);
                if (this.level.getBlockState(flamePos).getBlock() == Blocks.AIR &&
                        this.level.getBlockState(posBelow).getBlock() != Blocks.AIR &&
                        this.level.getBlockState(posBelow).getBlock() != Blocks.FIRE) {
                    this.level.setBlock(flamePos, Blocks.FIRE.defaultBlockState(), 3);
                }
            }
        }
    }
}