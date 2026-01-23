package com.hbm.nucleartech.datagen;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.block.RegisterBlocks;
import com.hbm.nucleartech.block.custom.ContaminatedVariableBlock;
import com.hbm.nucleartech.block.custom.ContaminatedVariableLayerBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

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

        blockWithItem(RegisterBlocks.ASBESTOS_BLOCK);

        blockWithItem(RegisterBlocks.SCHRABIDIUM_BLOCK);
        blockWithItem(RegisterBlocks.RAW_SCHRABIDIUM_BLOCK);

        blockWithItem(RegisterBlocks.AUSTRALIUM_BLOCK);

        blockWithItem(RegisterBlocks.COBALT_BLOCK);
        blockWithItem(RegisterBlocks.RAW_COBALT_BLOCK);

        blockWithItem(RegisterBlocks.CINNABAR_BLOCK);

        blockWithItem(RegisterBlocks.COLTAN_BLOCK);


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

        blockWithItem(RegisterBlocks.JUNGLE_BRICKS);
        blockWithItem(RegisterBlocks.CIRCULAR_JUNGLE_BRICKS);
        blockWithItem(RegisterBlocks.CRACKED_JUNGLE_BRICKS);
        blockWithItem(RegisterBlocks.FRAGILE_JUNGLE_BRICKS);
        blockWithItem(RegisterBlocks.JUNGLE_BRICKS_GLYPH_0);
        blockWithItem(RegisterBlocks.JUNGLE_BRICKS_GLYPH_1);
        blockWithItem(RegisterBlocks.JUNGLE_BRICKS_GLYPH_2);
        blockWithItem(RegisterBlocks.JUNGLE_BRICKS_GLYPH_3);
        blockWithItem(RegisterBlocks.JUNGLE_BRICKS_GLYPH_4);
        blockWithItem(RegisterBlocks.JUNGLE_BRICKS_GLYPH_5);
        blockWithItem(RegisterBlocks.JUNGLE_BRICKS_GLYPH_6);
        blockWithItem(RegisterBlocks.JUNGLE_BRICKS_GLYPH_7);
        blockWithItem(RegisterBlocks.JUNGLE_BRICKS_GLYPH_8);
        blockWithItem(RegisterBlocks.JUNGLE_BRICKS_GLYPH_9);
        blockWithItem(RegisterBlocks.JUNGLE_BRICKS_GLYPH_10);
        blockWithItem(RegisterBlocks.JUNGLE_BRICKS_GLYPH_11);
        blockWithItem(RegisterBlocks.JUNGLE_BRICKS_GLYPH_12);
        blockWithItem(RegisterBlocks.JUNGLE_BRICKS_GLYPH_13);
        blockWithItem(RegisterBlocks.JUNGLE_BRICKS_GLYPH_14);
        blockWithItem(RegisterBlocks.JUNGLE_BRICKS_GLYPH_15);
        blockWithItem(RegisterBlocks.FIRE_BRICKS);
        blockWithItem(RegisterBlocks.FORGOTTEN_BRICKS);
        blockWithItem(RegisterBlocks.LAVA_JUNGLE_BRICKS);
        blockWithItem(RegisterBlocks.MYSTIC_JUNGLE_BRICKS);
        blockWithItem(RegisterBlocks.OOZING_JUNGLE_BRICKS);
        blockWithItem(RegisterBlocks.TRAPPED_JUNGLE_BRICKS);
        blockWithItem(RegisterBlocks.LIGHT_BRICKS);
        blockWithItem(RegisterBlocks.LIGHT_BRICKS_ALT);
        blockWithItem(RegisterBlocks.OBSIDIAN_BRICKS);

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

        variantDeadGrassBlockWithItem(RegisterBlocks.DEAD_GRASS, 8);

        blockWithItem(RegisterBlocks.RAD_RESISTANT_BLOCK);

        randomCubeBlockWithItem(RegisterBlocks.SLAKED_SELLAFITE, 4);
        randomCubeBlockWithItem(RegisterBlocks.SELLAFITE, 4);
        randomCubeBlockWithItem(RegisterBlocks.HOT_SELLAFITE, 4);
        randomCubeBlockWithItem(RegisterBlocks.BOILING_SELLAFITE, 4);
        randomCubeBlockWithItem(RegisterBlocks.BLAZING_SELLAFITE, 4);
        randomCubeBlockWithItem(RegisterBlocks.INFERNAL_SELLAFITE, 4);
        randomCubeBlockWithItem(RegisterBlocks.SELLAFITE_CORIUM, 4);

        variantCubeBlockWithItem(RegisterBlocks.TRINITITE_ORE, 8);
        variantCubeBlockWithItem(RegisterBlocks.RED_TRINITITE_ORE, 8);

        blockWithItem(RegisterBlocks.TRINITITE_BLOCK);

        blockWithItem(RegisterBlocks.SCORCHED_URANIUM_ORE);

        blockWithItem(RegisterBlocks.CONTAMINATED_ICE);

        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_DIRT, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_GRAVEL, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_SAND, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_RED_SAND, 8);

        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_TERRACOTTA, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_WHITE_TERRACOTTA, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_LIGHT_GRAY_TERRACOTTA, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_GRAY_TERRACOTTA, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_BLACK_TERRACOTTA, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_BROWN_TERRACOTTA, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_RED_TERRACOTTA, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_ORANGE_TERRACOTTA, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_YELLOW_TERRACOTTA, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_LIME_TERRACOTTA, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_GREEN_TERRACOTTA, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_CYAN_TERRACOTTA, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_LIGHT_BLUE_TERRACOTTA, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_BLUE_TERRACOTTA, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_PURPLE_TERRACOTTA, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_MAGENTA_TERRACOTTA, 8);
        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_PINK_TERRACOTTA, 8);

        variantDeadGrassBlockWithItem(RegisterBlocks.CONTAMINATED_MYCELIUM, 8);

        variantCubeBlockWithItem(RegisterBlocks.CONTAMINATED_SNOW_BLOCK, 8);
        variantLayerBlockWithItem(RegisterBlocks.CONTAMINATED_SNOW, 8, RegisterBlocks.CONTAMINATED_SNOW_BLOCK);

        variantCubeBlockWithItem(RegisterBlocks.FALLOUT_BLOCK, 8);
        variantLayerBlockWithItem(RegisterBlocks.FALLOUT, 8, RegisterBlocks.FALLOUT_BLOCK);

        variantCubeBottomTopBlockWithItem(RegisterBlocks.CONTAMINATED_SANDSTONE, 8, Blocks.SANDSTONE);
        variantCubeBottomTopBlockWithItem(RegisterBlocks.CONTAMINATED_RED_SANDSTONE, 8, Blocks.RED_SANDSTONE);
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

    private void randomCubeBlockWithItem(RegistryObject<Block> block, int variants) {

        String name = block.getId().getPath();

        // Build model files: sellafite_0, sellafite_1, etc.
        ModelFile[] models = new ModelFile[variants];

        for (int i = 0; i < variants; i++) {
            models[i] = models()
                    .cubeAll(name + "_" + i,
                            modLoc("block/" + name + "_" + i));
        }

        // Create a blockstate with "" = random variant selector
        VariantBlockStateBuilder builder = getVariantBuilder(block.get());

        for (ModelFile model : models) {
            builder.partialState().addModels(new ConfiguredModel(model));
        }

        // Normal item model → point at variant 0 (vanilla standard behavior)
        itemModels()
                .getBuilder(name)
                .parent(models[0]);
    }

    private void variantCubeBlockWithItem(RegistryObject<Block> blockRegistryObject, int variants) {
        String name = blockRegistryObject.getId().getPath();

        // create models: name_0, name_1, ...
        ModelFile[] models = new ModelFile[variants];

        models[0] = models().cubeAll(name, modLoc("block/" + name));

        for (int i = 1; i < variants; i++) {
            models[i] = models().cubeAll(name + "_" + (i - 1), modLoc("block/" + name + "_" + (i - 1)));
        }

        // produce blockstate: variant=0 -> model name_0, variant=1 -> name_1, ...
        VariantBlockStateBuilder builder = getVariantBuilder(blockRegistryObject.get());

        for (int i = 0; i < variants; i++) {
            builder.partialState().with(ContaminatedVariableBlock.VARIANT, i)
                    .addModels(new ConfiguredModel(models[i]));
        }

        // item model points to the variant 0 model (common default)
        itemModels().getBuilder(name).parent(models[0]);
    }

    private void variantLayerBlockWithItem(RegistryObject<Block> blockRegistryObject, int variants, @Nullable RegistryObject<Block> otherBlock) {
        String name = blockRegistryObject.getId().getPath();

        String texName = otherBlock != null ? otherBlock.getId().getPath() : name;

        // create models: name_0, name_1, ...
        ModelFile[] models = new ModelFile[variants*8];

        for (int i = 1; i <= 8; i++) {

            String modelName = name + "_" + 0 + "_" + i;

            // snow_height2 .. snow_block
            models[i-1] = models()
                    .withExistingParent(modelName, mcLoc(i < 8 ? "block/snow_height" + (i*2) : "block/cube_all"))
                    .texture(i < 8 ? "texture" : "all",
                            modLoc("block/" + texName));
        }

        for (int i = 1; i < variants; i++) {
            for (int j = 1; j <= 8; j++) {

                String modelName = name + "_" + i + "_" + j;

                // snow_height2 .. snow_block
                models[j+((variants-1)*i)+(i-1)] = models()
                        .withExistingParent(modelName, mcLoc(j < 8 ? "block/snow_height" + (j*2) : "block/cube_all"))
                        .texture(j < 8 ? "texture" : "all",
                                modLoc("block/" + texName + "_" + (i - 1)));
            }
        }

        // produce blockstate: variant=0 -> model name_0, variant=1 -> name_1, ...
        VariantBlockStateBuilder builder = getVariantBuilder(blockRegistryObject.get());

        for (int i = 0; i < variants; i++) {
            for(int j = 1; j <= 8; j++) {

                builder.partialState()
                        .with(ContaminatedVariableLayerBlock.VARIANT, i)
                        .with(ContaminatedVariableLayerBlock.LAYERS, j)
                        .addModels(new ConfiguredModel(models[j+((variants-1)*i)+(i-1)]));
            }
        }

        // item model points to the variant 0 model (common default)
        itemModels().getBuilder(name).parent(models[0]);
    }

    private void variantCubeBottomTopBlockWithItem(RegistryObject<Block> blockRegistryObject, int variants, @Nullable Block copyVanilla) {
        String name = blockRegistryObject.getId().getPath();

        // create models: name_0, name_1, ...
        ModelFile[] models = new ModelFile[variants];

        if(copyVanilla != null) {

            String vN = copyVanilla.getName().getString().toLowerCase().replaceAll(" ", "_");

            models[0] = models().withExistingParent(name, mcLoc("block/" + vN));
        }
        else
            models[0] = models().cubeBottomTop(name, modLoc("block/" + name + "_side"), modLoc("block/" + name + "_bottom"), modLoc("block/" + name + "_top"));

        for (int i = 1; i < variants; i++) {
            models[i] = models().cubeBottomTop(name + "_" + (i - 1), modLoc("block/" + name + "_side" + "_" + (i - 1)), modLoc("block/" + name + "_bottom" + "_" + (i - 1)), modLoc("block/" + name + "_top" + "_" + (i - 1)));
        }

        // produce blockstate: variant=0 -> model name_0, variant=1 -> name_1, ...
        VariantBlockStateBuilder builder = getVariantBuilder(blockRegistryObject.get());

        for (int i = 0; i < variants; i++) {
            builder.partialState().with(ContaminatedVariableBlock.VARIANT, i)
                    .addModels(new ConfiguredModel(models[i]));
        }

        // item model points to the variant 0 model (common default)
        itemModels().getBuilder(name).parent(models[0]);
    }

    private void variantDeadGrassBlockWithItem(RegistryObject<Block> blockRegistryObject, int variants) {
        String name = blockRegistryObject.getId().getPath();

        // create models: name_0, name_1, ...
        ModelFile[] models = new ModelFile[variants];

        models[0] = models().cubeBottomTop(name, modLoc("block/" + name + "_side"), mcLoc("block/dirt"), modLoc("block/" + name + "_top"));

        for (int i = 1; i < variants; i++) {
            models[i] = models().cubeBottomTop(name + "_" + (i - 1), modLoc("block/" + name + "_side" + "_" + (i - 1)), modLoc("block/contaminated_dirt_" + (i - 1)), modLoc("block/" + name + "_top" + "_" + (i - 1)));
        }

        // produce blockstate: variant=0 -> model name_0, variant=1 -> name_1, ...
        VariantBlockStateBuilder builder = getVariantBuilder(blockRegistryObject.get());

        for (int i = 0; i < variants; i++) {
            builder.partialState().with(ContaminatedVariableBlock.VARIANT, i)
                    .addModels(new ConfiguredModel(models[i]));
        }

        // item model points to the variant 0 model (common default)
        itemModels().getBuilder(name).parent(models[0]);
    }

    private void carpetWithItem(RegistryObject<Block> blockRegistryObject) {
        String name = blockRegistryObject.getId().getPath();

        ResourceLocation texture = new ResourceLocation(HBM.MOD_ID, "block/" + name);

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
