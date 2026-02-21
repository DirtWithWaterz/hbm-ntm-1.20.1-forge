package com.hbm.nucleartech.item;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.block.RegisterBlocks;
import com.hbm.nucleartech.util.RegisterTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.hbm.nucleartech.HBM.getItemsFromTag;

public class RegisterCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HBM.MOD_ID);

    public static final RegistryObject<CreativeModeTab> NTM_RESOURCES_AND_PARTS = CREATIVE_TABS.register("resources_and_parts",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(RegisterItems.URANIUM_INGOT.get()))
                    .title(Component.translatable("creativetab.resources_and_parts"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(RegisterItems.TRINITITE.get());

                        output.accept(RegisterItems.TITANIUM_INGOT.get());
                        output.accept(RegisterItems.BERYLLIUM_INGOT.get());

                        output.accept(RegisterItems.URANIUM_INGOT.get());
                        output.accept(RegisterItems.URANIUM_233_INGOT.get());
                        output.accept(RegisterItems.URANIUM_235_INGOT.get());
                        output.accept(RegisterItems.URANIUM_238_INGOT.get());
                        output.accept(RegisterItems.URANIUM_238m2_INGOT.get());
                        output.accept(RegisterItems.URANIUM_FUEL_INGOT.get());

                        output.accept(RegisterItems.SULFUR.get());
                        output.accept(RegisterItems.NITER.get());
//                        output.accept(RegisterItems.STEEL_NUGGET.get());
                        output.accept(RegisterItems.STEEL_INGOT.get());
                        output.accept(RegisterItems.TUNGSTEN_INGOT.get());
                        output.accept(RegisterItems.ALUMINUM_INGOT.get());
                        output.accept(RegisterItems.FLUORITE.get());
                        output.accept(RegisterItems.LEAD_INGOT.get());
                        output.accept(RegisterItems.LIGNITE.get());
                        output.accept(RegisterItems.ASBESTOS_SHEET.get());
                        output.accept(RegisterItems.SCHRABIDIUM_INGOT.get());
                        output.accept(RegisterItems.AUSTRALIUM_INGOT.get());
                        output.accept(RegisterItems.COBALT_INGOT.get());
                        output.accept(RegisterItems.DESH_INGOT.get());
                        output.accept(RegisterItems.CINNABAR.get());
                        output.accept(RegisterItems.COLTAN.get());

                        output.accept(RegisterItems.URANIUM_POWDER.get());

                        output.accept(RegisterItems.URANIUM_NUGGET.get());
                        output.accept(RegisterItems.URANIUM_233_NUGGET.get());
                        output.accept(RegisterItems.URANIUM_235_NUGGET.get());
                        output.accept(RegisterItems.URANIUM_238_NUGGET.get());
                        output.accept(RegisterItems.URANIUM_238m2_NUGGET.get());
                        output.accept(RegisterItems.URANIUM_FUEL_NUGGET.get());

                        output.accept(RegisterItems.URANIUM_CRYSTAL.get());

                        output.accept(RegisterItems.URANIUM_BILLET.get());
                        output.accept(RegisterItems.URANIUM_233_BILLET.get());
                        output.accept(RegisterItems.URANIUM_235_BILLET.get());
                        output.accept(RegisterItems.URANIUM_238_BILLET.get());
                        output.accept(RegisterItems.URANIUM_FUEL_BILLET.get());

                        output.accept(RegisterItems.URANIUM_PILE_ROD.get());

                        output.accept(RegisterItems.RAW_TITANIUM.get());
                        output.accept(RegisterItems.RAW_URANIUM.get());
                        output.accept(RegisterItems.RAW_THORIUM.get());
                        output.accept(RegisterItems.RAW_TUNGSTEN.get());
                        output.accept(RegisterItems.RAW_ALUMINUM.get());
                        output.accept(RegisterItems.RAW_BERYLLIUM.get());
                        output.accept(RegisterItems.RAW_LEAD.get());
                        output.accept(RegisterItems.RAW_SCHRABIDIUM.get());
                        output.accept(RegisterItems.RAW_COBALT.get());

                        output.accept(RegisterItems.THORIUM_SHALE.get());
                        output.accept(RegisterItems.THORIUM_INGOT.get());
                        output.accept(RegisterItems.THORIUM_POWDER.get());

                        output.accept(RegisterItems.COPPER_POWDER.get());
                        output.accept(RegisterItems.TITANIUM_POWDER.get());
                        output.accept(RegisterItems.IRON_POWDER.get());
                        output.accept(RegisterItems.GOLD_POWDER.get());

                        output.accept(RegisterItems.GRAPHITE_INGOT.get());

                        output.accept(RegisterItems.DABLUFIUM_INGOT.get());

                        output.acceptAll(getItemsFromTag(RegisterTags.Items.SHREDDER_BLADES).stream().map(Item::getDefaultInstance).toList());

                        output.accept(RegisterItems.GEIGER_COUNTER.get());

                        output.accept(RegisterItems.M65_MASK.get());

                        output.accept(RegisterItems.GAS_MASK_FILTER_MONO.get());
                        output.accept(RegisterItems.GAS_MASK_FILTER.get());
                        output.accept(RegisterItems.GAS_MASK_FILTER_COMBO.get());
                        output.accept(RegisterItems.GAS_MASK_FILTER_RADON.get());

                        output.accept(RegisterItems.HAZMAT_HELMET.get());
                        output.accept(RegisterItems.HAZMAT_CHESTPLATE.get());
                        output.accept(RegisterItems.HAZMAT_LEGGINGS.get());
                        output.accept(RegisterItems.HAZMAT_BOOTS.get());

                        output.accept(RegisterItems.HAZMAT_HELMET_RED.get());
                        output.accept(RegisterItems.HAZMAT_CHESTPLATE_RED.get());
                        output.accept(RegisterItems.HAZMAT_LEGGINGS_RED.get());
                        output.accept(RegisterItems.HAZMAT_BOOTS_RED.get());

                        output.accept(RegisterItems.HAZMAT_HELMET_GREY.get());
                        output.accept(RegisterItems.HAZMAT_CHESTPLATE_GREY.get());
                        output.accept(RegisterItems.HAZMAT_LEGGINGS_GREY.get());
                        output.accept(RegisterItems.HAZMAT_BOOTS_GREY.get());

                        output.accept(RegisterItems.IRON_PLATE_STAMP.get());
                        output.accept(RegisterItems.STEEL_PLATE_STAMP.get());
                        output.accept(RegisterItems.STONE_PLATE_STAMP.get());
                        output.accept(RegisterItems.TITANIUM_PLATE_STAMP.get());
                        output.accept(RegisterItems.OBSIDIAN_PLATE_STAMP.get());
                        output.accept(RegisterItems.DESH_PLATE_STAMP.get());
                        output.accept(RegisterItems.SCHRABIDIUM_PLATE_STAMP.get());

                        output.accept(RegisterItems.IRON_WIRE_STAMP.get());
                        output.accept(RegisterItems.STEEL_WIRE_STAMP.get());
                        output.accept(RegisterItems.STONE_WIRE_STAMP.get());
                        output.accept(RegisterItems.TITANIUM_WIRE_STAMP.get());
                        output.accept(RegisterItems.OBSIDIAN_WIRE_STAMP.get());
                        output.accept(RegisterItems.DESH_WIRE_STAMP.get());
                        output.accept(RegisterItems.SCHRABIDIUM_WIRE_STAMP.get());

                        output.accept(RegisterItems.IRON_PLATE.get());
                        output.accept(RegisterItems.STEEL_PLATE.get());
                        output.accept(RegisterItems.COPPER_PLATE.get());
                        output.accept(RegisterItems.GOLD_PLATE.get());
                        output.accept(RegisterItems.TITANIUM_PLATE.get());
                        output.accept(RegisterItems.ALUMINUM_PLATE.get());
                        output.accept(RegisterItems.SCHRABIDIUM_PLATE.get());
                        output.accept(RegisterItems.LEAD_PLATE.get());

                        output.accept(RegisterItems.COPPER_WIRE.get());
                        output.accept(RegisterItems.GOLD_WIRE.get());
                        output.accept(RegisterItems.COPPER_COIL.get());
                        output.accept(RegisterItems.MOTOR.get());
                        output.accept(RegisterItems.ALUMINUM_WIRE.get());
                        output.accept(RegisterItems.CARBON_WIRE.get());
                        output.accept(RegisterItems.TUNGSTEN_WIRE.get());
                        output.accept(RegisterItems.SCHRABIDIUM_WIRE.get());
                        output.accept(RegisterItems.LEAD_WIRE.get());

                        output.accept(RegisterItems.DENSE_COPPER_WIRE.get());
                        output.accept(RegisterItems.DENSE_GOLD_WIRE.get());

                        output.accept(RegisterItems.BORON_PILE_ROD.get());
                        output.accept(RegisterItems.RADIUM_PILE_ROD.get());
                        output.accept(RegisterItems.PLUTONIUM_PILE_ROD.get());

                        output.accept(RegisterItems.CONTAMINATED_WATER_BUCKET.get());
                        output.accept(RegisterItems.STILL_WATER_BUCKET.get());
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> NTM_MACHINE_ITEMS_AND_FUEL = CREATIVE_TABS.register("machine_items_and_fuel",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(RegisterItems.PLACEHOLDER.get()))
                    .title(Component.translatable("creativetab.machine_items_and_fuel"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(RegisterItems.GENERIC_BATTERY_FULL.get());
                        output.accept(RegisterItems.GENERIC_BATTERY.get());
                        output.accept(RegisterItems.REDSTONE_CELL_FULL.get());
                        output.accept(RegisterItems.REDSTONE_CELL.get());
                        output.accept(RegisterItems.REDSTONE_6_CELL_FULL.get());
                        output.accept(RegisterItems.REDSTONE_6_CELL.get());
                        output.accept(RegisterItems.REDSTONE_24_CELL_FULL.get());
                        output.accept(RegisterItems.REDSTONE_24_CELL.get());
                        output.accept(RegisterItems.ADVANCED_BATTERY_FULL.get());
                        output.accept(RegisterItems.ADVANCED_BATTERY.get());
                        output.accept(RegisterItems.ADVANCED_CELL_FULL.get());
                        output.accept(RegisterItems.ADVANCED_CELL.get());
                        output.accept(RegisterItems.ADVANCED_4_CELL_FULL.get());
                        output.accept(RegisterItems.ADVANCED_4_CELL.get());
                        output.accept(RegisterItems.ADVANCED_12_CELL_FULL.get());
                        output.accept(RegisterItems.ADVANCED_12_CELL.get());
                        output.accept(RegisterItems.LITHIUM_BATTERY_FULL.get());
                        output.accept(RegisterItems.LITHIUM_BATTERY.get());
                        output.accept(RegisterItems.LITHIUM_CELL_FULL.get());
                        output.accept(RegisterItems.LITHIUM_CELL.get());
                        output.accept(RegisterItems.LITHIUM_3_CELL_FULL.get());
                        output.accept(RegisterItems.LITHIUM_3_CELL.get());
                        output.accept(RegisterItems.LITHIUM_6_CELL_FULL.get());
                        output.accept(RegisterItems.LITHIUM_6_CELL.get());
                        output.accept(RegisterItems.SCHRABIDIUM_BATTERY_FULL.get());
                        output.accept(RegisterItems.SCHRABIDIUM_BATTERY.get());
                        output.accept(RegisterItems.SCHRABIDIUM_CELL_FULL.get());
                        output.accept(RegisterItems.SCHRABIDIUM_CELL.get());
                        output.accept(RegisterItems.SCHRABIDIUM_2_CELL_FULL.get());
                        output.accept(RegisterItems.SCHRABIDIUM_2_CELL.get());
                        output.accept(RegisterItems.SCHRABIDIUM_4_CELL_FULL.get());
                        output.accept(RegisterItems.SCHRABIDIUM_4_CELL.get());
                        output.accept(RegisterItems.SPARK_BATTERY_FULL.get());
                        output.accept(RegisterItems.SPARK_BATTERY.get());
                        output.accept(RegisterItems.TRIXITE_BATTERY_FULL.get());
                        output.accept(RegisterItems.TRIXITE_BATTERY.get());
                        output.accept(RegisterItems.SPARK_6_CELL_FULL.get());
                        output.accept(RegisterItems.SPARK_6_CELL.get());
                        output.accept(RegisterItems.SPARK_CAR_BATTERY_FULL.get());
                        output.accept(RegisterItems.SPARK_CAR_BATTERY.get());
                        output.accept(RegisterItems.SPARK_100_CELL_FULL.get());
                        output.accept(RegisterItems.SPARK_100_CELL.get());
                        output.accept(RegisterItems.SPARK_1000_CELL_FULL.get());
                        output.accept(RegisterItems.SPARK_1000_CELL.get());
                        output.accept(RegisterItems.SPARK_2500_CELL_FULL.get());
                        output.accept(RegisterItems.SPARK_2500_CELL.get());
                        output.accept(RegisterItems.SPARK_10000_CELL_FULL.get());
                        output.accept(RegisterItems.SPARK_10000_CELL.get());
                        output.accept(RegisterItems.SPARK_POWER_CELL_FULL.get());
                        output.accept(RegisterItems.SPARK_POWER_CELL.get());
                        output.accept(RegisterItems.ELECTRONIUM_CUBE_FULL.get());
                        output.accept(RegisterItems.ELECTRONIUM_CUBE.get());
                        output.accept(RegisterItems.CREATIVE_BATTERY.get());
                        output.accept(RegisterItems.POTATO_BATTERY.get());
                        output.accept(RegisterItems.POTATOS_BATTERY.get());
                        output.accept(RegisterItems.URANIUM_SC_BATTERY.get());
                        output.accept(RegisterItems.TECHNETIUM_SC_BATTERY.get());
                        output.accept(RegisterItems.PLUTONIUM_SC_BATTERY.get());
                        output.accept(RegisterItems.POLONIUM_SC_BATTERY.get());
                        output.accept(RegisterItems.GOLD_SC_BATTERY.get());
                        output.accept(RegisterItems.LEAD_SC_BATTERY.get());
                        output.accept(RegisterItems.AMERICIUM_SC_BATTERY.get());
                        output.accept(RegisterItems.MAKESHIFT_ENERGY_CORE.get());
                        output.accept(RegisterItems.INFINITE_FUSION_CORE.get());

                        output.accept(RegisterItems.SCREWDRIVER.get());
                        output.accept(RegisterItems.HAND_DRILL.get());
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> NTM_BLOCKS = CREATIVE_TABS.register("ntm_blocks",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(RegisterBlocks.TITANIUM_BLOCK.get()))
                    .title(Component.translatable("creativetab.blocks"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(RegisterBlocks.WASTE_BLOCK.get());
                        output.accept(RegisterBlocks.URANIUM_BLOCK.get());
                        output.accept(RegisterBlocks.TITANIUM_BLOCK.get());

                        output.accept(RegisterBlocks.URANIUM_ORE.get());
                        output.accept(RegisterBlocks.DEEPSLATE_URANIUM_ORE.get());

                        output.accept(RegisterBlocks.TITANIUM_ORE.get());
                        output.accept(RegisterBlocks.DEEPSLATE_TITANIUM_ORE.get());
                        output.accept(RegisterBlocks.RAD_RESISTANT_BLOCK.get());

                        output.accept(RegisterBlocks.RED_THORIUM_ORE.get());
                        output.accept(RegisterBlocks.ORANGE_THORIUM_ORE.get());
                        output.accept(RegisterBlocks.YELLOW_THORIUM_ORE.get());
                        output.accept(RegisterBlocks.WHITE_THORIUM_ORE.get());
                        output.accept(RegisterBlocks.LIGHT_GRAY_THORIUM_ORE.get());
                        output.accept(RegisterBlocks.BROWN_THORIUM_ORE.get());

                        output.accept(RegisterBlocks.DEAD_GRASS.get());

                        output.accept(RegisterBlocks.SULFUR_ORE.get());
                        output.accept(RegisterBlocks.DEEPSLATE_SULFUR_ORE.get());
                        output.accept(RegisterBlocks.NITER_ORE.get());
                        output.accept(RegisterBlocks.DEEPSLATE_NITER_ORE.get());
                        output.accept(RegisterBlocks.TUNGSTEN_ORE.get());
                        output.accept(RegisterBlocks.DEEPSLATE_TUNGSTEN_ORE.get());
                        output.accept(RegisterBlocks.ALUMINIUM_ORE.get());
                        output.accept(RegisterBlocks.DEEPSLATE_ALUMINIUM_ORE.get());
                        output.accept(RegisterBlocks.FLUORITE_ORE.get());
                        output.accept(RegisterBlocks.DEEPSLATE_FLUORITE_ORE.get());
                        output.accept(RegisterBlocks.BERYLLIUM_ORE.get());
                        output.accept(RegisterBlocks.DEEPSLATE_BERYLLIUM_ORE.get());
                        output.accept(RegisterBlocks.LEAD_ORE.get());
                        output.accept(RegisterBlocks.DEEPSLATE_LEAD_ORE.get());
                        output.accept(RegisterBlocks.LIGNITE_ORE.get());
                        output.accept(RegisterBlocks.DEEPSLATE_LIGNITE_ORE.get());
                        output.accept(RegisterBlocks.ASBESTOS_ORE.get());
                        output.accept(RegisterBlocks.DEEPSLATE_ASBESTOS_ORE.get());
                        output.accept(RegisterBlocks.SCHRABIDIUM_ORE.get());
                        output.accept(RegisterBlocks.DEEPSLATE_SCHRABIDIUM_ORE.get());
                        output.accept(RegisterBlocks.RARE_EARTH_ORE.get());
                        output.accept(RegisterBlocks.DEEPSLATE_RARE_EARTH_ORE.get());
                        output.accept(RegisterBlocks.COBALT_ORE.get());
                        output.accept(RegisterBlocks.DEEPSLATE_COBALT_ORE.get());
                        output.accept(RegisterBlocks.CINNABAR_ORE.get());
                        output.accept(RegisterBlocks.DEEPSLATE_CINNABAR_ORE.get());
                        output.accept(RegisterBlocks.COLTAN_ORE.get());
                        output.accept(RegisterBlocks.DEEPSLATE_COLTAN_ORE.get());

                        output.accept(RegisterBlocks.SULFUR_BLOCK.get());
                        output.accept(RegisterBlocks.NITER_BLOCK.get());
                        output.accept(RegisterBlocks.TUNGSTEN_BLOCK.get());
                        output.accept(RegisterBlocks.RAW_TUNGSTEN_BLOCK.get());
                        output.accept(RegisterBlocks.ALUMINIUM_BLOCK.get());
                        output.accept(RegisterBlocks.RAW_ALUMINIUM_BLOCK.get());
                        output.accept(RegisterBlocks.FLUORITE_BLOCK.get());
                        output.accept(RegisterBlocks.BERYLLIUM_BLOCK.get());
                        output.accept(RegisterBlocks.RAW_BERYLLIUM_BLOCK.get());
                        output.accept(RegisterBlocks.LEAD_BLOCK.get());
                        output.accept(RegisterBlocks.RAW_LEAD_BLOCK.get());
                        output.accept(RegisterBlocks.LIGNITE_BLOCK.get());
                        output.accept(RegisterBlocks.ASBESTOS_BLOCK.get());
                        output.accept(RegisterBlocks.SCHRABIDIUM_BLOCK.get());
                        output.accept(RegisterBlocks.RAW_SCHRABIDIUM_BLOCK.get());
                        output.accept(RegisterBlocks.AUSTRALIUM_BLOCK.get());
                        output.accept(RegisterBlocks.COBALT_BLOCK.get());
                        output.accept(RegisterBlocks.RAW_COBALT_BLOCK.get());
                        output.accept(RegisterBlocks.CINNABAR_BLOCK.get());
                        output.accept(RegisterBlocks.COLTAN_BLOCK.get());

                        output.accept(RegisterBlocks.BLACK_CONCRETE.get());
                        output.accept(RegisterBlocks.BLUE_CONCRETE.get());
                        output.accept(RegisterBlocks.BROWN_CONCRETE.get());
                        output.accept(RegisterBlocks.CYAN_CONCRETE.get());
                        output.accept(RegisterBlocks.GRAY_CONCRETE.get());
                        output.accept(RegisterBlocks.GREEN_CONCRETE.get());
                        output.accept(RegisterBlocks.LIGHT_BLUE_CONCRETE.get());
                        output.accept(RegisterBlocks.LIGHT_GRAY_CONCRETE.get());
                        output.accept(RegisterBlocks.LIME_CONCRETE.get());
                        output.accept(RegisterBlocks.MAGENTA_CONCRETE.get());
                        output.accept(RegisterBlocks.ORANGE_CONCRETE.get());
                        output.accept(RegisterBlocks.PINK_CONCRETE.get());
                        output.accept(RegisterBlocks.PURPLE_CONCRETE.get());
                        output.accept(RegisterBlocks.RED_CONCRETE.get());
                        output.accept(RegisterBlocks.WHITE_CONCRETE.get());
                        output.accept(RegisterBlocks.YELLOW_CONCRETE.get());

                        output.accept(RegisterBlocks.JUNGLE_BRICKS.get());
                        output.accept(RegisterBlocks.CIRCULAR_JUNGLE_BRICKS.get());
                        output.accept(RegisterBlocks.CRACKED_JUNGLE_BRICKS.get());
                        output.accept(RegisterBlocks.FRAGILE_JUNGLE_BRICKS.get());
                        output.accept(RegisterBlocks.JUNGLE_BRICKS_GLYPH_0.get());
                        output.accept(RegisterBlocks.JUNGLE_BRICKS_GLYPH_1.get());
                        output.accept(RegisterBlocks.JUNGLE_BRICKS_GLYPH_2.get());
                        output.accept(RegisterBlocks.JUNGLE_BRICKS_GLYPH_3.get());
                        output.accept(RegisterBlocks.JUNGLE_BRICKS_GLYPH_4.get());
                        output.accept(RegisterBlocks.JUNGLE_BRICKS_GLYPH_5.get());
                        output.accept(RegisterBlocks.JUNGLE_BRICKS_GLYPH_6.get());
                        output.accept(RegisterBlocks.JUNGLE_BRICKS_GLYPH_7.get());
                        output.accept(RegisterBlocks.JUNGLE_BRICKS_GLYPH_8.get());
                        output.accept(RegisterBlocks.JUNGLE_BRICKS_GLYPH_9.get());
                        output.accept(RegisterBlocks.JUNGLE_BRICKS_GLYPH_10.get());
                        output.accept(RegisterBlocks.JUNGLE_BRICKS_GLYPH_11.get());
                        output.accept(RegisterBlocks.JUNGLE_BRICKS_GLYPH_12.get());
                        output.accept(RegisterBlocks.JUNGLE_BRICKS_GLYPH_13.get());
                        output.accept(RegisterBlocks.JUNGLE_BRICKS_GLYPH_14.get());
                        output.accept(RegisterBlocks.JUNGLE_BRICKS_GLYPH_15.get());
                        output.accept(RegisterBlocks.FIRE_BRICKS.get());
                        output.accept(RegisterBlocks.FORGOTTEN_BRICKS.get());
                        output.accept(RegisterBlocks.LAVA_JUNGLE_BRICKS.get());
                        output.accept(RegisterBlocks.MYSTIC_JUNGLE_BRICKS.get());
                        output.accept(RegisterBlocks.OOZING_JUNGLE_BRICKS.get());
                        output.accept(RegisterBlocks.TRAPPED_JUNGLE_BRICKS.get());
                        output.accept(RegisterBlocks.LIGHT_BRICKS.get());
                        output.accept(RegisterBlocks.LIGHT_BRICKS_ALT.get());
                        output.accept(RegisterBlocks.OBSIDIAN_BRICKS.get());

                        output.accept(RegisterBlocks.GRAPHITE_BLOCK.get());
                        output.accept(RegisterBlocks.BORON_BLOCK.get());

                        output.accept(RegisterBlocks.SLAKED_SELLAFITE.get());
                        output.accept(RegisterBlocks.SELLAFITE.get());
                        output.accept(RegisterBlocks.HOT_SELLAFITE.get());
                        output.accept(RegisterBlocks.BOILING_SELLAFITE.get());
                        output.accept(RegisterBlocks.BLAZING_SELLAFITE.get());
                        output.accept(RegisterBlocks.INFERNAL_SELLAFITE.get());
                        output.accept(RegisterBlocks.SELLAFITE_CORIUM.get());

                        output.accept(RegisterBlocks.TRINITITE_ORE.get());
                        output.accept(RegisterBlocks.RED_TRINITITE_ORE.get());

                        output.accept(RegisterBlocks.TRINITITE_BLOCK.get());

                        output.accept(RegisterBlocks.SCORCHED_URANIUM_ORE.get());

                        output.accept(RegisterBlocks.CONTAMINATED_DIRT.get());
                        output.accept(RegisterBlocks.CONTAMINATED_GRAVEL.get());
                        output.accept(RegisterBlocks.CONTAMINATED_SANDSTONE.get());
                        output.accept(RegisterBlocks.CONTAMINATED_SAND.get());
                        output.accept(RegisterBlocks.CONTAMINATED_RED_SANDSTONE.get());
                        output.accept(RegisterBlocks.CONTAMINATED_RED_SAND.get());
                        output.accept(RegisterBlocks.CONTAMINATED_SNOW_BLOCK.get());
                        output.accept(RegisterBlocks.CONTAMINATED_SNOW.get());
                        output.accept(RegisterBlocks.CONTAMINATED_ICE.get());

                        output.accept(RegisterBlocks.CONTAMINATED_TERRACOTTA.get());
                        output.accept(RegisterBlocks.CONTAMINATED_WHITE_TERRACOTTA.get());
                        output.accept(RegisterBlocks.CONTAMINATED_LIGHT_GRAY_TERRACOTTA.get());
                        output.accept(RegisterBlocks.CONTAMINATED_GRAY_TERRACOTTA.get());
                        output.accept(RegisterBlocks.CONTAMINATED_BLACK_TERRACOTTA.get());
                        output.accept(RegisterBlocks.CONTAMINATED_BROWN_TERRACOTTA.get());
                        output.accept(RegisterBlocks.CONTAMINATED_RED_TERRACOTTA.get());
                        output.accept(RegisterBlocks.CONTAMINATED_ORANGE_TERRACOTTA.get());
                        output.accept(RegisterBlocks.CONTAMINATED_YELLOW_TERRACOTTA.get());
                        output.accept(RegisterBlocks.CONTAMINATED_LIME_TERRACOTTA.get());
                        output.accept(RegisterBlocks.CONTAMINATED_GREEN_TERRACOTTA.get());
                        output.accept(RegisterBlocks.CONTAMINATED_CYAN_TERRACOTTA.get());
                        output.accept(RegisterBlocks.CONTAMINATED_LIGHT_BLUE_TERRACOTTA.get());
                        output.accept(RegisterBlocks.CONTAMINATED_BLUE_TERRACOTTA.get());
                        output.accept(RegisterBlocks.CONTAMINATED_PURPLE_TERRACOTTA.get());
                        output.accept(RegisterBlocks.CONTAMINATED_MAGENTA_TERRACOTTA.get());
                        output.accept(RegisterBlocks.CONTAMINATED_PINK_TERRACOTTA.get());

                        output.accept(RegisterBlocks.CONTAMINATED_MYCELIUM.get());
                        output.accept(RegisterBlocks.FALLOUT.get());
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> NTM_MACHINES = CREATIVE_TABS.register("ntm_machines",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(RegisterBlocks.RADIATION_DECONTAMINATOR.get()))
                    .title(Component.translatable("creativetab.machines"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(RegisterBlocks.RADIATION_DECONTAMINATOR.get());
                        output.accept(RegisterItems.BURNER_PRESS.get());
                        output.accept(RegisterItems.SHREDDER.get());
                        output.accept(RegisterItems.ARMOR_MODIFICATION_TABLE.get());

                        output.accept(RegisterBlocks.LITTLE_BOY.get());
                        output.accept(RegisterBlocks.ANTI_PERSONNEL_MINE.get());
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> NTM_CONSUMABLES_AND_GEAR = CREATIVE_TABS.register("ntm_consumables_and_gear",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(RegisterItems.PLACEHOLDER.get()))
                    .title(Component.translatable("creativetab.consumables_and_gear"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(RegisterItems.HEART_PIECE.get());
                        output.accept(RegisterItems.HEART_CONTAINER.get());
                        output.accept(RegisterItems.FAB_HEART.get());
                        output.accept(RegisterItems.HEART_BOOSTER.get());
                        output.accept(RegisterItems.HEART_OF_DARKNESS.get());
                        output.accept(RegisterItems.BLACK_DIAMOND.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {

        CREATIVE_TABS.register(eventBus);
    }
}
