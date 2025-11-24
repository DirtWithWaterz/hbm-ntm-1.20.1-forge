package com.hbm.nucleartech.block.entity;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.block.custom.base.BaseHbmBlockEntity;
import com.hbm.nucleartech.capability.HbmCapabilities;
import com.hbm.nucleartech.capability.energy.WattHourStorage;
import com.hbm.nucleartech.datagen.HbmRecipeProvider.MetaData;
import com.hbm.nucleartech.interfaces.IEnergyItem;
import com.hbm.nucleartech.item.RegisterItems;
import com.hbm.nucleartech.item.custom.BatteryItem;
import com.hbm.nucleartech.item.custom.BladeItem;
import com.hbm.nucleartech.item.custom.SelfChargingBatteryItem;
import com.hbm.nucleartech.network.HbmPacketHandler;
import com.hbm.nucleartech.network.packet.ClientboundShredderPacket;
import com.hbm.nucleartech.recipe.ShredderRecipe;
import com.hbm.nucleartech.screen.ShredderMenu;
import com.hbm.nucleartech.sound.RegisterSounds;
import com.hbm.nucleartech.util.FloatingLong;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hbm.nucleartech.capability.energy.WattHourStorage.translateWattHours;

public class ArmorModificationTableEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

    public static final String ANIM = "animation.armor_modification_table.";
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

//    private final ItemStackHandler itemHandler = new ItemStackHandler(30){
//        @Override
//        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
//
//            if(slot == POWER_SLOT && !(isEnergyItem(stack) && canExtract(stack)))
//                return false;
//
//            else if(slot == LEFT_BLADE_SLOT && !(stack.getItem() instanceof BladeItem))
//                return false;
//            else if(slot == RIGHT_BLADE_SLOT && !(stack.getItem() instanceof BladeItem))
//                return false;
//
//            for(int oS : OUTPUT_SLOT) {
//
//                if(slot == oS)
//                    return false;
//            }
//
//            return super.isItemValid(slot, stack);
//        }
//
//        @Override
//        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
//
//            setChanged();
//            return super.extractItem(slot, amount, simulate);
//        }
//
//        @Override
//        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
//
//            if(slot == POWER_SLOT && !(isEnergyItem(stack) && canExtract(stack)))
//                return stack;
//
//            else if(slot == LEFT_BLADE_SLOT && !(stack.getItem() instanceof BladeItem))
//                return stack;
//            else if(slot == RIGHT_BLADE_SLOT && !(stack.getItem() instanceof BladeItem))
//                return stack;
//
//            for(int oS : OUTPUT_SLOT) {
//
//                if(slot == oS)
//                    return stack;
//            }
//
//            setChanged();
//            return super.insertItem(slot, stack, simulate);
//        }
//
//        @Override
//        protected void onContentsChanged(int slot) {
//            super.onContentsChanged(slot);
//            setChanged();
//        }
//    };
//
//    private static final int POWER_SLOT = 0;
//    private static final int LEFT_BLADE_SLOT = 1;
//    private static final int RIGHT_BLADE_SLOT = 2;
//
//    private static final int[] INPUT_SLOT = new int[]{3, 4, 5, 6, 7, 8, 9, 10, 11};
//    private static final int[] OUTPUT_SLOT = new int[]{12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29};
//
//    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
//
//    protected final ContainerData data;
//    public int progress = 0;
//    public int soundProgress = 0;
//
//    public int maxProgress = 60; // default, overridden by hasItems() scan
//    public FloatingLong currentPowerConsumption = FloatingLong.ZERO; // default
//
//    public int scaledEnergyProgress = 0;
//
//    public boolean shred = false;
//
//    public int leftBIdx = 0;
//    public int rightBIdx = 0;
//
//    public int leftDur = 0;
//    public int rightDur = 0;
//    public int leftMaxDur = 0;
//    public int rightMaxDur = 0;

    public ArmorModificationTableEntity(BlockPos pPos, BlockState pBlockState) {
        super(RegisterBlockEntities.ARMOR_MODIFICATION_TABLE_ENTITY.get(), pPos, pBlockState);

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
//
//        this.data = new ContainerData() {
//            @Override
//            public int get(int pIndex) {
//                return switch(pIndex) {
//
//                    case 0 -> ArmorModificationTableEntity.this.progress;
//                    case 1 -> ArmorModificationTableEntity.this.scaledEnergyProgress;
//                    case 2 -> ArmorModificationTableEntity.this.leftDur;
//                    case 3 -> ArmorModificationTableEntity.this.rightDur;
//                    case 4 -> ArmorModificationTableEntity.this.leftMaxDur;
//                    case 5 -> ArmorModificationTableEntity.this.rightMaxDur;
//                    case 6 -> ArmorModificationTableEntity.this.maxProgress;
//                    default -> 0;
//                };
//            }
//
//            @Override
//            public void set(int pIndex, int pValue) {
//                switch(pIndex) {
//
//                    case 0 -> ArmorModificationTableEntity.this.progress = pValue;
//                    case 1 -> ArmorModificationTableEntity.this.scaledEnergyProgress = pValue;
//                    case 2 -> ArmorModificationTableEntity.this.leftDur = pValue;
//                    case 3 -> ArmorModificationTableEntity.this.rightDur = pValue;
//                    case 4 -> ArmorModificationTableEntity.this.leftMaxDur = pValue;
//                    case 5 -> ArmorModificationTableEntity.this.rightMaxDur = pValue;
//                    case 6 -> ArmorModificationTableEntity.this.maxProgress = pValue;
//                }
//            }
//
//            @Override
//            public int getCount() {
//                return 7;
//            }
//        };
    }

//    @Override
//    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
//
//        if(cap == ForgeCapabilities.ITEM_HANDLER) {
//            return lazyItemHandler.cast();
//        }
//
//        return super.getCapability(cap, side);
//    }

//    @Override
//    public void onLoad() {
//        super.onLoad();
//        lazyItemHandler = LazyOptional.of(() -> itemHandler);
//    }
//
//    @Override
//    public void invalidateCaps() {
//        super.invalidateCaps();
//        lazyItemHandler.invalidate();
//    }
//
//    public void drops() {
//
//        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
//        for(int i = 0; i < itemHandler.getSlots(); i++) {
//
//            inventory.setItem(i, itemHandler.getStackInSlot(i));
//        }
//        Containers.dropContents(this.level, this.worldPosition, inventory);
//    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.hbm.armor_modification_table");
    }

//    @Override
//    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
//        return new ArmorModificationTableMenu(pContainerId, pPlayerInventory, this, this.data);
//    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return null;
    }

//    @Override
//    protected void saveAdditional(CompoundTag pTag) {
//
//        pTag.put("inventory", itemHandler.serializeNBT());
//        pTag.putInt("shredder.progress", progress);
//        pTag.putInt("shredder.scaled_energy", scaledEnergyProgress);
//        pTag.putBoolean("shredder.shred", shred);
//
//        pTag.putInt("shredder.left_b_idx", leftBIdx);
//        pTag.putInt("shredder.right_b_idx", rightBIdx);
//
//        pTag.putInt("shredder.left_dur", leftDur);
//        pTag.putInt("shredder.right_dur", rightDur);
//
//        pTag.putInt("shredder.left_max_dur", leftMaxDur);
//        pTag.putInt("shredder.right_max_dur", rightMaxDur);
//
//        pTag.putInt("shredder.max_progress", maxProgress);
//
//        super.saveAdditional(pTag);
//    }
//
//    @Override
//    public void load(CompoundTag pTag) {
//
//        super.load(pTag);
//
//        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
//        progress = pTag.getInt("shredder.progress");
//        scaledEnergyProgress = pTag.getInt("shredder.scaled_energy");
//        shred = pTag.getBoolean("shredder.shred");
//
//        leftBIdx = pTag.getInt("shredder.left_b_idx");
//        rightBIdx = pTag.getInt("shredder.right_b_idx");
//
//        leftDur = pTag.getInt("shredder.left_dur");
//        rightDur = pTag.getInt("shredder.right_dur");
//
//        leftMaxDur = pTag.getInt("shredder.left_max_dur");
//        rightMaxDur = pTag.getInt("shredder.right_max_dur");
//
//        maxProgress = pTag.getInt("shredder.max_progress");
//    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        controllers.add(new AnimationController<>(this, "armor_mod_c", 0, state -> {

            BlockEntity e = state.getData(DataTickets.BLOCK_ENTITY);


            if(e instanceof ArmorModificationTableEntity) {

                state.setAnimation(RawAnimation.begin().thenLoop(ANIM + "idle"));

                return PlayState.CONTINUE;
            }
//            System.err.println("not correct entity");
                return PlayState.STOP;
        }));
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition, worldPosition.offset(1, 1, 1));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {

    }
}
