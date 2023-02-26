package com.willr27.blocklings.client.gui3.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A base container screen used to support using {@link Control} objects.
 */
@OnlyIn(Dist.CLIENT)
public abstract class BlocklingsContainerScreen<T extends Container> extends ContainerScreen<T>
{
    /**
     * The blockling.
     */
    @Nonnull
    protected final BlocklingEntity blockling;

    /**
     * The player.
     */
    @Nonnull
    private final PlayerEntity player;

    /**
     * The root control that contains all the sub controls on the screen.
     */
    @Nonnull
    protected final ScreenControl screenControl = new ScreenControl();

    /**
     * @param blockling the blockling.
     * @param container the container.
     */
    protected BlocklingsContainerScreen(@Nonnull BlocklingEntity blockling, @Nonnull T container)
    {
        super(container, Minecraft.getInstance().player.inventory, new StringTextComponent(""));
        this.blockling = blockling;
        this.player = Minecraft.getInstance().player;
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
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int screenMouseX, int screenMouseY)
    {

    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int screenMouseX, int screenMouseY, float partialTicks)
    {
        screenControl.render(matrixStack, screenMouseX, screenMouseY, partialTicks);

        super.render(matrixStack, screenMouseX, screenMouseY, partialTicks);
    }

    @Override
    protected void renderLabels(@Nonnull MatrixStack matrixStack, int screenMouseX, int screenMouseY)
    {

    }

    @Override
    public boolean mouseClicked(double screenMouseX, double screenMouseY, int mouseButton)
    {
        boolean handled = super.mouseClicked(screenMouseX, screenMouseY, mouseButton);

        return screenControl.mouseClicked(screenMouseX, screenMouseY, mouseButton) || handled;
    }

    @Override
    public boolean mouseReleased(double screenMouseX, double screenMouseY, int mouseButton)
    {
        boolean handled = super.mouseReleased(screenMouseX, screenMouseY, mouseButton);

        return screenControl.mouseReleased(screenMouseX, screenMouseY, mouseButton) || handled;
    }

    @Override
    public boolean mouseScrolled(double screenMouseX, double screenMouseY, double scrollAmount)
    {
        boolean handled = super.mouseScrolled(screenMouseX, screenMouseY, scrollAmount);

        return screenControl.mouseScrolled(screenMouseX, screenMouseY, scrollAmount) || handled;
    }

    @Override
    public final boolean keyPressed(int keyCode, int scanCode, int mods)
    {
        if (screenControl.keyPressed(keyCode, scanCode, mods))
        {
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
