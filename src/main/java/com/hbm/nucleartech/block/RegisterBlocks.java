package com.hbm.nucleartech.block;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.block.custom.*;
import com.hbm.nucleartech.fluid.RegisterFluids;
import com.hbm.nucleartech.hazard.HazardBlock;
import com.hbm.nucleartech.hazard.HazardBlockItem;
import com.hbm.nucleartech.hazard.LiquidHazardBlock;
import com.hbm.nucleartech.item.RegisterItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RegisterBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, HBM.MOD_ID);

    public static final List<RegistryObject<? extends Block>> HAZARD_BLOCKS = new ArrayList<>();

    public static final RegistryObject<Block> WASTE_BLOCK = registerHazardBlock(4500, "waste_block",
            () -> new HazardBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 4.0f),
                    4500
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> TITANIUM_BLOCK = registerBlock("titanium_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 4.0f)
            ));

    public static final RegistryObject<Block> URANIUM_BLOCK = registerHazardBlock(3.5, "uranium_block",
            () -> new HazardBlock(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)
                    .strength(5.0f, 4.0f),
                    3.5
            ));

    public static final RegistryObject<Block> BORON_BLOCK = registerBlock("boron_block",
            () -> new RadResistantBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK),
                    10.00f, 1
            ));

    public static final RegistryObject<Block> TITANIUM_ORE = registerBlock("titanium_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_ORE)
                    .strength(5.0f, 4.0f)
            ));

    public static final RegistryObject<Block> DEEPSLATE_TITANIUM_ORE = registerBlock("deepslate_titanium_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE)
                    .strength(8.0f, 3.0f)
            ));

    public static final RegistryObject<Block> URANIUM_ORE = registerBlock("uranium_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)
                    .strength(5.0f, 4.0f),
                    UniformInt.of(2, 4)
            ));

    public static final RegistryObject<Block> DEEPSLATE_URANIUM_ORE = registerBlock("deepslate_uranium_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)
                    .strength(8.0f, 3.0f),
                    UniformInt.of(2, 5)
            ));

    public static final RegistryObject<Block> RED_THORIUM_ORE = registerBlock("red_thorium_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DIAMOND_ORE)
                    .strength(3.0f, 4.0f),
                    UniformInt.of(2, 4)
            ));

    public static final RegistryObject<Block> ORANGE_THORIUM_ORE = registerBlock("orange_thorium_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DIAMOND_ORE)
                    .strength(3.0f, 4.0f),
                    UniformInt.of(2, 4)
            ));

    public static final RegistryObject<Block> YELLOW_THORIUM_ORE = registerBlock("yellow_thorium_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DIAMOND_ORE)
                    .strength(3.0f, 4.0f),
                    UniformInt.of(2, 4)
            ));

    public static final RegistryObject<Block> WHITE_THORIUM_ORE = registerBlock("white_thorium_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DIAMOND_ORE)
                    .strength(3.0f, 4.0f),
                    UniformInt.of(2, 4)
            ));

    public static final RegistryObject<Block> LIGHT_GRAY_THORIUM_ORE = registerBlock("light_gray_thorium_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DIAMOND_ORE)
                    .strength(3.0f, 4.0f),
                    UniformInt.of(2, 4)
            ));

    public static final RegistryObject<Block> BROWN_THORIUM_ORE = registerBlock("brown_thorium_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DIAMOND_ORE)
                    .strength(3.0f, 4.0f),
                    UniformInt.of(2, 4)
            ));

    public static final RegistryObject<Block> RADIATION_DECONTAMINATOR = registerBlock("radiation_decontaminator",
            () -> new DeconRadBlock(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)
                    .strength(5.0f, 4.0f)
            ));

    public static final RegistryObject<Block> SHREDDER = BLOCKS.register("shredder",
            () -> new ShredderBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));

    public static final RegistryObject<Block> ARMOR_MODIFICATION_TABLE = BLOCKS.register("armor_modification_table",
            () -> new ArmorModificationTableBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));

    public static final RegistryObject<Block> DEAD_GRASS = registerHazardBlock(4.0f, "dead_grass",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK), 4.0f).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> BURNER_PRESS = BLOCKS.register("burner_press",
            () -> new BurnerPressBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .noOcclusion()
                    .strength(5.0f, 4.0f)));

    public static final RegistryObject<Block> BURNER_PRESS_PART = BLOCKS.register("burner_press_part",
            () -> new BurnerPressPartBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .mapColor(MapColor.NONE)
                    .noOcclusion()
                    .strength(5.0f, 4.0f)
                    .noLootTable()
                    .pushReaction(PushReaction.BLOCK)
            ));

    public static final RegistryObject<Block> GRAPHITE_BLOCK = BLOCKS.register("graphite_block",
            () -> new GraphiteBlock(BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion()));

    public static final RegistryObject<Block> RAD_RESISTANT_BLOCK = registerBlock("rad_resistant_block",
            () -> new RadResistantBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 4.0f)
                    , 100.0f, 1f
            )); // has values of lead ^^ ~100% resistance (water is 79. Lead is 99.99)

    public static final RegistryObject<Block> SULFUR_ORE = registerBlock("sulfur_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)
                    .strength(3.0f, 3.0f)
            ));

    public static final RegistryObject<Block> DEEPSLATE_SULFUR_ORE = registerBlock("deepslate_sulfur_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)
                    .strength(6.0f, 3.0f)
            ));

    public static final RegistryObject<Block> NITER_ORE = registerBlock("niter_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)
                    .strength(3.0f, 3.0f)
            ));

    public static final RegistryObject<Block> DEEPSLATE_NITER_ORE = registerBlock("deepslate_niter_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)
                    .strength(6.0f, 3.0f)
            ));

    public static final RegistryObject<Block> TUNGSTEN_ORE = registerBlock("tungsten_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)
                    .strength(5.0f, 4.0f)
            ));

    public static final RegistryObject<Block> DEEPSLATE_TUNGSTEN_ORE = registerBlock("deepslate_tungsten_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)
                    .strength(8.0f, 3.0f)
            ));

    public static final RegistryObject<Block> ALUMINIUM_ORE = registerBlock("aluminum_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)
                    .strength(3.0f, 3.0f)
            ));

    public static final RegistryObject<Block> DEEPSLATE_ALUMINIUM_ORE = registerBlock("deepslate_aluminum_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)
                    .strength(6.0f, 3.0f)
            ));

    public static final RegistryObject<Block> FLUORITE_ORE = registerBlock("fluorite_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)
                    .strength(3.0f, 3.0f)
            ));

    public static final RegistryObject<Block> DEEPSLATE_FLUORITE_ORE = registerBlock("deepslate_fluorite_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)
                    .strength(6.0f, 3.0f)
            ));

    public static final RegistryObject<Block> BERYLLIUM_ORE = registerBlock("beryllium_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> DEEPSLATE_BERYLLIUM_ORE = registerBlock("deepslate_beryllium_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)
                    .strength(8.0f, 4.0f)
            ));

    public static final RegistryObject<Block> LEAD_ORE = registerBlock("lead_ore",
            () -> new RadResistantBlock(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)
                    .strength(6.0f, 4.0f),
                    80, 1
            ));

    public static final RegistryObject<Block> DEEPSLATE_LEAD_ORE = registerBlock("deepslate_lead_ore",
            () -> new RadResistantBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)
                    .strength(9.0f, 5.0f),
                    90, 1
            ));

    public static final RegistryObject<Block> COAL_AIR = registerBlock("coal_air",
            () -> new CoalAirBlock(BlockBehaviour.Properties.copy(Blocks.CAVE_AIR).randomTicks()));

    public static final RegistryObject<Block> LIGNITE_ORE = registerBlock("lignite_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.COAL_ORE),
                    UniformInt.of(1, 3)
            ));

    public static final RegistryObject<Block> DEEPSLATE_LIGNITE_ORE = registerBlock("deepslate_lignite_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_COAL_ORE),
                    UniformInt.of(2, 3)
            ));

    public static final RegistryObject<Block> ASBESTOS_AIR = registerBlock("asbestos_air",
            () -> new AsbestosAirBlock(BlockBehaviour.Properties.copy(Blocks.CAVE_AIR).randomTicks()));

    public static final RegistryObject<Block> ASBESTOS_ORE = registerBlock("asbestos_ore",
            () -> new AsbestosBlock(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));

    public static final RegistryObject<Block> DEEPSLATE_ASBESTOS_ORE = registerBlock("deepslate_asbestos_ore",
            () -> new AsbestosBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)));

    public static final RegistryObject<Block> SCHRABIDIUM_ORE = registerHazardBlock(15, "schrabidium_ore",
            () -> new HazardBlock(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)
                    .strength(5.0f, 4.0f),
                    UniformInt.of(2, 4),
                    15
            ));

    public static final RegistryObject<Block> DEEPSLATE_SCHRABIDIUM_ORE = registerHazardBlock(15, "deepslate_schrabidium_ore",
            () -> new HazardBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)
                    .strength(8.0f, 3.0f),
                    UniformInt.of(2, 5),
                    15
            ));

    public static final RegistryObject<Block> RARE_EARTH_ORE = registerBlock("rare_earth_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_ORE)
            ));

    public static final RegistryObject<Block> DEEPSLATE_RARE_EARTH_ORE = registerBlock("deepslate_rare_earth_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE)
            ));

    public static final RegistryObject<Block> COBALT_ORE = registerBlock("cobalt_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE)
            ));

    public static final RegistryObject<Block> DEEPSLATE_COBALT_ORE = registerBlock("deepslate_cobalt_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE)
                    .strength(5.5f, 3.0f),
                    UniformInt.of(4, 9)
            ));

    public static final RegistryObject<Block> CINNABAR_ORE = registerBlock("cinnabar_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.COAL_ORE)
            ));

    public static final RegistryObject<Block> DEEPSLATE_CINNABAR_ORE = registerBlock("deepslate_cinnabar_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_COAL_ORE)
            ));

    public static final RegistryObject<Block> COLTAN_ORE = registerBlock("coltan_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)
            ));

    public static final RegistryObject<Block> DEEPSLATE_COLTAN_ORE = registerBlock("deepslate_coltan_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)
            ));

    public static final RegistryObject<Block> SULFUR_BLOCK = registerBlock("sulfur_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
            ));

    public static final RegistryObject<Block> NITER_BLOCK = registerBlock("niter_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
            ));

    public static final RegistryObject<Block> TUNGSTEN_BLOCK = registerBlock("tungsten_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK)
            ));

    public static final RegistryObject<Block> RAW_TUNGSTEN_BLOCK = registerBlock("raw_tungsten_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ANCIENT_DEBRIS)
            ));

    public static final RegistryObject<Block> ALUMINIUM_BLOCK = registerBlock("aluminum_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
            ));

    public static final RegistryObject<Block> RAW_ALUMINIUM_BLOCK = registerBlock("raw_aluminum_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.RAW_IRON_BLOCK)
            ));

    public static final RegistryObject<Block> FLUORITE_BLOCK = registerBlock("fluorite_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK)
            ));

    public static final RegistryObject<Block> BERYLLIUM_BLOCK = registerBlock("beryllium_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
            ));

    public static final RegistryObject<Block> RAW_BERYLLIUM_BLOCK = registerBlock("raw_beryllium_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.RAW_IRON_BLOCK)
            ));

    public static final RegistryObject<Block> LEAD_BLOCK = registerBlock("lead_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
            ));

    public static final RegistryObject<Block> RAW_LEAD_BLOCK = registerBlock("raw_lead_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(8.0f, 3.0f)
            ));

    public static final RegistryObject<Block> LIGNITE_BLOCK = registerBlock("lignite_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.COAL_BLOCK)
            ));

    public static final RegistryObject<Block> ASBESTOS_BLOCK = registerAsbestosBlock(2, "asbestos_block",
            () -> new AsbestosBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(3.0f, 2.0f)
            ));

    public static final RegistryObject<Block> SCHRABIDIUM_BLOCK = registerHazardBlock(135.00, "schrabidium_block",
            () -> new HazardBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK),
                    135.00
            ));

    public static final RegistryObject<Block> RAW_SCHRABIDIUM_BLOCK = registerHazardBlock(13.50, "raw_schrabidium_block",
            () -> new HazardBlock(BlockBehaviour.Properties.copy(Blocks.RAW_IRON_BLOCK),
                    13.50
            ));

    public static final RegistryObject<Block> AUSTRALIUM_BLOCK = registerBlock("australium_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
            ));

    public static final RegistryObject<Block> COBALT_BLOCK = registerBlock("cobalt_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 4.0f)
            ));

    public static final RegistryObject<Block> RAW_COBALT_BLOCK = registerBlock("raw_cobalt_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(4.0f, 3.0f)
            ));

    public static final RegistryObject<Block> CINNABAR_BLOCK = registerBlock("cinnabar_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.COAL_BLOCK)
            ));

    public static final RegistryObject<Block> COLTAN_BLOCK = registerBlock("coltan_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
            ));

    public static final RegistryObject<Block> CYAN_CONCRETE = registerBlock("cyan_concrete",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> GRAY_CONCRETE = registerBlock("gray_concrete",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> GREEN_CONCRETE = registerBlock("green_concrete",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> LIGHT_BLUE_CONCRETE = registerBlock("light_blue_concrete",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> LIGHT_GRAY_CONCRETE = registerBlock("light_gray_concrete",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> LIME_CONCRETE = registerBlock("lime_concrete",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> MAGENTA_CONCRETE = registerBlock("magenta_concrete",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> ORANGE_CONCRETE = registerBlock("orange_concrete",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> PINK_CONCRETE = registerBlock("pink_concrete",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> PURPLE_CONCRETE = registerBlock("purple_concrete",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> RED_CONCRETE = registerBlock("red_concrete",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> WHITE_CONCRETE = registerBlock("white_concrete",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> YELLOW_CONCRETE = registerBlock("yellow_concrete",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> BLACK_CONCRETE = registerBlock("black_concrete",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> BLUE_CONCRETE = registerBlock("blue_concrete",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> BROWN_CONCRETE = registerBlock("brown_concrete",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));


    public static final RegistryObject<Block> JUNGLE_BRICKS = registerBlock("jungle_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));


    public static final RegistryObject<Block> CIRCULAR_JUNGLE_BRICKS = registerBlock("circular_jungle_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));


    public static final RegistryObject<Block> CRACKED_JUNGLE_BRICKS = registerBlock("cracked_jungle_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));


    public static final RegistryObject<Block> FRAGILE_JUNGLE_BRICKS = registerBlock("fragile_jungle_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));


    public static final RegistryObject<Block> JUNGLE_BRICKS_GLYPH_0 = registerBlock("glyphed_jungle_bricks_0",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));


    public static final RegistryObject<Block> JUNGLE_BRICKS_GLYPH_1 = registerBlock("glyphed_jungle_bricks_1",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> JUNGLE_BRICKS_GLYPH_2 = registerBlock("glyphed_jungle_bricks_2",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> JUNGLE_BRICKS_GLYPH_3 = registerBlock("glyphed_jungle_bricks_3",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> JUNGLE_BRICKS_GLYPH_4 = registerBlock("glyphed_jungle_bricks_4",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> JUNGLE_BRICKS_GLYPH_5 = registerBlock("glyphed_jungle_bricks_5",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> JUNGLE_BRICKS_GLYPH_6 = registerBlock("glyphed_jungle_bricks_6",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> JUNGLE_BRICKS_GLYPH_7 = registerBlock("glyphed_jungle_bricks_7",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> JUNGLE_BRICKS_GLYPH_8 = registerBlock("glyphed_jungle_bricks_8",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> JUNGLE_BRICKS_GLYPH_9 = registerBlock("glyphed_jungle_bricks_9",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> JUNGLE_BRICKS_GLYPH_10 = registerBlock("glyphed_jungle_bricks_10",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> JUNGLE_BRICKS_GLYPH_11 = registerBlock("glyphed_jungle_bricks_11",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> JUNGLE_BRICKS_GLYPH_12 = registerBlock("glyphed_jungle_bricks_12",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> JUNGLE_BRICKS_GLYPH_13 = registerBlock("glyphed_jungle_bricks_13",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> JUNGLE_BRICKS_GLYPH_14 = registerBlock("glyphed_jungle_bricks_14",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> JUNGLE_BRICKS_GLYPH_15 = registerBlock("glyphed_jungle_bricks_15",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> FIRE_BRICKS = registerBlock("fire_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> FORGOTTEN_BRICKS = registerBlock("forgotten_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));



    public static final RegistryObject<Block> LAVA_JUNGLE_BRICKS = registerBlock("lava_jungle_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));



    public static final RegistryObject<Block> MYSTIC_JUNGLE_BRICKS = registerBlock("mystic_jungle_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));



    public static final RegistryObject<Block> OOZING_JUNGLE_BRICKS = registerBlock("oozing_jungle_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));



    public static final RegistryObject<Block> TRAPPED_JUNGLE_BRICKS = registerBlock("trapped_jungle_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));



    public static final RegistryObject<Block> LIGHT_BRICKS = registerBlock("light_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));



    public static final RegistryObject<Block> LIGHT_BRICKS_ALT = registerBlock("light_bricks_alt",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> OBSIDIAN_BRICKS = registerBlock("obsidian_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 3.0f)
            ));

    public static final RegistryObject<Block> SLAKED_SELLAFITE = registerHazardBlock(2.5f, "slaked_sellafite",
            () -> new HazardBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(3.0f, 2.0f), 2.5f
            ));

    public static final RegistryObject<Block> SELLAFITE = registerHazardBlock(5.0f, "sellafite",
            () -> new HazardBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(3.0f, 2.0f), 5.0f
            ));

    public static final RegistryObject<Block> HOT_SELLAFITE = registerHazardBlock(10.0f, "hot_sellafite",
            () -> new HazardBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(3.0f, 2.0f), 10.0f
            ));

    public static final RegistryObject<Block> BOILING_SELLAFITE = registerHazardBlock(20.0f, "boiling_sellafite",
            () -> new HazardBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(3.0f, 2.0f), 20.0f
            ));

    public static final RegistryObject<Block> BLAZING_SELLAFITE = registerHazardBlock(40.0f, 1, "blazing_sellafite",
            () -> new HazardBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(3.0f, 2.0f), 40.0f
            ));

    public static final RegistryObject<Block> INFERNAL_SELLAFITE = registerHazardBlock(80.0f, 2, "infernal_sellafite",
            () -> new HazardBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(3.0f, 2.0f), 80.0f
            ));

    public static final RegistryObject<Block> SELLAFITE_CORIUM = registerHazardBlock(2000.0f, 5, "sellafite_corium",
            () -> new HazardBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(3.0f, 2.0f), 2000.0f
            ));

    public static final RegistryObject<Block> TRINITITE_ORE = registerHazardBlock(25.0f, "trinitite_ore",
            () -> new ContaminatedVariableFallingBlock(BlockBehaviour.Properties.copy(Blocks.SAND), 25.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> RED_TRINITITE_ORE = registerHazardBlock(25.0f, "red_trinitite_ore",
            () -> new ContaminatedVariableFallingBlock(BlockBehaviour.Properties.copy(Blocks.RED_SAND), 25.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> TRINITITE_BLOCK = registerHazardBlock(25.0f*9, "trinitite_block",
            () -> new HazardBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), 25.0f*9
            ));

    public static final RegistryObject<Block> SCORCHED_URANIUM_ORE = registerHazardBlock(0.5f, "scorched_uranium_ore",
            () -> new HazardBlock(BlockBehaviour.Properties.copy(RegisterBlocks.URANIUM_ORE.get()), 0.5f
            ));

    public static final RegistryObject<Block> CONTAMINATED_ICE = registerHazardBlock(20.0f, "contaminated_ice",
            () -> new ContaminatedIceBlock(BlockBehaviour.Properties.copy(Blocks.ICE).noOcclusion(), 20.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_DIRT = registerHazardBlock(1.0f, "contaminated_dirt",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.DIRT), 1.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_GRAVEL = registerHazardBlock(2.5f, "contaminated_gravel",
            () -> new ContaminatedVariableFallingBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL), 2.5f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_SANDSTONE = registerHazardBlock(2.5f, "contaminated_sandstone",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.SANDSTONE), 2.5f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_RED_SANDSTONE = registerHazardBlock(2.5f, "contaminated_red_sandstone",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.RED_SANDSTONE), 2.5f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_SAND = registerHazardBlock(5.0f, "contaminated_sand",
            () -> new ContaminatedVariableFallingBlock(BlockBehaviour.Properties.copy(Blocks.SAND), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_RED_SAND = registerHazardBlock(5.0f, "contaminated_red_sand",
            () -> new ContaminatedVariableFallingBlock(BlockBehaviour.Properties.copy(Blocks.RED_SAND), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_SNOW_BLOCK = registerHazardBlock(10.0f, "contaminated_snow_block",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.SNOW_BLOCK), 10.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_SNOW = registerHazardBlock(1.0f, "contaminated_snow",
            () -> new ContaminatedVariableLayerBlock(BlockBehaviour.Properties.copy(Blocks.SNOW), 1.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> FALLOUT_BLOCK = registerHazardBlock(100.0f, "fallout_block",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.SNOW_BLOCK), 100.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> FALLOUT = registerHazardBlock(20.0f, "fallout",
            () -> new ContaminatedVariableLayerBlock(BlockBehaviour.Properties.copy(Blocks.SNOW), 20.0f
            ));

    public static final RegistryObject<Block> CONTAMINATED_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_WHITE_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_white_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_LIGHT_GRAY_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_light_gray_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_GRAY_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_gray_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.GRAY_TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_BLACK_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_black_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_BROWN_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_brown_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_RED_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_red_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.RED_TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_ORANGE_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_orange_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.ORANGE_TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_YELLOW_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_yellow_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.YELLOW_TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_LIME_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_lime_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.LIME_TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_GREEN_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_green_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.GREEN_TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_CYAN_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_cyan_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.CYAN_TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_LIGHT_BLUE_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_light_blue_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_BLUE_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_blue_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.BLUE_TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_PURPLE_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_purple_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.PURPLE_TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_MAGENTA_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_magenta_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.MAGENTA_TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_PINK_TERRACOTTA = registerHazardBlock(5.0f, "contaminated_pink_terracotta",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.PINK_TERRACOTTA), 5.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_MYCELIUM = registerHazardBlock(4.0f, "contaminated_mycelium",
            () -> new ContaminatedVariableBlock(BlockBehaviour.Properties.copy(Blocks.MYCELIUM), 4.0f
            ).setDisplayEffect(HazardBlock.ExtDisplayEffect.RADFOG));

    public static final RegistryObject<Block> CONTAMINATED_WATER = BLOCKS.register("contaminated_water",
            () -> new LiquidHazardBlock(RegisterFluids.CONTAMINATED_WATER,
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.WATER)
                            .strength(100f)
                            .noCollission()
                            .noLootTable()
                            .liquid()
                            .pushReaction(PushReaction.DESTROY)
                            .sound(SoundType.EMPTY)
                            .replaceable(),
                    5.0d
            ));

    public static final RegistryObject<Block> STILL_WATER = BLOCKS.register("still_water",
            () -> new LiquidHazardBlock(RegisterFluids.STILL_WATER,
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.WATER)
                            .strength(100f)
                            .noCollission()
                            .noLootTable()
                            .liquid()
                            .pushReaction(PushReaction.DESTROY)
                            .sound(SoundType.EMPTY)
                            .replaceable(),
                    5.0d
            ));

//    ================================== lamp oil, rope, BOMB? You want it Link, I've got it. So long as YOU have enough rubies. ==================================================

    public static final RegistryObject<Block> LITTLE_BOY = registerBlock("little_boy",
            () -> new LittleBoyBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()
                    .strength(6.0f, 3.0f)
            ));

    public static final RegistryObject<Block> ANTI_PERSONNEL_MINE = registerBlock("anti_personnel_mine",
            () -> new AntiPersonnelMineBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()
                    .strength(1.0f, 1.0f)
            ));

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block) {

        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {

        return RegisterItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block>RegistryObject<T> registerHazardBlock(double radiation, String name, Supplier<T> block) {

        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerHazardBlockItem(radiation, name, toReturn);

        HAZARD_BLOCKS.add(toReturn);

        return toReturn;
    }

    private static <T extends Block>RegistryObject<T> registerHazardBlock(double radiation, int fire, String name, Supplier<T> block) {

        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerHazardBlockItem(radiation, fire, name, toReturn);

        HAZARD_BLOCKS.add(toReturn);

        return toReturn;
    }

    private static <T extends Block>RegistryObject<T> registerAsbestosBlock(int asbestos, String name, Supplier<T> block) {

        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerAsbestosBlockItem(asbestos, name, toReturn);

        HAZARD_BLOCKS.add(toReturn);

        return toReturn;
    }

    private static <T extends Block>RegistryObject<Item> registerHazardBlockItem(double radiation, String name, RegistryObject<T> block) {

        return RegisterItems.ITEMS.register(name, () -> new HazardBlockItem(radiation, block.get(), new Item.Properties()));
    }

    private static <T extends Block>RegistryObject<Item> registerHazardBlockItem(double radiation, int fire, String name, RegistryObject<T> block) {

        return RegisterItems.ITEMS.register(name, () -> new HazardBlockItem(radiation, 0, fire, block.get(), new Item.Properties()));
    }

    private static <T extends Block>RegistryObject<Item> registerAsbestosBlockItem(int asbestos, String name, RegistryObject<T> block) {

        return RegisterItems.ITEMS.register(name, () -> new HazardBlockItem(0, 0, 0, 0, 0, asbestos, block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {

        BLOCKS.register(eventBus);
    }
}
