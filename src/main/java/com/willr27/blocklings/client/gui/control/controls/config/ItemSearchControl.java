package com.willr27.blocklings.client.gui.control.controls.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.ItemControl;
import com.willr27.blocklings.client.gui.control.controls.TextFieldControl;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.GridPanel;
import com.willr27.blocklings.client.gui.control.event.events.ItemAddedEvent;
import com.willr27.blocklings.client.gui.control.event.events.TextChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.properties.GridDefinition;
import com.willr27.blocklings.client.gui.properties.Visibility;
import com.willr27.blocklings.client.gui.texture.Textures;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * A control used to search for and select an item.
 */
@OnlyIn(Dist.CLIENT)
class ItemSearchControl extends GridPanel
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
     * The list of items available to search from. If null, defaults to all items.
     */
    @Nullable
    private List<Item> searchableItems = null;

    /**
     * The filter used to filter the items searchable items.
     */
    @Nonnull
    private Predicate<Item> filter = item -> true;

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

        Iterable<Item> items = searchableItems == null ? Registry.ITEM : searchableItems;

        for (Item item : items)
        {
            if (new ItemStack(item).isEmpty() || !new ItemStack(item).getHoverName().getString().toLowerCase().contains(searchText.toLowerCase()) || !filter.test(item))
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
                        ItemSearchControl.this.eventBus.post(ItemSearchControl.this, new ItemAddedEvent(item));

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

    /**
     * @return the list of items available to search from.
     */
    @Nullable
    public List<Item> getSearchableItems()
    {
        return searchableItems;
    }

    /**
     * Sets the list of items available to search from.
     *
     * @param searchableItems the list of items available to search from.
     */
    public void setSearchableItems(@Nullable List<Item> searchableItems)
    {
        this.searchableItems = searchableItems;
    }

    /**
     * @return the filter used to filter the items searchable items.
     */
    @Nonnull
    public Predicate<Item> getFilter()
    {
        return filter;
    }

    /**
     * Sets the filter used to filter the items searchable items.
     *
     * @param filter the filter used to filter the items searchable items.
     */
    public void setFilter(@Nonnull Predicate<Item> filter)
    {
        this.filter = filter;
    }
}
