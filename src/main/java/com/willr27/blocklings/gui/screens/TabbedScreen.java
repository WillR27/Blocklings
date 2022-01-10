package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.screens.guis.TabbedGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class TabbedScreen extends Screen
{
    private TabbedGui tabbedGui;

    protected final BlocklingEntity blockling;
    protected final PlayerEntity player;
    protected int centerX, centerY;
    protected int left, top;
    protected int contentLeft, contentTop;
    protected int contentRight, contentBottom;

    public TabbedScreen(BlocklingEntity blockling, PlayerEntity player, String label)
    {
        super(new StringTextComponent(label));
        this.blockling = blockling;
        this.player = player;
    }

    @Override
    protected void init()
    {
        centerX = width / 2;
        centerY = height / 2 + TabbedGui.OFFSET_Y;

        left = centerX - TabbedGui.GUI_WIDTH / 2;
        top = centerY - TabbedGui.GUI_HEIGHT / 2;

        contentLeft = centerX - TabbedGui.CONTENT_WIDTH / 2;
        contentTop = top;
        contentRight = contentLeft + TabbedGui.CONTENT_WIDTH;
        contentBottom = contentTop + TabbedGui.CONTENT_HEIGHT;

        tabbedGui = new TabbedGui(blockling, centerX, centerY);

        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);

        super.init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        tabbedGui.render(matrixStack, mouseX, mouseY);

        RenderSystem.enableDepthTest();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.enableDepthTest();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        if (tabbedGui.mouseClicked((int) mouseX, (int) mouseY, state))
        {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, state);
    }
}
