package com.kuver.kuversguns.Registry;

import com.kuver.kuversguns.KuversGuns;
import com.mrcrayfish.guns.GunMod;
import com.mrcrayfish.guns.item.AmmoItem;
import com.mrcrayfish.guns.item.GunItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, KuversGuns.MOD_ID);

    public static final RegistryObject<GunItem> TEST_GUN = REGISTER.register("test_gun", () -> new GunItem(new Item.Properties().stacksTo(1).tab(GunMod.GROUP)));
    public static final RegistryObject<GunItem> BENELLIM4 = REGISTER.register("benellim4", () -> new GunItem(new Item.Properties().stacksTo(1).tab(GunMod.GROUP)));
}
