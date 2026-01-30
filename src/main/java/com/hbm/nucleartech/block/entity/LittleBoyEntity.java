package com.hbm.nucleartech.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class LittleBoyEntity extends BlockEntity /*implements GeoBlockEntity*/ {

//    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public LittleBoyEntity(BlockPos pPos, BlockState pBlockState) {
        super(RegisterBlockEntities.LITTLE_BOY_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.offset(-1, 0, 0), worldPosition.offset(4, 1, 1));
    }

//    @Override
//    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
//
//    }

//    @Override
//    public AnimatableInstanceCache getAnimatableInstanceCache() {
//        return cache;
//    }
}
