package com.hbm.nucleartech;

import com.hbm.nucleartech.block.RegisterBlocks;
import com.hbm.nucleartech.block.custom.RadResistantBlock;
import com.hbm.nucleartech.block.entity.RegisterBlockEntities;
import com.hbm.nucleartech.block.entity.ShredderEntity;
import com.hbm.nucleartech.block.entity.client.BurnerPressRenderer;
import com.hbm.nucleartech.block.entity.client.ShredderRenderer;
import com.hbm.nucleartech.entity.HbmEntities;
import com.hbm.nucleartech.entity.client.NuclearCreeperRenderer;
import com.hbm.nucleartech.handler.HazmatRegistry;
import com.hbm.nucleartech.handler.RadiationSystemNT;
import com.hbm.nucleartech.item.RegisterCreativeTabs;
import com.hbm.nucleartech.item.RegisterItems;
import com.hbm.nucleartech.item.custom.GeigerCounterItem;
import com.hbm.nucleartech.network.HbmPacketHandler;
import com.hbm.nucleartech.particle.RegisterParticles;
import com.hbm.nucleartech.recipe.RegisterRecipes;
import com.hbm.nucleartech.screen.BurnerPressScreen;
import com.hbm.nucleartech.screen.RegisterMenuTypes;
import com.hbm.nucleartech.screen.ShredderScreen;
import com.hbm.nucleartech.sound.RegisterSounds;
import com.hbm.nucleartech.util.ArmorUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
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
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.io.File;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

        GeckoLib.initialize();

        RegisterCreativeTabs.register(modEventBus);
        RegisterItems.register(modEventBus);
        RegisterBlocks.register(modEventBus);
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

    public static Set<BlockState> HAZARD_STATES = null;

    private void commonSetup(final FMLCommonSetupEvent event) {

        configDir = FMLPaths.CONFIGDIR.get().toFile();
        configHbmDir = new File(configDir.getAbsolutePath() + File.separatorChar + "hbmConfig");

        if(!configHbmDir.exists())
            configHbmDir.mkdir();

        HbmPacketHandler.register();
        HazmatRegistry.registerHazmats();
        ArmorUtil.register();

        GeigerCounterItem.initSoundMap();

        HAZARD_STATES = Set.of(
                RegisterBlocks.URANIUM_ORE.get().defaultBlockState(),
                RegisterBlocks.DEEPSLATE_URANIUM_ORE.get().defaultBlockState(),
                RegisterBlocks.RED_THORIUM_ORE.get().defaultBlockState(),
                RegisterBlocks.ORANGE_THORIUM_ORE.get().defaultBlockState(),
                RegisterBlocks.YELLOW_THORIUM_ORE.get().defaultBlockState(),
                RegisterBlocks.WHITE_THORIUM_ORE.get().defaultBlockState(),
                RegisterBlocks.LIGHT_GRAY_THORIUM_ORE.get().defaultBlockState(),
                RegisterBlocks.BROWN_THORIUM_ORE.get().defaultBlockState());
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {


    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

        AdvancementManager.init(event.getServer());
        RadiationSystemNT.init(event.getServer());
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            EntityRenderers.register(HbmEntities.NUCLEAR_CREEPER.get(), NuclearCreeperRenderer::new);
            BlockEntityRenderers.register(RegisterBlockEntities.BURNER_PRESS_ENTITY.get(), BurnerPressRenderer::new);
            BlockEntityRenderers.register(RegisterBlockEntities.SHREDDER_ENTITY.get(), ShredderRenderer::new);

            MenuScreens.register(RegisterMenuTypes.BURNER_PRESS_MENU.get(), BurnerPressScreen::new);
            MenuScreens.register(RegisterMenuTypes.SHREDDER_MENU.get(), ShredderScreen::new);
        }
    }

    private static final RandomSource RANDOM = RandomSource.create();

    public static int random(int min, int max) {

        return RANDOM.nextInt(max - min + 1) + min;
    }
}
