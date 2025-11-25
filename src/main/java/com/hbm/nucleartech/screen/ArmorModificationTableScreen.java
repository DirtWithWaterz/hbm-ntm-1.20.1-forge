package com.hbm.nucleartech.screen;

import com.hbm.nucleartech.HBM;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArmorModificationTableScreen extends AbstractContainerScreen<ArmorModificationTableMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(HBM.MOD_ID, "textures/gui/armor_modification_table_gui.png");

    public ArmorModificationTableScreen(ArmorModificationTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - (imageHeight+56)) / 2;

        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight+56);

        pGuiGraphics.blit(TEXTURE, x-22, y+31, 176, 96, 22, 80);

//        this.titleLabelX = (imageWidth / 2) - 33;
        this.titleLabelY = (imageHeight / 2) - 105;
        this.inventoryLabelY = ((imageHeight + 56) / 2) - 11;

        renderModifySlot(pGuiGraphics, x, y);

        renderHelmetSlot(pGuiGraphics, x, y);
        renderChestplateSlot(pGuiGraphics, x, y);
        renderLeggingsSlot(pGuiGraphics, x, y);
        renderBootsSlot(pGuiGraphics, x, y);
        renderServosSlot(pGuiGraphics, x, y);
        renderCladdingSlot(pGuiGraphics, x, y);
        renderInsertSlot(pGuiGraphics, x, y);
        renderSpecialSlot(pGuiGraphics, x, y);
        renderBatterySlot(pGuiGraphics, x, y);
    }

    private void renderModifySlot(GuiGraphics pGuiGraphics, int x, int y) {

        long t = System.currentTimeMillis();
        boolean val = (t / 500) % 2 == 0; // change every 500ms

        boolean filled = menu.getValue(0) == 1;

        int xO = 176;
        int yO = filled ? 74 : 52;

        if(!filled) {

            if(val)
                pGuiGraphics.blit(TEXTURE, x+41, y+60, xO, yO, 22, 22);
        }
        else {

            pGuiGraphics.blit(TEXTURE, x+41, y+60, xO, yO, 22, 22);
        }
    }

    private void renderHelmetSlot(GuiGraphics pGuiGraphics, int x, int y) {

        boolean filled = menu.getValue(1) == 1;

        if(filled)
            pGuiGraphics.blit(TEXTURE, x+25, y+26, 176, 34, 18, 18);
    }

    private void renderChestplateSlot(GuiGraphics pGuiGraphics, int x, int y) {

        boolean filled = menu.getValue(2) == 1;

        if(filled)
            pGuiGraphics.blit(TEXTURE, x+61, y+26, 176, 34, 18, 18);
    }

    private void renderLeggingsSlot(GuiGraphics pGuiGraphics, int x, int y) {

        boolean filled = menu.getValue(3) == 1;

        if(filled)
            pGuiGraphics.blit(TEXTURE, x+97, y+26, 176, 34, 18, 18);
    }

    private void renderBootsSlot(GuiGraphics pGuiGraphics, int x, int y) {

        boolean filled = menu.getValue(4) == 1;

        if(filled)
            pGuiGraphics.blit(TEXTURE, x+133, y+44, 176, 34, 18, 18);
    }

    private void renderServosSlot(GuiGraphics pGuiGraphics, int x, int y) {

        boolean filled = menu.getValue(5) == 1;

        if(filled)
            pGuiGraphics.blit(TEXTURE, x+133, y+80, 176, 34, 18, 18);
    }

    private void renderCladdingSlot(GuiGraphics pGuiGraphics, int x, int y) {

        boolean filled = menu.getValue(6) == 1;

        if(filled)
            pGuiGraphics.blit(TEXTURE, x+97, y+98, 176, 34, 18, 18);
    }

    private void renderInsertSlot(GuiGraphics pGuiGraphics, int x, int y) {

        boolean filled = menu.getValue(7) == 1;

        if(filled)
            pGuiGraphics.blit(TEXTURE, x+61, y+98, 176, 34, 18, 18);
    }

    private void renderSpecialSlot(GuiGraphics pGuiGraphics, int x, int y) {

        boolean filled = menu.getValue(8) == 1;

        if(filled)
            pGuiGraphics.blit(TEXTURE, x+25, y+98, 176, 34, 18, 18);
    }

    private void renderBatterySlot(GuiGraphics pGuiGraphics, int x, int y) {

        boolean filled = menu.getValue(9) == 1;

        if(filled)
            pGuiGraphics.blit(TEXTURE, x+7, y+62, 176, 34, 18, 18);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);

        // check hover and show tooltip
//        if (isMouseOverGuiArea(pMouseX, pMouseY, 8, 18-28, this.leftPos, this.topPos, 16, 88)) {
//            String storedDisplay = menu.blockEntity.formatWattHoursStored() + "/" + menu.blockEntity.formatMaxWattHoursStored();
//
//            List<Component> lines = new ArrayList<>();
//            lines.add(Component.literal(storedDisplay));
//
//            String discharge = menu.blockEntity.formatWattsDischarge();
//            if (!Objects.equals(discharge, "0.0W")) {
//                lines.add(Component.translatable("ui.hbm.power_draw", discharge));
//            }
//
//            // Multi-line tooltip: pass a List<Component>
//            pGuiGraphics.renderComponentTooltip(Minecraft.getInstance().font, lines, pMouseX, pMouseY);
//        }
    }

    /** helper: checks mouse position against GUI-relative rectangle */
    public static boolean isMouseOverGuiArea(int mouseX, int mouseY, int relX, int relY, int leftPos, int topPos, int width, int height) {
        int gx = leftPos + relX;
        int gy = topPos + relY;
        return mouseX >= gx && mouseY >= gy && mouseX < gx + width && mouseY < gy + height;
    }
}
