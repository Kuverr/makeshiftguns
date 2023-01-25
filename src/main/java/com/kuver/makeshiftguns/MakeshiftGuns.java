package com.kuver.makeshiftguns;

import com.kuver.makeshiftguns.client.render.gun.model.ARPrototypeModel;
import com.kuver.makeshiftguns.init.ItemInit;
import com.mrcrayfish.guns.client.render.gun.ModelOverrides;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("makeshiftguns")
public class MakeshiftGuns {
    public static final String MOD_ID = "makeshiftguns";

    public MakeshiftGuns()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);

        //Registers all the Deferred Registers from our init classes.
        ItemInit.ITEMS.register(bus);

        bus.addListener(this::OnClientSetup);
    }

    //Common setup event
    private void setup(final FMLCommonSetupEvent event) {

        System.out.println("Hello from Makeshift Guns preinit!");
    }

    private void OnClientSetup(final FMLClientSetupEvent event) {

        ModelOverrides.register(ItemInit.AR_PROTOTYPE.get(), new ARPrototypeModel());
    }
}
