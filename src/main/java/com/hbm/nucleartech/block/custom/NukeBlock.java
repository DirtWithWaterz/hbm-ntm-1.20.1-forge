package com.hbm.nucleartech.block.custom;

import com.hbm.nucleartech.handler.FalloutWeatherData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class NukeBlock extends Block {

    public NukeBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        if(!pLevel.isClientSide()) {

            FalloutWeatherData.get((ServerLevel)pLevel).startFallout((ServerLevel)pLevel, pPos, 100, 3);
//            System.err.println("[Debug] Starting Fallout");
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }
}
