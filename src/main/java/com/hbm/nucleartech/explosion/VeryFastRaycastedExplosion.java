package com.hbm.nucleartech.explosion;

import com.hbm.nucleartech.block.RegisterBlocks;
import com.hbm.nucleartech.entity.effects.NukeTorexEntity;
import com.hbm.nucleartech.mixin.MixinData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import com.hbm.nucleartech.network.packet.ClearMarkedChunkPack;
import com.hbm.nucleartech.network.packet.ClearSectionListPack;
import com.hbm.nucleartech.network.HbmPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.util.BitStorage;
import net.minecraft.util.Mth;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.joml.Vector2i;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("deprecation")
public class VeryFastRaycastedExplosion {

	private static ServerLevel level;
	private static int xx, yy ,zz;
	private static int edge;
    private static int raybigradiusx;
	private static int raybigradiusy;
	private static int radiusx;
	private static int radiusy;
	private static int rayradiusx;
    private static byte shape; //0 - sphere, 1 - ellipsoid, 2 - cuboid
	private static int powermltp;
	private static float dmgmltp;
	private static float knockbackmltp;
	private static BlockMarker marker;
	private static int minSec;
	private static int maxBuildHeight;
	private static int minBuildHeight;
	private static Long2ObjectOpenHashMap<DataLayer> block_map;
	private static double x, y, z;
	private static float scale;
	private static int maxdistsqInRange;
	private static int mindistsqInRange;
	private static Entity ssource;
	private static final HashMap<BlockState, Integer> blockResistanceMap = new HashMap<>();
	private int fire_small = 0;
	private  int initial_loss = 0;

	public VeryFastRaycastedExplosion(Level Level, double X, double Y, double Z, int Edge, int maxradiusx, int maxradiusy, int Rayradiusx,
									  byte Shape, float Knockbackmltp, int Powermltp, float Dmgmltp, @Nullable Entity source, float cloudScale) {
		//
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
		scale = cloudScale;
		edge = Edge;
		radiusx = maxradiusx;
		radiusy = maxradiusy;
		rayradiusx = Rayradiusx;
        int rayradiusy = rayradiusx * radiusy / radiusx;
        int maxbigradiusx = maxradiusx / 16;
		raybigradiusx = rayradiusx / 16;
		raybigradiusy = rayradiusy / 16;
		maxdistsqInRange = (rayradiusx + 1) * (rayradiusx + 1);
		mindistsqInRange = (rayradiusx) * (rayradiusx - 1);
		shape = Shape;
		powermltp = Powermltp;
		dmgmltp = Dmgmltp;
		knockbackmltp = Knockbackmltp / 10;
		marker = new BlockMarker((maxbigradiusx +2)*(maxbigradiusx +2)*4, level.getSectionsCount());
		minSec = level.getMinSection();
		final ThreadedLevelLightEngine lightEngine = ((ServerLevel) level).getChunkSource().getLightEngine();
		LightEngine<?, ?> block_engine = (LightEngine<?, ?>) lightEngine.getLayerListener(LightLayer.SKY);
		block_map = block_engine.storage.visibleSectionData.map;

		//HashMap<Block, Float> blockResistanceMap = new HashMap<>();
		long start = System.nanoTime();
		//final HashMap<Block, Integer> blockResistanceCache = new HashMap<>();
		Registry<Block> blockRegistry = BuiltInRegistries.BLOCK;
		for (Block block : blockRegistry) {
			StateDefinition<Block, BlockState> stateDefinition = block.getStateDefinition();
			for (BlockState blockState : stateDefinition.getPossibleStates()) {
				int explosionResistance = (int) (block.getExplosionResistance() * 10);
				if (explosionResistance < 20) { //0-1.9 - dirt
					blockResistanceMap.put(blockState, explosionResistance);
					continue;
				} else if (explosionResistance < 100) {//2-9.9 - stone
					explosionResistance /= 4;
					explosionResistance += 15;
				}
				else if (explosionResistance < 1000) {//10-99.9 - trial spawner
					explosionResistance /= 10;
					explosionResistance += 30;
				}
				else if (explosionResistance == 1000) {//100 - water, lava
					explosionResistance = 40;
				}
				else if (explosionResistance < 10000) {//100.1-999.9 - water, ender chest
					explosionResistance /= 20;
					explosionResistance += 80;
				}
				else { //1000+ - obsidian, netherite, bedrock
					explosionResistance /= 40;
					explosionResistance += 330;
				}
				//blockResistanceMap.put(block.getId(blockState), (Integer) explosionResistance);
				blockResistanceMap.put(blockState, explosionResistance);
				if (blockState == Blocks.BEDROCK.defaultBlockState()) System.out.println("Bedrock: " + blockResistanceMap.get(Blocks.BEDROCK.defaultBlockState()));
			}
		}
		//blockResistanceMap = blockResistanceCache;//Collections.unmodifiableMap(blockResistanceCache); // convert to unmodifiable -> faster .get
		System.out.println((System.nanoTime() - start)/1000 + "micros to get res, size: " + blockResistanceMap.size());
		//blockResistanceMap.forEach((blockState, resistance) -> System.out.println(blockState + ": " + resistance));
		if(scale > 0)
			NukeTorexEntity.statFac(level, x, z, scale);
		explode();
	}

	private static List<Vector2i> getChunksInRadius(int cx, int cz, int r) {
		List<Vector2i> list = new ArrayList<>();
		cx = cx>>4;
		cz = cz>>4;
		int cr = ((r+15)>>4)+2;
		for (int dx=-cr; dx<=cr; dx++) {
			int maxDz = (int) Math.sqrt(cr*cr-dx*dx);
			for (int dz=-maxDz; dz<=maxDz; dz++)
				list.add(new Vector2i(cx+dx,cz+dz));
		}
		return list;
	}

//	public VeryFastRaycast(Level level, double x, double y, double z, int edge, int maxradius, int rayradius, byte shape, float knockmltp, int powermltp, float dmgmltp, @Nullable Entity source) {
//		this( level, x, y, z, edge, maxradius, maxradius, rayradius, shape, knockmltp, powermltp, dmgmltp, source);
//	}
//
//	public VeryFastRaycast (Level level, double x, double y, double z, int edge, int maxradius, byte shape, float knockmltp, int powermltp, float dmgmltp, @Nullable Entity source) {
//		this( level, x, y, z, edge, maxradius, maxradius, maxradius, shape, knockmltp, powermltp, dmgmltp, source);
//	}

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

	static {
		for (int bits = 1; bits < other_magic.length; bits++) {
			int valuesPerLong = (char)(64 / bits);
			int i = 3 * (valuesPerLong - 1);
			MagicEntry entry = new MagicEntry(
					(1L << bits) - 1L, bits, valuesPerLong, Integer.toUnsignedLong(MAGIC[i]), Integer.toUnsignedLong(MAGIC[i + 1]), MAGIC[i + 2]
			);
			other_magic[bits] = entry;
		}

		rng = new Random();
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
						case 1: // Ellipsoid
							distSq = (double) dx * dx + (double) dy * dy * radiusx * radiusx / (radiusy * radiusy) + (double) dz * dz;
							break;
						case 2: // Cuboid (max axis)
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

	///
	public void removeMarked(long estimatedTime) {
		ServerChunkCache cache = level.getChunkSource();
		long[] affectedChunks = marker.getAffectedChunks();
		int longs_per_chunk = level.getSectionsCount() * 64;
		System.out.println("longs_per_chunk: " + longs_per_chunk);
		int usedChunks = marker.getUsedChunks();
		int chunkLongOffset = 0;
		long[] markerBits = marker.getBits();
		System.out.println(usedChunks + " chunks used");
		int mbMarked = 0;
		for (int affected_index = 0; affected_index < usedChunks; affected_index++) {
			long chunkIndex = affectedChunks[affected_index];
			int chunkX = (int)chunkIndex;
			int chunkZ = (int)(chunkIndex >> 32L);
			LevelChunk chunk = cache.getChunkNow(chunkX, chunkZ);
			if (chunk != null) {
				ByteBuf buf = Unpooled.directBuffer(longs_per_chunk + 3 << 3);
				HbmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new ClearMarkedChunkPack(marker.writeChunk(buf, chunkIndex, affected_index)));
				LevelChunkSection[] sections = chunk.getSections();
				long longID = asLong(chunkX, minSec, chunkZ);
				for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
					LevelChunkSection section = sections[sectionIndex];
					int blockLongOffset = 0;
					if (!section.hasOnlyAir()) {
						int sectionY = minSec + sectionIndex;
						longID = asLongY(longID, sectionY);
						int chunkAndSectionLongOffset = chunkLongOffset + (sectionIndex << 6);
						int maxOffset = chunkLongOffset + 64;
						for (int i = chunkAndSectionLongOffset; i < maxOffset && markerBits[i] == 0L; i++)
							blockLongOffset++;
						if (blockLongOffset != 64) {
							int air_id = ((MixinData<BlockState>)(Object)section.states.data).getPalette().idFor(AirState);
							if (!(((MixinData<BlockState>)(Object)section.states.data).getStorage() instanceof net.minecraft.util.ZeroBitStorage)) {
								SimpleBitStorage bitstorage = (SimpleBitStorage)((MixinData<BlockState>)(Object)section.states.data).getStorage();
								MagicEntry magicEntry = other_magic[bitstorage.getBits()];
								long maskedAirId = air_id & magicEntry.mask;
								DataLayer layer = (DataLayer)block_map.get(longID);
								if (layer == null) {
									layer = new DataLayer(0);
									block_map.put(longID, layer);
								}
								byte[] blockData = layer.getData();
								long[] rawData = bitstorage.getRaw();
								int realBlockLongOffset = chunkAndSectionLongOffset + blockLongOffset;
								for (int bigLong = blockLongOffset; bigLong < 64; bigLong++) {
									if (markerBits[realBlockLongOffset] != 0L) {
										int maxSingle = (bigLong << 6) + 64;
										for (int blockIndex = bigLong << 6; blockIndex < maxSingle; blockIndex++) {
											if (marker.get(realBlockLongOffset, blockIndex & 0x3F) != 0) {
												mbMarked++;
												int x = blockIndex & 0xF;
												int y = blockIndex >> 4 & 0xF;
												int z = blockIndex >> 8;
												int rindex = (y << 4 | z) << 4 | x;
												int e = (int)(rindex * magicEntry.divideMulLong + magicEntry.divideAddLong >> 32L >> magicEntry.divideShift);
												int k = (rindex - e * magicEntry.valuesPerLong) * magicEntry.bits;
												long j = rawData[e];
												rawData[e] = j & (~(magicEntry.mask << k)) | maskedAirId << k;
												int index = y << 8 | z << 4 | x;
												blockData[index >> 1] = (byte)(blockData[index >> 1] | 15 << 4 * (index & 0x1));
											}
										}
									}
									realBlockLongOffset++;
								}
							}
						}
					}
				}
				chunk.setUnsaved(true);
				chunkLongOffset += longs_per_chunk;
			}
		}
		System.out.println(mbMarked + " blocks have been checked in " + mbMarked + "ms, " + estimatedTime / 1000000L + "s, or " + estimatedTime / 1000000000L + "min, averaging " + estimatedTime / 60000000000L + " blocks per second.");
		HbmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new ClearSectionListPack(marker.affectedSections));
        for (long l : marker.affectedSections) {
            int x = x(l);
            int y = y(l);
            int z = z(l);
            LevelChunk chunk = level.getChunk(x, z);
            LevelChunkSection section = chunk.getSection(y(l));
            setAll(((MixinData<BlockState>)(Object)section.states.data).getPalette(), ((MixinData<BlockState>)(Object)section.states.data).getStorage());
            long longID = asLong(x, y + minSec, z);
            DataLayer layer = (DataLayer) block_map.get(longID);
            if (layer == null) {
                layer = new DataLayer(15);
                block_map.put(longID, layer);
            }
            layer.fill(15);
            section.recalcBlockCounts();
            chunk.setUnsaved(true);
        }
	}

	public static void setAll(Palette<BlockState> palette, BitStorage storage) {
		if (palette instanceof LinearPalette linearPalette) {
			int val = hasValue(palette);
			if (val != 0) {
				if (val != -1) {
					linearPalette.values[val] = linearPalette.values[0];
				}

				linearPalette.values[0] = AirState;
			}

			long[] data = storage.getRaw();
			System.arraycopy(zero_data, 0, data, 0, data.length);
		} else if (palette instanceof SingleValuePalette<BlockState> singleValuePalette) {
			singleValuePalette.value = AirState;
		} else if (!(palette instanceof HashMapPalette<BlockState> hashMapPalette)) {
			System.out.println("unknown class " + palette.getClass());
		} else {
			int value = hashMapPalette.idFor(AirState);
			if (value == 0) {
				long[] data = storage.getRaw();
				System.arraycopy(zero_data, 0, data, 0, data.length);
			} else {
				int bits = storage.getBits();
				long mask = (1L << bits) - 1L;
				long[] data = storage.getRaw();
				long longValue = 0L;
				int valuesPerLong = ((SimpleBitStorage)storage).valuesPerLong;

				for (int i = 0; i < valuesPerLong; i++) {
					longValue |= ((long)value & mask) << i * bits;
				}

				Arrays.fill(data, longValue);
			}
		}
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
		initial_loss = blockResistanceMap.get(getBlockFastNOPOS(level, xx, yy, zz));
		if (initial_loss >= radiusx*2*powermltp) {
			System.out.println("Explosion fully absorbed.");
			return;
		}
		level.setBlock(new BlockPos(xx, yy, zz), Blocks.AIR.defaultBlockState(), 3);
		if (dmgmltp > 0) fireEntityRays();
		switch (shape) {
			case 1 -> Ellipsoid();
			case 2 -> Cuboid();
			default -> Sphere();
		}

		//finishing the marking
		long estimatedTime = System.nanoTime() - startTime;
		System.out.println("Time taken for calc: " + estimatedTime/1000000 + "ms");
		removeMarked(estimatedTime);// remove the marked blocks
		contaminateStone();
		System.out.println(fire_small + " small rays"); // small rays
		System.out.println("called the mark function: " + blocks_marked + " times");

		// PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(),
		//       new MarkedBlockClearPack(marker));
	}

	private void Sphere() {

		for (int i = -raybigradiusx - 1; i < raybigradiusx + 2; ++i) { //possibly use shorts here
			for (int j = -raybigradiusx - 1; j < raybigradiusx + 2; ++j) {
				for (int k = -raybigradiusx - 1; k < raybigradiusx + 2; ++k) {
					if (secInRange(i, j, k)) {
						calculateRaysInSec(i, j, k);
					}
				}
			}
		}
	}

	private void Ellipsoid() {
		System.out.println("Beginning Ellipsoid()");
		for (int i = -raybigradiusx - 1; i < raybigradiusx + 2; ++i) { //possibly use shorts here
			for (int j = -raybigradiusy - 1; j < raybigradiusy + 2; ++j) {
				for (int k = -raybigradiusx - 1; k < raybigradiusx + 2; ++k) {
					if (secInRange(i, j, k)) {
						calculateRaysInSec(i, j, k);
					}
				}
			}
		}
	}

	private void Cuboid() {

		for (int i = -raybigradiusx - 1; i < raybigradiusx + 2; ++i) { //possibly use shorts here
			for (int j = -raybigradiusy - 1; j < raybigradiusy + 2; ++j) {
				for (int k = -raybigradiusx - 1; k < raybigradiusx + 2; ++k) {
					if (secInRange(i, j, k)) {
						calculateRaysInSec(i, j, k);
					}
				}
			}
		}
	}

	private boolean blockInRange(int dx, int dy, int dz) {
		long ddx = dx;
		long ddy = dy;
		long ddz = dz;
		long radiux = radiusx;
		long radiuy = radiusy;
		long distsq = switch (shape) {
            case 1 -> ddx * ddx + ddy * ddy * radiux * radiux / (radiuy * radiuy) + ddz * ddz; // ellipsoid
			case 2 -> ddx * ddx + ddz * ddz;
            default -> ddx * ddx + ddy * ddy + ddz * ddz; // sphere
        };
        return (distsq >= mindistsqInRange && distsq <= maxdistsqInRange);
	}

	private void calculateRaysInSec(int dcx, int dcy, int dcz)
	{
		int dx, dy, dz, dist, power;
		for (int i = 0; i < 16; ++i) {
			for (int j = 0; j < 16; ++j) {
				for (int k = 0; k < 16; ++k) {
					dx = (dcx << 4) + i - 7;
					dy = (dcy << 4) + j - 7;
					dz = (dcz << 4) + k - 7;
					if (blockInRange(dx, dy, dz)) {
						if (shape == 0) {
							power = rng.nextInt(radiusx-edge, radiusx);
							fireFastestSmallRay(dx, dy, dz, power*2*powermltp-initial_loss);
						}
						else {
							dist = (int)Math.sqrt(dx*dx + dy*dy + dz*dz);
							power = rng.nextInt(dist-edge, dist);
							fireFastestSmallRay(dx, dy, dz, power*2*powermltp-initial_loss);
						}
					}
				}
			}
		}
	}

	private static Random rng = new Random();

	public void fireFastestSmallRay(int finaldx, int finaldy, int finaldz, int power)
	{ //endxyz - delta of small ray end point from center, startxyz - raw beginning point of small ray
		int startx, starty, startz;
		startx = xx;
		starty = yy;
		startz = zz;
		fire_small++;
		//System.out.println("Endx: " + endx + ", endy: " + endy + ", endz: " + endz);
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
		switch (which) {
			case 4: //x y z
				if (starty < minBuildHeight || starty >= maxBuildHeight)
					break;
				for (int i = 0; i < total; ++i) {
					if (i >= total - edge) { //feather edge
						if (rng.nextInt(edge) < 1)
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
					power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
					if (power <= 0)
						break;
					if (finaldx > 0) {
						power = markBlock(level, startx+1, starty, startz, power, adx, ady, adz, which);
					} else {
						power = markBlock(level, startx-1, starty, startz, power, adx, ady, adz, which);
					}
					if (power <= 0)
						break;
					if (finaldy > 0) {
						power = markBlock(level, startx, starty+1, startz, power, adx, ady, adz, which);
					} else {
						power = markBlock(level, startx, starty-1, startz, power, adx, ady, adz, which);
					}
					if (power <= 0)
						break;
					if (finaldz > 0) {
						power = markBlock(level, startx, starty, startz+1, power, adx, ady, adz, which);
					} else {
						power = markBlock(level, startx, starty, startz-1, power, adx, ady, adz, which);
					}
					if (power <= 0)
						break;
				}
				break;
			case 1: //x
				if (starty < minBuildHeight || starty >= maxBuildHeight)
					break;
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
						if (starty < minBuildHeight || starty >= maxBuildHeight)
							break;
						power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
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
						power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
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
					power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
					if (power <= 0)
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
						power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
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
						power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
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
					power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
					if (power <= 0)
						break;
				}
				break;
			case 3: //z
				if (starty < minBuildHeight || starty >= maxBuildHeight)
					break;
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
						power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
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
						power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
						if (power <= 0)
							break;
					}
					tempy += ady;
					if (finaldz > 0)
						++startz;
					else
						startz--;
					power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
					if (power <= 0)
						break;
				}
				break;
			case 5: //x y
				if (starty < minBuildHeight || starty >= maxBuildHeight)
					break;
				for (int i = 0; i < total; ++i) {
					if (i >= total - edge) { //feather edge
						if (rng.nextInt(edge) < 1)
							break;
					}
					if (tempz > base) {
						if (finaldz > 0)
							++startz;
						else
							startz--;
						tempz -= base;
						power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
						if (power <= 0)
							break;
					}
					tempz += adz;
					if (finaldx > 0) {
						power = markBlock(level, startx+1, starty, startz, power, adx, ady, adz, which);
					}
					else {
						power = markBlock(level, startx-1, starty, startz, power, adx, ady, adz, which);
					}
					if (power <= 0)
						break;
					if (finaldy > 0)
						++starty;
					else
						starty--;
					if (starty < minBuildHeight || starty >= maxBuildHeight)
						break;
					power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
					if (power <= 0)
						break;
					if (finaldx > 0) {
						++startx;
					}
					else {
						startx--;
					}
					power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
					if (power <= 0)
						break;
				}
				break;
			case 6: //x z
				if (starty < minBuildHeight || starty >= maxBuildHeight)
					break;
				for (int i = 0; i < total; ++i) {
					if (i >= total - edge) { //feather edge
						if (rng.nextInt(edge) < 1)
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
						power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
						if (power <= 0)
							break;
					}
					tempy += ady;
					if (finaldx > 0) {
						power = markBlock(level, startx+1, starty, startz, power, adx, ady, adz, which);
					}
					else {
						power = markBlock(level, startx-1, starty, startz, power, adx, ady, adz, which);
					}
					if (power <= 0)
						break;
					if (finaldz > 0)
						++startz;
					else
						startz--;
					power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
					if (power <= 0)
						break;
					if (finaldx > 0) {
						++startx;
					}
					else {
						startx--;
					}
					power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
					if (power <= 0)
						break;
				}
				break;
			case 7: //y z
				if (starty < minBuildHeight || starty >= maxBuildHeight)
					break;
				for (int i = 0; i < total; ++i) {
					if (i >= total - edge) { //feather edge
						if (rng.nextInt(edge) < 1)
							break;
					}
					if (tempx > base) {
						if (finaldx > 0) {
							++startx;
						} else
							startx--;
						tempx -= base;
						power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
						if (power <= 0)
							break;
					}
					tempx += adx;
					if (finaldz > 0) {
						power = markBlock(level, startx, starty, startz+1, power, adx, ady, adz, which);
					}
					else {
						power = markBlock(level, startx, starty, startz-1, power, adx, ady, adz, which);
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
					power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
					if (power <= 0)
						break;
					if (finaldz > 0)
						++startz;
					else
						startz--;
					power = markBlock(level, startx, starty, startz, power, adx, ady, adz, which);
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
			// skip removed/dead stuff
			if (e.isRemoved()) return false;
			// skip spectators
			if (e instanceof Player p && p.isSpectator()) return false;
			// positive health
			if (e instanceof LivingEntity en && en.getHealth() <= 0F) return false;
			//distance
			double ddx = e.getX()-x, ddy = e.getEyeY()-y, ddz = e.getZ()-z;
			double entdistance = switch (shape) {
				case 1 -> ddx*ddx + ddy*ddy*radiusx*radiusx/radiusy/radiusy + ddz*ddz;
				case 2 -> ddx*ddx + ddz*ddz;
				default -> ddx*ddx + ddy*ddy + ddz*ddz;
			};
            return !(entdistance > radiusx * radiusx);
        });
		float damage, power, distance, distconst, radiussqrt;
		//int text = 0;
		for (Entity entity : entities)
		{
			power = fireRayAtEntity(entity); //ray remaining power
			if (power > 0) { //calc: damage = dmgmltp * power/maxpower * (distance - radius) ^ 2 / radius -> power * ((distance-radius)/radius)^2
				dx = (float)(entity.getX()-x);
				dy = (float)(entity.getEyeY()-y);
				dz = (float)(entity.getZ()-z);
				distance = switch (shape) {
					case 1 -> (float)Math.sqrt(dx*dx + dy*dy*radiusx*radiusx/radiusy/radiusy + dz*dz);
					case 2 -> Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
					default -> (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
				};
				distconst = distance-radiusx;
				//radiussqrt = (float)Math.sqrt(radiusx);
				damage = power * distconst*distconst / radiusx / radiusx;
				if (entity instanceof LivingEntity lentity) lentity.hurt(level.damageSources().explosion(ssource, entity), damage);
				//if (text < 30) System.out.println("Fired rat at entity: " + entity + " which has health: " + entity.getHealth() + ", at dx: " + dx + ", dy: " + dy + ", dz: " + dz + ", ray reached entity with power: " + power + ", entity was at distance: " + distance + ", it dealt damage: " + damage);
				if (!(entity instanceof LivingEntity lentity && lentity.getHealth() < 0F)) {
					knockconst = knockbackmltp * damage / Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz))) / dmgmltp;
					entity.addDeltaMovement(new Vec3(knockconst * dx, knockconst * dy, knockconst * dz));
					entity.hurtMarked = true;
				}
				//text++;
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


	public boolean secInRange(int dcx, int dcy, int dcz) //corner calculation
	{
		int dx, dy, dz, closedistsq, fardistsq;
		if (dcx == 0)
			dx = 0;
		else if (dcx > 0)
			dx = (dcx << 4) - 7;
		else
			dx = (dcx << 4) + 8;
		if (dcy == 0)
			dy = 0;
		else if (dcy > 0)
			dy = (dcy << 4) - 7;
		else
			dy = (dcy << 4) + 8;
		if (dcz == 0)
			dz = 0;
		else if (dcz > 0)
			dz = (dcz << 4) - 7;
		else
			dz = (dcz << 4) + 8;
		closedistsq = switch (shape) {
			case 1 -> dx * dx + dy * dy * radiusx / radiusy * radiusx / radiusy + dz * dz;
			default -> dx * dx + dy * dy + dz * dz;
		};
		//get furthest corner
		if (dcx == 0)
			dx = 8;
		else if (dcx > 0)
			dx = (dcx << 4) + 8;
		else
			dx = (dcx << 4) - 7;
		if (dcy == 0)
			dy = 8;
		else if (dcy > 0)
			dy = (dcy << 4) + 8;
		else
			dy = (dcy << 4) - 7;
		if (dcz == 0)
			dz = 8;
		else if (dcz > 0)
			dz = (dcz << 4) + 8;
		else
			dz = (dcz << 4) - 7;
		fardistsq = switch (shape) {
			case 1 -> dx * dx + dy * dy * radiusx / radiusy * radiusx / radiusy + dz * dz;
			default -> dx * dx + dy * dy + dz * dz;
		};
		return (fardistsq >= mindistsqInRange && closedistsq <= maxdistsqInRange);
	}




	private int blocks_marked = 0;

	private int markBlock(ServerLevel level, int x, int y, int z, int power, int adx, int ady, int adz, int which) {
		//int res = 0;/
		int res = blockResistanceMap.get(getBlockFastNOPOS(level, x, y, z));
		if (res < power) {
			marker.markBlock(x >> 4, z >> 4, (y - minBuildHeight) >> 4,
					(x & 15), (y & 15), (z & 15));
			blocks_marked++;
			if (which < 4) return power - (res * rayradiusx / (adx+ady+adz)); // x, y and z rays
			else if (which == 4) return power - (res*10*rayradiusx/radiusx/23); // xyz rays
			else return power - (res * 2 * rayradiusx / 3 / (adx+ady+adz)); //xy, yz and xz rays
		}
		return 0;
	}

	private float checkRes(ServerLevel level, int x, int y, int z, float power) {
		if (y >= level.getMinBuildHeight() && y < level.getMaxBuildHeight()) return blockResistanceMap.get(getBlockFastNOPOS(level, x, y, z));
		else return 0;
	}

	//temp for no bpos
	private BlockState getBlockFastNOPOS(ServerLevel level, int x, int y, int z) {
		LevelChunkSection section = level.getChunk(x >> 4, z >> 4)
				.getSection((y - minBuildHeight) >> 4);
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
}