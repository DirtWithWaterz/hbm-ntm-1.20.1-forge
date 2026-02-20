package com.hbm.nucleartech.hazard;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class LowProfileHazardBlock extends HazardBlock {

    public LowProfileHazardBlock(BlockBehaviour.Properties pProperties, double rad) {
        super(pProperties, rad);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {}
}
