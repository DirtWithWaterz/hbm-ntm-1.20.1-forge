package com.hbm.nucleartech.client;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.network.packet.ClientboundFalloutStatePacket;
import com.hbm.nucleartech.particle.RegisterParticles;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientFalloutHandler {
    private static List<ClientboundFalloutStatePacket.FalloutInstance> activeInstances = new ArrayList<>();
    private static boolean inFallout = false;
    private static float currentIntensity = 0.0F;

    public static void updateFalloutInstances(List<ClientboundFalloutStatePacket.FalloutInstance> instances) {
        activeInstances = instances;
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        inFallout = false;
        currentIntensity = 0.0F;
        for (ClientboundFalloutStatePacket.FalloutInstance inst : activeInstances) {
            double dist = mc.player.blockPosition().distToCenterSqr(inst.center.getX(), inst.center.getY(), inst.center.getZ());
            int radius = (int)(20 * inst.intensity) + 2;  // Buffer
            if (dist <= radius * radius) {
                inFallout = true;
                currentIntensity = Math.max(currentIntensity, Math.min(1, inst.intensity * (1 - (float)(dist / (radius * radius)))));
            }
        }

        if (inFallout) {
            // Spawn particles scaled by currentIntensity
            // e.g., for (int i = 0; i < 150 * currentIntensity; i++) { level.addParticle(...); }
            ClientLevel level = mc.level;

            Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
//            AABB viewBox = new AABB(cam, cam).inflate(20, 10, 20).move(0, -cam.y + event.player.getY(), 0);
            RandomSource rand = level.random;

            for (int i = 0; i < 150; i++) {  // Adjust density
                BlockPos pos = BlockPos.containing(cam.x + (rand.nextDouble() - 0.5) * 20,
                        cam.y + rand.nextDouble() * 10,
                        cam.z + (rand.nextDouble() - 0.5) * 20);
                level.addParticle(RegisterParticles.FALLOUT_PARTICLE.get(),
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 4,  // Sky height
                        pos.getZ() + rand.nextDouble(),
                        rand.nextDouble() * (rand.nextBoolean() ? -1 : 1), 0, rand.nextDouble() * (rand.nextBoolean() ? -1 : 1));  // Vel=0, gravity handles fall

            }

            // sound logic here (maybe some wasteland/windy noises)

        }
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        if (inFallout) {
            // Grayish fog (adjust RGB for desired color, e.g., 0.3F, 0.3F, 0.3F for dark gray)
            // Interpolate with original based on intensity (optional)
            float gray = 0.8F * currentIntensity + event.getRed() * (1 - currentIntensity);
            event.setRed(gray);
            event.setGreen(gray);
            event.setBlue(gray);
        }
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (inFallout) {

//            HBM.LOGGER.debug("[Debug] current intensity: {}", currentIntensity);

            float density = currentIntensity * 0.5f; // 0.0-1.0 (thicker with intensity)
            event.setNearPlaneDistance(0.0F); // Start close
            event.setFarPlaneDistance(64.0F * (1 - density) + 16.0F); // Shorter view = thicker fog
            event.setFogShape(FogShape.SPHERE);
            event.setCanceled(true); // Override vanilla
        }
    }
}