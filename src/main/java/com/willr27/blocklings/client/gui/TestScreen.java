package com.willr27.blocklings.client.gui;

import com.willr27.blocklings.client.gui.control.controls.TestControl;
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
        rootControl.addChild(control1);
        control1.addChild(control2);
        control2.addChild(control3);

        control1.setInnerScale(2.0f);
        control2.setInnerScale(2.0f);

        control1.setX(2);
        control1.setY(2);
        control2.setX(1);
        control2.setY(1);
        control3.setX(2);
        control3.setY(2);
    }
}
