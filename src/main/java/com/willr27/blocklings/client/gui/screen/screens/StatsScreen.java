package com.willr27.blocklings.client.gui.screen.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Side;
import com.willr27.blocklings.client.gui.control.controls.EntityControl;
import com.willr27.blocklings.client.gui.control.controls.TabbedControl;
import com.willr27.blocklings.client.gui.control.controls.TextFieldControl;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.item.BlocklingsItems;
import net.minecraft.util.text.ITextComponent;
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
        textFieldControl.setMaxTextLength(25);
        textFieldControl.setText(blockling.getCustomName().getString());
        textFieldControl.focusChanged.subscribe((e) ->
        {
            if (!textFieldControl.getText().trim().isEmpty())
            {
                blockling.setCustomName(new StringTextComponent(textFieldControl.getText()), true);
            }
            else
            {
                ITextComponent name = BlocklingsItems.BLOCKLING.get().getName(BlocklingsItems.BLOCKLING.get().getDefaultInstance());
                blockling.setCustomName(new StringTextComponent(name.getString()), true);
                textFieldControl.setText(name.getString());
            }
        });

        EntityControl entityControl = new EntityControl();
        entityControl.setParent(contentControl);
        entityControl.setEntity(blockling);
        entityControl.setWidth(48);
        entityControl.setHeight(48);
        entityControl.setScaleToBoundingBox(true);
        entityControl.setEntityScale(1.0f);
        entityControl.setOffsetY(0.0f);
        entityControl.setShouldScissor(false);
        entityControl.setPercentX(0.5f);
        entityControl.setPercentY(0.3f);
    }
}
