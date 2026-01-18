package com.hbm.nucleartech.client;

import com.hbm.nucleartech.block.RegisterBlocks;
import com.hbm.nucleartech.entity.HbmEntities;
import com.hbm.nucleartech.entity.client.NukeTorexRenderer;
import com.hbm.nucleartech.fluid.RegisterFluids;
import com.hbm.nucleartech.particle.*;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "hbm", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

//    public static void init(IEventBus eventBus) {
//
//        eventBus.addListener(ClientSetup::registerParticleFactories);
//    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(HbmEntities.NUKE_TOREX.get(), NukeTorexRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

        ItemBlockRenderTypes.setRenderLayer(
                RegisterBlocks.CONTAMINATED_ICE.get(),
                RenderType.translucent()
        );
        ItemBlockRenderTypes.setRenderLayer(
                RegisterFluids.CONTAMINATED_WATER.get(),
                RenderType.translucent()
        );
        ItemBlockRenderTypes.setRenderLayer(
                RegisterFluids.FLOWING_CONTAMINATED_WATER.get(),
                RenderType.translucent()
        );
        ItemBlockRenderTypes.setRenderLayer(
                RegisterFluids.STILL_WATER.get(),
                RenderType.translucent()
        );
        ItemBlockRenderTypes.setRenderLayer(
                RegisterFluids.FLOWING_STILL_WATER.get(),
                RenderType.translucent()
        );
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {

        event.registerSpriteSet(RegisterParticles.DECON_PARTICLE.get(), DeconParticleProvider::new);
        event.registerSpriteSet(RegisterParticles.ASBESTOS_PARTICLE.get(), AsbestosParticleProvider::new);
        event.registerSpriteSet(RegisterParticles.NEUTRON_PARTICLE.get(), NeutronParticleProvider::new);
        event.registerSpriteSet(RegisterParticles.CONTAMINATED_BUBBLE_PARTICLE.get(), ContaminatedBubbleParticleProvider::new);
        event.registerSpriteSet(RegisterParticles.CONTAMINATED_SPLASH_PARTICLE.get(), ContaminatedSplashParticleProvider::new);
        event.registerSpriteSet(RegisterParticles.FALLOUT_PARTICLE.get(), FalloutParticleProvider::new);
    }
}
