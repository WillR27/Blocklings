package com.willr27.blocklings.client.gui.controls.tasks.config.configs;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.*;
import com.willr27.blocklings.client.gui.controls.Orientation;
import com.willr27.blocklings.client.gui.controls.common.ScrollbarControl;
import com.willr27.blocklings.client.gui.controls.common.TextFieldControl;
import com.willr27.blocklings.client.gui.controls.common.panel.FlowPanelControl;
import com.willr27.blocklings.client.gui.controls.tasks.config.ConfigControl;
import com.willr27.blocklings.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A control used to display a whitelist.
 */
@OnlyIn(Dist.CLIENT)
public class ItemsConfigControl extends ConfigControl
{
    /**
     * The panel that contains all the item controls.
     */
    @Nonnull
    private final FlowPanelControl itemsPanel;

    /**
     * @param parent the parent control.
     * @param x the x position.
     * @param y the y position.
     * @param width the width.
     * @param height the height.
     */
    public ItemsConfigControl(@Nonnull IControl parent, int x, int y, int width, int height)
    {
        super(parent, x, y, width, height);

//        int i = 0;
//        for (Map.Entry<ResourceLocation, Boolean> entry : whitelist.entrySet())
//        {
//            entryControls.add(new EntryControl(this, whitelist, entry, ENTRY_GAP + (i % 4) * (EntryControl.ENTRY_SELECTED.width + ENTRY_GAP), 3 + ENTRY_GAP + (i / 4) * (EntryControl.ENTRY_SELECTED.height + ENTRY_GAP)));
//            contentScrollbarControl.setMaxScrollY(3 + ENTRY_GAP + (i / 4) * (EntryControl.ENTRY_SELECTED.height + ENTRY_GAP) + EntryControl.ENTRY_UNSELECTED.height + ENTRY_GAP - height);
//            i++;
//        }

        itemsPanel = new FlowPanelControl(this, 0, 0, width, height);
        itemsPanel.setPadding(5, 8, 5, 5);
        itemsPanel.setOrientation(Orientation.HORIZONTAL);
        itemsPanel.setIsAutoSizedY(true);
        itemsPanel.setItemGapX(2);
        itemsPanel.setItemGapY(2);
        itemsPanel.setIsReorderable(true);
        itemsPanel.onReorder.subscribe((e ->
        {
            // Prevent the add control from moving.
            if (e.newIndex == 0)
            {
                e.setCancelled(true);
                e.setIsHandled(true);
            }
        }));

        itemsPanel.addChild(new AddItemControl(this));

        for (int i = 0; i < 10; i++)
        {
            for (Block block : BlockUtil.ORES.get())
            {
                itemsPanel.addChild(new ItemControl(this, block.asItem()));
            }
        }
    }

    /**
     * A control for individual items in a list of items.
     */
    private static class AddItemControl extends Control
    {
        /**
         * The width of the control.
         */
        private static final int WIDTH = 20;

        /**
         * The texture for the start of the control's background.
         */
        @Nonnull
        private static final GuiTexture BACKGROUND_START_TEXTURE = GuiTextures.RAISED_BAR.width(WIDTH - GuiTextures.RAISED_BAR_END.width);

        /**
         * The texture for the end of the control's background.
         */
        @Nonnull
        private static final GuiTexture BACKGROUND_END_TEXTURE = GuiTextures.RAISED_BAR_END;

        /**
         * @param parent the parent control.
         */
        public AddItemControl(@Nonnull Control parent)
        {
            super(parent, 0, 0, WIDTH, BACKGROUND_START_TEXTURE.height);

            setIsDraggable(false);
        }

        @Override
        public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
        {
            renderTexture(matrixStack, BACKGROUND_START_TEXTURE);
            renderTexture(matrixStack, BACKGROUND_START_TEXTURE.width, 0, BACKGROUND_END_TEXTURE);
            renderTexture(matrixStack, (width - GuiTextures.PLUS_ICON.width) / 2, (height - GuiTextures.PLUS_ICON.height) / 2, GuiTextures.PLUS_ICON);
        }

        @Override
        public void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
        {
            screen.renderTooltip(matrixStack, new StringTextComponent("TOOLTIP"), mouseX, mouseY);
        }
    }

    /**
     * A control for individual items in a list of items.
     */
    private static class ItemControl extends Control
    {
        /**
         * The width of the control.
         */
        private static final int WIDTH = 20;

        /**
         * The texture for the start of the control's background.
         */
        @Nonnull
        private static final GuiTexture BACKGROUND_START_TEXTURE = GuiTextures.RAISED_BAR.width(WIDTH - GuiTextures.RAISED_BAR_END.width);

        /**
         * The texture for the end of the control's background.
         */
        @Nonnull
        private static final GuiTexture BACKGROUND_END_TEXTURE = GuiTextures.RAISED_BAR_END;

        /**
         * The associated item.
         */
        @Nonnull
        public final Item item;

        /**
         * @param parent the parent control.
         * @param item   the associated item.
         */
        public ItemControl(@Nonnull Control parent, @Nonnull Item item)
        {
            super(parent, 0, 0, WIDTH, BACKGROUND_START_TEXTURE.height);
            this.item = item;
        }

        @Override
        public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
        {
            renderTexture(matrixStack, BACKGROUND_START_TEXTURE);
            renderTexture(matrixStack, BACKGROUND_START_TEXTURE.width, 0, BACKGROUND_END_TEXTURE);

            GuiUtil.renderItemStack(matrixStack, new ItemStack(item), (int) (screenX / getEffectiveScale()) + getEffectiveWidth() / 2, (int) (screenY / getEffectiveScale()) + getEffectiveHeight() / 2, 16.0f);
        }

        @Override
        public void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
        {
            screen.renderTooltip(matrixStack, item.getName(item.getDefaultInstance()), mouseX, mouseY);
        }
    }

    /**
     * Used to search for and select items.
     */
    private static class ItemSelectionControl extends Control
    {
        /**
         * The list of item controls to show that match the search criteria.
         */
        @Nonnull
        private final List<ItemControl> items = new ArrayList<>();

        /**
         * The text field used to search for items.
         */
        @Nonnull
        private final TextFieldControl searchTextFieldControl;

        public ItemSelectionControl(@Nonnull Control parent, int width)
        {
            super(parent, 0, 0, width, 100);

            IControl screen = (IControl) getScreen();
            float scale = screen.getScale();
            searchTextFieldControl = new TextFieldControl(font, (int) ((screenX / scale) + 20), (int) ((screenY / scale) + 0), width - 20, 20, new StringTextComponent(""))
            {

            };
        }

        @Override
        public void preRender(int mouseX, int mouseY, float partialTicks)
        {

        }

        private static class ItemControl extends Control
        {
            public ItemControl(@Nonnull Control parent)
            {
                super(parent, 0, 0, 16, 16);
            }
        }
    }
}
