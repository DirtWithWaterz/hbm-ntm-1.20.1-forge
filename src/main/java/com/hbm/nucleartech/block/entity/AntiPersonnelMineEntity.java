package com.hbm.nucleartech.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.util.RenderUtils;

import java.util.List;

public class AntiPersonnelMineEntity extends BlockEntity {

    private static final int activationDistance = 2;
    private final AABB box;

    public AntiPersonnelMineEntity(BlockPos pPos, BlockState pBlockState) {
        super(RegisterBlockEntities.ANTI_PERSONNEL_MINE_ENTITY.get(), pPos, pBlockState);

        this.box = new AABB(pPos).inflate(activationDistance);
    }

    public void tick(BlockState pState, Level pLevel, BlockPos pPos) {

        if(pLevel.isClientSide) return;
        if(pLevel.getServer().getTickCount() % 10 != 0) return;

        List<LivingEntity> entities = pLevel.getEntitiesOfClass(LivingEntity.class, box);

        LivingEntity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        for (LivingEntity target : entities) {

            double distance = this.worldPosition.distSqr(target.blockPosition());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestEntity = target;
            }
        }

        if (closestEntity != null) {

            System.out.println("[Debug] kaboom!!");
        }
    }
}
