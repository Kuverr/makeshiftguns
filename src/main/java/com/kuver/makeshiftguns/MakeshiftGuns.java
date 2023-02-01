package com.kuver.makeshiftguns;

import com.kuver.makeshiftguns.client.handler.GunRenderingHandler;
import com.kuver.makeshiftguns.client.render.gun.model.ARPrototypeModel;
import com.kuver.makeshiftguns.client.render.gun.model.MarksmanPistolModel;
import com.kuver.makeshiftguns.client.render.pose.HandgunPose;
import com.kuver.makeshiftguns.entity.client.MolotovRenderer;
import com.kuver.makeshiftguns.entity.client.SmokeGrenadeRenderer;
import com.kuver.makeshiftguns.init.EntityInit;
import com.kuver.makeshiftguns.init.ItemInit;
import com.kuver.makeshiftguns.init.ParticleInit;
import com.mrcrayfish.guns.client.render.gun.ModelOverrides;
import com.mrcrayfish.guns.common.GripType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("makeshiftguns")
public class MakeshiftGuns {
    public static final String MOD_ID = "makeshiftguns";

    public static final CreativeModeTab GROUP = new CreativeModeTab(MOD_ID) {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(ItemInit.PIPE_BOMB.get());
        }
    };

    public MakeshiftGuns() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);

        //Registers all the Deferred Registers from our init classes.
        ItemInit.REGISTER.register(bus);
        EntityInit.REGISTER.register(bus);
        ParticleInit.REGISTER.register(bus);

        bus.addListener(this::onClientSetup);
    }

    //Common setup event
    private void setup(final FMLCommonSetupEvent event) {

        System.out.println("Hello from Makeshift Guns preinit!");

        GripType.registerType(new GripType(new ResourceLocation("makeshiftguns", "handgun"), new HandgunPose()));
    }

    private void onClientSetup(final FMLClientSetupEvent event) {

        ModelOverrides.register(ItemInit.AR_PROTOTYPE.get(), new ARPrototypeModel());
        ModelOverrides.register(ItemInit.MARKSMAN_PISTOL.get(), new MarksmanPistolModel());

        EntityRenderers.register(EntityInit.THROWABLE_SMOKE_GRENADE.get(), SmokeGrenadeRenderer::new);
        EntityRenderers.register(EntityInit.THROWABLE_MOLOTOV.get(), MolotovRenderer::new);

        MinecraftForge.EVENT_BUS.register(GunRenderingHandler.get());
    }
}
