package com.hbm.nucleartech.block.entity.client;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.block.entity.ArmorModificationTableEntity;
import com.hbm.nucleartech.block.entity.LittleBoyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LittleBoyModel extends GeoModel<LittleBoyEntity> {

    @Override
    public ResourceLocation getModelResource(LittleBoyEntity animatable) {
        return new ResourceLocation(HBM.MOD_ID, "geo/block/little_boy.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LittleBoyEntity animatable) {
        return new ResourceLocation(HBM.MOD_ID, "textures/block/little_boy.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LittleBoyEntity animatable) {
        return new ResourceLocation(HBM.MOD_ID, "animations/block/little_boy.animation.json");
    }
}
