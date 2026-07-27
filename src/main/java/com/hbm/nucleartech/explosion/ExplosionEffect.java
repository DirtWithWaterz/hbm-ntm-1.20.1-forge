package com.hbm.nucleartech.explosion;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ExplosionEffect {

    public enum EffectShape { //Normal - same as explosion, 1/weight chance of placing
        SPHERE,               //Hollow - ring, -edge = inside and outside, +edge = inside smooth
        DISTANCE_SPHERE,      //distance - weight = radius, edge = transition
        HOLLOW_SPHERE,        //Normal - 2 - inside, 1 - irr edge, 0 - outside
        ELLIPSOID,            //Distance - layers of irr hollow shapes
        DISTACNE_ELLIPSOID,   //Hollow - 4 - inside, 3 - in edge, 2 - perfect, 1 - out edge, 0 - outside
        HOLLOW_ELLIPSOID,
        CUBOID,
        DISTANCE_CUBOID,
        HOLLOW_CUBOID,
        CYLINDER,
        DISTANCE_CYLINDER,
        HOLLOW_CYLINDER
    }

    private static final Random rng;

    static { rng = new Random(); }

    private static void placeNonFloatingBlockInSection(LevelChunk chunk, int secIndex, BlockState block) {
        LevelChunkSection section = chunk.getSection(secIndex);
        LevelChunkSection sectionBelow = null;
        if (secIndex > 0)
            sectionBelow = chunk.getSection(secIndex-1);
        PalettedContainer<BlockState> states = section.states;
        if (sectionBelow != null) {
            for (int blockX = 0; blockX < 16; blockX++) {
                for (int blockZ = 0; blockZ < 16; blockZ++) {
                    if (states.get(blockX, 0, blockZ) == VeryFastRaycastedExplosion.AirState && sectionBelow.states.get(blockX, 15, blockZ).isSolid()) {
                        states.getAndSetUnchecked(blockX, 0, blockZ, block);
                    }
                }
            }
        }
        for (int blockX = 0; blockX < 16; blockX++) {
            for (int blockY = 1; blockY < 16; blockY++) {
                for (int blockZ = 0; blockZ < 16; blockZ++) {
                    if (states.get(blockX, 0, blockZ) == VeryFastRaycastedExplosion.AirState && states.get(blockX, blockY-1, blockZ).isSolid()) {
                        states.getAndSetUnchecked(blockX, blockY, blockZ, block);
                    }
                }
            }
        }
    }

    private static void placeNonFloatingBlockInSectionEdge(LevelChunk chunk, int basedx, int basedy, int basedz, int secIndex,
                                                           BlockState block, int minradx, int minrady, int radx, int rady, int edge, EffectShape shape) {
        LevelChunkSection section = chunk.getSection(secIndex);
        LevelChunkSection sectionBelow = null;
        if (secIndex > 0)
            sectionBelow = chunk.getSection(secIndex-1);
        PalettedContainer<BlockState> states = section.states;
        int inside, distSq, dx, dy, dz, radxSq = radx*radx;
        int minradxSq = (radx-edge)*(radx-edge);
        int edgeSq = (radxSq-minradxSq);
        if (sectionBelow != null) {
            for (int blockX = 0; blockX < 16; blockX++) {
                dx = basedx + blockX;
                for (int blockZ = 0; blockZ < 16; blockZ++) {
                    dz = basedz + blockZ;
                    distSq = (int)ShapeCalculator.distanceSq(dx, basedy, dz, radx, rady, shape);
                    inside = ShapeCalculator.isInRangeEffect(basedx+blockX, basedy, basedz, minradx, minrady, radx, rady, edge, shape);
                    if (inside == 2 || inside == 1 && rng.nextInt(edgeSq) <= radxSq-distSq) {
                        if (states.get(blockX, 0, blockZ) == VeryFastRaycastedExplosion.AirState && sectionBelow.states.get(blockX, 15, blockZ).isSolid()) {
                            states.getAndSetUnchecked(blockX, 0, blockZ, block);
                        }
                    }
                }
            }
        }
        for (int blockX = 0; blockX < 16; blockX++) {
            for (int blockY = 1; blockY < 16; blockY++) {
                for (int blockZ = 0; blockZ < 16; blockZ++) {
                    if (states.get(blockX, blockY, blockZ) == VeryFastRaycastedExplosion.AirState && states.get(blockX, blockY-1, blockZ).isSolid()) {
                        states.getAndSetUnchecked(blockX, blockY, blockZ, block);
                    }
                }
            }
        }
    }


    private static void placeNonFloatingBlocksInSection(LevelChunk chunk, int secIndex, List<BlockState> blocks, int[] cumulweight) {
        LevelChunkSection section = chunk.getSection(secIndex);
        LevelChunkSection sectionBelow = null;
        if (secIndex > 0)
            sectionBelow = chunk.getSection(secIndex-1);
        PalettedContainer<BlockState> states = section.states;
        if (sectionBelow != null) {
            for (int blockX = 0; blockX < 16; blockX++) {
                for (int blockZ = 0; blockZ < 16; blockZ++) {
                    if (states.get(blockX, 0, blockZ) == VeryFastRaycastedExplosion.AirState && sectionBelow.states.get(blockX, 15, blockZ).isSolid()) {
                        states.getAndSetUnchecked(blockX, 0, blockZ, blocks.get(0));
                    }
                }
            }
        }
        for (int blockX = 0; blockX < 16; blockX++) {
            for (int blockY = 1; blockY < 16; blockY++) {
                for (int blockZ = 0; blockZ < 16; blockZ++) {
                    if (states.get(blockX, 0, blockZ) == VeryFastRaycastedExplosion.AirState && states.get(blockX, blockY-1, blockZ).isSolid()) {
                        states.getAndSetUnchecked(blockX, blockY, blockZ, blocks.get(0));
                    }
                }
            }
        }
    }

    public static void placeBlocksInSection(LevelChunkSection section, long secId, List<BlockState> blocks, int[] cumulWeight, List<BlockState> replaceBlocks) {
        PalettedContainer<BlockState> states = section.states;
        rng.setSeed(secId);
        BlockState block = null;
        if (blocks.size() == 1)
            block = blocks.get(0);
        if (block != null) {
            for (int blockX = 0; blockX < 16; blockX++) {
                for (int blockY = 0; blockY < 16; blockY++) {
                    for (int blockZ = 0; blockZ < 16; blockZ++) {
                        if (replaceBlocks.contains(states.get(blockX, blockY, blockZ))) { //if only 1 blockstate
                            states.getAndSetUnchecked(blockX, blockY, blockZ, block);
                        }
                    }
                }
            }
        }
        else {
            for (int blockX = 0; blockX < 16; blockX++) {
                for (int blockY = 0; blockY < 16; blockY++) {
                    for (int blockZ = 0; blockZ < 16; blockZ++) {
                        if (replaceBlocks.contains(states.get(blockX, blockY, blockZ))) {
                            block = blocks.get(cumulWeight[rng.nextInt(cumulWeight.length)]);
                            states.getAndSetUnchecked(blockX, blockY, blockZ, block);
                        }
                    }
                }
            }
        }
    }

    public static void placeBlockInSection(LevelChunkSection section, BlockState block, float chance, List<BlockState> replaceBlocks) {
        PalettedContainer<BlockState> states = section.states;
        Palette<BlockState> palette = states.data.palette;
        boolean hasReplaceStates = false;
        List<BlockState> allStates;
        if (palette instanceof LinearPalette linearPalette) {
            for (int i = 0; i < linearPalette.getSize(); i++) {
                if (linearPalette.values[i] instanceof BlockState state) {
                    if (replaceBlocks.contains(state)) {
                        hasReplaceStates = true;
                        break;
                    }
                }
            }
        } else if (palette instanceof HashMapPalette hashPalette) {
            allStates = hashPalette.getEntries();
            for (BlockState state : allStates) {
                if (replaceBlocks.contains(state)) {
                    hasReplaceStates = true;
                    break;
                }
            }
        }
        if (!hasReplaceStates) return;
        for (int blockX = 0; blockX < 16; blockX++) {
            for (int blockY = 0; blockY < 16; blockY++) {
                for (int blockZ = 0; blockZ < 16; blockZ++) {
                    if (replaceBlocks.contains(states.get(blockX, blockY, blockZ)) && rng.nextFloat() <= chance) {
                        states.getAndSetUnchecked(blockX, blockY, blockZ, block);
                    }
                }
            }
        }
    }

    public static void createEffect(ServerLevel level, int x, int y, int z, int minradx, int minrady, int radx, int rady, int edge, EffectShape shape,
                                                BlockState state, int weight, List<BlockState> replaceStates, boolean floating, boolean replaceAllSolid) {
        List<BlockState> states = Collections.singletonList(state);
        int[] weights = new int[] { weight };
        createEffect(level, x, y, z, minradx, minrady, radx, rady, edge, shape, states, weights, replaceStates, floating, replaceAllSolid);
    }

    public static void createEffect(ServerLevel level, int x, int y, int z, int minradx, int minrady, int radx, int rady, int edge, EffectShape shape,
                                                 List<BlockState> states, int[] weight, List<BlockState> replaceStates, boolean floating, boolean replaceAllSolid) {
        int total = 0;
        int min = Integer.MAX_VALUE;
        for (int j : weight)     // weight = [ 3, 2, 1 ] -> cumulWeight = [ 0, 0, 0, 1, 1, 2 ]
            total += j;
        int[] cumulWeight = new int[total];
        int index = 0;
        for (int i = 0; i < weight.length; i++) {
            for (int j = 0; j < weight[i]; j++) {
                cumulWeight[index] = i;
                index++;
            }
            if (weight[i] < min)
                min = weight[i];
        }
        int radiicount;
        if (shape.ordinal() % 3 == 0) //normal
            radiicount = 2;
        else if (shape.ordinal() % 3 == 1) //distance
            radiicount = states.size()*2;
        else //hollow
            radiicount = 4;
        int[] radiiw = new int[radiicount]; //radii squared
        int[] radiih = new int[radiicount];
        if (shape.ordinal() % 3 == 0) { //normal
            radiiw[0] = radx-edge;
            radiiw[1] = radx;
            radiih[0] = rady-edge;
            radiih[1] = rady;
        }
        else if (shape.ordinal() % 3 == 1) { //distance
            if (edge > Math.min(min*radx/total, min*rady/total)) //ensure edge is smaller than smallest section
                edge = Math.min(min*radx/total, min*rady/total);
            for (int i = 0; i < weight.length; i++) {
                radiiw[2*i] = (weight[i]*radx/total-edge/2)*(weight[i]*radx/total-edge/2); // [3, 2, 1] -> 3*50/6=25 base, +/- edge
                radiiw[2*i+1] = (weight[i]*radx/total+edge/2)*(weight[i]*radx/total+edge/2);
                radiih[2*i] = (weight[i]*rady/total-edge/2)*(weight[i]*rady/total-edge/2);
                radiih[2*i+1] = (weight[i]*rady/total+edge/2)*(weight[i]*rady/total+edge/2);
            }
            radiiw[2*weight.length-1] = (weight[weight.length-1]*radx/total)*(weight[weight.length-1]*radx/total);
            radiih[2*weight.length-1] = (weight[weight.length-1]*rady/total)*(weight[weight.length-1]*rady/total);
        }
        else { //hollow
            radiiw[0] = minradx;
            radiiw[1] = minradx - Math.min(edge, 0);  //inner edge only if negative, otherwise 0  30-(-5)=35, 30-0=30
            radiiw[2] = radx - Math.abs(edge);  //radius - edge -> 50-5=45
            radiiw[3] = radx;
            radiih[0] = minrady;
            radiih[1] = minrady - Math.min(edge, 0);  //inner edge only if negative, otherwise 0  30-(-5)=35, 30-0=30
            radiih[2] = rady - Math.abs(edge);  //radius - edge -> 50-5=45
            radiih[3] = rady;
        }

        ServerChunkCache cache = level.getChunkSource();
        int minSec = level.getMinSection();
        int minX, maxX, minY, maxY, minZ, maxZ;
        minX = x - radx;
        maxX = x + radx;
        minY = y - rady;
        maxY = y + rady;
        minZ = z - radx;
        maxZ = z + radx;
        if (shape == EffectShape.CYLINDER || shape == EffectShape.CUBOID || shape == EffectShape.HOLLOW_CYLINDER
                || shape == EffectShape.HOLLOW_CUBOID || shape == EffectShape.DISTANCE_CYLINDER || shape == EffectShape.DISTANCE_CUBOID) {
            minY = y - rady/2;
            maxY = y + (rady-1)/2;
        }
        if (shape == EffectShape.CUBOID || shape == EffectShape.HOLLOW_CUBOID || shape == EffectShape.DISTANCE_CUBOID) {
            minX = x - radx/2;
            maxX = x + (radx-1)/2;
            minZ = z - radx/2;
            maxZ = z + (radx-1)/2;
        }
        int minIndex = Math.max(0, (minY >> 4) - minSec);
        int maxIndex = Math.min(level.getMaxBuildHeight() - level.getMinBuildHeight() >> 4, (maxY >> 4) - minSec + 1);
        int maxXChunk = maxX >> 4;
        int maxZChunk = maxZ >> 4;
        int xinsec = x&15;
        int yinsec = y&15;
        int zinsec = z&15;
        int ccx = x >> 4;
        int ccy = y >> 4;
        int ccz = z >> 4;
        ChunkMap chunkMap = cache.chunkMap;
        for (int chunkX = minX >> 4; chunkX <= maxXChunk; chunkX++) {

            for (int chunkZ = minZ >> 4; chunkZ <= maxZChunk; chunkZ++) {
                LevelChunk chunk = cache.getChunkNow(chunkX, chunkZ);

                if (chunk == null) { //not in cache
                    long chunkLong = VeryFastRaycastedExplosion.asLongChunk(chunkX, chunkZ);
                    ChunkHolder holder = chunkMap.getUpdatingChunkIfPresent(chunkLong);
                    if (holder != null && holder.getLastAvailableStatus() == ChunkStatus.FULL) { //try again
                        chunk = (LevelChunk)(holder.getFutureIfPresentUnchecked(ChunkStatus.FULL).getNow(null)).left().orElse(null);
                    }
                    if (chunk == null) {
                        continue;
                    }
                }

                LevelChunkSection[] sections = chunk.getSections();
                long longID = VeryFastRaycastedExplosion.asLong(chunkX, minSec, chunkZ);
                if (edge > 0) rng.setSeed(longID);
                for (int sectionIndex = minIndex; sectionIndex < maxIndex; sectionIndex++) {
                    LevelChunkSection section = sections[sectionIndex];
                    if (!section.hasOnlyAir()) {
                        int sectionY = sectionIndex + minSec;
                        int inside = ShapeCalculator.secInRangeEffect(chunkX-ccx, sectionY-ccy, chunkZ-ccz, xinsec, yinsec, zinsec, minradx, minrady, radx, rady, edge, shape);
                        longID = VeryFastRaycastedExplosion.asLongY(longID, sectionY);
                        if (inside > 0) {

                        }
                    }
                }
                chunk.setUnsaved(true);
            }
        }
        short shap = (short)shape.ordinal();
        //SuperTNTPacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new ClearShapePack(x, y, z, maxres, radx, rady, edge, shap));
    }
}
