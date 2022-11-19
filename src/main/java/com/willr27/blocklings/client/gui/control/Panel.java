package com.willr27.blocklings.client.gui.control;

import com.willr27.blocklings.client.gui.control.Control;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A panel is a control that automatically lays out its contents.
 */
@OnlyIn(Dist.CLIENT)
public abstract class Panel extends Control
{
    /**
     */
    public Panel()
    {
        onChildAdded.subscribe((e) -> layoutContents());
        onChildRemoved.subscribe((e) -> layoutContents());
    }

    /**
     * Re-lays out the child controls of the panel.
     */
    public abstract void layoutContents();

    @Override
    public void setWidth(int width)
    {
        super.setWidth(width);

        layoutContents();
    }

    @Override
    public void setHeight(int height)
    {
        super.setHeight(height);

        layoutContents();
    }
}
