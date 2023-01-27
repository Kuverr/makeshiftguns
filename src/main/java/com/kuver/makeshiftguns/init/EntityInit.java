package com.kuver.makeshiftguns.init;

import com.kuver.makeshiftguns.MakeshiftGuns;
import com.kuver.makeshiftguns.entity.ThrowableSmokeGrenadeEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiFunction;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, MakeshiftGuns.MOD_ID);
    public static final RegistryObject<EntityType<ThrowableSmokeGrenadeEntity>> THROWABLE_SMOKE_GRENADE = registerBasic("throwable_smoke_grenade", ThrowableSmokeGrenadeEntity::new);

    private static <T extends Entity> RegistryObject<EntityType<T>> registerBasic(String id, BiFunction<EntityType<T>, Level, T> function) {

        return REGISTER.register(id, () -> EntityType.Builder.of(function::apply, MobCategory.MISC)
                .sized(0.25F, 0.25F)
                .setTrackingRange(100)
                .setUpdateInterval(1)
                .noSummon()
                .fireImmune()
                .setShouldReceiveVelocityUpdates(true).build(id));

    }
}
