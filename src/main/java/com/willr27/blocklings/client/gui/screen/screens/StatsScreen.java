package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.control.controls.TextFieldControl;
import com.willr27.blocklings.client.gui.control.controls.TabbedUIControl;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A screen to show the stats of a blockling.
 */
@OnlyIn(Dist.CLIENT)
public class StatsScreen extends TabbedScreen
{
    /**
     * @param blockling the blockling associated with the screen.
     */
    public StatsScreen(@Nonnull BlocklingEntity blockling)
    {
        super(blockling, TabbedUIControl.Tab.STATS);

        TextFieldControl textFieldControl = new TextFieldControl();
        textFieldControl.setParent(tabbedUIControl.contentControl);
        textFieldControl.setWidthPercentage(1.0);
        textFieldControl.setHeight(20.0);
        textFieldControl.setText("Hello World!");
        textFieldControl.setHorizontalContentAlignment(0.5);
        textFieldControl.setMaxTextLength(25);
    }
}
