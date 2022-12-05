package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.control.*;
import com.willr27.blocklings.client.gui.control.controls.TestControl;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MousePosEvent;
import com.willr27.blocklings.client.gui.screen.BlocklingsScreen;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class TestScreen extends BlocklingsScreen
{
    public TestScreen(@Nonnull BlocklingEntity blockling)
    {
        super(blockling);
    }

    @Override
    protected void init()
    {
        super.init();

        pog();
    }

    private void wog()
    {
        Control control1 = new Control()
        {
            @Override
            protected void onMouseClicked(@Nonnull MouseButtonEvent mouseButtonEvent)
            {
                setWidth(90 * mouseButtonEvent.mouseButton);
                setHeight(50 * mouseButtonEvent.mouseButton);

                super.onMouseClicked(mouseButtonEvent);
            }
        };
        control1.setParent(screenControl);
        control1.setWidth(300);
        control1.setHeight(200);
        control1.setInnerScale(2.0f);
        control1.setBackgroundColour(new Colour(255, 0, 255));

        Control anchorControl = new Control();
        anchorControl.setWidth(30);
        anchorControl.setHeight(20);
        anchorControl.setX(100);
        anchorControl.setY(50);
        anchorControl.setParent(control1);
        anchorControl.setAnchor(EnumSet.noneOf(Side.class));
        anchorControl.setBackgroundColour(new Colour(0, 255, 120));
    }

    private void pog()
    {
        Control control333 = new Control();
//        control333.setParent(screenControl);
        control333.setHeight(100);
        control333.setDraggableXY(true);
        control333.setScrollableXY(true);
        control333.setMaxScrollOffsetY(270);

        FlowPanel flowPanel = new FlowPanel();
        flowPanel.setParent(screenControl);
        flowPanel.setInnerScale(2.0f);
        flowPanel.setWidth(300);
        flowPanel.setHeight(200);
        flowPanel.setPadding(3, 2, 5, 7);
        flowPanel.setBackgroundColour(new Colour(1.0f, 0.0f, 0.0f));
        flowPanel.setBlocksDrag(true);
        flowPanel.setDraggableXY(true);
        flowPanel.setScrollableXY(true);
        flowPanel.setFlowDirection(Direction.LEFT_TO_RIGHT);
        flowPanel.setOverflowOrientation(Orientation.VERTICAL);
        flowPanel.setDragReorderType(DragReorderType.INSERT_ON_RELEASE);
        flowPanel.setFitToContentsXY(false);
        flowPanel.setItemGapX(1);
        flowPanel.setItemGapY(1);

        Random random = new Random();
        for (int i = 0; i < 400 ; i++)
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
            control.setWidth(23);
            control.setHeight(35);
            control.setMargins(5, 2, 1, 3);
            control.setBackgroundColour(new Colour(random.nextFloat(), random.nextFloat(), random.nextFloat()));
        }

        int i = 0;
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
