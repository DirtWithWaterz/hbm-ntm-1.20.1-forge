package com.hbm.nucleartech.datagen;

import com.hbm.nucleartech.datagen.loot.HbmBlockLootTables;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public class HbmLootTableProvider {

    public static LootTableProvider create(PackOutput output) {

        return new LootTableProvider(output, Set.of(), List.of(

                new LootTableProvider.SubProviderEntry(HbmBlockLootTables::new, LootContextParamSets.BLOCK)
        ));
    }
}
