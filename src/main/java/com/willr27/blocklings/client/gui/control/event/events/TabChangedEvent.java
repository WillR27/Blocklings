package com.willr27.blocklings.client.gui.control.event.events;

import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.controls.panels.TabbedPanel;
import com.willr27.blocklings.util.event.IEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * An event fired when a tab is changed.
 */
@OnlyIn(Dist.CLIENT)
public class TabChangedEvent implements IEvent
{
    /**
     * The tab control that was switched to.
     */
    @Nonnull
    public final TabbedPanel.TabControl tabControl;

    /**
     * The tab container that was switched to.
     */
    @Nonnull
    public final BaseControl containerControl;

    /**
     * @param tabControl the tab control that was switched to.
     * @param containerControl the tab container that was switched to.
     */
    public TabChangedEvent(@Nonnull TabbedPanel.TabControl tabControl, @Nonnull BaseControl containerControl)
    {
        this.tabControl = tabControl;
        this.containerControl = containerControl;
    }
}
