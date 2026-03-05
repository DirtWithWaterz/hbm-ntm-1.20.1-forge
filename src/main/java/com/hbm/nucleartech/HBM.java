package com.hbm.nucleartech;

import com.hbm.nucleartech.block.RegisterBlocks;
import com.hbm.nucleartech.block.entity.RegisterBlockEntities;
import com.hbm.nucleartech.block.entity.client.*;
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
import com.hbm.nucleartech.screen.*;
import com.hbm.nucleartech.sound.RegisterSounds;
import com.hbm.nucleartech.util.ArmorUtil;
import com.hbm.nucleartech.util.InterceptUtil;
import com.mojang.logging.LogUtils;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.io.File;
import java.util.*;
import java.util.function.*;

/**
 * Main mod entrypoint.
 */
@Mod(HBM.MOD_ID)
public class HBM {

    /* ------------------------------------------------------------ */
    /* Constants                                                     */
    /* ------------------------------------------------------------ */

    public static final String MOD_ID = "hbm";
    private static final String PROTOCOL_VERSION = "1";

    public static final Logger LOGGER = LogUtils.getLogger();
    private static final RandomSource RANDOM = RandomSource.create();

    /* ------------------------------------------------------------ */
    /* Network                                                       */
    /* ------------------------------------------------------------ */

    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MOD_ID, MOD_ID),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int messageID = 0;

    /* ------------------------------------------------------------ */
    /* Config                                                        */
    /* ------------------------------------------------------------ */

    public static File configDir;
    public static File configHbmDir;

    /* ------------------------------------------------------------ */
    /* Constructor                                                   */
    /* ------------------------------------------------------------ */

    public HBM() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        initializeLibraries();
        registerContent(modEventBus);
        registerEvents(modEventBus);
        registerConfig();
    }

    /* ------------------------------------------------------------ */
    /* Initialization                                                */
    /* ------------------------------------------------------------ */

    private void initializeLibraries() {
        GeckoLib.initialize();
    }

    private void registerContent(IEventBus modEventBus) {

        InterceptUtil.registerVanillaItemIntercepts(modEventBus);

        RegisterCreativeTabs.register(modEventBus);
        RegisterItems.register(modEventBus);
        RegisterBlocks.register(modEventBus);
        RegisterFluids.register(modEventBus);
        RegisterParticles.register(modEventBus);
        HbmEntities.register(modEventBus);
        RegisterBlockEntities.register(modEventBus);
        RegisterMenuTypes.register(modEventBus);
        RegisterRecipes.register(modEventBus);

        RegisterSounds.SOUNDS.register(modEventBus);
    }

    private void registerEvents(IEventBus modEventBus) {

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    /* ------------------------------------------------------------ */
    /* Networking                                                    */
    /* ------------------------------------------------------------ */

    public static <T> void addNetworkMessage(
            Class<T> messageType,
            BiConsumer<T, FriendlyByteBuf> encoder,
            Function<FriendlyByteBuf, T> decoder,
            BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer
    ) {
        PACKET_HANDLER.registerMessage(messageID++, messageType, encoder, decoder, messageConsumer);
    }

    /* ------------------------------------------------------------ */
    /* Setup                                                         */
    /* ------------------------------------------------------------ */

    private void commonSetup(final FMLCommonSetupEvent event) {

        event.enqueueWork(() ->
                net.minecraft.world.item.Items.COAL = InterceptUtil.COAL_REPLACEMENT.get()
        );

        initializeConfigDirectory();

        HbmPacketHandler.register();
        HazmatRegistry.registerHazmats();
        ArmorUtil.register();
        GeigerCounterItem.initSoundMap();
    }

    private static void initializeConfigDirectory() {

        configDir = FMLPaths.CONFIGDIR.get().toFile();
        configHbmDir = new File(configDir, "hbmConfig");

        if (!configHbmDir.exists()) {
            configHbmDir.mkdirs();
        }
    }

    /* ------------------------------------------------------------ */
    /* Events                                                        */
    /* ------------------------------------------------------------ */

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Intentionally empty
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

        AdvancementManager.init(event.getServer());
        HbmContaminationSystem.init(event.getServer());
    }

    /* ------------------------------------------------------------ */
    /* Client                                                        */
    /* ------------------------------------------------------------ */

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            EntityRenderers.register(
                    HbmEntities.NUCLEAR_CREEPER.get(),
                    NuclearCreeperRenderer::new
            );

            BlockEntityRenderers.register(
                    RegisterBlockEntities.BURNER_PRESS_ENTITY.get(),
                    BurnerPressRenderer::new
            );

            BlockEntityRenderers.register(
                    RegisterBlockEntities.SHREDDER_ENTITY.get(),
                    ShredderRenderer::new
            );

            BlockEntityRenderers.register(
                    RegisterBlockEntities.GRAPHITE_BLOCK_ENTITY.get(),
                    GraphiteBlockRenderer::new
            );

            BlockEntityRenderers.register(
                    RegisterBlockEntities.ARMOR_MODIFICATION_TABLE_ENTITY.get(),
                    ArmorModificationTableRenderer::new
            );

            MenuScreens.register(
                    RegisterMenuTypes.BURNER_PRESS_MENU.get(),
                    BurnerPressScreen::new
            );

            MenuScreens.register(
                    RegisterMenuTypes.SHREDDER_MENU.get(),
                    ShredderScreen::new
            );

            MenuScreens.register(
                    RegisterMenuTypes.ARMOR_MODIFICATION_TABLE_MENU.get(),
                    ArmorModificationTableScreen::new
            );
        }
    }

    /* ------------------------------------------------------------ */
    /* Utilities                                                     */
    /* ------------------------------------------------------------ */

    public static int random(int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static List<Item> getItemsFromTag(TagKey<Item> tagKey) {

        List<Item> items = new ArrayList<>();

        for (Item item : ForgeRegistries.ITEMS.tags().getTag(tagKey)) {
            items.add(item);
        }

        return items;
    }
}
