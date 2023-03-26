package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseClickedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseScrolledEvent;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A scrollbar control.
 */
@OnlyIn(Dist.CLIENT)
public class ScrollbarControl extends Control
{
    /**
     * The grabber control.
     */
    @Nonnull
    private final Control grabber;

    /**
     * The control the scrollbar is attached to.
     */
    @Nullable
    private BaseControl attachedControl;

    /**
     */
    public ScrollbarControl()
    {
        super();

        setWidth(Textures.Common.Scrollbar.GRABBER_PRESSED.width);
        setPressable(false);

        grabber = new TexturedControl(Textures.Common.Scrollbar.GRABBER_UNPRESSED, Textures.Common.Scrollbar.GRABBER_PRESSED)
        {
            @Override
            protected void onRenderUpdate(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                if (getAttachedControl() != null)
                {
                    if (!getAttachedControl().canScrollVertically())
                    {
                        setInteractive(false);
                        setVerticalAlignment(0.0);
                    }
                    else
                    {
                        if (getAttachedControl().getMaxScrollY() - getAttachedControl().getMinScrollY() == 0.0)
                        {
                            setInteractive(false);
                            setVerticalAlignment(0.0);
                        }
                        else
                        {
                            setInteractive(true);

                            if (isDragging())
                            {
                                double minPixelY = getParent().toPixelY(0.0 + getHeight() / 2.0);
                                double maxPixelY = getParent().toPixelY(getParent().getHeight() - getHeight() / 2.0);
                                double percentage = ((mouseY - getHeight() / 2.0) - minPixelY) / (maxPixelY - minPixelY);

                                getAttachedControl().setScrollPercentY(percentage);
                            }

                            setVerticalAlignment(getAttachedControl().getScrollPercentY());
                        }
                    }
                }
            }

            @Override
            protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                if (isInteractive())
                {
                    if (isPressed() && getPressedBackgroundTexture() != null)
                    {
                        renderTextureAsBackground(matrixStack, getPressedBackgroundTexture());
                    }
                    else
                    {
                        renderTextureAsBackground(matrixStack, getBackgroundTexture());
                    }
                }
                else
                {
                    RenderSystem.color3f(0.7f, 0.7f, 0.7f);
                    renderTextureAsBackground(matrixStack, getBackgroundTexture());
                }
            }
        };
        grabber.setParent(this);
        grabber.setDraggableY(true);
        grabber.setDragThreshold(0.0);
    }

    @Override
    protected void onMouseClicked(@Nonnull MouseClickedEvent e)
    {
        if (grabber.isInteractive())
        {
            grabber.setIsDragging(true);
            grabber.setPressed(true);

            e.setIsHandled(true);
        }
    }

    @Override
    public void onMouseScrolled(@Nonnull MouseScrolledEvent e)
    {
        if (getAttachedControl() != null)
        {
            if (getAttachedControl().canScrollVertically())
            {
                getAttachedControl().onMouseScrolled(e);
            }
        }
    }

    /**
     * @return the control the scrollbar is attached to.
     */
    @Nullable
    public BaseControl getAttachedControl()
    {
        return attachedControl;
    }

    /**
     * Sets the control the scrollbar is attached to.
     *
     * @param control the control the scrollbar is attached to.
     */
    public void setAttachedControl(@Nullable BaseControl control)
    {
        attachedControl = control;
    }
}
