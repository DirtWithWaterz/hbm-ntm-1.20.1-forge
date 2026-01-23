package com.hbm.nucleartech.block.entity;

import com.hbm.nucleartech.Config;
import com.hbm.nucleartech.block.RegisterBlocks;
import com.hbm.nucleartech.item.RegisterItems;
import com.hbm.nucleartech.network.HbmPacketHandler;
import com.hbm.nucleartech.network.packet.ClientboundGraphiteBlockPacket;
import com.hbm.nucleartech.network.packet.ClientboundSpawnNeutronParticlePacket;
import com.hbm.nucleartech.render.amlfrom1710.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.RenderUtils;

public class GraphiteBlockEntity extends BlockEntity implements GeoBlockEntity {

    public static final String BASE_ANIM = "animation.graphite_block.base";
    public static final String EMPTY_ANIM = "animation.graphite_block.empty";
    public static final String URANIUM_ANIM = "animation.graphite_block.uranium";
    public static final String PLUTONIUM_ANIM = "animation.graphite_block.plutonium";
    public static final String RADIUM_ANIM = "animation.graphite_block.radium";
    public static final String CONTROL_ON_ANIM = "animation.graphite_block.control_on";
    public static final String CONTROL_OFF_ANIM = "animation.graphite_block.control_off";

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public void sync() {
        if (level == null) return;
        if (!level.isClientSide()) {
            BlockState state = getBlockState();
            // send block update to clients; this will cause the client to request the BE update packet
            level.sendBlockUpdated(worldPosition, state, state, 3);
            // mark dirty so world will save
            setChanged();
        }
    }

    public int type = -1;
    public int oldType = -11;

    private static final float maxHeat = 1000;
    public float heat = 0;

    private static final int maxDepletion = 100000;
    public int depletion = 0;

    public float depletionPercentage;

    public int neutrons = 0;
    public int lastNeutrons = 0;

    public GraphiteBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(RegisterBlockEntities.GRAPHITE_BLOCK_ENTITY.get(), pPos, pBlockState);

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public void drops() {

        if (level == null || level.isClientSide()) return;

        ItemStack blockDrop = asItemStackWithTag();

        ItemStack rodDrop = switch (this.type) {

            case 1 -> RegisterItems.URANIUM_PILE_ROD.get().getDefaultInstance();
            case 2 -> RegisterItems.PLUTONIUM_PILE_ROD.get().getDefaultInstance();
            case 3 -> RegisterItems.RADIUM_PILE_ROD.get().getDefaultInstance();
            case 4, 5 -> RegisterItems.BORON_PILE_ROD.get().getDefaultInstance();
            default -> null;
        };

        // spawn item at centre of block
        ItemEntity graphiteItemEntity = new ItemEntity(level,
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5,
                blockDrop);
        level.addFreshEntity(graphiteItemEntity);

        if(rodDrop == null) return;

        ItemEntity pileRodItemEntity = new ItemEntity(level,
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5,
                rodDrop);
        level.addFreshEntity(pileRodItemEntity);
    }

    public ItemStack asItemStackWithTag() {

        ItemStack stack = new ItemStack(RegisterBlocks.GRAPHITE_BLOCK.get());

        CompoundTag beTag = new CompoundTag();
        beTag.putInt("graphite_block.type", this.type == -1 ? -1 : 0);
        beTag.putInt("graphite_block.old_type", -11);
        beTag.putFloat("graphite_block.heat", 0);
        beTag.putInt("graphite_block.depletion", 0);
        beTag.putInt("graphite_block.neutrons", 0);
        beTag.putInt("graphite_block.last_neutrons", 0);

        stack.addTagElement("BlockEntityTag", beTag);

        return stack;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {

        pTag.putInt("graphite_block.type", this.type);
        pTag.putInt("graphite_block.old_type", this.oldType);
        pTag.putFloat("graphite_block.heat", this.heat);
        pTag.putInt("graphite_block.depletion", this.depletion);
        pTag.putInt("graphite_block.neutrons", this.neutrons);
        pTag.putInt("graphite_block.last_neutrons", this.lastNeutrons);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {

        super.load(pTag);

        this.type = pTag.getInt("graphite_block.type");
        this.oldType = pTag.getInt("graphite_block.old_type");
        this.heat = pTag.getFloat("graphite_block.heat");
        this.depletion = pTag.getInt("graphite_block.depletion");
        this.neutrons = pTag.getInt("graphite_block.neutrons");
        this.lastNeutrons = pTag.getInt("graphite_block.last_neutrons");
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = new CompoundTag();
        saveAdditional(nbt);
        return nbt;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        controllers.add(new AnimationController<>(this, "graphite_c", 0, state -> {

            BlockEntity e = state.getData(DataTickets.BLOCK_ENTITY);

            if(e instanceof GraphiteBlockEntity graphiteBlock) {

                int typee = graphiteBlock.get(0);

//                System.err.println(typee);

                String val = switch (typee) {

                    case -1 -> BASE_ANIM;
                    case  0 -> EMPTY_ANIM;
                    case  1 -> URANIUM_ANIM;
                    case  2 -> PLUTONIUM_ANIM;
                    case  3 -> RADIUM_ANIM;
                    case  4 -> CONTROL_OFF_ANIM;
                    case  5 -> CONTROL_ON_ANIM;
                    default -> null;
                };

                state.getController().setAnimation(RawAnimation.begin().then(val, Animation.LoopType.LOOP));
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition, worldPosition.offset(1, 1, 1));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {

//        System.out.println("[Debug] ticking");

        if(this.type != this.oldType)
            reset();

        switch (this.type) {

            case 1: // uranium

                dissipateHeat();
                react(pLevel);

                if(this.heat > maxHeat) {

                    pLevel.destroyBlock(pPos, false);
                    pLevel.explode(null, pPos.getX(), pPos.getY(), pPos.getZ(), 5, Level.ExplosionInteraction.BLOCK);
                }

                if(this.depletion >= maxDepletion)
                    this.type = 2;
                break;
            case 2: // plutonium

                for(int i = 0; i < 16; i++)
                    castRay(3, 5, pLevel);
                break;
            case 3: // radium

                for(int i = 0; i < 16; i++)
                    castRay(1, 5, pLevel);
                break;
        }

        ClientboundGraphiteBlockPacket packet = new ClientboundGraphiteBlockPacket(
                pPos.getX(), pPos.getY(), pPos.getZ(), type, heat, depletionPercentage, lastNeutrons
        );

        HbmPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pPos)), packet);

        setChanged(pLevel, pPos, pState);
        sync();
    }

    private void reset() {

        this.heat = 0;
        this.depletion = 0;
        this.depletionPercentage = 0;
        this.neutrons = 0;
        this.lastNeutrons = 0;

        this.oldType = this.type;

        setChanged();
        sync();
    }

    public Integer get(int val) {

        return switch (val) {

            case 0 -> this.type;
            default -> -2;
        };
    }
    public void set(int val, int set) {

        assert getLevel() != null;
//        System.err.println("[Debug] side: " + (getLevel().isClientSide() ? "client" : "server"));

        switch (val) {

            case 0:
                this.type = set;
        }
        setChanged();
        sync();
    }

    private void dissipateHeat() {

        this.heat -= (heat * 0.05f);
        setChanged();
        sync();
    }

    private void react(Level pLevel) {

//        System.out.println("[Debug] reaction started from: " + this.type);
        double reaction = (this.neutrons * (1D - ((double)this.heat / (double)maxHeat) * 0.5D));

//        System.out.println("[Debug] (" + this.neutrons + " * (1 - (" + this.heat + " / " + maxHeat + ") * 0.5)) = " + reaction);

        this.lastNeutrons = this.neutrons;
        this.neutrons = 0;

        this.depletion += (int)Math.round(reaction);

        this.depletionPercentage = (((float)this.depletion / (float)maxDepletion) * 100f);

        if(reaction > 0) {

            this.heat += (float)reaction*2;

            for(int i = 0; i < 16; i++)
                castRay((int)Math.max(reaction * 0.25, 1), 5, pLevel);
        }
        setChanged();
        sync();
    }

    private void castRay(int flux, int range, Level pLevel) {

        RandomSource rand = pLevel.getRandom();
        Vec3 vec = Vec3.createVectorHelper(1, 0, 0);
        vec.rotateAroundZ((float)(rand.nextDouble() * Math.PI * 2D));
        vec.rotateAroundY((float)(rand.nextDouble() * Math.PI * 2D));

        int prevX = worldPosition.getX();
        int prevY = worldPosition.getY();
        int prevZ = worldPosition.getZ();

        for(float i = 1; i <= range; i += 0.5f) {

            int x = (int)Math.floor(worldPosition.getX() + 0.5 + vec.xCoord * i);
            int y = (int)Math.floor(worldPosition.getY() + 0.5 + vec.yCoord * i);
            int z = (int)Math.floor(worldPosition.getZ() + 0.5 + vec.zCoord * i);

            if(x == prevX && y == prevY && z == prevZ)
                continue;

            prevX = x;
            prevY = y;
            prevZ = z;

            BlockEntity be = pLevel.getBlockEntity(new BlockPos(x, y, z));

            if(be == null)
                continue;

            if(pLevel.getBlockState(new BlockPos(x, y, z)).is(RegisterBlocks.BORON_BLOCK.get()))
                return;

            if(be instanceof GraphiteBlockEntity graphiteBlock) {

                if(graphiteBlock.get(0) == 5)
                    return;

                if(graphiteBlock.get(0) == 1) {

                    float mult = Math.min((float)i / 2.5f, 1f);
                    int n = (int)(flux * mult);

                    graphiteBlock.receiveNeutrons(n);
                    return;
                }
            }
        }

        if(this.getTick(this) % (20f/Config.neutronParticleSpawnSpeed) == 0) {

            Vec3 pos = new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
            Vec3 vel = new Vec3(vec.xCoord, vec.yCoord, vec.zCoord);

            ClientboundSpawnNeutronParticlePacket packet = new ClientboundSpawnNeutronParticlePacket(
                    pos.xCoord, pos.yCoord, pos.zCoord, vel.xCoord, vel.yCoord, vel.zCoord
            );

            HbmPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> pLevel.getChunkAt(pos.toBlockPos())), packet);
        }
    }

    public void receiveNeutrons(int n) {

        if(this.type == 1)
            this.neutrons += n;
        setChanged();
        sync();
    }
}
