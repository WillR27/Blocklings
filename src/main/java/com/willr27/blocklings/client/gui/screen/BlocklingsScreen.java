package com.willr27.blocklings.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.controls.ScreenControl;
import com.willr27.blocklings.client.gui.control.event.events.input.*;
import com.willr27.blocklings.client.gui.util.GuiUtil;
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
    public void onClose()
    {
        super.onClose();

        screenControl.forwardClose(screenControl.shouldReallyClose());
    }

    @Override
    public void tick()
    {
        screenControl.forwardTick();

        super.tick();
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        screenControl.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double screenMouseX, double screenMouseY, int button)
    {
        double mouseX = GuiUtil.get().getPixelMouseX();
        double mouseY = GuiUtil.get().getPixelMouseY();

        MouseClickedEvent e = new MouseClickedEvent(mouseX, mouseY, button);

        screenControl.forwardMouseClicked(e);

        if (e.isHandled() || super.mouseClicked(screenMouseX, screenMouseY, button))
        {
            return true;
        }
        else
        {
            screenControl.setPressed(true);
            screenControl.setFocused(true);

            return false;
        }
    }

    @Override
    public boolean mouseReleased(double screenMouseX, double screenMouseY, int button)
    {
        double mouseX = GuiUtil.get().getPixelMouseX();
        double mouseY = GuiUtil.get().getPixelMouseY();

        MouseReleasedEvent e = new MouseReleasedEvent(mouseX, mouseY, button);

        screenControl.forwardMouseReleased(e);

        return e.isHandled() || super.mouseReleased(screenMouseX, screenMouseY, button);
    }

    @Override
    public boolean mouseScrolled(double screenMouseX, double screenMouseY, double amount)
    {
        double mouseX = GuiUtil.get().getPixelMouseX();
        double mouseY = GuiUtil.get().getPixelMouseY();

        MouseScrolledEvent e = new MouseScrolledEvent(mouseX, mouseY, amount);

        screenControl.forwardMouseScrolled(e);

        return e.isHandled() || super.mouseScrolled(screenMouseX, screenMouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        KeyPressedEvent e = new KeyPressedEvent(keyCode, scanCode, modifiers);

        screenControl.forwardGlobalKeyPressed(e);

        if (e.isHandled() || super.keyPressed(keyCode, scanCode, modifiers))
        {
            return true;
        };

        if (GuiUtil.get().isCloseKey(keyCode))
        {
            onClose();

            return true;
        }

        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        KeyReleasedEvent e = new KeyReleasedEvent(keyCode, scanCode, modifiers);

        screenControl.forwardGlobalKeyReleased(e);

        return e.isHandled() || super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char character, int modifiers)
    {
        CharTypedEvent e = new CharTypedEvent(character, modifiers);

        screenControl.forwardGlobalCharTyped(e);

        return e.isHandled() || super.charTyped(character, modifiers);
    }
}
