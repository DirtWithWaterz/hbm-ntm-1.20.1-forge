package com.hbm.nucleartech.datagen;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.block.RegisterBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class HbmBlockStateProvider extends BlockStateProvider {

    public HbmBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {

        super(output, HBM.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        blockWithItem(RegisterBlocks.WASTE_BLOCK);

        blockWithItem(RegisterBlocks.TITANIUM_BLOCK);

        blockWithItem(RegisterBlocks.URANIUM_BLOCK);

        blockWithItem(RegisterBlocks.BORON_BLOCK);

        blockWithItem(RegisterBlocks.TITANIUM_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_TITANIUM_ORE);

        blockWithItem(RegisterBlocks.URANIUM_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_URANIUM_ORE);

        blockWithItem(RegisterBlocks.SULFUR_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_SULFUR_ORE);

        blockWithItem(RegisterBlocks.NITER_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_NITER_ORE);

        blockWithItem(RegisterBlocks.TUNGSTEN_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_TUNGSTEN_ORE);

        blockWithItem(RegisterBlocks.ALUMINIUM_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_ALUMINIUM_ORE);

        blockWithItem(RegisterBlocks.FLUORITE_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_FLUORITE_ORE);

        blockWithItem(RegisterBlocks.BERYLLIUM_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_BERYLLIUM_ORE);

        blockWithItem(RegisterBlocks.LEAD_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_LEAD_ORE);

        blockWithItem(RegisterBlocks.LIGNITE_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_LIGNITE_ORE);

        blockWithItem(RegisterBlocks.ASBESTOS_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_ASBESTOS_ORE);

        blockWithItem(RegisterBlocks.SCHRABIDIUM_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_SCHRABIDIUM_ORE);

        blockWithItem(RegisterBlocks.AUSTRALIUM_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_AUSTRALIUM_ORE);

        blockWithItem(RegisterBlocks.RARE_EARTH_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_RARE_EARTH_ORE);

        blockWithItem(RegisterBlocks.COBALT_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_COBALT_ORE);

        blockWithItem(RegisterBlocks.CINNABAR_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_CINNABAR_ORE);

        blockWithItem(RegisterBlocks.COLTAN_ORE);
        blockWithItem(RegisterBlocks.DEEPSLATE_COLTAN_ORE);

        blockWithItem(RegisterBlocks.SULFUR_BLOCK);

        blockWithItem(RegisterBlocks.NITER_BLOCK);

        blockWithItem(RegisterBlocks.TUNGSTEN_BLOCK);
        blockWithItem(RegisterBlocks.RAW_TUNGSTEN_BLOCK);

        blockWithItem(RegisterBlocks.ALUMINIUM_BLOCK);
        blockWithItem(RegisterBlocks.RAW_ALUMINIUM_BLOCK);

        blockWithItem(RegisterBlocks.FLUORITE_BLOCK);

        blockWithItem(RegisterBlocks.BERYLLIUM_BLOCK);
        blockWithItem(RegisterBlocks.RAW_BERYLLIUM_BLOCK);

        blockWithItem(RegisterBlocks.LEAD_BLOCK);
        blockWithItem(RegisterBlocks.RAW_LEAD_BLOCK);

        blockWithItem(RegisterBlocks.LIGNITE_BLOCK);
        blockWithItem(RegisterBlocks.RAW_LIGNITE_BLOCK);

        blockWithItem(RegisterBlocks.ASBESTOS_BLOCK);

        blockWithItem(RegisterBlocks.SCHRABIDIUM_BLOCK);
        blockWithItem(RegisterBlocks.RAW_SCHRABIDIUM_BLOCK);

        blockWithItem(RegisterBlocks.AUSTRALIUM_BLOCK);
        blockWithItem(RegisterBlocks.RAW_AUSTRALIUM_BLOCK);

        blockWithItem(RegisterBlocks.COBALT_BLOCK);
        blockWithItem(RegisterBlocks.RAW_COBALT_BLOCK);

        blockWithItem(RegisterBlocks.CINNABAR_BLOCK);
        blockWithItem(RegisterBlocks.RAW_CINNABAR_BLOCK);

        blockWithItem(RegisterBlocks.COLTAN_BLOCK);
        blockWithItem(RegisterBlocks.RAW_COLTAN_BLOCK);


        blockWithItem(RegisterBlocks.BLACK_CONCRETE);
        blockWithItem(RegisterBlocks.BLUE_CONCRETE);
        blockWithItem(RegisterBlocks.BROWN_CONCRETE);
        blockWithItem(RegisterBlocks.CYAN_CONCRETE);
        blockWithItem(RegisterBlocks.GRAY_CONCRETE);
        blockWithItem(RegisterBlocks.GREEN_CONCRETE);
        blockWithItem(RegisterBlocks.LIGHT_BLUE_CONCRETE);
        blockWithItem(RegisterBlocks.LIGHT_GRAY_CONCRETE);
        blockWithItem(RegisterBlocks.LIME_CONCRETE);
        blockWithItem(RegisterBlocks.MAGENTA_CONCRETE);
        blockWithItem(RegisterBlocks.ORANGE_CONCRETE);
        blockWithItem(RegisterBlocks.PINK_CONCRETE);
        blockWithItem(RegisterBlocks.PURPLE_CONCRETE);
        blockWithItem(RegisterBlocks.RED_CONCRETE);
        blockWithItem(RegisterBlocks.WHITE_CONCRETE);
        blockWithItem(RegisterBlocks.YELLOW_CONCRETE);

        blockWithItem(RegisterBlocks.JUNGLE_BRICK);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_CIRCLE);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_CRACKED);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_FRAGILE);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_GLYPH_0);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_GLYPH_1);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_GLYPH_2);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_GLYPH_3);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_GLYPH_4);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_GLYPH_5);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_GLYPH_6);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_GLYPH_7);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_GLYPH_8);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_GLYPH_9);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_GLYPH_10);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_GLYPH_11);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_GLYPH_12);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_GLYPH_13);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_GLYPH_14);
        blockWithItem(RegisterBlocks.JUNGLE_BRICK_GLYPH_15);
        blockWithItem(RegisterBlocks.BRICK_FIRE);
        blockWithItem(RegisterBlocks.BRICK_FORGOTTEN);
        blockWithItem(RegisterBlocks.BRICK_JUNGLE_LAVA);
        blockWithItem(RegisterBlocks.BRICK_JUNGLE_MYSTIC);
        blockWithItem(RegisterBlocks.BRICK_JUNGLE_OOZE);
        blockWithItem(RegisterBlocks.BRICK_JUNGLE_TRAP);
        blockWithItem(RegisterBlocks.BRICK_LIGHT);
        blockWithItem(RegisterBlocks.BRICK_LIGHT_ALT);
        blockWithItem(RegisterBlocks.BRICK_OBSIDIAN);

        blockWithItem(RegisterBlocks.RED_THORIUM_ORE);
        blockWithItem(RegisterBlocks.ORANGE_THORIUM_ORE);
        blockWithItem(RegisterBlocks.YELLOW_THORIUM_ORE);
        blockWithItem(RegisterBlocks.WHITE_THORIUM_ORE);
        blockWithItem(RegisterBlocks.LIGHT_GRAY_THORIUM_ORE);
        blockWithItem(RegisterBlocks.BROWN_THORIUM_ORE);

        blockWithItem(RegisterBlocks.RADIATION_DECONTAMINATOR,
                ResourceLocation.tryParse("hbm:block/radiation_decontaminator_top"),
                ResourceLocation.tryParse("hbm:block/radiation_decontaminator_side"),
                ResourceLocation.tryParse("hbm:block/radiation_decontaminator_side"),
                ResourceLocation.tryParse("hbm:block/radiation_decontaminator_side"),
                ResourceLocation.tryParse("hbm:block/radiation_decontaminator_side"));

        cubeBottomTopBlockWithItem(RegisterBlocks.DEAD_GRASS,
                ResourceLocation.tryParse("hbm:block/dead_grass_top"),
                ResourceLocation.tryParse("minecraft:block/dirt"),
                ResourceLocation.tryParse("hbm:block/dead_grass_side"));

        blockWithItem(RegisterBlocks.RAD_RESISTANT_BLOCK);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {

        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
    private void blockWithItem(RegistryObject<Block> blockRegistryObject, ResourceLocation bottom, ResourceLocation side, ResourceLocation top) {

//        System.err.println(blockRegistryObject.getId().getPath());
        simpleBlockWithItem(blockRegistryObject.get(), models().cube(blockRegistryObject.getId().getPath(), bottom, top, side, side, side, side));
    }
    private void blockWithItem(RegistryObject<Block> blockRegistryObject, ResourceLocation top, ResourceLocation bottom, ResourceLocation side, ResourceLocation front, ResourceLocation particle) {
        String name = blockRegistryObject.getId().getPath();

        // Create the model using orientable_with_bottom
        ModelFile model = models()
                .withExistingParent(name, mcLoc("block/orientable_with_bottom"))
                .texture("top", top)
                .texture("bottom", bottom)
                .texture("side", side)
                .texture("front", front)
                .texture("particle", particle);

        // Register blockstate with facing direction
        horizontalBlock(blockRegistryObject.get(), model);

        // Register the item model to point to the block model
        itemModels().getBuilder(name).parent(model);
    }

    private void carpetWithItem(RegistryObject<Block> blockRegistryObject) {
        String name = blockRegistryObject.getId().getPath();

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(HBM.MOD_ID, "block/" + name);

        // Create a carpet model that uses the 'wool' texture slot the vanilla parent expects
        ModelFile model = models()
                .withExistingParent(name, mcLoc("block/carpet"))
                .texture("wool", texture);

        // register horizontal-facing variants (will rotate the model for each FACING)
        horizontalBlock(blockRegistryObject.get(), model);

        // register item model (point at the block model)
        itemModels().getBuilder(name).parent(model);
    }

    private void cubeBottomTopBlockWithItem(RegistryObject<Block> blockRegistryObject, ResourceLocation top, ResourceLocation bottom, ResourceLocation side) {
        String name = blockRegistryObject.getId().getPath();

        ModelFile model = models()
                .cubeBottomTop(name, side, bottom, top);

        simpleBlockWithItem(blockRegistryObject.get(), model);
    }
}
