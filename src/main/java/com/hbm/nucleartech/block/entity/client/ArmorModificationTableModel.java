package com.hbm.nucleartech.block.entity.client;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.block.entity.ArmorModificationTableEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ArmorModificationTableModel extends GeoModel<ArmorModificationTableEntity> {

    @Override
    public ResourceLocation getModelResource(ArmorModificationTableEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(HBM.MOD_ID, "geo/block/armor_modification_table.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArmorModificationTableEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(HBM.MOD_ID, "textures/block/armor_modification_table.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ArmorModificationTableEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(HBM.MOD_ID, "animations/block/armor_modification_table.animation.json");
    }
}
