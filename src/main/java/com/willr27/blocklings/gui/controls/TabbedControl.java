package com.willr27.blocklings.gui.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * The control for the tabs in the blockling's gui.
 * This is a control instead of a screen, so it can be used with containers screens as well as regular screens.
 */
@OnlyIn(Dist.CLIENT)
public class TabbedControl extends Control
{
    /**
     * The offset for the entire gui.
     */
    public static final int OFFSET_Y = -5;

    /**
     * The width of the gui from the edge of a selected left-hand tab to the edge of a selected right-hand tab.
     */
    public static final int GUI_WIDTH = 234;

    /**
     * The height of the gui from the top of the regular inventory texture to the bottom.
     */
    public static final int GUI_HEIGHT = 166;

    /**
     * The width of the regular inventory texture.
     */
    public static final int CONTENT_WIDTH = 176;

    /**
     * The height of the regular inventory texture.
     */
    public static final int CONTENT_HEIGHT = 166;

    /**
     * @param parent the parent control.
     * @param blockling the blockling.
     * @param x the x position.
     * @param y the y position.
     */
    public TabbedControl(@Nonnull IControl parent, @Nonnull BlocklingEntity blockling, int x, int y)
    {
        super(parent, x, y, GUI_WIDTH, GUI_HEIGHT);

        for (Tab tab : Tab.values())
        {
            new TabControl(this, tab, blockling, 0, 0, width);
        }
    }

    @Override
    public void controlMouseReleased(@Nonnull MouseButtonEvent e)
    {

    }

    /**
     * The control for the tabs along the edge of the gui.
     */
    public static class TabControl extends Control
    {
        /**
         * The width/height of a tab icon.
         */
        private static final int ICON_SIZE = 22;

        /**
         * The background texture for a selected tab on the left-hand side.
         */
        @Nonnull
        private static final GuiTexture SELECTED_BACKGROUND_TEXTURE_LEFT = new GuiTexture(GuiTextures.TABS, 52, 0, 32, 28);

        /**
         * The background texture for a selected tab on the right-hand side.
         */
        @Nonnull
        private static final GuiTexture SELECTED_BACKGROUND_TEXTURE_RIGHT = new GuiTexture(GuiTextures.TABS, 85, 0, 32, 28);

        /**
         * The background texture for an unselected tab on the left-hand side.
         */
        @Nonnull
        private static final GuiTexture UNSELECTED_BACKGROUND_TEXTURE_LEFT = new GuiTexture(GuiTextures.TABS, 0, 0, 25, 28);

        /**
         * The background texture for an unselected tab on the right-hand side.
         */
        @Nonnull
        private static final GuiTexture UNSELECTED_BACKGROUND_TEXTURE_RIGHT = new GuiTexture(GuiTextures.TABS, 26, 0, 25, 28);

        /**
         * The associated tab.
         */
        @Nonnull
        public final Tab tab;

        /**
         * The blockling.
         */
        @Nonnull
        private final BlocklingEntity blockling;

        /**
         * The background texture used when the tab is selected.
         */
        @Nonnull
        private final GuiTexture selectedBackgroundTexture;

        /**
         * The background texture used when the tab is unselected.
         */
        @Nonnull
        private final GuiTexture unselectedBackgroundTexture;

        /**
         * The texture used for the tab icon.
         */
        @Nonnull
        private final GuiTexture iconTexture;

        /**
         * @param parent the parent control.
         * @param tab the tab.
         * @param blockling the blockling.
         * @param left the x position at the left-hand side of the left-most tab.
         * @param top the y position at the top of the gui.
         * @param right the x position at the right-hand side of the right-most tab.
         */
        public TabControl(@Nonnull IControl parent, @Nonnull Tab tab, @Nonnull BlocklingEntity blockling, int left, int top, int right)
        {
            super(parent, tab.left ? left : right - SELECTED_BACKGROUND_TEXTURE_LEFT.width, top + tab.getIndex() * (SELECTED_BACKGROUND_TEXTURE_LEFT.height + 4) + 5, SELECTED_BACKGROUND_TEXTURE_LEFT.width, SELECTED_BACKGROUND_TEXTURE_LEFT.height);
            this.tab = tab;
            this.blockling = blockling;
            this.selectedBackgroundTexture = tab.left ? SELECTED_BACKGROUND_TEXTURE_LEFT : SELECTED_BACKGROUND_TEXTURE_RIGHT;
            this.unselectedBackgroundTexture = tab.left ? UNSELECTED_BACKGROUND_TEXTURE_LEFT : UNSELECTED_BACKGROUND_TEXTURE_RIGHT;
            this.iconTexture = new GuiTexture(GuiTextures.TABS, tab.getIndex() * ICON_SIZE, tab.left ? 28 : 28 + ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }

        @Override
        public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
        {
            RenderSystem.enableDepthTest();

            if (isSelected())
            {
                matrixStack.translate(0.0, 0.0, 5.0);

                renderTexture(matrixStack, selectedBackgroundTexture);
                renderTexture(matrixStack, tab.left ? 6 : 4, 3, iconTexture);
            }
            else
            {
                renderTexture(matrixStack, tab.left ? 4 : 3, 0, unselectedBackgroundTexture);
                renderTexture(matrixStack, tab.left ? 7 : 3, 3, iconTexture);
            }
        }

        @Override
        public void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
        {
            screen.renderTooltip(matrixStack, tab.name, mouseX, mouseY);
        }

        @Override
        public void controlMouseClicked(@Nonnull MouseButtonEvent e)
        {
            blockling.guiHandler.openGui(tab.guiId, screen.getMinecraft().player);

            e.setIsHandled(true);
        }

        /**
         * @return true if the current tab is selected.
         */
        public boolean isSelected()
        {
            return tab.guiId == blockling.guiHandler.getRecentGuiId();
        }
    }
}
