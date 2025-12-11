package com.hbm.nucleartech.fluid;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.fluid.custom.ContaminatedWaterFluid;
import com.hbm.nucleartech.fluid.custom.StillWaterFluid;
import com.hbm.nucleartech.fluid.type.ContaminatedWaterFluidType;
import com.hbm.nucleartech.fluid.type.StillWaterFluidType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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

    public static final RegistryObject<FluidType> STILL_WATER_TYPE =
            FLUID_TYPES.register("still_water", StillWaterFluidType::new);

    public static final RegistryObject<FlowingFluid> STILL_WATER =
            FLUIDS.register("still_water", StillWaterFluid.Source::new);

    public static final RegistryObject<FlowingFluid> FLOWING_STILL_WATER =
            FLUIDS.register("flowing_still_water", StillWaterFluid.Flowing::new);

    public static void register(IEventBus eventBus) {

        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }
}
