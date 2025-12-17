package com.hbm.nucleartech.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSpawnRadioactiveDustParticlePacket {

    public final double x, y, z;
    public final double dx, dy, dz;

    public ClientboundSpawnRadioactiveDustParticlePacket(double x, double y, double z, double dx, double dy, double dz) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    public static void encode(ClientboundSpawnRadioactiveDustParticlePacket msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeDouble(msg.dx);
        buf.writeDouble(msg.dy);
        buf.writeDouble(msg.dz);
    }

    public static ClientboundSpawnRadioactiveDustParticlePacket decode(FriendlyByteBuf buf) {
        return new ClientboundSpawnRadioactiveDustParticlePacket(
                buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readDouble(), buf.readDouble(), buf.readDouble()
        );
    }

    public static void handle(ClientboundSpawnRadioactiveDustParticlePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                level.addParticle(ParticleTypes.MYCELIUM,
                        msg.x, msg.y, msg.z,
                        msg.dx, msg.dy, msg.dz);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
