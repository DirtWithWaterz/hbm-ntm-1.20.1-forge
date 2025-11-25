package com.hbm.nucleartech.item.custom.base;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.Map;

public class ArmorModifierItem extends Item {

    public enum APPLICABLE {

        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        ALL
    }

    public static final Map<APPLICABLE, String> applicableMap = Map.of(

            APPLICABLE.HELMET, "desc.applicableh",
            APPLICABLE.CHESTPLATE, "desc.applicablec",
            APPLICABLE.LEGGINGS, "desc.applicablel",
            APPLICABLE.BOOTS, "desc.applicableb",
            APPLICABLE.ALL, "desc.applicableall"
    );

    public final APPLICABLE applicableTo;

    protected final String blurb;

    public ArmorModifierItem(Properties pProperties, APPLICABLE applicableTo, String blurb) {

        super(pProperties);

        this.applicableTo = applicableTo;
        this.blurb = blurb;
    }

    public Component getBlurb() {

        return Component.literal("  ").append(Component.translatable(blurb));
    }
}
