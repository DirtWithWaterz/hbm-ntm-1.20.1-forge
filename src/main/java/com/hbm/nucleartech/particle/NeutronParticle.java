package com.hbm.nucleartech.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import org.jetbrains.annotations.NotNull;

public class NeutronParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected NeutronParticle(ClientLevel world, double x, double y, double z,
                              double dx, double dy, double dz, SpriteSet sprites) {
        super(world, x, y, z, dx, dy, dz);
        this.setSize(0.5F, 0.5F);
        this.lifetime = world.random.nextInt(10)+1;
        this.gravity = 0.0F;
        this.setSprite(sprites.get(world.random));
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.sprites = sprites;
        this.scale(0.1F);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; // ParticleRenderType.PARTICLE_SHEET does not exist.
    }
}
