package com.willr27.blocklings.client.gui.control.controls.panels;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.TextBlockControl;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.properties.Visibility;
import com.willr27.blocklings.client.gui.texture.Texture;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.ScissorBounds;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.client.gui3.GuiTextures;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Displays horizontal tabs that each have their own containers.
 */
@OnlyIn(Dist.CLIENT)
public class TabbedPanel extends Control
{
    /**
     * The tabs' container.
     */
    @Nonnull
    private final TabContainer tabContainer;

    /**
     * The container container.
     */
    @Nonnull
    private final Control containerContainer;

    /**
     * The currently selected tab.
     */
    @Nullable
    private TabControl selectedTab;

    /**
     */
    public TabbedPanel()
    {
        super();

        setWidthPercentage(1.0);
        setHeightPercentage(1.0);

        containerContainer = new Control();
        containerContainer.setParent(this);
        containerContainer.setWidthPercentage(1.0);
        containerContainer.setHeightPercentage(1.0);

        tabContainer = new TabContainer();
        tabContainer.setParent(this);
    }

    @Override
    public void measureChildren()
    {
        tabContainer.doMeasure(getDesiredWidth(), GuiTextures.Common.Tab.TAB_SELECTED_BACKGROUND.height);
        containerContainer.doMeasure(getDesiredWidth() - 2, getDesiredHeight() - GuiTextures.Common.Tab.FULLY_OPAQUE_HEIGHT - 1);
    }

    @Override
    protected void arrange()
    {
        tabContainer.setWidth(tabContainer.getDesiredWidth());
        tabContainer.setHeight(tabContainer.getDesiredHeight());
        tabContainer.setPosition(0.0, 0.0);
        containerContainer.setWidth(containerContainer.getDesiredWidth());
        containerContainer.setHeight(containerContainer.getDesiredHeight());
        containerContainer.setPosition(1.0, GuiTextures.Common.Tab.FULLY_OPAQUE_HEIGHT);
    }

    @Override
    public void addChild(@Nonnull BaseControl child)
    {
        if (child != tabContainer && child != containerContainer)
        {
            throw new UnsupportedOperationException("Cannot add child to TabbedPanel. Use TabbedPanel#addTab() instead.");
        }

        super.addChild(child);
    }

    @Override
    public void insertChildBefore(@Nonnull BaseControl controlToInsert, @Nonnull BaseControl controlToInsertBefore)
    {
        throw new UnsupportedOperationException("Cannot insert child to TabbedPanel. Use TabbedPanel#addTab() instead.");
    }

    @Override
    public void insertChildAfter(@Nonnull BaseControl controlToInsert, @Nonnull BaseControl controlToInsertAfter)
    {
        throw new UnsupportedOperationException("Cannot insert child to TabbedPanel. Use TabbedPanel#addTab() instead.");
    }

    @Override
    public void removeChild(@Nonnull BaseControl child)
    {
        throw new UnsupportedOperationException("Cannot remove child from TabbedPanel. Use TabbedPanel#removeTab() instead.");
    }

    /**
     * Adds a tab to the tabbed panel.
     *
     * @param name the tab's name.
     * @return the tab's content container.
     */
    @Nonnull
    public BaseControl addTab(@Nonnull ITextComponent name)
    {
        TabControl tab = new TabControl(name);
        tabContainer.addChild(tab);

        Control container = new Control();
        container.setParent(containerContainer);
        container.setWidthPercentage(1.0);
        container.setHeightPercentage(1.0);
        container.setVisibility(Visibility.COLLAPSED);

        if (tabContainer.getChildren().size() == 1)
        {
            setSelectedTab(tab);
        }

        return container;
    }

    /**
     * Removes a tab from the tabbed panel.
     *
     * @param name the tab's name.
     */
    public void removeTab(@Nonnull ITextComponent name)
    {
        int i = 0;

        while (i < tabContainer.getChildren().size())
        {
            TabControl tab = (TabControl) tabContainer.getChildren().get(i);

            if (tab.name.equals(name))
            {
                removeTab(i);
                break;
            }

            i++;
        }
    }

    /**
     * Removes a tab from the tabbed panel.
     *
     * @param index the index of the tab to remove.
     */
    public void removeTab(int index)
    {
        if (getTabIndex(selectedTab) == index)
        {
            setSelectedTab(null);
        }

        tabContainer.removeChild(tabContainer.getChildren().get(index));
        containerContainer.removeChild(containerContainer.getChildren().get(index));

        if (selectedTab == null && tabContainer.getChildren().size() > 0)
        {
            setSelectedTab((TabControl) tabContainer.getChildren().get(0));
        }
    }

    /**
     * Removes all tabs from the tabbed panel.
     */
    public void clearTabs()
    {
        tabContainer.clearChildren();
        containerContainer.clearChildren();
        selectedTab = null;
    }

    /**
     * Notifies all tabs of a change.
     */
    public void notifyTabsOfChange()
    {
        for (BaseControl child : tabContainer.getChildren())
        {
            TabControl tab = (TabControl) child;
            tab.onTabsChanged();
        }
    }

    /**
     * @param tab the tab to get the index of.
     * @return the index of the given tab.
     */
    public int getTabIndex(@Nonnull TabControl tab)
    {
        return tabContainer.getChildren().indexOf(tab);
    }

    /**
     * @return the container control at the given index.
     */
    @Nonnull
    public BaseControl getContainer(int index)
    {
        return containerContainer.getChildren().get(index);
    }

    /**
     * @return the selected tab control.
     */
    @Nullable
    private TabControl getSelectedTab()
    {
        return selectedTab;
    }

    /**
     * Sets the currently selected tab.
     */
    private void setSelectedTab(@Nullable TabControl tab)
    {
        if (tab != null && tab.isSelected())
        {
            return;
        }

        if (selectedTab != null)
        {
            selectedTab.onUnselected();
        }

        selectedTab = tab;

        if (selectedTab != null)
        {
            selectedTab.onSelected();

            containerContainer.getChildren().forEach(c -> c.setVisibility(Visibility.COLLAPSED));
            getContainer(getTabIndex(getSelectedTab())).setVisibility(Visibility.VISIBLE);
        }
    }

    /**
     * An individual tab control.
     */
    private class TabControl extends Control
    {
        /**
         * The tab's name.
         */
        @Nonnull
        public ITextComponent name;

        /**
         * The tab background texture.
         */
        @Nonnull
        private Texture backgroundTexture = Textures.Common.Tab.TAB_UNSELECTED_BACKGROUND;

        /**
         * The tab's text block.
         */
        @Nonnull
        private final TextBlockControl tabNameTextBlock;

        /**
         * @param name the tab's name.
         */
        public TabControl(@Nonnull ITextComponent name)
        {
            super();
            this.name = name;

            setWidthPercentage(1.0);

            tabNameTextBlock = new TextBlockControl();
            tabNameTextBlock.setParent(this);
            tabNameTextBlock.setText(name);
            tabNameTextBlock.setHorizontalAlignment(0.5);
            tabNameTextBlock.setVerticalAlignment(0.4);
            tabNameTextBlock.setHorizontalContentAlignment(0.5);

            onUnselected();
        }

        @Override
        protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
        {
            if (isFirst() && isLast())
            {
                renderTextureAsBackground(matrixStack, backgroundTexture.dx(GuiTextures.Common.Tab.EDGE_WIDTH).width((int) getWidth()));
            }
            else if (isFirst())
            {
                renderTextureAsBackground(matrixStack, backgroundTexture.dx((int) (backgroundTexture.width - getWidth())).width((int) getWidth()));
            }
            else if (isLast())
            {
                renderTextureAsBackground(matrixStack, backgroundTexture.width((int) getWidth()));
            }
            else
            {
                renderTextureAsBackground(matrixStack, backgroundTexture.width((int) getWidth() - GuiTextures.Common.Tab.EDGE_WIDTH));
                renderTextureAsBackground(matrixStack, backgroundTexture.dx(backgroundTexture.width - GuiTextures.Common.Tab.EDGE_WIDTH).width(GuiTextures.Common.Tab.EDGE_WIDTH), (int) (getWidth() - GuiTextures.Common.Tab.EDGE_WIDTH), 0);
            }

            super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
        }

        @Override
        public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
        {
            renderTooltip(matrixStack, mouseX, mouseY, name);
        }

        @Override
        protected void applyScissor(@Nonnull ScissorStack scissorStack)
        {
            double dx = 0.0;
            double dy = 0.0;
            double dw = 0.0;

            if (!isSelected())
            {
                if (isFirst())
                {
                    dx = getPixelScaleX();
                    dw = getPixelScaleX();
                }

                if (isLast())
                {
                    dw = getPixelScaleX();
                }

                if (isFirst() && isLast())
                {
                    dx = getPixelScaleX();
                    dw = getPixelScaleX() * 2;
                }

                dy = getPixelScaleY();
            }

            if (shouldScissor())
            {
                scissorStack.push(new ScissorBounds((int) Math.round(getPixelX() + dx) , (int) Math.round(getPixelY() + dy), (int) Math.round(getPixelWidth() - dw), (int) Math.round(getPixelHeight() - dy)));
                scissorStack.enable();
            }
            else
            {
                scissorStack.disable();
            }
        }

        @Override
        protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
        {
            setSelectedTab(this);
            e.setIsHandled(true);
        }

        /**
         * Updates the text padding.
         */
        private void updateTextPadding()
        {
            if (getParent() != null && getParent().getChildren().size() > 1)
            {
                if (isFirst())
                {
                    setPaddingLeft(3);
                    setPaddingRight(5);
                }
                else if (isLast())
                {
                    setPaddingLeft(5);
                    setPaddingRight(3);
                }
                else
                {
                    setPaddingLeft(5);
                    setPaddingRight(5);
                }
            }
            else
            {
                setPaddingLeft(3);
                setPaddingRight(3);
            }
        }

        /**
         * Called when the tabs list changes.
         */
        public void onTabsChanged()
        {
            updateTextPadding();
        }

        /**
         * Called when the tab is selected.
         */
        private void onSelected()
        {
            tabNameTextBlock.setShouldRenderShadow(true);
            tabNameTextBlock.setTextColour(0xffffffff);
            backgroundTexture = Textures.Common.Tab.TAB_SELECTED_BACKGROUND;
            setHeight(backgroundTexture.height);
            updateTextPadding();
        }

        /**
         * Called when the tab is unselected.
         */
        private void onUnselected()
        {
            tabNameTextBlock.setShouldRenderShadow(false);
            tabNameTextBlock.setTextColour(0xffeeeeee);
            backgroundTexture = Textures.Common.Tab.TAB_UNSELECTED_BACKGROUND;
            setHeight(backgroundTexture.height);
            updateTextPadding();
        }

        /**
         * @return whether the tab is the first tab.
         */
        private boolean isFirst()
        {
            return tabContainer.getChildren().indexOf(this) == 0;
        }

        /**
         * @return whether the tab is the last tab.
         */
        private boolean isLast()
        {
            return tabContainer.getChildren().indexOf(this) == tabContainer.getChildren().size() - 1;
        }

        /**
         * @return whether the tab is selected.
         */
        private boolean isSelected()
        {
            return getSelectedTab() == this;
        }
    }

    /**
     * A container for all the tabs in a {@link TabbedPanel}.
     */
    private class TabContainer extends Control
    {
        /**
         */
        public TabContainer()
        {
            super();

            setWidthPercentage(1.0);
            setHeightPercentage(1.0);
        }

        @Override
        public void measureChildren()
        {
            double availableWidth = getDesiredWidth();
            double availableHeight = getDesiredHeight();
            int numTabs = getChildren().size();
            double overlap = 1.0;

            if (numTabs == 1)
            {
                getChildrenCopy().get(0).doMeasure(availableWidth, availableHeight);
            }
            else if (numTabs == 2)
            {
                double tabWidth = (availableWidth / 2) + overlap;
                getChildrenCopy().get(0).doMeasure(tabWidth, availableHeight);
                getChildrenCopy().get(1).doMeasure(tabWidth, availableHeight);
            }
            else
            {
                double firstTabWidth = (availableWidth - (numTabs - 2) * 3.0) / numTabs;
                double middleTabsWidth = (availableWidth + 2 * 3.0) / numTabs;
                boolean wasLastTabRoundedUp = false;

                for (BaseControl child : getChildrenCopy())
                {
                    boolean isFirst = child == getChildrenCopy().get(0);
                    boolean isLast = child == getChildrenCopy().get(getChildrenCopy().size() - 1);
                    double width = isFirst ? firstTabWidth : isLast ? availableWidth : middleTabsWidth;
                    width = !wasLastTabRoundedUp ? Math.ceil(width) : Math.floor(width);
                    width += isLast ? 0.0 : overlap;

                    child.doMeasure(width, availableHeight);

                    availableWidth -= width - overlap;
                    wasLastTabRoundedUp = !wasLastTabRoundedUp;
                }
            }
        }

        @Override
        protected void arrange()
        {
            int numTabs = getChildren().size();

            if (numTabs == 1)
            {
                BaseControl control = getChildrenCopy().get(0);
                control.setWidth(control.getDesiredWidth());
                control.setHeight(control.getDesiredHeight());
                control.setPosition(0.0, 0.0);
            }
            else if (numTabs == 2)
            {
                BaseControl control = getChildrenCopy().get(0);
                control.setWidth(control.getDesiredWidth());
                control.setHeight(control.getDesiredHeight());
                control.setPosition(0.0, 0.0);
                BaseControl control2 = getChildrenCopy().get(1);
                control2.setWidth(control2.getDesiredWidth());
                control2.setHeight(control2.getDesiredHeight());
                control2.setPosition(control.getWidth() - 1.0, 0.0);
            }
            else
            {
                double nextX = 0.0;

                for (BaseControl child : getChildrenCopy())
                {
                    child.setWidth(child.getDesiredWidth());
                    child.setHeight(child.getDesiredHeight());
                    child.setPosition(nextX, 0.0);

                    nextX = child.getX() + child.getWidth() - 1.0;
                }
            }
        }
    }
}

