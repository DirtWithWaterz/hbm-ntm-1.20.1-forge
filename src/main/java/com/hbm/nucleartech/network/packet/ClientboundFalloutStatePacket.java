package com.hbm.nucleartech.network.packet;

import com.hbm.nucleartech.client.ClientFalloutHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ClientboundFalloutStatePacket {

    private final List<FalloutInstance> instances;

    public static class FalloutInstance {
        public final BlockPos center;
        public final float intensity;
        public final int duration;

        public FalloutInstance(BlockPos center, float intensity, int duration) {
            this.center = center;
            this.intensity = intensity;
            this.duration = duration;
        }
    }

    public ClientboundFalloutStatePacket(List<FalloutInstance> instances) {
        this.instances = instances;
    }

    public static void encode(ClientboundFalloutStatePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.instances.size());
        for (FalloutInstance inst : msg.instances) {
            buf.writeBlockPos(inst.center);
            buf.writeFloat(inst.intensity);
            buf.writeInt(inst.duration);
        }
    }

    public static ClientboundFalloutStatePacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<FalloutInstance> instances = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            BlockPos center = buf.readBlockPos();
            float intensity = buf.readFloat();
            int duration = buf.readInt();
            instances.add(new FalloutInstance(center, intensity, duration));
        }
        return new ClientboundFalloutStatePacket(instances);
    }

    public static void handle(ClientboundFalloutStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Update client-side state (e.g., in a client-only handler)
            ClientFalloutHandler.updateFalloutInstances(msg.instances);
        });
        ctx.get().setPacketHandled(true);
    }
}