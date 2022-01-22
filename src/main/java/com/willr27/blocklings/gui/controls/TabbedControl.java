package com.willr27.blocklings.gui.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.Tab;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * The control for the tabs in the blockling's gui.
 * This is a control instead of a screen, so it can be used with containers screens as well as regular screens.
 */
@OnlyIn(Dist.CLIENT)
public class TabbedControl extends AbstractGui
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
     * The tab controls.
     */
    @Nonnull
    private final List<TabControl> tabControls = new ArrayList<>();

    /**
     * @param blockling the blockling.
     * @param centerX the x position at the center of the screen.
     * @param centerY the y position at the center of the screen.
     */
    public TabbedControl(@Nonnull BlocklingEntity blockling, int centerX, int centerY)
    {
        this.left = centerX - GUI_WIDTH / 2;
        this.top = centerY - GUI_HEIGHT / 2;
        this.right = left + GUI_WIDTH;

        for (Tab tab : Tab.values())
        {
            tabControls.add(new TabControl(tab, blockling, left, top, right));
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
        tabControls.forEach(tabControl -> tabControl.render(matrixStack, mouseX, mouseY));
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

        for (TabControl tabControl : tabControls)
        {
            if (tabControl.mouseClicked(mouseX, mouseY, state))
            {
                result = true;
            }
        }

        return result;
    }

    /**
     * The control for the tabs along the edge of the gui.
     */
    private static class TabControl extends Control
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
        private final Tab tab;

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
         * @param tab the tab.
         * @param blockling the blockling.
         * @param left the x position at the left-hand side of the tab.
         * @param top the y position at the top of the gui.
         * @param right the x position at the right-hand side of the tab.
         */
        public TabControl(@Nonnull Tab tab, @Nonnull BlocklingEntity blockling, int left, int top, int right)
        {
            super(tab.left ? left : right - SELECTED_BACKGROUND_TEXTURE_LEFT.width, top + tab.getIndex() * (SELECTED_BACKGROUND_TEXTURE_LEFT.height + 4) + 5, SELECTED_BACKGROUND_TEXTURE_LEFT.width, SELECTED_BACKGROUND_TEXTURE_LEFT.height);
            this.tab = tab;
            this.blockling = blockling;
            this.selectedBackgroundTexture = tab.left ? SELECTED_BACKGROUND_TEXTURE_LEFT : SELECTED_BACKGROUND_TEXTURE_RIGHT;
            this.unselectedBackgroundTexture = tab.left ? UNSELECTED_BACKGROUND_TEXTURE_LEFT : UNSELECTED_BACKGROUND_TEXTURE_RIGHT;
            this.iconTexture = new GuiTexture(GuiTextures.TABS, tab.getIndex() * ICON_SIZE, tab.left ? 28 : 28 + ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }

        @Override
        public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
        {
            if (isSelected())
            {
                renderTexture(matrixStack, selectedBackgroundTexture);
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
