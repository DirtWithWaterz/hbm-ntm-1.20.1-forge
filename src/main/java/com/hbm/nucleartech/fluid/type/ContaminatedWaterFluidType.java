package com.hbm.nucleartech.fluid.type;

import com.hbm.nucleartech.HBM;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public class ContaminatedWaterFluidType extends FluidType {

    public ContaminatedWaterFluidType() {

        super(FluidType.Properties.create()
                .fallDistanceModifier(0F)
                .canExtinguish(true)
                .supportsBoating(true)
                .canHydrate(false)
                .motionScale(0.007D)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH));
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {

        consumer.accept(new IClientFluidTypeExtensions() {

            private static final ResourceLocation
                    STILL_TEXTURE = ResourceLocation.fromNamespaceAndPath(HBM.MOD_ID, "block/contaminated_water_still"),
                    FLOWING_TEXTURE = ResourceLocation.fromNamespaceAndPath(HBM.MOD_ID, "block/contaminated_water_flow");

            @Override
            public ResourceLocation getStillTexture() {

                return STILL_TEXTURE;
            }

            @Override
            public ResourceLocation getFlowingTexture() {

                return FLOWING_TEXTURE;
            }
        });
    }

}
