package com.willr27.blocklings.client.gui.control.controls.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.NullableIntFieldControl;
import com.willr27.blocklings.client.gui.control.controls.ItemControl;
import com.willr27.blocklings.client.gui.control.controls.TextBlockControl;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.GridPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.StackPanel;
import com.willr27.blocklings.client.gui.control.event.events.*;
import com.willr27.blocklings.client.gui.control.event.events.input.KeyPressedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.properties.Flow;
import com.willr27.blocklings.client.gui.properties.GridDefinition;
import com.willr27.blocklings.client.gui.properties.Visibility;
import com.willr27.blocklings.client.gui.texture.Texture;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.entity.blockling.goal.config.iteminfo.*;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import com.willr27.blocklings.util.event.ValueChangedEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A control used to select items.
 */
@OnlyIn(Dist.CLIENT)
public abstract class ItemsConfigurationControl extends Control
{
    /**
     * The associated item info set.
     */
    @Nonnull
    public final OrderedItemInfoSet itemInfoSet;

    /**
     * Whether the configuration is for taking items or depositing items.
     */
    public final boolean isTakeItems;

    /**
     * The max amount of items.
     */
    private int maxItems = 40;

    /**
     * @param itemInfoSet the associated item info set.
     * @param isTakeItems whether the configuration is for taking items or depositing items.
     */
    public ItemsConfigurationControl(@Nonnull OrderedItemInfoSet itemInfoSet, boolean isTakeItems)
    {
        super();
        this.itemInfoSet = itemInfoSet;
        this.isTakeItems = isTakeItems;

        setWidthPercentage(1.0);
        setFitHeightToContent(true);

        itemInfoSet.eventBus.subscribe((OrderedItemInfoSet s, ItemInfoAddedEvent e) ->
        {
            addItemInfo(e.itemInfo);
        });

        itemInfoSet.eventBus.subscribe((OrderedItemInfoSet s, ItemInfoRemovedEvent e) ->
        {
            removeItemInfo(e.itemInfo);
        });

        itemInfoSet.eventBus.subscribe((OrderedItemInfoSet s, ItemInfoMovedEvent e) ->
        {
            moveItemInfo(e.movedItemInfo, e.closestItemInfo, e.insertBefore);
        });
    }

    /**
     * @param itemInfo the item info to get the control for.
     * @return the control for the item info.
     */
    @Nullable
    protected abstract ItemInfoControl getItemInfoControl(@Nonnull ItemInfo itemInfo);

    /**
     * Adds an item info to the list.
     *
     * @param itemInfo the item info to add.
     */
    protected abstract void addItemInfo(@Nonnull ItemInfo itemInfo);

    /**
     * Removes an item info from the list.
     *
     * @param itemInfo the item info to remove.
     */
    protected abstract void removeItemInfo(@Nonnull ItemInfo itemInfo);

    /**
     * Moves an item info in the list.
     *
     * @param movedItemInfo the item info to move.
     * @param closestItemInfo the item info to move the moved item info relative to.
     * @param insertBefore whether to insert the moved item info before the closest item info.
     */
    protected abstract void moveItemInfo(@Nonnull ItemInfo movedItemInfo, @Nonnull ItemInfo closestItemInfo, boolean insertBefore);

    /**
     * @return the max amount of items.
     */
    public int getMaxItems()
    {
        return maxItems;
    }

    /**
     * Sets the max amount of items.
     *
     * @param maxItems the max amount of items.
     */
    public void setMaxItems(int maxItems)
    {
        this.maxItems = maxItems;
    }

    /**
     * An item info config control.
     */
    private static class ItemInfoControl extends Control
    {
        /**
         * The item info to display.
         */
        @Nonnull
        public final ItemInfo itemInfo;

        /**
         * @param itemInfo the item info to display.
         */
        public ItemInfoControl(@Nonnull ItemInfo itemInfo)
        {
            super();
            this.itemInfo = itemInfo;
        }
    }

    /**
     * A simple items configuration control.
     */
    public static class SimpleItemsConfigurationControl extends ItemsConfigurationControl
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
         * @param itemInfoSet the associated item info set.
         * @param isTakeItems whether the configuration is for taking items or depositing items.
         */
        public SimpleItemsConfigurationControl(@Nonnull OrderedItemInfoSet itemInfoSet, boolean isTakeItems)
        {
            super(itemInfoSet, isTakeItems);

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
                ItemInfoControl itemInfoControl = (ItemInfoControl) e.draggedControl;
                ItemInfoControl closestItemInfoControl = (ItemInfoControl) e.closestControl;

                if (e.insertBefore)
                {
                    itemInfoSet.moveBefore(itemInfoControl.itemInfo, closestItemInfoControl.itemInfo);
                }
                else
                {
                    itemInfoSet.moveAfter(itemInfoControl.itemInfo, closestItemInfoControl.itemInfo);
                }
            });

            addBackground = new TexturedControl(Textures.Tasks.TASK_ICON_BACKGROUND_RAISED, Textures.Tasks.TASK_ICON_BACKGROUND_PRESSED)
            {
                @Override
                protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
                {
                    if (itemsPanel.getChildren().size() - 1 > getMaxItems())
                    {
                        renderTextureAsBackground(matrixStack, getBackgroundTexture());
                    }
                    else
                    {
                        super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
                    }
                }

                @Override
                public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
                {
                    List<IReorderingProcessor> tooltip = new ArrayList<>();
                    tooltip.add(new BlocklingsTranslationTextComponent("config.item.add").withStyle(itemsPanel.getChildren().size() - 1 > getMaxItems() ? TextFormatting.GRAY : TextFormatting.WHITE).getVisualOrderText());
                    tooltip.add(new BlocklingsTranslationTextComponent("config.item.amount", itemsPanel.getChildren().size() - 2, getMaxItems()).withStyle(TextFormatting.GRAY).getVisualOrderText());
                    renderTooltip(matrixStack, mouseX, mouseY, tooltip);
                }

                @Override
                protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
                {
                    if (isPressed() && itemsPanel.getChildren().size() - 1 <= getMaxItems())
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

            TexturedControl addIcon = new TexturedControl(Textures.Common.PLUS_ICON)
            {
                @Override
                protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
                {
                    if (itemsPanel.getChildren().size() - 1 > getMaxItems())
                    {
                        renderTextureAsBackground(matrixStack, Textures.Common.PLUS_ICON_DISABLED);
                    }
                    else
                    {
                        super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
                    }
                }
            };
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
            itemSearchControl.setFilter((Item item) -> itemsPanel.getChildren().stream().noneMatch(c -> c instanceof SimpleItemInfoControl && ((SimpleItemInfoControl)c).itemInfo.getItem() == item));
            itemSearchControl.eventBus.subscribe((BaseControl c, ItemAddedEvent e) ->
            {
                ItemInfo itemInfo = new ItemInfo(e.item);

                addItemInfo(itemInfo);
                closeItemSearch();

                itemInfoSet.add(itemInfo, true);
            });

            screenEventBus.subscribe((BaseControl c, FocusChangedEvent e) ->
            {
                if (!c.isFocused() && itemSearchControl.isThisOrDescendant(c) && !itemSearchControl.isThisOrDescendant(getFocusedControl()))
                {
                    closeItemSearch();
                }
            });

            ArrayList<ItemInfo> reverseItemInfos = new ArrayList<>(itemInfoSet.getItemInfos());
            Collections.reverse(reverseItemInfos);

            for (ItemInfo itemInfo : reverseItemInfos)
            {
                addItemInfo(itemInfo);
            }
        }

        @Nullable
        @Override
        protected ItemInfoControl getItemInfoControl(@Nonnull ItemInfo itemInfo)
        {
            for (BaseControl child : itemsPanel.getChildren())
            {
                if (child instanceof SimpleItemInfoControl && ((SimpleItemInfoControl)child).itemInfo.equals(itemInfo))
                {
                    return (SimpleItemInfoControl)child;
                }
            }

            return null;
        }

        @Override
        protected void addItemInfo(@Nonnull ItemInfo itemInfo)
        {
            if (getItemInfoControl(itemInfo) != null)
            {
                return;
            }

            itemsPanel.insertChildAfter(new SimpleItemInfoControl(itemInfo), addBackground);
        }

        @Override
        protected void removeItemInfo(@Nonnull ItemInfo itemInfo)
        {
            ItemInfoControl itemInfoControl = getItemInfoControl(itemInfo);

            if (itemInfoControl == null)
            {
                return;
            }

            itemsPanel.removeChild(itemInfoControl);
        }

        @Override
        protected void moveItemInfo(@Nonnull ItemInfo movedItemInfo, @Nonnull ItemInfo closestItemInfo, boolean insertBefore)
        {
            ItemInfoControl closestItemInfoControl = getItemInfoControl(closestItemInfo);

            if (closestItemInfoControl == null)
            {
                return;
            }

            ItemInfoControl movedItemInfoControl = getItemInfoControl(movedItemInfo);

            if (movedItemInfoControl == null)
            {
                return;
            }

            if (insertBefore)
            {
                itemsPanel.insertChildBefore(movedItemInfoControl, closestItemInfoControl);
            }
            else
            {
                itemsPanel.insertChildAfter(movedItemInfoControl, closestItemInfoControl);
            }
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
         * A simple item info config control.
         */
        private class SimpleItemInfoControl extends ItemInfoControl
        {
            /**
             * The item overlay.
             */
            @Nonnull
            private final Control itemOverlay;

            /**
             * The cross icon.
             */
            @Nonnull
            private final TexturedControl crossIcon;

            /**
             * @param itemInfo the item info to display.
             */
            public SimpleItemInfoControl(@Nonnull ItemInfo itemInfo)
            {
                super(itemInfo);

                setFitWidthToContent(true);
                setFitHeightToContent(true);
                setDraggableX(true);
                setDraggableY(true);
                setShouldSnapToScreenCoords(true);

                TexturedControl itemBackground = new TexturedControl(Textures.Tasks.TASK_ICON_BACKGROUND_RAISED, Textures.Tasks.TASK_ICON_BACKGROUND_PRESSED)
                {
                    @Override
                    protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
                    {
                        super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
                    }
                    @Override
                    public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
                    {
                        renderTooltip(matrixStack, mouseX, mouseY, new BlocklingsTranslationTextComponent("config.item.remove", new ItemStack(itemInfo.getItem()).getHoverName().getString()));
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

                            itemInfoSet.remove(itemInfo);

                            e.setIsHandled(true);
                        }
                    }
                };
                itemBackground.setParent(this);
                itemBackground.setChildrenInteractive(false);

                ItemControl itemControl = new ItemControl();
                itemControl.setParent(itemBackground);
                itemControl.setItem(itemInfo.getItem());
                itemControl.setWidthPercentage(1.0);
                itemControl.setHeightPercentage(1.0);
                itemControl.setItemScale(0.8f);
                itemControl.setHorizontalAlignment(0.5);
                itemControl.setVerticalAlignment(0.5);

                itemOverlay = new Control();
                itemOverlay.setParent(itemControl);
                itemOverlay.setWidthPercentage(1.0);
                itemOverlay.setHeightPercentage(1.0);

                crossIcon = new TexturedControl(Textures.Common.CROSS_ICON);
                crossIcon.setParent(itemBackground);
                crossIcon.setVisibility(Visibility.INVISIBLE);
                crossIcon.setHorizontalAlignment(0.5);
                crossIcon.setVerticalAlignment(0.5);
            }
        }
    }

    /**
     * An advanced items configuration control.
     */
    public static class AdvancedItemsConfigurationControl extends ItemsConfigurationControl
    {
        /**
         * The items panel.
         */
        @Nonnull
        private final StackPanel itemsPanel;

        /**
         * The add item container.
         */
        @Nonnull
        private final Control addItemContainer;

        /**
         * The item search control.
         */
        @Nonnull
        private final ItemSearchControl itemSearchControl;

        /**
         * @param itemInfoSet the item info set.
         * @param isTakeItems whether the configuration is taking or depositing items.
         */
        public AdvancedItemsConfigurationControl(@Nonnull OrderedItemInfoSet itemInfoSet, boolean isTakeItems)
        {
            super(itemInfoSet, isTakeItems);

            setWidthPercentage(1.0);
            setFitHeightToContent(true);
            setClipContentsToBounds(false);

            itemsPanel = new StackPanel();
            itemsPanel.setParent(this);
            itemsPanel.setWidthPercentage(1.0);
            itemsPanel.setFitHeightToContent(true);
            itemsPanel.setSpacing(4.0);
            itemsPanel.setClipContentsToBounds(false);
            itemsPanel.eventBus.subscribe((BaseControl c, ReorderEvent e) ->
            {
                ItemInfoControl itemInfoControl = (ItemInfoControl) e.draggedControl;
                ItemInfoControl closestItemInfoControl = (ItemInfoControl) e.closestControl;

                if (e.insertBefore)
                {
                    itemInfoSet.moveBefore(itemInfoControl.itemInfo, closestItemInfoControl.itemInfo);
                }
                else
                {
                    itemInfoSet.moveAfter(itemInfoControl.itemInfo, closestItemInfoControl.itemInfo);
                }
            });

            addItemContainer = new Control();
            addItemContainer.setParent(itemsPanel);
            addItemContainer.setWidthPercentage(1.0);
            addItemContainer.setFitHeightToContent(true);
            addItemContainer.setReorderable(false);
            TexturedControl addItemButton = new TexturedControl(Textures.Common.PLUS_ICON)
            {
                @Override
                protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
                {
                    if (itemsPanel.getChildren().size() - 1 > getMaxItems())
                    {
                        renderTextureAsBackground(matrixStack, Textures.Common.PLUS_ICON_DISABLED);
                    }
                    else
                    {
                        super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
                    }
                }

                @Override
                public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
                {
                    List<IReorderingProcessor> tooltip = new ArrayList<>();
                    tooltip.add(new BlocklingsTranslationTextComponent("config.item.add").withStyle(itemsPanel.getChildren().size() - 1 > getMaxItems() ? TextFormatting.GRAY : TextFormatting.WHITE).getVisualOrderText());
                    tooltip.add(new BlocklingsTranslationTextComponent("config.item.amount", itemsPanel.getChildren().size() - 2, getMaxItems()).withStyle(TextFormatting.GRAY).getVisualOrderText());
                    renderTooltip(matrixStack, mouseX, mouseY, tooltip);
                }

                @Override
                protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
                {
                    if (isPressed() && itemsPanel.getChildren().size() - 1 <= getMaxItems())
                    {
                        openItemSearch();

                        e.setIsHandled(true);
                    }
                }
            };
            addItemButton.setParent(addItemContainer);
            addItemButton.setHorizontalAlignment(0.5);
            addItemButton.setMargins(0.0, 1.0, 0.0, 1.0);

            itemSearchControl = new ItemSearchControl()
            {
                @Override
                public void onKeyPressed(@Nonnull KeyPressedEvent e)
                {
                    openItemSearch();

                    e.setIsHandled(true);
                }
            };
            itemSearchControl.setVisibility(Visibility.COLLAPSED);
            itemSearchControl.setParent(itemsPanel);
            itemSearchControl.setReorderable(false);
            itemSearchControl.setFilter((Item item) -> itemsPanel.getChildren().stream().noneMatch(c -> c instanceof AdvancedItemInfoControl && ((AdvancedItemInfoControl)c).itemInfo.getItem() == item));
            itemSearchControl.eventBus.subscribe((BaseControl c, ItemAddedEvent e) ->
            {
                ItemInfo itemInfo = new ItemInfo(e.item);

                addItemInfo(itemInfo);
                closeItemSearch();

                itemInfoSet.add(itemInfo, false);
            });

            screenEventBus.subscribe((BaseControl c, FocusChangedEvent e) ->
            {
                if (!c.isFocused() && itemSearchControl.isThisOrDescendant(c) && !itemSearchControl.isThisOrDescendant(getFocusedControl()))
                {
                    closeItemSearch();
                }
            });

            itemsPanel.addChild(addItemContainer);

            for (ItemInfo itemInfo : itemInfoSet.getItemInfos())
            {
                addItemInfo(itemInfo);
            }

            itemsPanel.insertChildLast(itemSearchControl);
            itemsPanel.insertChildLast(addItemContainer);
        }

        @Nullable
        @Override
        protected ItemInfoControl getItemInfoControl(@Nonnull ItemInfo itemInfo)
        {
            for (BaseControl child : itemsPanel.getChildren())
            {
                if (child instanceof AdvancedItemInfoControl && ((AdvancedItemInfoControl)child).itemInfo == itemInfo)
                {
                    return (ItemInfoControl) child;
                }
            }

            return null;
        }

        @Override
        protected void addItemInfo(@Nonnull ItemInfo itemInfo)
        {
            if (getItemInfoControl(itemInfo) != null)
            {
                return;
            }

            itemsPanel.insertChildBefore(new AdvancedItemInfoControl(itemInfo), addItemContainer);
        }

        @Override
        protected void removeItemInfo(@Nonnull ItemInfo itemInfo)
        {
            ItemInfoControl itemInfoControl = getItemInfoControl(itemInfo);

            if (itemInfoControl == null)
            {
                return;
            }

            itemsPanel.removeChild(itemInfoControl);
        }

        @Override
        protected void moveItemInfo(@Nonnull ItemInfo movedItemInfo, @Nonnull ItemInfo closestItemInfo, boolean insertBefore)
        {
            ItemInfoControl closestItemInfoControl = getItemInfoControl(closestItemInfo);

            if (closestItemInfoControl == null)
            {
                return;
            }

            ItemInfoControl movedItemInfoControl = getItemInfoControl(movedItemInfo);

            if (movedItemInfoControl == null)
            {
                return;
            }

            if (insertBefore)
            {
                itemsPanel.insertChildBefore(movedItemInfoControl, closestItemInfoControl);
            }
            else
            {
                itemsPanel.insertChildAfter(movedItemInfoControl, closestItemInfoControl);
            }
        }

        /**
         * Opens the item search control.
         */
        private void openItemSearch()
        {
            itemsPanel.insertChildBefore(itemSearchControl, addItemContainer);
            itemSearchControl.setVisibility(Visibility.VISIBLE);
            itemSearchControl.setFocused(true);
        }

        /**
         * Closes the item search control.
         */
        private void closeItemSearch()
        {
            itemSearchControl.setVisibility(Visibility.COLLAPSED);
        }

        /**
         * An advanced item info config control.
         */
        private class AdvancedItemInfoControl extends ItemInfoControl
        {
            /**
             * @param itemInfo the item info to display.
             */
            public AdvancedItemInfoControl(@Nonnull ItemInfo itemInfo)
            {
                super(itemInfo);

                setWidthPercentage(1.0);
                setFitHeightToContent(true);
                setDraggableY(true);

                GridPanel grid = new GridPanel();
                addChild(grid);
                grid.setWidthPercentage(1.0);
                grid.setFitHeightToContent(true);
                grid.addRowDefinition(GridDefinition.AUTO, 1.0);
                grid.addRowDefinition(GridDefinition.AUTO, 1.0);
                grid.addColumnDefinition(GridDefinition.AUTO, 1.0);

                GridPanel mainGrid = new GridPanel();
                grid.addChild(mainGrid, 0, 0);
                mainGrid.setWidthPercentage(1.0);
                mainGrid.setFitHeightToContent(true);
                mainGrid.addRowDefinition(GridDefinition.RATIO, 1.0);
                mainGrid.addColumnDefinition(GridDefinition.AUTO, 1.0);
                mainGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);

                Control crossBackground = new Control();

                TexturedControl iconBackground = new TexturedControl(Textures.Tasks.TASK_ICON_BACKGROUND_RAISED, Textures.Tasks.TASK_ICON_BACKGROUND_PRESSED)
                {
                    @Override
                    public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
                    {
                        renderTooltip(matrixStack, mouseX, mouseY, new BlocklingsTranslationTextComponent("config.item.remove", new ItemStack(itemInfo.getItem()).getHoverName().getString()));
                    }

                    @Override
                    public void onHoverEnter()
                    {
                        crossBackground.setVisibility(Visibility.VISIBLE);
                    }

                    @Override
                    public void onHoverExit()
                    {
                        crossBackground.setVisibility(Visibility.COLLAPSED);
                    }

                    @Override
                    protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
                    {
                        if (isPressed())
                        {
                            AdvancedItemInfoControl.this.setParent(null);

                            itemInfoSet.remove(itemInfo);

                            e.setIsHandled(true);
                        }
                    }
                };
                mainGrid.addChild(iconBackground, 0, 0);
                iconBackground.setChildrenInteractive(false);

                ItemControl itemIcon = new ItemControl();
                iconBackground.addChild(itemIcon);
                itemIcon.setWidthPercentage(1.0);
                itemIcon.setHeightPercentage(1.0);
                itemIcon.setItemScale(0.8f);
                itemIcon.setItem(itemInfo.getItem());

                iconBackground.addChild(crossBackground);
                crossBackground.setBackgroundColour(0x55000000);
                crossBackground.setWidthPercentage(1.0);
                crossBackground.setHeightPercentage(1.0);
                crossBackground.setVisibility(Visibility.COLLAPSED);

                TexturedControl crossIcon = new TexturedControl(Textures.Common.CROSS_ICON);
                crossBackground.addChild(crossIcon);
                crossIcon.setVerticalAlignment(0.5);
                crossIcon.setHorizontalAlignment(0.5);
                crossIcon.setRenderZ(18.0);

                GridPanel dropdownGrid = new GridPanel()
                {
                    @Override
                    protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
                    {
                        Texture texture = Textures.Common.BAR_FLAT.dy(1).dHeight(-2).width((int) getWidth());
                        Texture endTexture = Textures.Common.BAR_FLAT.dy(1).dHeight(-2).width(2).x(Textures.Common.BAR_FLAT.width - 2);

                        for (int i = 0; i < getHeight(); i += texture.height)
                        {
                            renderTextureAsBackground(matrixStack, texture, 0, i);
                            renderTextureAsBackground(matrixStack, endTexture, getWidth() - 2, i);
                        }

                        renderTextureAsBackground(matrixStack, texture.dy(18).height(1), 0, getHeight() - 1);
                        renderRectangleAsBackground(matrixStack, 0x33000000, 1.0, 0.0, (int) (getWidth() - 2), (int) (getHeight() - 1));
                    }
                };

                TexturedControl upArrow = new TexturedControl(Textures.Common.ComboBox.UP_ARROW);
                TexturedControl downArrow = new TexturedControl(Textures.Common.ComboBox.DOWN_ARROW);
                TextBlockControl name = new TextBlockControl();

                TexturedControl nameBackground = new TexturedControl(Textures.Common.BAR_RAISED)
                {
                    @Override
                    protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
                    {
                        if (isHovered() && getDraggedControl() == null)
                        {
                            RenderSystem.color3f(0.7f, 0.9f, 1.0f);
                        }

                        Texture texture = getBackgroundTexture();

                        renderTextureAsBackground(matrixStack, texture.dx(1).width((int) (getWidth() - 2)));
                        renderTextureAsBackground(matrixStack, texture.x(texture.width - 2).width(2), getWidth() - 2, 0);
                    }

                    @Override
                    public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
                    {
                        renderTooltip(matrixStack, mouseX, mouseY, name.getText());
                    }

                    @Override
                    public void forwardTryDrag(@Nonnull TryDragEvent e)
                    {
                        super.forwardTryDrag(e);
                    }

                    @Override
                    protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
                    {
                        if (isPressed())
                        {
                            dropdownGrid.setVisibility(dropdownGrid.getVisibility() == Visibility.VISIBLE ? Visibility.COLLAPSED : Visibility.VISIBLE);
                            upArrow.setVisibility(dropdownGrid.getVisibility());
                            downArrow.setVisibility(dropdownGrid.getVisibility() == Visibility.VISIBLE ? Visibility.COLLAPSED : Visibility.VISIBLE);
                        }
                    }
                };
                mainGrid.addChild(nameBackground, 0, 1);
                nameBackground.setWidthPercentage(1.0);

                GridPanel nameGrid = new GridPanel();
                mainGrid.addChild(nameGrid, 0, 1);
                nameGrid.setWidthPercentage(1.0);
                nameGrid.setFitHeightToContent(true);
                nameGrid.setVerticalAlignment(0.5);
                nameGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
                nameGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);
                nameGrid.addColumnDefinition(GridDefinition.AUTO, 1.0);
                nameGrid.setInteractive(false);

                nameGrid.addChild(name, 0, 0);
                name.setText(new ItemStack(itemInfo.getItem()).getHoverName().getString());
                name.setWidthPercentage(1.0);
                name.setMarginLeft(4.0);

                nameGrid.addChild(upArrow, 0, 1);
                upArrow.setVerticalAlignment(0.5);
                upArrow.setMargins(4.0, 0.0, 5.0, 0.0);
                upArrow.setVisibility(Visibility.COLLAPSED);

                nameGrid.addChild(downArrow, 0, 1);
                downArrow.setVerticalAlignment(0.5);
                downArrow.setMargins(4.0, 0.0, 5.0, 0.0);

                grid.addChild(dropdownGrid, 1, 0);
                dropdownGrid.setWidthPercentage(1.0);
                dropdownGrid.setFitHeightToContent(true);
                dropdownGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
                dropdownGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
                dropdownGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
                dropdownGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
                dropdownGrid.addColumnDefinition(GridDefinition.AUTO, 1.0);
                dropdownGrid.setDebugName("Dropdown Grid");
                dropdownGrid.setShouldPropagateDrag(false);
                dropdownGrid.setPaddingBottom(4.0);
                dropdownGrid.setVisibility(Visibility.COLLAPSED);

                int maxSymbolWidth = GuiUtil.get().getTextWidth(">=") + 8;

                TextBlockControl startText = new TextBlockControl();
                dropdownGrid.addChild(startText, 0, 0);
                startText.setWidthPercentage(1.0);
                startText.setText(new BlocklingsTranslationTextComponent("config.item.start_at"));
                startText.setMarginLeft(4.0);
                startText.setMarginRight(4.0);
                startText.setMarginTop(4.0);
                startText.setMarginBottom(3.0);

                GridPanel startGrid = new GridPanel();
                dropdownGrid.addChild(startGrid, 1, 0);
                startGrid.setWidthPercentage(1.0);
                startGrid.setFitHeightToContent(true);
                startGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
                startGrid.addColumnDefinition(GridDefinition.FIXED, maxSymbolWidth);
                startGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);
                startGrid.addColumnDefinition(GridDefinition.FIXED, maxSymbolWidth);
                startGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);

                TextBlockControl startInventoryText = new TextBlockControl();
                startGrid.addChild(startInventoryText, 0, 0);
                startInventoryText.setFitWidthToContent(true);
                startInventoryText.setText(isTakeItems ? "<" : ">");
                startInventoryText.setMarginLeft(4.0);
                startInventoryText.setMarginRight(4.0);
                startInventoryText.setHorizontalAlignment(0.5);
                startInventoryText.setVerticalAlignment(0.5);

                NullableIntFieldControl startInventoryField = new NullableIntFieldControl()
                {
                    @Override
                    public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
                    {
                        List<IReorderingProcessor> tooltip = new ArrayList<>();
                        tooltip.add(new BlocklingsTranslationTextComponent("config.item.inventory_start_amount.name").getVisualOrderText());
                        tooltip.addAll(GuiUtil.get().split(new BlocklingsTranslationTextComponent("config.item.inventory_start_amount.desc").withStyle(TextFormatting.GRAY), 200));

                        renderTooltip(matrixStack, mouseX, mouseY, tooltip);
                    }
                };
                startGrid.addChild(startInventoryField, 0, 1);
                startInventoryField.setWidthPercentage(1.0);
                startInventoryField.setHeight(16);
                startInventoryField.setMarginRight(4.0);
                startInventoryField.setHorizontalContentAlignment(0.5);
                startInventoryField.setValue(itemInfo.getStartInventoryAmount());
                startInventoryField.setMinVal(0);
                startInventoryField.setMaxVal(99999);
                startInventoryField.eventBus.subscribe((BaseControl c, ValueChangedEvent<Integer> e) ->
                {
                    int index = itemInfoSet.getItemInfos().indexOf(itemInfo);
                    itemInfo.setStartInventoryAmount(e.newValue);
                    itemInfoSet.set(index, itemInfo);
                });

                TextBlockControl startContainerText = new TextBlockControl();
                startGrid.addChild(startContainerText, 0, 2);
                startContainerText.setFitWidthToContent(true);
                startContainerText.setText(isTakeItems ? ">" : "<");
                startContainerText.setMarginLeft(4.0);
                startContainerText.setMarginRight(4.0);
                startContainerText.setHorizontalAlignment(0.5);
                startContainerText.setVerticalAlignment(0.5);

                NullableIntFieldControl startContainerField = new NullableIntFieldControl()
                {
                    @Override
                    public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
                    {
                        List<IReorderingProcessor> tooltip = new ArrayList<>();
                        tooltip.add(new BlocklingsTranslationTextComponent("config.item.container_start_amount.name").getVisualOrderText());
                        tooltip.addAll(GuiUtil.get().split(new BlocklingsTranslationTextComponent("config.item.container_start_amount.desc").withStyle(TextFormatting.GRAY), 200));

                        renderTooltip(matrixStack, mouseX, mouseY, tooltip);
                    }
                };
                startGrid.addChild(startContainerField, 0, 3);
                startContainerField.setWidthPercentage(1.0);
                startContainerField.setHeight(16);
                startContainerField.setMarginRight(4.0);
                startContainerField.setHorizontalContentAlignment(0.5);
                startContainerField.setValue(itemInfo.getStartContainerAmount());
                startContainerField.setMinVal(0);
                startContainerField.setMaxVal(99999);
                startContainerField.eventBus.subscribe((BaseControl c, ValueChangedEvent<Integer> e) ->
                {
                    int index = itemInfoSet.getItemInfos().indexOf(itemInfo);
                    itemInfo.setStartContainerAmount(e.newValue);
                    itemInfoSet.set(index, itemInfo);
                });

                TextBlockControl stopText = new TextBlockControl();
                dropdownGrid.addChild(stopText, 2, 0);
                stopText.setWidthPercentage(1.0);
                stopText.setText(new BlocklingsTranslationTextComponent("config.item.stop_at"));
                stopText.setMarginLeft(4.0);
                stopText.setMarginRight(4.0);
                stopText.setMarginTop(6.0);
                stopText.setMarginBottom(3.0);

                GridPanel stopGrid = new GridPanel();
                dropdownGrid.addChild(stopGrid, 3, 0);
                stopGrid.setWidthPercentage(1.0);
                stopGrid.setFitHeightToContent(true);
                stopGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
                stopGrid.addColumnDefinition(GridDefinition.FIXED, maxSymbolWidth);
                stopGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);
                stopGrid.addColumnDefinition(GridDefinition.FIXED, maxSymbolWidth);
                stopGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);

                TextBlockControl stopInventoryText = new TextBlockControl();
                stopGrid.addChild(stopInventoryText, 0, 0);
                stopInventoryText.setFitWidthToContent(true);
                stopInventoryText.setText(isTakeItems ? ">=" : "<=");
                stopInventoryText.setMarginLeft(4.0);
                stopInventoryText.setMarginRight(4.0);
                stopInventoryText.setHorizontalAlignment(0.5);
                stopInventoryText.setVerticalAlignment(0.5);

                NullableIntFieldControl stopInventoryField = new NullableIntFieldControl()
                {
                    @Override
                    public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
                    {
                        List<IReorderingProcessor> tooltip = new ArrayList<>();
                        tooltip.add(new BlocklingsTranslationTextComponent("config.item.inventory_stop_amount.name").getVisualOrderText());
                        tooltip.addAll(GuiUtil.get().split(new BlocklingsTranslationTextComponent("config.item.inventory_stop_amount.desc").withStyle(TextFormatting.GRAY), 200));

                        renderTooltip(matrixStack, mouseX, mouseY, tooltip);
                    }
                };
                stopGrid.addChild(stopInventoryField, 0, 1);
                stopInventoryField.setWidthPercentage(1.0);
                stopInventoryField.setHeight(16);
                stopInventoryField.setMarginRight(4.0);
                stopInventoryField.setHorizontalContentAlignment(0.5);
                stopInventoryField.setValue(itemInfo.getStopInventoryAmount());
                stopInventoryField.setMinVal(0);
                stopInventoryField.setMaxVal(99999);
                stopInventoryField.eventBus.subscribe((BaseControl c, ValueChangedEvent<Integer> e) ->
                {
                    int index = itemInfoSet.getItemInfos().indexOf(itemInfo);
                    itemInfo.setStopInventoryAmount(e.newValue);
                    itemInfoSet.set(index, itemInfo);
                });

                TextBlockControl stopContainerText = new TextBlockControl();
                stopGrid.addChild(stopContainerText, 0, 2);
                stopContainerText.setFitWidthToContent(true);
                stopContainerText.setText(isTakeItems ? "<=" : ">=");
                stopContainerText.setMarginLeft(4.0);
                stopContainerText.setMarginRight(4.0);
                stopContainerText.setHorizontalAlignment(0.5);
                stopContainerText.setVerticalAlignment(0.5);

                NullableIntFieldControl stopContainerField = new NullableIntFieldControl()
                {
                    @Override
                    public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
                    {
                        List<IReorderingProcessor> tooltip = new ArrayList<>();
                        tooltip.add(new BlocklingsTranslationTextComponent("config.item.container_stop_amount.name").getVisualOrderText());
                        tooltip.addAll(GuiUtil.get().split(new BlocklingsTranslationTextComponent("config.item.container_stop_amount.desc").withStyle(TextFormatting.GRAY), 200));

                        renderTooltip(matrixStack, mouseX, mouseY, tooltip);
                    }
                };
                stopGrid.addChild(stopContainerField, 0, 3);
                stopContainerField.setWidthPercentage(1.0);
                stopContainerField.setHeight(16);
                stopContainerField.setMarginRight(4.0);
                stopContainerField.setHorizontalContentAlignment(0.5);
                stopContainerField.setValue(itemInfo.getStopContainerAmount());
                stopContainerField.setMinVal(0);
                stopContainerField.setMaxVal(99999);
                stopContainerField.eventBus.subscribe((BaseControl c, ValueChangedEvent<Integer> e) ->
                {
                    int index = itemInfoSet.getItemInfos().indexOf(itemInfo);
                    itemInfo.setStopContainerAmount(e.newValue);
                    itemInfoSet.set(index, itemInfo);
                });
            }
        }
    }
}