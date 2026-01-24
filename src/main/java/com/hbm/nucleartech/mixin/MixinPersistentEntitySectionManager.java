package com.hbm.nucleartech.mixin;

import com.hbm.nucleartech.entity.effects.NukeTorexEntity;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.reflect.Field;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static com.hbm.nucleartech.entity.effects.NukeTorexEntity.SHOULD_DIE;

@Mixin(PersistentEntitySectionManager.class)
public class MixinPersistentEntitySectionManager<T extends EntityAccess> {

    @Shadow
    @Final
    private Queue<ChunkEntities<T>> loadingInbox;

    @Shadow
    @Final
    LevelCallback<T> callbacks;

    @Shadow
    @Final
    private EntityLookup<T> visibleEntityStorage;

    /**
     * Shadow the chunkLoadStatuses as a raw map. We don't use the generic type since the inner enum type
     * is not accessible at compile time. The raw map lets us put Object values (the enum constants).
     */
    @Shadow
    @Final
    private Long2ObjectMap chunkLoadStatuses;

    @Shadow
    private boolean addEntity(T pEntity, boolean pWorldGenSpawned) { throw new AssertionError(); }

    // cached reference to the enum constant object for LOADED
    private static final AtomicReference<Object> LOADED_ENUM_CONST = new AtomicReference<>(null);
    private static final AtomicReference<Class<?>> CHUNK_LOAD_STATUS_CLASS = new AtomicReference<>(null);

    /**
     * Find (and cache) the inner enum class and the 'LOADED' enum constant.
     * This is safe even if the enum class is private or obfuscated.
     */
    @SuppressWarnings("unchecked")
    private static Object getLoadedEnumConstant() {
        Object cached = LOADED_ENUM_CONST.get();
        if (cached != null) return cached;

        Class<?> enumClass = CHUNK_LOAD_STATUS_CLASS.get();
        if (enumClass == null) {
            // find an enum declared inside PersistentEntitySectionManager that contains a constant named "LOADED"
            for (Class<?> c : PersistentEntitySectionManager.class.getDeclaredClasses()) {
                if (!c.isEnum()) continue;
                try {
                    Object[] consts = c.getEnumConstants();
                    if (consts == null) continue;
                    for (Object cc : consts) {
                        if ("LOADED".equals(cc.toString())) {
                            enumClass = c;
                            break;
                        }
                    }
                } catch (Throwable t) {
                    // ignore and continue
                }
                if (enumClass != null) break;
            }
            if (enumClass == null) {
                // fallback: try to find by simple name in case mappings expose it
                for (Class<?> c : PersistentEntitySectionManager.class.getDeclaredClasses()) {
                    if (c.isEnum() && "ChunkLoadStatus".equals(c.getSimpleName())) {
                        enumClass = c;
                        break;
                    }
                }
            }
            CHUNK_LOAD_STATUS_CLASS.set(enumClass);
        }

        if (enumClass == null) {
            throw new IllegalStateException("Could not find PersistentEntitySectionManager inner enum for ChunkLoadStatus");
        }

        // find LOADED constant
        for (Object cc : enumClass.getEnumConstants()) {
            if ("LOADED".equals(cc.toString())) {
                LOADED_ENUM_CONST.set(cc);
                return cc;
            }
        }

        // If not found, throw
        throw new IllegalStateException("Could not find LOADED enum constant in ChunkLoadStatus enum");
    }

    /**
     * @author mudz
     * @reason ignore nuke torex when unloading entities.
     */
    @Overwrite
    private void unloadEntity(EntityAccess p_157586_) {
        if (p_157586_ instanceof NukeTorexEntity torex && !torex.getEntityData().get(SHOULD_DIE)) return;

        p_157586_.setRemoved(Entity.RemovalReason.UNLOADED_TO_CHUNK);
        p_157586_.setLevelCallback(EntityInLevelCallback.NULL);
    }

    /**
     * @author mudz
     * @reason ignore nuke torex when loading entities.
     */
    @Overwrite
    private void processPendingLoads() {

        ChunkEntities<T> chunkentities;
        while ((chunkentities = loadingInbox.poll()) != null) {
            chunkentities.getEntities().forEach((p_157593_) -> {

                if(!(p_157593_ instanceof NukeTorexEntity torex && !torex.getEntityData().get(SHOULD_DIE))) {

                    addEntity(p_157593_, true);
                    if (p_157593_ instanceof Entity entity) entity.onAddedToWorld();
                }
            });

            // put the LOADED enum constant into the map (use raw map to accept Object)
            long key = chunkentities.getPos().toLong();
            Object loadedConst = getLoadedEnumConstant();

            // plain put into the raw map
            chunkLoadStatuses.put(key, loadedConst);
        }
    }
}
