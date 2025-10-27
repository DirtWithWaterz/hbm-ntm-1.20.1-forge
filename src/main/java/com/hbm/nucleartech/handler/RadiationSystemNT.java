package com.hbm.nucleartech.handler;


import com.hbm.nucleartech.AdvancementManager;
import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.capability.HbmCapabilities;
import com.hbm.nucleartech.damagesource.RegisterDamageSources;
import com.hbm.nucleartech.entity.HbmEntities;
import com.hbm.nucleartech.entity.custom.NuclearCreeperEntity;
import com.hbm.nucleartech.interfaces.IEntityCapabilityBase.Type;
import com.hbm.nucleartech.util.ContaminationUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLWarning;
import java.util.*;

import static com.hbm.nucleartech.capability.entity.LivingEntityCapability.maxBlacklung;
import static com.hbm.nucleartech.util.ContaminationUtil.getPlayerNeutronRads;


@Mod.EventBusSubscriber(modid = HBM.MOD_ID)
public class RadiationSystemNT {

    static MinecraftServer server;

//    private static int ticks;

    public static void init(MinecraftServer pServer) {

        server = pServer;

    }

    /**
     * @deprecated "When implementing max health related changes, update entity capability: Type.OLD_MAX_HEALTH. (20 + whatever modifications) p.s. this makes the mod incompatible with other mods that add max health, add compatibilities later. Other data that must be updated later is in onPlayerRespawnedSyncPlayerCapability() in HbmCapabilities.java"
     */
    @Deprecated(forRemoval = true, since = "1.8.21")
    private static void updateEntityContamination(Level world, boolean updateData) {

//        System.err.println("updating contamination");
        if(world != null && !world.isClientSide /* && GeneralConfig.enableRads */ ) {

            Set<ChunkPos> processedChunks = new HashSet<>();

            for (Player playerI : world.players()) {
                ChunkPos playerChunkPos = new ChunkPos(playerI.blockPosition());
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        ChunkPos chunkPos = new ChunkPos(playerChunkPos.x + dx, playerChunkPos.z + dz);
                        if (processedChunks.add(chunkPos) && world.hasChunk(chunkPos.x, chunkPos.z)) {
                            LevelChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
                            AABB chunkAABB = getAabb(world, chunkPos);
                            List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, chunkAABB);

                            for (Entity e : entities) {
                                // Apply contamination logic to each entity (but of course, FIGURE OUT GIVING MOBS FUCKING NBT TAGS AAHHHHHAIWHUDHAWIUDHAWIDUYHAWD)

                                if(e instanceof LivingEntity entity) {

                                    ContaminationUtil.setEntityENV(entity, world);

                                    if (entity instanceof ServerPlayer player) {


                                        double receivedRadiation = ContaminationUtil.getNoNeutronPlayerRads(player)*0.00004D-(0.00004D * 20); //RadiationConfig.neutronActivationThreshold (20Rad/s default)

//                                        System.out.println("[Debug] received Radiation: " + receivedRadiation);

                                        float neutronRads = getPlayerNeutronRads(player);

//                                        System.out.println("[Debug] neutron radiation: " + neutronRads);

                                        if(neutronRads > 0) {

//                                            System.out.println("[Debug] neutron rads are higher than 0, contaminating...");

                                            ContaminationUtil.contaminate(player, ContaminationUtil.HazardType.NEUTRON, ContaminationUtil.ContaminationType.CREATIVE, neutronRads * 0.05f);
                                        }
                                        else {

//                                            System.out.println("[Debug] neutron radiation is less than or equal to 0, setting it to 0");

                                            HbmCapabilities.getData(player).setValue(Type.NEUTRON, 0);
                                        }
                                        if(receivedRadiation > 0.0012f) {

//                                            System.out.println("[Debug] Received radiation is greater than minimum value, neutron activating players inventory...");

                                            ContaminationUtil.neutronActivateInventory(player, (float)receivedRadiation, 1f);
                                            player.containerMenu.broadcastChanges();
                                        }

                                        if (player.isCreative() || player.isSpectator())
                                            continue;

                                    }

                                    int blacklung = (int)HbmCapabilities.getData(entity).getValue(Type.BLACKLUNG);

//                                    if(blacklung > 0)
//                                        System.out.println("[Debug] Blacklung = " + blacklung);

                                    if(blacklung < maxBlacklung*0.25) {

                                        ContaminationUtil.contaminate(entity, ContaminationUtil.HazardType.COAL, ContaminationUtil.ContaminationType.CREATIVE, -1);

                                    }
                                    else {

                                        // cough at random

//                                        if(world.random.nextInt(100) == 0)
//                                            System.out.println("[Debug] yippee blacklung cough!!");

                                        if(blacklung > maxBlacklung*0.5) {

                                            // cough soot particles at random

                                            if (world.random.nextInt(300) == 0)
                                                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10 * 20, 2, true, false));
                                        }
                                        if(blacklung > maxBlacklung*0.75) {

                                            // cough bloody soot particles at random

                                            if (world.random.nextInt(300) == 0)
                                                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10 * 20, 3, true, false));
                                            if (world.random.nextInt(300) == 0)
                                                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 8 * 20, 2, true, false));
                                        }
                                        if(blacklung > maxBlacklung*0.9) {

                                            // cough bloody soot particles at random

                                            if (world.random.nextInt(300) == 0)
                                                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10 * 20, 3, true, false));
                                            if (world.random.nextInt(300) == 0)
                                                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 10 * 20, 3, true, false));
                                        }
                                        if(blacklung > maxBlacklung) {

                                            // cough bloody soot particles at random

                                            entity.hurt(RegisterDamageSources.BLACKLUNG, 1000f);

                                            if (world.random.nextInt(300) == 0)
                                                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10 * 20, 3, true, false));
                                            if (world.random.nextInt(300) == 0)
                                                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 10 * 20, 3, true, false));
                                        }
                                    }

                                    double eRad = (double) HbmCapabilities.getData(entity).getValue(Type.RADIATION);

                                    if(eRad >= 200 &&
                                            entity.getHealth() > 0 &&
                                            entity instanceof Creeper creeper) {

                                        if(world.random.nextInt(3) == 0 && creeper != null) {

                                            replace(creeper, new NuclearCreeperEntity(HbmEntities.NUCLEAR_CREEPER.get(), world), world);
                                        } else {
                                            try {

                                                if(creeper != null)
                                                    creeper.hurt(RegisterDamageSources.RADIATION, 100f);
                                                // grant achievement "wait, what?"
                                            } catch (Exception ignored) { System.err.println("client had a packet error! e: " + ignored.getCause()); }
                                        }
                                        continue;
                                    }
                                    else if(eRad >= 500 &&
                                            entity instanceof Cow cow &&
                                            !(entities instanceof MushroomCow)) {

                                        replace(cow, new MushroomCow(EntityType.MOOSHROOM, world), world);

                                        continue;
                                    }
                                    else if(eRad >= 600 &&
                                            entity instanceof Villager vill) {

                                        replace(vill, new ZombieVillager(EntityType.ZOMBIE_VILLAGER, world), world);

                                        continue;
                                    }
//                                    else if(eRad >= 700 &&
//                                            entity instanceof Blaze blaze) {
//
//                                        replace(blaze, new RADBeast(HbmEntities.RAD_BEAST.get(), world), world);
//
//                                        continue;
//                                    }
                                    else if(eRad >= 800 &&
                                            entity instanceof Horse horse) {

                                        ZombieHorse zom = new ZombieHorse(EntityType.ZOMBIE_HORSE, world);
                                        zom.setAge(horse.getAge());
                                        zom.setTemper(horse.getTemper());
                                        if(horse.isSaddled())
                                            zom.equipSaddle(null);
                                        zom.setTamed(horse.isTamed());
                                        zom.setOwnerUUID(horse.getOwnerUUID());
                                        zom.makeMad();
                                        replace(horse, zom, world);

                                        continue;
                                    }
//                                    else if(eRad >= 900 &&
//                                            entity instanceof Duck duck) {
//
//                                        replace(duck, new Quackos(HbmEntities.QUACKOS, world), world);
//
//                                        continue;
//                                    }

                                    HbmCapabilities.getData(entity).addValue(Type.PERMANENT_CONTAMINATION, (float)(eRad/1000000f));
                                    HbmCapabilities.getData(entity).syncLivingVariables(entity);

                                    if (eRad > 1000000)
                                        HbmCapabilities.getData(entity).setValue(Type.RADIATION, 1000000); // grant achievement "HOW"

                                    double perm_con = HbmCapabilities.getData(entity).getValue(Type.PERMANENT_CONTAMINATION);

                                    HbmCapabilities.getData(entity).addValue(Type.INTERNAL_DAMAGE, perm_con/200000f);
                                    HbmCapabilities.getData(entity).syncLivingVariables(entity);

                                    double internal_dam = HbmCapabilities.getData(entity).getValue(Type.INTERNAL_DAMAGE);

                                    if(internal_dam >= 100) {
//                                        System.out.println("[Debug] internal damage is greater than or equal to: " + 100);

                                        try {

                                            entity.hurt(RegisterDamageSources.RADIATION, 1000f);
                                            // grant achievement "wait, what?"
                                        } catch (Exception ignored) { System.err.println("client had a packet error!"); }
                                        // Grant achievement, "Ouch, Radiation!"
                                    }
                                    else if (internal_dam >= 80) {
//                                        System.out.println("[Debug] internal damage is greater than or equal to: " + 80);

                                        if (world.random.nextInt(300) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 5 * 30, 0, true, false));
                                        if (world.random.nextInt(300) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10 * 20, 2, true, false));
                                        if (world.random.nextInt(300) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10 * 20, 2, true, false));
                                        if (world.random.nextInt(300) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 3 * 20, 2, true, false));
                                        if (world.random.nextInt(300) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 3 * 20, 1, true, false));
                                        if (world.random.nextInt(300) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 5 * 20, 3, true, false));
                                        if (world.random.nextInt(300) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 5 * 20, 3, true, false));
                                    }
                                    else if (internal_dam >= 60) {
//                                        System.out.println("[Debug] internal damage is greater than or equal to: " + 60);

                                        if (world.random.nextInt(300) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 5 * 30, 0, true, false));
                                        if (world.random.nextInt(300) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10 * 20, 2, true, false));
                                        if (world.random.nextInt(300) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10 * 20, 2, true, false));
                                        if (world.random.nextInt(500) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 3 * 20, 1, true, false));
                                        if (world.random.nextInt(300) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 5 * 20, 3, true, false));
                                        if (world.random.nextInt(400) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 6 * 20, 2, true, false));
                                    }
                                    else if (internal_dam >= 40) {
//                                        System.out.println("[Debug] internal damage is greater than or equal to: " + 40);

                                        if (world.random.nextInt(300) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 5 * 30, 0, true, false));
                                        if (world.random.nextInt(500) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5 * 20, 0, true, false));
                                        if (world.random.nextInt(300) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 5 * 20, 1, true, false));
                                        if (world.random.nextInt(500) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 3 * 20, 2, true, false));
                                        if (world.random.nextInt(600) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 4 * 20, 1, true, false));
                                    }
                                    else if (internal_dam >= 20) {
//                                        System.out.println("[Debug] internal damage is greater than or equal to: " + 20);

                                        if (world.random.nextInt(300) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 5 * 20, 0, true, false));
                                        if (world.random.nextInt(500) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 5 * 20, 0, true, false));
                                        if (world.random.nextInt(700) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 3 * 20, 2, true, false));
                                        if (world.random.nextInt(800) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 4 * 20, 0, true, false));
                                    }
                                    else if (internal_dam >= 10) {
//                                        System.out.println("[Debug] internal damage is greater than or equal to: " + 10);

                                        if (world.random.nextInt(800) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 2 * 20, 0, true, false));
                                        if (world.random.nextInt(1000) == 0)
                                            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1 * 20, 0, true, false));

                                        // Grant achievement, "Yay, Radiation!"
                                        if(entity instanceof ServerPlayer player)
                                            AdvancementManager.grant(player, "rad_poison");
                                    }

                                    AttributeInstance hp = entity.getAttribute(Attributes.MAX_HEALTH);

                                    int oldRoundedDam = (int)HbmCapabilities.getData(entity).getValue(Type.OLD_ROUNDED_DAMAGE);

                                    int roundedDam = (int)Math.round(internal_dam);

//                                    boolean maxHealthChanged;

                                    if (hp != null) {

                                        if(roundedDam != oldRoundedDam) {

                                            float currMax = entity.getMaxHealth();
                                            float ogMax = (float)HbmCapabilities.getData(entity).getValue(Type.OLD_MAX_HEALTH);

                                            float newMax = switch(roundedDam) {

                                                case 100 -> 1;
                                                case 95, 90,
                                                     85, 80,
                                                     75, 70,
                                                     65, 60,
                                                     55, 50,
                                                     45, 40,
                                                     35, 30,
                                                     25, 20,
                                                     15, 10,
                                                     5   -> ogMax*(1f - (roundedDam/100f));
                                                default  -> currMax;
                                            };
                                            hp.setBaseValue(newMax);

                                            // clamp current health to new max so the entity doesn't have > max HP
                                            if (entity.getHealth() > newMax) {
                                                entity.setHealth(newMax);
                                            }

                                            HbmCapabilities.getData(entity).setValue(Type.OLD_ROUNDED_DAMAGE, roundedDam);
                                            HbmCapabilities.getData(entity).syncLivingVariables(entity);

//                                            maxHealthChanged = true;

//                                            if(entity instanceof ServerPlayer)
//                                                System.out.println("\n================================================================================"
//                                                        + "\ncontamination = " + eRad + ", permanent contamination = " + perm_con + ", internal damage = " + internal_dam
//                                                        + "\nrounded internal damage = " + roundedDam + ", old rounded internal damage = " + oldRoundedDam + ", max health changed = " + maxHealthChanged
//                                                        + "\ncurrent max health = " + currMax + ", new max health = " + newMax + ", entity.getHealth() = " + entity.getHealth() + ", og max health = " + ogMax
//                                                        + "\n================================================================================");
                                        }

//                                        if(entity instanceof ServerPlayer)
//                                            System.out.println("\n================================================================================"
//                                                    + "\ncontamination = " + eRad + ", permanent contamination = " + perm_con + ", internal damage = " + internal_dam
//                                                    + "\nrounded internal damage = " + roundedDam + ", old rounded internal damage = " + oldRoundedDam
//                                                    + "\nentity.getHealth() = " + entity.getHealth()
//                                                    + "\n================================================================================");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void replace(LivingEntity oldEntity, Mob newEntity, Level level) {

        if(level.isClientSide)
            return;

        if(oldEntity instanceof Villager vill &&
                newEntity instanceof ZombieVillager zom) {

            zom.setVillagerData(vill.getVillagerData());

            zom.setBaby(vill.isBaby());
        }

        newEntity.moveTo(oldEntity.position());

        newEntity.setYHeadRot(oldEntity.getYHeadRot());
        newEntity.setYRot(oldEntity.getYRot());
        newEntity.setXRot(oldEntity.getXRot());

        newEntity.setCustomName(oldEntity.getCustomName());
        newEntity.setCustomNameVisible(oldEntity.isCustomNameVisible());
        newEntity.setPersistenceRequired();

        for(MobEffectInstance instance : oldEntity.getActiveEffects())
            newEntity.addEffect(instance);

        if(!oldEntity.isDeadOrDying())
            if(!level.isClientSide())
                level.addFreshEntity(newEntity);

        oldEntity.discard();
    }

//    public static void updateRadiation() {
//        long time = System.currentTimeMillis();
//
//        for(WorldRadiationData w : worldMap.values()){
//
//            List<RadPocket> itrActive = new ArrayList<>(w.getActivePockets());
//            Iterator<RadPocket> itr = itrActive.iterator();
//            while(itr.hasNext()){
//                RadPocket p = itr.next();
//                BlockPos pos = p.parent.parent.getWorldPos(p.parent.yLevel);
//                ChunkPos chunkPos = p.parent.parent.chunk.getPos();
//                ServerLevel sLevel = (ServerLevel) w.pLevel;
//                LevelChunk chunk = sLevel.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
//                if(chunk == null || sLevel.getPlayers(player -> {
//                    BlockPos playerPos = player.blockPosition();
//                    int dx = (playerPos.getX() >> 4) - chunkPos.x;
//                    int dz = (playerPos.getZ() >> 4) - chunkPos.z;
//                    return dx * dx + dz * dz <= 64;
//                }).isEmpty()) {
//                    sLevel.setChunkForced(chunkPos.x, chunkPos.z, false);
//                }
//                p.radiation *= 0.999F;
//                p.radiation -= 0.05F;
//                p.parent.parent.chunk.setUnsaved(true);
//                if(p.radiation <= 0){
//                    p.radiation = 0;
//                    p.accumulatedRads = 0;
//                    itr.remove();
//                    p.parent.parent.chunk.setUnsaved(true);
//                    continue;
//                }
//
//                // radioactive fog spawning goes here vvv
//                if(w.pLevel.random.nextInt(10) == 0){
//                    for (int i = 0; i < 10; i++) {
//                        BlockPos randPos = new BlockPos(
//                                w.pLevel.random.nextInt(16),
//                                w.pLevel.random.nextInt(16),
//                                w.pLevel.random.nextInt(16)
//                        );
//
//                        if (p.parent.pocketsByBlock == null || p.parent.pocketsByBlock[
//                                randPos.getX() * 16 * 16 + randPos.getY() * 16 + randPos.getZ()] == p) {
//
//                            randPos = randPos.offset(p.parent.parent.getWorldPos(p.parent.yLevel));
//                            BlockState state = w.pLevel.getBlockState(randPos);
//
//                            Vec3 rPos = new Vec3(
//                                    randPos.getX() + 0.5,
//                                    randPos.getY() + 0.5,
//                                    randPos.getZ() + 0.5
//                            );
//
//                            BlockHitResult trace = w.pLevel.clip(new ClipContext(
//                                    rPos,
//                                    rPos.add(0, -6, 0),
//                                    ClipContext.Block.COLLIDER,
//                                    ClipContext.Fluid.NONE,
//                                    null
//                            ));
//
//                            if (state.isAir() && trace.getType() == HitResult.Type.BLOCK) {
//                                w.pLevel.addParticle(ParticleTypes.MYCELIUM, randPos.getX(), randPos.getY(), randPos.getZ(), 0.0, 0.0, 0.0);
//                                break;
//                            }
//                        }
//                    }
//                }
//                // ^^^                                ^^^
//
//                float count = 0;
//                for(Direction e : Direction.values()){
//                    count += p.connectionIndicies[e.ordinal()].size();
//                }
//                float amountPer = 0.7F / count;
//                if(count == 0 || p.radiation < 1){
//                    amountPer = 0;
//                }
//
//                if(p.radiation > 0 && amountPer > 0){
//                    for(Direction e : Direction.values()){
//                        BlockPos nPos = pos.relative(e, 16);
//
//                        if(!p.parent.parent.chunk.getLevel().hasChunkAt(nPos) || nPos.getY() < -64 || nPos.getY() > 320)
//                            continue;
//
//                        if(p.connectionIndicies[e.ordinal()].size() == 1 && p.connectionIndicies[e.ordinal()].get(0) == -1) {
//                            rebuildChunkPockets(p.parent.parent.chunk.getLevel().getChunkAt(nPos), (nPos.getY()) >> 4);
//                        }
//                        else {
//                            SubChunkRadiationStorage sc2 = getSubChunkStorage(p.parent.parent.chunk.getLevel(), nPos);
//                            for(int idx : p.connectionIndicies[e.ordinal()]){
//                                if(!sc2.pockets[idx].isSealed()){
//                                    sc2.pockets[idx].accumulatedRads += p.radiation * amountPer;
//                                    w.addActivePocket(sc2.pockets[idx]);
//                                }
//                            }
//                        }
//                    }
//                }
//                if(amountPer != 0) {
//                    p.accumulatedRads += p.radiation * 0.3F;
//                }
//                if(System.currentTimeMillis() - time > 20) {
//                    break;
//                }
//            }
//
//            List<RadPocket> itrActiveCheck = new ArrayList<>(w.getActivePockets());
//            itr = itrActiveCheck.iterator();
//            while(itr.hasNext()){
//                RadPocket act = itr.next();
//                act.radiation = act.accumulatedRads;
//                act.accumulatedRads = 0;
//                if(act.radiation <= 0){
//                    w.removeActivePocket(act);
//                    itr.remove();
//                }
//            }
//        }
//
//        if(System.currentTimeMillis() - time > 50){
//            System.out.println("Rads took too long: " + (System.currentTimeMillis() - time));
//        }
//    }
//

    private static int worldDestructionCooldown = 0;
    private static final int TICKS_PER_SECOND = 20;
    private static final int MIN_SECONDS_BETWEEN_EVENTS = 0;
    private static final int MAX_SECONDS_BETWEEN_EVENTS = 4;

//    private static int updateEntityCooldown = 0;

    @SubscribeEvent
    public static void onWorldUpdate(TickEvent.LevelTickEvent event) {
//        if(GeneralConfig.enableDebugMode) {
//            MainRegistry.logger.info("[Debug] onWorldUpdate called for RadSys tick " + ticks);
//        }

        boolean allowUpdate = (event.phase == TickEvent.Phase.START);

        if (allowUpdate) {
            // Make the world stinky
            // Countdown & trigger random world radiation effects
            if (--worldDestructionCooldown <= 0) {
                RadiationWorldHandler.handleWorldDestruction(event.level);

                // Reset cooldown: random seconds * 20 ticks/second
                int delaySeconds = event.level.random.nextInt(
                        MAX_SECONDS_BETWEEN_EVENTS - MIN_SECONDS_BETWEEN_EVENTS + 1
                ) + MIN_SECONDS_BETWEEN_EVENTS;

                worldDestructionCooldown = delaySeconds * TICKS_PER_SECOND;

//                System.out.println("[Debug] Scheduled next world destruction in " + delaySeconds + "s");
            }
        }

//        if(--updateEntityCooldown <= 0) {

            // Make entities stinky
            if(!event.level.isClientSide())
                updateEntityContamination(event.level, allowUpdate);

//            updateEntityCooldown = 10;
//        }
    }

    public static @NotNull AABB getAabb(Level world, ChunkPos chunkPos) {
        int chunkStartX = chunkPos.getMinBlockX();
        int chunkStartZ = chunkPos.getMinBlockZ();
        int chunkEndX = chunkStartX + 15;
        int chunkEndZ = chunkStartZ + 15;
        int worldMinY = world.getMinBuildHeight();
        int worldMaxY = world.getMaxBuildHeight();

        return new AABB(chunkStartX, worldMinY, chunkStartZ, chunkEndX + 1, worldMaxY, chunkEndZ + 1);
    }

}
