package com.hbm.nucleartech.block.custom;

import com.hbm.nucleartech.block.entity.AntiPersonnelMineEntity;
import com.hbm.nucleartech.block.entity.GraphiteBlockEntity;
import com.hbm.nucleartech.block.entity.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class AntiPersonnelMineBlock extends BaseEntityBlock {

    public AntiPersonnelMineBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Block.box(6.5, 0, 6.5, 9.5, 1, 9.5);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AntiPersonnelMineEntity(pPos, pState);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {

        ((AntiPersonnelMineEntity)pLevel.getBlockEntity(pPos)).tick(pState, pLevel, pPos);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {

        if(pLevel.isClientSide())
            return null;

        return createTickerHelper(pBlockEntityType, RegisterBlockEntities.ANTI_PERSONNEL_MINE_ENTITY.get(),
                (pLevel1, pPos, pState1, pBlockEntity) ->
                        pBlockEntity.tick(pState1, pLevel1, pPos));
    }
}
