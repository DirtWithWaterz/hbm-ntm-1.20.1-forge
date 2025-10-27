package com.hbm.nucleartech.block.custom;

import com.hbm.nucleartech.util.ContaminationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class CoalAirBlock extends AirBlock {

    private static final Direction[] NO_UP = Arrays.stream(Direction.values())
            .filter(d -> d != Direction.UP)
            .toArray(Direction[]::new);

    public CoalAirBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {

        super.entityInside(pState, pLevel, pPos, pEntity);

        if(pEntity instanceof LivingEntity livingEntity)
            ContaminationUtil.contaminate(livingEntity, ContaminationUtil.HazardType.COAL, ContaminationUtil.ContaminationType.CREATIVE, 1);
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {

        super.randomTick(pState, pLevel, pPos, pRandom);

//        System.out.println("[Debug] random tick called");

        BlockPos rel = pPos.relative(NO_UP[pRandom.nextInt(NO_UP.length)]);

        BlockState targetBefore = pLevel.getBlockState(rel);

        if(targetBefore.isAir()) {

            pLevel.setBlock(rel, pState, 3);
            pLevel.setBlock(pPos, targetBefore, 3);
        }
        else if(pRandom.nextInt(4) == 0)
            pLevel.removeBlock(pPos, false);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {

        super.animateTick(pState, pLevel, pPos, pRandom);

        // animate coal dust particles
        if (pLevel.isClientSide()) {
            // spawn a few coal-dust-like particles with small random offsets
            double cx = pPos.getX() + 0.5D;
            double cy = pPos.getY() + 0.5D;
            double cz = pPos.getZ() + 0.5D;

            if (pRandom.nextInt(6) == 0) { // sporadic
                double vx = (pRandom.nextDouble() - 0.5) * 0.02;
                double vy = (pRandom.nextDouble() - 0.5) * 0.02;
                double vz = (pRandom.nextDouble() - 0.5) * 0.02;
                pLevel.addParticle(ParticleTypes.SMOKE, cx, cy, cz, vx, vy, vz);
                // replace ParticleTypes.SMOKE with a custom particle if you have one
            }
        }
    }
}
