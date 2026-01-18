package com.hbm.nucleartech.entity.client;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.entity.effects.NukeTorexEntity;
import com.hbm.nucleartech.entity.effects.NukeTorexEntity.Cloudlet;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import static com.hbm.nucleartech.damagesource.RegisterDamageSources.NUCLEAR_BLAST;
import static com.hbm.nucleartech.entity.effects.NukeTorexEntity.shockSpeed;

// Assuming you have a cloud texture; replace with your mod's actual ResourceLocation
@OnlyIn(Dist.CLIENT)
public class NukeTorexRenderer extends EntityRenderer<NukeTorexEntity> {

    private static final ResourceLocation CLOUD_TEXTURE = ResourceLocation.fromNamespaceAndPath(HBM.MOD_ID, "textures/particle/contrail.png");
    private static final ResourceLocation FLARE_TEXTURE = ResourceLocation.fromNamespaceAndPath(HBM.MOD_ID, "textures/particle/flare.png");

    private static final int FLASH_BASE_DURATION = 30;
    private static final int FLARE_BASE_DURATION = 100;

    public NukeTorexRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(NukeTorexEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        doScreenShake(entity, partialTicks);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(CLOUD_TEXTURE));

        int fullBright = LightTexture.FULL_BRIGHT;

        ArrayList<Cloudlet> sortedCloudlets = new ArrayList<>(entity.cloudlets);
        sortedCloudlets.sort(cloudSorter);

        for (Cloudlet cloudlet : sortedCloudlets) {
            if (cloudlet.isDead) continue;

            Vec3 pos = cloudlet.getInterpPos(partialTicks);
            Vec3 colorVec = cloudlet.getInterpColor(partialTicks);
            float r = Mth.clamp((float)colorVec.x, 0F, 1F);
            float g = Mth.clamp((float)colorVec.y, 0F, 1F);
            float b = Mth.clamp((float)colorVec.z, 0F, 1F);
            float alpha = cloudlet.getAlpha();
            float scale = cloudlet.getScale();

            poseStack.pushPose();
            poseStack.translate(pos.x - entity.getX(), pos.y - entity.getY(), pos.z - entity.getZ());
            poseStack.mulPose(this.entityRenderDispatcher.camera.rotation());

            Matrix4f matrix = poseStack.last().pose();
            float halfScale = scale / 2.0F;

            drawQuad(vertexConsumer, matrix, halfScale, r, g, b, alpha, fullBright);

            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            drawQuad(vertexConsumer, matrix, halfScale, r, g, b, alpha, fullBright);
            poseStack.popPose();

            poseStack.popPose();
        }

        float flareDuration = (float) entity.getScale() * FLARE_BASE_DURATION;
        if (entity.tickCount < flareDuration + 1) {
            renderFlare(entity, partialTicks, flareDuration, buffer, poseStack);
        }

        float flashDuration = (float) entity.getScale() * FLASH_BASE_DURATION;
        if (entity.tickCount < flashDuration + 1) {
            renderFlash(entity, partialTicks, 200, buffer, poseStack);
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private final Comparator<Cloudlet> cloudSorter = (first, second) -> {
        Player player = Minecraft.getInstance().player;
        double dist1 = player.distanceToSqr(first.posX, first.posY, first.posZ);
        double dist2 = player.distanceToSqr(second.posX, second.posY, second.posZ);
        return Double.compare(dist2, dist1);
    };

    private void doScreenShake(NukeTorexEntity entity, float partialTicks) {
        if (entity.tickCount > 300) return;
        Player player = Minecraft.getInstance().player;

        double dist = player.distanceTo(entity);
        double shockwaveDistance = dist - entity.tickCount * shockSpeed;
        if (shockwaveDistance > shockSpeed * 2 || shockwaveDistance < 0) return;
        float amplitude = (float) entity.getScale() * 100;
        entity.level().playSound(player, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, net.minecraft.sounds.SoundSource.AMBIENT, amplitude, 0.8F + entity.level().random.nextFloat() * 0.2F);
        int duration = (int) (40 * Math.min(1.5, (amplitude * amplitude) / (dist * dist)));
        if (duration < 15) return;
        int swingTimer = duration << 1;

        player.hurtTime = swingTimer;
        player.hurtDuration = duration; // Corrected from maxHurtTime
        player.hurtDir = 0F; // is correct. (added to accesstransformer.cfg to remove protected access)
    }

    private void renderFlare(NukeTorexEntity entity, float partialTicks, float flareDuration, MultiBufferSource buffer, PoseStack poseStack) {
        RenderSystem.setShaderTexture(0, FLARE_TEXTURE);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(FLARE_TEXTURE));
        int fullBright = LightTexture.FULL_BRIGHT;

        Random rand = new Random(entity.getId());
        for (int i = 0; i < 3; i++) {
            float offsetX = (float) (rand.nextGaussian() * 0.5F * entity.rollerSize);
            float offsetY = (float) (rand.nextGaussian() * 0.5F * entity.rollerSize);
            float offsetZ = (float) (rand.nextGaussian() * 0.5F * entity.rollerSize);
            float flareScale = (float) (30 * entity.rollerSize);
            float alpha = (float) Math.max(0, (flareDuration - (entity.tickCount + partialTicks)) / flareDuration * 0.5F); // Halved max alpha and clamped to >=0

            if (alpha <= 0) continue; // Skip if fully faded

            poseStack.pushPose();
            poseStack.translate(offsetX, offsetY + entity.coreHeight, offsetZ);
            poseStack.mulPose(this.entityRenderDispatcher.camera.rotation());

            Matrix4f matrix = poseStack.last().pose();
            float halfScale = flareScale / 2.0F;

            drawQuad(vertexConsumer, matrix, halfScale, 1F, 1F, 1F, alpha, fullBright);

            poseStack.popPose();
        }
    }

    private void renderFlash(NukeTorexEntity entity, float partialTicks, float flashDuration, MultiBufferSource buffer, PoseStack poseStack) {

        double intensity = (entity.tickCount + partialTicks) / flashDuration;
        intensity = (intensity * Math.pow(Math.E, -intensity * 30) * 2.717391304D) * 30;

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        Random random = new Random(432L);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buf = tesselator.getBuilder();

        poseStack.pushPose();
        poseStack.scale(0.2F, 0.2F, 0.2F);
        poseStack.translate(0, entity.coreHeight * 4, 0);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        for (int i = 0; i < 300; i++) {
            poseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));

            float vert1 = (random.nextFloat() * 200.0f + 50.0f) * (float) (intensity * flashDuration / FLASH_BASE_DURATION);
            float vert2 = (random.nextFloat() * 2.0F + 1.0F + 1 * 2.0F) * (float) (intensity * flashDuration / FLASH_BASE_DURATION);

            Matrix4f matrix = poseStack.last().pose(); // Get matrix inside loop after rotations

            buf.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            buf.vertex(matrix, 0F, 0F, 0F).color(1.0F, 1.0F, 1.0F, (float) (intensity)).endVertex();
            buf.vertex(matrix, (float) -0.866 * vert2, vert1, (float) -0.5 * vert2).color(1.0F, 1.0F, 1.0F, 0.0F).endVertex();
            buf.vertex(matrix, (float) 0.866 * vert2, vert1, (float) -0.5 * vert2).color(1.0F, 1.0F, 1.0F, 0.0F).endVertex();
            buf.vertex(matrix, 0F, vert1, vert2).color(1.0F, 1.0F, 1.0F, 0.0F).endVertex();
            buf.vertex(matrix, (float) -0.866 * vert2, vert1, (float) -0.5 * vert2).color(1.0F, 1.0F, 1.0F, 0.0F).endVertex();
            tesselator.end();
        }

        poseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private void drawQuad(VertexConsumer vertexConsumer, Matrix4f matrix, float halfScale, float r, float g, float b, float alpha, int light) {
        vertexConsumer.vertex(matrix, -halfScale, -halfScale, 0).color(r, g, b, alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
        vertexConsumer.vertex(matrix, -halfScale, halfScale, 0).color(r, g, b, alpha).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
        vertexConsumer.vertex(matrix, halfScale, halfScale, 0).color(r, g, b, alpha).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
        vertexConsumer.vertex(matrix, halfScale, -halfScale, 0).color(r, g, b, alpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(NukeTorexEntity entity) {
        return CLOUD_TEXTURE; // Not strictly needed if handling textures manually
    }
}