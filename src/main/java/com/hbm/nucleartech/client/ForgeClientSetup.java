package com.hbm.nucleartech.client;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.fluid.RegisterFluids;
import com.hbm.nucleartech.network.HbmPacketHandler;
import com.hbm.nucleartech.network.packet.ClientboundSpawnContaminatedBubbleParticlePacket;
import com.hbm.nucleartech.network.packet.ClientboundSpawnContaminatedSplashParticlePacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = HBM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientSetup {

    private static final ResourceLocation CONTAM_OVERLAY =
            new ResourceLocation(HBM.MOD_ID, "textures/misc/under_contaminated_water.png");

    private static final Map<UUID, Boolean> previousFeetInFluid = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> previousEyesInFluid = new ConcurrentHashMap<>();
    private static TickableSoundInstance ambientSound = null;

    private static boolean prevEyesFlClient = false;

    @SubscribeEvent
    public static void onClientPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isServer()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        FluidType[] targetType = new FluidType[]{RegisterFluids.CONTAMINATED_WATER_TYPE.get(), RegisterFluids.STILL_WATER_TYPE.get()};
        boolean currentEyes = Arrays.stream(targetType).anyMatch(a -> a == player.getEyeInFluidType());

        SoundManager soundManager = Minecraft.getInstance().getSoundManager();

        // ENTER: Start loop
        if (currentEyes && !prevEyesFlClient) {
            if (ambientSound != null) {
                soundManager.stop(ambientSound);  // Safety
            }
            ambientSound = new ContaminatedWaterAmbientSound(
                    SoundEvents.AMBIENT_UNDERWATER_LOOP, player/*, targetType*/
            );
            soundManager.play(ambientSound);
        }
        // EXIT: Stop loop (sound also self-stops via tick)
        else if (!currentEyes && prevEyesFlClient) {
            if (ambientSound != null) {
                soundManager.stop(ambientSound);
                ambientSound = null;
            }
        }

        prevEyesFlClient = currentEyes;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;

        Player player = event.player;
        UUID uuid = player.getUUID();
        Level level = player.level();

        FluidType[] targetType = new FluidType[]{RegisterFluids.CONTAMINATED_WATER_TYPE.get(), RegisterFluids.STILL_WATER_TYPE.get()};

        boolean currentFeet = isFeetOverlappingModdedFluid(player, level, targetType);
        Boolean prevFeet = previousFeetInFluid.getOrDefault(uuid, false);

        boolean currentEyes = Arrays.stream(targetType).anyMatch(a -> a == player.getEyeInFluidType());
        Boolean prevEyes = previousEyesInFluid.getOrDefault(uuid, false);

        if (currentFeet && !prevFeet) {

            level.playSound(null, player.getOnPos(), SoundEvents.GENERIC_SPLASH, SoundSource.PLAYERS, 0.1f, 1);

            // to implement:
            // send bubble particles
            // send splash particles
            int r = level.random.nextInt(3)+4;
            for(int i = 0; i < r; i++) {

                ClientboundSpawnContaminatedBubbleParticlePacket packet = new ClientboundSpawnContaminatedBubbleParticlePacket(
                        player.getX() + ((level.random.nextFloat() - 0.25D) * (level.random.nextBoolean() ? 1 : -1)) * player.getBbWidth(),
                        player.getY()+0.7f,
                        player.getZ() + ((level.random.nextFloat() - 0.25D) * (level.random.nextBoolean() ? 1 : -1)) * player.getBbWidth(),
                        0.0D, 0.0D, 0.0D
                );

                HbmPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(player.getOnPos())), packet);
            }
            r = level.random.nextInt(4)+6;
            for(int i = 0; i < r; i++) {

                ClientboundSpawnContaminatedSplashParticlePacket packet = new ClientboundSpawnContaminatedSplashParticlePacket(
                        player.getX() + ((level.random.nextFloat() - 0.25D) * (level.random.nextBoolean() ? 1 : -1)) * player.getBbWidth(),
                        player.getY()+0.7f,
                        player.getZ() + ((level.random.nextFloat() - 0.25D) * (level.random.nextBoolean() ? 1 : -1)) * player.getBbWidth(),
                        0.0D, 0.0D, 0.0D
                );

                HbmPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(player.getOnPos())), packet);
            }
        }
        if(currentEyes && !prevEyes) {

            level.playSound(null, player.getOnPos(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundSource.PLAYERS, 1, 1);
        }
        else if (!currentEyes && prevEyes) {

            level.playSound(null, player.getOnPos(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundSource.PLAYERS, 1, 1);
        }

        previousFeetInFluid.put(uuid, currentFeet);
        previousEyesInFluid.put(uuid, currentEyes);
    }

    private static boolean isFeetOverlappingModdedFluid(Player player, Level level, FluidType[] targetType) {
        AABB bb = player.getBoundingBox().deflate(0, 0.5, 0);  // Catches edges
        int minX = Mth.floor(bb.minX), maxX = Mth.ceil(bb.maxX);
        int minY = Mth.floor(bb.minY), maxY = Mth.ceil(bb.maxY);
        int minZ = Mth.floor(bb.minZ), maxZ = Mth.ceil(bb.maxZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    FluidState fs = level.getFluidState(pos);
                    if (!fs.isEmpty() && Arrays.stream(targetType).anyMatch(a -> a == fs.getFluidType())) {
                        return true;  // Early exit: found it!
                    }
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        previousFeetInFluid.remove(event.getEntity().getUUID());
        previousEyesInFluid.remove(event.getEntity().getUUID());
    }

    public static class ContaminatedWaterAmbientSound extends AbstractTickableSoundInstance {
        private final LocalPlayer player;
//        private final FluidType targetType;

        public ContaminatedWaterAmbientSound(SoundEvent sound, LocalPlayer player/*, FluidType targetType*/) {
            super(sound, SoundSource.AMBIENT, RandomSource.create());
            this.looping = true;
            this.delay = 0;
            this.volume = 1.0F;
            this.pitch = 1.0F;
            this.player = player;
//            this.targetType = targetType;
            updatePosition();
        }

        @Override
        public void tick() {
//            if (!this.player.isAlive() || this.player.level() != Minecraft.getInstance().level || this.player.getEyeInFluidType() != targetType) {
//                this.stop();  // Self-stop if exited/teleported
//            }
            updatePosition();
        }

        private void updatePosition() {
            this.x = (float) this.player.getX();
            this.y = (float) this.player.getY();
            this.z = (float) this.player.getZ();
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        if (player.getEyeInFluidType() != RegisterFluids.CONTAMINATED_WATER_TYPE.get()) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

         guiGraphics.fill(0, 0, width, height, 0x03000000);

        // Use the modern shader + texture binding
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc(); // SRC_ALPHA, ONE_MINUS_SRC_ALPHA
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 0.0125f); // alpha = 1 here, or <1 to tint/fade
        RenderSystem.setShaderTexture(0, CONTAM_OVERLAY);

        // Use GuiGraphics.blit overload that draws the entire texture full-screen.
        // This variant: blit(ResourceLocation, x, y, u, v, width, height, texWidth, texHeight)
        guiGraphics.blit(
                CONTAM_OVERLAY,
                0, 0,           // x, y
                0, 0,           // u, v
                width, height,  // width, height to draw
                160, 160   // texture size (use actual texture pixel size if different)
        );

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f); // reset
    }
}
