package com.willr27.blocklings.client.gui.controls.stats;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.*;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A control used to cycle through a set of icons and stats.
 */
@OnlyIn(Dist.CLIENT)
public class EnumeratingStatControl extends Control
{
    /**
     * The size of each stat icon.
     */
    public static final int ICON_SIZE = 11;

    /**
     * The start position of the stat textures.
     */
    private static final int STAT_ICON_TEXTURE_Y = 166;

    /**
     * The x offset to render the text at.
     */
    private static final int TEXT_OFFSET_X = 4;

    /**
     * The y offset to render the text at.
     */
    private static final int TEXT_OFFSET_Y = 1;

    /**
     * The conditions that dictate whether an attribute should be shown.
     */
    @Nonnull
    protected final List<Supplier<Boolean>> conditionSuppliers = new ArrayList<>();

    /**
     * The suppliers that provide the value to render next to the icon.
     */
    @Nonnull
    protected final List<Supplier<String>> valueSuppliers = new ArrayList<>();

    /**
     * The suppliers that provide the tooltips for each attribute.
     */
    @Nonnull
    protected final List<Supplier<List<ITextComponent>>> tooltipSuppliers = new ArrayList<>();

    /**
     * The textures for each icon.
     */
    @Nonnull
    protected final List<GuiTexture> iconTextures = new ArrayList<>();

    /**
     * The colour tints to apply to each icon.
     */
    @Nonnull
    protected final List<Color> colours = new ArrayList<>();

    /**
     * The name for this set of stats.
     */
    @Nonnull
    protected final ITextComponent name;

    /**
     * Whether the text should be aligned right to left and on the left-hand side of the icon.
     */
    protected final boolean shouldRightAlignText;

    /**
     * The interval between each stat.
     */
    protected final int enumerationInterval;

    /**
     * Whether to combine all the tooltips into a single large tooltip.
     */
    protected final boolean shouldCombineTooltips;

    /**
     * The blockling.
     */
    @Nonnull
    protected final BlocklingEntity blockling;

    /**
     * The tick count used to check whether to switch stats.
     */
    protected int tickCount = 0;

    /**
     * The current stat to render.
     */
    protected int currentEnumeration = 0;

    /**
     * @param parent the parent control.
     * @param name the name for the set of stats.
     * @param x the x position.
     * @param y the y position.
     * @param shouldRightAlignText whether the text should be aligned right to left and on the left-hand side of the icon.
     * @param enumerationInterval the interval between each stat.
     * @param shouldCombineTooltips whether to combine all the tooltips into a single large tooltip.
     * @param blockling the blockling.
     */
    public EnumeratingStatControl(@Nonnull IControl parent, @Nonnull ITextComponent name, int x, int y, boolean shouldRightAlignText, int enumerationInterval, boolean shouldCombineTooltips, @Nonnull BlocklingEntity blockling)
    {
        super(parent, x, y, ICON_SIZE, ICON_SIZE);
        this.name = name;
        this.shouldRightAlignText = shouldRightAlignText;
        this.enumerationInterval = enumerationInterval;
        this.shouldCombineTooltips = shouldCombineTooltips;
        this.blockling = blockling;
    }

    /**
     * Adds a stat to the control.
     *
     * @param conditionSupplier whether to display the stat or not.
     * @param valueSupplier the value to display.
     * @param tooltipSupplier the tooltip for this stat.
     * @param iconIndex the x index of the icon's texture.
     */
    public void addStat(@Nonnull Supplier<Boolean> conditionSupplier, @Nonnull Supplier<String> valueSupplier, @Nonnull Supplier<List<ITextComponent>> tooltipSupplier, int iconIndex)
    {
        addStat(conditionSupplier, valueSupplier, tooltipSupplier, iconIndex, Color.WHITE);
    }

    /**
     * Adds a stat to the control.
     *
     * @param conditionSupplier whether to display the stat or not.
     * @param valueSupplier the value to display.
     * @param tooltipSupplier the tooltip for this stat.
     * @param iconIndex the x index of the icon's texture.
     * @param colour the colour to tint the icon.
     */
    public void addStat(@Nonnull Supplier<Boolean> conditionSupplier, @Nonnull Supplier<String> valueSupplier, @Nonnull Supplier<List<ITextComponent>> tooltipSupplier, int iconIndex, @Nonnull Color colour)
    {
        conditionSuppliers.add(conditionSupplier);
        valueSuppliers.add(valueSupplier);
        tooltipSuppliers.add(tooltipSupplier);
        iconTextures.add(new GuiTexture(GuiTextures.STATS, ICON_SIZE * iconIndex, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));
        colours.add(colour);
        currentEnumeration = iconTextures.size() - 1;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (blockling.tickCount - tickCount > enumerationInterval)
        {
            tickCount = blockling.tickCount;
            currentEnumeration = (currentEnumeration + 1) % iconTextures.size();
        }

        while (!conditionSuppliers.get(currentEnumeration).get())
        {
            currentEnumeration = (currentEnumeration + 1) % iconTextures.size();
        }

        RenderSystem.color3f(colours.get(currentEnumeration).getRed() / 255.0f, colours.get(currentEnumeration).getGreen() / 255.0f, colours.get(currentEnumeration).getBlue() / 255.0f);
        renderTexture(matrixStack, iconTextures.get(currentEnumeration));
        renderShadowedText(matrixStack, valueSuppliers.get(currentEnumeration).get(), width + TEXT_OFFSET_X, TEXT_OFFSET_Y, shouldRightAlignText, 0xffe100);
    }

    @Override
    public void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (shouldCombineTooltips)
        {
            screen.renderTooltip(matrixStack, prependNameToTooltip(combineTooltips()).stream().map(t -> t.getVisualOrderText()).collect(Collectors.toList()), mouseX, mouseY);
        }
        else
        {
            screen.renderTooltip(matrixStack, prependNameToTooltip(tooltipSuppliers.get(currentEnumeration).get()).stream().map(t -> t.getVisualOrderText()).collect(Collectors.toList()), mouseX, mouseY);
        }
    }

    private List<ITextComponent> prependNameToTooltip(@Nonnull List<ITextComponent> tooltip)
    {
//            tooltip.add(0, new StringTextComponent("").getVisualOrderText());
        tooltip.add(0, name);

        return tooltip;
    }

    @Nonnull
    private List<ITextComponent> combineTooltips()
    {
        List<ITextComponent> tooltip = new ArrayList<>();

        for (int i = 0; i < conditionSuppliers.size(); i++)
        {
            if (!conditionSuppliers.get(i).get())
            {
                continue;
            }

            List<ITextComponent> subTooltip = tooltipSuppliers.get(i).get();

            if (i == currentEnumeration)
            {
                subTooltip.set(0, new StringTextComponent(subTooltip.get(0).getString().substring(0, 2) + TextFormatting.ITALIC + subTooltip.get(0).getString().substring(2)));
            }

            if (GuiUtil.isKeyDown(Minecraft.getInstance().options.keyShift.getKey().getValue()))
            {
                tooltip.addAll(subTooltip);
            }
            else
            {
                tooltip.add(subTooltip.get(0));
            }
        }

        if (!GuiUtil.isKeyDown(Minecraft.getInstance().options.keyShift.getKey().getValue()))
        {
            tooltip.add(new StringTextComponent(TextFormatting.DARK_GRAY + "" + TextFormatting.ITALIC + new BlocklingsTranslationTextComponent("gui.more_info", Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage().getString()).getString()));
        }

        return tooltip;
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY)
    {
        if (shouldRightAlignText)
        {
            return GuiUtil.isMouseOver(mouseX, mouseY, screenX - (font.width(valueSuppliers.get(currentEnumeration).get()) + TEXT_OFFSET_X + 3), screenY, getScreenWidth() + font.width(valueSuppliers.get(currentEnumeration).get()) + TEXT_OFFSET_X + 3, getScreenHeight());
        }
        else
        {
            return GuiUtil.isMouseOver(mouseX, mouseY, screenX, screenY, getScreenWidth() + font.width(valueSuppliers.get(currentEnumeration).get()) + TEXT_OFFSET_X + 3, getScreenHeight());
        }
    }

    @Override
    public void controlMouseClicked(@Nonnull MouseButtonEvent e)
    {
        tickCount = blockling.tickCount;
        currentEnumeration = (currentEnumeration + 1) % iconTextures.size();

        while (!conditionSuppliers.get(currentEnumeration).get())
        {
            currentEnumeration = (currentEnumeration + 1) % iconTextures.size();
        }

        e.setIsHandled(true);
    }
}
