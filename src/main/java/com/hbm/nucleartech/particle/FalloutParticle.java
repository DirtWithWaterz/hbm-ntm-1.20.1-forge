package com.hbm.nucleartech.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import org.jetbrains.annotations.NotNull;

public class FalloutParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected FalloutParticle(ClientLevel world, double x, double y, double z,
                              double dx, double dy, double dz, SpriteSet sprites) {
        super(world, x, y, z, dx, dy, dz);
        this.quadSize = 0.2F;  // Size
        this.lifetime = 40 + level.random.nextInt(20);  // Longer fall
        this.gravity = 0.9F;  // Slow fall
        this.sprites = sprites;
        this.setSprite(sprites.get(world.random));
        this.xd = dx;  // Wind sway
        this.yd = dy;
        this.zd = dz;
        this.setColor(0.5F, 0.5F, 0.6F);  // Gray/green tint
    }

    @Override
    public void tick() {
        super.tick();
        // Add wind sway: slight random horizontal adjustments
        this.xd += (this.random.nextFloat() - 0.5F) * 0.02F;
        this.zd += (this.random.nextFloat() - 0.5F) * 0.02F;
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; // ParticleRenderType.PARTICLE_SHEET does not exist.
    }
}
