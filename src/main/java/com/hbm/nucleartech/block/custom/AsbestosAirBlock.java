package com.hbm.nucleartech.block.custom;

import com.hbm.nucleartech.block.RegisterBlocks;
import com.hbm.nucleartech.particle.RegisterParticles;
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
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

import static com.hbm.nucleartech.block.custom.SettledAsbestosBlock.FACING;
import static net.minecraft.world.level.block.SnowLayerBlock.LAYERS;

public class AsbestosAirBlock extends AirBlock {

    private static final Direction[] NO_UP_OR_DOWN = Arrays.stream(Direction.values())
            .filter(d -> d != Direction.UP && d != Direction.DOWN)
            .toArray(Direction[]::new);

    public AsbestosAirBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {

        super.entityInside(pState, pLevel, pPos, pEntity);

//        System.out.println("[Debug] calling entity inside asbestos.");

        if(pEntity instanceof LivingEntity livingEntity)
            ContaminationUtil.contaminate(livingEntity, ContaminationUtil.HazardType.ASBESTOS, ContaminationUtil.ContaminationType.CREATIVE, 1);
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {

        super.randomTick(pState, pLevel, pPos, pRandom);

//        System.out.println("[Debug] random tick called");

        Direction dir = Direction.values()[pRandom.nextInt(Direction.values().length)];

        BlockPos rel = pPos.relative(dir);

        BlockState targetBefore = pLevel.getBlockState(rel);

        if(targetBefore.isAir()) {

            pLevel.setBlock(rel, pState, 3);
            pLevel.setBlock(pPos, targetBefore, 3);
        }
        else if(pRandom.nextInt(4) == 0) {

            if(dir.equals(Direction.DOWN) && pLevel.getBlockState(rel).isSolid())
                pLevel.setBlock(pPos, RegisterBlocks.SETTLED_ASBESTOS.get().defaultBlockState().setValue(FACING, NO_UP_OR_DOWN[pLevel.random.nextInt(NO_UP_OR_DOWN.length)]), 3);
            else
                pLevel.removeBlock(pPos, false);
        }
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {

        super.animateTick(pState, pLevel, pPos, pRandom);

        // animate coal dust particles
        if (pLevel.isClientSide()) {
            // spawn a few asbestos-like particles with small random offsets
            double cx = pPos.getX() + 0.5D;
            double cy = pPos.getY() + 0.5D;
            double cz = pPos.getZ() + 0.5D;

            if (pRandom.nextInt(3) == 0) { // sporadic
                double vx = (pRandom.nextDouble() - 0.5) * 0.02;
                double vy = (pRandom.nextDouble() - 0.5) * 0.02;
                double vz = (pRandom.nextDouble() - 0.5) * 0.02;
                pLevel.addParticle(RegisterParticles.ASBESTOS_PARTICLE.get(), cx, cy, cz, vx, vy, vz);
            }
        }
    }
}
