package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.controls.TabbedControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;

/**
 * A container screen that includes the blockling gui tabs.
 */
public class TabbedContainerScreen<T extends Container> extends ContainerScreen<T>
{
    /**
     * The blockling.
     */
    @Nonnull
    protected final BlocklingEntity blockling;

    /**
     * The player opening the gui.
     */
    @Nonnull
    protected final PlayerEntity player;

    /**
     * The x position in the center of the screen.
     */
    protected int centerX;

    /**
     * The y position in the center of the screen.
     */
    protected int centerY;

    /**
     * The x position at the left of the gui's tabs.
     */
    protected int left;

    /**
     * The y position at the top of the gui.
     */
    protected int top;

    /**
     * The x position at the left of the gui excluding the tabs.
     */
    protected int contentLeft;

    /**
     * The y position at the top of the gui excluding.
     */
    protected int contentTop;

    /**
     * The x position at the right of the gui excluding the tabs.
     */
    protected int contentRight;

    /**
     * The y position at the bottom of the gui excluding.
     */
    protected int contentBottom;

    /**
     * The gui used to the draw and handle the tabs.
     */
    private TabbedControl tabbedControl;

    /**
     * @param blockling the blockling.
     */
    public TabbedContainerScreen(@Nonnull T screenContainer, @Nonnull BlocklingEntity blockling)
    {
        super(screenContainer, Minecraft.getInstance().player.inventory, new StringTextComponent(""));
        this.blockling = blockling;
        this.player = Minecraft.getInstance().player;
    }

    /**
     * Called on first creation and whenever the screen is resized.
     */
    @Override
    protected void init()
    {
        centerX = width / 2;
        centerY = height / 2 + TabbedControl.OFFSET_Y;

        left = centerX - TabbedControl.GUI_WIDTH / 2;
        top = centerY - TabbedControl.GUI_HEIGHT / 2;

        contentLeft = centerX - TabbedControl.CONTENT_WIDTH / 2;
        contentTop = top;
        contentRight = contentLeft + TabbedControl.CONTENT_WIDTH;
        contentBottom = contentTop + TabbedControl.CONTENT_HEIGHT;

        tabbedControl = new TabbedControl(blockling, centerX, centerY);

        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);

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
        tabbedControl.render(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        // Leave empty to stop container labels being rendered
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        if (tabbedControl.mouseClicked((int) mouseX, (int) mouseY, state))
        {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        return super.mouseReleased(mouseX, mouseY, state);
    }
}
