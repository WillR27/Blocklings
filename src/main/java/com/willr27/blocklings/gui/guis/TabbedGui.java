package com.willr27.blocklings.gui.guis;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.Tab;
import com.willr27.blocklings.gui.widgets.TexturedWidget;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * The gui for the tabs in the blockling's gui.
 * This is a gui instead of a screen, so it can be used with containers screens as well as regular screens.
 */
@OnlyIn(Dist.CLIENT)
public class TabbedGui extends AbstractGui
{
    /**
     * The offset for the entire gui.
     */
    public static final int OFFSET_Y = -10;

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
     * The x position at the left-hand side of the tabs.
     */
    private int left;

    /**
     * The y position at the top of the gui.
     */
    private int top;

    /**
     * The x position at the right-hand side of the tabs.
     */
    private int right;

    /**
     * The tab widgets.
     */
    @Nonnull
    private final List<TabWidget> tabWidgets = new ArrayList<>();

    /**
     * @param blockling the blockling.
     * @param centerX the x position at the center of the screen.
     * @param centerY the y position at the center of the screen.
     */
    public TabbedGui(@Nonnull BlocklingEntity blockling, int centerX, int centerY)
    {
        this.left = centerX - GUI_WIDTH / 2;
        this.top = centerY - GUI_HEIGHT / 2;
        this.right = left + GUI_WIDTH;

        for (Tab tab : Tab.values())
        {
            tabWidgets.add(new TabWidget(tab, blockling, left, top, right));
        }
    }

    /**
     * Renders the tabs.
     *
     * @param matrixStack the matrix stack.
     * @param mouseX the current mouse x position.
     * @param mouseY the current mouse y position.
     */
    public void render(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        tabWidgets.forEach(tabWidget -> tabWidget.render(matrixStack, mouseX, mouseY));
    }

    /**
     * Handles the mouse being clicked anywhere on the screen.
     *
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     * @param state the mouse state.
     * @return true if the click is handled.
     */
    public boolean mouseClicked(int mouseX, int mouseY, int state)
    {
        boolean result = false;

        for (TabWidget tabWidget : tabWidgets)
        {
            if (tabWidget.mouseClicked(mouseX, mouseY, state))
            {
                result = true;
            }
        }

        return result;
    }

    /**
     * The widget for the tabs along the edge of the gui.
     */
    private static class TabWidget extends TexturedWidget
    {
        /**
         * The width/height of a tab icon.
         */
        private static final int ICON_SIZE = 22;

        /**
         * The background texture for a selected tab on the left-hand side.
         */
        private static final GuiTexture SELECTED_BACKGROUND_TEXTURE_LEFT = new GuiTexture(GuiTextures.TABS, 52, 0, 32, 28);

        /**
         * The background texture for a selected tab on the right-hand side.
         */
        private static final GuiTexture SELECTED_BACKGROUND_TEXTURE_RIGHT = new GuiTexture(GuiTextures.TABS, 85, 0, 32, 28);

        /**
         * The background texture for an unselected tab on the left-hand side.
         */
        private static final GuiTexture UNSELECTED_BACKGROUND_TEXTURE_LEFT = new GuiTexture(GuiTextures.TABS, 0, 0, 25, 28);

        /**
         * The background texture for an unselected tab on the right-hand side.
         */
        private static final GuiTexture UNSELECTED_BACKGROUND_TEXTURE_RIGHT = new GuiTexture(GuiTextures.TABS, 26, 0, 25, 28);

        /**
         * The associated tab.
         */
        private final Tab tab;

        /**
         * The blockling.
         */
        private final BlocklingEntity blockling;

        /**
         * The background texture used when the tab is unselected.
         */
        private final GuiTexture unselectedBackgroundTexture;

        /**
         * The texture used for the tab icon.
         */
        private final GuiTexture iconTexture;

        /**
         * @param tab the tab.
         * @param blockling the blockling.
         * @param left the x position at the left-hand side of the tab.
         * @param top the y position at the top of the gui.
         * @param right the x position at the right-hand side of the tab.
         */
        public TabWidget(@Nonnull Tab tab, @Nonnull BlocklingEntity blockling, int left, int top, int right)
        {
            super(tab.left ? left : right - SELECTED_BACKGROUND_TEXTURE_LEFT.width, top + tab.getIndex() * (SELECTED_BACKGROUND_TEXTURE_LEFT.height + 4) + 5, tab.left ? SELECTED_BACKGROUND_TEXTURE_LEFT : SELECTED_BACKGROUND_TEXTURE_RIGHT);
            this.tab = tab;
            this.blockling = blockling;
            this.unselectedBackgroundTexture = tab.left ? UNSELECTED_BACKGROUND_TEXTURE_LEFT : UNSELECTED_BACKGROUND_TEXTURE_RIGHT;
            this.iconTexture = new GuiTexture(GuiTextures.TABS, tab.getIndex() * ICON_SIZE, tab.left ? 28 : 28 + ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }

        @Override
        public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
        {
            if (isSelected())
            {
                super.render(matrixStack, mouseX, mouseY);
                renderTexture(matrixStack, tab.left ? 6 : 4, 3, iconTexture);
            }
            else
            {
                renderTexture(matrixStack, tab.left ? 5 : 3, 0, unselectedBackgroundTexture);
                renderTexture(matrixStack, tab.left ? 8 : 3, 3, iconTexture);
            }

            if (isMouseOver(mouseX, mouseY))
            {
                screen.renderTooltip(matrixStack, tab.name, mouseX, mouseY);
            }
        }

        @Override
        public boolean mouseClicked(int mouseX, int mouseY, int button)
        {
            if (isMouseOver(mouseX, mouseY))
            {
                blockling.guiHandler.openGui(tab.guiId, screen.getMinecraft().player);

                return true;
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        /**
         * @return true if the current tab is selected.
         */
        private boolean isSelected()
        {
            return tab.guiId == blockling.guiHandler.getRecentGuiId();
        }
    }
}
