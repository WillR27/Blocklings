package com.willr27.blocklings.client.gui.screen.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.EntityControl;
import com.willr27.blocklings.client.gui.control.controls.TabbedUIControl;
import com.willr27.blocklings.client.gui.containers.EquipmentContainer;
import com.willr27.blocklings.client.gui.control.controls.panels.CanvasPanel;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A screen that displays the blockling's equipment.
 */
@OnlyIn(Dist.CLIENT)
public class EquipmentScreen extends TabbedContainerScreen<EquipmentContainer>
{
    /**
     * @param blockling   the blockling associated with the screen.
     * @param container   the container.
     */
    public EquipmentScreen(@Nonnull BlocklingEntity blockling, @Nonnull EquipmentContainer container)
    {
        super(blockling, container, TabbedUIControl.Tab.EQUIPMENT);

        EntityControl entityControl = new EntityControl();
        entityControl.setParent(tabbedUIControl.contentControl);
        entityControl.setWidth(48);
        entityControl.setHeight(48);
        entityControl.setEntity(blockling);
        entityControl.setEntityScale(0.7f);
        entityControl.setScaleToBoundingBox(true);
        entityControl.setOffsetY(-3.0f);

        CanvasPanel canvasPanel = new CanvasPanel();
        canvasPanel.setParent(tabbedUIControl.contentControl);
        canvasPanel.setWidthPercentage(1.0);
        canvasPanel.setHeightPercentage(1.0);
        canvasPanel.setInteractive(false);

        Control leftSlotControl = new Control()
        {
            @Override
            protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                if (blockling.getEquipment().hasToolEquipped(Hand.MAIN_HAND))
                {
                    super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
                }
            }
        };
        leftSlotControl.setParent(canvasPanel);
        leftSlotControl.setWidth(16);
        leftSlotControl.setHeight(16);
        leftSlotControl.setX(4);
        leftSlotControl.setY(54);
        leftSlotControl.setBackgroundColour(0xff8b8b8b);

        Control rightSlotControl = new Control()
        {
            @Override
            protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                if (blockling.getEquipment().hasToolEquipped(Hand.OFF_HAND))
                {
                    super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
                }
            }
        };
        rightSlotControl.setParent(canvasPanel);
        rightSlotControl.setWidth(16);
        rightSlotControl.setHeight(16);
        rightSlotControl.setX(28);
        rightSlotControl.setY(54);
        rightSlotControl.setBackgroundColour(0xff8b8b8b);
    }
}
