package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.GuiTextures;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.*;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.client.gui2.GuiTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Displays a list of selectable items.
 */
@OnlyIn(Dist.CLIENT)
public class DropDownControl extends FlowPanel
{
    /**
     * Whether the dropdown is expanded.
     */
    private boolean isExpanded = false;

    /**
     * Used as a placeholder for the selected control.
     */
    private final DummyControl dummyControl = new DummyControl();

    /**
     */
    public DropDownControl()
    {
        super();

        setFitToContentsY(true);
        setFlowDirection(Direction.TOP_TO_BOTTOM);
    }

    /**
     * Toggles whether the dropdown is expanded.
     */
    public void toggle()
    {
        if (isExpanded())
        {
            collapse();
        }
        else
        {
            expand();
        }
    }

    /**
     * Expands the dropdown.
     */
    public void expand()
    {
        getChildren().forEach(control -> control.setVisible(true));

        isExpanded = true;
    }

    /**
     * Collapses the dropdown.
     */
    public void collapse()
    {
        getChildren().forEach(control -> control.setVisible(false));

        if (!getChildren().isEmpty())
        {
            getChildren().get(0).setVisible(true);
        }

        isExpanded = false;
    }

    /**
     * @return whether the {@link Item} is in the dropdown.
     */
    public boolean containsItem(@Nonnull Item item)
    {
        return getChildren().stream().anyMatch(control ->
            {
                if (control instanceof ItemControl)
                {
                    return ((ItemControl) control).item == item;
                }

                return false;
            });
    }

    /**
     * @return the {@link ItemControl} with the given {@link Item}.
     */
    @Nonnull
    private ItemControl getItemControl(@Nonnull Item item)
    {
        return (ItemControl) getChildren().stream().filter(control ->
                {
                    if (control instanceof ItemControl)
                    {
                        return ((ItemControl) control).item == item;
                    }

                    return false;
                }).findFirst().get();
    }

    /**
     * Adds the given {@link Item} as an option in the dropdown.
     */
    public void addItem(@Nonnull Item item)
    {
        if (containsItem(item))
        {
            return;
        }

        ItemControl itemControl = new ItemControl(item);
        itemControl.setParent(this);

        if (getChildren().size() == 1)
        {
            itemControl.onSelected(false);

            // Add a dummy control, so we know the place of selected item.
            dummyControl.setParent(null);
            dummyControl.setParent(this);
        }
        else
        {
            itemControl.onUnselected(false);
            itemControl.setVisible(isExpanded());
        }
    }

    /**
     * @return the selected {@link ItemControl}.
     */
    @Nonnull
    private ItemControl getSelectedItemControl()
    {
        return (ItemControl) getChildren().get(0);
    }

    /**
     * Sets the selected {@link ItemControl}.
     */
    private void setSelectedItemControl(@Nonnull ItemControl itemControl)
    {
        ItemControl selectedItemControl = getSelectedItemControl();

        if (selectedItemControl == itemControl)
        {
            return;
        }

        insertOrMoveChildAfter(selectedItemControl, dummyControl);

        dummyControl.setParent(null);
        insertOrMoveChildAfter(dummyControl, itemControl);

        selectedItemControl.onUnselected(true);
        insertOrMoveChildFirst(itemControl);
        getSelectedItemControl().onSelected(true);
    }

    /**
     * @return the selected {@link Item}.
     */
    @Nonnull
    public Item getSelectedItem()
    {
        return getSelectedItemControl().item;
    }

    /**
     * Sets the selected {@link Item}.
     */
    public void setSelectedItem(@Nonnull Item item)
    {
        if (!containsItem(item))
        {
            addItem(item);
        }

        setSelectedItemControl(getItemControl(item));
    }

    /**
     * @return whether the dropdown is expanded.
     */
    public boolean isExpanded()
    {
        return isExpanded;
    }

    /**
     * Used as a placeholder for the selected item.
     */
    private class DummyControl extends Control
    {
        /**
         */
        public DummyControl()
        {
            super();

            setWidth(new Fill(1.0f));
            setHeight(0);
        }
    }

    /**
     * An item control.
     */
    private class ItemControl extends Control
    {
        /**
         * The associated item.
         */
        @Nonnull
        private final Item item;

        /**
         * The background texture.
         */
        @Nonnull
        private final TexturedControl backgroundControl;

        /**
         * The icon control.
         */
        @Nonnull
        private final TexturedControl iconControl;

        /**
         */
        public ItemControl(@Nonnull Item item)
        {
            super();
            this.item = item;

            setWidth(new Fill(1.0f));
            setFitToContentsY(true);

            iconControl = new TexturedControl(GuiTextures.Common.DropDown.UNSELECTED_BACKGROUND)
            {
                @Override
                public void onRenderBackground(@Nonnull RenderArgs renderArgs)
                {
                    renderTexture(renderArgs.matrixStack, 0, isSelected() ? 0 : -1, getTexture());
                }
            };
            iconControl.setInteractive(false);

            backgroundControl = new TexturedControl(GuiTextures.Common.DropDown.SELECTED_BACKGROUND)
            {
                @Override
                public void setHeight(float height)
                {
                    super.setHeight(height);
                }

                @Override
                public void onRenderBackground(@Nonnull RenderArgs renderArgs)
                {
                    if (getParent().isDescendant(getScreen().getHoveredControl()))
                    {
                        RenderSystem.color3f(0.7f, 0.9f, 1.0f);
                    }

                    int borderSize = GuiTextures.Common.DropDown.BORDER_SIZE;
                    renderTexture(renderArgs.matrixStack, getTexture().width((int) (getWidth() - borderSize)));
                    renderTexture(renderArgs.matrixStack, getWidth() - borderSize, 0, getTexture().width(borderSize).shift(getTexture().width - borderSize, 0));

                    RenderSystem.color3f(1.0f, 1.0f, 1.0f);
                }

                @Override
                protected void onMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
                {

                }

                @Override
                public void onUnfocused(@Nullable Control focusedControl)
                {
                    if (!getParent().getParent().isDescendant(focusedControl))
                    {
                        DropDownControl.this.collapse();
                    }
                }
            };
            backgroundControl.setParent(this);
            backgroundControl.setWidth(new Fill(1.0f));

            GridControl gridControl = new GridControl(new GridControl.GridDefinition()
                    .addCol(new GridControl.GridDefinition.Auto())
                    .addCol(new GridControl.GridDefinition.Fill(1.0f))
                    .addCol(new GridControl.GridDefinition.Auto())
                    .addRow(new GridControl.GridDefinition.Fill(1.0f)));
            gridControl.setParent(this);
            gridControl.setWidth(new Fill(1.0f));
            gridControl.setHeight(new Fill(1.0f));
            gridControl.addControl(iconControl, 0, 0);
            gridControl.setInteractive(false);

            TextBlockControl itemNameControl = new TextBlockControl()
            {
                @Override
                public void onTick()
                {
                    if (item.toString() != getText())
                    {
                        setText(item.toString());
                    }

                    if (item.getIconTexture() != null)
                    {
                        setPadding(Side.LEFT, 2);
                    }
                    else
                    {
                        setPadding(Side.LEFT, 6);
                    }
                }
            };
            gridControl.addControl(itemNameControl, 1, 0);
            itemNameControl.setWidth(new Fill(1.0f));
            itemNameControl.setHeight(new Fill(1.0f));
            itemNameControl.setPadding(6, 0, 4, 0);
            itemNameControl.setInteractive(false);
            itemNameControl.onTick();

            Control arrowControl = new Control()
            {
                @Override
                public void onTick()
                {
                    if (isSelected())
                    {
                       setWidth(GuiTextures.Common.DropDown.DOWN_ARROW.width + 5.0f);
                    }
                    else
                    {
                        setWidth(0.0f);
                    }
                }

                @Override
                public void onRenderBackground(@Nonnull RenderArgs renderArgs)
                {
                    renderTexture(renderArgs.matrixStack, isExpanded() ? GuiTextures.Common.DropDown.UP_ARROW : GuiTextures.Common.DropDown.DOWN_ARROW);
                }
            };
            gridControl.addControl(arrowControl, 2, 0);
            arrowControl.setHeight(GuiTextures.Common.DropDown.DOWN_ARROW.height);
            arrowControl.setAlignmentY(new Alignment(0.5f));
            arrowControl.setInteractive(false);
        }

        @Override
        public void onTick()
        {
            if (item.getIconTexture() != iconControl.getTexture())
            {
                if (item.getIconTexture() != null)
                {
                    iconControl.setTexture(item.getIconTexture());
                    iconControl.setWidth(iconControl.getTexture().width);
                    iconControl.setHeight(iconControl.getTexture().height);
                }
                else
                {
                    iconControl.setWidth(0);
                }
            }
        }

        @Override
        protected void onMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
        {
            if (isSelected())
            {
                DropDownControl.this.toggle();
            }
            else
            {
                DropDownControl.this.setSelectedItemControl(this);
                DropDownControl.this.collapse();
            }

            super.onMouseReleased(mouseButtonEvent);
        }

        @Override
        public void onUnfocused(@Nullable Control focusedControl)
        {
            if (!getParent().getChildren().contains(focusedControl))
            {
                DropDownControl.this.collapse();
            }
        }

        /**
         * Called when the item is selected.
         *
         * @param forwardToItem whether to call {@link Item#onSelected}.
         */
        private void onSelected(boolean forwardToItem)
        {
            setVisible(true);
            backgroundControl.setTexture(GuiTextures.Common.DropDown.SELECTED_BACKGROUND);
            backgroundControl.setHeight(backgroundControl.getTexture().height);

            if (forwardToItem)
            {
                item.onSelected();
            }
        }

        /**
         * Called when the item is unselected.
         *
         * @param forwardToItem whether to call {@link Item#onUnselected}.
         */
        private void onUnselected(boolean forwardToItem)
        {
            setVisible(isExpanded());
            backgroundControl.setTexture(GuiTextures.Common.DropDown.UNSELECTED_BACKGROUND);
            backgroundControl.setHeight(backgroundControl.getTexture().height);

            if (forwardToItem)
            {
                item.onUnselected();
            }
        }

        /**
         * @return whether the {@link ItemControl} is selected.
         */
        public boolean isSelected()
        {
            return DropDownControl.this.getSelectedItemControl() == this;
        }
    }

    /**
     * A wrapper for an item in a dropdown.
     */
    public static abstract class Item
    {
        /**
         * The optional icon texture.
         */
        @Nullable
        private GuiTexture iconTexture;

        /**
         * Called when the item is selected.
         */
        protected abstract void onSelected();

        /**
         * Called when the item is unselected.
         */
        protected abstract void onUnselected();

        @Override
        @Nonnull
        public abstract String toString();

        /**
         * @return the icon texture.
         */
        @Nullable
        public GuiTexture getIconTexture()
        {
            return iconTexture;
        }

        /**
         * Sets the icon texture.
         */
        public void setIconTexture(@Nullable GuiTexture iconTexture)
        {
            this.iconTexture = iconTexture;
        }
    }
}
