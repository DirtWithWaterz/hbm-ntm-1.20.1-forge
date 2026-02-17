package com.hbm.nucleartech.block.custom;

import com.hbm.nucleartech.hazard.HazardItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MetaHazardItem extends HazardItem {

    public MetaHazardItem(float halfLife, int explosionSize, Type type, float mass, double radiation, Properties pProperties) {
        super(type, mass, radiation, pProperties, true);

        this.module.halfLife = halfLife * 1000; // half-life should be given in seconds and then converted to milliseconds here
        this.module.uExpRadius = explosionSize;
    }

    public MetaHazardItem(float halfLife, int explosionSize, Type type, float mass, double radiation, double digamma, int fire, int cryogenic, int toxiclvl, int asbestos, int coal, boolean blinding, boolean hydroReactive, float explosive, Properties pProperties) {
        super(type, mass, radiation, digamma, fire, cryogenic, toxiclvl, asbestos, coal, blinding, hydroReactive, explosive, pProperties, true);

        this.module.halfLife = halfLife * 1000;
        this.module.uExpRadius = explosionSize;
    }

    public void initialize(ItemStack stack) {

        stack.getOrCreateTag().putLong("start_time", System.currentTimeMillis());
        stack.getOrCreateTag().putBoolean("initialized", true);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if(!this.module.isInitialized(pStack))
            initialize(pStack);
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        if(!this.module.isInitialized(pStack))
            initialize(pStack);
        super.onCraftedBy(pStack, pLevel, pPlayer);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if(!this.module.isInitialized(stack))
            initialize(stack);
        return super.onEntityItemUpdate(stack, entity);
    }
}
