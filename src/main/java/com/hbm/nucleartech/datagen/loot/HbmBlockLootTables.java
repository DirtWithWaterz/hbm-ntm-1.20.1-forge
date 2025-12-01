package com.hbm.nucleartech.datagen.loot;

import com.hbm.nucleartech.block.RegisterBlocks;
import com.hbm.nucleartech.item.RegisterItems;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class HbmBlockLootTables extends BlockLootSubProvider {


    public HbmBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {

        this.dropSelf(RegisterBlocks.COAL_AIR.get());
        this.dropSelf(RegisterBlocks.ASBESTOS_AIR.get());

        this.dropSelf(RegisterBlocks.WASTE_BLOCK.get());
        this.dropSelf(RegisterBlocks.TITANIUM_BLOCK.get());
        this.dropSelf(RegisterBlocks.URANIUM_BLOCK.get());
        this.dropSelf(RegisterBlocks.RADIATION_DECONTAMINATOR.get());

        this.dropSelf(RegisterBlocks.SULFUR_BLOCK.get());
        this.dropSelf(RegisterBlocks.NITER_BLOCK.get());
        this.dropSelf(RegisterBlocks.TUNGSTEN_BLOCK.get());
        this.dropSelf(RegisterBlocks.RAW_TUNGSTEN_BLOCK.get());
        this.dropSelf(RegisterBlocks.ALUMINIUM_BLOCK.get());
        this.dropSelf(RegisterBlocks.RAW_ALUMINIUM_BLOCK.get());
        this.dropSelf(RegisterBlocks.FLUORITE_BLOCK.get());
        this.dropSelf(RegisterBlocks.BERYLLIUM_BLOCK.get());
        this.dropSelf(RegisterBlocks.RAW_BERYLLIUM_BLOCK.get());
        this.dropSelf(RegisterBlocks.LEAD_BLOCK.get());
        this.dropSelf(RegisterBlocks.RAW_LEAD_BLOCK.get());
        this.dropSelf(RegisterBlocks.LIGNITE_BLOCK.get());
        this.dropSelf(RegisterBlocks.ASBESTOS_BLOCK.get());
        this.dropSelf(RegisterBlocks.SCHRABIDIUM_BLOCK.get());
        this.dropSelf(RegisterBlocks.RAW_SCHRABIDIUM_BLOCK.get());
        this.dropSelf(RegisterBlocks.AUSTRALIUM_BLOCK.get());
        this.dropSelf(RegisterBlocks.COBALT_BLOCK.get());
        this.dropSelf(RegisterBlocks.RAW_COBALT_BLOCK.get());
        this.dropSelf(RegisterBlocks.CINNABAR_BLOCK.get());
        this.dropSelf(RegisterBlocks.COLTAN_BLOCK.get());

        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS.get());
        this.dropSelf(RegisterBlocks.CIRCULAR_JUNGLE_BRICKS.get());
        this.dropSelf(RegisterBlocks.CRACKED_JUNGLE_BRICKS.get());
        this.dropSelf(RegisterBlocks.FRAGILE_JUNGLE_BRICKS.get());
        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS_GLYPH_0.get());
        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS_GLYPH_1.get());
        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS_GLYPH_2.get());
        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS_GLYPH_3.get());
        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS_GLYPH_4.get());
        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS_GLYPH_5.get());
        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS_GLYPH_6.get());
        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS_GLYPH_7.get());
        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS_GLYPH_8.get());
        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS_GLYPH_9.get());
        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS_GLYPH_10.get());
        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS_GLYPH_11.get());
        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS_GLYPH_12.get());
        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS_GLYPH_13.get());
        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS_GLYPH_14.get());
        this.dropSelf(RegisterBlocks.JUNGLE_BRICKS_GLYPH_15.get());

        this.dropSelf(RegisterBlocks.FIRE_BRICKS.get());
        this.dropSelf(RegisterBlocks.FORGOTTEN_BRICKS.get());
        this.dropSelf(RegisterBlocks.LAVA_JUNGLE_BRICKS.get());
        this.dropSelf(RegisterBlocks.MYSTIC_JUNGLE_BRICKS.get());
        this.dropSelf(RegisterBlocks.OOZING_JUNGLE_BRICKS.get());
        this.dropSelf(RegisterBlocks.TRAPPED_JUNGLE_BRICKS.get());
        this.dropSelf(RegisterBlocks.LIGHT_BRICKS.get());
        this.dropSelf(RegisterBlocks.LIGHT_BRICKS_ALT.get());
        this.dropSelf(RegisterBlocks.OBSIDIAN_BRICKS.get());


        this.dropSelf(RegisterBlocks.BLACK_CONCRETE.get());
        this.dropSelf(RegisterBlocks.BLUE_CONCRETE.get());
        this.dropSelf(RegisterBlocks.BROWN_CONCRETE.get());
        this.dropSelf(RegisterBlocks.CYAN_CONCRETE.get());
        this.dropSelf(RegisterBlocks.GRAY_CONCRETE.get());
        this.dropSelf(RegisterBlocks.GREEN_CONCRETE.get());
        this.dropSelf(RegisterBlocks.LIGHT_BLUE_CONCRETE.get());
        this.dropSelf(RegisterBlocks.LIGHT_GRAY_CONCRETE.get());
        this.dropSelf(RegisterBlocks.LIME_CONCRETE.get());
        this.dropSelf(RegisterBlocks.MAGENTA_CONCRETE.get());
        this.dropSelf(RegisterBlocks.ORANGE_CONCRETE.get());
        this.dropSelf(RegisterBlocks.PINK_CONCRETE.get());
        this.dropSelf(RegisterBlocks.PURPLE_CONCRETE.get());
        this.dropSelf(RegisterBlocks.RED_CONCRETE.get());
        this.dropSelf(RegisterBlocks.WHITE_CONCRETE.get());
        this.dropSelf(RegisterBlocks.YELLOW_CONCRETE.get());

        this.dropSelf(RegisterBlocks.GRAPHITE_BLOCK.get());
        this.dropSelf(RegisterBlocks.BORON_BLOCK.get());

        this.dropSelf(RegisterBlocks.SLAKED_SELLAFITE.get());
        this.dropSelf(RegisterBlocks.SELLAFITE.get());
        this.dropSelf(RegisterBlocks.HOT_SELLAFITE.get());
        this.dropSelf(RegisterBlocks.BOILING_SELLAFITE.get());
        this.dropSelf(RegisterBlocks.BLAZING_SELLAFITE.get());
        this.dropSelf(RegisterBlocks.INFERNAL_SELLAFITE.get());
        this.dropSelf(RegisterBlocks.SELLAFITE_CORIUM.get());

        this.dropSelf(RegisterBlocks.TRINITITE_BLOCK.get());

        this.add(RegisterBlocks.TRINITITE_ORE.get(),
                block -> createOreDrop(RegisterBlocks.TRINITITE_ORE.get(), RegisterItems.TRINITITE.get()));
        this.add(RegisterBlocks.RED_TRINITITE_ORE.get(),
                block -> createOreDrop(RegisterBlocks.RED_TRINITITE_ORE.get(), RegisterItems.TRINITITE.get()));

        this.add(RegisterBlocks.SCORCHED_URANIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.SCORCHED_URANIUM_ORE.get(), RegisterItems.RAW_URANIUM.get()));

        this.add(RegisterBlocks.TITANIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.TITANIUM_ORE.get(), RegisterItems.RAW_TITANIUM.get()));
        this.add(RegisterBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.DEEPSLATE_TITANIUM_ORE.get(), RegisterItems.RAW_TITANIUM.get()));

        this.add(RegisterBlocks.URANIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.URANIUM_ORE.get(), RegisterItems.RAW_URANIUM.get()));
        this.add(RegisterBlocks.DEEPSLATE_URANIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.DEEPSLATE_URANIUM_ORE.get(), RegisterItems.RAW_URANIUM.get()));

        this.add(RegisterBlocks.SULFUR_ORE.get(),
                block -> createOreDrop(RegisterBlocks.SULFUR_ORE.get(), RegisterItems.SULFUR.get()));
        this.add(RegisterBlocks.DEEPSLATE_SULFUR_ORE.get(),
                block -> createOreDrop(RegisterBlocks.DEEPSLATE_SULFUR_ORE.get(), RegisterItems.SULFUR.get()));

        this.add(RegisterBlocks.NITER_ORE.get(),
                block -> createOreDrop(RegisterBlocks.NITER_ORE.get(), RegisterItems.NITER.get()));
        this.add(RegisterBlocks.DEEPSLATE_NITER_ORE.get(),
                block -> createOreDrop(RegisterBlocks.DEEPSLATE_NITER_ORE.get(), RegisterItems.NITER.get()));

        this.add(RegisterBlocks.TUNGSTEN_ORE.get(),
                block -> createOreDrop(RegisterBlocks.TUNGSTEN_ORE.get(), RegisterItems.RAW_TUNGSTEN.get()));
        this.add(RegisterBlocks.DEEPSLATE_TUNGSTEN_ORE.get(),
                block -> createOreDrop(RegisterBlocks.DEEPSLATE_TUNGSTEN_ORE.get(), RegisterItems.RAW_TUNGSTEN.get()));

        this.add(RegisterBlocks.ALUMINIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.ALUMINIUM_ORE.get(), RegisterItems.RAW_ALUMINUM.get()));
        this.add(RegisterBlocks.DEEPSLATE_ALUMINIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.DEEPSLATE_ALUMINIUM_ORE.get(), RegisterItems.RAW_ALUMINUM.get()));

        this.add(RegisterBlocks.FLUORITE_ORE.get(),
                block -> createOreDrop(RegisterBlocks.FLUORITE_ORE.get(), RegisterItems.FLUORITE.get()));
        this.add(RegisterBlocks.DEEPSLATE_FLUORITE_ORE.get(),
                block -> createOreDrop(RegisterBlocks.DEEPSLATE_FLUORITE_ORE.get(), RegisterItems.FLUORITE.get()));

        this.add(RegisterBlocks.BERYLLIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.BERYLLIUM_ORE.get(), RegisterItems.RAW_BERYLLIUM.get()));
        this.add(RegisterBlocks.DEEPSLATE_BERYLLIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.DEEPSLATE_BERYLLIUM_ORE.get(), RegisterItems.RAW_BERYLLIUM.get()));

        this.add(RegisterBlocks.LEAD_ORE.get(),
                block -> createOreDrop(RegisterBlocks.LEAD_ORE.get(), RegisterItems.RAW_LEAD.get()));
        this.add(RegisterBlocks.DEEPSLATE_LEAD_ORE.get(),
                block -> createOreDrop(RegisterBlocks.DEEPSLATE_LEAD_ORE.get(), RegisterItems.RAW_LEAD.get()));

        this.add(RegisterBlocks.LIGNITE_ORE.get(),
                block -> createOreDrop(RegisterBlocks.LIGNITE_ORE.get(), RegisterItems.LIGNITE.get()));
        this.add(RegisterBlocks.DEEPSLATE_LIGNITE_ORE.get(),
                block -> createOreDrop(RegisterBlocks.DEEPSLATE_LIGNITE_ORE.get(), RegisterItems.LIGNITE.get()));

        this.add(RegisterBlocks.ASBESTOS_ORE.get(),
                block -> createOreDrop(RegisterBlocks.ASBESTOS_ORE.get(), RegisterItems.ASBESTOS_SHEET.get()));
        this.add(RegisterBlocks.DEEPSLATE_ASBESTOS_ORE.get(),
                block -> createOreDrop(RegisterBlocks.DEEPSLATE_ASBESTOS_ORE.get(), RegisterItems.ASBESTOS_SHEET.get()));

        this.add(RegisterBlocks.SCHRABIDIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.SCHRABIDIUM_ORE.get(), RegisterItems.RAW_SCHRABIDIUM.get()));
        this.add(RegisterBlocks.DEEPSLATE_SCHRABIDIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.DEEPSLATE_SCHRABIDIUM_ORE.get(), RegisterItems.RAW_SCHRABIDIUM.get()));

        this.add(RegisterBlocks.RARE_EARTH_ORE.get(),
                block -> createOreDrop(RegisterBlocks.RARE_EARTH_ORE.get(), RegisterBlocks.RARE_EARTH_ORE.get().asItem()));
        this.add(RegisterBlocks.DEEPSLATE_RARE_EARTH_ORE.get(),
                block -> createOreDrop(RegisterBlocks.DEEPSLATE_RARE_EARTH_ORE.get(), RegisterBlocks.DEEPSLATE_RARE_EARTH_ORE.get().asItem()));

        this.add(RegisterBlocks.COBALT_ORE.get(),
                block -> createOreDrop(RegisterBlocks.COBALT_ORE.get(), RegisterItems.RAW_COBALT.get()));
        this.add(RegisterBlocks.DEEPSLATE_COBALT_ORE.get(),
                block -> createOreDrop(RegisterBlocks.DEEPSLATE_COBALT_ORE.get(), RegisterItems.RAW_COBALT.get()));

        this.add(RegisterBlocks.CINNABAR_ORE.get(),
                block -> createOreDrop(RegisterBlocks.CINNABAR_ORE.get(), RegisterItems.CINNABAR.get()));
        this.add(RegisterBlocks.DEEPSLATE_CINNABAR_ORE.get(),
                block -> createOreDrop(RegisterBlocks.DEEPSLATE_CINNABAR_ORE.get(), RegisterItems.CINNABAR.get()));

        this.add(RegisterBlocks.COLTAN_ORE.get(),
                block -> createOreDrop(RegisterBlocks.COLTAN_ORE.get(), RegisterItems.COLTAN.get()));
        this.add(RegisterBlocks.DEEPSLATE_COLTAN_ORE.get(),
                block -> createOreDrop(RegisterBlocks.DEEPSLATE_COLTAN_ORE.get(), RegisterItems.COLTAN.get()));

        this.add(RegisterBlocks.RED_THORIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.RED_THORIUM_ORE.get(), RegisterItems.THORIUM_SHALE.get()));
        this.add(RegisterBlocks.ORANGE_THORIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.ORANGE_THORIUM_ORE.get(), RegisterItems.THORIUM_SHALE.get()));
        this.add(RegisterBlocks.YELLOW_THORIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.YELLOW_THORIUM_ORE.get(), RegisterItems.THORIUM_SHALE.get()));
        this.add(RegisterBlocks.WHITE_THORIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.WHITE_THORIUM_ORE.get(), RegisterItems.THORIUM_SHALE.get()));
        this.add(RegisterBlocks.LIGHT_GRAY_THORIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.LIGHT_GRAY_THORIUM_ORE.get(), RegisterItems.THORIUM_SHALE.get()));
        this.add(RegisterBlocks.BROWN_THORIUM_ORE.get(),
                block -> createOreDrop(RegisterBlocks.BROWN_THORIUM_ORE.get(), RegisterItems.THORIUM_SHALE.get()));

        this.dropSelf(RegisterBlocks.RAD_RESISTANT_BLOCK.get());

        this.dropSelf(RegisterBlocks.DEAD_GRASS.get());

        this.dropSelf(RegisterBlocks.BURNER_PRESS.get());

        this.dropSelf(RegisterBlocks.ARMOR_MODIFICATION_TABLE.get());

        this.dropSelf(RegisterBlocks.SHREDDER.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {

        return RegisterBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
