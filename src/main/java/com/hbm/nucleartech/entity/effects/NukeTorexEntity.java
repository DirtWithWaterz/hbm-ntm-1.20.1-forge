package com.hbm.nucleartech.entity.effects;

import java.util.ArrayList;

import com.hbm.interfaces.IConstantRenderer;

import com.hbm.nucleartech.entity.HbmEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hbm.nucleartech.entity.client.NukeTorexRenderer.FLASH_BASE_DURATION;

//import static com.hbm.entity.logic.EntityNukeExplosionMK5.shockSpeed;

/*
 * Toroidal Convection Simulation Explosion Effect
 * Tor                            Ex
 */
public class NukeTorexEntity extends Entity implements IConstantRenderer {

	public static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(NukeTorexEntity.class, EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Byte> TYPE = SynchedEntityData.defineId(NukeTorexEntity.class, EntityDataSerializers.BYTE);

	public static final EntityDataSerializer<Double> DOUBLE = EntityDataSerializer.simple(FriendlyByteBuf::writeDouble, FriendlyByteBuf::readDouble);

	public static final EntityDataAccessor<Double> POS_X = SynchedEntityData.defineId(NukeTorexEntity.class, DOUBLE);
	public static final EntityDataAccessor<Double> POS_Y = SynchedEntityData.defineId(NukeTorexEntity.class, DOUBLE);
	public static final EntityDataAccessor<Double> POS_Z = SynchedEntityData.defineId(NukeTorexEntity.class, DOUBLE);

	static {

		EntityDataSerializers.registerSerializer(DOUBLE);
	}

	public static final int firstCondenseHeight = 130;
	public static final int secondCondenseHeight = 170;
	public static final int maxCloudlets = 20_000;

	public static final int shockSpeed = 4;

	//Nuke colors
	public static final double nr1 = 2.5;
	public static final double ng1 = 1.3;
	public static final double nb1 = 0.4;
	public static final double nr2 = 0.1;
	public static final double ng2 = 0.075;
	public static final double nb2 = 0.05;

	//Balefire colors
	public static final double br1 = 1;
	public static final double bg1 = 2;
	public static final double bb1 = 0.5;
	public static final double br2 = 0.1;
	public static final double bg2 = 0.1;
	public static final double bb2 = 0.1;

	public double coreHeight = 3;
	public double convectionHeight = 3;
	public double torusWidth = 3;
	public double rollerSize = 1;
	public double heat = 1;
	public double lastSpawnY = -1;
	public ArrayList<Cloudlet> cloudlets = new ArrayList<>();
	public long startTime = 0;
	public int maxAge = 3000;
	public float humidity = -1;

	public NukeTorexEntity(EntityType<? extends NukeTorexEntity> type, Level level) {
		super(type, level);
		this.noCulling = true;
		this.fireImmune();
		this.setNoGravity(true);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(SCALE, 1.0F);
		this.entityData.define(TYPE, (byte) 0);
		this.entityData.define(POS_X, 0d);
		this.entityData.define(POS_Y, 0d);
		this.entityData.define(POS_Z, 0d);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		if (compound.contains("scale")) {
			setScale(compound.getFloat("scale"));
		}
		if (compound.contains("type")) {
			this.entityData.set(TYPE, compound.getByte("type"));
		}
		if(compound.contains("pos")) {
			
			CompoundTag posTag = compound.getCompound("pos");
			
			this.entityData.set(POS_X, posTag.getDouble("x"));
			this.entityData.set(POS_Y, posTag.getDouble("y"));
			this.entityData.set(POS_Z, posTag.getDouble("z"));
		}
		if (compound.contains("time")) {
			startTime = compound.getLong("time");
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		compound.putFloat("scale", this.entityData.get(SCALE));
		compound.putByte("type", this.entityData.get(TYPE));
		
		CompoundTag posTag = new CompoundTag();
		
		posTag.putDouble("x", getPos().x);
		posTag.putDouble("y", getPos().y);
		posTag.putDouble("z", getPos().z);

		compound.putLong("time", startTime);
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {

		return true;
	}

	@Override
	public void tick() {
		super.tick();
//		System.out.println("[Debug] ticking");
		if (!this.level().isClientSide) {
			long time = this.level().getGameTime();
			if (time < startTime || time - startTime > maxAge) {
				this.discard();
			}
			if(this.tickCount > ((float) this.getScale() * FLASH_BASE_DURATION) && this.tickCount % 20 == 0) {

				Player player = level().getNearestPlayer(this, -1f);
				if(player != null)
					this.setPos(player.position());
			}
		} else {
			double s = this.getScale();
			double cs = 1.5;
			if (this.tickCount == 1) {
				this.setScale((float) s);
			}

			if (humidity == -1) {
				humidity = this.level().getBiome(this.blockPosition()).value().getModifiedClimateSettings().downfall();
			}

			if (lastSpawnY == -1) {
				lastSpawnY = getPos().y - 3;
			}

			int spawnTarget = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) Math.floor(getPos().x), (int) Math.floor(getPos().z)) - 3; // Removed Math.max(...,1) to allow negative Y
			spawnTarget = Math.max(spawnTarget, this.level().getMinBuildHeight() + 1); // Clamp to world bottom +1 to avoid invalid Y
			double moveSpeed = 0.5D;

			if (Math.abs(spawnTarget - lastSpawnY) < moveSpeed) {
				lastSpawnY = spawnTarget;
			} else {
				lastSpawnY += moveSpeed * Math.signum(spawnTarget - lastSpawnY);
			}

			// spawn mush clouds
			double range = (torusWidth - rollerSize) * 0.5;
			double simSpeed = getSimulationSpeed();
			int lifetime = Math.min((this.tickCount * this.tickCount) + 600, maxAge - this.tickCount + 200);
			int toSpawn = (int) (0.6 * Math.min(Math.max(0, maxCloudlets - cloudlets.size()), Math.ceil(10 * simSpeed * simSpeed * Math.min(1, 1200 / (double) lifetime))));


			for (int i = 0; i < toSpawn; i++) {
				double x = getPos().x + this.random.nextGaussian() * range;
				double z = getPos().z + this.random.nextGaussian() * range;
				Cloudlet cloud = new Cloudlet(x, lastSpawnY, z, (float) (this.random.nextDouble() * 2D * Math.PI), 0, lifetime);
				cloud.setScale((float) (Math.sqrt(s) * 3 + this.tickCount * 0.0025 * s), (float) (Math.sqrt(s) * 3 + this.tickCount * 0.0025 * 6 * cs * s));
				cloudlets.add(cloud);
			}

			if (this.tickCount < 120 * s) {
				this.level().setSkyFlashTime(2);
			}

			// spawn shock clouds
			if (this.tickCount * shockSpeed < 200) {

				int cloudCount = (int) Math.min(this.tickCount * shockSpeed, 100);
				int shockLife = (int) Math.max(s * 300 - this.tickCount * shockSpeed * 10, 60);

				for (int i = 0; i < cloudCount; i++) {
					Vec3 vec = new Vec3((this.tickCount + this.random.nextDouble() * 2 - 2) * shockSpeed, 0, 0);
					float rot = (float) (Math.PI * 2 * this.random.nextDouble());
					vec = vec.yRot(rot);
					double shockY = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) Math.floor(vec.x + getPos().x + 1), (int) Math.floor(vec.z + getPos().z)) + 3;
					Cloudlet cloud = new Cloudlet(vec.x + getPos().x, shockY, vec.z + getPos().z, rot, 0, shockLife, TorexType.SHOCK);
					cloud.setScale((float) s * 5F, (float) s * 2F).setMotion(1.0); // Remove clamp for immediate expansion
					cloudlets.add(cloud);
				}
			}

			// spawn ring clouds
			if (this.tickCount < 200) {
				lifetime *= (int) s;
				for (int i = 0; i < 2; i++) {
					Cloudlet cloud = new Cloudlet(getPos().x, getPos().y + coreHeight, getPos().z, (float) (this.random.nextDouble() * 2D * Math.PI), 0, lifetime, TorexType.RING);
					cloud.setScale((float) (Math.sqrt(s) * cs + this.tickCount * 0.0015 * s), (float) (Math.sqrt(s) * cs + this.tickCount * 0.0015 * 6 * cs * s));
					cloudlets.add(cloud);
				}
			}

			if (this.humidity > 0 && this.tickCount * shockSpeed < 180) {
				// spawn lower condensation clouds
				spawnCondensationClouds(this.tickCount * shockSpeed - 8, this.humidity, firstCondenseHeight, 80, 4, s, cs);

				// spawn upper condensation clouds
				spawnCondensationClouds(this.tickCount * shockSpeed - 8, this.humidity, secondCondenseHeight, 80, 2, s, cs);
			}



			cloudlets.removeIf(x -> x.isDead);
			for (Cloudlet cloud : cloudlets) {
				cloud.update();
			}
//        System.out.println(cloudlets.size());

			coreHeight += 0.15/* * s*/;
			torusWidth += 0.05/* * s*/;
			rollerSize = torusWidth * 0.35;
			convectionHeight = coreHeight + rollerSize;

			int maxHeat = (int) (50 * s * s);
			heat = maxHeat - Math.pow((double) (maxHeat * this.tickCount) / maxAge, 0.6);
		}
	}

	public void spawnCondensationClouds(double range, float humidity, int height, int count, int spreadAngle, double s, double cs) {
		if (range > 0 && (getPos().y + range) > height) {

			for (int i = 0; i < (int) (5 * humidity * count / (double) spreadAngle); i++) {
				for (int j = 1; j < spreadAngle; j++) {
					float angle = (float) (Math.PI * 2 * this.random.nextDouble());
					Vec3 vec = new Vec3(0, range, 0);
					vec = vec.zRot((float) Math.acos((height - getPos().y) / (range)) + (float) Math.toRadians(humidity * humidity * 90 * j * (0.1 * this.random.nextDouble() - 0.05)));
					vec = vec.yRot(angle);
					Cloudlet cloud = new Cloudlet(getPos().x + vec.x, getPos().y + vec.y, getPos().z + vec.z, angle, 0, (int) ((20 + range / 10) * (1 + this.random.nextDouble() * 0.1)), TorexType.CONDENSATION);
					cloud.setScale(3F * (float) (cs * s), 4F * (float) (cs * s));
					cloudlets.add(cloud);
				}
			}
		}
	}

	public NukeTorexEntity setScale(float scale) {
		if (!this.level().isClientSide) {
			this.entityData.set(SCALE, scale);
		}
		this.coreHeight = this.coreHeight * scale;
		this.convectionHeight = this.convectionHeight * scale;
		this.torusWidth = this.torusWidth * scale;
		this.rollerSize = this.rollerSize * scale;
		this.maxAge = (int) (45 * 20 * scale);
		return this;
	}

	public NukeTorexEntity setTorexType(int type) {
		this.entityData.set(TYPE, (byte) type);
		return this;
	}

	public double getScale() {
		return this.entityData.get(SCALE);
	}

	public Vec3 getPos() {

		return new Vec3(this.entityData.get(POS_X), this.entityData.get(POS_Y), this.entityData.get(POS_Z));
	}

	public byte getTorexType() {
		return this.entityData.get(TYPE);
	}

	public double getSimulationSpeed() {

		int simSlow = maxAge / 4;
		int life = this.tickCount;

		if (life > maxAge) {
			return 0D;
		}

		if (life > simSlow) {
			return 1D - ((double) (life - simSlow) / (double) (maxAge - simSlow));
		}

		return 1.0D;
	}

	public float getAlpha() {

		int fadeOut = maxAge * 3 / 4;
		int life = this.tickCount;

		if (life > fadeOut) {
			float fac = (float) (life - fadeOut) / (float) (maxAge - fadeOut);
			return 1F - fac;
		}

		return 1.0F;
	}

	public Vec3 getInterpColor(double interp, byte type) {
		if (type == 0) {
			return new Vec3(
					(nr2 + (nr1 - nr2) * interp),
					(ng2 + (ng1 - ng2) * interp),
					(nb2 + (nb1 - nb2) * interp));
		}
		return new Vec3(
				(br2 + (br1 - br2) * interp),
				(bg2 + (bg1 - bg2) * interp),
				(bb2 + (bb1 - bb2) * interp));
	}

	public class Cloudlet {

		public double posX;
		public double posY;
		public double posZ;
		public double prevPosX;
		public double prevPosY;
		public double prevPosZ;
		public double motionX;
		public double motionY;
		public double motionZ;
		public int age;
		public int cloudletLife;
		public float angle;
		public boolean isDead = false;
		float rangeMod = 1.0F;
		public float colorMod = 1.0F;
		public Vec3 color;
		public Vec3 prevColor;
		public TorexType type;
		private float startingScale = 3F;
		private float growingScale = 5F;

		public Cloudlet(double posX, double posY, double posZ, float angle, int age, int maxAge) {
			this(posX, posY, posZ, angle, age, maxAge, TorexType.STANDARD);
		}

		public Cloudlet(double posX, double posY, double posZ, float angle, int age, int maxAge, TorexType type) {
			this.posX = posX;
			this.posY = posY;
			this.posZ = posZ;
			this.age = age;
			this.cloudletLife = maxAge;
			this.angle = angle;
			this.rangeMod = 0.3F + NukeTorexEntity.this.random.nextFloat() * 0.7F;
			this.colorMod = 0.8F + NukeTorexEntity.this.random.nextFloat() * 0.2F;
			this.type = type;

			this.updateColor();
		}

		private double motionMult = 1F;
		private double motionConvectionMult = 0.5F;
		private double motionLiftMult = 0.625f;
		private double motionRingMult = 0.5F;
		private double motionCondensationMult = 1F;
		private double motionShockwaveMult = 1F;


		private void update() {
			age++;

			if (age > cloudletLife) {
				this.isDead = true;
			}

			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;

			Vec3 simPos = new Vec3(getPos().x - this.posX, 0, getPos().z - this.posZ);
			double simPosX = getPos().x + simPos.length();
			double simPosZ = getPos().z + 0D;

			if (this.type == TorexType.STANDARD) {
				Vec3 convection = getConvectionMotion(simPosX, simPosZ);
				Vec3 lift = getLiftMotion(simPosX, simPosZ);

				double factor = Mth.clamp((this.posY - getPos().y) / NukeTorexEntity.this.coreHeight, 0, 1);
				this.motionX = convection.x * factor + lift.x * (1D - factor);
				this.motionY = convection.y * factor + lift.y * (1D - factor);
				this.motionZ = convection.z * factor + lift.z * (1D - factor);
			} else if (this.type == TorexType.RING) {
				Vec3 motion = getRingMotion(simPosX, simPosZ);
				this.motionX = motion.x;
				this.motionY = motion.y;
				this.motionZ = motion.z;
			} else if (this.type == TorexType.CONDENSATION) {
				Vec3 motion = getCondensationMotion();
				this.motionX = motion.x;
				this.motionY = motion.y;
				this.motionZ = motion.z;
			} else if (this.type == TorexType.SHOCK) {
				Vec3 motion = getShockwaveMotion();
				this.motionX = motion.x;
				this.motionY = motion.y;
				this.motionZ = motion.z;
			}

			double mult = this.motionMult * getSimulationSpeed();

			this.posX += this.motionX * mult;
			this.posY += this.motionY * mult;
			this.posZ += this.motionZ * mult;

			this.updateColor();
		}

		private Vec3 getCondensationMotion() {
			Vec3 delta = new Vec3(posX - getPos().x, 0, posZ - getPos().z).normalize();
			double speed = motionCondensationMult * NukeTorexEntity.this.getScale() * 0.125D;
			delta = new Vec3(delta.x * speed, 0, delta.z * speed);
			return delta;
		}

		private Vec3 getShockwaveMotion() {
			Vec3 delta = new Vec3(posX - getPos().x, 0, posZ - getPos().z).normalize();
			double speed = motionShockwaveMult * NukeTorexEntity.this.getScale() * 0.5D; // Increased from 0.25D for faster expansion
			delta = new Vec3(delta.x * speed, 0, delta.z * speed);
			return delta;
		}

		private Vec3 getRingMotion(double simPosX, double simPosZ) {

			if (simPosX > getPos().x + torusWidth * 2)
				return Vec3.ZERO;

			/* the position of the torus' outer ring center */
			Vec3 torusPos = new Vec3(
					(getPos().x + torusWidth),
					(getPos().y + coreHeight * 0.5),
					getPos().z);

			/* the difference between the cloudlet and the torus' ring center */
			Vec3 delta = new Vec3(torusPos.x - simPosX, torusPos.y - this.posY, torusPos.z - simPosZ);

			/* the distance this cloudlet wants to achieve to the torus' ring center */
			double roller = NukeTorexEntity.this.rollerSize * this.rangeMod * 0.25;
			/* the distance between this cloudlet and the torus' outer ring perimeter */
			double dist = delta.length() / roller - 1D;

			/* euler function based on how far the cloudlet is away from the perimeter */
			double func = 1D - Math.pow(Math.E, -dist); // [0;1]
			/* just an approximation, but it's good enough */
			float angle = (float) (func * Math.PI * 0.5D); // [0;90°]

			/* vector going from the ring center in the direction of the cloudlet, stopping at the perimeter */
			Vec3 rot = new Vec3(-delta.x / dist, -delta.y / dist, -delta.z / dist);
			/* rotate by the approximate angle */
			rot = rot.zRot(angle);

			/* the direction from the cloudlet to the target position on the perimeter */
			Vec3 motion = new Vec3(
					torusPos.x + rot.x - simPosX,
					torusPos.y + rot.y - this.posY,
					torusPos.z + rot.z - simPosZ);

			motion = motion.normalize();
			motion = motion.yRot(this.angle);
			double speed = motionRingMult * 0.5D;
			motion = new Vec3(motion.x * speed, motion.y * speed, motion.z * speed);

			return motion;
		}

		/* simulated on a 2D-plane along the X/Y axis */
		private Vec3 getConvectionMotion(double simPosX, double simPosZ) {

			if (simPosX > getPos().x + torusWidth * 2)
				return Vec3.ZERO;

			/* the position of the torus' outer ring center */
			Vec3 torusPos = new Vec3(
					(getPos().x + torusWidth),
					(getPos().y + coreHeight),
					getPos().z);

			/* the difference between the cloudlet and the torus' ring center */
			Vec3 delta = new Vec3(torusPos.x - simPosX, torusPos.y - this.posY, torusPos.z - simPosZ);

			/* the distance this cloudlet wants to achieve to the torus' ring center */
			double roller = NukeTorexEntity.this.rollerSize * this.rangeMod;
			/* the distance between this cloudlet and the torus' outer ring perimeter */
			double dist = delta.length() / roller - 1D;

			/* euler function based on how far the cloudlet is away from the perimeter */
			double func = 1D - Math.pow(Math.E, -dist); // [0;1]
			/* just an approximation, but it's good enough */
			float angle = (float) (func * Math.PI * 0.5D); // [0;90°]

			/* vector going from the ring center in the direction of the cloudlet, stopping at the perimeter */
			Vec3 rot = new Vec3(-delta.x / dist, -delta.y / dist, -delta.z / dist);
			/* rotate by the approximate angle */
			rot = rot.zRot(angle);

			/* the direction from the cloudlet to the target position on the perimeter */
			Vec3 motion = new Vec3(
					torusPos.x + rot.x - simPosX,
					torusPos.y + rot.y - this.posY,
					torusPos.z + rot.z - simPosZ);

			motion = motion.normalize();
			motion = motion.yRot(this.angle);

			motion = new Vec3(motion.x * motionConvectionMult, motion.y * motionConvectionMult * 2.0, motion.z * motionConvectionMult); // Double vertical speed for faster rise

			return motion;
		}

		private Vec3 getLiftMotion(double simPosX, double simPosZ) {
			double scale = Mth.clamp(1D - (simPosX - (getPos().x + torusWidth)), 0, 1) * motionLiftMult;

			Vec3 motion = new Vec3(getPos().x - this.posX, (getPos().y + convectionHeight) - this.posY, getPos().z - this.posZ);

			motion = motion.normalize();
			motion = new Vec3(motion.x * scale, motion.y * scale, motion.z * scale);

			return motion;
		}

		private void updateColor() {
			this.prevColor = this.color;

			double exX = getPos().x;
			double exY = getPos().y + NukeTorexEntity.this.coreHeight;
			double exZ = getPos().z;

			double distX = exX - posX;
			double distY = exY - posY;
			double distZ = exZ - posZ;

			double distSq = distX * distX + distY * distY + distZ * distZ;
			distSq /= this.type == TorexType.SHOCK ? NukeTorexEntity.this.heat * 3 : NukeTorexEntity.this.heat;

			double col = 2D / Math.max(distSq, 1); //col goes from 2-0

			byte type = NukeTorexEntity.this.getTorexType();

			this.color = NukeTorexEntity.this.getInterpColor(col, type);
		}

		public Vec3 getInterpPos(float interp) {
			return new Vec3(
					prevPosX + (posX - prevPosX) * interp,
					prevPosY + (posY - prevPosY) * interp,
					prevPosZ + (posZ - prevPosZ) * interp);
		}

		public Vec3 getInterpColor(float interp) {

			if (this.type == TorexType.CONDENSATION) {
				return new Vec3(1F, 1F, 1F);
			}

			double greying = 0;

			if (this.type == TorexType.RING) {
				greying += 0.05;
			}

			return new Vec3(
					(prevColor.x + (color.x - prevColor.x) * interp) + greying,
					(prevColor.y + (color.y - prevColor.y) * interp) + greying,
					(prevColor.z + (color.z - prevColor.z) * interp) + greying);
		}

		public float getAlpha() {
			float alpha = (1F - ((float) age / (float) cloudletLife)) * NukeTorexEntity.this.getAlpha();
			if (this.type == TorexType.CONDENSATION) alpha *= 0.25;
//        System.out.println("[Debug] alpha: " + alpha);
			return Mth.clamp(alpha, 0.0001F, 1F);
		}


		public float getScale() {
			return startingScale + ((float) age / (float) cloudletLife) * growingScale;
		}

		public Cloudlet setScale(float start, float grow) {
			this.startingScale = start;
			this.growingScale = grow;
			return this;
		}

		public Cloudlet setMotion(double mult) {
			this.motionMult = mult;
			return this;
		}
	}

	public enum TorexType {
		STANDARD,
		RING,
		CONDENSATION,
		SHOCK
	}
	
	private NukeTorexEntity setTorexPos(double x, double y, double z) {

		this.entityData.set(POS_X, x);
		this.entityData.set(POS_Y, y);
		this.entityData.set(POS_Z, z);
		
		return this;
	}

	public static void statFac(Level level, double x, double y, double z, float scale) {
		NukeTorexEntity torex = new NukeTorexEntity(HbmEntities.NUKE_TOREX.get(), level).setScale(Mth.clamp(scale * 0.01F, 0.25F, 5F)).setTorexPos(x, y, z);

		torex.setPos(x, y, z);

		torex.startTime = level.getGameTime();
		level.addFreshEntity(torex);
	}

	public static void statFacBale(Level level, double x, double y, double z, float scale) {
		NukeTorexEntity torex = new NukeTorexEntity(HbmEntities.NUKE_TOREX.get(), level).setScale(Mth.clamp(scale * 0.01F, 0.25F, 5F)).setTorexType(1).setTorexPos(x, y, z);

		torex.setPos(x, y, z);

		torex.startTime = level.getGameTime();
		level.addFreshEntity(torex);
	}

	// Override to set entity dimensions (replace with actual dimensions if needed)
	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return EntityDimensions.scalable(20F, 40F);
	}
}