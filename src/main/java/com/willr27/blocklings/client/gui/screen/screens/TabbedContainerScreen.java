package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.TabbedUIControl;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseClickedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseScrolledEvent;
import com.willr27.blocklings.client.gui.screen.BlocklingsContainerScreen;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A base screen for all tabbed blockling screens.
 */
@OnlyIn(Dist.CLIENT)
public abstract class TabbedContainerScreen<T extends Container> extends BlocklingsContainerScreen<T>
{
    /**
     * The tabbed UI control.
     */
    @Nonnull
    protected final TabbedUIControl tabbedUIControl;

    /**
     * @param blockling the blockling associated with the screen.
     * @param container the container.
     * @param selectedTab the tab to select when the screen is opened.
     */
    public TabbedContainerScreen(@Nonnull BlocklingEntity blockling, @Nonnull T container, @Nonnull TabbedUIControl.Tab selectedTab)
    {
        super(blockling, container);

        Control background = new Control();
        background.setParent(screenControl);
        background.setInteractive(false);
        background.setWidthPercentage(1.0);
        background.setHeightPercentage(1.0);
        background.setBackgroundColour(0xaa000000);
        background.setDebugName("Screen Background");

        tabbedUIControl = new TabbedUIControl(blockling, selectedTab)
        {
            @Override
            protected void onMouseClicked(@Nonnull MouseClickedEvent e)
            {

            }

            @Override
            protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
            {

            }

            @Override
            public void onMouseScrolled(@Nonnull MouseScrolledEvent e)
            {

            }
        };
        tabbedUIControl.setParent(screenControl);
        tabbedUIControl.setHoverable(false);
        tabbedUIControl.setFocusable(false);
    }

    @Override
    protected void init()
    {
        super.init();

        screenControl.measureAndArrange();
        leftPos = (int) (tabbedUIControl.getPixelX() / tabbedUIControl.getGuiScale()) + 29;
        topPos = (int) (tabbedUIControl.getPixelY() / tabbedUIControl.getGuiScale()) + 25;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
