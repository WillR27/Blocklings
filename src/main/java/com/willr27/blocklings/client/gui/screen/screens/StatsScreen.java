package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.GuiTextures;
import com.willr27.blocklings.client.gui.RenderArgs;
import com.willr27.blocklings.client.gui.control.*;
import com.willr27.blocklings.client.gui.control.controls.*;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui.control.controls.stats.EnumeratingStatControl;
import com.willr27.blocklings.client.gui.control.controls.stats.HealthBarControl;
import com.willr27.blocklings.client.gui.control.controls.stats.StatControl;
import com.willr27.blocklings.client.gui.control.controls.stats.XpBarControl;
import com.willr27.blocklings.client.gui2.GuiUtil;
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
import java.util.Random;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

/**
 * A screen to display the blockling's stats.
 */
@OnlyIn(Dist.CLIENT)
public class StatsScreen extends TabbedScreen
{
    /**
     * @param blockling the blockling.
     */
    public StatsScreen(@Nonnull BlocklingEntity blockling)
    {
        super(blockling, TabbedUIControl.Tab.STATS);
    }

    @Override
    protected void init()
    {
        super.init();

        TextFieldControl textFieldControl = new TextFieldControl();
        textFieldControl.setParent(contentControl);
        textFieldControl.setDock(Dock.TOP);
        textFieldControl.setHorizontalAlignment(HorizontalAlignment.MIDDLE);
        textFieldControl.setMaxTextLength(25);
        textFieldControl.setText(blockling.getCustomName().getString());
        textFieldControl.focusChanged.subscribe((e) ->
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

        Control statsControl = new Control()
        {
            @Override
            public void layoutDockedContents()
            {
                super.layoutDockedContents();
            }
        };
        statsControl.setParent(contentControl);
        statsControl.setDock(Dock.FILL);

        Control healthControl = new Control();
        healthControl.setParent(statsControl);
        healthControl.setPercentHeight(0.14f);
        healthControl.setDock(Dock.TOP);

        HealthBarControl healthBarControl = new HealthBarControl(blockling);
        healthBarControl.setParent(healthControl);
        healthBarControl.setPercentX(0.5f);
        healthBarControl.setPercentY(0.5f);

        FlowPanel levelsPanel = new FlowPanel();
        levelsPanel.setParent(statsControl);
        levelsPanel.setFitToContentsY(true);
        levelsPanel.setPercentWidth(1.0f);
        levelsPanel.setDock(Dock.BOTTOM);
        levelsPanel.setFlowDirection(Direction.TOP_TO_BOTTOM);
        levelsPanel.setOverflowOrientation(Orientation.VERTICAL);
        levelsPanel.setPadding(Side.TOP, 2);
        levelsPanel.setPadding(Side.BOTTOM, 4);
        levelsPanel.setItemGapY(1);

        Control centerStatsControl = new Control();
        centerStatsControl.setParent(statsControl);
        centerStatsControl.setDock(Dock.FILL);

        Control leftStatsControl = new Control();
        leftStatsControl.setParent(centerStatsControl);
        leftStatsControl.setPercentWidth(0.5f);
        leftStatsControl.setPadding(Side.LEFT, 10);
        leftStatsControl.setDock(Dock.LEFT);

        Control rightStatsControl = new Control();
        rightStatsControl.setParent(centerStatsControl);
        rightStatsControl.setPercentWidth(0.5f);
        rightStatsControl.setPadding(Side.RIGHT, 10);
        rightStatsControl.setDock(Dock.RIGHT);

        EntityControl entityControl = new EntityControl()
        {
            @Override
            public void onRenderTooltip(@Nonnull RenderArgs renderArgs)
            {
                List<IReorderingProcessor> tooltip = new ArrayList<>();

                tooltip.add(new StringTextComponent(TextFormatting.GOLD + blockling.getCustomName().getString()).getVisualOrderText());
                tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("type.natural.name").getString() + TextFormatting.WHITE + blockling.getNaturalBlocklingType().name.getString()).getVisualOrderText());

                List<String> splitText;

                if (GuiUtil.isKeyDown(Minecraft.getInstance().options.keyShift.getKey().getValue()))
                {
                    splitText = GuiUtil.splitText(font, new BlocklingsTranslationTextComponent("type.natural.desc").getString(), 200);
                    splitText.stream().map(s -> new StringTextComponent(TextFormatting.DARK_GRAY + s).getVisualOrderText()).forEach(tooltip::add);
                }

                splitText = GuiUtil.splitText(font, new BlocklingsTranslationTextComponent("type." + blockling.getNaturalBlocklingType().key + ".passive").getString(), 200);
                splitText.stream().map(s -> new StringTextComponent(TextFormatting.AQUA + s).getVisualOrderText()).forEach(tooltip::add);

                tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("type.name").getString() + TextFormatting.WHITE + blockling.getBlocklingType().name.getString()).getVisualOrderText());

                if (GuiUtil.isKeyDown(Minecraft.getInstance().options.keyShift.getKey().getValue()))
                {
                    splitText = GuiUtil.splitText(font, new BlocklingsTranslationTextComponent("type.desc").getString(), 200);
                    splitText.stream().map(s -> new StringTextComponent(TextFormatting.DARK_GRAY + s).getVisualOrderText()).forEach(tooltip::add);
                }

                splitText = GuiUtil.splitText(font, new BlocklingsTranslationTextComponent("type." + blockling.getBlocklingType().key + ".passive").getString(), 200);
                splitText.stream().map(s -> new StringTextComponent(TextFormatting.AQUA + s).getVisualOrderText()).forEach(tooltip::add);

                String foodsString = TextFormatting.GRAY + new BlocklingsTranslationTextComponent("type.foods").getString() + TextFormatting.WHITE + new BlocklingsTranslationTextComponent("type.foods.flowers").getString() + ", ";
                foodsString += blockling.getBlocklingType().foods.stream().map(food -> food.getDescription().getString()).collect(joining(", "));
                splitText = GuiUtil.splitText(font, foodsString, 200);
                splitText.stream().map(s -> new StringTextComponent(s).getVisualOrderText()).forEach(tooltip::add);

                if (!GuiUtil.isKeyDown(Minecraft.getInstance().options.keyShift.getKey().getValue()))
                {
                    tooltip.add(new StringTextComponent(TextFormatting.DARK_GRAY + "" + TextFormatting.ITALIC + new BlocklingsTranslationTextComponent("gui.more_info", Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage().getString()).getString()).getVisualOrderText());
                }

                renderTooltip(renderArgs, tooltip);
            }
        };
        entityControl.setParent(centerStatsControl);
        entityControl.setEntity(blockling);
        entityControl.setWidth(48);
        entityControl.setHeight(48);
        entityControl.setScaleToBoundingBox(true);
        entityControl.setEntityScale(0.9f);
        entityControl.setOffsetY(-1.0f);
        entityControl.setShouldScissor(false);
        entityControl.setAlignmentX(new Alignment(0.5f));
        entityControl.setAlignmentY(new Alignment(0.5f));

        Random random = new Random();
        BlocklingAttributes stats = blockling.getStats();

        EnumeratingControl combatStats = new EnumeratingStatControl(new BlocklingsTranslationTextComponent("stats.attack.name"));
        combatStats.setParent(leftStatsControl);
        combatStats.setPercentX(0.0f);
        combatStats.setAlignmentY(new Alignment(0.25f));
        combatStats.addControl(new StatControl(
                GuiTextures.Stats.ATTACK_DAMAGE_MAIN,
                () -> stats.mainHandAttackDamage.displayStringValueFunction.apply(stats.mainHandAttackDamage.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.mainHandAttackDamage, TextFormatting.DARK_RED), false),
                () -> blockling.getEquipment().isAttackingWith(BlocklingHand.MAIN));
        combatStats.addControl(new StatControl(
                GuiTextures.Stats.ATTACK_DAMAGE_OFF,
                () -> stats.offHandAttackDamage.displayStringValueFunction.apply(stats.offHandAttackDamage.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.offHandAttackDamage, TextFormatting.DARK_RED), false),
                () -> blockling.getEquipment().isAttackingWith(BlocklingHand.OFF));
        combatStats.addControl(new StatControl(
                GuiTextures.Stats.ATTACK_SPEED,
                () -> stats.attackSpeed.displayStringValueFunction.apply(stats.attackSpeed.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.attackSpeed, TextFormatting.DARK_PURPLE), false));

        EnumeratingControl defenceStats = new EnumeratingStatControl(new BlocklingsTranslationTextComponent("stats.defence.name"));
        defenceStats.setParent(leftStatsControl);
        defenceStats.setPercentX(0.0f);
        defenceStats.setAlignmentY(new Alignment(0.75f));
        defenceStats.addControl(new StatControl(
                GuiTextures.Stats.ARMOUR,
                () -> stats.armour.displayStringValueFunction.apply(stats.armour.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.armour, TextFormatting.DARK_AQUA), false));
        defenceStats.addControl(new StatControl(
                GuiTextures.Stats.ARMOUR_TOUGHNESS,
                () -> stats.armourToughness.displayStringValueFunction.apply(stats.armourToughness.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.armourToughness, TextFormatting.AQUA), false));
        defenceStats.addControl(new StatControl(
                GuiTextures.Stats.KNOCKBACK_RESISTANCE,
                () -> stats.knockbackResistance.displayStringValueFunction.apply(stats.knockbackResistance.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.knockbackResistance, TextFormatting.YELLOW), false));

        EnumeratingControl gatherStats = new EnumeratingStatControl(new BlocklingsTranslationTextComponent("stats.gathering.name"));
        gatherStats.setParent(rightStatsControl);
        gatherStats.setPercentX(1.0f);
        gatherStats.setAlignmentX(new Alignment(1.0f));
        gatherStats.setAlignmentY(new Alignment(0.25f));
        gatherStats.addControl(new StatControl(
                GuiTextures.Stats.MINING_SPEED,
                () -> stats.miningSpeed.displayStringValueFunction.apply(stats.miningSpeed.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.miningSpeed, TextFormatting.BLUE), true));
        gatherStats.addControl(new StatControl(
                GuiTextures.Stats.WOODCUTTING_SPEED,
                () -> stats.woodcuttingSpeed.displayStringValueFunction.apply(stats.woodcuttingSpeed.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.woodcuttingSpeed, TextFormatting.DARK_GREEN), true));
        gatherStats.addControl(new StatControl(
                GuiTextures.Stats.FARMING_SPEED,
                () -> stats.farmingSpeed.displayStringValueFunction.apply(stats.farmingSpeed.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.farmingSpeed, TextFormatting.YELLOW), true));

        EnumeratingControl movementStats = new EnumeratingStatControl(new BlocklingsTranslationTextComponent("stats.movement.name"));
        movementStats.setParent(rightStatsControl);
        movementStats.setPercentX(1.0f);
        movementStats.setAlignmentX(new Alignment(1.0f));
        movementStats.setAlignmentY(new Alignment(0.75f));
        movementStats.addControl(new StatControl(
                GuiTextures.Stats.MOVE_SPEED,
                () -> stats.moveSpeed.displayStringValueFunction.apply(stats.moveSpeed.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.moveSpeed, TextFormatting.BLUE), true));

        XpBarControl combatXpBarControl = new XpBarControl(blockling, BlocklingAttributes.Level.COMBAT);
        combatXpBarControl.setParent(levelsPanel);
        combatXpBarControl.setAlignmentX(new Alignment(0.5f));
        XpBarControl miningXpBarControl = new XpBarControl(blockling, BlocklingAttributes.Level.MINING);
        miningXpBarControl.setParent(levelsPanel);
        miningXpBarControl.setAlignmentX(new Alignment(0.5f));
        XpBarControl woodcuttingXpBarControl = new XpBarControl(blockling, BlocklingAttributes.Level.WOODCUTTING);
        woodcuttingXpBarControl.setParent(levelsPanel);
        woodcuttingXpBarControl.setAlignmentX(new Alignment(0.5f));
        XpBarControl farmingXpBarControl = new XpBarControl(blockling, BlocklingAttributes.Level.FARMING);
        farmingXpBarControl.setParent(levelsPanel);
        farmingXpBarControl.setAlignmentX(new Alignment(0.5f));
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
