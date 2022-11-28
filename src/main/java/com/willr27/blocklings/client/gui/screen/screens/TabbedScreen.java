package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.GuiTextures;
import com.willr27.blocklings.client.gui.control.controls.TabbedControl;
import com.willr27.blocklings.client.gui.screen.BlocklingsScreen;
import com.willr27.blocklings.client.gui2.GuiTexture;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A screen that displays some content and tabs.
 */
@OnlyIn(Dist.CLIENT)
public class TabbedScreen extends BlocklingsScreen
{
    /**
     * The control that handles rendering the tabs and the content background.
     */
    @Nonnull
    protected final TabbedControl tabbedControl;

    /**
     * @param blockling the blockling.
     * @param tab the associated tab.
     */
    public TabbedScreen(@Nonnull BlocklingEntity blockling, @Nonnull TabbedControl.Tab tab)
    {
        super(blockling);

        tabbedControl = new TabbedControl(blockling, tab);
    }

    @Override
    protected void init()
    {
        super.init();

        tabbedControl.setParent(screenControl);
        tabbedControl.setPercentX(0.5f);
        tabbedControl.setPercentY(0.5f);
        // Cast the coords to ints as otherwise container slots will be misaligned.
        tabbedControl.setX((int) tabbedControl.getX());
        tabbedControl.setY((int) tabbedControl.getY());
        tabbedControl.moveY(-5);

    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
