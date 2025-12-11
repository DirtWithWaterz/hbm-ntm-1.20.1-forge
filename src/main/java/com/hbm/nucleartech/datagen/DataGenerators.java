package com.hbm.nucleartech.datagen;

import com.hbm.nucleartech.HBM;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = HBM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new HbmRecipeProvider(packOutput));
        generator.addProvider(event.includeServer(), HbmLootTableProvider.create(packOutput));

        generator.addProvider(event.includeClient(), new HbmBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new HbmItemModelProvider(packOutput, existingFileHelper));

        HbmBlockTagGenerator blockTagGenerator = generator.addProvider(event.includeServer(),
                new HbmBlockTagGenerator(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new HbmItemTagGenerator(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper));

        generator.addProvider(event.includeServer(),
                new HbmFluidTagGenerator(packOutput, lookupProvider, existingFileHelper));

        generator.addProvider(event.includeServer(), new HbmWorldGenProvider(packOutput, lookupProvider));

        generator.addProvider(event.includeServer(), new HbmDamageTypeProvider(packOutput));

        generator.addProvider(event.includeServer(), new HbmAdvancementProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new HbmDamageTypeTagProvider(packOutput, lookupProvider, existingFileHelper));
    }
}
