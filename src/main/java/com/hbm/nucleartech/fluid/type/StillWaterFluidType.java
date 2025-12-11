package com.hbm.nucleartech.fluid.type;

import com.hbm.nucleartech.HBM;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class StillWaterFluidType extends FluidType {

    public StillWaterFluidType() {

        super(Properties.create()
                .fallDistanceModifier(0F)
                .canExtinguish(true)
                .supportsBoating(true)
                .canHydrate(false)
                .motionScale(0.003D)
                .density(3000)
                .viscosity(3000)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH));
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {

        consumer.accept(new IClientFluidTypeExtensions() {

            private static final ResourceLocation
                    STILL_TEXTURE = ResourceLocation.fromNamespaceAndPath(HBM.MOD_ID, "block/still_water_still"),
                    FLOWING_TEXTURE = ResourceLocation.fromNamespaceAndPath(HBM.MOD_ID, "block/still_water_flow");

            @Override
            public ResourceLocation getStillTexture() {

                return STILL_TEXTURE;
            }

            @Override
            public ResourceLocation getFlowingTexture() {

                return FLOWING_TEXTURE;
            }

            @Nullable
            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {

                return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/underwater.png");
            }

            @Override
            public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level,
                                                    int renderDistance, float darkenWorldAmount,
                                                    Vector3f fluidFogColor) {
                // tweak the fog color (r,g,b floats 0..1) — here bluish example
                return new Vector3f(0.55f, 0.55f, 0.55f);
            }

            @Override
            public int getTintColor(FluidStack stack) {
                // ARGB (alpha << 24) | (red << 16) | (green << 8) | blue
                return 0x55ffffff; // semi-transparent bluish
            }

            // optional: override renderOverlay if you want custom render code
            @Override
            public void renderOverlay(Minecraft mc, PoseStack poseStack) {
                IClientFluidTypeExtensions.super.renderOverlay(mc, poseStack);
            }
        });
    }

}
