package com.willr27.blocklings.client.gui.screen.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.EntityControl;
import com.willr27.blocklings.client.gui.control.controls.EnumeratingControl;
import com.willr27.blocklings.client.gui.control.controls.TabbedUIControl;
import com.willr27.blocklings.client.gui.control.controls.TextFieldControl;
import com.willr27.blocklings.client.gui.control.controls.panels.GridPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.StackPanel;
import com.willr27.blocklings.client.gui.control.controls.stats.EnumeratingStatControl;
import com.willr27.blocklings.client.gui.control.controls.stats.HealthBarControl;
import com.willr27.blocklings.client.gui.control.controls.stats.StatControl;
import com.willr27.blocklings.client.gui.control.controls.stats.XpBarControl;
import com.willr27.blocklings.client.gui.control.event.events.FocusChangedEvent;
import com.willr27.blocklings.client.gui.properties.Direction;
import com.willr27.blocklings.client.gui.properties.GridDefinition;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.BlocklingHand;
import com.willr27.blocklings.entity.blockling.attribute.BlocklingAttributes;
import com.willr27.blocklings.entity.blockling.attribute.IModifiable;
import com.willr27.blocklings.entity.blockling.attribute.IModifier;
import com.willr27.blocklings.entity.blockling.attribute.Operation;
import com.willr27.blocklings.item.BlocklingsItems;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

/**
 * A screen to show the stats of a blockling.
 */
@OnlyIn(Dist.CLIENT)
public class StatsScreen extends TabbedScreen
{
    /**
     * @param blockling the blockling associated with the screen.
     */
    public StatsScreen(@Nonnull BlocklingEntity blockling)
    {
        super(blockling, TabbedUIControl.Tab.STATS);

        GridPanel mainGridPanel = new GridPanel();
        mainGridPanel.setParent(tabbedUIControl.contentControl);
        mainGridPanel.setWidthPercentage(1.0);
        mainGridPanel.setHeightPercentage(1.0);
        mainGridPanel.addRowDefinition(GridDefinition.FIXED, 20.0);
        mainGridPanel.addRowDefinition(GridDefinition.RATIO, 1.0);
        mainGridPanel.addColumnDefinition(GridDefinition.RATIO, 1.0);

        TextFieldControl textFieldControl = new TextFieldControl();
        mainGridPanel.addChild(textFieldControl, 0, 0);
        textFieldControl.setWidthPercentage(1.0);
        textFieldControl.setHeightPercentage(1.0);
        textFieldControl.setText(blockling.getCustomName().getString());
        textFieldControl.setHorizontalContentAlignment(0.5);
        textFieldControl.setMaxTextLength(25);
        textFieldControl.setShouldRenderBackground(false);
        textFieldControl.setBackgroundColour(0x00000000);
        textFieldControl.eventBus.subscribe((BaseControl control, FocusChangedEvent e) ->
        {
            if (!textFieldControl.getText().trim().isEmpty())
            {
                blockling.setCustomName(new StringTextComponent(textFieldControl.getText()), true);
            }
            else
            {
                ITextComponent name = BlocklingsItems.BLOCKLING.get().getName(BlocklingsItems.BLOCKLING.get().getDefaultInstance());
                blockling.setCustomName(new StringTextComponent(name.getString()), true);
                textFieldControl.setText(name.getString());
            }
        });

        GridPanel statsGridPanel = new GridPanel();
        mainGridPanel.addChild(statsGridPanel, 1, 0);
        statsGridPanel.setWidthPercentage(1.0);
        statsGridPanel.setHeightPercentage(1.0);
        statsGridPanel.setMargins(1.0, 0.0, 1.0, 1.0);
        statsGridPanel.addRowDefinition(GridDefinition.AUTO, 1.0);
        statsGridPanel.addRowDefinition(GridDefinition.RATIO, 1.0);
        statsGridPanel.addRowDefinition(GridDefinition.AUTO, 1.0);
        statsGridPanel.addColumnDefinition(GridDefinition.RATIO, 1.0);

        HealthBarControl healthBarControl = new HealthBarControl(blockling);
        statsGridPanel.addChild(healthBarControl, 0, 0);
        healthBarControl.setHorizontalAlignment(0.5);
        healthBarControl.setVerticalAlignment(0.5);
        healthBarControl.setMarginTop(6.0);

        Control statsContainer = new Control();
        statsGridPanel.addChild(statsContainer, 1, 0);
        statsContainer.setWidthPercentage(1.0);
        statsContainer.setHeightPercentage(1.0);

        Control leftStatsControl = new Control();
        leftStatsControl.setParent(statsContainer);
        leftStatsControl.setWidthPercentage(0.5);
        leftStatsControl.setHeightPercentage(1.0);
        leftStatsControl.setPadding(10.0, 0.0, 0.0, 0.0);
        leftStatsControl.setHorizontalAlignment(0.0);

        Control rightStatsControl = new Control();
        rightStatsControl.setParent(statsContainer);
        rightStatsControl.setWidthPercentage(0.5);
        rightStatsControl.setHeightPercentage(1.0);
        rightStatsControl.setPadding(0.0, 0.0, 10.0, 0.0);
        rightStatsControl.setHorizontalAlignment(1.0);

        BlocklingAttributes stats = blockling.getStats();

        EnumeratingControl combatStats = new EnumeratingStatControl(new BlocklingsTranslationTextComponent("stats.attack.name"));
        combatStats.setParent(leftStatsControl);
        combatStats.setHorizontalAlignment(0.0);
        combatStats.setVerticalAlignment(0.22);
        combatStats.addControl(new StatControl(
                Textures.Stats.ATTACK_DAMAGE_MAIN,
                () -> stats.mainHandAttackDamage.displayStringValueFunction.apply(stats.mainHandAttackDamage.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.mainHandAttackDamage, TextFormatting.DARK_RED), false),
                () -> blockling.getEquipment().isAttackingWith(BlocklingHand.MAIN));
        combatStats.addControl(new StatControl(
                Textures.Stats.ATTACK_DAMAGE_OFF,
                () -> stats.offHandAttackDamage.displayStringValueFunction.apply(stats.offHandAttackDamage.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.offHandAttackDamage, TextFormatting.DARK_RED), false),
                () -> blockling.getEquipment().isAttackingWith(BlocklingHand.OFF));
        combatStats.addControl(new StatControl(
                Textures.Stats.ATTACK_SPEED,
                () -> stats.attackSpeed.displayStringValueFunction.apply(stats.attackSpeed.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.attackSpeed, TextFormatting.DARK_PURPLE), false));

        EnumeratingControl defenceStats = new EnumeratingStatControl(new BlocklingsTranslationTextComponent("stats.defence.name"));
        defenceStats.setParent(leftStatsControl);
        defenceStats.setHorizontalAlignment(0.0);
        defenceStats.setVerticalAlignment(0.72);
        defenceStats.addControl(new StatControl(
                Textures.Stats.ARMOUR,
                () -> stats.armour.displayStringValueFunction.apply(stats.armour.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.armour, TextFormatting.DARK_AQUA), false));
        defenceStats.addControl(new StatControl(
                Textures.Stats.ARMOUR_TOUGHNESS,
                () -> stats.armourToughness.displayStringValueFunction.apply(stats.armourToughness.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.armourToughness, TextFormatting.AQUA), false));
        defenceStats.addControl(new StatControl(
                Textures.Stats.KNOCKBACK_RESISTANCE,
                () -> stats.knockbackResistance.displayStringValueFunction.apply(stats.knockbackResistance.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.knockbackResistance, TextFormatting.YELLOW), false));

        EnumeratingControl gatherStats = new EnumeratingStatControl(new BlocklingsTranslationTextComponent("stats.gathering.name"));
        gatherStats.setParent(rightStatsControl);
        gatherStats.setHorizontalAlignment(1.0);
        gatherStats.setVerticalAlignment(0.22);
        gatherStats.addControl(new StatControl(
                Textures.Stats.MINING_SPEED,
                () -> stats.miningSpeed.displayStringValueFunction.apply(stats.miningSpeed.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.miningSpeed, TextFormatting.BLUE), true));
        gatherStats.addControl(new StatControl(
                Textures.Stats.WOODCUTTING_SPEED,
                () -> stats.woodcuttingSpeed.displayStringValueFunction.apply(stats.woodcuttingSpeed.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.woodcuttingSpeed, TextFormatting.DARK_GREEN), true));
        gatherStats.addControl(new StatControl(
                Textures.Stats.FARMING_SPEED,
                () -> stats.farmingSpeed.displayStringValueFunction.apply(stats.farmingSpeed.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.farmingSpeed, TextFormatting.YELLOW), true));

        EnumeratingControl movementStats = new EnumeratingStatControl(new BlocklingsTranslationTextComponent("stats.movement.name"));
        movementStats.setParent(rightStatsControl);
        movementStats.setHorizontalAlignment(1.0);
        movementStats.setVerticalAlignment(0.72);
        movementStats.addControl(new StatControl(
                Textures.Stats.MOVE_SPEED,
                () -> stats.moveSpeed.displayStringValueFunction.apply(stats.moveSpeed.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.moveSpeed, TextFormatting.BLUE), true));

        EntityControl entityControl = new EntityControl()
        {
            @Override
            public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
            {
                List<IReorderingProcessor> tooltip = new ArrayList<>();

                tooltip.add(new StringTextComponent(TextFormatting.GOLD + blockling.getCustomName().getString()).getVisualOrderText());
                tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("type.natural.name").getString() + TextFormatting.WHITE + blockling.getNaturalBlocklingType().name.getString()).getVisualOrderText());

                List<String> splitText;

                if (GuiUtil.get().isCrouchKeyDown())
                {
                    splitText = GuiUtil.get().split(new BlocklingsTranslationTextComponent("type.natural.desc").getString(), 200);
                    splitText.stream().map(s -> new StringTextComponent(TextFormatting.DARK_GRAY + s).getVisualOrderText()).forEach(tooltip::add);
                }

                splitText = GuiUtil.get().split(new BlocklingsTranslationTextComponent("type." + blockling.getNaturalBlocklingType().key + ".passive").getString(), 200);
                splitText.stream().map(s -> new StringTextComponent(TextFormatting.AQUA + s).getVisualOrderText()).forEach(tooltip::add);

                tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("type.name").getString() + TextFormatting.WHITE + blockling.getBlocklingType().name.getString()).getVisualOrderText());

                if (GuiUtil.get().isCrouchKeyDown())
                {
                    splitText = GuiUtil.get().split(new BlocklingsTranslationTextComponent("type.desc").getString(), 200);
                    splitText.stream().map(s -> new StringTextComponent(TextFormatting.DARK_GRAY + s).getVisualOrderText()).forEach(tooltip::add);
                }

                splitText = GuiUtil.get().split(new BlocklingsTranslationTextComponent("type." + blockling.getBlocklingType().key + ".passive").getString(), 200);
                splitText.stream().map(s -> new StringTextComponent(TextFormatting.AQUA + s).getVisualOrderText()).forEach(tooltip::add);

                String foodsString = TextFormatting.GRAY + new BlocklingsTranslationTextComponent("type.foods").getString() + TextFormatting.WHITE + new BlocklingsTranslationTextComponent("type.foods.flowers").getString() + ", ";
                foodsString += blockling.getBlocklingType().foods.stream().map(food -> food.getDescription().getString()).collect(joining(", "));
                splitText = GuiUtil.get().split(foodsString, 200);
                splitText.stream().map(s -> new StringTextComponent(s).getVisualOrderText()).forEach(tooltip::add);

                if (!GuiUtil.get().isCrouchKeyDown())
                {
                    tooltip.add(new StringTextComponent(TextFormatting.DARK_GRAY + "" + TextFormatting.ITALIC + new BlocklingsTranslationTextComponent("gui.more_info", Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage().getString()).getString()).getVisualOrderText());
                }

                renderTooltip(matrixStack, mouseX, mouseY, getPixelScaleX(), getPixelScaleY(), tooltip);
            }
        };
        entityControl.setParent(statsContainer);
        entityControl.setWidth(48.0);
        entityControl.setHeight(48.0);
        entityControl.setEntity(blockling);
        entityControl.setScaleToBoundingBox(true);
        entityControl.setEntityScale(0.9f);
        entityControl.setOffsetY(-1.0f);
        entityControl.setClipContentsToBounds(false);
        entityControl.setHorizontalAlignment(0.5);
        entityControl.setVerticalAlignment(0.47);

        StackPanel levelsPanel = new StackPanel();
        statsGridPanel.addChild(levelsPanel, 2, 0);
        levelsPanel.setWidthPercentage(1.0);
        levelsPanel.setFitHeightToContent(true);
        levelsPanel.setDirection(Direction.TOP_TO_BOTTOM);
        levelsPanel.setPadding(0.0, 0.0, 0.0, 6.0);
        levelsPanel.setSpacing(1.0);

        XpBarControl combatXpBarControl = new XpBarControl(blockling, BlocklingAttributes.Level.COMBAT);
        combatXpBarControl.setParent(levelsPanel);
        combatXpBarControl.setHorizontalAlignment(0.5);
        XpBarControl miningXpBarControl = new XpBarControl(blockling, BlocklingAttributes.Level.MINING);
        miningXpBarControl.setParent(levelsPanel);
        miningXpBarControl.setHorizontalAlignment(0.5);
        XpBarControl woodcuttingXpBarControl = new XpBarControl(blockling, BlocklingAttributes.Level.WOODCUTTING);
        woodcuttingXpBarControl.setParent(levelsPanel);
        woodcuttingXpBarControl.setHorizontalAlignment(0.5);
        XpBarControl farmingXpBarControl = new XpBarControl(blockling, BlocklingAttributes.Level.FARMING);
        farmingXpBarControl.setParent(levelsPanel);
        farmingXpBarControl.setHorizontalAlignment(0.5);
    }

    /**
     * Creates a tooltip based on a modifiable float attribute and a colour.
     *
     * @param attribute the modifiable attribute.
     * @param colour the colour for the attribute's value.
     * @return the tooltip.
     */
    @Nonnull
    public static List<ITextComponent> createModifiableFloatAttributeTooltip(@Nonnull IModifiable<Float> attribute, @Nonnull TextFormatting colour)
    {
        List<ITextComponent> tooltip = new ArrayList<>();

        tooltip.add(new StringTextComponent(colour + attribute.getDisplayStringValueFunction().apply(attribute.getValue()) + " " + TextFormatting.GRAY + attribute.createTranslation("name").getString()));

        appendModifiableFloatAttributeToTooltip(tooltip, attribute, 1);

        return tooltip;
    }

    /**
     * Appends to a tooltip based on a modifiable float attribute and a depth.
     *
     * @param tooltip the tooltip to append to.
     * @param attribute the modifiable attribute.
     * @param depth the current depth in terms of modifiers on modifiers.
     */
    public static void appendModifiableFloatAttributeToTooltip(@Nonnull List<ITextComponent> tooltip, @Nonnull IModifiable<Float> attribute, int depth)
    {
        for (IModifier<Float> modifier : attribute.getModifiers())
        {
            if (!modifier.isEnabled() || !modifier.isEffective())
            {
                continue;
            }

            String sign = modifier.getValue() < 0.0f && modifier.getOperation() == Operation.ADD ? "" : modifier.getValue() < 1.0f && modifier.getOperation() != Operation.ADD ? "" : "+";
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + generate(() -> " ").limit(depth).collect(joining()) + sign + modifier.getDisplayStringValueFunction().apply(modifier.getValue()) + " " + TextFormatting.DARK_GRAY + modifier.getDisplayStringNameSupplier().get()));

            if (modifier instanceof IModifiable<?>)
            {
                appendModifiableFloatAttributeToTooltip(tooltip, (IModifiable<Float>) modifier, depth + 1);
            }
        }
    }
}
