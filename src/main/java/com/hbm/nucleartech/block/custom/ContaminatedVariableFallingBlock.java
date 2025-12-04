package com.hbm.nucleartech.block.custom;

import com.hbm.nucleartech.hazard.HazardBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ContaminatedVariableFallingBlock extends HazardBlock implements Fallable {

    // allow 0..7 (8 variants).
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 7);

    public ContaminatedVariableFallingBlock(Properties properties, double rad) {
        super(properties, rad);
        // default variant 0
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
    }

    // optional: have block pick variant on regular placement from item (useful if item stores BlockStateTag)
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // If the ItemStack has a BlockStateTag.variant, prefer it
        var stack = context.getItemInHand();
        if (stack.hasTag() && stack.getTag().contains("BlockStateTag")) {
            var tag = stack.getTag().getCompound("BlockStateTag");
            if (tag.contains("variant")) {
                int v = tag.getInt("variant");
                v = Math.max(0, Math.min(v, 3));
                return this.defaultBlockState().setValue(VARIANT, v);
            }
        }
        // otherwise keep the default
        return this.defaultBlockState();
    }

    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        pLevel.scheduleTick(pPos, this, this.getDelayAfterPlace());
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        pLevel.scheduleTick(pCurrentPos, this, this.getDelayAfterPlace());
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (isFree(pLevel.getBlockState(pPos.below())) && pPos.getY() >= pLevel.getMinBuildHeight()) {
            FallingBlockEntity $$4 = FallingBlockEntity.fall(pLevel, pPos, pState);
            this.falling($$4);
        }
    }

    protected void falling(FallingBlockEntity pEntity) {
    }

    protected int getDelayAfterPlace() {
        return 2;
    }

    public static boolean isFree(BlockState pState) {
        return pState.isAir() || pState.is(BlockTags.FIRE) || pState.liquid() || pState.canBeReplaced();
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pRandom.nextInt(16) == 0) {
            BlockPos $$4 = pPos.below();
            if (isFree(pLevel.getBlockState($$4))) {
                ParticleUtils.spawnParticleBelow(pLevel, pPos, pRandom, new BlockParticleOption(ParticleTypes.FALLING_DUST, pState));
            }
        }

    }

    public int getDustColor(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return -16777216;
    }
}
