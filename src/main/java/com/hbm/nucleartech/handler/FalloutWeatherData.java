package com.hbm.nucleartech.handler;

import com.hbm.nucleartech.block.RegisterBlocks;
import com.hbm.nucleartech.network.HbmPacketHandler;
import com.hbm.nucleartech.network.packet.ClientboundFalloutStatePacket;
import com.hbm.nucleartech.util.ContaminationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.hbm.nucleartech.block.custom.ContaminatedVariableLayerBlock.LAYERS;
import static com.hbm.nucleartech.block.custom.ContaminatedVariableLayerBlock.VARIANT;

@Mod.EventBusSubscriber(modid = "hbm", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FalloutWeatherData extends SavedData {
    private static final String ID = "hbm_fallout_weather";

    private Map<Integer, WeatherDataInstance> instances = new HashMap<>();

    private int prevActiveCount = -1;  // Track changes for sync

    public static int getVariant(float dist, float max_dist, ServerLevel level) {

            if(dist >= max_dist)
                return -1;
            float step = max_dist / 7;

            long result = 7 - Math.round(Math.floor(dist / step));

            return (int)Mth.clamp(result + level.getRandom().nextInt(2), 0, 7);
    }

    private static class WeatherDataInstance {

        private boolean active = false;
        private int duration;  // Ticks
        private final BlockPos center;  // Explosion epicenter (for localized spread)
        private final float intensity;

        public WeatherDataInstance(boolean active, BlockPos center, int duration, float intensity) {
            this.active = active;
            this.center = center;
            this.duration = duration;
            this.intensity = intensity;
        }

        public void tick(ServerLevel level) {
            if (!active) return;
            duration--;
//            HBM.LOGGER.debug("[Debug] duration = {}", duration);
            if (duration <= 0) {
                active = false;
//                HBM.LOGGER.error("inactive");
                return;  // Mark for removal post-loop
            }
//            HBM.LOGGER.debug("active");

            // Accumulation: Place layers in radius (throttle to avoid lag)
            if (level.random.nextInt(80) == 0) {  // ~4/sec
                int radius = (int)(20 * intensity);
                for (int i = 0; i < 10; i++) {  // Perf cap: 50 samples/tick
                    double dist = level.random.nextDouble() * radius;
                    double angle = level.random.nextDouble() * Math.PI * 2;
                    BlockPos pos = center.offset((int)(Math.cos(angle) * dist), 0, (int)(Math.sin(angle) * dist));
                    pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos);  // Top surface

                    BlockState surface = level.getBlockState(pos);

                    if(surface.is(RegisterBlocks.CONTAMINATED_WATER.get()) || surface.is(RegisterBlocks.STILL_WATER.get()))
                        continue;

                    if (!surface.is(RegisterBlocks.FALLOUT.get())) {
                        if(level.getBlockState(pos.above()).isAir())
                            level.setBlock(pos, RegisterBlocks.FALLOUT.get().defaultBlockState().setValue(VARIANT, (int)getVariant((float)dist, radius, level)), 3);
                    }
                    else {
                        if(surface.getValue(LAYERS) < 8) {

                            BlockState newState = surface.setValue(LAYERS, surface.getValue(LAYERS) + 1);
                            level.setBlock(pos, newState, 2);
                            level.neighborChanged(pos.below(), newState.getBlock(), pos);  // Update below
                        }
                        else if(level.getBlockState(pos.above()).isAir() && level.getBlockState(pos.above().above()).isAir())
                            level.setBlock(pos.above(), RegisterBlocks.FALLOUT.get().defaultBlockState().setValue(VARIANT, (int)getVariant((float)dist, radius, level)), 3);
                    }
                }
            }

            // Entity effects: Radiation in radius
            int radius = (int)(20 * intensity);
            AABB area = new AABB(center).inflate(radius).move(0, level.getMinBuildHeight(), 0).inflate(10);  // Buffer
            int rad = (int)(50 * intensity);
            level.getEntitiesOfClass(LivingEntity.class, area).forEach(e -> {
                if (level.random.nextFloat() < 0.01F * intensity) {

                    ContaminationUtil.contaminate(e, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.CREATIVE, rad);
                }
            });
        }
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            FalloutWeatherData data = FalloutWeatherData.get(level);
            data.tick(level);
        }
    }

    public int startFallout(ServerLevel level, BlockPos center, int duration, float intensity) {
        instances.put(instances.size(), new WeatherDataInstance(true, center, duration * 20, intensity));  // Convert seconds to ticks
        setDirty();
//        syncToClients(level);  // Immediate sync on start
        return instances.size() - 1;
    }

    public boolean isActive(int id) {
        if (id < 0 || !instances.containsKey(id)) return false;
        WeatherDataInstance inst = instances.get(id);
        return inst != null && inst.active;
    }

    public void tick(ServerLevel level) {
        Iterator<Map.Entry<Integer, WeatherDataInstance>> itr = instances.entrySet().iterator();
        boolean changed = false;
        while (itr.hasNext()) {
            Map.Entry<Integer, WeatherDataInstance> entry = itr.next();
            WeatherDataInstance instance = entry.getValue();
            instance.tick(level);
            if (!instance.active) {
                itr.remove();  // Safe removal during iteration
                instances.remove(entry.getKey());
                changed = true;
            }
        }
        int activeCount = instances.size();
        if (changed || activeCount != prevActiveCount) {
            syncToClients(level);
            prevActiveCount = activeCount;
        }
        if (!instances.isEmpty()) setDirty();
    }

    private void syncToClients(ServerLevel level) {
        List<ClientboundFalloutStatePacket.FalloutInstance> syncList = new ArrayList<>();
        for (WeatherDataInstance inst : instances.values()) {
            if (inst.active) syncList.add(new ClientboundFalloutStatePacket.FalloutInstance(inst.center, inst.intensity, inst.duration));
        }
        ClientboundFalloutStatePacket packet = new ClientboundFalloutStatePacket(syncList);
        HbmPacketHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(level::dimension), packet);  // Send to all players
    }

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            FalloutWeatherData.get(level).syncToClients(level);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level() instanceof ServerLevel level) {
            FalloutWeatherData.get(level).syncToClients(level);
        }
    }

    public static FalloutWeatherData create() {
        return new FalloutWeatherData();
    }

    public static FalloutWeatherData load(CompoundTag tag) {
        FalloutWeatherData data = create();
        data.prevActiveCount = tag.getInt("prev_active_count");
        ListTag instancesTag = tag.getList("instances", Tag.TAG_COMPOUND);
        int key = 0;
        for (Tag t : instancesTag) {
            CompoundTag instTag = (CompoundTag) t;
            boolean active = instTag.getBoolean("active");
            int duration = instTag.getInt("duration");
            float intensity = instTag.getFloat("intensity");
            BlockPos center = null;
            if (instTag.contains("center_x")) {
                int x = instTag.getInt("center_x");
                int y = instTag.getInt("center_y");
                int z = instTag.getInt("center_z");
                center = new BlockPos(x, y, z);
            }
            data.instances.put(key++, new WeatherDataInstance(active, center, duration, intensity));
        }
        return data;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        ListTag instancesTag = new ListTag();
        tag.putInt("prev_active_count", prevActiveCount);
        for (WeatherDataInstance instance : instances.values()) {
            CompoundTag instTag = new CompoundTag();
            instTag.putBoolean("active", instance.active);
            instTag.putInt("duration", instance.duration);
            instTag.putFloat("intensity", instance.intensity);
            if (instance.center != null) {
                instTag.putInt("center_x", instance.center.getX());
                instTag.putInt("center_y", instance.center.getY());
                instTag.putInt("center_z", instance.center.getZ());
            }
            instancesTag.add(instTag);
        }
        tag.put("instances", instancesTag);
        return tag;
    }

    public static FalloutWeatherData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FalloutWeatherData::load, FalloutWeatherData::create, ID);
    }
}