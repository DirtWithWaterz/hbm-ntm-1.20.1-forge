package com.hbm.nucleartech;

import com.hbm.nucleartech.block.RegisterBlocks;
import com.hbm.nucleartech.block.custom.ArmorModificationTableBlock;
import com.hbm.nucleartech.block.entity.RegisterBlockEntities;
import com.hbm.nucleartech.block.entity.client.ArmorModificationTableRenderer;
import com.hbm.nucleartech.block.entity.client.BurnerPressRenderer;
import com.hbm.nucleartech.block.entity.client.GraphiteBlockRenderer;
import com.hbm.nucleartech.block.entity.client.ShredderRenderer;
import com.hbm.nucleartech.entity.HbmEntities;
import com.hbm.nucleartech.entity.client.NuclearCreeperRenderer;
import com.hbm.nucleartech.fluid.RegisterFluids;
import com.hbm.nucleartech.handler.HazmatRegistry;
import com.hbm.nucleartech.handler.HbmContaminationSystem;
import com.hbm.nucleartech.item.RegisterCreativeTabs;
import com.hbm.nucleartech.item.RegisterItems;
import com.hbm.nucleartech.item.custom.GeigerCounterItem;
import com.hbm.nucleartech.network.HbmPacketHandler;
import com.hbm.nucleartech.particle.RegisterParticles;
import com.hbm.nucleartech.recipe.RegisterRecipes;
import com.hbm.nucleartech.screen.ArmorModificationTableScreen;
import com.hbm.nucleartech.screen.BurnerPressScreen;
import com.hbm.nucleartech.screen.RegisterMenuTypes;
import com.hbm.nucleartech.screen.ShredderScreen;
import com.hbm.nucleartech.sound.RegisterSounds;
import com.hbm.nucleartech.util.ArmorUtil;
import com.hbm.nucleartech.util.InterceptUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.hbm.nucleartech.block.RegisterBlocks.HAZARD_BLOCKS;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HBM.MOD_ID)
public class HBM
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "hbm";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static File configDir;
    public static File configHbmDir;

    public HBM() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        InterceptUtil.registerVanillaItemIntercepts(modEventBus);

        GeckoLib.initialize();

        RegisterCreativeTabs.register(modEventBus);
        RegisterItems.register(modEventBus);
        RegisterBlocks.register(modEventBus);
        RegisterFluids.register(modEventBus);
        RegisterParticles.register(modEventBus);
        HbmEntities.register(modEventBus);
//        ClientSetup.init(modEventBus);
        RegisterBlockEntities.register(modEventBus);
        RegisterMenuTypes.register(modEventBus);

        RegisterRecipes.register(modEventBus);

        RegisterSounds.SOUNDS.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath(MOD_ID, MOD_ID),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int messageID = 0;

    public static <T> void addNetworkMessage(
            Class<T> messageType,
            BiConsumer<T, FriendlyByteBuf> encoder,
            Function<FriendlyByteBuf, T> decoder,
            BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {

        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        messageID++;
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        event.enqueueWork(() -> {

            net.minecraft.world.item.Items.COAL = InterceptUtil.COAL_REPLACEMENT.get();
        });

        configDir = FMLPaths.CONFIGDIR.get().toFile();
        configHbmDir = new File(configDir.getAbsolutePath() + File.separatorChar + "hbmConfig");

        if(!configHbmDir.exists())
            configHbmDir.mkdir();

        HbmPacketHandler.register();
        HazmatRegistry.registerHazmats();
        ArmorUtil.register();

        GeigerCounterItem.initSoundMap();
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {


    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

        AdvancementManager.init(event.getServer());
        HbmContaminationSystem.init(event.getServer());
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            EntityRenderers.register(HbmEntities.NUCLEAR_CREEPER.get(), NuclearCreeperRenderer::new);
            BlockEntityRenderers.register(RegisterBlockEntities.BURNER_PRESS_ENTITY.get(), BurnerPressRenderer::new);
            BlockEntityRenderers.register(RegisterBlockEntities.SHREDDER_ENTITY.get(), ShredderRenderer::new);
            BlockEntityRenderers.register(RegisterBlockEntities.GRAPHITE_BLOCK_ENTITY.get(), GraphiteBlockRenderer::new);
            BlockEntityRenderers.register(RegisterBlockEntities.ARMOR_MODIFICATION_TABLE_ENTITY.get(), ArmorModificationTableRenderer::new);

            MenuScreens.register(RegisterMenuTypes.BURNER_PRESS_MENU.get(), BurnerPressScreen::new);
            MenuScreens.register(RegisterMenuTypes.SHREDDER_MENU.get(), ShredderScreen::new);
            MenuScreens.register(RegisterMenuTypes.ARMOR_MODIFICATION_TABLE_MENU.get(), ArmorModificationTableScreen::new);
        }
    }

    private static final RandomSource RANDOM = RandomSource.create();

    public static int random(int min, int max) {

        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static List<Item> getItemsFromTag(TagKey<Item> tagKey) {
        List<Item> items = new ArrayList<>();

        // ForgeRegistries.ITEMS is wrapped with holders
        for (Item holder : ForgeRegistries.ITEMS.tags().getTag(tagKey)) {
            items.add(holder);
        }

        return items;
    }
}
