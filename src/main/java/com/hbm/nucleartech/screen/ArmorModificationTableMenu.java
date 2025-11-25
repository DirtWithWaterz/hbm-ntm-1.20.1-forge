package com.hbm.nucleartech.screen;

import com.hbm.nucleartech.block.RegisterBlocks;
import com.hbm.nucleartech.block.entity.ArmorModificationTableEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class ArmorModificationTableMenu extends AbstractContainerMenu {

    public final ArmorModificationTableEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    private final Player player;

    public ArmorModificationTableMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {

        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(10), inv.player);
    }

    public ArmorModificationTableMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data, Player player) {

        super(RegisterMenuTypes.ARMOR_MODIFICATION_TABLE_MENU.get(), pContainerId);

        checkContainerSize(inv, 10);
        blockEntity = ((ArmorModificationTableEntity) entity);
        this.level = inv.player.level();
        this.data = data;
        this.player = player;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addPlayerArmorslots(inv);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {

            this.addSlot(new SlotItemHandler(iItemHandler, 0, 44, 63-28));
            this.addSlot(new SlotItemHandler(iItemHandler, 1, 26, 27-28));
            this.addSlot(new SlotItemHandler(iItemHandler, 2, 62, 27-28));
            this.addSlot(new SlotItemHandler(iItemHandler, 3, 98, 27-28));
            this.addSlot(new SlotItemHandler(iItemHandler, 4, 134, 45-28));
            this.addSlot(new SlotItemHandler(iItemHandler, 5, 134, 81-28));
            this.addSlot(new SlotItemHandler(iItemHandler, 6, 98, 99-28));
            this.addSlot(new SlotItemHandler(iItemHandler, 7, 62, 99-28));
            this.addSlot(new SlotItemHandler(iItemHandler, 8, 26, 99-28));
            this.addSlot(new SlotItemHandler(iItemHandler, 9, 8, 63-28));
        });

        addDataSlots(data);
    }

    public int getValue(int index) {

        return this.data.get(index);
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 10;  // must be the number of slots you have!
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, RegisterBlocks.ARMOR_MODIFICATION_TABLE.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {

        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 9; j++)
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, (84 + i * 18)+28));
    }

    private void addPlayerHotbar(Inventory playerInventory) {

        for(int i = 0; i < 9; i++)
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142+28));
    }

    private void addPlayerArmorslots(Inventory playerInventory) {
        // Armor indices: 36=boots, 37=leggings, 38=chest, 39=helmet

        // Helmet
        this.addSlot(new ArmorSlot(playerInventory, EquipmentSlot.HEAD, 39, -17, 18-10, player));

        // Chestplate
        this.addSlot(new ArmorSlot(playerInventory, EquipmentSlot.CHEST, 38, -17, 36-10, player));

        // Leggings
        this.addSlot(new ArmorSlot(playerInventory, EquipmentSlot.LEGS, 37, -17, 54-10, player));

        // Boots
        this.addSlot(new ArmorSlot(playerInventory, EquipmentSlot.FEET, 36, -17, 72-10, player));
    }

    private static class ArmorSlot extends Slot {
        private final EquipmentSlot slotType;
        private final Player player;

        public ArmorSlot(Inventory inv, EquipmentSlot slotType, int index, int x, int y, Player player) {
            super(inv, index, x, y);
            this.slotType = slotType;
            this.player = player;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.canEquip(slotType, player);
        }

        @Override
        public boolean mayPickup(Player player) {
            ItemStack stack = this.getItem();
            return !stack.isEmpty() && !EnchantmentHelper.hasBindingCurse(stack);
        }
    }
}
