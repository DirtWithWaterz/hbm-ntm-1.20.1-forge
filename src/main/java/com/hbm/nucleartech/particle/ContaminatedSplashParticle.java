package com.hbm.nucleartech.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ContaminatedSplashParticle extends WaterDropParticle {
    ContaminatedSplashParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        super(pLevel, pX, pY, pZ);
        this.gravity = 0.04F;
        if (pYSpeed == (double)0.0F && (pXSpeed != (double)0.0F || pZSpeed != (double)0.0F)) {
            this.xd = pXSpeed;
            this.yd = 0.1;
            this.zd = pZSpeed;
        }
    }

//    public ParticleRenderType getRenderType() {
//        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
//    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            ContaminatedSplashParticle $$8 = new ContaminatedSplashParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }
}
