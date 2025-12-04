package com.hbm.nucleartech.block.custom;

import com.hbm.nucleartech.block.RegisterBlocks;
import com.hbm.nucleartech.hazard.HazardBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContaminatedVariableLayerBlock extends HazardBlock {

    // allow 0..7 (8 variants).
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 7);

    public static final int MAX_HEIGHT = 8;
    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 8);
    protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[]{Shapes.empty(), Block.box(0.0F, 0.0F, 0.0F, 16.0F, 2.0F, 16.0F), Block.box(0.0F, 0.0F, 0.0F, 16.0F, 4.0F, 16.0F), Block.box(0.0F, 0.0F, 0.0F, 16.0F, 6.0F, 16.0F), Block.box(0.0F, 0.0F, 0.0F, 16.0F, 8.0F, 16.0F), Block.box(0.0F, 0.0F, 0.0F, 16.0F, 10.0F, 16.0F), Block.box(0.0F, 0.0F, 0.0F, 16.0F, 12.0F, 16.0F), Block.box(0.0F, 0.0F, 0.0F, 16.0F, 14.0F, 16.0F), Block.box(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F)};
    public static final int HEIGHT_IMPASSABLE = 5;

    public ContaminatedVariableLayerBlock(Properties properties, double rad) {
        super(properties, rad);
        // default variant 0
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, 0));
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
        builder.add(LAYERS);
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        if (Objects.requireNonNull(pType) == PathComputationType.LAND) {
            return pState.getValue(LAYERS) < HEIGHT_IMPASSABLE;
        }
        return false;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_LAYER[pState.getValue(LAYERS)];
    }

    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_LAYER[pState.getValue(LAYERS) - 1];
    }

    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return SHAPE_BY_LAYER[pState.getValue(LAYERS)];
    }

    public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_LAYER[pState.getValue(LAYERS)];
    }

    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.getValue(LAYERS) == MAX_HEIGHT ? 0.2F : 1.0F;
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockState $$3 = pLevel.getBlockState(pPos.below());
        if ($$3.is(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
            return false;
        } else if ($$3.is(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON)) {
            return true;
        } else {
            return Block.isFaceFull($$3.getCollisionShape(pLevel, pPos.below()), Direction.UP) || $$3.is(this) && $$3.getValue(LAYERS) == MAX_HEIGHT;
        }
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.getBrightness(LightLayer.BLOCK, pPos) > 11) {
            dropResources(pState, pLevel, pPos);
            pLevel.removeBlock(pPos, false);
        }

    }

    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        int $$2 = pState.getValue(LAYERS);
        if (pUseContext.getItemInHand().is(this.asItem()) && $$2 < MAX_HEIGHT) {
            if (pUseContext.replacingClickedOnBlock()) {
                return pUseContext.getClickedFace() == Direction.UP;
            } else {
                return true;
            }
        } else {
            return $$2 == 1;
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState $$1 = pContext.getLevel().getBlockState(pContext.getClickedPos());
        if ($$1.is(this)) {
            int $$2 = $$1.getValue(LAYERS);
            return $$1.setValue(LAYERS, Math.min(MAX_HEIGHT, $$2 + 1));
        } else {
            return super.getStateForPlacement(pContext);
        }
    }
}
