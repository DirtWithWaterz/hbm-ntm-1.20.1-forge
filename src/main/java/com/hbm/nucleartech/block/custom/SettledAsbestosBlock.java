package com.hbm.nucleartech.block.custom;

import com.hbm.nucleartech.block.RegisterBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Arrays;

public class SettledAsbestosBlock extends CarpetBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public SettledAsbestosBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {

        self().playerWillDestroy(level, pos, state, player);

        for(Direction d : Direction.values()) {

            var rel = pos.relative(d);

            if(level.getBlockState(rel).isAir())
                level.setBlock(rel, RegisterBlocks.ASBESTOS_AIR.get().defaultBlockState(), level.isClientSide ? 11 : 3);
        }

        return level.setBlock(pos, RegisterBlocks.ASBESTOS_AIR.get().defaultBlockState(), level.isClientSide ? 11 : 3);
    }

    @Override
    public void fallOn(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, float pFallDistance) {

        super.fallOn(pLevel, pState, pPos, pEntity, pFallDistance);

        for(Direction d : Direction.values()) {

            var rel = pPos.relative(d);

            if(pLevel.getBlockState(rel).isAir()) {

                pLevel.setBlock(rel, RegisterBlocks.ASBESTOS_AIR.get().defaultBlockState(), pLevel.isClientSide ? 11 : 3);
            }
        }

        pLevel.setBlock(pPos, RegisterBlocks.ASBESTOS_AIR.get().defaultBlockState(), pLevel.isClientSide ? 11 : 3);
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {

        for(Direction d : Direction.values()) {

            var rel = pPos.relative(d);

            if(pLevel.getBlockState(rel).isAir()) {

                pLevel.setBlock(rel, RegisterBlocks.ASBESTOS_AIR.get().defaultBlockState(), pLevel.isClientSide ? 11 : 3);
            }
        }

        pLevel.setBlock(pPos, RegisterBlocks.ASBESTOS_AIR.get().defaultBlockState(), pLevel.isClientSide ? 11 : 3);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        for(Direction d : Direction.values()) {

            var rel = pPos.relative(d);

            if(pLevel.getBlockState(rel).isAir()) {

                pLevel.setBlock(rel, RegisterBlocks.ASBESTOS_AIR.get().defaultBlockState(), pLevel.isClientSide ? 11 : 3);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {

        for(Direction d : Direction.values()) {

            var rel = pPos.relative(d);

            if(pLevel.getBlockState(rel).isAir()) {

                pLevel.setBlock(rel, RegisterBlocks.ASBESTOS_AIR.get().defaultBlockState(), pLevel.isClientSide ? 11 : 3);
            }
        }

        pLevel.setBlock(pPos, RegisterBlocks.ASBESTOS_AIR.get().defaultBlockState(), pLevel.isClientSide ? 11 : 3);
    }
}
