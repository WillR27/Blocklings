package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.panels.DockPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.GridPanel;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseClickedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.properties.Dock;
import com.willr27.blocklings.client.gui.properties.Flow;
import com.willr27.blocklings.client.gui.properties.GridDefinition;
import com.willr27.blocklings.client.gui.screen.BlocklingsScreen;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;

import javax.annotation.Nonnull;

public class TestScreen extends BlocklingsScreen
{
    /**
     * @param blockling the blockling associated with the screen.
     */
    public TestScreen(@Nonnull BlocklingEntity blockling)
    {
        super(blockling);

        Control control1 = new Control();
        control1.setParent(screenControl);
//        control1.setY(-100.0);
        control1.setHorizontalAlignment(1.5);
        control1.setVerticalAlignment(0.5);
        control1.setWidth(100.0);
        control1.setHeight(600.0);
        control1.setCanScrollHorizontally(true);
        control1.setCanScrollVertically(true);
//        control.setMaxWidth(100.0);
//        control.setShouldFitWidthToContent(true);
//        control.setShouldFitHeightToContent(true);
//        control.setWidthPercentage(0.5);
//        control.setHeightPercentage(0.75);
        control1.setPadding(5, 10, 15, 20);
        control1.setBackgroundColour(control1.randomColour());
//
//        Control control2 = new Control()
//        {
//            protected void mouseReleased(double mouseX, double mouseY, int button)
//            {
//                setWidth(163);
//                setHeight(20);
//                control.setInnerScale(0.5, 0.5);
////                setWidthPercentage(1.0);
////                setHeightPercentage(1.0);
//                control.setPadding(5, 10, 15, 20);
//                setHorizontalAlignment(0.5);
//                setVerticalAlignment(0.5);
////                setMargin(8);
//            }
//        };
//        control2.setParent(control);
//        control2.setBackgroundColour(control2.randomColour());
//
//        Control control3 = new Control();
//        control3.setParent(control);
//        control3.setWidth(20.0);
//        control3.setHeight(50.0);
//        control3.setHorizontalAlignment(0.5);
//        control3.setBackgroundColour(control3.randomColour());

        DockPanel dockPanel = new DockPanel();
        dockPanel.setParent(screenControl);
        dockPanel.setPadding(10.0, 5.0, 10.0, 5.0);
        dockPanel.setWidth(100.0);
        dockPanel.setHeight(100.0);
        dockPanel.setInnerScale(0.5, 0.5);

        Control dockPanelControl2 = new Control();
        dockPanel.addChild(dockPanelControl2, Dock.RIGHT);
        dockPanelControl2.setWidth(dockPanel.randomInt(10, 30));
        dockPanelControl2.setHeightPercentage(1.0);
        dockPanelControl2.setBackgroundColour(0xff00ff00);
        dockPanelControl2.setMargin(dockPanel.randomInt(0, 5));

        Control dockPanelControl1 = new Control();
        dockPanel.addChild(dockPanelControl1, Dock.BOTTOM);
        dockPanelControl1.setWidthPercentage(1.0);
        dockPanelControl1.setHeight(dockPanel.randomInt(10, 30));
        dockPanelControl1.setBackgroundColour(0xffff0000);
        dockPanelControl1.setMargin(dockPanel.randomInt(0, 5));

        Control dockPanelControl3 = new Control();
        dockPanel.addChild(dockPanelControl3, Dock.TOP);
        dockPanelControl3.setWidthPercentage(1.0);
        dockPanelControl3.setHeight(dockPanel.randomInt(10, 30));
        dockPanelControl3.setBackgroundColour(0xff0000ff);
        dockPanelControl3.setMargin(dockPanel.randomInt(0, 5));

        Control dockPanelControl4 = new Control();
        dockPanel.addChild(dockPanelControl4, Dock.LEFT);
        dockPanelControl4.setWidth(dockPanel.randomInt(10, 30));
        dockPanelControl4.setHeightPercentage(1.0);
        dockPanelControl4.setBackgroundColour(0xffffff00);
        dockPanelControl4.setMargin(dockPanel.randomInt(0, 5));

        Control dockPanelControl5 = new Control();
        dockPanel.addChild(dockPanelControl5, Dock.FILL);
        dockPanelControl5.setWidthPercentage(1.0);
        dockPanelControl5.setHeightPercentage(1.0);
        dockPanelControl5.setBackgroundColour(0xff00ffff);
        dockPanelControl5.setMargin(dockPanel.randomInt(0, 5));

        FlowPanel flowPanel = new FlowPanel();
        flowPanel.setParent(screenControl);
        flowPanel.setWidth(70.0);
        flowPanel.setHeight(70.0);
//        stackPanel.setMaxWidth(100.0);
//        stackPanel.setMaxHeight(100.0);
        flowPanel.setInnerScale(0.5, 0.5);
//        stackPanel.setMinScroll(0.0, 0.0);
//        stackPanel.setMaxScroll(100.0, 100.0);
        flowPanel.setCanScrollHorizontally(true);
        flowPanel.setCanScrollVertically(true);
        flowPanel.setPadding(10, 10, 10, 10);
//        stackPanel.setShouldFitWidthToContent(true);
//        stackPanel.setShouldFitHeightToContent(true);
        flowPanel.setFlow(Flow.TOP_LEFT_LEFT_TO_RIGHT);
        flowPanel.setHorizontalSpacing(4.0);
        flowPanel.setVerticalSpacing(4.0);
        flowPanel.setHorizontalAlignment(0.5);
        flowPanel.setVerticalAlignment(0.5);
//        flowPanel.setHorizontalContentAlignment(0.5);
//        flowPanel.setVerticalContentAlignment(0.5);
        flowPanel.setLineAlignment(0.5);
        flowPanel.setBackgroundColour(flowPanel.randomColour());

        for (int i = 0; i < 25; i++)
        {
            Control stackPanelControl = new Control();
            stackPanelControl.setParent(flowPanel);
            stackPanelControl.setWidth(stackPanelControl.randomInt(10, 40));
            stackPanelControl.setHeight(stackPanelControl.randomInt(10, 40));
//            stackPanelControl.setMargin(stackPanelControl.randomInt(2, 4));
            stackPanelControl.setMargin(1);
            stackPanelControl.setBackgroundColour(0xffffff00 | (i*30 << 0));
            stackPanelControl.eventBus.subscribe((BaseControl control, MouseClickedEvent e) -> control.setBackgroundColour(0xff00ff00));
            stackPanelControl.eventBus.subscribe((BaseControl control, MouseReleasedEvent e) -> System.out.println("Released!"));
        }

        GridPanel gridPanel = new GridPanel();
        gridPanel.setDraggableX(true);
        gridPanel.setDraggableY(true);
        gridPanel.setShouldScissor(false);
        gridPanel.setParent(screenControl);
        gridPanel.setPadding(10.0, 10.0, 10.0, 10.0);
        gridPanel.setInnerScale(0.5, 0.5);
        gridPanel.setWidth(100.0);
        gridPanel.setHeight(100.0);
        gridPanel.setVerticalAlignment(1.0);
        gridPanel.addRowDefinition(GridDefinition.FIXED, 50.0);
        gridPanel.addRowDefinition(GridDefinition.AUTO, 50.0);
        gridPanel.addRowDefinition(GridDefinition.RATIO, 1.0);
        gridPanel.addRowDefinition(GridDefinition.RATIO, 2.0);
        gridPanel.addColumnDefinition(GridDefinition.FIXED, 50.0);
        gridPanel.addColumnDefinition(GridDefinition.AUTO, 50.0);
        gridPanel.addColumnDefinition(GridDefinition.RATIO, 1.0);
        gridPanel.addColumnDefinition(GridDefinition.RATIO, 2.0);

        Control gridPanelControl1 = new Control();
        gridPanel.addChild(gridPanelControl1, 0, 0);
        gridPanelControl1.setWidthPercentage(1.0);
        gridPanelControl1.setHeightPercentage(1.0);
        gridPanelControl1.setBackgroundColour(0xffff0000);

        Control gridPanelControl2 = new Control();
        gridPanel.addChild(gridPanelControl2, 1, 1);
        gridPanelControl2.setWidth(10.0);
        gridPanelControl2.setHeight(10.0);
        gridPanelControl2.setBackgroundColour(0xff00ff00);

        Control gridPanelControl3 = new Control();
        gridPanel.addChild(gridPanelControl3, 1, 0);
        gridPanelControl3.setWidthPercentage(1.0);
        gridPanelControl3.setHeight(20.0);
        gridPanelControl3.setBackgroundColour(0xff0000ff);

        Control gridPanelControl4 = new Control();
        gridPanel.addChild(gridPanelControl4, 2, 2);
        gridPanelControl4.setWidthPercentage(1.0);
        gridPanelControl4.setHeightPercentage(1.0);
        gridPanelControl4.setBackgroundColour(0xffffff00);

        Control gridPanelControl5 = new Control();
        gridPanel.addChild(gridPanelControl5, 3, 3);
        gridPanelControl5.setWidthPercentage(1.0);
        gridPanelControl5.setHeightPercentage(1.0);
        gridPanelControl5.setBackgroundColour(0xff00ffff);
    }
}
