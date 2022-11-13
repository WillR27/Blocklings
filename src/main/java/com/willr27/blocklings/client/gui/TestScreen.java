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
        rootControl.addChild(control1);
        control1.addChild(control2);
    }
}
