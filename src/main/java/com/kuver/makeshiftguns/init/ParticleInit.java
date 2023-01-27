package com.kuver.makeshiftguns.init;

import com.kuver.makeshiftguns.MakeshiftGuns;
import com.kuver.makeshiftguns.particle.custom.SmokeGrenadeParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = MakeshiftGuns.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParticleInit {
    public static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MakeshiftGuns.MOD_ID);

    public static final RegistryObject<SimpleParticleType> SMOKE_GRENADE_PARTICLES = REGISTER.register("smoke_grenade_particles",
            () -> new SimpleParticleType(true));

    @SuppressWarnings("resource")
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onParticlesRegistry(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(ParticleInit.SMOKE_GRENADE_PARTICLES.get(), SmokeGrenadeParticles.Provider::new);
    }
}
