package com.hbm.nucleartech.block.custom;

import com.hbm.nucleartech.block.entity.GraphiteBlockEntity;
import com.hbm.nucleartech.block.entity.RegisterBlockEntities;
import com.hbm.nucleartech.datagen.HbmItemTagGenerator;
import com.hbm.nucleartech.item.RegisterItems;
import com.hbm.nucleartech.util.RegisterTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;

import static com.hbm.nucleartech.HBM.getItemsFromTag;
import static com.hbm.nucleartech.datagen.HbmItemTagGenerator.SharedTagLists.ROD_MAP;
import static net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER;

public class GraphiteBlock extends BaseEntityBlock {

    private static final Direction[] HORZ_DIRS = Arrays.stream(Direction.values()) .filter(d -> d != Direction.UP && d != Direction.DOWN) .toArray(Direction[]::new);

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public GraphiteBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new GraphiteBlockEntity(pPos, pState);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Block.box(0, 0, 0, 16, 16, 16);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {

        pState.setValue(FACING, HORZ_DIRS[pLevel.random.nextInt(HORZ_DIRS.length)]);

        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {

        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        if(pLevel.isClientSide()) return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);

        if(pHand == InteractionHand.OFF_HAND)
            return InteractionResult.PASS;

        ItemStack stack = pPlayer.getItemInHand(pHand);

//        System.out.println("[Debug] use called! clientside: " + pLevel.isClientSide() + ", with hand: " + pHand.name());

        if(pLevel.getBlockEntity(pPos) instanceof GraphiteBlockEntity graphiteBlock) {

//            System.out.println("[Debug] it is a graphite block.");
//            System.out.println("[Debug] player is holding: " + stack.getDisplayName().plainCopy().getString());

            switch (graphiteBlock.get(0)) {

                case -1:

//                    System.out.println("[Debug] it is undrilled.");


                    if(stack.is(RegisterItems.HAND_DRILL.get())) {

//                        System.out.println("[Debug] drilling...");

                        graphiteBlock.set(0, 0);

                        stack.hurtAndBreak(1, pPlayer, (p) -> {
                            p.broadcastBreakEvent(pHand);
                        });

                        return InteractionResult.SUCCESS;
                    }
                    else
                        return InteractionResult.FAIL;
                case 0:

//                    System.out.println("[Debug] it is drilled. Empty.");

                    if(stack.is(RegisterTags.Items.PILE_RODS)) {

//                        System.out.println("[Debug] attempting to insert rod: " + stack.getDisplayName().plainCopy().getString());

                        if (!pPlayer.getAbilities().instabuild) {

                                if (stack.getCount() > 1) {

                                    stack.shrink(1);
                                } else {

                                    pPlayer.setItemInHand(pHand, ItemStack.EMPTY);
                                }
                        }
                        graphiteBlock.set(0, ROD_MAP.get(stack.getDisplayName().plainCopy().getString()));

                        return InteractionResult.SUCCESS;
                    }
                    else
                        return InteractionResult.FAIL;
                case 1:

                    if(stack.is(RegisterItems.HAND_DRILL.get())) {

                        String heatPrefix = "";

                        float heat = graphiteBlock.heat;

                        if(heat < 200)
                            heatPrefix += ChatFormatting.GREEN;
                        else if(heat < 400)
                            heatPrefix += ChatFormatting.YELLOW;
                        else if(heat < 600)
                            heatPrefix += ChatFormatting.GOLD;
                        else if(heat < 800)
                            heatPrefix += ChatFormatting.RED;
                        else if(heat < 1000)
                            heatPrefix += ChatFormatting.DARK_RED;
                        else
                            heatPrefix += ChatFormatting.DARK_GRAY;

                        pPlayer.sendSystemMessage(Component.literal("CP1 FUEL ASSEMBLY ID" + graphiteBlock.getBlockPos().getX() + "" + graphiteBlock.getBlockPos().getY() + "" + graphiteBlock.getBlockPos().getZ()).withStyle(ChatFormatting.GOLD));

                        pPlayer.sendSystemMessage(Component.literal("HEAT: " + heatPrefix + String.format("%.1f", heat) + "°C").withStyle(ChatFormatting.YELLOW));
                        pPlayer.sendSystemMessage(Component.literal("DEPLETION: " + String.format("%.2f", graphiteBlock.depletionPercentage) + "%").withStyle(ChatFormatting.YELLOW));
                        pPlayer.sendSystemMessage(Component.literal("NEUTRON FLUX: " + graphiteBlock.lastNeutrons).withStyle(ChatFormatting.YELLOW));
                    }
                    else if(stack.is(RegisterItems.SCREWDRIVER.get())) {

                        graphiteBlock.set(0, 0);

                        boolean added = pPlayer.addItem(RegisterItems.URANIUM_PILE_ROD.get().getDefaultInstance());
                        if(!added)
                            pPlayer.drop(RegisterItems.URANIUM_PILE_ROD.get().getDefaultInstance(), false);

                        stack.hurtAndBreak(1, pPlayer, (p) -> {
                            p.broadcastBreakEvent(pHand);
                        });

                        return InteractionResult.SUCCESS;
                    }
                    else
                        return InteractionResult.FAIL;
                case 2:

                    if(stack.is(RegisterItems.SCREWDRIVER.get())) {

                        graphiteBlock.set(0, 0);

                        boolean added = pPlayer.addItem(RegisterItems.PLUTONIUM_PILE_ROD.get().getDefaultInstance());
                        if(!added)
                            pPlayer.drop(RegisterItems.PLUTONIUM_PILE_ROD.get().getDefaultInstance(), false);

                        stack.hurtAndBreak(1, pPlayer, (p) -> {
                            p.broadcastBreakEvent(pHand);
                        });

                        return InteractionResult.SUCCESS;
                    }
                    else
                        return InteractionResult.FAIL;
                case 3:

                    if(stack.is(RegisterItems.SCREWDRIVER.get())) {

                        graphiteBlock.set(0, 0);

                        boolean added = pPlayer.addItem(RegisterItems.RADIUM_PILE_ROD.get().getDefaultInstance());
                        if(!added)
                            pPlayer.drop(RegisterItems.RADIUM_PILE_ROD.get().getDefaultInstance(), false);

                        stack.hurtAndBreak(1, pPlayer, (p) -> {
                            p.broadcastBreakEvent(pHand);
                        });

                        return InteractionResult.SUCCESS;
                    }
                    else
                        return InteractionResult.FAIL;
                case 4:

//                    System.out.println("[Debug] control rod. hand: " + stack.getDisplayName().plainCopy().getString() + ", is empty: " + stack.isEmpty());

                    if(stack.is(RegisterItems.SCREWDRIVER.get())) {

                        graphiteBlock.set(0, 0);

                        boolean added = pPlayer.addItem(RegisterItems.BORON_PILE_ROD.get().getDefaultInstance());
                        if(!added)
                            pPlayer.drop(RegisterItems.BORON_PILE_ROD.get().getDefaultInstance(), false);

                        stack.hurtAndBreak(1, pPlayer, (p) -> {
                            p.broadcastBreakEvent(pHand);
                        });

                        return InteractionResult.SUCCESS;
                    }
                    else if(stack.isEmpty()) {

                        graphiteBlock.set(0, 5);
                        return InteractionResult.SUCCESS;
                    }
                    else
                        return InteractionResult.FAIL;
                case 5:

//                    System.out.println("[Debug] control rod. hand: " + stack.getDisplayName().plainCopy().getString() + ", is empty: " + stack.isEmpty());

                    if(stack.is(RegisterItems.SCREWDRIVER.get())) {

                        graphiteBlock.set(0, 0);

                        boolean added = pPlayer.addItem(RegisterItems.BORON_PILE_ROD.get().getDefaultInstance());
                        if(!added)
                            pPlayer.drop(RegisterItems.BORON_PILE_ROD.get().getDefaultInstance(), false);

                        stack.hurtAndBreak(1, pPlayer, (p) -> {
                            p.broadcastBreakEvent(pHand);
                        });

                        return InteractionResult.SUCCESS;
                    }
                    else if(stack.isEmpty()) {

                        graphiteBlock.set(0, 4);
                        return InteractionResult.SUCCESS;
                    }
                    else
                        return InteractionResult.FAIL;
                default:
//                    System.out.println("[Debug] the block is not a valid type.");
                    return InteractionResult.FAIL;
            }
        }
//        System.out.println("[Debug] it is not a graphite block.");
        return InteractionResult.FAIL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {

        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {

        ((GraphiteBlockEntity)pLevel.getBlockEntity(pPos)).tick(pLevel, pPos, pState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {

        if(pLevel.isClientSide())
            return null;

        return createTickerHelper(pBlockEntityType, RegisterBlockEntities.GRAPHITE_BLOCK_ENTITY.get(),
                (pLevel1, pPos, pState1, pBlockEntity) ->
                    pBlockEntity.tick(pLevel1, pPos, pState1));
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {

        if(!pPlayer.isCreative() && !pPlayer.isSpectator()) {

            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if(blockEntity instanceof GraphiteBlockEntity graphiteBlock)
                graphiteBlock.drops();
        }

        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public void destroy(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {

        super.destroy(pLevel, pPos, pState);
    }

    @Override
    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pBlockEntity, ItemStack pTool) {

        pPlayer.awardStat(Stats.BLOCK_MINED.get(this));
        pPlayer.causeFoodExhaustion(0.005F);
    }
}
