package com.kuver.makeshiftguns.item.grenades;

import com.kuver.makeshiftguns.entity.ThrowableMolotovEntity;
import com.mrcrayfish.guns.init.ModSounds;
import com.mrcrayfish.guns.item.AmmoItem;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class MolotovItem extends AmmoItem {
    protected int maxCookTime;

    public MolotovItem(Item.Properties properties, int maxCookTime) {
        super(properties);
        this.maxCookTime = maxCookTime;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return this.maxCookTime;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (!this.canCook()) return;

        int duration = this.getUseDuration(stack) - count;
        if (duration == 10)
            player.level.playLocalSound(player.getX(), player.getY(), player.getZ(), ModSounds.ITEM_GRENADE_PIN.get(), SoundSource.PLAYERS, 1.0F, 1.0F, false);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        playerIn.startUsingItem(handIn);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        if (this.canCook() && !worldIn.isClientSide()) {
            if (!(entityLiving instanceof Player) || !((Player) entityLiving).isCreative())
                stack.shrink(1);
            ThrowableMolotovEntity grenade = this.create(worldIn, entityLiving, 0);
            grenade.onDeath();
            if (entityLiving instanceof Player) {
                ((Player) entityLiving).awardStat(Stats.ITEM_USED.get(this));
            }
        }
        return stack;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (!worldIn.isClientSide()) {
            int duration = this.getUseDuration(stack) - timeLeft;
            if (duration >= 10) {
                if (!(entityLiving instanceof Player) || !((Player) entityLiving).isCreative())
                    stack.shrink(1);
                ThrowableMolotovEntity grenade = this.create(worldIn, entityLiving, this.maxCookTime - duration);
                grenade.shootFromRotation(entityLiving, entityLiving.getXRot(), entityLiving.getYRot(), 0.0F, Math.min(1.0F, duration / 20F), 1.0F);
                worldIn.addFreshEntity(grenade);
                this.onThrown(worldIn, grenade);
                if (entityLiving instanceof Player) {
                    ((Player) entityLiving).awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    public ThrowableMolotovEntity create(Level world, LivingEntity entity, int timeLeft) {
        return new ThrowableMolotovEntity(world, entity, 160);
    }

    public boolean canCook() {
        return false;
    }

    protected void onThrown(Level world, ThrowableMolotovEntity entity) {
        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.ITEM_GRENADE_PIN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}