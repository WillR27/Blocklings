package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.containers.EquipmentContainer;
import com.willr27.blocklings.gui.screens.guis.TabbedGui;
import net.minecraft.entity.player.PlayerEntity;

public class EquipmentScreen extends TabbedContainerScreen<EquipmentContainer>
{
    public EquipmentScreen(EquipmentContainer screenContainer, BlocklingEntity blockling, PlayerEntity player)
    {
        super(screenContainer, blockling, player, "Equipment");
    }

    @Override
    protected void init()
    {
        super.init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        GuiUtil.bindTexture(GuiUtil.EQUIPMENT);
        blit(matrixStack, contentLeft, contentTop, 0, 0, TabbedGui.CONTENT_WIDTH, TabbedGui.CONTENT_HEIGHT);

        GuiUtil.renderEntityOnScreen(centerX - 58, centerY - 38, 20, centerX - 58 - mouseX, centerY - 38 - mouseY, blockling);

        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
    }
}
