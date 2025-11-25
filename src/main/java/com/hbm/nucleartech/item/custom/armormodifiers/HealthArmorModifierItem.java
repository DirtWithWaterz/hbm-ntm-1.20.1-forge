package com.hbm.nucleartech.item.custom.armormodifiers;

import com.hbm.nucleartech.item.custom.base.ArmorModifierItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HealthArmorModifierItem extends ArmorModifierItem {

    private final float healthBuff;

    public HealthArmorModifierItem(Properties pProperties, APPLICABLE applicable, float healthBuff, String name) {
        super(pProperties, applicable, "blurb." + name);

        this.healthBuff = healthBuff;
    }

    public float getHealthBuff() {

        return healthBuff;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {

        long t = System.currentTimeMillis();
        boolean pink = (t / 500) % 2 == 0; // change every 500ms
        ChatFormatting color = pink ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.RED;

        pTooltipComponents.add(Component.literal("+" + String.format("%.1f", healthBuff)).append(Component.literal(" ")).append(Component.translatable("noun.health")).withStyle(color));
        pTooltipComponents.add(Component.empty());
        pTooltipComponents.add(Component.translatable("desc.applicable").withStyle(ChatFormatting.DARK_PURPLE));
        pTooltipComponents.add(Component.literal("  ").append(Component.translatable(applicableMap.get(this.applicableTo))));
        pTooltipComponents.add(Component.translatable("desc.applicableslot").withStyle(ChatFormatting.DARK_PURPLE));
        pTooltipComponents.add(Component.literal("  ").append(Component.translatable("desc.applicableextra")));

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public Component getBlurb() {

        long t = System.currentTimeMillis();
        boolean pink = (t / 500) % 2 == 0; // change every 500ms
        ChatFormatting color = pink ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.RED;

        return Component.literal("  ").append(Component.translatable(this.blurb).withStyle(color));
    }
}
