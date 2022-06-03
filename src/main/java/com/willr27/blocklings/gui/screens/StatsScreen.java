package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.attribute.BlocklingAttributes;
import com.willr27.blocklings.attribute.IModifiable;
import com.willr27.blocklings.attribute.IModifier;
import com.willr27.blocklings.attribute.Operation;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.controls.common.TextFieldControl;
import com.willr27.blocklings.gui.controls.stats.EnumeratingStatControl;
import com.willr27.blocklings.gui.controls.stats.HealthBarControl;
import com.willr27.blocklings.gui.controls.stats.LevelControl;
import com.willr27.blocklings.gui.controls.TabbedControl;
import com.willr27.blocklings.item.items.BlocklingsItems;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

/**
 * The screen to display all the blockling's stats like levels, combat stats, gathering stats, health etc.
 */
@OnlyIn(Dist.CLIENT)
public class StatsScreen extends TabbedScreen
{
    /**
     * The x offset from the sides for the stat icons.
     */
    private static final int ICON_X_OFFSET = 20;

    /**
     * The y position of the top stat icons.
     */
    private static final int TOP_ICON_Y = 51;

    /**
     * The y position of the top stat icons.
     */
    private static final int BOTTOM_ICON_Y = 79;

    /**
     * The gap between each level control.
     */
    private static final int LEVEL_XP_GAP = 13;

    /**
     * The blocklings' attributes.
     */
    @Nonnull
    private final BlocklingAttributes stats;

    /**
     * The text field control used to change the blockling's name.
     */
    private TextFieldControl nameField;

    /**
     *
     * @param blockling the blockling.
     */
    public StatsScreen(@Nonnull BlocklingEntity blockling)
    {
        super(blockling);
        this.stats = blockling.getStats();
    }

    @Override
    protected void init()
    {
        super.init();

        new HealthBarControl(this, blockling, contentLeft + 20, contentTop + 36);

        EnumeratingStatControl attackControl = new EnumeratingStatControl(this, new BlocklingsTranslationTextComponent("stats.attack.name"), contentLeft + ICON_X_OFFSET, contentTop + TOP_ICON_Y, false, 60, true, blockling);
        attackControl.addStat(() -> blockling.getEquipment().isAttackingWith(BlocklingHand.MAIN), () -> stats.mainHandAttackDamage.displayStringValueFunction.apply(stats.mainHandAttackDamage.getValue()), () -> createModifiableFloatAttributeTooltip(stats.mainHandAttackDamage, TextFormatting.DARK_RED), 12);
        attackControl.addStat(() -> blockling.getEquipment().isAttackingWith(BlocklingHand.OFF), () -> stats.offHandAttackDamage.displayStringValueFunction.apply(stats.offHandAttackDamage.getValue()), () -> createModifiableFloatAttributeTooltip(stats.offHandAttackDamage, TextFormatting.DARK_RED), 10);
        attackControl.addStat(() -> true, () -> stats.attackSpeed.displayStringValueFunction.apply(stats.attackSpeed.getValue()), () -> createModifiableFloatAttributeTooltip(stats.attackSpeed, TextFormatting.DARK_PURPLE),11);

        EnumeratingStatControl defenceControl = new EnumeratingStatControl(this, new BlocklingsTranslationTextComponent("stats.defence.name"), contentLeft + ICON_X_OFFSET, contentTop + BOTTOM_ICON_Y, false, 60, true, blockling);
        defenceControl.addStat(() -> true, () -> stats.armour.displayStringValueFunction.apply(stats.armour.getValue()), () -> createModifiableFloatAttributeTooltip(stats.armour, TextFormatting.DARK_AQUA), 5);
        defenceControl.addStat(() -> true, () -> stats.armourToughness.displayStringValueFunction.apply(stats.armourToughness.getValue()), () -> createModifiableFloatAttributeTooltip(stats.armourToughness, TextFormatting.AQUA), 6);
        defenceControl.addStat(() -> true, () -> stats.knockbackResistance.displayStringValueFunction.apply(stats.knockbackResistance.getValue()), () -> createModifiableFloatAttributeTooltip(stats.knockbackResistance, TextFormatting.YELLOW), 7);

        EnumeratingStatControl gatheringControl = new EnumeratingStatControl(this, new BlocklingsTranslationTextComponent("stats.gathering.name"), contentLeft + TabbedControl.CONTENT_WIDTH - ICON_X_OFFSET - EnumeratingStatControl.ICON_SIZE, contentTop + TOP_ICON_Y, true, 60, true, blockling);
        gatheringControl.addStat(() -> true, () -> stats.miningSpeed.displayStringValueFunction.apply(stats.miningSpeed.getValue()), () -> createModifiableFloatAttributeTooltip(stats.miningSpeed, TextFormatting.BLUE), 1);
        gatheringControl.addStat(() -> true, () -> stats.woodcuttingSpeed.displayStringValueFunction.apply(stats.woodcuttingSpeed.getValue()), () -> createModifiableFloatAttributeTooltip(stats.woodcuttingSpeed, TextFormatting.DARK_GREEN), 2);
        gatheringControl.addStat(() -> true, () -> stats.farmingSpeed.displayStringValueFunction.apply(stats.farmingSpeed.getValue()), () -> createModifiableFloatAttributeTooltip(stats.farmingSpeed, TextFormatting.YELLOW), 3);

        EnumeratingStatControl movementControl = new EnumeratingStatControl(this, new BlocklingsTranslationTextComponent("stats.movement.name"), contentLeft + TabbedControl.CONTENT_WIDTH - ICON_X_OFFSET - EnumeratingStatControl.ICON_SIZE, contentTop + BOTTOM_ICON_Y, true, 60, true, blockling);
        movementControl.addStat(() -> true, () -> stats.moveSpeed.displayStringValueFunction.apply(stats.moveSpeed.getValue()), () -> createModifiableFloatAttributeTooltip(stats.moveSpeed, TextFormatting.BLUE), 8);

        LevelControl combatLevelControl = new LevelControl(this, BlocklingAttributes.Level.COMBAT, blockling, contentLeft + 15, contentTop + 102);
        LevelControl miningLevelControl = new LevelControl(this, BlocklingAttributes.Level.MINING, blockling, + combatLevelControl.getX(), combatLevelControl.getY() + LEVEL_XP_GAP);
        LevelControl woodcuttingLevelControl = new LevelControl(this, BlocklingAttributes.Level.WOODCUTTING, blockling, + combatLevelControl.getX(), miningLevelControl.getY() + LEVEL_XP_GAP);
        new LevelControl(this, BlocklingAttributes.Level.FARMING, blockling, + combatLevelControl.getX(), woodcuttingLevelControl.getY() + LEVEL_XP_GAP);

        new Control(this, contentLeft + TabbedControl.CONTENT_WIDTH / 2 - 50 / 2, contentTop + TabbedControl.CONTENT_HEIGHT / 2 - 50 / 2 - 10, 50, 50)
        {
            @Override
            public void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
            {
                List<IReorderingProcessor> tooltip = new ArrayList<>();

                tooltip.add(new StringTextComponent(TextFormatting.GOLD + blockling.getCustomName().getString()).getVisualOrderText());
                tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("type.name").getString() + TextFormatting.WHITE + blockling.getBlocklingType().name.getString()).getVisualOrderText());

                if (GuiUtil.isKeyDown(Minecraft.getInstance().options.keyShift.getKey().getValue()))
                {
                    List<String> splitText = GuiUtil.splitText(font, new BlocklingsTranslationTextComponent("type.desc").getString(), 150);
                    splitText.stream().map(s -> new StringTextComponent(TextFormatting.DARK_GRAY + s).getVisualOrderText()).forEach(tooltip::add);
                }

                tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("type.natural.name").getString() + TextFormatting.WHITE + blockling.getOriginalBlocklingType().name.getString()).getVisualOrderText());

                if (GuiUtil.isKeyDown(Minecraft.getInstance().options.keyShift.getKey().getValue()))
                {
                    List<String> splitText = GuiUtil.splitText(font, new BlocklingsTranslationTextComponent("type.natural.desc").getString(), 150);
                    splitText.stream().map(s -> new StringTextComponent(TextFormatting.DARK_GRAY + s).getVisualOrderText()).forEach(tooltip::add);
                }

                screen.renderTooltip(matrixStack, tooltip, mouseX, mouseY);
            }
        };

        nameField = new TextFieldControl(font, contentLeft + 14, contentTop + 14, 160, 20, new StringTextComponent(""))
        {
            @Override
            public void setFocus(boolean focus)
            {
                if (!focus)
                {
                    if (!getValue().equals(""))
                    {
                        blockling.setCustomName(new StringTextComponent(getValue()), true);
                    }
                    else
                    {
                        ITextComponent name = BlocklingsItems.BLOCKLING.get().getName(BlocklingsItems.BLOCKLING.get().getDefaultInstance());
                        blockling.setCustomName(new StringTextComponent(name.getString()), true);
                        setValue(name.getString());
                    }
                }

                super.setFocus(focus);
            }
        };
        nameField.setBordered(false);
        nameField.setMaxLength(25);
        nameField.setVisible(true);
        nameField.setTextColor(16777215);
        nameField.setValue(blockling.getCustomName().getString());
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
            if (!modifier.isEnabled() || ((modifier.getValue() == 0.0f && modifier.getOperation() == Operation.ADD) || (modifier.getValue() == 1.0f && modifier.getOperation() != Operation.ADD)))
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

    @Override
    public void tick()
    {
        nameField.tick();

        super.tick();
    }

    @Override
    public void renderScreen(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        mouseX /= getEffectiveScale();
        mouseY /= getEffectiveScale();

        GuiUtil.bindTexture(GuiTextures.STATS);
        blit(matrixStack, contentLeft, contentTop, 0, 0, TabbedControl.CONTENT_WIDTH, TabbedControl.CONTENT_HEIGHT);

        RenderSystem.enableDepthTest();
        nameField.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.enableDepthTest();

        GuiUtil.renderEntityOnScreen(matrixStack, centerX, centerY + 10, 35, centerX - mouseX, centerY - mouseY, blockling);
    }

    @Override
    public void globalMouseClicked(@Nonnull MouseButtonEvent e)
    {
        nameField.mouseClicked(e.mouseX / getEffectiveScale(), e.mouseY / getEffectiveScale(), e.button);
    }

    @Override
    public void globalMouseReleased(@Nonnull MouseButtonEvent e)
    {
        nameField.mouseReleased(e.mouseX / getEffectiveScale(), e.mouseY / getEffectiveScale(), e.button);
    }

    @Override
    public void globalKeyPressed(@Nonnull KeyEvent e)
    {
        if (e.keyCode == GLFW.GLFW_KEY_ENTER || e.keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            if (nameField.isFocused())
            {
                nameField.setFocus(false);

                return;
            }
        }
        else
        {
            nameField.keyPressed(e.keyCode, e.scanCode, e.modifiers);
        }

        if (!nameField.isFocused() && GuiUtil.isCloseInventoryKey(e.keyCode))
        {
            onClose();
        }
    }

    @Override
    public void globalCharTyped(@Nonnull CharEvent e)
    {
        nameField.charTyped(e.character, e.keyCode);
    }
}
