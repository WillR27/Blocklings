package com.willr27.blocklings.client.gui.controls.common;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.*;
import com.willr27.blocklings.util.event.Event;
import com.willr27.blocklings.util.event.EventHandler;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A control used to select a single option from a list.
 */
@OnlyIn(Dist.CLIENT)
public class DropdownControl extends Control
{
    /**
     * Whether the dropdown is open.
     */
    private boolean isOpen = false;

    /**
     * The event handler called when the dropdown selection changes.
     */
    @Nonnull
    public final EventHandler<DropDownSelectionChangedEvent> onDropDownSelectionChanged = new EventHandler<>();

    /**
     * The currently selected item.
     */
    @Nonnull
    private final SelectedItemControl selectedItemControl;

    /**
     * The list of items in the dropdown.
     */
    @Nonnull
    private final List<ItemControl> itemControls = new ArrayList<>();

    /**
     * @param parent the parent control.
     * @param x the x position.
     * @param y the y position.
     * @param width the width of the control.
     */
    public DropdownControl(@Nonnull IControl parent, int x, int y, int width)
    {
        super(parent, x, y, width, GuiTextures.RAISED_BAR.height);
        this.selectedItemControl = new SelectedItemControl(this, width);
    }

    @Override
    public void preRender(int mouseX, int mouseY, float partialTicks)
    {
        height = selectedItemControl.height;

        if (isOpen)
        {
            for (ItemControl item : itemControls)
            {
                height += item.height;
            }
        }

        for (int i = 0; i < itemControls.size(); i++)
        {
            ItemControl itemControl = itemControls.get(i);

            itemControl.setY(selectedItemControl.height + (i * itemControl.height));
        }
    }

    @Override
    public void globalMouseClicked(@Nonnull MouseButtonEvent e)
    {
        if (!isMouseOver(e.mouseX, e.mouseY) && (getScrollbarY() == null || !getScrollbarY().isMouseOver(e.mouseX, e.mouseY)))
        {
            if (isOpen)
            {
                close();
            }
        }
    }

    /**
     * Opens the dropdown.
     */
    private void open()
    {
        isOpen = true;
    }

    /**
     * Closes the dropdown.
     */
    private void close()
    {
        isOpen = false;
    }

    /**
     * Adds all the given items to the dropdown.
     *
     * @param items the items to add.
     */
    public void addItems(@Nonnull List<Item> items)
    {
        for (Item item : items)
        {
            addItem(item);
        }
    }

    /**
     * Adds the given item to the end of the dropdown.
     *
     * @param item the item to add.
     */
    public void addItem(@Nonnull Item item)
    {
        if (itemControls.stream().anyMatch(dropdownItemControl -> dropdownItemControl.item.equals(item)))
        {
            return;
        }

        itemControls.add(new ItemControl(this, width, item));
    }

    /**
     * Removes the item from the dropdown.
     *
     * @param item the item to remove.
     */
    public void removeItem(@Nonnull Object item)
    {
        List<ItemControl> itemsToRemove = itemControls.stream().filter(dropdownItemControl -> dropdownItemControl.item.equals(item)).collect(Collectors.toList());

       if (selectedItemControl.item != null && selectedItemControl.item.equals(item))
       {
           onDropDownSelectionChanged.handle(new DropDownSelectionChangedEvent(selectedItemControl.item, selectedItemControl.item = null));
       }

        itemsToRemove.forEach(this::removeChild);
        itemsToRemove.forEach(itemControls::remove);
    }

    /**
     * Sets the currently selected item.
     */
    public void setSelectedItem(@Nullable Item item)
    {
        if (item != null && itemControls.stream().noneMatch(dropdownItemControl -> dropdownItemControl.item.equals(item)))
        {
            addItem(item);
        }

        if (!Objects.equals(selectedItemControl.item, item))
        {
            onDropDownSelectionChanged.handle(new DropDownSelectionChangedEvent(selectedItemControl.item, selectedItemControl.item = item));
        }
    }

    /**
     * A control used to represent the selected item in a dropdown.
     */
    public static class SelectedItemControl extends Control
    {
        /**
         * The parent dropdown control.
         */
        @Nonnull
        private final DropdownControl dropdownControl;

        /**
         * The start texture for an item in the dropdown list.
         */
        @Nonnull
        private final GuiTexture itemStartTexture;

        /**
         * The end texture for an item in the dropdown list.
         */
        @Nonnull
        private final GuiTexture itemEndTexture;

        /**
         * The underlying object that is selected.
         */
        @Nullable
        public Item item;

        /**
         * @param dropdownControl the dropdown control.
         * @param width the width of the control.
         */
        public SelectedItemControl(@Nonnull DropdownControl dropdownControl, int width)
        {
            super(dropdownControl, 0, 0, width, GuiTextures.RAISED_BAR.height);
            this.dropdownControl = dropdownControl;
            this.itemStartTexture = GuiTextures.RAISED_BAR.width(width - GuiTextures.RAISED_BAR_END.width);
            this.itemEndTexture = GuiTextures.RAISED_BAR_END;
        }

        @Override
        public void controlHoverStart(@Nonnull MouseEvent e)
        {
            setBackgroundColour(0.85f, 0.85f, 0.85f);

            e.setIsHandled(true);
        }

        @Override
        public void controlHoverStop(@Nonnull MouseEvent e)
        {
            setBackgroundColour(1.0f, 1.0f, 1.0f);

            e.setIsHandled(true);
        }

        @Override
        public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
        {
            getBackgroundColour().apply();

            renderTexture(matrixStack, itemStartTexture);
            renderTexture(matrixStack, itemStartTexture.width, 0, itemEndTexture);

            resetRenderColour();

            if (dropdownControl.isOpen)
            {
                renderTexture(matrixStack, itemStartTexture.width - GuiTextures.DROPDOWN_UP_ARROW.width - itemEndTexture.width - 1, itemStartTexture.height / 2 - GuiTextures.DROPDOWN_UP_ARROW.height / 2 - 1, GuiTextures.DROPDOWN_UP_ARROW);
            }
            else
            {
                renderTexture(matrixStack, itemStartTexture.width - GuiTextures.DROPDOWN_DOWN_ARROW.width - itemEndTexture.width - 1, itemStartTexture.height / 2 - GuiTextures.DROPDOWN_DOWN_ARROW.height / 2, GuiTextures.DROPDOWN_DOWN_ARROW);
            }

            int textOffset = 0;

            if (item != null && item.iconTexture != null)
            {
                renderTexture(matrixStack, 0, 0, item.iconTexture);

                textOffset += item.iconTexture.width - 3;
            }

            if (item != null)
            {
                renderText(matrixStack, GuiUtil.trimWithEllipses(font, item.toString(), width - 23 - textOffset), 5 + textOffset, 6, false, 0xffffffff);
            }
        }

        @Override
        public void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
        {
            if (item.tooltip != null)
            {
                screen.renderTooltip(matrixStack, item.tooltip, mouseX, mouseY);
            }
            else
            {
                screen.renderTooltip(matrixStack, new StringTextComponent(item.toString()), mouseX, mouseY);
            }
        }

        @Override
        public void controlMouseClicked(@Nonnull MouseButtonEvent e)
        {
            if (!dropdownControl.isOpen)
            {
                dropdownControl.open();
            }
            else
            {
                dropdownControl.close();
            }

            e.setIsHandled(true);
        }
    }

    /**
     * A control used to represent a single item in a dropdown.
     */
    public static class ItemControl extends Control
    {
        /**
         * The parent dropdown control.
         */
        @Nonnull
        private final DropdownControl dropdownControl;

        /**
         * The start texture for an item in the dropdown list.
         */
        @Nonnull
        private final GuiTexture itemStartTexture;

        /**
         * The end texture for an item in the dropdown list.
         */
        @Nonnull
        private final GuiTexture itemEndTexture;

        /**
         * The underlying object the
         */
        @Nonnull
        private final Item item;

        /**
         * @param dropdownControl the dropdown control.
         * @param width the width of the control.
         * @param item the underlying item.
         */
        public ItemControl(@Nonnull DropdownControl dropdownControl, int width, @Nonnull Item item)
        {
            super(dropdownControl, 0, 0, width, GuiTextures.FLAT_BAR.height - 1);
            this.dropdownControl = dropdownControl;
            this.itemStartTexture = GuiTextures.FLAT_BAR.width(width - GuiTextures.FLAT_BAR_END.width).shift(0, 1);
            this.itemEndTexture = GuiTextures.FLAT_BAR_END.shift(0, 1);
            this.item = item;
        }

        @Override
        public void controlHoverStart(@Nonnull MouseEvent e)
        {
            setBackgroundColour(0.85f, 0.85f, 0.85f);

            e.setIsHandled(true);
        }

        @Override
        public void controlHoverStop(@Nonnull MouseEvent e)
        {
            setBackgroundColour(1.0f, 1.0f, 1.0f);

            e.setIsHandled(true);
        }

        @Override
        public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
        {
            getBackgroundColour().apply();

            renderTexture(matrixStack, itemStartTexture);
            renderTexture(matrixStack, itemStartTexture.width, 0, itemEndTexture);

            resetRenderColour();

            int textOffset = 0;

            if (item.iconTexture != null)
            {
                renderTexture(matrixStack, 0, -1, item.iconTexture);

                textOffset += item.iconTexture.width - 3;
            }

            renderShadowedText(matrixStack, GuiUtil.trimWithEllipses(font, item.toString(), width - 11 - textOffset), 5 + textOffset, 6, false, 0xffffffff);
        }

        @Override
        public void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
        {
            if (item.tooltip != null)
            {
                screen.renderTooltip(matrixStack, item.tooltip, mouseX, mouseY);
            }
            else
            {
                screen.renderTooltip(matrixStack, new StringTextComponent(item.toString()), mouseX, mouseY);
            }
        }

        @Override
        public void controlMouseClicked(@Nonnull MouseButtonEvent e)
        {
            dropdownControl.setSelectedItem(item);
            dropdownControl.close();

            e.setIsHandled(true);
        }
    }

    /**
     * Represents an item in a dropdown list.
     */
    public static class Item
    {
        /**
         * The underlying object.
         */
        @Nonnull
        public Object item;

        /**
         * The optional item texture.
         */
        @Nullable
        public GuiTexture iconTexture;

        /**
         * The optional item tooltip.
         * Defaults to the item's toString() value if null.
         */
        @Nullable
        public List<IReorderingProcessor> tooltip;

        /**
         * @param item the underlying object.
         * @param iconTexture the optional item texture.
         * @param tooltip the optional item tooltip.
         */
        public Item(@Nonnull Object item, @Nullable GuiTexture iconTexture, @Nullable List<IReorderingProcessor> tooltip)
        {
            this.item = item;
            this.iconTexture = iconTexture;
            this.tooltip = tooltip;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof Item)
            {
                return item.equals(((Item) obj).item);
            }

            return super.equals(obj);
        }

        @Override
        public String toString()
        {
            return item.toString();
        }
    }

    /**
     * An event used when the dropdown selection is changed.
     */
    public static class DropDownSelectionChangedEvent extends Event
    {
        /**
         * The previous selection.
         */
        @Nullable
        public final Item previousSelection;

        /**
         * The new selection.
         */
        @Nullable
        public final Item newSelection;

        /**
         * @param previousSelection the previous selection.
         * @param newSelection the new selection.
         */
        public DropDownSelectionChangedEvent(@Nullable Item previousSelection, @Nullable Item newSelection)
        {
            this.previousSelection = previousSelection;
            this.newSelection = newSelection;
        }
    }
}
