package com.kuver.makeshiftguns.entity;

import com.kuver.makeshiftguns.init.EntityInit;
import com.kuver.makeshiftguns.init.ItemInit;
import com.kuver.makeshiftguns.init.ParticleInit;
import com.mrcrayfish.guns.entity.ThrowableItemEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
public class ThrowableSmokeGrenadeEntity extends ThrowableItemEntity {
    public float rotation;
    public float prevRotation;

    public boolean exploded = false;

    public ThrowableSmokeGrenadeEntity(EntityType<? extends ThrowableItemEntity> entityType, Level worldIn) {
        super(entityType, worldIn);
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
            if (this.level.isClientSide) {
                this.level.addParticle(ParticleTypes.SMOKE, true, this.getX(), this.getY() + 0.25, this.getZ(), 0, 0, 0);
            }
        }
        if (speed < 0.1 && !this.exploded) {
            if (this.level.isClientSide) {
                for (int i = 0; i < 32; i++) {
                    this.level.addAlwaysVisibleParticle(
                            ParticleInit.SMOKE_GRENADE_PARTICLES.get(), true,
                            this.getX() + ((Math.random() * (3 - 0.1)) + 0.1),
                            this.getY() + 2 + (Math.random() * 1 - 0.75),
                            this.getZ() + ((Math.random() * (3 - 0.1)) + 0.1),
                            0  + (Math.random() * 0.5 - 0.25),
                            0,
                            0  + (Math.random() * 0.5 - 0.25)
                    );
                }
            }
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.LAVA_EXTINGUISH,
                    net.minecraft.sounds.SoundSource.BLOCKS,
                    1.0F,
                    1.0F);
            this.exploded = true;
        }
    }

    public void onDeath() {
    }
}