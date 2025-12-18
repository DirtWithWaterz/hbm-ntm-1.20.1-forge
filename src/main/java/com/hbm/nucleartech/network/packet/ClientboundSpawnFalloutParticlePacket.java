package com.hbm.nucleartech.network.packet;

import com.hbm.nucleartech.particle.RegisterParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSpawnFalloutParticlePacket {

    public final double x, y, z;
    public final double dx, dy, dz;

    public ClientboundSpawnFalloutParticlePacket(double x, double y, double z, double dx, double dy, double dz) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    public static void encode(ClientboundSpawnFalloutParticlePacket msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeDouble(msg.dx);
        buf.writeDouble(msg.dy);
        buf.writeDouble(msg.dz);
    }

    public static ClientboundSpawnFalloutParticlePacket decode(FriendlyByteBuf buf) {
        return new ClientboundSpawnFalloutParticlePacket(
                buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readDouble(), buf.readDouble(), buf.readDouble()
        );
    }

    public static void handle(ClientboundSpawnFalloutParticlePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                level.addParticle(RegisterParticles.FALLOUT_PARTICLE.get(),
                        msg.x, msg.y, msg.z,
                        msg.dx, msg.dy, msg.dz);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
