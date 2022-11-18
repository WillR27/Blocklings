package com.willr27.blocklings.client.gui;

import com.willr27.blocklings.client.gui.control.controls.TestControl;
import com.willr27.blocklings.client.gui2.Colour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TestScreen extends BlocklingsScreen
{
    @Override
    protected void init()
    {
        super.init();

        TestControl control1 = new TestControl();
        TestControl control2 = new TestControl();
        TestControl control3 = new TestControl();
        screenControl.addChild(control1);
        control1.addChild(control2);
        control2.addChild(control3);

        control1.setInnerScale(2.0f);
        control2.setInnerScale(4.0f);

        control1.setX(2);
        control1.setY(2);
        control2.setX(4);
        control2.setY(4);
        control3.setX(0);
        control3.setY(0);

        control1.setWidth(120);
        control1.setHeight(91);
        control2.setWidth(36);
        control2.setHeight(21);
        control3.setWidth(3);
        control3.setHeight(6);

        control1.setScrollableXY(true);
        control1.setMaxScrollOffsetX(200);
        control1.setMaxScrollOffsetY(300);
        control2.setScrollableXY(true);
        control2.setMaxScrollOffsetX(5);
        control2.setMaxScrollOffsetY(5);

        control1.setBackgroundColour(new Colour(255, 0, 255));
        control2.setBackgroundColour(new Colour(0, 255, 255));
        control3.setBackgroundColour(new Colour(255, 255, 0));
    }
}
