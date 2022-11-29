package com.willr27.blocklings.client.gui.screen.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.Side;
import com.willr27.blocklings.client.gui.control.controls.TabbedControl;
import com.willr27.blocklings.client.gui.control.controls.TextFieldControl;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * A screen to display the blockling's stats.
 */
@OnlyIn(Dist.CLIENT)
public class StatsScreen extends TabbedScreen
{
    /**
     * @param blockling the blockling.
     */
    public StatsScreen(@Nonnull BlocklingEntity blockling)
    {
        super(blockling, TabbedControl.Tab.STATS);
    }

    @Override
    protected void init()
    {
        super.init();

        TextFieldControl textFieldControl = new TextFieldControl();
        textFieldControl.setParent(contentControl);
        textFieldControl.setX(0);
        textFieldControl.setY(0);
        textFieldControl.setWidth(contentControl.getWidth());
        textFieldControl.setAnchor(EnumSet.of(Side.LEFT, Side.RIGHT));
        textFieldControl.setHeight(20);
        textFieldControl.setText("HELLO");
    }
}
