package com.kuver.makeshiftguns.init;

import com.kuver.makeshiftguns.MakeshiftGuns;
import com.kuver.makeshiftguns.item.grenades.PipeBombItem;
import com.mrcrayfish.guns.GunMod;
import com.mrcrayfish.guns.item.GunItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MakeshiftGuns.MOD_ID);

    public static final RegistryObject<GunItem> TEST_GUN = ITEMS.register("testgun", () -> new GunItem(new Item.Properties().stacksTo(1).tab(MakeshiftGuns.GROUP)));

    public static final RegistryObject<GunItem> AR_PROTOTYPE = ITEMS.register("ar_prototype", () -> new GunItem(new Item.Properties().stacksTo(1).tab(MakeshiftGuns.GROUP)));
    public static final RegistryObject<Item> PIPE_BOMB = ITEMS.register("pipe_bomb", () -> new PipeBombItem(new Item.Properties().tab(MakeshiftGuns.GROUP), 25 * 4));
}
