package com.willr27.blocklings.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.controls.ScreenControl;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseClickedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseScrolledEvent;
import com.willr27.blocklings.client.gui3.util.GuiUtil;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A base screen to provide an adapter for {@link Screen}.
 */
@OnlyIn(Dist.CLIENT)
public class BlocklingsScreen extends Screen
{
    /**
     * The blockling associated with the screen.
     */
    @Nonnull
    public final BlocklingEntity blockling;

    /**
     * The root control that contains all the sub controls on the screen.
     */
    @Nonnull
    public final ScreenControl screenControl = new ScreenControl();

    /**
     * @param blockling the blockling associated with the screen.
     */
    protected BlocklingsScreen(@Nonnull BlocklingEntity blockling)
    {
        super(new StringTextComponent(""));
        this.blockling = blockling;
    }

    @Override
    protected void init()
    {
        super.init();

        screenControl.setWidth(width);
        screenControl.setHeight(height);
        screenControl.markMeasureDirty(true);
        screenControl.markArrangeDirty(true);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        screenControl.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double screenMouseX, double screenMouseY, int button)
    {
        double mouseX = GuiUtil.getInstance().getPixelMouseX();
        double mouseY = GuiUtil.getInstance().getPixelMouseY();

        MouseClickedEvent e = new MouseClickedEvent(mouseX, mouseY, button);

        screenControl.forwardMouseClicked(e);

        return e.isHandled() || super.mouseClicked(screenMouseX, screenMouseY, button);
    }

    @Override
    public boolean mouseReleased(double screenMouseX, double screenMouseY, int button)
    {
        double mouseX = GuiUtil.getInstance().getPixelMouseX();
        double mouseY = GuiUtil.getInstance().getPixelMouseY();

        MouseReleasedEvent e = new MouseReleasedEvent(mouseX, mouseY, button);

        screenControl.forwardMouseReleased(e);

        return e.isHandled() || super.mouseReleased(screenMouseX, screenMouseY, button);
    }

    @Override
    public boolean mouseScrolled(double screenMouseX, double screenMouseY, double amount)
    {
        double mouseX = GuiUtil.getInstance().getPixelMouseX();
        double mouseY = GuiUtil.getInstance().getPixelMouseY();

        MouseScrolledEvent e = new MouseScrolledEvent(mouseX, mouseY, amount);

        screenControl.forwardMouseScrolled(e);

        return e.isHandled() || super.mouseScrolled(screenMouseX, screenMouseY, amount);
    }
}
