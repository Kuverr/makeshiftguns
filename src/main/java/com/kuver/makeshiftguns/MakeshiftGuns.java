package com.kuver.makeshiftguns;

import com.kuver.makeshiftguns.client.render.pose.LongWeaponPose;
import com.kuver.makeshiftguns.client.render.pose.TestWeaponPose;
import com.mojang.logging.LogUtils;
import com.mrcrayfish.guns.common.GripType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import com.kuver.makeshiftguns.Registry.ItemRegistry;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MakeshiftGuns.MOD_ID)
public class MakeshiftGuns
{
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String MOD_ID = "makeshiftguns";

    public MakeshiftGuns()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);


        ItemRegistry.REGISTER.register(bus);
        GripType.registerType(new GripType(new ResourceLocation("kuversguns", "long_weapon"), new LongWeaponPose()));
        GripType.registerType(new GripType(new ResourceLocation("kuversguns", "test_weapon"), new TestWeaponPose()));
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}
