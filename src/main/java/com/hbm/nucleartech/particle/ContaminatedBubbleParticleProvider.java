package com.hbm.nucleartech.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContaminatedBubbleParticleProvider implements ParticleProvider<SimpleParticleType> {

    private final SpriteSet sprites;

    public ContaminatedBubbleParticleProvider(SpriteSet sprites) {

        this.sprites = sprites;
    }

    @Nullable
    @Override
    public Particle createParticle(@NotNull SimpleParticleType pType, @NotNull ClientLevel pLevel, double pX, double pY,
                                   double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        return new ContaminatedBubbleParticle.Provider(sprites).createParticle(pType, pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
    }
}
