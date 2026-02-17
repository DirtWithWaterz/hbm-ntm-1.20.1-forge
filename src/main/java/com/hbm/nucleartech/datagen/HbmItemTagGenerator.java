package com.hbm.nucleartech.datagen;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.item.RegisterItems;
import com.hbm.nucleartech.util.RegisterTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HbmItemTagGenerator extends ItemTagsProvider {
    public HbmItemTagGenerator(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {

        super(p_275343_, p_275729_, p_275322_, HBM.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        tag(RegisterTags.Items.PLATE_STAMPS)
                .add(SharedTagLists.PLATE_STAMPS.toArray(new Item[0]));
        tag(RegisterTags.Items.WIRE_STAMPS)
                .add(SharedTagLists.WIRE_STAMPS.toArray(new Item[0]));
        tag(RegisterTags.Items.DESH)
                .add(SharedTagLists.DESH.toArray(new Item[0]));
        tag(RegisterTags.Items.SHREDDER_BLADES)
                .add(SharedTagLists.SHREDDER_BLADES.toArray(new Item[0]));
        tag(RegisterTags.Items.PILE_RODS)
                .add(SharedTagLists.PILE_RODS.toArray(new Item[0]));
        tag(RegisterTags.Items.BIOMASS)
                .add(SharedTagLists.BIOMASS.toArray(new Item[0]));
        tag(RegisterTags.Items.SPECIAL_ARMOR_MODIFIERS)
                .add(SharedTagLists.SPECIAL_ARMOR_MODIFIERS.toArray(new Item[0]));

        tag(Tags.Items.INGOTS)
                .add(
                        RegisterItems.ALUMINUM_INGOT.get(),
                        RegisterItems.TITANIUM_INGOT.get()
                );

        tag(Tags.Items.ARMORS)
                .add(
                        RegisterItems.HAZMAT_BOOTS.get(),
                        RegisterItems.HAZMAT_BOOTS_GREY.get(),
                        RegisterItems.HAZMAT_BOOTS_RED.get(),
                        RegisterItems.HAZMAT_LEGGINGS.get(),
                        RegisterItems.HAZMAT_LEGGINGS_GREY.get(),
                        RegisterItems.HAZMAT_LEGGINGS_RED.get(),
                        RegisterItems.HAZMAT_CHESTPLATE.get(),
                        RegisterItems.HAZMAT_CHESTPLATE_GREY.get(),
                        RegisterItems.HAZMAT_CHESTPLATE_RED.get(),
                        RegisterItems.HAZMAT_HELMET.get(),
                        RegisterItems.HAZMAT_HELMET_GREY.get(),
                        RegisterItems.HAZMAT_HELMET_RED.get()
                );

        tag(Tags.Items.ARMORS_BOOTS)
                .add(
                        RegisterItems.HAZMAT_BOOTS.get(),
                        RegisterItems.HAZMAT_BOOTS_GREY.get(),
                        RegisterItems.HAZMAT_BOOTS_RED.get()
                );

        tag(Tags.Items.ARMORS_LEGGINGS)
                .add(
                        RegisterItems.HAZMAT_LEGGINGS.get(),
                        RegisterItems.HAZMAT_LEGGINGS_GREY.get(),
                        RegisterItems.HAZMAT_LEGGINGS_RED.get()
                );

        tag(Tags.Items.ARMORS_CHESTPLATES)
                .add(
                        RegisterItems.HAZMAT_CHESTPLATE.get(),
                        RegisterItems.HAZMAT_CHESTPLATE_GREY.get(),
                        RegisterItems.HAZMAT_CHESTPLATE_RED.get()
                );

        tag(Tags.Items.ARMORS_HELMETS)
                .add(
                        RegisterItems.HAZMAT_HELMET.get(),
                        RegisterItems.HAZMAT_HELMET_GREY.get(),
                        RegisterItems.HAZMAT_HELMET_RED.get()
                );
    }

    public static class SharedTagLists {

        public static final List<Item> SPECIAL_ARMOR_MODIFIERS = List.of(
                RegisterItems.HEART_PIECE.get(),
                RegisterItems.HEART_CONTAINER.get(),
                RegisterItems.FAB_HEART.get(),
                RegisterItems.HEART_BOOSTER.get(),
                RegisterItems.HEART_OF_DARKNESS.get(),
                RegisterItems.BLACK_DIAMOND.get()
        );
        public static final List<Item> PLATE_STAMPS = List.of(
                RegisterItems.IRON_PLATE_STAMP.get(),
                RegisterItems.STEEL_PLATE_STAMP.get(),
                RegisterItems.TITANIUM_PLATE_STAMP.get(),
                RegisterItems.OBSIDIAN_PLATE_STAMP.get(),
                RegisterItems.DESH_PLATE_STAMP.get(),
                RegisterItems.SCHRABIDIUM_PLATE_STAMP.get()
        );
        public static final List<Item> WIRE_STAMPS = List.of(
                RegisterItems.IRON_WIRE_STAMP.get(),
                RegisterItems.STEEL_WIRE_STAMP.get(),
                RegisterItems.TITANIUM_WIRE_STAMP.get(),
                RegisterItems.OBSIDIAN_WIRE_STAMP.get(),
                RegisterItems.DESH_WIRE_STAMP.get(),
                RegisterItems.SCHRABIDIUM_WIRE_STAMP.get()
        );
        public static final List<Item> DESH = List.of(
                RegisterItems.DESH_WIRE_STAMP.get(),
                RegisterItems.DESH_PLATE_STAMP.get(),
                RegisterItems.DESH_BLADE.get()
        );
        public static final List<Item> SHREDDER_BLADES = List.of(
                RegisterItems.ALUMINUM_BLADE.get(),
                RegisterItems.GOLD_BLADE.get(),
                RegisterItems.IRON_BLADE.get(),
                RegisterItems.STEEL_BLADE.get(),
                RegisterItems.TITANIUM_BLADE.get(),
                RegisterItems.ADVANCED_BLADE.get(),
                RegisterItems.CMB_BLADE.get(),
                RegisterItems.SCHRABIDIUM_BLADE.get(),
                RegisterItems.DESH_BLADE.get()
        );
        public static final List<Item> PILE_RODS = List.of(
                RegisterItems.URANIUM_PILE_ROD.get(),
                RegisterItems.PLUTONIUM_PILE_ROD.get(),
                RegisterItems.RADIUM_PILE_ROD.get(),
                RegisterItems.BORON_PILE_ROD.get()
        );
        public static final Map<String, Integer> ROD_MAP = Map.of(
                HbmItemTagGenerator.SharedTagLists.PILE_RODS.get(0).getDefaultInstance().getDisplayName().plainCopy().getString(), 1,
                HbmItemTagGenerator.SharedTagLists.PILE_RODS.get(1).getDefaultInstance().getDisplayName().plainCopy().getString(), 2,
                HbmItemTagGenerator.SharedTagLists.PILE_RODS.get(2).getDefaultInstance().getDisplayName().plainCopy().getString(), 3,
                HbmItemTagGenerator.SharedTagLists.PILE_RODS.get(3).getDefaultInstance().getDisplayName().plainCopy().getString(), 4
        );
        public static final List<Item> BIOMASS = List.of(
                Blocks.ACACIA_LOG.asItem(),
                Blocks.BIRCH_LOG.asItem(),
                Blocks.CHERRY_LOG.asItem(),
                Blocks.DARK_OAK_LOG.asItem(),
                Blocks.JUNGLE_LOG.asItem(),
                Blocks.MANGROVE_LOG.asItem(),
                Blocks.OAK_LOG.asItem(),
                Blocks.SPRUCE_LOG.asItem(),

                Blocks.ACACIA_WOOD.asItem(),
                Blocks.BIRCH_WOOD.asItem(),
                Blocks.CHERRY_WOOD.asItem(),
                Blocks.DARK_OAK_WOOD.asItem(),
                Blocks.JUNGLE_WOOD.asItem(),
                Blocks.MANGROVE_WOOD.asItem(),
                Blocks.OAK_WOOD.asItem(),
                Blocks.SPRUCE_WOOD.asItem(),

                Items.CARROT,
                Items.POTATO,
                Items.BEETROOT,
                Items.WHEAT,
                Items.APPLE,
                Items.SWEET_BERRIES,
                Items.GLOW_BERRIES,

                Items.MOSS_BLOCK,
                Items.MOSS_CARPET,
                Items.AZALEA,

                Blocks.ACACIA_LEAVES.asItem(),
                Blocks.BIRCH_LEAVES.asItem(),
                Blocks.CHERRY_LEAVES.asItem(),
                Blocks.DARK_OAK_LEAVES.asItem(),
                Blocks.JUNGLE_LEAVES.asItem(),
                Blocks.MANGROVE_LEAVES.asItem(),
                Blocks.OAK_LEAVES.asItem(),
                Blocks.SPRUCE_LEAVES.asItem(),
                Blocks.AZALEA_LEAVES.asItem(),

                Items.ACACIA_SAPLING,
                Items.BIRCH_SAPLING,
                Items.CHERRY_SAPLING,
                Items.DARK_OAK_SAPLING,
                Items.JUNGLE_SAPLING,
                Items.MANGROVE_PROPAGULE,
                Items.OAK_SAPLING,
                Items.SPRUCE_SAPLING,

                Items.GRASS,
                Items.TALL_GRASS,
                Items.SEAGRASS,
                Items.SEA_PICKLE
        );
    }
}
