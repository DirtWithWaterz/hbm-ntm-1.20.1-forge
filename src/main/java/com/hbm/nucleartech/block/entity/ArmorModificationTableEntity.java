package com.hbm.nucleartech.block.entity;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.item.custom.base.ArmorModifierItem;
import com.hbm.nucleartech.screen.ArmorModificationTableMenu;
import com.hbm.nucleartech.util.RegisterTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
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

public class ArmorModificationTableEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

    public static final String ANIM = "animation.armor_modification_table.";
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private final ItemStackHandler itemHandler = new ItemStackHandler(10){
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {

            return switch(slot) {

                case MODIFY_SLOT -> stack.is(Tags.Items.ARMORS);
                case HELMET_SLOT -> stack.is(RegisterTags.Items.HELMET_ARMOR_MODIFIERS) && isCompatible(stack);
                case CHESTPLATE_SLOT -> stack.is(RegisterTags.Items.CHESTPLATE_ARMOR_MODIFIERS) && isCompatible(stack);
                case LEGGINGS_SLOT -> stack.is(RegisterTags.Items.LEGGINGS_ARMOR_MODIFIERS) && isCompatible(stack);
                case BOOTS_SLOT -> stack.is(RegisterTags.Items.BOOTS_ARMOR_MODIFIERS) && isCompatible(stack);
                case SERVOS_SLOT -> stack.is(RegisterTags.Items.SERVO_ARMOR_MODIFIERS) && isCompatible(stack);
                case CLADDING_SLOT -> stack.is(RegisterTags.Items.CLADDING_ARMOR_MODIFIERS) && isCompatible(stack);
                case INSERT_SLOT -> stack.is(RegisterTags.Items.INSERT_ARMOR_MODIFIERS) && isCompatible(stack);
                case SPECIAL_SLOT -> stack.is(RegisterTags.Items.SPECIAL_ARMOR_MODIFIERS) && isCompatible(stack);
                case BATTERY_SLOT -> stack.is(RegisterTags.Items.BATTERY_ARMOR_MODIFIERS) && isCompatible(stack);
                default -> super.isItemValid(slot, stack);
            };
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {

            if(getStackInSlot(MODIFY_SLOT).isEmpty() && slot != MODIFY_SLOT)
                return ItemStack.EMPTY;

            ItemStack returned = super.extractItem(slot, amount, simulate);

            if(!returned.isEmpty())
                onExtract(slot, simulate);

            setChanged();
            return returned;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {

            if(getStackInSlot(MODIFY_SLOT).isEmpty() && slot != MODIFY_SLOT)
                return stack;

            ItemStack returned = super.insertItem(slot, stack, simulate);

            if(!returned.is(stack.getItem()))
                onInsert(slot, stack);

            setChanged();
            return returned;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    };

    private boolean isCompatible(@NotNull ItemStack stack) {

        ArmorModifierItem modder = (ArmorModifierItem)stack.getItem();

        if(modder.applicableTo == ArmorModifierItem.APPLICABLE.ALL)
            return true;

        ItemStack moddingStack = this.itemHandler.getStackInSlot(MODIFY_SLOT);

        if(moddingStack.isEmpty())
            return false;

        ArmorItem.Type type = ((ArmorItem)moddingStack.getItem()).getType();

        if(modder.applicableTo == ArmorModifierItem.APPLICABLE.HELMET && type == ArmorItem.Type.HELMET)
            return true;
        else if(modder.applicableTo == ArmorModifierItem.APPLICABLE.CHESTPLATE && type == ArmorItem.Type.CHESTPLATE)
            return true;
        else if(modder.applicableTo == ArmorModifierItem.APPLICABLE.LEGGINGS && type == ArmorItem.Type.LEGGINGS)
            return true;
        else return modder.applicableTo == ArmorModifierItem.APPLICABLE.BOOTS && type == ArmorItem.Type.BOOTS;
    }

    private static final int MODIFY_SLOT = 0;

    private static final int HELMET_SLOT = 1;
    private static final int CHESTPLATE_SLOT = 2;
    private static final int LEGGINGS_SLOT = 3;
    private static final int BOOTS_SLOT = 4;

    private static final int SERVOS_SLOT = 5;
    private static final int CLADDING_SLOT = 6;
    private static final int INSERT_SLOT = 7;
    private static final int SPECIAL_SLOT = 8;
    private static final int BATTERY_SLOT = 9;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;

    public int[] values = new int[10];

    public ArmorModificationTableEntity(BlockPos pPos, BlockState pBlockState) {
        super(RegisterBlockEntities.ARMOR_MODIFICATION_TABLE_ENTITY.get(), pPos, pBlockState);

        SingletonGeoAnimatable.registerSyncedAnimatable(this);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch(pIndex) {

                    case 0,1,2,3,4,5,6,7,8,9 -> ArmorModificationTableEntity.this.values[pIndex];
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch(pIndex) {

                    case 0,1,2,3,4,5,6,7,8,9 -> ArmorModificationTableEntity.this.values[pIndex] = pValue;
                }
            }

            @Override
            public int getCount() {
                return 10;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops() {

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {

            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.hbm.armor_modification_table");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ArmorModificationTableMenu(pContainerId, pPlayerInventory, this, this.data, pPlayer);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {

        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("armor_modification_table.modify", values[0]);
        pTag.putInt("armor_modification_table.helmet", values[1]);
        pTag.putInt("armor_modification_table.chestplate", values[2]);
        pTag.putInt("armor_modification_table.leggings", values[3]);
        pTag.putInt("armor_modification_table.boots",values[4]);
        pTag.putInt("armor_modification_table.servos", values[5]);
        pTag.putInt("armor_modification_table.cladding", values[6]);
        pTag.putInt("armor_modification_table.insert", values[7]);
        pTag.putInt("armor_modification_table.special", values[8]);
        pTag.putInt("armor_modification_table.battery", values[9]);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {

        super.load(pTag);

        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        values[0] = pTag.getInt("armor_modification_table.modify");
        values[1] = pTag.getInt("armor_modification_table.helmet");
        values[2] = pTag.getInt("armor_modification_table.chestplate");
        values[3] = pTag.getInt("armor_modification_table.leggings");
        values[4] = pTag.getInt("armor_modification_table.boots");
        values[5] = pTag.getInt("armor_modification_table.servos");
        values[6] = pTag.getInt("armor_modification_table.cladding");
        values[7] = pTag.getInt("armor_modification_table.insert");
        values[8] = pTag.getInt("armor_modification_table.special");
        values[9] = pTag.getInt("armor_modification_table.battery");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        controllers.add(new AnimationController<>(this, "armor_mod_c", 0, state -> {

            BlockEntity e = state.getData(DataTickets.BLOCK_ENTITY);


            if(e instanceof ArmorModificationTableEntity) {

                state.setAnimation(RawAnimation.begin().thenPlay(ANIM + "idle"));

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

    public void onInsert(int slot, ItemStack stack) {

//        System.err.println("[Debug] on Insert:");

        switch(slot) {

            case MODIFY_SLOT:

//                System.err.println("[Debug] adding main item...");

                CompoundTag mod_tag = stack.getOrCreateTag().getCompound("hbm_armor_mod_table");

                values[0] = 1;

                for(int i = 1; i <= 9; i++) {

                    String md = mod_tag.getString(String.valueOf(i));

//                    HBM.LOGGER.warn("[Debug] metadata: {}", md);

                    String[] metadata = md.split(":");

                    if(metadata.length != 3)
                        continue;

                    Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(metadata[0], metadata[1]));

                    ItemStack itemStack = item != null ? new ItemStack(item) : ItemStack.EMPTY;

                    int slotI = Integer.parseInt(metadata[2]);

                    if(!itemStack.isEmpty()) {

                        this.itemHandler.setStackInSlot(slotI, itemStack.getItem().getDefaultInstance());

                        values[i] = 1;
                    } else
                        values[i] = 0;
                }
//                a metadata looks like this: "hbm:heart_crystal:8"
                break;
            case HELMET_SLOT:
                break;
            case CHESTPLATE_SLOT:
                break;
            case LEGGINGS_SLOT:
                break;
            case BOOTS_SLOT:
                break;
            case SERVOS_SLOT:
                break;
            case CLADDING_SLOT:
                break;
            case INSERT_SLOT:
                break;
            case SPECIAL_SLOT:

//                System.err.println("[Debug] adding special item...");

                ItemStack moddingStack = this.itemHandler.getStackInSlot(MODIFY_SLOT);

                CompoundTag root = moddingStack.getOrCreateTag();

                CompoundTag special_tag = root.getCompound("hbm_armor_mod_table");

                String md = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString() + ":" + 8;

//                HBM.LOGGER.warn("[Debug] <Special Slot> inserting -> metadata: {}", md);

                special_tag.putString("8", md);

                root.put("hbm_armor_mod_table", special_tag);

                moddingStack.setTag(root);

                values[8] = 1;

                break;
            case BATTERY_SLOT:
                break;
        }

        setChanged();
    }

    public void onExtract(int slot, boolean simulate) {

//        System.err.println("[Debug] on Extract:");

        if(simulate) return;

//        System.err.println("[Debug] passed simulate check...");

        switch(slot) {

            case MODIFY_SLOT:

//                System.err.println("[Debug] removing main item...");

                values[0] = 0;

                for(int i = 1; i <= 9; i++) {

                    this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);

                    values[i] = 0;
                }

                break;
            case HELMET_SLOT:
                break;
            case CHESTPLATE_SLOT:
                break;
            case LEGGINGS_SLOT:
                break;
            case BOOTS_SLOT:
                break;
            case SERVOS_SLOT:
                break;
            case CLADDING_SLOT:
                break;
            case INSERT_SLOT:
                break;
            case SPECIAL_SLOT:

//                System.err.println("[Debug] removing special item...");

                ItemStack moddingStack = this.itemHandler.getStackInSlot(MODIFY_SLOT);

                CompoundTag root = moddingStack.getOrCreateTag();

                CompoundTag tag = root.getCompound("hbm_armor_mod_table");

                tag.remove("8");

                root.put("hbm_armor_mod_table", tag);

                moddingStack.setTag(root);

                values[8] = 0;

                break;
            case BATTERY_SLOT:
                break;
        }

        setChanged();
    }
//
//    public void onChanged(int slot) {
//
//        switch (slot) {
//
//            case MODIFY_SLOT:
//
//                ItemStack modify_stack = this.itemHandler.getStackInSlot(SPECIAL_SLOT);
//
//                if(modify_stack.isEmpty()) {
//
////                    System.err.println("[Debug] removing main item...");
//
//                    values[0] = 0;
//
//                    for(int i = 1; i <= 9; i++) {
//
//                        this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
//
//                        values[i] = 0;
//                    }
//                }
//                else {
//
////                    System.err.println("[Debug] adding main item...");
//
//                    CompoundTag mod_tag = modify_stack.getOrCreateTag().getCompound("hbm_armor_mod_table");
//
//                    values[0] = 1;
//
//                    for(int i = 1; i <= 9; i++) {
//
//                        String md = mod_tag.getString(String.valueOf(i));
//
////                        HBM.LOGGER.warn("[Debug] metadata: {}", md);
//
//                        String[] metadata = md.split(":");
//
//                        if(metadata.length != 3)
//                            continue;
//
//                        Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(metadata[0], metadata[1]));
//
//                        ItemStack itemStack = item != null ? new ItemStack(item) : ItemStack.EMPTY;
//
//                        int slotI = Integer.parseInt(metadata[2]);
//
//                        if(!itemStack.isEmpty()) {
//
//                            this.itemHandler.setStackInSlot(slotI, itemStack.getItem().getDefaultInstance());
//
//                            values[i] = 1;
//                        } else
//                            values[i] = 0;
//                    }
////                a metadata looks like this: "hbm:heart_crystal:8"
//                }
//
//                break;
//            case HELMET_SLOT:
//                break;
//            case CHESTPLATE_SLOT:
//                break;
//            case LEGGINGS_SLOT:
//                break;
//            case BOOTS_SLOT:
//                break;
//            case SERVOS_SLOT:
//                break;
//            case CLADDING_SLOT:
//                break;
//            case INSERT_SLOT:
//                break;
//            case SPECIAL_SLOT:
//
//                ItemStack special_stack = this.itemHandler.getStackInSlot(SPECIAL_SLOT);
//
//                if(special_stack.isEmpty()) {
//
////                    System.err.println("[Debug] removing special item...");
//
//                    ItemStack moddingStack = this.itemHandler.getStackInSlot(MODIFY_SLOT);
//
//                    CompoundTag root = moddingStack.getOrCreateTag();
//
//                    CompoundTag tag = root.getCompound("hbm_armor_mod_table");
//
//                    tag.remove("8");
//
//                    root.put("hbm_armor_mod_table", tag);
//
//                    moddingStack.setTag(root);
//
//                    values[8] = 0;
//                }
//                else {
//
////                    System.err.println("[Debug] adding special item...");
//
//                    ItemStack moddingStack = this.itemHandler.getStackInSlot(MODIFY_SLOT);
//
//                    CompoundTag root = moddingStack.getOrCreateTag();
//
//                    CompoundTag special_tag = root.getCompound("hbm_armor_mod_table");
//
//                    String md = ForgeRegistries.ITEMS.getKey(special_stack.getItem()).toString() + ":" + 8;
//
////                    HBM.LOGGER.warn("[Debug] <Special Slot> inserting -> metadata: {}", md);
//
//                    special_tag.putString("8", md);
//
//                    root.put("hbm_armor_mod_table", special_tag);
//
//                    moddingStack.setTag(root);
//
//                    values[8] = 1;
//                }
//
//                break;
//            case BATTERY_SLOT:
//                break;
//        }
//    }
}
