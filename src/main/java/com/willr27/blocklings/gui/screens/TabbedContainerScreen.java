package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.screens.guis.TabbedGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.StringTextComponent;

public class TabbedContainerScreen<T extends Container> extends ContainerScreen<T>
{
    private TabbedGui tabbedGui;

    protected final BlocklingEntity blockling;
    protected final PlayerEntity player;
    protected int centerX, centerY;
    protected int left, top;
    protected int contentLeft, contentTop;
    protected int contentRight, contentBottom;

    public TabbedContainerScreen(T screenContainer, BlocklingEntity blockling, PlayerEntity player, String label)
    {
        super(screenContainer, player.inventory, new StringTextComponent(label));
        this.blockling = blockling;
        this.player = player;
    }

    @Override
    protected void init()
    {
        centerX = width / 2;
        centerY = height / 2 + TabbedGui.OFFSET_Y;

        left = centerX - TabbedGui.UI_WIDTH / 2;
        top = centerY - TabbedGui.UI_HEIGHT / 2;

        contentLeft = centerX - TabbedGui.CONTENT_WIDTH / 2;
        contentTop = top;
        contentRight = contentLeft + TabbedGui.CONTENT_WIDTH;
        contentBottom = contentTop + TabbedGui.CONTENT_HEIGHT;

        tabbedGui = new TabbedGui(blockling, player, centerX, centerY);

        super.init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        tabbedGui.drawTooltip(matrixStack, mouseX, mouseY, this);
        renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        tabbedGui.drawTabs(matrixStack);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        // Leave empty to stop container labels being rendered
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        return super.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        tabbedGui.mouseReleased((int)mouseX, (int)mouseY, state);

        return super.mouseReleased(mouseX, mouseY, state);
    }
}
