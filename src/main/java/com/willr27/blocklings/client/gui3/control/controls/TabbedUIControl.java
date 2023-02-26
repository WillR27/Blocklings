package com.willr27.blocklings.client.gui3.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui3.GuiTextures;
import com.willr27.blocklings.client.gui3.RenderArgs;
import com.willr27.blocklings.client.gui3.control.Control;
import com.willr27.blocklings.client.gui3.control.Side;
import com.willr27.blocklings.client.gui3.control.event.events.input.MouseButtonEvent;
import com.willr27.blocklings.client.gui2.BlocklingGuiHandler;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.client.gui2.GuiTexture;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.EnumSet;

import static com.willr27.blocklings.client.gui3.GuiTextures.Tabs.*;

/**
 * The screen to display the blockling's UI with the tabs.
 */
@OnlyIn(Dist.CLIENT)
public class TabbedUIControl extends Control
{
    /**
     * The blockling.
     */
    @Nonnull
    public final BlocklingEntity blockling;

    /**
     * The selected tab.
     */
    @Nonnull
    public final Tab selectedTab;

    /**
     * The background texture control.
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

        backgroundControl = new TexturedControl(selectedTab.backgroundTexture);

        // The main area the UI + tabs are within.
        setWidth(234);
        setHeight(166);
        setAnchor(EnumSet.noneOf(Side.class));
        setBackgroundColour(new Colour(1.0f, 0.0f, 0.0f));
    }

    /**
     * Resets the {@link TabbedUIControl} children.
     */
    public void resetChildren()
    {
        getChildrenCopy().forEach(child -> removeChild(child));

        for (Tab tab : Tab.values())
        {
            new TabControl(tab, blockling, this);
        }

        backgroundControl.setParent(this);
        backgroundControl.setPercentX(0.5f);
        backgroundControl.setPercentY(0.5f);
        backgroundControl.setInteractive(false);
    }

    @Override
    protected void onMouseClicked(@Nonnull MouseButtonEvent mouseButtonEvent)
    {

    }

    @Override
    protected void onMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
    {

    }

    /**
     * A tab along the edge of blockling's UI.
     */
    private class TabControl extends Control
    {
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
         * @param tab the associated tab.
         * @param blockling the blockling.
         * @param mainControl the main parent control.
         */
        public TabControl(@Nonnull Tab tab, @Nonnull BlocklingEntity blockling, @Nonnull Control mainControl)
        {
            super();
            this.tab = tab;
            this.blockling = blockling;
            this.selectedBackgroundTexture = tab.left ? SELECTED_BACKGROUND_TEXTURE_LEFT : SELECTED_BACKGROUND_TEXTURE_RIGHT;
            this.unselectedBackgroundTexture = tab.left ? UNSELECTED_BACKGROUND_TEXTURE_LEFT : UNSELECTED_BACKGROUND_TEXTURE_RIGHT;

            setParent(mainControl);
            setWidth(SELECTED_BACKGROUND_TEXTURE_LEFT.width);
            setHeight(SELECTED_BACKGROUND_TEXTURE_LEFT.height);

            if (tab.left)
            {
                setPercentX(0.0f);
            }
            else
            {
                setPercentX(1.0f);
            }

            setY(tab.verticalIndex * (getHeight() + 4) + 5);
        }

        @Override
        public void onRenderBackground(@Nonnull RenderArgs renderArgs)
        {
            MatrixStack matrixStack = renderArgs.matrixStack;

            if (isSelected())
            {
                matrixStack.translate(0.0, 0.0, 5.0);

                renderTexture(matrixStack, selectedBackgroundTexture);
                renderTexture(matrixStack, tab.left ? 6 : 4, 3, tab.iconTexture);
            }
            else
            {
                renderTexture(matrixStack, tab.left ? 4 : 3, 0, unselectedBackgroundTexture);
                renderTexture(matrixStack, tab.left ? 7 : 3, 3, tab.iconTexture);
            }
        }

        @Override
        public void onRenderTooltip(@Nonnull RenderArgs renderArgs)
        {
            renderTooltip(renderArgs, tab.name);
        }

        @Override
        protected void onMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
        {
            blockling.guiHandler.openGui(tab.guiId, Minecraft.getInstance().player);

            mouseButtonEvent.setIsHandled(true);
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
        STATS("stats", BlocklingGuiHandler.STATS_ID, GuiTextures.Tabs.STATS, GuiTextures.Stats.BACKGROUND, true, 0),
        TASKS("tasks", BlocklingGuiHandler.TASKS_ID, GuiTextures.Tabs.TASKS, GuiTextures.Tasks.BACKGROUND, true, 1),
        EQUIPMENT("equipment", BlocklingGuiHandler.EQUIPMENT_ID, GuiTextures.Tabs.EQUIPMENT, GuiTextures.Equipment.BACKGROUND, true, 2),
        GENERAL("general", BlocklingGuiHandler.GENERAL_ID, GuiTextures.Tabs.GENERAL, GuiTextures.Skills.BACKGROUND, false, 0),
        COMBAT("combat", BlocklingGuiHandler.COMBAT_ID, GuiTextures.Tabs.COMBAT, GuiTextures.Skills.BACKGROUND, false, 1),
        MINING("mining", BlocklingGuiHandler.MINING_ID, GuiTextures.Tabs.MINING, GuiTextures.Skills.BACKGROUND, false, 2),
        WOODCUTTING("woodcutting", BlocklingGuiHandler.WOODCUTTING_ID, GuiTextures.Tabs.WOODCUTTING, GuiTextures.Skills.BACKGROUND, false, 3),
        FARMING("farming", BlocklingGuiHandler.FARMING_ID, GuiTextures.Tabs.FARMING, GuiTextures.Skills.BACKGROUND, false, 4);

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
        public final GuiTexture iconTexture;

        /**
         * The texture of the tab's background.
         */
        public final GuiTexture backgroundTexture;

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
        Tab(@Nonnull String key, int guiId, @Nonnull GuiTexture iconTexture, @Nonnull GuiTexture backgroundTexture, boolean left, int verticalIndex)
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
