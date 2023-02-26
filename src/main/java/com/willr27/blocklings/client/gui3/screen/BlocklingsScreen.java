package com.willr27.blocklings.client.gui3.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A base screen used to support using {@link Control} objects.
 */
@OnlyIn(Dist.CLIENT)
public abstract class BlocklingsScreen extends Screen
{
    /**
     * The blockling.
     */
    @Nonnull
    protected final BlocklingEntity blockling;

    /**
     * The root control that contains all the sub controls on the screen.
     */
    @Nonnull
    protected final ScreenControl screenControl = new ScreenControl();

    /**
     * @param blockling the blockling.
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

        screenControl.init(width, height);
    }

    @Override
    public void tick()
    {
        screenControl.tick();
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int screenMouseX, int screenMouseY, float partialTicks)
    {
        screenControl.render(matrixStack, screenMouseX, screenMouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double screenMouseX, double screenMouseY, int mouseButton)
    {
        if (screenControl.mouseClicked(screenMouseX, screenMouseY, mouseButton))
        {
            return true;
        }

        return super.mouseClicked(screenMouseX, screenMouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double screenMouseX, double screenMouseY, int mouseButton)
    {
        if (screenControl.mouseReleased(screenMouseX, screenMouseY, mouseButton))
        {
            return true;
        }

        return super.mouseReleased(screenMouseX, screenMouseY, mouseButton);
    }

    @Override
    public boolean mouseScrolled(double screenMouseX, double screenMouseY, double scrollAmount)
    {
        if (screenControl.mouseScrolled(screenMouseX, screenMouseY, scrollAmount))
        {
            return true;
        }

        return super.mouseScrolled(screenMouseX, screenMouseY, scrollAmount);
    }

    @Override
    public final boolean keyPressed(int keyCode, int scanCode, int mods)
    {
        if (screenControl.keyPressed(keyCode, scanCode, mods))
        {
            return true;
        }

        InputMappings.Input key = InputMappings.getKey(keyCode, scanCode);

        if (Minecraft.getInstance().options.keyInventory.isActiveAndMatches(key))
        {
            onClose();

            return true;
        }

        return super.keyPressed(keyCode, scanCode, mods);
    }

    @Override
    public final boolean keyReleased(int keyCode, int scanCode, int mods)
    {
        if (screenControl.keyReleased(keyCode, scanCode, mods))
        {
            return true;
        }

        return super.keyReleased(keyCode, scanCode, mods);
    }

    @Override
    public final boolean charTyped(char character, int keyCode)
    {
        if (screenControl.charTyped(character, keyCode))
        {
            return true;
        }

        return super.charTyped(character, keyCode);
    }
}
