package net.maxi.tutorialmod.entity.client;

import net.maxi.tutorialmod.TutorialMod;
import net.maxi.tutorialmod.entity.custom.ChomperEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ChomperModel extends AnimatedGeoModel<ChomperEntity> {
    @Override
    public ResourceLocation getModelResource(ChomperEntity object) {
        return new ResourceLocation(TutorialMod.MOD_ID, "geo/chomper.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ChomperEntity object) {
        return new ResourceLocation(TutorialMod.MOD_ID, "textures/entity/chomper_texture.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ChomperEntity animatable) {
        return new ResourceLocation(TutorialMod.MOD_ID, "animations/chomper.animation.json");
    }
}
