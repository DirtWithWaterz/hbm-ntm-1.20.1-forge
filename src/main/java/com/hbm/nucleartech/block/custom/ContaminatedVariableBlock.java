package com.hbm.nucleartech.block.custom;
import com.hbm.nucleartech.hazard.HazardBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;

public class ContaminatedVariableBlock extends HazardBlock {

    // allow 0..7 (8 variants).
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 7);

    public ContaminatedVariableBlock(Properties properties, double rad) {
        super(properties, rad);
        // default variant 0
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
    }
}
