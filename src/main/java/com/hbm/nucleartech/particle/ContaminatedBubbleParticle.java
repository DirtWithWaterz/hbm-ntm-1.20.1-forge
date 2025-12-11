package com.hbm.nucleartech.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ContaminatedBubbleParticle extends TextureSheetParticle {
    ContaminatedBubbleParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        super(pLevel, pX, pY, pZ);
        float size = (this.random.nextFloat() * 0.003f) + 0.0005f;
        this.setSize(size, size);
        this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
        this.xd = pXSpeed * (double)0.2F + (Math.random() * (double)2.0F - (double)1.0F) * (double)0.02F;
        this.yd = pYSpeed * (double)0.2F + (Math.random() * (double)2.0F - (double)1.0F) * (double)0.02F;
        this.zd = pZSpeed * (double)0.2F + (Math.random() * (double)2.0F - (double)1.0F) * (double)0.02F;
        this.lifetime = (int)((double)8.0F / (Math.random() * 0.8 + 0.2));
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
        } else {
            if (this.level.getFluidState(BlockPos.containing(this.x, this.y, this.z)).is(FluidTags.WATER))
                this.yd += 0.002;
            else
                this.yd -= 0.002;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double)0.85F;
            this.yd *= (double)0.85F;
            this.zd *= (double)0.85F;

        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            ContaminatedBubbleParticle $$8 = new ContaminatedBubbleParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }
}
