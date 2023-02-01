package com.kuver.makeshiftguns.init;

import com.kuver.makeshiftguns.MakeshiftGuns;
import com.kuver.makeshiftguns.item.grenades.MolotovItem;
import com.kuver.makeshiftguns.item.grenades.PipeBombItem;
import com.kuver.makeshiftguns.item.grenades.SmokeGrenadeItem;
import com.mrcrayfish.guns.item.AmmoItem;
import com.kuver.makeshiftguns.item.GunItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MakeshiftGuns.MOD_ID);

    public static final RegistryObject<Item> DUCT_TAPE = REGISTER.register("duct_tape",
            () -> new Item(new Item.Properties().stacksTo(16).tab(MakeshiftGuns.GROUP)));

    public static final RegistryObject<AmmoItem> TEST_BULLET = REGISTER.register("test_bullet",
            () -> new AmmoItem(new Item.Properties().stacksTo(64).tab(MakeshiftGuns.GROUP)));

    public static final RegistryObject<GunItem> MAKESHIFT_RIFLE = REGISTER.register("makeshift_rifle",
            () -> new GunItem(new Item.Properties().stacksTo(1).tab(MakeshiftGuns.GROUP)));

    public static final RegistryObject<GunItem> AR_PROTOTYPE = REGISTER.register("ar_prototype",
            () -> new GunItem(new Item.Properties().stacksTo(1).tab(MakeshiftGuns.GROUP)));

    public static final RegistryObject<GunItem> MARKSMAN_PISTOL = REGISTER.register("marksman_pistol",
            () -> new GunItem(new Item.Properties().stacksTo(1).tab(MakeshiftGuns.GROUP)));

    public static final RegistryObject<Item> PIPE_BOMB = REGISTER.register("pipe_bomb",
            () -> new PipeBombItem(new Item.Properties().tab(MakeshiftGuns.GROUP), 72000));

    public static final RegistryObject<Item> MOLOTOV = REGISTER.register("molotov",
            () -> new MolotovItem(new Item.Properties().tab(MakeshiftGuns.GROUP), 72000));

    public static final RegistryObject<Item> SMOKE_GRENADE = REGISTER.register("smoke_grenade",
            () -> new SmokeGrenadeItem(new Item.Properties().tab(MakeshiftGuns.GROUP), 72000));

}
