package com.willr27.blocklings.client.gui.control.controls.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.ItemControl;
import com.willr27.blocklings.client.gui.control.controls.TextFieldControl;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.GridPanel;
import com.willr27.blocklings.client.gui.control.event.events.*;
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
import java.util.Set;

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
        itemsPanel.eventBus.subscribe((BaseControl c, ReorderEvent e) ->
        {
            ItemControl itemControl = (ItemControl) e.draggedControl.getChildren().get(0);
            ItemControl closestItemControl = (ItemControl) e.closestControl.getChildren().get(0);

            eventBus.post(this, new ItemMovedEvent(itemControl.getItem(), closestItemControl.getItem(), e.insertBefore));
        });

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
            public void onKeyPressed(@Nonnull KeyPressedEvent e)
            {
                openItemSearch();

                e.setIsHandled(true);
            }
        };
        itemsPanel.insertChildFirst(itemSearchControl);
        itemSearchControl.setVisibility(Visibility.COLLAPSED);
        itemSearchControl.setFilter((Item item) -> itemsPanel.getChildren().stream().noneMatch(b -> b.getChildren().stream().anyMatch(c -> c instanceof ItemControl && ((ItemControl) c).getItem() == item)));
        itemSearchControl.eventBus.subscribe((BaseControl c, ItemAddedEvent e) ->
        {
            addItem(e.item);
            closeItemSearch();

            eventBus.post(this, new ItemAddedEvent(e.item));
        });

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
        for (BaseControl child : itemsPanel.getChildren())
        {
            if (child instanceof ItemControl)
            {
                itemsPanel.removeChild(child);
            }
        }

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
        itemControl.setItemScale(0.8f);

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

                    ItemsSelectionControl.this.eventBus.post(ItemsSelectionControl.this, new ItemRemovedEvent(item));

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
}
