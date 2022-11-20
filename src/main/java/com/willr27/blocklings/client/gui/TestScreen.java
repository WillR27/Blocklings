package com.willr27.blocklings.client.gui;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Direction;
import com.willr27.blocklings.client.gui.control.controls.TestControl;
import com.willr27.blocklings.client.gui.control.DragReorderType;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MousePosEvent;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.client.gui2.controls.Orientation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class TestScreen extends BlocklingsScreen
{
    @Override
    protected void init()
    {
        super.init();

        pog();
    }

    private void pog()
    {
        FlowPanel flowPanel = new FlowPanel();
        flowPanel.setInnerScale(0.5f);
        flowPanel.setParent(screenControl);
        flowPanel.setWidth(300);
        flowPanel.setHeight(200);
        flowPanel.setPadding(3, 5, 2, 6);
        flowPanel.setBackgroundColour(new Colour(1.0f, 0.0f, 0.0f));
        flowPanel.setBlocksDrag(false);
        flowPanel.setDraggableXY(true);
        flowPanel.setScrollableXY(true);
        flowPanel.setFlowDirection(Direction.TOP_TO_BOTTOM);
        flowPanel.setDragReorderType(DragReorderType.INSERT_ON_RELEASE);
        flowPanel.setItemGapX(3);
        flowPanel.setItemGapY(20);

        Random random = new Random();
        for (int i = 0; i < 99; i++)
        {
            Control control = new Control()
            {
                @Override
                public void onDragStart(@Nonnull MousePosEvent mousePosEvent)
                {
                    setBackgroundColour(new Colour(0.0f, 1.0f, 1.0f));
                }

                @Override
                public void onDragEnd(@Nonnull MousePosEvent mousePosEvent)
                {
                    setBackgroundColour(new Colour(1.0f, 1.0f, 1.0f));
                }

                @Override
                protected void onMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
                {
//                    getParent().removeChild(this);

                    super.onMouseReleased(mouseButtonEvent);
                }
            };
            control.setParent(flowPanel);
            control.setDraggableXY(true);
            control.setWidth(90);
            control.setHeight(76);
            control.setMargins(4, 1, 3, 5);
            control.setBackgroundColour(new Colour(random.nextFloat(), random.nextFloat(), random.nextFloat()));
        }
    }

    private void egg()
    {
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
