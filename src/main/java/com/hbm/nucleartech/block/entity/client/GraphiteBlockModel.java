package com.hbm.nucleartech.block.entity.client;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.block.entity.GraphiteBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GraphiteBlockModel extends GeoModel<GraphiteBlockEntity> {

    @Override
    public ResourceLocation getModelResource(GraphiteBlockEntity animatable) {
        return new ResourceLocation(HBM.MOD_ID, "geo/block/graphite_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GraphiteBlockEntity animatable) {
        return new ResourceLocation(HBM.MOD_ID, "textures/block/graphite_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GraphiteBlockEntity animatable) {
        return new ResourceLocation(HBM.MOD_ID, "animations/block/graphite_block.animation.json");
    }
}
