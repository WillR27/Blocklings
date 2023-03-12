package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.panels.GridPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.StackPanel;
import com.willr27.blocklings.client.gui.control.event.events.SelectionChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.TryHoverEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseClickedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.properties.Direction;
import com.willr27.blocklings.client.gui.properties.GridDefinition;
import com.willr27.blocklings.client.gui.properties.Visibility;
import com.willr27.blocklings.client.gui.texture.Texture;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A control that allows the user to select an item from a list.
 */
@OnlyIn(Dist.CLIENT)
public class ComboBoxControl extends StackPanel
{
    /**
     * The ordered list of items in the combo box.
     */
    @Nonnull
    private final List<Item> items = new ArrayList<>();

    /**
     * The currently selected item control.
     */
    @Nonnull
    private ItemControl selectedItemControl;

    /**
     * The dropdown part of the combo box.
     */
    @Nonnull
    private final StackPanel itemsStackPanel;

    /**
     */
    public ComboBoxControl()
    {
        super();

        setFitHeightToContent(true);
        setDirection(Direction.TOP_TO_BOTTOM);

        itemsStackPanel = new StackPanel();
        itemsStackPanel.setParent(this);
        itemsStackPanel.setWidthPercentage(1.0);
        itemsStackPanel.setFitHeightToContent(true);

        selectedItemControl = new ItemControl(null);
        setSelectedItemControl(selectedItemControl, false);

        collapse();
    }

    /**
     * Adds an item to the combo box.
     *
     * @param item the item to add.
     */
    public void addItem(@Nonnull Item item)
    {
        if (items.contains(item))
        {
            return;
        }

        ItemControl itemControl = new ItemControl(item);

        items.add(item);
        itemsStackPanel.addChild(itemControl);

        if (selectedItemControl.item == null)
        {
            setSelectedItemControl(itemControl, false);
        }
    }

    /**
     * @return the control for the given item.
     */
    @Nullable
    public ItemControl getItemControl(@Nonnull Item item)
    {
        for (BaseControl control : itemsStackPanel.getChildren())
        {
            if (control instanceof ItemControl)
            {
                ItemControl itemControl = (ItemControl) control;
                if (itemControl.item == item) return itemControl;
            }
        }

        return getSelectedItemControl().item == item ? getSelectedItemControl() : null;
    }

    /**
     * @return whether the combo box is expanded.
     */
    public boolean isExpanded()
    {
        return itemsStackPanel.getVisibility() == Visibility.VISIBLE;
    }

    /**
     * Toggles the combo box.
     */
    private void toggle()
    {
        if (isExpanded()) collapse();
        else expand();
    }

    /**
     * Expands the combo box.
     */
    private void expand()
    {
        itemsStackPanel.setVisibility(Visibility.VISIBLE);
        selectedItemControl.onExpanded();
        itemsStackPanel.getChildren().forEach(control -> ((ItemControl)control).onExpanded());
    }

    /**
     * Collapses the combo box.
     */
    private void collapse()
    {
        itemsStackPanel.setVisibility(Visibility.COLLAPSED);
        selectedItemControl.onCollapsed();
        itemsStackPanel.getChildren().forEach(control -> ((ItemControl)control).onCollapsed());
    }

    /**
     * @return the currently selected item control.
     */
    @Nonnull
    public ItemControl getSelectedItemControl()
    {
        return selectedItemControl;
    }

    /**
     * Sets the currently selected item control.
     *
     * @param control the item control to select.
     */
    public void setSelectedItemControl(@Nonnull ItemControl control, boolean postEvent)
    {
        ItemControl previousItemControl = selectedItemControl;
        previousItemControl.onUnselected();

        if (previousItemControl.item != null)
        {
            int indexOfPreviousItem = items.indexOf(previousItemControl.item);

            if (indexOfPreviousItem == 0)
            {
                itemsStackPanel.insertChildFirst(previousItemControl);
            }
            else if (indexOfPreviousItem == items.size() - 1)
            {
                itemsStackPanel.insertChildLast(previousItemControl);
            }
            else
            {
                Item itemBefore = items.get(indexOfPreviousItem - 1);

                for (BaseControl child : itemsStackPanel.getChildren())
                {
                    if (child instanceof ItemControl)
                    {
                        ItemControl itemControl = (ItemControl) child;
                        if (itemControl.item == itemBefore)
                        {
                            itemsStackPanel.insertChildAfter(previousItemControl, itemControl);
                            break;
                        }
                    }
                }
            }
        }
        else
        {
            previousItemControl.setParent(null);
        }

        selectedItemControl = control;
        selectedItemControl.onSelected();
        insertChildFirst(selectedItemControl);

        if (postEvent)
        {
            eventBus.post(this, new SelectionChangedEvent(previousItemControl.item, selectedItemControl.item));
        }
    }

    /**
     * Sets the currently selected item.
     *
     * @param item the item to select.
     */
    public void setSelectedItem(@Nonnull Item item)
    {
        ItemControl itemControl = getItemControl(item);

        if (itemControl == null)
        {
            addItem(item);
        }

        setSelectedItemControl(getItemControl(item), true);
    }

    /**
     * A control for an item in a combo box.
     */
    private class ItemControl extends Control
    {
        /**
         * The associated item.
         */
        @Nullable
        private final Item item;

        /**
         * The background control.
         */
        @Nonnull
        private final TexturedControl backgroundControl;

        /**
         * The grid containing the icon, name and arrow.
         */
        @Nonnull
        private final GridPanel gridPanel;

        /**
         * The arrow control.
         */
        @Nonnull
        private final TexturedControl arrowControl;

        /**
         * @param item the associated item.
         */
        public ItemControl(@Nullable Item item)
        {
            super();
            this.item = item;

            setWidthPercentage(1.0);
            setFitHeightToContent(true);

            backgroundControl = new TexturedControl(Textures.Common.DropDown.UNSELECTED_BACKGROUND)
            {
                @Override
                public void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
                {
                    if (getParent() == getHoveredControl())
                    {
                        RenderSystem.color3f(0.7f, 0.9f, 1.0f);
                    }

                    int borderSize = Textures.Common.DropDown.BORDER_SIZE;
                    renderTextureAsBackground(matrixStack, getBackgroundTexture().width((int) (getWidth() - borderSize)));
                    renderTextureAsBackground(matrixStack, getBackgroundTexture().width(borderSize).dx(getBackgroundTexture().width - borderSize), getWidth() - borderSize, 0);

                    RenderSystem.color3f(1.0f, 1.0f, 1.0f);
                }

                @Override
                protected void onMouseClicked(@Nonnull MouseClickedEvent e)
                {

                }

                @Override
                protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
                {

                }
            };
            backgroundControl.setParent(this);
            backgroundControl.setWidthPercentage(1.0);
            backgroundControl.setHoverable(false);

            gridPanel = new GridPanel();
            gridPanel.addRowDefinition(GridDefinition.RATIO, 1.0);
            gridPanel.addColumnDefinition(GridDefinition.AUTO, 1.0);
            gridPanel.addColumnDefinition(GridDefinition.RATIO, 1.0);
            gridPanel.addColumnDefinition(GridDefinition.AUTO, 1.0);
            gridPanel.setParent(this);
            gridPanel.setWidthPercentage(1.0);
            gridPanel.setFitHeightToContent(true);
            gridPanel.setVerticalAlignment(0.0);
            gridPanel.setInteractive(false);

            if (item != null && item.iconTexture != null)
            {
                TexturedControl iconControl = new TexturedControl(item.iconTexture);
                gridPanel.addChild(iconControl, 0, 0);
                iconControl.setVerticalAlignment(0.5);
                iconControl.setMarginLeft(1.0);
            }

            TextBlockControl nameControl = new TextBlockControl();
            gridPanel.addChild(nameControl, 0, 1);
            if (item != null) nameControl.setText(item.name);
            nameControl.setVerticalAlignment(0.5);
            nameControl.setMarginLeft(4.0);
            nameControl.setMarginRight(3.0);

            arrowControl = new TexturedControl(Textures.Common.DropDown.DOWN_ARROW);
            gridPanel.addChild(arrowControl, 0, 2);
            arrowControl.setVerticalAlignment(0.5);
            arrowControl.setMarginRight(5.0);
            arrowControl.setVisibility(Visibility.COLLAPSED);
        }

        @Override
        public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
        {
            if (item.tooltip != null)
            {
                renderTooltip(matrixStack, mouseX, mouseY, item.tooltip);
            }
        }

        @Override
        protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
        {
            if (isPressed())
            {
                if (isSelected())
                {
                    toggle();
                }
                else
                {
                    setSelectedItemControl(this, true);
                    collapse();
                }
            }
        }

        @Override
        public void onUnfocused()
        {
            if (!getParent().isDescendant(getFocusedControl()) && isExpanded())
            {
                collapse();
            }
        }

        /**
         * Called when the item is selected.
         */
        private void onSelected()
        {
            backgroundControl.setBackgroundTexture(Textures.Common.DropDown.SELECTED_BACKGROUND);
            gridPanel.setVerticalAlignment(0.5);
            arrowControl.setVisibility(Visibility.VISIBLE);
        }

        /**
         * Called when the item is unselected.
         */
        private void onUnselected()
        {
            backgroundControl.setBackgroundTexture(Textures.Common.DropDown.UNSELECTED_BACKGROUND);
            gridPanel.setVerticalAlignment(0.0);
            arrowControl.setVisibility(Visibility.COLLAPSED);
        }

        /**
         * Called when the item is expanded.
         */
        private void onExpanded()
        {
            arrowControl.setBackgroundTexture(Textures.Common.DropDown.UP_ARROW);
        }

        /**
         * Called when the item is collapsed.
         */
        private void onCollapsed()
        {
            arrowControl.setBackgroundTexture(Textures.Common.DropDown.DOWN_ARROW);
        }

        /**
         * @return whether this item is selected.
         */
        private boolean isSelected()
        {
            return selectedItemControl == this;
        }
    }

    /**
     * An item in a combo box.
     */
    public static class Item
    {
        /**
         * The name of the item.
         */
        @Nonnull
        public final ITextComponent name;

        /**
         * The value of the item.
         */
        @Nonnull
        public final Object value;

        /**
         * The optional icon texture of the item.
         */
        @Nullable
        public final Texture iconTexture;

        /**
         * The tooltip of the item.
         */
        @Nullable
        public final List<IReorderingProcessor> tooltip;

        /**
         * @param name the name of the item.
         * @param value the value of the item.
         */
        public Item(@Nonnull ITextComponent name, @Nonnull Object value)
        {
            this(name, value, null, null);
        }

        /**
         * @param name the name of the item.
         * @param value the value of the item.
         * @param iconTexture the optional icon texture of the item.
         */
        public Item(@Nonnull ITextComponent name, @Nonnull Object value, @Nullable Texture iconTexture)
        {
            this(name, value, iconTexture, null);
        }

        /**
         * @param name the name of the item.
         * @param value the value of the item.
         * @param tooltip the optional tooltip of the item.
         */
        public Item(@Nonnull ITextComponent name, @Nonnull Object value, @Nullable List<IReorderingProcessor> tooltip)
        {
            this(name, value, null, tooltip);
        }

        /**
         * @param tooltip the tooltip of the item.
         * @param value the value of the item.
         * @param iconTexture the optional icon texture of the item.
         * @param tooltip the optional tooltip of the item.
         */
        public Item(@Nonnull ITextComponent name, @Nonnull Object value, @Nullable Texture iconTexture, @Nullable List<IReorderingProcessor> tooltip)
        {
            this.name = name;
            this.value = value;
            this.iconTexture = iconTexture;
            this.tooltip = tooltip;
        }
    }
}
