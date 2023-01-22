package com.kuver.makeshiftguns.Registry;

import com.kuver.makeshiftguns.MakeshiftGuns;
import com.kuver.makeshiftguns.item.grenades.PipeBombItem;
import com.mrcrayfish.guns.GunMod;
import com.mrcrayfish.guns.item.GrenadeItem;
import com.mrcrayfish.guns.item.GunItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MakeshiftGuns.MOD_ID);

    public static final RegistryObject<GunItem> TEST_GUN = REGISTER.register("test_gun", () -> new GunItem(new Item.Properties().stacksTo(1).tab(GunMod.GROUP)));
    public static final RegistryObject<Item> PIPE_BOMB = REGISTER.register("pipe_bomb", () -> new PipeBombItem(new Item.Properties().tab(GunMod.GROUP), 10 * 4));
}
