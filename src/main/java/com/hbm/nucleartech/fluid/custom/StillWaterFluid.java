
package com.hbm.nucleartech.fluid.custom;

import com.hbm.nucleartech.block.RegisterBlocks;
import com.hbm.nucleartech.fluid.RegisterFluids;
import com.hbm.nucleartech.item.RegisterItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;


public abstract class StillWaterFluid extends ForgeFlowingFluid {
    public static final Properties PROPERTIES = new Properties(() -> RegisterFluids.STILL_WATER_TYPE.get(), () -> RegisterFluids.STILL_WATER.get(), () -> RegisterFluids.FLOWING_STILL_WATER.get())
            .explosionResistance(100f).bucket(RegisterItems.STILL_WATER_BUCKET).block(() -> (LiquidBlock) RegisterBlocks.STILL_WATER.get());

    private StillWaterFluid() {
        super(PROPERTIES);
    }

    public void animateTick(Level pLevel, BlockPos pPos, FluidState pState, RandomSource pRandom) {
        if (!pState.isSource() && !(Boolean)pState.getValue(FALLING)) {
            if (pRandom.nextInt(64) == 0) {
                pLevel.playLocalSound((double)pPos.getX() + (double)0.5F, (double)pPos.getY() + (double)0.5F, (double)pPos.getZ() + (double)0.5F, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, pRandom.nextFloat() * 0.25F + 0.75F, pRandom.nextFloat() + 0.5F, false);
            }
        } else if (pRandom.nextInt(10) == 0) {
            pLevel.addParticle(ParticleTypes.UNDERWATER, (double)pPos.getX() + pRandom.nextDouble(), (double)pPos.getY() + pRandom.nextDouble(), (double)pPos.getZ() + pRandom.nextDouble(), (double)0.0F, (double)0.0F, (double)0.0F);
        }
    }

    public static class Source extends StillWaterFluid {
        public int getAmount(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends StillWaterFluid {
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }
}
