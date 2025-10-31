package com.hbm.nucleartech.client;

import com.hbm.nucleartech.particle.AsbestosParticleProvider;
import com.hbm.nucleartech.particle.RegisterParticles;
import com.hbm.nucleartech.particle.DeconParticleProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "hbm", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

//    public static void init(IEventBus eventBus) {
//
//        eventBus.addListener(ClientSetup::registerParticleFactories);
//    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {

        event.registerSpriteSet(RegisterParticles.DECON_PARTICLE.get(), DeconParticleProvider::new);
        event.registerSpriteSet(RegisterParticles.ASBESTOS_PARTICLE.get(), AsbestosParticleProvider::new);
    }
}
