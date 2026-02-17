package com.hbm.nucleartech.hazard;

import com.hbm.nucleartech.util.ContaminationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

public class LowProfileHazardBlock extends HazardBlock {

    public LowProfileHazardBlock(BlockBehaviour.Properties pProperties, double rad) {
        super(pProperties, rad);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {

        if(this.rad3d > 0){
            ContaminationUtil.radiate(pLevel, pPos.getX()+0.5, pPos.getY()+0.5, pPos.getZ()+0.5, 32, (float)this.rad3d, 0, this.module.fire * 5000, 0, 0, false, pPos);
            pLevel.scheduleTick(pPos, this.toBlock(), this.tickRate(), TickPriority.EXTREMELY_LOW);
        }
    }
}
