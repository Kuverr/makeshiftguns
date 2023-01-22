package com.kuver.makeshiftguns.Registry;

import com.kuver.makeshiftguns.MakeshiftGuns;
import com.mrcrayfish.guns.GunMod;
import com.mrcrayfish.guns.item.GunItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MakeshiftGuns.MOD_ID);

    public static final RegistryObject<GunItem> TEST_GUN = REGISTER.register("test_gun", () -> new GunItem(new Item.Properties().stacksTo(1).tab(GunMod.GROUP)));
    public static final RegistryObject<GunItem> M1014 = REGISTER.register("m1014", () -> new GunItem(new Item.Properties().stacksTo(1).tab(GunMod.GROUP)));
    public static final RegistryObject<GunItem> MK18 = REGISTER.register("mk18", () -> new GunItem(new Item.Properties().stacksTo(1).tab(GunMod.GROUP)));
}
