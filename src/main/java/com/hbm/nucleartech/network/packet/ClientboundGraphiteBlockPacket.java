package com.hbm.nucleartech.network.packet;

import com.hbm.nucleartech.block.entity.GraphiteBlockEntity;
import com.hbm.nucleartech.block.entity.ShredderEntity;
import com.hbm.nucleartech.util.FloatingLong;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundGraphiteBlockPacket {

    public final BlockPos pos;

    public final int type;

    public final float heat;

    public final float depletion;

    public final int flux;

    public ClientboundGraphiteBlockPacket(int x, int y, int z,
                                          int type, float heat,
                                          float depletion, int flux) {

        this.pos = new BlockPos(x, y, z);

        this.type = type;

        this.heat = heat;
        this.depletion = depletion;
        this.flux = flux;
    }

    public static void encode(ClientboundGraphiteBlockPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.pos.getX());
        buf.writeInt(msg.pos.getY());
        buf.writeInt(msg.pos.getZ());

        buf.writeInt(msg.type);

        buf.writeFloat(msg.heat);
        buf.writeFloat(msg.depletion);
        buf.writeInt(msg.flux);
    }

    public static ClientboundGraphiteBlockPacket decode(FriendlyByteBuf buf) {
        return new ClientboundGraphiteBlockPacket(
                buf.readInt(), buf.readInt(), buf.readInt(),
                buf.readInt(), buf.readFloat(), buf.readFloat(),
                buf.readInt()
        );
    }

    public static void handle(ClientboundGraphiteBlockPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {

                BlockEntity e = level.getBlockEntity(msg.pos);
                if(e instanceof GraphiteBlockEntity graphiteBlock) {

                    graphiteBlock.type = msg.type;

                    graphiteBlock.heat = msg.heat;
                    graphiteBlock.depletionPercentage = msg.depletion;
                    graphiteBlock.lastNeutrons = msg.type;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
