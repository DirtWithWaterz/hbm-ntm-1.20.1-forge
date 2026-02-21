package com.hbm.nucleartech.block.custom;

import com.hbm.nucleartech.explosion.VeryFastRaycastedExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class NukeBlock extends BaseEntityBlock {

    public NukeBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        if(!pLevel.isClientSide()) {

//            FalloutWeatherData.get((ServerLevel)pLevel).startFallout((ServerLevel)pLevel, pPos, 100, 3);

//            System.err.println("[Debug] Starting Fallout");
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos,
                                Block pBlock, BlockPos fromPos, boolean isMoving) {

        if (pLevel.isClientSide) return;

        if (pLevel.hasNeighborSignal(pPos)){
            new VeryFastRaycastedExplosion(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), 48, 95, 95, Math.round(95f*1.5f), (byte)0, 1, 2, 1, null, 95, false);
            for(Direction d : Direction.values())
                pLevel.removeBlock(pPos.relative(d, 1), false);
            pLevel.removeBlock(pPos, false);
            pLevel.removeBlockEntity(pPos);
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return null;
    }
}
