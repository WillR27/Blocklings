package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.control.controls.TabbedUIControl;
import com.willr27.blocklings.client.gui.screen.BlocklingsScreen;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A base screen for all tabbed blockling screens.
 */
@OnlyIn(Dist.CLIENT)
public abstract class TabbedScreen extends BlocklingsScreen
{
    /**
     * The tabbed UI control.
     */
    @Nonnull
    protected final TabbedUIControl tabbedUIControl;

    /**
     * @param blockling the blockling associated with the screen.
     * @param selectedTab the tab to select when the screen is opened.
     */
    public TabbedScreen(@Nonnull BlocklingEntity blockling, @Nonnull TabbedUIControl.Tab selectedTab)
    {
        super(blockling);

        tabbedUIControl = new TabbedUIControl(blockling, selectedTab);
        tabbedUIControl.setParent(screenControl);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
