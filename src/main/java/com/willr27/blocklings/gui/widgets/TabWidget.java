package com.willr27.blocklings.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.task.Task;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class TabWidget extends Widget
{
    public static final int SELECTED_HEIGHT = 18;
    public static final int UNSELECTED_HEIGHT = 15;
    public static final int OVERLAP = 1;

    public static final GuiTexture SELECTED_LEFT = new GuiTexture(GuiUtil.COMMON_WIDGETS, 0, 15, 4, SELECTED_HEIGHT);
    public static final GuiTexture SELECTED_RIGHT = new GuiTexture(GuiUtil.COMMON_WIDGETS, 252, 15, 4, SELECTED_HEIGHT);
    public static final GuiTexture UNSELECTED_LEFT = new GuiTexture(GuiUtil.COMMON_WIDGETS, 0, 33, 4, UNSELECTED_HEIGHT);
    public static final GuiTexture UNSELECTED_RIGHT = new GuiTexture(GuiUtil.COMMON_WIDGETS, 252, 33, 4, UNSELECTED_HEIGHT);

    private int selectedTabIndex = 0;
    List<Tab> tabs = new ArrayList<>();

    public TabWidget(Task task, FontRenderer font, int x, int y, int width)
    {
        super(font, x, y, width, UNSELECTED_HEIGHT);
    }

    public void add(String name, Runnable onSelect)
    {
        tabs.add(new Tab(name, onSelect));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (tabs.isEmpty())
        {
            return;
        }

        int edgePadding = 2;

        int baseTabWidth = (width + edgePadding * 2) / tabs.size() + OVERLAP;
        int numberOfTabsWithExtraWidth = (width + edgePadding * 2) % tabs.size();

        for (int i = 0; i < tabs.size(); i++)
        {
            Tab tab = tabs.get(i);
            tab.x = i != 0 ? tabs.get(i - 1).x + tabs.get(i - 1).width - OVERLAP : x - edgePadding;
            tab.width = i < numberOfTabsWithExtraWidth ? baseTabWidth + 1 : baseTabWidth;

            if (i == selectedTabIndex)
            {
                continue;
            }

            GuiTexture unselectedLeft = new GuiTexture(GuiUtil.COMMON_WIDGETS, UNSELECTED_LEFT.x, UNSELECTED_LEFT.y, tab.width - UNSELECTED_RIGHT.width, UNSELECTED_HEIGHT);
            unselectedLeft = i == 0 ? unselectedLeft.shift(4, 0).resize(-edgePadding, 0) : unselectedLeft;
            renderTexture(matrixStack, unselectedLeft, tab.x + (i == 0 ? edgePadding : 0), y);

            GuiTexture unselectedRight = i == tabs.size() - 1 ? UNSELECTED_RIGHT.shift(-4, 0).resize(-edgePadding - 1, 0) : UNSELECTED_RIGHT;
            renderTexture(matrixStack, unselectedRight, tab.x + tab.width - unselectedRight.width - (i == tabs.size() - 1 ? edgePadding + 1 : 0), y);

            String name = GuiUtil.trimWithEllipses(font, tab.name, tab.width - 10);
            font.draw(matrixStack, name, tab.x + tab.width / 2 - font.width(name) / 2 + 1, y + 2, 0x666666);
        }

        Tab tab = tabs.get(selectedTabIndex);

        GuiTexture selectedLeft = new GuiTexture(GuiUtil.COMMON_WIDGETS, SELECTED_LEFT.x, SELECTED_LEFT.y, tab.width - SELECTED_RIGHT.width, SELECTED_HEIGHT);
        selectedLeft = selectedTabIndex == 0 ? selectedLeft.shift(4, 0).resize(-edgePadding + 1, 0) : selectedLeft;
        renderTexture(matrixStack, selectedLeft, selectedTabIndex == 0 ? tab.x + edgePadding - 1 : tab.x, y - 1);

        GuiTexture selectedRight = selectedTabIndex == tabs.size() - 1 ? SELECTED_RIGHT.shift(-5, 0).resize(-edgePadding, 0) : SELECTED_RIGHT;
        renderTexture(matrixStack, selectedRight, tab.x + tab.width - selectedRight.width - (selectedTabIndex == tabs.size() - 1 ? edgePadding : 0), y - 1);

        String name = GuiUtil.trimWithEllipses(font, tab.name, tab.width - 10);
        renderCenteredText(matrixStack, name, tab.x - x - width + tab.width / 2 + 1, 2, false, 0xffffff);
    }

    public void renderTooltips(MatrixStack matrixStack, int mouseX, int mouseY, Screen screen)
    {
        Tab hoveredTab = getHoveredTab(mouseX, mouseY);

        if (hoveredTab != null)
        {
            screen.renderTooltip(matrixStack, new StringTextComponent(hoveredTab.name), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int state)
    {
        if (isPressed && isMouseOver(mouseX, mouseY))
        {
            Tab clickedTab = getHoveredTab(mouseX, mouseY);

            if (clickedTab != null)
            {
                selectedTabIndex = tabs.indexOf(clickedTab);
                clickedTab.onSelect.run();

                return true;
            }
        }

        return super.mouseReleased(mouseX, mouseY, state);
    }

    private Tab getHoveredTab(int mouseX, int mouseY)
    {
        return tabs.stream().filter(tab -> GuiUtil.isMouseOver(mouseX, mouseY, tab.x, y, tab.width, UNSELECTED_HEIGHT)).findFirst().orElse(null);
    }

    private class Tab
    {
        public final String name;
        public final Runnable onSelect;

        public int x = 0;
        public int width = 30;

        public Tab(String name, Runnable onSelect)
        {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}
