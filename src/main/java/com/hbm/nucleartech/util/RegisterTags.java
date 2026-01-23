package com.hbm.nucleartech.util;

import com.hbm.nucleartech.HBM;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class RegisterTags {

    public static class Blocks {

        private static TagKey<Block> tag(String name) {

            return BlockTags.create(new ResourceLocation(HBM.MOD_ID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> PLATE_STAMPS = tag("plate_stamps");
        public static final TagKey<Item> WIRE_STAMPS = tag("wire_stamp");
        public static final TagKey<Item> DESH = tag("desh");
        public static final TagKey<Item> SHREDDER_BLADES = tag("shredder_blades");
        public static final TagKey<Item> PILE_RODS = tag("pile_rods");
        public static final TagKey<Item> BIOMASS = tag("biomass");

        public static final TagKey<Item> HELMET_ARMOR_MODIFIERS = tag("helmet_armor_modifiers");
        public static final TagKey<Item> CHESTPLATE_ARMOR_MODIFIERS = tag("chestplate_armor_modifiers");
        public static final TagKey<Item> LEGGINGS_ARMOR_MODIFIERS = tag("leggings_armor_modifiers");
        public static final TagKey<Item> BOOTS_ARMOR_MODIFIERS = tag("boots_armor_modifiers");
        public static final TagKey<Item> SERVO_ARMOR_MODIFIERS = tag("servo_armor_modifiers");
        public static final TagKey<Item> CLADDING_ARMOR_MODIFIERS = tag("cladding_armor_modifiers");
        public static final TagKey<Item> INSERT_ARMOR_MODIFIERS = tag("insert_armor_modifiers");
        public static final TagKey<Item> SPECIAL_ARMOR_MODIFIERS = tag("special_armor_modifiers");
        public static final TagKey<Item> BATTERY_ARMOR_MODIFIERS = tag("battery_armor_modifiers");


        private static TagKey<Item> tag(String name) {

            return ItemTags.create(new ResourceLocation(HBM.MOD_ID, name));
        }
    }
}
