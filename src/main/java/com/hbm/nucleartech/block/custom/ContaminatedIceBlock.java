package com.hbm.nucleartech.block.custom;

import com.hbm.nucleartech.hazard.HazardBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ContaminatedIceBlock extends HazardBlock {

    public ContaminatedIceBlock(BlockBehaviour.Properties pProperties, double rad) {
        super(pProperties, rad);
    }

    public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pSide) {
        return pAdjacentBlockState.is(this) ? true : super.skipRendering(pState, pAdjacentBlockState, pSide);
    }

    /// @Deprecated change to melt into contaminated water instead of regular.
    @Deprecated(since="2.0.0-alpha", forRemoval = true)
    public static BlockState meltsInto() {
        return Blocks.WATER.defaultBlockState();
    }

    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pTe, ItemStack pStack) {
        super.playerDestroy(pLevel, pPlayer, pPos, pState, pTe, pStack);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, pStack) == 0) {
            if (pLevel.dimensionType().ultraWarm()) {
                pLevel.removeBlock(pPos, false);
                return;
            }

            BlockState $$6 = pLevel.getBlockState(pPos.below());
            if ($$6.blocksMotion() || $$6.liquid()) {
                pLevel.setBlockAndUpdate(pPos, meltsInto());
            }
        }

    }

    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.getBrightness(LightLayer.BLOCK, pPos) > 11 - pState.getLightBlock(pLevel, pPos)) {
            this.melt(pState, pLevel, pPos);
        }

    }

    protected void melt(BlockState pState, Level pLevel, BlockPos pPos) {
        if (pLevel.dimensionType().ultraWarm()) {
            pLevel.removeBlock(pPos, false);
        } else {
            pLevel.setBlockAndUpdate(pPos, meltsInto());
            pLevel.neighborChanged(pPos, meltsInto().getBlock(), pPos);
        }
    }
}
