package com.kuver.makeshiftguns.client;

import com.kuver.makeshiftguns.MakeshiftGuns;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = MakeshiftGuns.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public enum SpecialModels {

    MARKSMAN_PISTOL_MAIN("marksman_pistol_main"),
    MARKSMAN_PISTOL_BOLT("marksman_pistol_bolt"),
    AR_PROTOTYPE_MAIN("ar_prototype_main"),
    AR_PROTOTYPE_HANDLE("ar_prototype_handle");

    private final ResourceLocation modelLocation;
    private final boolean specialModel;
    @OnlyIn(Dist.CLIENT)
    private BakedModel cachedModel;

    SpecialModels(String modelName) {
        this(new ResourceLocation(MakeshiftGuns.MOD_ID, "special/gun/" + modelName), true);
    }

    SpecialModels(ResourceLocation resourceLocation, boolean specialModel) {
        this.modelLocation = resourceLocation;
        this.specialModel = specialModel;
    }

    @OnlyIn(Dist.CLIENT)
    public BakedModel getModel() {
        if (this.cachedModel == null) {
            BakedModel model = Minecraft.getInstance().getModelManager().getModel(this.modelLocation);
            if (model == Minecraft.getInstance().getModelManager().getMissingModel()) {
                return model;
            }
            this.cachedModel = model;
        }
        return this.cachedModel;
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void register(ModelRegistryEvent event) {
        for (SpecialModels model : values()) {
            if (model.specialModel) {
                ForgeModelBakery.addSpecialModel(model.modelLocation);
            }
        }
    }

}
