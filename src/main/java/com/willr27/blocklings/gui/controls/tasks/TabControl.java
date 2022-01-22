package com.willr27.blocklings.gui.controls.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.gui.*;
import com.willr27.blocklings.task.Task;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A control to display tabs.
 */
@OnlyIn(Dist.CLIENT)
public class TabControl extends Control
{
    /**
     * The height of a selected tab.
     */
    public static final int SELECTED_HEIGHT = 18;

    /**
     * The height of an unselected tab.
     */
    public static final int UNSELECTED_HEIGHT = 15;

    /**
     * The overlap between two tabs.
     */
    public static final int OVERLAP = 1;

    /**
     * The selected tab texture on the left.
     */
    @Nonnull
    public static final GuiTexture SELECTED_LEFT = new GuiTexture(GuiTextures.COMMON_WIDGETS, 0, 15, 4, SELECTED_HEIGHT);

    /**
     * The selected tab texture on the right.
     */
    @Nonnull
    public static final GuiTexture SELECTED_RIGHT = new GuiTexture(GuiTextures.COMMON_WIDGETS, 252, 15, 4, SELECTED_HEIGHT);

    /**
     * The unselected tab texture on the left.
     */
    @Nonnull
    public static final GuiTexture UNSELECTED_LEFT = new GuiTexture(GuiTextures.COMMON_WIDGETS, 0, 33, 4, UNSELECTED_HEIGHT);

    /**
     * The unselected tab texture on the right.
     */
    @Nonnull
    public static final GuiTexture UNSELECTED_RIGHT = new GuiTexture(GuiTextures.COMMON_WIDGETS, 252, 33, 4, UNSELECTED_HEIGHT);

    /**
     * The currently selected tab index.
     */
    private int selectedTabIndex = 0;

    /**
     * The list of tabs.
     */
    @Nonnull
    private final List<Tab> tabs = new ArrayList<>();

    /**
     * @param parent the parent control.
     * @param x the x position.
     * @param y the y position.
     * @param width the width.
     */
    public TabControl(@Nonnull IControl parent, int x, int y, int width)
    {
        super(parent, x, y, width, UNSELECTED_HEIGHT);
    }

    /**
     * Adds a new tab.
     *
     * @param name the name of the tab.
     * @param onSelect the callback to run when the tab is selected.
     */
    public void add(@Nonnull String name, @Nonnull Runnable onSelect)
    {
        tabs.add(new Tab(name, onSelect));
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
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
            tab.x = i != 0 ? tabs.get(i - 1).x + tabs.get(i - 1).width - OVERLAP : screenX - edgePadding;
            tab.width = i < numberOfTabsWithExtraWidth ? baseTabWidth + 1 : baseTabWidth;

            if (i == selectedTabIndex)
            {
                continue;
            }

            GuiTexture unselectedLeft = new GuiTexture(GuiTextures.COMMON_WIDGETS, UNSELECTED_LEFT.x, UNSELECTED_LEFT.y, tab.width - UNSELECTED_RIGHT.width, UNSELECTED_HEIGHT);
            unselectedLeft = i == 0 ? unselectedLeft.shift(4, 0).resize(-edgePadding, 0) : unselectedLeft;
            renderTexture(matrixStack, unselectedLeft, tab.x + (i == 0 ? edgePadding : 0), screenY);

            GuiTexture unselectedRight = i == tabs.size() - 1 ? UNSELECTED_RIGHT.shift(-4, 0).resize(-edgePadding - 1, 0) : UNSELECTED_RIGHT;
            renderTexture(matrixStack, unselectedRight, tab.x + tab.width - unselectedRight.width - (i == tabs.size() - 1 ? edgePadding + 1 : 0), screenY);

            String name = GuiUtil.trimWithEllipses(font, tab.name, tab.width - 10);
            font.draw(matrixStack, name, tab.x + tab.width / 2 - font.width(name) / 2 + 1, screenY + 2, 0x666666);
        }

        Tab tab = tabs.get(selectedTabIndex);

        GuiTexture selectedLeft = new GuiTexture(GuiTextures.COMMON_WIDGETS, SELECTED_LEFT.x, SELECTED_LEFT.y, tab.width - SELECTED_RIGHT.width, SELECTED_HEIGHT);
        selectedLeft = selectedTabIndex == 0 ? selectedLeft.shift(4, 0).resize(-edgePadding + 1, 0) : selectedLeft;
        renderTexture(matrixStack, selectedLeft, selectedTabIndex == 0 ? tab.x + edgePadding - 1 : tab.x, screenY - 1);

        GuiTexture selectedRight = selectedTabIndex == tabs.size() - 1 ? SELECTED_RIGHT.shift(-5, 0).resize(-edgePadding, 0) : SELECTED_RIGHT;
        renderTexture(matrixStack, selectedRight, tab.x + tab.width - selectedRight.width - (selectedTabIndex == tabs.size() - 1 ? edgePadding : 0), screenY - 1);

        String name = GuiUtil.trimWithEllipses(font, tab.name, tab.width - 10);
        renderCenteredText(matrixStack, name, tab.x - screenX - width + tab.width / 2 + 1, 2, false, 0xffffff);
    }

    /**
     * Renders the tooltips for the tabs.
     *
     * @param matrixStack the matrix stack.
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     */
    public void renderTooltips(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        Tab hoveredTab = getHoveredTab(mouseX, mouseY);

        if (hoveredTab != null)
        {
            screen.renderTooltip(matrixStack, new StringTextComponent(hoveredTab.name), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        if (isPressed() && isMouseOver(mouseX, mouseY))
        {
            Tab clickedTab = getHoveredTab(mouseX, mouseY);

            if (clickedTab != null)
            {
                selectedTabIndex = tabs.indexOf(clickedTab);
                clickedTab.onSelect.run();

                return true;
            }
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * @return the tab at the mouse position.
     */
    @Nullable
    private Tab getHoveredTab(int mouseX, int mouseY)
    {
        return tabs.stream().filter(tab -> GuiUtil.isMouseOver(mouseX, mouseY, tab.x, screenY, tab.width, UNSELECTED_HEIGHT)).findFirst().orElse(null);
    }

    /**
     * A class to represent a tab.
     */
    private static class Tab
    {
        /**
         * The tab's name.
         */
        @Nonnull
        public final String name;

        /**
         * The callback to call when the tab is selected.
         */
        @Nonnull
        public final Runnable onSelect;

        /**
         * The x position.
         */
        public int x = 0;

        /**
         * The tab width.
         */
        public int width = 30;

        public Tab(@Nonnull String name, @Nonnull Runnable onSelect)
        {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}
