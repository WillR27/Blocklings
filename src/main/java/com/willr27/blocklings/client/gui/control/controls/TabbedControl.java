package com.willr27.blocklings.client.gui.control.controls;

import com.willr27.blocklings.client.gui.GuiTextures;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.*;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.client.gui2.GuiTexture;
import com.willr27.blocklings.entity.BlocklingsEntityTypes;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Displays horizontal tabs that each have their own containers.
 */
@OnlyIn(Dist.CLIENT)
public class TabbedControl extends Control
{
    /**
     * The control that contains the tab controls.
     */
    @Nonnull
    private final Control tabContainerControl;

    /**
     * The control that contains tab containers.
     */
    @Nonnull
    private final Control containerContainerControl;

    /**
     * The currently selected tab.
     */
    @Nullable
    private TabControl selectedTab;

    /**
     */
    public TabbedControl(@Nonnull Control parent)
    {
        setParent(parent);
        setWidth(new Fill(1.0f));
        setHeight(new Fill(1.0f));
        setShouldScissor(false);

        tabContainerControl = new Control();
        tabContainerControl.setParent(this);
        tabContainerControl.setHeight(GuiTextures.Common.Tab.FULLY_OPAQUE_HEIGHT);
        tabContainerControl.setHitbox(new Hitbox.RectangleHitbox(0, 0, tabContainerControl.getWidth(), GuiTextures.Common.Tab.TAB_SELECTED_BACKGROUND.height));
        tabContainerControl.setDock(Dock.TOP);
        tabContainerControl.setShouldScissor(false);

        containerContainerControl = new Control();
        containerContainerControl.setParent(this);
        containerContainerControl.setDock(Dock.FILL);

        onSizeChanged.subscribe((e) ->
        {
            resizeTabs();
        });
    }

    @Nonnull
    @Override
    public List<Control> getRenderChildren()
    {
        return getReverseChildrenCopy();
    }

    /**
     * Adds a tab with the given name and returns the tab container control.
     *
     * @return the tab container control.
     */
    public Control addTab(@Nonnull String name)
    {
        TabControl tabControl = new TabControl(name);
        tabControl.setParent(tabContainerControl);

        Control containerControl = new Control();
        containerControl.setParent(containerContainerControl);
        containerControl.setWidth(new Fill(1.0f));
        containerControl.setHeight(new Fill(1.0f));
        containerControl.setBackgroundColour(Colour.fromARGBInt(getRandom().nextInt()));

        EntityControl entityControl = new EntityControl();
        entityControl.setParent(containerControl);
        entityControl.setWidth(50.0f);
        entityControl.setHeight(50.0f);
        entityControl.setEntity(new BlocklingEntity(BlocklingsEntityTypes.BLOCKLING.get(), Minecraft.getInstance().level));

        if (selectedTab == null)
        {
            setSelectedTab(tabControl);
        }
        else
        {
            containerControl.setVisible(false);
        }

        resizeTabs();

        tabContainerControl.getChildren().forEach(control -> ((TabControl) control).onTabsChanged());

        return containerControl;
    }

    /**
     * Sets the selected tab.
     */
    private void setSelectedTab(@Nullable TabControl tabControl)
    {
        if (selectedTab != null)
        {
            selectedTab.onUnselected();
            containerContainerControl.getChildren().get(tabContainerControl.getChildren().indexOf(selectedTab)).setVisible(false);
        }

        selectedTab = tabControl;

        if (selectedTab != null)
        {
            selectedTab.onSelected();
            containerContainerControl.getChildren().get(tabContainerControl.getChildren().indexOf(selectedTab)).setVisible(true);
        }

        resizeTabs();
    }

    /**
     * Adjusts the width of the tab controls.
     */
    private void resizeTabs()
    {
        if (tabContainerControl.getChildren().isEmpty())
        {
            return;
        }

        if (tabContainerControl.getChildren().size() == 1)
        {
            tabContainerControl.getChildren().get(0).setPercentWidth(1.0f);
        }

        int overlap = 1;
        int tabEdgeWidth = 3;
        int totalTabs = tabContainerControl.getChildren().size();
        float controlX = 0.0f;
        float availableWidth = getWidth() + (totalTabs > 2 ? tabEdgeWidth * 2 : 0);
        float individualWidth = availableWidth / tabContainerControl.getChildren().size();

        for (int i = 0; i < totalTabs; i++)
        {
            int firstLastWidthReduction = totalTabs > 2 && (i == 0 || i == totalTabs - 1) ? tabEdgeWidth : 0;
            int middleOverlapAdditionalWidth = i != 0 ? overlap : 0;
            int controlWidth = Math.round(Math.min(availableWidth, individualWidth) + (middleOverlapAdditionalWidth) - (firstLastWidthReduction));

            Control control = tabContainerControl.getChildren().get(i);
            control.setX(controlX);
            control.setWidth(controlWidth);

            availableWidth -= control.getWidth() - middleOverlapAdditionalWidth;
            controlX += control.getWidth() - overlap;
        }

        if (selectedTab.isFirst())
        {
            selectedTab.setX(-1.0f);
            selectedTab.setWidth(selectedTab.getWidth() + (tabContainerControl.getChildren().size() == 1 ? 2.0f : 1.0f));
        }
        else if (selectedTab.isLast())
        {
            selectedTab.setWidth(selectedTab.getWidth() + 1.0f);
        }
    }

    /**
     * An individual tab control.
     */
    private class TabControl extends Control
    {
        /**
         * The tab name.
         */
        @Nonnull
        private final String name;

        /**
         * The tab background texture.
         */
        @Nonnull
        private GuiTexture backgroundTexture = GuiTextures.Common.Tab.TAB_UNSELECTED_BACKGROUND;

        /**
         * The tab text control.
         */
        @Nonnull
        private final TextBlockControl tabTextControl;

        /**
         * @param name the tab name.
         */
        public TabControl(@Nonnull String name)
        {
            super();
            this.name = name;

            setShouldScissor(false);
            setHeight(new Fill(1.0f));

            tabTextControl = new TextBlockControl();
            tabTextControl.setParent(this);
            tabTextControl.setShouldScissor(false);
            tabTextControl.setInteractive(false);
            tabTextControl.setText(name);
            tabTextControl.setHorizontalAlignment(HorizontalAlignment.MIDDLE);
            tabTextControl.setVerticalAlignment(VerticalAlignment.MIDDLE);
            tabTextControl.setWidth(new Fill(1.0f));
            tabTextControl.setHeight(new Fill(1.0f));
            tabTextControl.setAlignmentX(new Alignment(0.5f));
            tabTextControl.setAlignmentY(new Alignment(0.5f));

            onUnselected();

            // Render above its contents.
            addRenderOperation(new RenderOperation(
                    (c, r) -> r.matrixStack.translate(0.0, 0.0, 1.0),
                    (c, r) -> r.matrixStack.translate(0.0, 0.0, -1.0)));
        }

        @Override
        public void onTick()
        {
            tabTextControl.recalcTextPosition();
        }

        @Override
        public void onRenderBackground(@Nonnull RenderArgs renderArgs)
        {
            renderArgs.scissorStack.disable();

            if (isFirst() && isLast())
            {
                renderTexture(renderArgs.matrixStack, backgroundTexture.shift(GuiTextures.Common.Tab.EDGE_WIDTH, 0).width((int) getWidth()));
            }
            else if (isFirst())
            {
                renderTexture(renderArgs.matrixStack, backgroundTexture.shift((int) (backgroundTexture.width - getWidth()), 0).width((int) getWidth()));
            }
            else if (isLast())
            {
                renderTexture(renderArgs.matrixStack, backgroundTexture.width((int) getWidth()));
            }
            else
            {
                renderTexture(renderArgs.matrixStack, backgroundTexture.width((int) getWidth() - GuiTextures.Common.Tab.EDGE_WIDTH));
                renderTexture(renderArgs.matrixStack, (int) (getWidth() - GuiTextures.Common.Tab.EDGE_WIDTH), 0, backgroundTexture.shift(backgroundTexture.width - GuiTextures.Common.Tab.EDGE_WIDTH, 0).width(GuiTextures.Common.Tab.EDGE_WIDTH));
            }

            super.onRenderBackground(renderArgs);
        }

        @Override
        public void onRenderTooltip(@Nonnull RenderArgs renderArgs)
        {
            renderTooltip(renderArgs, new StringTextComponent(name));
        }

        @Override
        protected void onMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
        {
            setSelectedTab(this);

            super.onMouseReleased(mouseButtonEvent);
        }

        /**
         * Called when the tab is selected.
         */
        public void onSelected()
        {
            tabTextControl.setShouldRenderShadow(true);
            backgroundTexture = GuiTextures.Common.Tab.TAB_SELECTED_BACKGROUND;

            setY(-1.0f);
            setHitbox(new Hitbox.RectangleHitbox(0, -1, getWidth(), backgroundTexture.height + 1)
            {
                @Override
                public void resize(float oldWidth, float newWidth, float oldHeight, float newHeight)
                {
                    float dWidth = newWidth - oldWidth;
                    width += dWidth;
                    width = Math.max(0, width);
                }
            });
            updateTextPadding();
            setPadding(Side.TOP, 2);
        }

        /**
         * Called when the tab is unselected.
         */
        public void onUnselected()
        {
            tabTextControl.setShouldRenderShadow(false);
            backgroundTexture = GuiTextures.Common.Tab.TAB_UNSELECTED_BACKGROUND;

            setY(0.0f);
            setHitbox(new Hitbox.RectangleHitbox(0,0, getWidth(), backgroundTexture.height)
            {
                @Override
                public void resize(float oldWidth, float newWidth, float oldHeight, float newHeight)
                {
                    float dWidth = newWidth - oldWidth;
                    width += dWidth;
                    width = Math.max(0, width);
                }
            });
            updateTextPadding();
            setPadding(Side.TOP, 0);
        }

        /**
         * Called when the tabs list changes.
         */
        public void onTabsChanged()
        {
            updateTextPadding();
        }

        /**
         * Updates the text padding.
         */
        private void updateTextPadding()
        {
            if (getParent() != null && getParent().getChildren().size() > 2)
            {
                if (isFirst())
                {
                    setPadding(Side.LEFT, 2);
                    setPadding(Side.RIGHT, 5);
                }
                else if (isLast())
                {
                    setPadding(Side.LEFT, 5);
                    setPadding(Side.RIGHT, 2);
                }
                else
                {
                    setPadding(Side.LEFT, 5);
                    setPadding(Side.RIGHT, 5);
                }
            }
            else
            {
                setPadding(Side.LEFT, 5);
                setPadding(Side.RIGHT, 5);
            }
        }

        /**
         * @return whether the tab is currently selected.
         */
        private boolean isSelected()
        {
            return this == selectedTab;
        }

        /**
         * @return whether the tab is the first tab.
         */
        private boolean isFirst()
        {
            return tabContainerControl.getChildren().indexOf(this) == 0;
        }

        /**
         * @return whether the tab is the last tab.
         */
        private boolean isLast()
        {
            return tabContainerControl.getChildren().indexOf(this) == tabContainerControl.getChildren().size() - 1;
        }
    }
}