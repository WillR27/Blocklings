package com.willr27.blocklings.client.gui.control.controls.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.ItemControl;
import com.willr27.blocklings.client.gui.control.controls.TextFieldControl;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.GridPanel;
import com.willr27.blocklings.client.gui.control.event.events.FocusChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.TextChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.KeyPressedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.properties.Flow;
import com.willr27.blocklings.client.gui.properties.GridDefinition;
import com.willr27.blocklings.client.gui.properties.Visibility;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * A control used to select items.
 */
@OnlyIn(Dist.CLIENT)
public class ItemsSelectionControl extends Control
{
    /**
     * The control used to search for items.
     */
    @Nonnull
    private final ItemSearchControl itemSearchControl;

    /**
     * The panel containing the items.
     */
    @Nonnull
    private final FlowPanel itemsPanel;

    /**
     * The background used to add items.
     */
    @Nonnull
    private final TexturedControl addBackground;

    /**
     */
    public ItemsSelectionControl()
    {
        super();

        setWidthPercentage(1.0);
        setFitHeightToContent(true);
        setClipContentsToBounds(false);

        itemsPanel = new FlowPanel();
        itemsPanel.setParent(this);
        itemsPanel.setWidthPercentage(1.0);
        itemsPanel.setFitHeightToContent(true);
        itemsPanel.setFlow(Flow.TOP_LEFT_LEFT_TO_RIGHT);
        itemsPanel.setHorizontalSpacing(2.0);
        itemsPanel.setVerticalSpacing(2.0);
        itemsPanel.setClipContentsToBounds(false);

        addBackground = new TexturedControl(Textures.Tasks.TASK_ICON_BACKGROUND_RAISED, Textures.Tasks.TASK_ICON_BACKGROUND_PRESSED)
        {
            @Override
            public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
            {
                renderTooltip(matrixStack, mouseX, mouseY, new BlocklingsTranslationTextComponent("config.add_item"));
            }

            @Override
            protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
            {
                if (isPressed())
                {
                    itemSearchControl.setVisibility(Visibility.VISIBLE);
                    itemSearchControl.setFocused(true);
                    setVisibility(Visibility.COLLAPSED);

                    e.setIsHandled(true);
                }
            }
        };
        addBackground.setParent(itemsPanel);
        addBackground.setReorderable(false);
        addBackground.setChildrenInteractive(false);

        TexturedControl addIcon = new TexturedControl(Textures.Common.PLUS_ICON);
        addIcon.setParent(addBackground);
        addIcon.setHorizontalAlignment(0.5);
        addIcon.setVerticalAlignment(0.5);

        itemSearchControl = new ItemSearchControl()
        {
            @Override
            protected void measureSelf(double availableWidth, double availableHeight)
            {
                super.measureSelf(availableWidth, availableHeight);
            }

            @Override
            public void onKeyPressed(@Nonnull KeyPressedEvent e)
            {
                openItemSearch();

                e.setIsHandled(true);
            }
        };
        itemsPanel.insertChildFirst(itemSearchControl);
        itemSearchControl.setVisibility(Visibility.COLLAPSED);

        screenEventBus.subscribe((BaseControl c, FocusChangedEvent e) ->
        {
            if (!c.isFocused() && itemSearchControl.isThisOrDescendant(c) && !itemSearchControl.isThisOrDescendant(getFocusedControl()))
            {
                closeItemSearch();
            }
        });
    }

    /**
     * Opens the item search control.
     */
    private void openItemSearch()
    {
        itemSearchControl.setVisibility(Visibility.VISIBLE);
        itemSearchControl.setFocused(true);
        addBackground.setVisibility(Visibility.COLLAPSED);
    }

    /**
     * Closes the item search control.
     */
    private void closeItemSearch()
    {
        itemSearchControl.setVisibility(Visibility.COLLAPSED);
        addBackground.setVisibility(Visibility.VISIBLE);
    }

    /**
     * Sets the items to display.
     *
     * @param items the items to display.
     */
    public void setItems(@Nonnull List<Item> items)
    {
        itemsPanel.clearChildren();

        for (Item item : items)
        {
            addItem(item);
        }
    }

    /**
     * Adds an item to the list.
     *
     * @param item the item to add.
     */
    private void addItem(@Nonnull Item item)
    {
        ItemControl itemControl = new ItemControl();
        itemControl.setItem(item);
        itemControl.setWidthPercentage(1.0);
        itemControl.setHeightPercentage(1.0);
        itemControl.setItemScale(0.5f);

        Control itemOverlay = new Control();
        itemOverlay.setWidthPercentage(1.0);
        itemOverlay.setHeightPercentage(1.0);

        TexturedControl crossIcon = new TexturedControl(Textures.Common.CROSS_ICON);
        crossIcon.setVisibility(Visibility.INVISIBLE);

        TexturedControl itemBackground = new TexturedControl(Textures.Tasks.TASK_ICON_BACKGROUND_RAISED, Textures.Tasks.TASK_ICON_BACKGROUND_PRESSED)
        {
            @Override
            public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
            {
                renderTooltip(matrixStack, mouseX, mouseY, itemControl.getItemStack().getHoverName());
            }

            @Override
            public void onHoverEnter()
            {
                crossIcon.setVisibility(Visibility.VISIBLE);
                crossIcon.setRenderZ(15.0);
                itemOverlay.setBackgroundColour(0x44000000);
                itemOverlay.setRenderZ(15.0);
            }

            @Override
            public void onHoverExit()
            {
                crossIcon.setVisibility(Visibility.INVISIBLE);
                crossIcon.setRenderZ(0.0);
                itemOverlay.setBackgroundColour(0x00000000);
                itemOverlay.setRenderZ(0.0);
            }

            @Override
            protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
            {
                if (isPressed())
                {
                    setParent(null);

                    e.setIsHandled(true);
                }
            }

            @Nullable
            @Override
            public BaseControl getScrollFromDragControl()
            {
                return ItemsSelectionControl.this.getParent();
            }
        };
        itemBackground.setDraggableX(true);
        itemBackground.setDraggableY(true);
        itemBackground.setShouldSnapToScreenCoords(true);
        itemBackground.setChildrenInteractive(false);
        itemBackground.setHorizontalContentAlignment(0.5);
        itemBackground.setVerticalContentAlignment(0.5);

        itemBackground.setParent(itemsPanel);
        itemControl.setParent(itemBackground);
        crossIcon.setParent(itemBackground);
        itemOverlay.setParent(itemControl);
    }

    /**
     * A control used to search for and select an item.
     */
    private class ItemSearchControl extends GridPanel
    {
        /**
         * The panel containing the list of items.
         */
        @Nonnull
        private final FlowPanel itemsContainer;

        /**
         * The search field.
         */
        @Nonnull
        private final TextFieldControl searchField;

        /**
         */
        public ItemSearchControl()
        {
            super();

            setWidthPercentage(1.0);
            setFitHeightToContent(true);

            addRowDefinition(GridDefinition.AUTO, 1.0);
            addRowDefinition(GridDefinition.AUTO, 1.0);
            addColumnDefinition(GridDefinition.RATIO, 1.0);

            GridPanel searchPanel = new GridPanel();
            addChild(searchPanel, 0, 0);
            searchPanel.setWidthPercentage(1.0);
            searchPanel.setFitHeightToContent(true);
            searchPanel.addRowDefinition(GridDefinition.RATIO, 1.0);
            searchPanel.addColumnDefinition(GridDefinition.AUTO, 1.0);
            searchPanel.addColumnDefinition(GridDefinition.RATIO, 1.0);

            TexturedControl searchBackground = new TexturedControl(Textures.Tasks.TASK_ICON_BACKGROUND_RAISED.dWidth(-1));
            searchPanel.addChild(searchBackground, 0, 0);

            TexturedControl searchIcon = new TexturedControl(Textures.Common.SEARCH_ICON);
            searchIcon.setParent(searchBackground);
            searchIcon.setVerticalAlignment(0.5);
            searchIcon.setMarginLeft(3.0);

            searchField = new TextFieldControl();
            searchPanel.addChild(searchField, 0, 1);
            searchField.setWidthPercentage(1.0);
            searchField.setBackgroundColour(0xff191919);
            searchField.setBorderColour(0xff373737);
            searchField.setBorderFocusedColour(searchField.getBorderColour());

            Control itemsContainerContainer = new Control();
            addChild(itemsContainerContainer, 1, 0);
            itemsContainerContainer.setWidthPercentage(1.0);
            itemsContainerContainer.setFitHeightToContent(true);
            itemsContainerContainer.setBackgroundColour(searchField.getBorderColour());

            itemsContainer = new FlowPanel();
            itemsContainer.setParent(itemsContainerContainer);
            itemsContainer.setWidthPercentage(1.0);
            itemsContainer.setFitHeightToContent(true);
            itemsContainer.setBackgroundColour(searchField.getBackgroundColour());
            itemsContainer.setMargins(1.0, 0.0, 1.0 ,1.0);

            searchField.eventBus.subscribe((BaseControl c, TextChangedEvent e) ->
            {
                updateItems(e.newText);
            });
        }

        @Override
        public void onFocused()
        {
            searchField.setFocused(true);
        }

        @Override
        public void setVisibility(@Nonnull Visibility visibility)
        {
            super.setVisibility(visibility);

            searchField.setText("");
        }

        /**
         * Updates the list of items.
         */
        private void updateItems(@Nonnull String searchText)
        {
            itemsContainer.clearChildren();

            int i = 0;

            for (Item item : Registry.ITEM)
            {
                if (new ItemStack(item).isEmpty() || !new ItemStack(item).getHoverName().getString().toLowerCase().contains(searchText.toLowerCase()) || itemsPanel.getChildren().stream().anyMatch(b -> b.getChildren().stream().anyMatch(c -> c instanceof ItemControl && ((ItemControl) c).getItem() == item)))
                {
                    continue;
                }

                ItemControl itemControl = new ItemControl()
                {
                    @Override
                    public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
                    {
                        renderTooltip(matrixStack, mouseX, mouseY, getItemStack().getHoverName());
                    }

                    @Override
                    public void onHoverEnter()
                    {
                        setForegroundColour(0x44ffffff);
                    }

                    @Override
                    public void onHoverExit()
                    {
                        setForegroundColour(0x00000000);
                    }

                    @Override
                    protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
                    {
                        if (isPressed())
                        {
                            addItem(item);
                            closeItemSearch();

                            e.setIsHandled(true);
                        }
                    }
                };
                itemControl.setParent(itemsContainer);
                itemControl.setWidth(16.0);
                itemControl.setHeight(16.0);
                itemControl.setItemScale(1.0f);
                itemControl.setItem(item);

                i++;

                if (i > 100)
                {
                    break;
                }
            }
        }
    }
}
