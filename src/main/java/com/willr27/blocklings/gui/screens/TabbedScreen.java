package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.IControl;
import com.willr27.blocklings.gui.controls.TabbedControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A screen that includes the blockling gui tabs.
 */
@OnlyIn(Dist.CLIENT)
public class TabbedScreen extends Screen implements IControl
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
    private TabbedControl tabbedGui;

    /**
     * The list of guis to handle.
     */
    @Nonnull
    private final List<Control> children = new ArrayList<>();

    /**
     * @param blockling the blockling.
     */
    public TabbedScreen(@Nonnull BlocklingEntity blockling)
    {
        super(new StringTextComponent(""));
        this.blockling = blockling;
        this.player = Minecraft.getInstance().player;
    }

    @Override
    @Nullable
    public IControl getParent()
    {
        return null;
    }

    @Nonnull
    @Override
    public List<Control> getChildren()
    {
        return children;
    }

    @Override
    public void addChild(@Nonnull Control control)
    {
        children.add(control);
    }

    @Override
    public void removeChild(@Nonnull Control control)
    {
        children.remove(control);
    }

    @Override
    public int getScreenX()
    {
        return contentLeft;
    }

    @Override
    public int getScreenY()
    {
        return contentTop;
    }

    /**
     * Called on first creation and whenever the screen is resized.
     */
    @Override
    protected void init()
    {
        children.clear();

        centerX = width / 2;
        centerY = height / 2 + TabbedControl.OFFSET_Y;

        left = centerX - TabbedControl.GUI_WIDTH / 2;
        top = centerY - TabbedControl.GUI_HEIGHT / 2;

        contentLeft = centerX - TabbedControl.CONTENT_WIDTH / 2;
        contentTop = top;
        contentRight = contentLeft + TabbedControl.CONTENT_WIDTH;
        contentBottom = contentTop + TabbedControl.CONTENT_HEIGHT;

        tabbedGui = new TabbedControl(blockling, centerX, centerY);

        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);

        super.init();
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        tabbedGui.render(matrixStack, mouseX, mouseY);

        RenderSystem.enableDepthTest();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.enableDepthTest();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (tabbedGui.mouseClicked((int) mouseX, (int) mouseY, button))
        {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
