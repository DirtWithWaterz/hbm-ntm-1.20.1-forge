package com.hbm.nucleartech.fluid;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.block.custom.*;
import com.hbm.nucleartech.fluid.custom.ContaminatedWaterFluid;
import com.hbm.nucleartech.fluid.type.ContaminatedWaterFluidType;
import com.hbm.nucleartech.hazard.HazardBlock;
import com.hbm.nucleartech.hazard.HazardBlockItem;
import com.hbm.nucleartech.item.RegisterItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RegisterFluids {

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, HBM.MOD_ID);

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, HBM.MOD_ID);

    public static final RegistryObject<FluidType> CONTAMINATED_WATER_TYPE =
            FLUID_TYPES.register("contaminated_water", ContaminatedWaterFluidType::new);

    public static final RegistryObject<FlowingFluid> CONTAMINATED_WATER =
            FLUIDS.register("contaminated_water", ContaminatedWaterFluid.Source::new);

    public static final RegistryObject<FlowingFluid> FLOWING_CONTAMINATED_WATER =
            FLUIDS.register("flowing_contaminated_water", ContaminatedWaterFluid.Flowing::new);

    public static void register(IEventBus eventBus) {

        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }
}
