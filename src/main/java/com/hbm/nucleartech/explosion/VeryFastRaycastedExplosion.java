package com.hbm.nucleartech.explosion;

import com.hbm.nucleartech.block.RegisterBlocks;
import com.hbm.nucleartech.entity.effects.NukeTorexEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import com.hbm.nucleartech.network.packet.ClearMarkedChunkPack;
import com.hbm.nucleartech.network.packet.ClearSectionListPack;
import com.hbm.nucleartech.network.HbmPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.*;
import net.minecraft.util.BitStorage;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.util.ZeroBitStorage;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("deprecation")
public class VeryFastRaycastedExplosion {
	public enum Shape {
		SPHERE,
		ELLIPSOID,
		CUBOID,
		CYLINDER
	}


	private static ServerLevel level;
	private static int xx, yy ,zz;
	private final int xinsec, yinsec, zinsec;
	private static int edge;
	private final double edgeratio;
    private static int raybigradiusx;
	private static int raybigradiusy;
	private static int radiusx;
	private static int radiusy;
	private static int rayradiusx;
    private static Shape shape; //0 - sphere, 1 - ellipsoid, 2 - cuboid
	private static int powermltp;
	private static float dmgmltp;
	private static float knockbackmltp;
	private static float scale;
	private static BlockMarker marker;
	private static int minSec;
	private static int maxBuildHeight;
	private static int minBuildHeight;
	private final Long2ObjectOpenHashMap<DataLayer> skyMap;
	private static Long2ObjectOpenHashMap<DataLayer> blockMap;
	private static double x, y, z;
	private static int maxdistsqInRange;
	private static int mindistsqInRange;
	private static Entity ssource;
	private static final HashMap<BlockState, Long> blockResistanceMap = new HashMap<>();
	private final Long2ObjectOpenHashMap<Long[]> resCache = new Long2ObjectOpenHashMap<>();
	private int fireSmall = 0;
	private long initialLoss = 0;
	private long mbMarked = 0;

	public VeryFastRaycastedExplosion(Level Level, double X, double Y, double Z, int Edge, int maxradiusx, int maxradiusy, int Rayradiusx,
									  Shape Shape, float Knockbackmltp, int Powermltp, float Dmgmltp, @Nullable Entity source, float cloudScale) {
		level = (ServerLevel) Level;
		maxBuildHeight = level.getMaxBuildHeight();
		minBuildHeight = level.getMinBuildHeight();
		if (source != null) ssource = source;
		x = X;
		y = Y;
		z = Z;
		xx = (int) Math.floor(X);
		yy = (int) Math.floor(Y);
		zz = (int) Math.floor(Z);
		xinsec = xx & 15;
		yinsec = yy & 15;
		zinsec = zz & 15;
		scale = cloudScale;
		edge = Edge;
		radiusx = maxradiusx;
		radiusy = maxradiusy;
		rayradiusx = Rayradiusx;
		edgeratio = 1 - ((double)edge / (double)radiusx);
        int rayradiusy = rayradiusx * radiusy / radiusx;
        int maxbigradiusx = maxradiusx / 16;
		raybigradiusx = rayradiusx / 16;
		raybigradiusy = rayradiusy / 16;
		maxdistsqInRange = (rayradiusx) * (rayradiusx + 1);
		mindistsqInRange = (rayradiusx) * (rayradiusx - 2);
		shape = Shape;
		powermltp = Powermltp;
		dmgmltp = Dmgmltp;
		knockbackmltp = Knockbackmltp / 10;
		marker = new BlockMarker((maxbigradiusx +2)*(maxbigradiusx +2)*4, level.getSectionsCount());
		minSec = level.getMinSection();
		final ThreadedLevelLightEngine lightEngine = ((ServerLevel) level).getChunkSource().getLightEngine();
		LightEngine<?, ?> skyEngine = lightEngine.getLayerListener(LightLayer.SKY) instanceof LightEngine<?, ?> engine ? engine : null;
		skyMap = skyEngine != null ? skyEngine.storage.visibleSectionData.map : null;
		LightEngine<?, ?> blockEngine = lightEngine.getLayerListener(LightLayer.BLOCK) instanceof LightEngine<?, ?> engine ? engine : null;
		blockMap = blockEngine != null ? blockEngine.storage.visibleSectionData.map : null;
		long explosionResistance;
		long start = System.nanoTime();
		if (blockResistanceMap.isEmpty()) {
			Registry<Block> blockRegistry = BuiltInRegistries.BLOCK;
			for (Block block : blockRegistry) {
				StateDefinition<Block, BlockState> stateDefinition = block.getStateDefinition();
				for (BlockState blockState : stateDefinition.getPossibleStates()) {
					explosionResistance = (long) (block.getExplosionResistance() * 100);
					if (explosionResistance < 200) { //0-1.9 - dirt
						blockResistanceMap.put(blockState, explosionResistance);
						continue;
					} else if (explosionResistance < 1000) {//2-9.9 - stone
						explosionResistance /= 4;
						explosionResistance += 150;
					} else if (explosionResistance < 10000) {//10-99.9 - trial spawner
						explosionResistance /= 10;
						explosionResistance += 300;
					} else if (explosionResistance == 10000) {//100 - water, lava
						explosionResistance = 400;
					} else if (explosionResistance < 100000) {//100.1-999.9 - water, ender chest
						explosionResistance /= 20;
						explosionResistance += 800;
					} else { //1000+ - obsidian, netherite, bedrock
						explosionResistance /= 40;
						explosionResistance += 3300;
					}
					blockResistanceMap.put(blockState, explosionResistance);
				}
			}
		}
		System.out.println((System.nanoTime() - start)/1000 + "micros to get res, size: " + blockResistanceMap.size());

		ServerChunkCache cache = level.getChunkSource();
		ChunkMap chunkMap = cache.chunkMap;
		LevelChunk chunk;
		LevelChunkSection[] sections;
		LevelChunkSection section;
		Palette<BlockState> palette;
		int chunkZ, secIndex, secY, index;
		long chunkSecId, res;
		int maxChunkX = Math.min((xx + radiusx) >> 4, (xx + 512) >> 4)+1; //either edge of crater or edge of 32 chunk render dist, whichever smaller
		int minChunkX = Math.max((xx - radiusx) >> 4, (xx - 512) >> 4); //either edge of crater or edge of 32 chunk render dist, whichever smaller
		int maxChunkZ = Math.min((zz + radiusx) >> 4, (zz + 512) >> 4)+1;
		int minChunkZ = Math.max((zz - radiusx) >> 4, (zz - 512) >> 4);
		int maxSecIndex = Math.min((yy + radiusy - minBuildHeight) >> 4, level.getSectionsCount());
		int minSecIndex = Math.max((yy - radiusy - minBuildHeight) >> 4, 0);
		for (int chunkX = minChunkX; chunkX < maxChunkX; chunkX++) {
			for (chunkZ = minChunkZ; chunkZ < maxChunkZ; chunkZ++) {
				chunk = cache.getChunkNow(chunkX, chunkZ);
				if (chunk == null) { //not in cache
					long chunkLong = asLongChunk(chunkX, chunkZ);
					ChunkHolder holder = chunkMap.getUpdatingChunkIfPresent(chunkLong);
					if (holder != null && holder.getLastAvailableStatus() == ChunkStatus.FULL) { //try again
						chunk = (LevelChunk)(holder.getFutureIfPresentUnchecked(ChunkStatus.FULL).getNow(null)).left().orElse(null);
					}
					if (chunk == null) {
						continue;
					}
				}
				sections = chunk.getSections();
				for (secIndex = minSecIndex; secIndex < maxSecIndex; secIndex++) {
					Long[] sectionRes = new Long[4096];
					section = sections[secIndex];
					secY = secIndex + level.getMinSection();
					chunkSecId = asLong(chunkX, secY, chunkZ);
					if (!section.hasOnlyAir()) {
						palette = section.states.data.palette;
						if (palette instanceof SingleValuePalette<BlockState> singleValuePalette) {
							res = blockResistanceMap.get(singleValuePalette.value);
							Arrays.fill(sectionRes, res);
						} else {
							index = -1;
							for (int blockX = 0; blockX < 16; blockX++) {
								for (int blockY = 0; blockY < 16; blockY++) {
									for (int blockZ = 0; blockZ < 16; blockZ++) {
										index++;
										res = blockResistanceMap.get(section.states.get(blockX,blockY,blockZ));
										sectionRes[index] = res;
									}
								}
							}
						}
					}
					else {
						Arrays.fill(sectionRes, 0L);
					}
					resCache.put(chunkSecId, sectionRes);
				}
			}
		}
		System.out.println((System.nanoTime() - start)/1000000 + "ms to set res cache");

		explode();
	}



	public VeryFastRaycastedExplosion(Level level, double x, double y, double z, int edge, int maxradius, int rayradius, Shape shape, float knockmltp, int powermltp, float dmgmltp, @Nullable Entity source, float cloudScale) {
		this( level, x, y, z, edge, maxradius, maxradius, rayradius, shape, knockmltp, powermltp, dmgmltp, source, cloudScale);
	}

	public VeryFastRaycastedExplosion(Level level, double x, double y, double z, int edge, int maxradius, Shape shape, float knockmltp, int powermltp, float dmgmltp, @Nullable Entity source, float cloudScale) {
		this( level, x, y, z, edge, maxradius, maxradius, maxradius, shape, knockmltp, powermltp, dmgmltp, source, cloudScale);
	}

	public static final BlockState BedrockState = Blocks.BEDROCK.defaultBlockState();
	public static final BlockState AirState = Blocks.AIR.defaultBlockState();
	private static final long[] zero_data = new long[4096];
	private static final int[] MAGIC = new int[]{
			-1, -1, 0, Integer.MIN_VALUE, 0, 0, 1431655765, 1431655765, 0, Integer.MIN_VALUE,
			0, 1, 858993459, 858993459, 0, 715827882, 715827882, 0, 613566756, 613566756,
			0, Integer.MIN_VALUE, 0, 2, 477218588, 477218588, 0, 429496729, 429496729, 0,
			390451572, 390451572, 0, 357913941, 357913941, 0, 330382099, 330382099, 0, 306783378,
			306783378, 0, 286331153, 286331153, 0, Integer.MIN_VALUE, 0, 3, 252645135, 252645135,
			0, 238609294, 238609294, 0, 226050910, 226050910, 0, 214748364, 214748364, 0,
			204522252, 204522252, 0, 195225786, 195225786, 0, 186737708, 186737708, 0, 178956970,
			178956970, 0, 171798691, 171798691, 0, 165191049, 165191049, 0, 159072862, 159072862,
			0, 153391689, 153391689, 0, 148102320, 148102320, 0, 143165576, 143165576, 0,
			138547332, 138547332, 0, Integer.MIN_VALUE, 0, 4, 130150524, 130150524, 0, 126322567,
			126322567, 0, 122713351, 122713351, 0, 119304647, 119304647, 0, 116080197, 116080197,
			0, 113025455, 113025455, 0, 110127366, 110127366, 0, 107374182, 107374182, 0,
			104755299, 104755299, 0, 102261126, 102261126, 0, 99882960, 99882960, 0, 97612893,
			97612893, 0, 95443717, 95443717, 0, 93368854, 93368854, 0, 91382282, 91382282,
			0, 89478485, 89478485, 0, 87652393, 87652393, 0, 85899345, 85899345, 0,
			84215045, 84215045, 0, 82595524, 82595524, 0, 81037118, 81037118, 0, 79536431,
			79536431, 0, 78090314, 78090314, 0, 76695844, 76695844, 0, 75350303, 75350303,
			0, 74051160, 74051160, 0, 72796055, 72796055, 0, 71582788, 71582788, 0,
			70409299, 70409299, 0, 69273666, 69273666, 0, 68174084, 68174084, 0, Integer.MIN_VALUE,
			0, 5 };
	public static final MagicEntry[] other_magic = new MagicEntry[20];

	public static long asLong(int x, int y, int z) {
		return (long)(x & 4194303) << 42 | (long)(y & 1048575) | (long)(z & 4194303) << 20;
	}

	public static long asLongChunk(int x, int z) {
		return (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
	}

	public static long asLongY(long original, long newY) {
		return original & -1048576L | newY & 1048575L;
	}

	public static int hasValue(Palette<?> palette) {
		LinearPalette<?> lpalette = (LinearPalette<?>)palette;
		int id = -1;
		int size = palette.getSize();

		for (int i = 0; i < size; i++) {
			if (lpalette.values[i] == AirState) {
				return i;
			}
		}

		return id;
	}


	private void contaminateStone() {
		double extFactor = 1.1; // Plus a bit further (10%)
		int extRadiusX = (int) (radiusx * extFactor);
		int extRadiusY = (int) (radiusy * extFactor);
		double maxExtDistSq = (double) extRadiusX * extRadiusX; // For quick checks

		// Iterate over bounding box
		for (int dx = -extRadiusX; dx <= extRadiusX; dx++) {
			for (int dy = -extRadiusY; dy <= extRadiusY; dy++) {
				for (int dz = -extRadiusX; dz <= extRadiusX; dz++) {
					// Calculate distSq based on shape
					double distSq;
					switch (shape) {
						case ELLIPSOID: // Ellipsoid
							distSq = (double) dx * dx + (double) dy * dy * radiusx * radiusx / (radiusy * radiusy) + (double) dz * dz;
							break;
						case CUBOID: // Cuboid (max axis)
							distSq = Math.max(Math.abs(dx), Math.max(Math.abs(dy * radiusx / radiusy), Math.abs(dz)));
							distSq *= distSq; // Square for comparison
							break;
						default: // Sphere
							distSq = (double) dx * dx + (double) dy * dy + (double) dz * dz;
							break;
					}

					if (distSq > maxExtDistSq) continue; // Outside extended volume

					int bx = xx + dx;
					int by = yy + dy;
					int bz = zz + dz;
					if (by < minBuildHeight || by >= maxBuildHeight) continue;

					BlockPos pos = new BlockPos(bx, by, bz);
					BlockState state = level.getBlockState(pos);
					if (state.is(Blocks.STONE)) {
						// Normalized dist: 0 at center, 1 at original radius (scale to original for gradient)
						double dist = Math.sqrt(distSq);
						double normDist = dist / radiusx; // Adjust for shape (approx)

						// Add jitter for random gradient
						double jitter = level.random.nextGaussian() * 0.1;
						double adjusted = Math.max(0, Math.min(1, normDist + jitter));

						// Choose based on adjusted dist (hotter near 0)
						BlockState newState;
						if (adjusted < 0.15) {
							newState = RegisterBlocks.SELLAFITE_CORIUM.get().defaultBlockState();
						} else if (adjusted < 0.3) {
							newState = RegisterBlocks.INFERNAL_SELLAFITE.get().defaultBlockState();
						} else if (adjusted < 0.45) {
							newState = RegisterBlocks.BLAZING_SELLAFITE.get().defaultBlockState();
						} else if (adjusted < 0.6) {
							newState = RegisterBlocks.BOILING_SELLAFITE.get().defaultBlockState();
						} else if (adjusted < 0.75) {
							newState = RegisterBlocks.HOT_SELLAFITE.get().defaultBlockState();
						} else if (adjusted < 0.9) {
							newState = RegisterBlocks.SELLAFITE.get().defaultBlockState();
						} else {
							newState = RegisterBlocks.SLAKED_SELLAFITE.get().defaultBlockState();
						}

						// Set block and mark chunk dirty
						level.setBlock(pos, newState, 3); // Update with flags (notify neighbors, etc.)
						ChunkAccess chunk = level.getChunk(pos);
						chunk.setUnsaved(true);
					}
				}
			}
		}
	}

	public void removeMarked(long estimatedTime) {
		ServerChunkCache cache = level.getChunkSource();
		long[] affectedChunks = marker.getMarkedChunks();
		int longs_per_chunk = level.getSectionsCount() * 64;
		int usedChunks = marker.getNumOfMarkedChunks();
		int chunkLongOffset = 0;
		long[] markerBits = marker.getBlocks();
		int blockX, blockY, blockZ, cellIndex, $$1, $$2, index, chunkX, sectionY, chunkZ, sectionOffset, airId, realBlockLongOffset, maxSingle;
		long $$3, maskedAirId, chunkIndex, longID;
		byte[] blockData = new byte[0];
		byte[] skyData = new byte[0];
		long[] rawData;
		LevelChunkSection[] sections;
		LevelChunkSection section, air;
		DataLayer skyLayer, blockLayer;
		SimpleBitStorage bitstorage;
		MagicEntry entry;
		ByteBuf buf;
		for (int affected_index = 0; affected_index < usedChunks; affected_index++) {
			chunkIndex = affectedChunks[affected_index];
			chunkX = (int)chunkIndex;
			chunkZ = (int)(chunkIndex >> 32L);
			int basedx = (chunkX << 4) - xx;
			int basedz = (chunkZ << 4) - zz;
			int basedy;
			LevelChunk chunk = cache.getChunkNow(chunkX, chunkZ);
			if (chunk != null) {
				buf = Unpooled.directBuffer(longs_per_chunk + 3 << 3);
				HbmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new ClearMarkedChunkPack(marker.writeChunk(buf, chunkIndex, affected_index, xx, yy, zz, radiusx, radiusy, (short)shape.ordinal())));
				sections = chunk.getSections();
				longID = asLong(chunkX, minSec, chunkZ);
				LevelChunkSection lastSec = null;
				for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
					section = sections[sectionIndex];
					if (!section.hasOnlyAir()) {
						sectionOffset = chunkLongOffset + (sectionIndex << 6);
						if (marker.markedBitsInSec[sectionOffset>>6] == 0) {
							continue;
						}
						sectionY = minSec + sectionIndex;
						longID = asLongY(longID, sectionY);
						basedy = (sectionY << 4) - yy;
						if (marker.markedBitsInSec[sectionOffset>>6] < 4096) {
							airId = section.states.data.palette.idFor(AirState);
							if (!(section.states.data.storage instanceof ZeroBitStorage)) {
								bitstorage = (SimpleBitStorage) section.states.data.storage;
								entry = storage[bitstorage.getBits()];
								maskedAirId = airId & entry.mask;
								if (skyMap != null) {
									skyLayer = skyMap.get(longID);
									if (skyLayer == null) {
										skyLayer = new DataLayer(0);
										skyMap.put(longID, skyLayer);
									}
									skyData = skyLayer.getData();
								}
								if (blockMap != null) {
									blockLayer = blockMap.get(longID);
									if (blockLayer == null) {
										blockLayer = new DataLayer(0);
										blockMap.put(longID, blockLayer);
									}
									blockData = blockLayer.getData();
								}
								rawData = bitstorage.getRaw();
								realBlockLongOffset = sectionOffset;
								for (int bigLong = 0; bigLong < 64; bigLong++) {
									if (markerBits[realBlockLongOffset] != 0L) {
										maxSingle = (bigLong << 6) + 64;
										for (int blockIndex = bigLong << 6; blockIndex < maxSingle; blockIndex++) {
											if (marker.get(realBlockLongOffset, blockIndex & 0x3F) != 0) {
												mbMarked++;
												blockX = blockIndex & 0xF;
												blockY = blockIndex >> 4 & 0xF;
												blockZ = blockIndex >> 8;
												cellIndex = (blockY << 4 | blockZ) << 4 | blockX;
												$$1 = (int) (cellIndex * entry.divideMulLong + entry.divideAddLong >> 32L >> entry.divideShift);
												$$2 = (cellIndex - $$1 * entry.valuesPerLong) * entry.bits;
												$$3 = rawData[$$1];
												rawData[$$1] = $$3 & (~(entry.mask << $$2)) | maskedAirId << $$2;
												index = blockY << 8 | blockZ << 4 | blockX;
												if (skyMap != null) skyData[index >> 1] = (byte) (skyData[index >> 1] | 15 << 4 * (index & 1)); //set sky light
												if (blockMap != null) blockData[index >> 1] = (byte) 0; //set block light
											}
										}
									}
									realBlockLongOffset++;
								}
								section.recalcBlockCounts();
							}

						}
						else {
							clearSectionWhole(section, -1);
							section.recalcBlockCounts();
							section.recalcBlockCounts();
							if (skyMap != null) {
								skyLayer = skyMap.get(longID); //set sky light
								if (skyLayer == null) {
									skyLayer = new DataLayer(15);
									skyMap.put(longID, skyLayer);
								} else {
									skyLayer.fill(15);
								}
							}
							if (blockMap != null) {
								blockLayer = blockMap.get(longID); //set block light
								if (blockLayer == null) {
									blockLayer = new DataLayer(0);
									blockMap.put(longID, blockLayer);
								} else {
									blockLayer.fill(0);
								}
							}
							mbMarked += 4096;
							section.recalcBlockCounts();
						}
					}
					lastSec = section;
				}
				chunk.setUnsaved(true);
			}
			chunkLongOffset += longs_per_chunk;

		}

		System.out.println(mbMarked + " blocks have been removed in " + estimatedTime / 1000000L + "ms, " + estimatedTime / 1000000000L + "s, or " + estimatedTime / 60000000000L + "min, averaging " + (mbMarked * 1000000L / (estimatedTime / 1000L)) + " blocks per second.");
		if(scale > 0)
			NukeTorexEntity.statFac(level, x, z, scale);
	}

	public static int clearSectionWhole(LevelChunkSection section, int maxres) {
		Palette<BlockState> palette = section.states.data.palette;
		BitStorage storage = section.states.data.storage;
		int mbd = 0;
		boolean hasHighRes = false;
		int airId = palette.idFor(AirState);
		List<BlockState> allStates;
		HashSet<Integer> allUntouchedStateIDs = new HashSet<>();
		if (maxres >= 0) {
			if (palette instanceof SingleValuePalette singlePalette) { //if single value
				assert singlePalette.value != null;
				if (((BlockState) singlePalette.value).getBlock().getExplosionResistance() > maxres) { //if single value that can't be destroyed
					return 0;
				}
			} else {
				if (palette instanceof LinearPalette linearPalette) {
					int size = linearPalette.getSize();

					for (int i = 0; i < size; i++) {
						if (linearPalette.values[i] instanceof BlockState state) {

							if (state.getBlock().getExplosionResistance() > maxres) {
								hasHighRes = true;
								allUntouchedStateIDs.add(i);
							}
						}
					}
				} else if (palette instanceof HashMapPalette hashPalette) {
					allStates = hashPalette.getEntries();
					for (BlockState state : allStates) {
						if (state.getBlock().getExplosionResistance() > maxres) {
							allUntouchedStateIDs.add(hashPalette.idFor(state));
							hasHighRes = true;
						}
					}
				}

				//System.out.println();
				SimpleBitStorage bitStorage = (SimpleBitStorage) storage;
				if (hasHighRes) {
					int bits = bitStorage.getBits();
					long[] data = bitStorage.getRaw();
					long mask = (1L << bits) - 1L;
					int valuesPerLong = bitStorage.valuesPerLong;

					for (int $$2 = 0; $$2 < data.length; $$2++) {
						long $$3 = data[$$2];
						for (int $$4 = 0; $$4 < valuesPerLong; $$4++) {
							int bitPosition = $$4 * bits;
							int currentId = (int) ($$3 >> bitPosition & mask);
							if (!allUntouchedStateIDs.contains(currentId)) {
								$$3 = $$3 & ~(mask << bitPosition) | ((long) airId & mask) << bitPosition;
								mbd++;
							}
						}
						data[$$2] = $$3;
					}
					return mbd;
				}
			}
		}
		section.states = new PalettedContainer(Block.BLOCK_STATE_REGISTRY, AirState, PalettedContainer.Strategy.SECTION_STATES);
		return 4096;
	}

	//fromsectionpos
	public static int x(long p_123214_) {
		return (int) (p_123214_ >> 42);
	}

	public static int y(long p_123226_) {
		return (int) (p_123226_ << 44 >> 44);
	}

	public static int z(long p_123231_) {
		return (int) (p_123231_ << 22 >> 42);
	}

	///
	public void explode() {
		long startTime = System.nanoTime();
		if (dmgmltp > 0) fireEntityRays();
		mbMarked = 0;
		long initial_loss = blockResistanceMap.get(getBlockFastNOPOS(level, xx, yy, zz));
		if (initial_loss > radiusx*20L*powermltp)
			return;
		marker.markBlock(xx >> 4, zz >> 4, (yy - minBuildHeight) >> 4, (xx & 15), (yy & 15), (zz & 15));
		CalcRays();

		//finishing the marking
		long estimatedTime = System.nanoTime() - startTime;
		System.out.println("Time taken for calc: " + estimatedTime/1000000 + "ms");
		removeMarked(estimatedTime);// remove the marked blocks
		contaminateStone();
		System.out.println(fireSmall + " small rays"); // small rays
		System.out.println("called the mark function: " + mbMarked + " times");

	}


	private void CalcRays() {

		for (int i = -raybigradiusx - 1; i < raybigradiusx + 2; ++i) { //possibly use shorts here
			for (int j = -raybigradiusy - 1; j < raybigradiusy + 2; ++j) {
				for (int k = -raybigradiusx - 1; k < raybigradiusx + 2; ++k) {
					if (secInRange(i, j, k, xinsec, yinsec, zinsec, radiusx, radiusy, shape, mindistsqInRange, maxdistsqInRange)) {
						calculateRaysInSec(i, j, k);
					}
				}
			}
		}
	}

	private boolean blockInRange(int dx, int dy, int dz) {
		long distsq = ShapeCalculator.distanceSq(dx, dy, dz, radiusx, radiusy, shape);
		return (distsq >= mindistsqInRange && distsq <= maxdistsqInRange);
	}

	private void calculateRaysInSec(int dcx, int dcy, int dcz)
	{
		int dx, dy, dz, dist;
		for (int i = 0; i < 16; ++i) {
			for (int j = 0; j < 16; ++j) {
				for (int k = 0; k < 16; ++k) {
					dx = (dcx << 4) + i - xinsec;
					dy = (dcy << 4) + j - yinsec;
					dz = (dcz << 4) + k - zinsec;
					if (blockInRange(dx, dy, dz)) {
						if (shape == Shape.SPHERE) fireFastestSmallRay(dx, dy, dz, (long)(radiusx*rng.nextDouble(edgeratio, 1)*20*powermltp));
						else {
							dist = (int)Math.sqrt(dx*dx + dy*dy + dz*dz);
							fireFastestSmallRay(dx, dy, dz, (long)((rng.nextDouble(edgeratio, 1)/rayradiusx)*radiusx*dist*20*powermltp));
						}
					}
				}
			}
		}
	}

	public static boolean secInRange(int dcx, int dcy, int dcz, int xinsec, int yinsec, int zinsec, int radx, int rady, Shape shape, int mindistsqInRange, int maxdistsqInRange) //corner calculation
	{
		int dx, dy, dz, closedistsq, fardistsq;
		//get furthest corner
		if (dcx == 0)
			dx = 15 - xinsec;
		else if (dcx > 0)
			dx = (dcx << 4) + 15 - xinsec;
		else
			dx = (dcx << 4) - xinsec;
		if (dcy == 0)
			dy = 15 - yinsec;
		else if (dcy > 0)
			dy = (dcy << 4) + 15 - yinsec;
		else
			dy = (dcy << 4) - yinsec;
		if (dcz == 0)
			dz = 15 - zinsec;
		else if (dcz > 0)
			dz = (dcz << 4) + 15 - zinsec;
		else
			dz = (dcz << 4) - zinsec;
		fardistsq = (int)ShapeCalculator.distanceSq(dx, dy, dz, radx, rady, shape);
		if (dcx == 0)
			dx = 0;
		else if (dcx > 0)
			dx = (dcx << 4) - xinsec;
		else
			dx = (dcx << 4) + 15 - xinsec;
		if (dcy == 0)
			dy = 0;
		else if (dcy > 0)
			dy = (dcy << 4) - yinsec;
		else
			dy = (dcy << 4) + 15 - yinsec;
		if (dcz == 0)
			dz = 0;
		else if (dcz > 0)
			dz = (dcz << 4) - zinsec;
		else
			dz = (dcz << 4) + 15 - zinsec;
		closedistsq = (int)ShapeCalculator.distanceSq(dx, dy, dz, radx, rady, shape);
		return (fardistsq >= mindistsqInRange && closedistsq <= maxdistsqInRange);
	}

	private static final Random rng = new Random();

	public void fireFastestSmallRay(int finaldx, int finaldy, int finaldz, long power)
	{ //finaldxyz - delta of small ray end point from center, startxyz - raw beginning point of small ray
		int startx, starty, startz;
		startx = xx;
		starty = yy;
		startz = zz;
		fireSmall++;
		int which, total;
		int adx = Math.abs(finaldx), ady = Math.abs(finaldy), adz = Math.abs(finaldz); //how much to add to itself when counting
		int tempx = (adx+1)/2, tempy = (ady+1)/2, tempz = (adz+1)/2; //start halfway to make it smooth and swap at correct times
		int base = Math.max(adx, Math.max(ady, adz)); //largest offset, x y or z
		if (base == adx && base == ady && base == adz) {
			which = 4;
			total = adx * radiusx / rayradiusx;
		} else if (base == adx && base == ady) {
			which = 5;
			total = adx * radiusx / rayradiusx;
		} else if (base == adx && base == adz) {
			which = 6;
			total = adx * radiusx / rayradiusx;
		} else if (base == ady && base == adz) {
			which = 7;
			total = ady * radiusx / rayradiusx;
		} else if (base == adx) {
			which = 1;
			total = adx * radiusx / rayradiusx;
		} else if (base == ady) {
			which = 2;
			total = ady * radiusx / rayradiusx;
		} else {
			which = 3;
			total = adz * radiusx / rayradiusx; //setting largest offset in loop
		}
		int tempedge = edge*total/radiusx;
		if (radiusx == 80 && radiusy == 80) { //super
			if (Math.min(adx, Math.min(ady, adz)) < 2 && total > 74) {
				total = 73;
				tempedge = 1;
				//System.out.println("Setting total to: " + total);
			}
		}
		int resConst;
		if (which < 4) resConst = 100 * rayradiusx / (adx+ady+adz); // x, y and z rays
		else if (which == 4) resConst = 1000/23; // xyz rays
		else resConst = 30*rayradiusx/(adx+ady+adz)+25;// * 3 * rayradiusx / 4 / (adx+ady+adz); //xy, yz and xz rays
		//when adx+ady+adz = sqrt(2)*rayradiusx (so one dxyz is 0) then 2/3, when adx+ady+adz = sqrt(3)*rayradiusx (so they are close to corner) then 3/4
		long powerLoss = power*8/total/10;
		switch (which) {
			case 4: //x y z
				if (starty < minBuildHeight || starty >= maxBuildHeight)
					break;
				for (int i = 0; i < total; ++i) {
					if (i >= total - tempedge) { //feather edge
						if (rng.nextInt(tempedge) < 2)
							break;
					}
					if (finaldx > 0) {
						++startx;
					}
					else {
						startx--;
					}
					if (finaldy > 0) {
						++starty;
					}
					else {
						starty--;
					}
					if (finaldz > 0) {
						++startz;
					}
					else {
						startz--;
					}
					if (starty - 1 < minBuildHeight || starty + 1 >= maxBuildHeight)
						break;
					power = markBlock(level, startx, starty, startz, power, resConst);
					power -= powerLoss;
					if (power <= 0)
						break;
					if (finaldx > 0) {
						power = markBlock(level, startx+1, starty, startz, power, resConst);
					} else {
						power = markBlock(level, startx-1, starty, startz, power, resConst);
					}
					if (power <= 0)
						break;
					if (finaldy > 0) {
						power = markBlock(level, startx, starty+1, startz, power, resConst);
					} else {
						power = markBlock(level, startx, starty-1, startz, power, resConst);
					}
					if (power <= 0)
						break;
					if (finaldz > 0) {
						power = markBlock(level, startx, starty, startz+1, power, resConst);
					} else {
						power = markBlock(level, startx, starty, startz-1, power, resConst);
					}
					if (power <= 0)
						break;
				}
				break;
			case 1: //x
				if (starty < minBuildHeight || starty >= maxBuildHeight)
					break;
				for (int i = 0; i < total; ++i) {
					if (i >= total - tempedge) { //feather edge
						if (rng.nextInt(tempedge) < 2)
							break;
					}
					if (tempy > base) {
						if (finaldy > 0)
							++starty;
						else
							starty--;
						tempy -= base;
						if (starty < minBuildHeight || starty >= maxBuildHeight)
							break;
						power = markBlock(level, startx, starty, startz, power, resConst);
						if (power <= 0)
							break;
					}
					tempy += ady;
					if (tempz > base) {
						if (finaldz > 0)
							++startz;
						else
							startz--;
						tempz -= base;
						power = markBlock(level, startx, starty, startz, power, resConst);
						if (power <= 0)
							break;
					}
					tempz += adz;
					if (finaldx > 0) {
						++startx;
					}
					else {
						startx--;
					}
					power = markBlock(level, startx, starty, startz, power, resConst);
					power -= powerLoss;
					if (power <= 0)
						break;
				}
				break;
			case 2: //y
				for (int i = 0; i < total; ++i) {
					if (i >= total - tempedge) { //feather edge
						if (rng.nextInt(tempedge) < 2)
							break;
					}
					if (tempx > base) {
						if (finaldx > 0)
							++startx;
						else
							startx--;
						tempx -= base;
						power = markBlock(level, startx, starty, startz, power, resConst);
						if (power <= 0)
							break; //thick rays (mark after every increment (x y z), not just largest offset (this case x))
					}
					tempx += adx;
					if (tempz > base) {
						if (finaldz > 0)
							++startz;
						else
							startz--;
						tempz -= base;
						power = markBlock(level, startx, starty, startz, power, resConst);
						if (power <= 0)
							break;
					}
					tempz += adz;
					if (finaldy > 0)
						++starty;
					else
						starty--;
					if (starty < minBuildHeight || starty >= maxBuildHeight)
						break;
					power = markBlock(level, startx, starty, startz, power, resConst);
					power -= powerLoss;
					if (power <= 0)
						break;
				}
				break;
			case 3: //z
				if (starty < minBuildHeight || starty >= maxBuildHeight)
					break;
				for (int i = 0; i < total; ++i) {
					if (i >= total - tempedge) { //feather edge
						if (rng.nextInt(tempedge) < 2)
							break;
					}
					if (tempx > base) {
						if (finaldx > 0)
							++startx;
						else
							startx--;
						tempx -= base;
						power = markBlock(level, startx, starty, startz, power, resConst);
						if (power <= 0)
							break;
					}
					tempx += adx;
					if (tempy > base) {
						if (finaldy > 0)
							++starty;
						else
							starty--;
						tempy -= base;
						if (starty < minBuildHeight || starty >= maxBuildHeight)
							break;
						power = markBlock(level, startx, starty, startz, power, resConst);
						if (power <= 0)
							break;
					}
					tempy += ady;
					if (finaldz > 0)
						++startz;
					else
						startz--;
					power = markBlock(level, startx, starty, startz, power, resConst);
					power -= powerLoss;
					if (power <= 0)
						break;
				}
				break;
			case 5: //x y
				if (starty < minBuildHeight || starty >= maxBuildHeight)
					break;
				for (int i = 0; i < total; ++i) {
					if (i >= total - tempedge) { //feather edge
						if (rng.nextInt(tempedge) < 2)
							break;
					}
					if (tempz > base) {
						if (finaldz > 0)
							++startz;
						else
							startz--;
						tempz -= base;
						power = markBlock(level, startx, starty, startz, power, resConst);
						if (power <= 0)
							break;
					}
					tempz += adz;
					if (finaldx > 0) {
						power = markBlock(level, startx+1, starty, startz, power, resConst);
					}
					else {
						power = markBlock(level, startx-1, starty, startz, power, resConst);
					}
					if (power <= 0)
						break;
					if (finaldy > 0)
						++starty;
					else
						starty--;
					if (starty < minBuildHeight || starty >= maxBuildHeight)
						break;
					power = markBlock(level, startx, starty, startz, power, resConst);
					if (power <= 0)
						break;
					if (finaldx > 0) {
						++startx;
					}
					else {
						startx--;
					}
					power = markBlock(level, startx, starty, startz, power, resConst);
					power -= powerLoss;
					if (power <= 0)
						break;
				}
				break;
			case 6: //x z
				if (starty < minBuildHeight || starty >= maxBuildHeight)
					break;
				for (int i = 0; i < total; ++i) {
					if (i >= total - tempedge) { //feather edge
						if (rng.nextInt(tempedge) < 2)
							break;
					}
					if (tempy > base) {
						if (finaldy > 0) {
							++starty;
						} else
							starty--;
						if (starty < minBuildHeight || starty >= maxBuildHeight)
							break;
						tempy -= base;
						power = markBlock(level, startx, starty, startz, power, resConst);
						if (power <= 0)
							break;
					}
					tempy += ady;
					if (finaldx > 0) {
						power = markBlock(level, startx+1, starty, startz, power, resConst);
					}
					else {
						power = markBlock(level, startx-1, starty, startz, power, resConst);
					}
					if (power <= 0)
						break;
					if (finaldz > 0)
						++startz;
					else
						startz--;
					power = markBlock(level, startx, starty, startz, power, resConst);
					if (power <= 0)
						break;
					if (finaldx > 0) {
						++startx;
					}
					else {
						startx--;
					}
					power = markBlock(level, startx, starty, startz, power, resConst);
					power -= powerLoss;
					if (power <= 0)
						break;
				}
				break;
			case 7: //y z
				if (starty < minBuildHeight || starty >= maxBuildHeight)
					break;
				for (int i = 0; i < total; ++i) {
					if (i >= total - tempedge) { //feather edge
						if (rng.nextInt(tempedge) < 2)
							break;
					}
					if (tempx > base) {
						if (finaldx > 0) {
							++startx;
						} else
							startx--;
						tempx -= base;
						power = markBlock(level, startx, starty, startz, power, resConst);
						if (power <= 0)
							break;
					}
					tempx += adx;
					if (finaldz > 0) {
						power = markBlock(level, startx, starty, startz+1, power, resConst);
					}
					else {
						power = markBlock(level, startx, starty, startz-1, power, resConst);
					}
					if (power <= 0)
						break;
					if (finaldy > 0) {
						++starty;
					}
					else {
						starty--;
					}
					if (starty < minBuildHeight || starty >= maxBuildHeight)
						break;
					power = markBlock(level, startx, starty, startz, power, resConst);
					if (power <= 0)
						break;
					if (finaldz > 0)
						++startz;
					else
						startz--;
					power = markBlock(level, startx, starty, startz, power, resConst);
					power -= powerLoss;
					if (power <= 0)
						break;
				}
				break;
		}
	}

	private void fireEntityRays() {
		AABB box = new AABB(x-radiusx, y-radiusy, z-radiusx, x+radiusx, y+radiusy, z+radiusx);
		float dx, dy, dz, knockconst;
		List<Entity> entities = level.getEntitiesOfClass(Entity.class, box, e -> {
			if (e.isRemoved()) return false;// skip removed/dead stuff
			if (e instanceof Player p && p.isSpectator()) return false;// skip spectators
			if (e instanceof LivingEntity en && en.getHealth() <= 0F) return false;// positive health
			double ddx = e.getX()-x, ddy = e.getEyeY()-y, ddz = e.getZ()-z;//distance
			double entdistance = switch (shape) {
				case ELLIPSOID -> ddx*ddx + ddy*ddy*radiusx*radiusx/radiusy/radiusy + ddz*ddz;
				case CYLINDER -> ddx*ddx + ddz*ddz;
				default -> ddx*ddx + ddy*ddy + ddz*ddz;
			};
			return !(entdistance > radiusx * radiusx);
		});
		float damage, power, distance, distconst;
		for (Entity entity : entities)
		{
			power = fireRayAtEntity(entity); //ray remaining power
			if (power > 0) { //calc: damage = dmgmltp * power/maxpower * (distance - radius) ^ 2 / radius -> power * ((distance-radius)/radius)^2
				dx = (float)(entity.getX()-x);
				dy = (float)(entity.getEyeY()-y);
				dz = (float)(entity.getZ()-z);
				distance = switch (shape) {
					case ELLIPSOID -> (float)Math.sqrt(dx*dx + dy*dy*radiusx*radiusx/radiusy/radiusy + dz*dz);
					case CUBOID -> Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
					default -> (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
				};
				distconst = distance-radiusx;
				damage = power * distconst*distconst / radiusx / radiusx;
				if (entity instanceof LivingEntity lentity) lentity.hurt(level.damageSources().explosion(ssource, entity), damage);
				if (!(entity instanceof LivingEntity lentity && lentity.getHealth() < 0F)) {
					knockconst = knockbackmltp * damage / Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz))) / dmgmltp;
					entity.addDeltaMovement(new Vec3(knockconst * dx, knockconst * dy, knockconst * dz));
					entity.hurtMarked = true;
				}
			}
		}
	}

	private float fireRayAtEntity(Entity entity) {
		//endxyz - small ray end point, startxyz - beginning point of small ray
		int startx, starty, startz;
		float damage = radiusx*dmgmltp;
		startx = xx;
		starty = yy;
		startz = zz;
		//fire_entity++;
		int endx = (int) Math.floor(entity.getX()), endy = (int) Math.floor(entity.getEyeY()), endz = (int) Math.floor(entity.getZ());
		int finaldx = endx - xx;
		int finaldy = endy - yy;
		int finaldz = endz - zz;
		//System.out.println("Endx: " + endx + ", endy: " + endy + ", endz: " + endz);
		int which, total;
		int adx = Math.abs(finaldx), ady = Math.abs(finaldy), adz = Math.abs(finaldz); //how much to add to itself when counting
		int tempx = (adx + 1) / 2, tempy = (ady + 1) / 2, tempz = (adz + 1) / 2; //start halfway to make it smooth and swap at correct times
		int base = Math.max(adx, Math.max(ady, adz)); //largest offset, x y or z
		if (base == adx) {
			which = 1;
			total = adx;
		} else if (base == ady) {
			which = 2;
			total = ady;
		} else {
			which = 3;
			total = adz; //setting largest offset in loop
		}
		switch (which) {
			case 1: //x
				for (int i = 0; i < total; ++i) {
					if (i >= total - edge) { //feather edge
						if (rng.nextInt(edge) < 1)
							break;
					}
					if (tempy > base) {
						if (finaldy > 0)
							++starty;
						else
							starty--;
						tempy -= base;
						damage -= checkRes(level, startx, starty, startz, damage) * radiusx / (adx + ady + adz);
						if (damage <= 0)
							break;
					}
					tempy += ady;
					if (tempz > base) {
						if (finaldz > 0)
							++startz;
						else
							startz--;
						tempz -= base;
						damage -= checkRes(level, startx, starty, startz, damage) * radiusx / (adx + ady + adz);
						if (damage <= 0)
							break;
					}
					tempz += adz;
					if (finaldx > 0) {
						++startx;
					} else {
						startx--;
					}
					damage -= checkRes(level, startx, starty, startz, damage) * radiusx / (adx + ady + adz);
					if (damage <= 0)
						break;
				}
				break;
			case 2: //y
				for (int i = 0; i < total; ++i) {
					if (i >= total - edge) { //feather edge
						if (rng.nextInt(edge) < 1)
							break;
					}
					if (tempx > base) {
						if (finaldx > 0)
							++startx;
						else
							startx--;
						tempx -= base;
						damage -= checkRes(level, startx, starty, startz, damage) * radiusx / (adx + ady + adz);
						if (damage <= 0)
							break;
					}
					tempx += adx;
					if (tempz > base) {
						if (finaldz > 0)
							++startz;
						else
							startz--;
						tempz -= base;
						damage -= checkRes(level, startx, starty, startz, damage) * radiusx / (adx + ady + adz);
						if (damage <= 0)
							break;
					}
					tempz += adz;
					if (finaldy > 0)
						++starty;
					else
						starty--;
					damage -= checkRes(level, startx, starty, startz, damage) * radiusx / (adx + ady + adz);
					if (damage <= 0)
						break;
				}
				break;
			case 3: //z
				for (int i = 0; i < total; ++i) {
					if (i >= total - edge) { //feather edge
						if (rng.nextInt(edge) < 1)
							break;
					}
					if (tempx > base) {
						if (finaldx > 0)
							++startx;
						else
							startx--;
						tempx -= base;
						damage -= checkRes(level, startx, starty, startz, damage) * radiusx / (adx + ady + adz);
					}
					tempx += adx;
					if (tempy > base) {
						if (finaldy > 0)
							++starty;
						else
							starty--;
						tempy -= base;
						damage -= checkRes(level, startx, starty, startz, damage) * radiusx / (adx + ady + adz);
					}
					tempy += ady;
					if (finaldz > 0)
						++startz;
					else
						startz--;
					damage -= checkRes(level, startx, starty, startz, damage) * radiusx / (adx + ady + adz);
					if (damage <= 0)
						break;
				}
				break;
		}
		return damage;
	}

	public long markBlock(ServerLevel level, int x, int y, int z, long power, int resConst) {
		long chunkSecId = asLong(x>>4, y>>4, z>>4);
		long res;
		if (resCache.containsKey(chunkSecId)) {
			res = resCache.get(chunkSecId)[((((x&15) << 4) | (y&15)) << 4) | (z&15)];
		}
		else {
			res = blockResistanceMap.get(getBlockFastNOPOS(level, x, y, z));
		}
		if (res == 0) { //skip calc
			marker.markBlock(x >> 4, z >> 4, (y - minBuildHeight) >> 4, (x & 15), (y & 15), (z & 15));
			return power;
		}
		if (res < power) {
			marker.markBlock(x >> 4, z >> 4, (y - minBuildHeight) >> 4, (x & 15), (y & 15), (z & 15));
			return power - res*resConst/100;
		}
		return 0;
	}

	private float checkRes(ServerLevel level, int x, int y, int z, float power) {
		if (y >= level.getMinBuildHeight() && y < level.getMaxBuildHeight()) {
			long chunkSecId = asLong(x>>4, y>>4, z>>4);
			if (resCache.containsKey(chunkSecId)) {
				return resCache.get(chunkSecId)[((((x&15) << 4) | (y&15)) << 4) | (z&15)];
			}
			else {
				return blockResistanceMap.get(getBlockFastNOPOS(level, x, y, z));
			}
		}
		return 0;
	}

	//temp for no bpos
	public static BlockState getBlockFastNOPOS(ServerLevel level, int x, int y, int z) {
		if (y < level.getMinBuildHeight() || y >= level.getMaxBuildHeight())
			return AirState;
		LevelChunkSection section = level.getChunk(x >> 4, z >> 4)
				.getSection((y - level.getMinBuildHeight()) >> 4);
		if (section.hasOnlyAir()) { //slightly faster
			return AirState;
		}
		byte xx = (byte) (x & 15);
		byte yy = (byte) (y & 15);
		byte zz = (byte) (z & 15);
		return section.states.get(xx, yy, zz);
	}


	private void breakBlockFastNOPOS(ServerLevel level, int x, int y, int z) {
		if (y - level.getMinBuildHeight() >> 4 < 0 || y - level.getMinBuildHeight() >> 4 >= 24)
			return;
		LevelChunk chunk = level.getChunk(x >> 4, z >> 4);
		LevelChunkSection section = chunk.getSection(level.getSectionIndexFromSectionY(y >> 4));
		chunk.setUnsaved(true);
		byte xx = (byte) (x & 15);
		byte yy = (byte) (y & 15);
		byte zz = (byte) (z & 15);
		section.getStates().set(xx, yy, zz, Blocks.AIR.defaultBlockState());
		level.getChunkSource().blockChanged(new BlockPos(xx, yy, zz));
	}
	public static final class MagicEntry {
		public final long mask;
		public final int bits;
		public final int valuesPerLong;
		public final long divideMulLong;
		public final long divideAddLong;
		public final int divideShift;

		public MagicEntry(long mask, int bits, int valuesPerLong, long divideMulLong, long divideAddLong, int divideShift) {
			this.mask = mask;
			this.bits = bits;
			this.valuesPerLong = valuesPerLong;
			this.divideMulLong = divideMulLong;
			this.divideAddLong = divideAddLong;
			this.divideShift = divideShift;
		}
	}
	public static final MagicEntry[] storage = new MagicEntry[25];

	static {
		for (int bits = 1; bits < storage.length; bits++) {
			int valuesPerLong = (char)(64 / bits);
			int i = 3 * (valuesPerLong - 1);
			MagicEntry entry = new MagicEntry(
					(1L << bits) - 1L, bits, valuesPerLong, Integer.toUnsignedLong(MAGIC[i]), Integer.toUnsignedLong(MAGIC[i + 1]), MAGIC[i + 2]
			);
			storage[bits] = entry;
		}
	}
}