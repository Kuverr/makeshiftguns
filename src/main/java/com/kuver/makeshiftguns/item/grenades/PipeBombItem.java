package com.kuver.makeshiftguns.item.grenades;

import com.kuver.makeshiftguns.entity.ThrowablePipeBombEntity;
import com.mrcrayfish.guns.entity.ThrowableGrenadeEntity;
import com.mrcrayfish.guns.init.ModSounds;
import com.mrcrayfish.guns.item.GrenadeItem;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class PipeBombItem extends GrenadeItem {
    public PipeBombItem(Item.Properties properties, int maxCookTime) {
        super(properties, maxCookTime);
    }

    public ThrowableGrenadeEntity create(Level world, LivingEntity entity, int timeLeft) {
        return new ThrowablePipeBombEntity(world, entity, 40);
    }

    public boolean canCook() {
        return false;
    }

    protected void onThrown(Level world, ThrowableGrenadeEntity entity) {
        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.ITEM_GRENADE_PIN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
