package com.willr27.blocklings.client.gui.control.controls.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.ItemControl;
import com.willr27.blocklings.client.gui.control.controls.TextBlockControl;
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
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
     * The too many results message.
     */
    @Nonnull
    private final TextBlockControl tooManyResultsMessage;

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
     * The maximum number of items to display. Any items that are an exact match for the search criteria
     * will also be displayed regardless of the number of items displayed.
     */
    private int maxItems = 256;

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

        tooManyResultsMessage = new TextBlockControl();
        tooManyResultsMessage.setWidthPercentage(1.0);
        tooManyResultsMessage.setText(new BlocklingsTranslationTextComponent("config.search.too_many_results"));
        tooManyResultsMessage.setShouldRenderShadow(false);
        tooManyResultsMessage.setPadding(2.0, 1.0 ,2.0 ,1.0);
        tooManyResultsMessage.setVerticalContentAlignment(0.5);

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
        tooManyResultsMessage.setParent(null);

        int itemCount = 0;
        int missingItems = 0;

        Iterable<Item> items = searchableItems == null ? Registry.ITEM : searchableItems;
        List<Item> itemsToAdd = new ArrayList<>();

        for (Item item : items)
        {
            ItemStack stack = new ItemStack(item);

            // Don't display empty items.
            if (stack.isEmpty())
            {
                continue;
            }

            // Don't display items that don't match the search criteria.
            if (!stack.getHoverName().getString().toLowerCase().contains(searchText.toLowerCase()))
            {
                continue;
            }

            // Don't display more items than the max unless they exactly match the search criteria.
            if (itemCount >= maxItems && !stack.getHoverName().getString().toLowerCase().equals(searchText.toLowerCase()))
            {
                missingItems++;

                continue;
            }

            // Don't display items that don't match the filter.
            if (!filter.test(item))
            {
                continue;
            }

            itemsToAdd.add(item);

            itemCount++;
        }

        for (Item item : itemsToAdd.stream().sorted((item1, item2) -> new ItemStack(item1).getHoverName().getString().compareToIgnoreCase(new ItemStack(item2).getHoverName().getString())).collect(Collectors.toList()))
        {
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
        }

        if (missingItems > 0)
        {
            itemsContainer.insertChildLast(tooManyResultsMessage);
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

    /**
     * @return the maximum number of items to display.
     */
    public int getMaxItems()
    {
        return maxItems;
    }

    /**
     * Sets the maximum number of items to display.
     *
     * @param maxItems the maximum number of items to display.
     */
    public void setMaxItems(int maxItems)
    {
        this.maxItems = maxItems;
    }
}
