package com.hbm.nucleartech.util;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.block.RegisterBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HBM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class VanillaIntercepts {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {

        if (event.getLevel().isClientSide()) return; // server only

        BlockState state = event.getLevel().getBlockState(event.getPos());
        if (state.is(Blocks.COAL_ORE) || state.is(Blocks.DEEPSLATE_COAL_ORE)) {
            ServerLevel server = (ServerLevel) event.getLevel();
            BlockPos pos = event.getPos();

            for(Direction d : Direction.values()) {

                var rel = pos.relative(d);

                if(server.getBlockState(rel).isAir())
                    server.setBlock(rel, RegisterBlocks.COAL_AIR.get().defaultBlockState(), 3);
            }

            server.setBlock(pos, RegisterBlocks.COAL_AIR.get().defaultBlockState(), 3);
        }
    }
}
