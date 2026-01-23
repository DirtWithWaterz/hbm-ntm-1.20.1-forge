package com.hbm.nucleartech.item.client;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.item.custom.GraphiteBlockItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class GraphiteBlockItemModel extends GeoModel<GraphiteBlockItem> {

    @Override
    public ResourceLocation getModelResource(GraphiteBlockItem animatable) {
        return new ResourceLocation(HBM.MOD_ID, "geo/item/graphite_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GraphiteBlockItem animatable) {
        return new ResourceLocation(HBM.MOD_ID, "textures/block/graphite_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GraphiteBlockItem animatable) {
        return new ResourceLocation(HBM.MOD_ID, "animations/item/graphite_block.animation.json");
    }

    @Override
    public void setCustomAnimations(GraphiteBlockItem animatable, long instanceId, AnimationState<GraphiteBlockItem> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        float degToRad = (float)(Math.PI / 180.0);

        CoreGeoBone root = getAnimationProcessor().getBone("root");

        if (root != null) {

            var context = animationState.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);

            if (context == ItemDisplayContext.GUI) {
                // Inventory view

                root.setScaleX(0.625f);
                root.setScaleY(0.625f);
                root.setScaleZ(0.625f);

                root.setPosY(-4.44f);
                root.setPosZ(0);

                root.setRotX(39.2315f * degToRad);
                root.setRotY(-37.7612f * degToRad);
                root.setRotZ(-26.5651f * degToRad);
            }
            else if (context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {

                root.setScaleX(0.4f);
                root.setScaleY(0.4f);
                root.setScaleZ(0.4f);

                root.setPosY(-3.33f);
                root.setPosZ(0);

                root.setRotX(0 * degToRad);
                root.setRotY(45 * degToRad);
                root.setRotZ(0 * degToRad);
            }
            else if(context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {

                root.setScaleX(0.375f);
                root.setScaleY(0.375f);
                root.setScaleZ(0.375f);

                root.setPosY(1.75f);
                root.setPosZ(-3f);

                root.setRotX(79.2714f * degToRad);
                root.setRotY(10.5453f * degToRad);
                root.setRotZ(44.007f * degToRad);
            }
            else if(context == ItemDisplayContext.GROUND) {

                root.setScaleX(0.25f);
                root.setScaleY(0.25f);
                root.setScaleZ(0.25f);

                root.setPosY(-3f);
                root.setPosZ(0);

                root.setRotX(0 * degToRad);
                root.setRotY(0 * degToRad);
                root.setRotZ(0 * degToRad);
            }
        }
    }
}
