package com.willr27.blocklings.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.controls.ScreenControl;
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
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        screenControl.measureAndArrange();

        float guiScale = GuiUtil.getInstance().getGuiScale();

        matrixStack.pushPose();
        matrixStack.scale(1.0f / guiScale, 1.0f / guiScale, 1.0f);

        screenControl.forwardRender(matrixStack, mouseX, mouseY, partialTicks);

        matrixStack.popPose();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        screenControl.forwardMouseReleased(mouseX, mouseY, button);

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        screenControl.forwardMouseScrolled(mouseX, mouseY, amount);

        return super.mouseScrolled(mouseX, mouseY, amount);
    }
}
