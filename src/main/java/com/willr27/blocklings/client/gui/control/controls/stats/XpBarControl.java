package com.willr27.blocklings.client.gui.control.controls.stats;

import com.willr27.blocklings.client.gui.GuiTextures;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.Alignment;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.Direction;
import com.willr27.blocklings.client.gui.control.Orientation;
import com.willr27.blocklings.client.gui.control.controls.TextBlockControl;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui2.GuiTexture;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.attribute.Attribute;
import com.willr27.blocklings.entity.blockling.attribute.BlocklingAttributes;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays a blockling's xp bar.
 */
@OnlyIn(Dist.CLIENT)
public class XpBarControl extends Control
{
    /**
     * The blockling.
     */
    @Nonnull
    private final BlocklingEntity blockling;

    /**
     * The level.
     */
    @Nonnull
    private final BlocklingAttributes.Level level;

    /**
     * @param blockling the blockling.
     * @param level     the level.
     */
    public XpBarControl(@Nonnull BlocklingEntity blockling, @Nonnull BlocklingAttributes.Level level)
    {
        super();
        this.blockling = blockling;
        this.level = level;

        setFitToContentsXY(true);

        FlowPanel flowPanel = new FlowPanel();
        flowPanel.setParent(this);
        flowPanel.setFitToContentsXY(true);
        flowPanel.setFlowDirection(Direction.LEFT_TO_RIGHT);
        flowPanel.setOverflowOrientation(Orientation.HORIZONTAL);
        flowPanel.setItemGapX(5);
        flowPanel.setInteractive(false);

        LevelIconControl leftIconControl = new LevelIconControl(true);
        leftIconControl.setParent(flowPanel);

        Control xpBarControl = new Control();
        xpBarControl.setParent(flowPanel);
        xpBarControl.setFitToContentsXY(true);
        xpBarControl.setAlignmentY(new Alignment(0.5f));

        TexturedControl xpBarBackgroundControl = new TexturedControl(level.getXpBarBackgroundTexture());
        xpBarBackgroundControl.setParent(xpBarControl);

        TexturedControl xpBarForegroundControl = new TexturedControl(level.getXpBarForegroundTexture())
        {
            @Override
            public void onTick()
            {
                setWidth(getTexture().width * getXpPercentage());
            }
        };
        xpBarForegroundControl.setParent(xpBarControl);
        xpBarForegroundControl.onTick();

        LevelIconControl rightIconControl = new LevelIconControl(false);
        rightIconControl.setParent(flowPanel);

        TextBlockControl levelTextControl = new TextBlockControl()
        {
            @Override
            public void onTick()
            {
                setText(String.valueOf(blockling.getStats().getLevelAttribute(level).getValue()));
            }
        };
        levelTextControl.setParent(this);
        levelTextControl.setFitToContentsXY(true);
        levelTextControl.useDescenderlessLineHeight();
        levelTextControl.setTextColour(level.getLevelColour());
        levelTextControl.setAlignmentX(new Alignment(0.5f));
        levelTextControl.setAlignmentY(new Alignment(0.5f));
        levelTextControl.setInteractive(false);
        levelTextControl.onTick();
    }

    @Override
    public void onRenderTooltip(@Nonnull RenderArgs renderArgs)
    {
        Attribute<Integer> level = blockling.getStats().getLevelAttribute(this.level);

        List<IReorderingProcessor> tooltip = new ArrayList<>();
        tooltip.add(new StringTextComponent(TextFormatting.GOLD + blockling.getStats().getLevelAttribute(this.level).displayStringNameSupplier.get()).getVisualOrderText());
        tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("gui.current_level", TextFormatting.WHITE, level.getValue()).getString()).getVisualOrderText());
        tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("gui.xp_required", TextFormatting.WHITE, blockling.getStats().getLevelXpAttribute(this.level).getValue(), BlocklingAttributes.getXpForLevel(level.getValue())).getString()).getVisualOrderText());

        renderTooltip(renderArgs, tooltip);
    }

    /**
     * @return the percentage of the way to the next level.
     */
    private float getXpPercentage()
    {
        return Math.min(1.0f, (float) blockling.getStats().getLevelXpAttribute(level).getValue() / BlocklingAttributes.getXpForLevel(blockling.getStats().getLevelAttribute(level).getValue()));
    }

    /**
     * Displays an icon for a level.
     */
    private class LevelIconControl extends Control
    {
        /**
         * Whether the icon represents the current or next level.
         */
        private final boolean current;

        /**
         * The icons texture.
         */
        @Nonnull
        private final GuiTexture iconsTexture = level.getLevelIconsTexture();

        /**
         * @param current whether the icon represents the current or next level.
         */
        public LevelIconControl(boolean current)
        {
            super();
            this.current = current;

            setWidth(GuiTextures.Stats.LevelIconsTexture.ICON_SIZE);
            setHeight(GuiTextures.Stats.LevelIconsTexture.ICON_SIZE);
        }

        @Override
        public void onRender(@Nonnull RenderArgs renderArgs)
        {
            int iconToRender = (int) (((float) (blockling.getStats().getLevelAttribute(level).getValue() + (current ? 0 : 1)) / BlocklingAttributes.Level.MAX) * (GuiTextures.Stats.LevelIconsTexture.NUMBER_OF_ICONS - 1));

            int iconSize = GuiTextures.Stats.LevelIconsTexture.ICON_SIZE;
            renderTexture(renderArgs.matrixStack, iconsTexture.shift(iconToRender * iconSize, 0).width(iconSize));
        }
    }
}
