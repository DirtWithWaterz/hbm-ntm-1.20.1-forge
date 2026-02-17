package com.hbm.nucleartech.hazard;

import java.util.function.Supplier;

import com.hbm.nucleartech.Config;
import com.hbm.nucleartech.interfaces.IItemHazard;
import com.hbm.nucleartech.modules.ItemHazardModule;
import com.hbm.nucleartech.util.ContaminationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.TickPriority;

public class LiquidHazardBlock extends LiquidBlock implements IItemHazard {

    ItemHazardModule module;

    private double radIn = 0.0F;
    private double rad3d = 0.0f;
    private LiquidHazardBlock.ExtDisplayEffect extEffect = null;

    private boolean beaconable = false;

    public LiquidHazardBlock() {
        
        super(Fluids.WATER, Properties.copy(Blocks.WATER));
        this.module = new ItemHazardModule();
    }

    public LiquidHazardBlock(FlowingFluid pFluid, Properties pProperties, double rad) {

        super(pFluid, pProperties);
        this.module = new ItemHazardModule();
        this.rad3d = rad;
        this.radIn = rad;
    }

    public LiquidHazardBlock(Supplier<? extends FlowingFluid> pFluid, Properties pProperties, double rad) {
        
        super(pFluid, pProperties);
        this.module = new ItemHazardModule();
        this.rad3d = rad;
        this.radIn = rad;
    }

    public LiquidHazardBlock(Supplier<? extends FlowingFluid> pFluid, Properties pProperties, SoundType pSoundType){
        
        super(pFluid, pProperties.sound(pSoundType));
    }

    public LiquidHazardBlock setDisplayEffect(LiquidHazardBlock.ExtDisplayEffect extEffect){
        this.extEffect = extEffect;
        return this;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);

        if(extEffect == null)
            return;

        switch(extEffect) {
            case RADFOG:
                sPart(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), pRandom);
            case SCHRAB:
                break;
            case FLAMES:
                sPart(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), pRandom);
                break;
            case SPARKS:
                break;
            case LAVAPOP:
                pLevel.addParticle(ParticleTypes.LAVA, pPos.getX() + pRandom.nextFloat(), pPos.getY() + 1, pPos.getZ() + pRandom.nextFloat(), 0.0, 0.0, 0.0);
                break;
            default:
                break;
        }
    }

    private void sPart(Level pLevel, int x, int y, int z, RandomSource rand){

        for(Direction dir : Direction.values()){

            if(dir == Direction.DOWN && this.extEffect == LiquidHazardBlock.ExtDisplayEffect.FLAMES)
                continue;

            if(pLevel.getBlockState(new BlockPos(x + dir.getStepX(), y + dir.getStepY(), z + dir.getStepZ())).getProperties() == Blocks.AIR.defaultBlockState()) {

                double ix = x + 0.5F + dir.getStepX() + rand.nextDouble() * 3 -1.5D;
                double iy = x + 0.5F + dir.getStepY() + rand.nextDouble() * 3 -1.5D;
                double iz = x + 0.5F + dir.getStepZ() + rand.nextDouble() * 3 -1.5D;

                if(dir.getStepX() != 0)
                    ix = x + 0.5F + dir.getStepX() * 0.5F + rand.nextDouble() * dir.getStepX();
                if(dir.getStepY() != 0)
                    iy = x + 0.5F + dir.getStepY() * 0.5F + rand.nextDouble() * dir.getStepY();
                if(dir.getStepZ() != 0)
                    iz = x + 0.5F + dir.getStepZ() * 0.5F + rand.nextDouble() * dir.getStepZ();

                if(this.extEffect == LiquidHazardBlock.ExtDisplayEffect.RADFOG) {
                    pLevel.addParticle(ParticleTypes.MYCELIUM, ix, iy, iz, 0.0, 0.0, 0.0);
                }
                if(this.extEffect == LiquidHazardBlock.ExtDisplayEffect.SCHRAB) {
                    // vvv add new schrabidium particle effect vvv

                    // ^^^                                     ^^^
                }
                if(this.extEffect == LiquidHazardBlock.ExtDisplayEffect.FLAMES) {
                    pLevel.addParticle(ParticleTypes.FLAME, ix, iy, iz, 0.0, 0.0, 0.0);
                    pLevel.addParticle(ParticleTypes.SMOKE, ix, iy, iz, 0.0, 0.0, 0.0);
                    pLevel.addParticle(ParticleTypes.SMOKE, ix, iy, iz, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Override
    public ItemHazardModule getModule() {
        return module;
    }

    @Override
    public IItemHazard addRadiation(double radiation){
        this.getModule().addRadiation(radiation);
        this.radIn = radiation * 0.1F;
        return this;
    }

    public LiquidHazardBlock makeBeaconable() {
        this.beaconable = true;
        return this;
    }

    public LiquidHazardBlock addRad3d(int rad3d) {
        this.rad3d = rad3d;
        return this;
    }

    public boolean isBeaconable() {
        return beaconable;
    }

    public void onGenerated(ServerLevel level, BlockPos worldPosition) {

        level.scheduleTick(worldPosition, this.toBlock(), this.tickRate(level), TickPriority.HIGH);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);

        if(this.rad3d > 0){
            ContaminationUtil.radiate(pLevel, pPos.getX()+0.5, pPos.getY()+0.5, pPos.getZ()+0.5, 32, (float)this.rad3d, 0, this.module.fire * 5000, 0, 0, pPos);
            pLevel.scheduleTick(pPos, this.toBlock(), this.tickRate(pLevel), TickPriority.HIGH);
        }
//        if(this.radIn > 0){
//            RadiationSavedData.incrementRad(pLevel, pPos, (float)radIn, (float)radIn*10F);
//        }
    }

    public int tickRate(Level level){
        if(this.rad3d > 0)
            return ((1 - ((Config.hazardBlockTickSpeed - 1) / 20)) * 20);
//        if(this.radIn > 0)
//            return 60+level.random.nextInt(500);
        return 20;
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        if(this.radIn > 0 || this.rad3d > 0){
            pLevel.scheduleTick(pPos, this.toBlock(), this.tickRate(pLevel));
        }
    }

    public static enum ExtDisplayEffect {
        RADFOG,
        SPARKS,
        SCHRAB,
        FLAMES,
        LAVAPOP
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        super.stepOn(pLevel, pPos, pState, pEntity);
        if(pEntity instanceof LivingEntity e)
            this.module.update(null, e, 0.5F, 0, false, InteractionHand.MAIN_HAND);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        super.entityInside(pState, pLevel, pPos, pEntity);
        if(pEntity instanceof LivingEntity e)
            this.module.update(null, e, 0.5F, 0, false, InteractionHand.MAIN_HAND);
    }
}
