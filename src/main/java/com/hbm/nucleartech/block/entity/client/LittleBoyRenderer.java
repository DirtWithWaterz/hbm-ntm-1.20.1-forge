package com.hbm.nucleartech.block.entity.client;

import com.hbm.nucleartech.block.entity.LittleBoyEntity;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.lang.reflect.Field;

public class LittleBoyRenderer extends GeoBlockRenderer<LittleBoyEntity> {

    public LittleBoyRenderer(BlockEntityRendererProvider.Context context) {
        super(new LittleBoyModel());
    }

    @Override
    public boolean shouldRenderOffScreen(LittleBoyEntity pBlockEntity) {
        return true;
    }

    @Override
    public void createVerticesOfQuad(GeoQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, red, green, blue, alpha);

        int k = getBreakingStageAt(animatable.getBlockPos());

        Matrix3f normalMat = new Matrix3f();
        poseState.normal(normalMat);

        if(k > -1) {

            VertexConsumer vertexConsumer = new SheetedDecalTextureGenerator(Minecraft.getInstance().renderBuffers().crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(k)), poseState, normalMat, 1.0F);

            for (GeoVertex vertex : quad.vertices()) {
                Vector3f position = vertex.position();
                Vector4f vector4f = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0f));

                vertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.texU(),
                        vertex.texV(), packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
            }
        }
    }

    private int getBreakingStageAt(BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();
//        if (mc == null) return -1;
        LevelRenderer worldRenderer = mc.levelRenderer; // field: levelRenderer
        try {
            // reflectively fetch the blockBreakingInfos (Int2ObjectMap<BlockBreakingInfo>) or the BlockBreakingInfo map
            // Field name can vary by mapping; try the obvious names or use reflection by type
            Field f = LevelRenderer.class.getDeclaredField("destroyingBlocks"); // try mapped name
            f.setAccessible(true);
            Object map = f.get(worldRenderer); // Int2ObjectMap<BlockBreakingInfo>
            for (Object v : ((Int2ObjectMap<?>)map).values()) {
                // BlockBreakingInfo has getPos() and getStage()
                BlockDestructionProgress info = (BlockDestructionProgress) v;
                if (info.getPos().equals(pos)) {
                    return info.getProgress(); // 0..n
                }
            }
        } catch (ReflectiveOperationException ex) {
            // fallback: no access -> -1
        }
        return -1;
    }
}
