package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.panels.GridPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.StackPanel;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseClickedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseScrolledEvent;
import com.willr27.blocklings.client.gui.properties.GridDefinition;
import com.willr27.blocklings.client.gui.texture.Texture;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.client.gui.BlocklingGuiHandler;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

import static com.willr27.blocklings.client.gui.texture.Textures.Tabs.*;

/**
 * A control for all tabbed blockling controls.
 */
@OnlyIn(Dist.CLIENT)
public class TabbedUIControl extends GridPanel
{
    /**
     * The associated blockling.
     */
    @Nonnull
    private final BlocklingEntity blockling;

    /**
     * The selected tab.
     */
    @Nonnull
    private final Tab selectedTab;

    /**
     * The container control for the content in the middle of the UI.
     */
    @Nonnull
    public final Control contentControl;

    /**
     * The background control.
     */
    @Nonnull
    public final TexturedControl backgroundControl;

    /**
     * @param blockling the blockling.
     * @param selectedTab the selected tab.
     */
    public TabbedUIControl(@Nonnull BlocklingEntity blockling, @Nonnull Tab selectedTab)
    {
        super();
        this.blockling = blockling;
        this.selectedTab = selectedTab;

        setWidth(234);
        setHeight(186);
        setHorizontalAlignment(0.5);
        setVerticalAlignment(0.41);
        setShouldSnapToScreenCoords(true);
        addRowDefinition(GridDefinition.FIXED, 20.0);
        addRowDefinition(GridDefinition.RATIO, 1.0);
        addColumnDefinition(GridDefinition.RATIO, 1.0);
        setDebugName("Tabbed UI Control");

        TextBlockControl titleControl = new TextBlockControl();
        addChild(titleControl, 0, 0);
        titleControl.setText(selectedTab.name);
        titleControl.setFitWidthToContent(true);
        titleControl.setHorizontalAlignment(0.5);
        titleControl.setVerticalAlignment(0.5);

        backgroundControl = new TexturedControl(selectedTab.backgroundTexture);
        addChild(backgroundControl, 1, 0);
        backgroundControl.setHorizontalAlignment(0.5);
        backgroundControl.setVerticalAlignment(1.0);
        backgroundControl.setInteractive(false);

        StackPanel leftTabsPanel = new StackPanel();
        addChild(leftTabsPanel, 1, 0);
        leftTabsPanel.setWidth(32);
        leftTabsPanel.setHeight(156.0);
        leftTabsPanel.setVerticalAlignment(0.5);
        leftTabsPanel.setSpacing(4.0);
        leftTabsPanel.setHorizontalContentAlignment(1.0);

        StackPanel rightTabsPanel = new StackPanel();
        addChild(rightTabsPanel, 1, 0);
        rightTabsPanel.setWidth(32);
        rightTabsPanel.setHeight(156.0);
        rightTabsPanel.setHorizontalAlignment(1.0);
        rightTabsPanel.setVerticalAlignment(0.5);
        rightTabsPanel.setSpacing(4.0);
        rightTabsPanel.setHorizontalContentAlignment(0.0);

        for (Tab tab : Tab.values())
        {
            if (tab.left)
            {
                leftTabsPanel.addChild(new TabControl(tab, blockling));
            }
            else
            {
                rightTabsPanel.addChild(new TabControl(tab, blockling));
            }
        }

        contentControl = new Control()
        {
            @Override
            protected void onMouseClicked(@Nonnull MouseClickedEvent e)
            {

            }

            @Override
            protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
            {

            }

            @Override
            public void onMouseScrolled(@Nonnull MouseScrolledEvent e)
            {

            }
        };
        addChild(contentControl, 1, 0);
        contentControl.setWidth(160);
        contentControl.setHeight(150);
        contentControl.setHorizontalAlignment(0.5);
        contentControl.setVerticalAlignment(1.0);
        contentControl.setMarginBottom(8.0);
    }

    @Override
    protected void onMouseClicked(@Nonnull MouseClickedEvent e)
    {

    }

    @Override
    protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
    {

    }

    /**
     * A control for a tab.
     */
    private static class TabControl extends Control
    {
        /**
         * The associated tab.
         */
        @Nonnull
        private final TabbedUIControl.Tab tab;

        /**
         * The blockling.
         */
        @Nonnull
        private final BlocklingEntity blockling;

        /**
         * The background texture used when the tab is selected.
         */
        @Nonnull
        private final Texture selectedBackgroundTexture;

        /**
         * The background texture used when the tab is unselected.
         */
        @Nonnull
        private final Texture unselectedBackgroundTexture;

        /**
         * @param tab the associated tab.
         * @param blockling the blockling.
         */
        public TabControl(@Nonnull Tab tab, @Nonnull BlocklingEntity blockling)
        {
            super();
            this.tab = tab;
            this.blockling = blockling;
            this.selectedBackgroundTexture = tab.left ? SELECTED_BACKGROUND_TEXTURE_LEFT : SELECTED_BACKGROUND_TEXTURE_RIGHT;
            this.unselectedBackgroundTexture = tab.left ? UNSELECTED_BACKGROUND_TEXTURE_LEFT : UNSELECTED_BACKGROUND_TEXTURE_RIGHT;

            setWidth(SELECTED_BACKGROUND_TEXTURE_LEFT.width);
            setHeight(SELECTED_BACKGROUND_TEXTURE_LEFT.height);
        }

        @Override
        protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
        {
            matrixStack.pushPose();

            if (isSelected())
            {
                matrixStack.translate(0.0, 0.0, 0.5);

                renderTextureAsBackground(matrixStack, selectedBackgroundTexture);
                renderTextureAsBackground(matrixStack, tab.iconTexture, tab.left ? 6 : 4, 3);
            }
            else
            {
                renderTextureAsBackground(matrixStack, unselectedBackgroundTexture, tab.left ? 4 : 3, 0);
                renderTextureAsBackground(matrixStack, tab.iconTexture, tab.left ? 7 : 3, 3);
            }

            matrixStack.popPose();
        }

        @Override
        public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
        {
            renderTooltip(matrixStack, mouseX, mouseY, getPixelScaleX(), getPixelScaleY(), tab.name);
        }

        @Override
        protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
        {
            if (isPressed())
            {
                blockling.guiHandler.openGui(tab.guiId, Minecraft.getInstance().player);
            }

            e.setIsHandled(true);
        }

        /**
         * @return whether the current tab is selected.
         */
        public boolean isSelected()
        {
            return tab.guiId == blockling.guiHandler.getRecentGuiId();
        }
    }

    /**
     * An enum representing each possible tab.
     */
    public enum Tab
    {
        STATS("stats", BlocklingGuiHandler.STATS_ID, Textures.Tabs.STATS, Textures.Stats.BACKGROUND, true, 0),
        TASKS("tasks", BlocklingGuiHandler.TASKS_ID, Textures.Tabs.TASKS, Textures.Tasks.BACKGROUND, true, 1),
        EQUIPMENT("equipment", BlocklingGuiHandler.EQUIPMENT_ID, Textures.Tabs.EQUIPMENT, Textures.Equipment.BACKGROUND, true, 2),
        GENERAL("general", BlocklingGuiHandler.GENERAL_ID, Textures.Tabs.GENERAL, Textures.Skills.BACKGROUND, false, 0),
        COMBAT("combat", BlocklingGuiHandler.COMBAT_ID, Textures.Tabs.COMBAT, Textures.Skills.BACKGROUND, false, 1),
        MINING("mining", BlocklingGuiHandler.MINING_ID, Textures.Tabs.MINING, Textures.Skills.BACKGROUND, false, 2),
        WOODCUTTING("woodcutting", BlocklingGuiHandler.WOODCUTTING_ID, Textures.Tabs.WOODCUTTING, Textures.Skills.BACKGROUND, false, 3),
        FARMING("farming", BlocklingGuiHandler.FARMING_ID, Textures.Tabs.FARMING, Textures.Skills.BACKGROUND, false, 4);

        /**
         * The tab name.
         */
        @Nonnull
        public final TranslationTextComponent name;

        /**
         * The gui id for the tab.
         */
        public final int guiId;

        /**
         * The texture of the tab's icon.
         */
        public final Texture iconTexture;

        /**
         * The texture of the tab's background.
         */
        public final Texture backgroundTexture;

        /**
         * Whether the tab is on the left or right side of the screen.
         */
        public final boolean left;

        /**
         * The vertical index of the tab for the side it's on where 0 is at the top.
         */
        public final int verticalIndex;

        /**
         * @param key the key for the translation text component.
         * @param guiId the gui id.
         * @param iconTexture the icon texture.
         * @param backgroundTexture the background texture.
         * @param left whether the tab is on the left or right side.
         * @param verticalIndex the vertical index of the tab.
         */
        Tab(@Nonnull String key, int guiId, @Nonnull Texture iconTexture, @Nonnull Texture backgroundTexture, boolean left, int verticalIndex)
        {
            this.name = new TabTranslationTextComponent(key);
            this.guiId = guiId;
            this.iconTexture = iconTexture;
            this.backgroundTexture = backgroundTexture;
            this.left = left;
            this.verticalIndex = verticalIndex;
        }

        /**
         * A translation text component for a tab.
         */
        public class TabTranslationTextComponent extends BlocklingsTranslationTextComponent
        {
            /**
             * @param key the key for the translation text component.
             */
            public TabTranslationTextComponent(@Nonnull String key)
            {
                super("tab." + key);
            }
        }
    }
}
