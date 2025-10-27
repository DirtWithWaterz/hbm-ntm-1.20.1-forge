package com.hbm.nucleartech.block.custom;

import com.hbm.nucleartech.block.RegisterBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BlackLungerBlock extends DropExperienceBlock {

    private final IntProvider xpRange;

    public BlackLungerBlock(Properties pProperties, IntProvider pXpRange) {
        super(pProperties);

        this.xpRange = pXpRange;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {

        // spawn blacklung air pocket

        self().playerWillDestroy(level, pos, state, player);

        for(Direction d : Direction.values()) {

            var rel = pos.relative(d);

            if(level.getBlockState(rel).isAir())
                level.setBlock(rel, RegisterBlocks.COAL_AIR.get().defaultBlockState(), level.isClientSide ? 11 : 3);
        }

        return level.setBlock(pos, RegisterBlocks.COAL_AIR.get().defaultBlockState(), level.isClientSide ? 11 : 3);
    }
}
