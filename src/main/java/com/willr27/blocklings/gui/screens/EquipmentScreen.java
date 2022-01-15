package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.containers.EquipmentContainer;
import com.willr27.blocklings.gui.guis.TabbedGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * The container screen for the blockling's main equipment inventory.
 */
@OnlyIn(Dist.CLIENT)
public class EquipmentScreen extends TabbedContainerScreen<EquipmentContainer>
{
    /**
     * @param equipmentContainer the container for the equipment.
     * @param blockling the blockling.
     */
    public EquipmentScreen(@Nonnull EquipmentContainer equipmentContainer, @Nonnull BlocklingEntity blockling)
    {
        super(equipmentContainer, blockling);
    }

    @Override
    protected void init()
    {
        super.init();
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        GuiUtil.bindTexture(GuiTextures.EQUIPMENT);
        blit(matrixStack, contentLeft, contentTop, 0, 0, TabbedGui.CONTENT_WIDTH, TabbedGui.CONTENT_HEIGHT);

        GuiUtil.renderEntityOnScreen(centerX - 58, centerY - 38, 20, centerX - 58 - mouseX, centerY - 38 - mouseY, blockling);

        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
    }
}
