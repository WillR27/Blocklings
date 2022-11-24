package com.willr27.blocklings.client.gui.screen.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Side;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui.screen.BlocklingsScreen;
import com.willr27.blocklings.client.gui2.BlocklingGuiHandler;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.client.gui2.GuiTexture;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.EnumSet;

import static com.willr27.blocklings.client.gui.GuiTextures.Tabs.*;

/**
 * The screen to display the blockling's UI with the tabs.
 */
@OnlyIn(Dist.CLIENT)
public class TabbedScreen extends BlocklingsScreen
{
    /**
     * The blockling.
     */
    @Nonnull
    private final BlocklingEntity blockling;

    /**
     * The background texture to use.
     */
    @Nonnull
    private final GuiTexture backgroundTexture;

    /**
     * @param blockling the blockling.
     * @param backgroundTexture the background texture to use.
     */
    public TabbedScreen(@Nonnull BlocklingEntity blockling, @Nonnull GuiTexture backgroundTexture)
    {
        super();
        this.blockling = blockling;
        this.backgroundTexture = backgroundTexture;
    }

    @Override
    protected void init()
    {
        super.init();

        // The main area the UI + tabs are within.
        Control mainControl = new Control();
        mainControl.setParent(screenControl);
        mainControl.setWidth(234);
        mainControl.setHeight(166);
        mainControl.setPercentX(0.5f);
        mainControl.setPercentY(0.45f);
        mainControl.setAnchor(EnumSet.noneOf(Side.class));
        mainControl.setBackgroundColour(new Colour(1.0f, 0.0f, 0.0f));

        for (Tab tab : Tab.values())
        {
            new TabControl(tab, blockling, mainControl);
        }

        Control backgroundControl = new TexturedControl(backgroundTexture);
        backgroundControl.setParent(mainControl);
        backgroundControl.setPercentX(0.5f);
        backgroundControl.setPercentY(0.5f);
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
        protected void onRenderBackground(@Nonnull RenderArgs renderArgs)
        {
            MatrixStack matrixStack = renderArgs.matrixStack;

            if (isSelected())
            {
                matrixStack.translate(0.0, 0.0, 5.0);

                renderTexture(matrixStack, selectedBackgroundTexture);
//                renderTexture(matrixStack, tab.left ? 6 : 4, 3, iconTexture);
            }
            else
            {
                renderTexture(matrixStack, tab.left ? 4 : 3, 0, unselectedBackgroundTexture);
//                renderTexture(matrixStack, tab.left ? 7 : 3, 3, iconTexture);
            }
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
    enum Tab
    {
        STATS("stats", BlocklingGuiHandler.STATS_ID, true, 0),
        TASKS("tasks", BlocklingGuiHandler.TASKS_ID, true, 1),
        EQUIPMENT("equipment", BlocklingGuiHandler.EQUIPMENT_ID, true, 2),
        GENERAL("general", BlocklingGuiHandler.GENERAL_ID, false, 0),
        COMBAT("combat", BlocklingGuiHandler.COMBAT_ID, false, 1),
        MINING("mining", BlocklingGuiHandler.MINING_ID, false, 2),
        WOODCUTTING("woodcutting", BlocklingGuiHandler.WOODCUTTING_ID, false, 3),
        FARMING("farming", BlocklingGuiHandler.FARMING_ID, false, 4);

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
         * @param left whether the tab is on the left or right side.
         * @param verticalIndex the vertical index of the tab.
         */
        Tab(@Nonnull String key, int guiId, boolean left, int verticalIndex)
        {
            this.name = new TabTranslationTextComponent(key);
            this.guiId = guiId;
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
