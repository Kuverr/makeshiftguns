package com.kuver.makeshiftguns.item.grenades;

import com.kuver.makeshiftguns.entity.grenades.ThrowablePipeBombEntity;
import com.mrcrayfish.guns.entity.ThrowableGrenadeEntity;
import com.mrcrayfish.guns.item.GrenadeItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class PipeBombItem extends GrenadeItem {

    public PipeBombItem(Item.Properties properties, int maxLife) {
        super(properties, maxLife);
    }


    public ThrowableGrenadeEntity create(Level world, LivingEntity entity, int timeLeft)
    {
        return new ThrowablePipeBombEntity(world, entity, timeLeft);
    }
}
