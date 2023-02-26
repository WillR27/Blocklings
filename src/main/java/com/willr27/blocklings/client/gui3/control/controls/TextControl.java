package com.willr27.blocklings.client.gui3.control.controls;

import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.client.gui3.control.HorizontalAlignment;
import com.willr27.blocklings.client.gui3.control.VerticalAlignment;
import com.willr27.blocklings.client.gui3.control.event.events.PaddingChangedEvent;
import com.willr27.blocklings.util.event.EventHandler;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Displays a block of text.
 */
@OnlyIn(Dist.CLIENT)
public abstract class TextControl extends Control
{
    /**
     * The horizontal alignment of the text.
     */
    @Nonnull
    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;

    /**
     * The vertical alignment of the text.
     */
    @Nonnull
    private VerticalAlignment verticalAlignment = VerticalAlignment.MIDDLE;

    /**
     * The line height to use to position the text. Useful for centering text that doesn't
     * contain any characters with descenders.
     */
    private int lineHeight = font.lineHeight;

    /**
     */
    public TextControl()
    {
        onPositionChanged.subscribe((e) ->
        {
            recalcTextPosition();
        });

        onSizeChanged.subscribe((e) ->
        {
            recalcTextPosition();
        });

        EventHandler.Handler<PaddingChangedEvent> onParentPaddingChanged = (e) -> recalcTextPosition();

        onParentChanged.subscribe((e) ->
        {
            recalcTextPosition();

            if (e.oldParent != null)
            {
                e.oldParent.onPaddingChanged.unsubscribe(onParentPaddingChanged);
            }

            if (getParent() != null)
            {
                getParent().onPaddingChanged.subscribe(onParentPaddingChanged);
            }
        });
    }

    @Override
    public void recalcPixelX()
    {
        super.recalcPixelX();

        recalcTextPosition();
    }

    @Override
    public void recalcPixelY()
    {
        super.recalcPixelY();

        recalcTextPosition();
    }

    /**
     * Recalculates the screen position to render the text at.
     */
    protected abstract void recalcTextPosition();

    /**
     * @return the text inside the text field.
     */
    @Nonnull
    public abstract String getText();

    /**
     * Sets the text inside the text field.
     */
    public void setText(@Nonnull String text)
    {
        setText(new StringTextComponent(text));
    }

    /**
     * Sets the text inside the text field.
     */
    public abstract void setText(@Nonnull ITextComponent text);

    /**
     * @return the horizontal alignment of the text.
     */
    @Nonnull
    public HorizontalAlignment getHorizontalAlignment()
    {
        return horizontalAlignment;
    }

    /**
     * Sets the horizontal alignment of the text.
     */
    public void setHorizontalAlignment(@Nonnull HorizontalAlignment horizontalAlignment)
    {
        this.horizontalAlignment = horizontalAlignment;

        recalcTextPosition();
    }

    /**
     * @return the vertical alignment of the text.
     */
    @Nonnull
    public VerticalAlignment getVerticalAlignment()
    {
        return verticalAlignment;
    }

    /**
     * Sets the vertical alignment of the text.
     */
    public void setVerticalAlignment(@Nonnull VerticalAlignment verticalAlignment)
    {
        this.verticalAlignment = verticalAlignment;

        recalcTextPosition();
    }

    /**
     * @return the line height.
     */
    public int getLineHeight()
    {
        return lineHeight;
    }

    /**
     * Sets the line height.
     */
    public void setLineHeight(int lineHeight)
    {
        this.lineHeight = lineHeight;

        recalcTextPosition();
    }

    /**
     * Sets the line height to fit text that doesn't have descenders. Useful for number only text.
     */
    public void useDescenderlessLineHeight()
    {
        setLineHeight(9);
    }
}
