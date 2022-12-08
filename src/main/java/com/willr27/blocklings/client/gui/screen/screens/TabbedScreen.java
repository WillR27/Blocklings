package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.TabbedControl;
import com.willr27.blocklings.client.gui.screen.BlocklingsScreen;
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
     * The control that encompasses the area that the content should be displayed.
     */
    @Nonnull
    public final Control contentControl;

    /**
     * @param blockling the blockling.
     * @param tab the associated tab.
     */
    public TabbedScreen(@Nonnull BlocklingEntity blockling, @Nonnull TabbedControl.Tab tab)
    {
        super(blockling);

        tabbedControl = new TabbedControl(blockling, tab);

        contentControl = new Control();
        contentControl.setParent(tabbedControl);
        contentControl.setPercentX(0.5f);
        contentControl.setPercentY(0.5f);
        contentControl.setWidth(158);
        contentControl.setHeight(148);
    }

    @Override
    protected void init()
    {
        super.init();

        tabbedControl.resetChildren();
        tabbedControl.setParent(screenControl);
        tabbedControl.setPercentX(0.5f);
        tabbedControl.setPercentY(0.5f);
        // Cast the coords to ints as otherwise container slots will be misaligned.
        tabbedControl.setX((int) tabbedControl.getX());
        tabbedControl.setY((int) tabbedControl.getY());
        tabbedControl.moveY(-5);
        contentControl.clearChildren();
        contentControl.setParent(tabbedControl);
        contentControl.setPercentX(0.5f);
        contentControl.setPercentY(0.5f);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
