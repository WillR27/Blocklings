package com.willr27.blocklings.gui.screens.stats;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.attribute.*;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.controls.TextFieldWidget;
import com.willr27.blocklings.gui.guis.TabbedGui;
import com.willr27.blocklings.gui.screens.TabbedScreen;
import com.willr27.blocklings.item.items.Items;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.ArrayList;
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
     * The control to display the blockling's health.
     */
    private HealthBarControl healthBar;

    /**
     * The control to display the attack stats.
     */
    private EnumeratingStatControl attackWidget;

    /**
     * The control to display the defence stats.
     */
    private EnumeratingStatControl defenceWidget;

    /**
     * The control to display the gathering stats.
     */
    private EnumeratingStatControl gatheringWidget;

    /**
     * The control to display the combat stats.
     */
    private EnumeratingStatControl movementWidget;

    /**
     * The control to display the combat level info.
     */
    private LevelControl combatLevelWidget;

    /**
     * The control to display the mining level info.
     */
    private LevelControl miningLevelWidget;

    /**
     * The control to display the woodcutting level info.
     */
    private LevelControl woodcuttingLevelWidget;

    /**
     * The control to display the farming level info.
     */
    private LevelControl farmingLevelWidget;

    /**
     * The text field control used to change the blockling's name.
     */
    private TextFieldWidget nameField;

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

        healthBar = new HealthBarControl(this, blockling, 20, 36);

        attackWidget = new EnumeratingStatControl(this, new BlocklingsTranslationTextComponent("stats.attack.name"), ICON_X_OFFSET, TOP_ICON_Y, false, 60, true, blockling);
        attackWidget.addStat(() -> blockling.getEquipment().isAttackingWith(BlocklingHand.MAIN), () -> stats.mainHandAttackDamage.displayStringValueFunction.apply(stats.mainHandAttackDamage.getValue()), () -> createModifiableFloatAttributeTooltip(stats.mainHandAttackDamage, TextFormatting.DARK_RED), 12);
        attackWidget.addStat(() -> blockling.getEquipment().isAttackingWith(BlocklingHand.OFF), () -> stats.offHandAttackDamage.displayStringValueFunction.apply(stats.offHandAttackDamage.getValue()), () -> createModifiableFloatAttributeTooltip(stats.offHandAttackDamage, TextFormatting.DARK_RED), 10);
        attackWidget.addStat(() -> true, () -> stats.attackSpeed.displayStringValueFunction.apply(stats.attackSpeed.getValue()), () -> createModifiableFloatAttributeTooltip(stats.attackSpeed, TextFormatting.DARK_PURPLE),11);

        defenceWidget = new EnumeratingStatControl(this, new BlocklingsTranslationTextComponent("stats.defence.name"), ICON_X_OFFSET, BOTTOM_ICON_Y, false, 60, true, blockling);
        defenceWidget.addStat(() -> true, () -> stats.armour.displayStringValueFunction.apply(stats.armour.getValue()), () -> createModifiableFloatAttributeTooltip(stats.armour, TextFormatting.DARK_AQUA), 5);
        defenceWidget.addStat(() -> true, () -> stats.armourToughness.displayStringValueFunction.apply(stats.armourToughness.getValue()), () -> createModifiableFloatAttributeTooltip(stats.armourToughness, TextFormatting.AQUA), 6);
        defenceWidget.addStat(() -> true, () -> stats.knockbackResistance.displayStringValueFunction.apply(stats.knockbackResistance.getValue()), () -> createModifiableFloatAttributeTooltip(stats.knockbackResistance, TextFormatting.YELLOW), 7);

        gatheringWidget = new EnumeratingStatControl(this, new BlocklingsTranslationTextComponent("stats.gathering.name"), TabbedGui.CONTENT_WIDTH - ICON_X_OFFSET - EnumeratingStatControl.ICON_SIZE, TOP_ICON_Y, true, 60, true, blockling);
        gatheringWidget.addStat(() -> true, () -> stats.miningSpeed.displayStringValueFunction.apply(stats.miningSpeed.getValue()), () -> createModifiableFloatAttributeTooltip(stats.miningSpeed, TextFormatting.BLUE), 1);
        gatheringWidget.addStat(() -> true, () -> stats.woodcuttingSpeed.displayStringValueFunction.apply(stats.woodcuttingSpeed.getValue()), () -> createModifiableFloatAttributeTooltip(stats.woodcuttingSpeed, TextFormatting.DARK_GREEN), 2);
        gatheringWidget.addStat(() -> true, () -> stats.farmingSpeed.displayStringValueFunction.apply(stats.farmingSpeed.getValue()), () -> createModifiableFloatAttributeTooltip(stats.farmingSpeed, TextFormatting.YELLOW), 3);

        movementWidget = new EnumeratingStatControl(this, new BlocklingsTranslationTextComponent("stats.movement.name"), TabbedGui.CONTENT_WIDTH - ICON_X_OFFSET - EnumeratingStatControl.ICON_SIZE, BOTTOM_ICON_Y, true, 60, true, blockling);
        movementWidget.addStat(() -> true, () -> stats.moveSpeed.displayStringValueFunction.apply(stats.moveSpeed.getValue()), () -> createModifiableFloatAttributeTooltip(stats.moveSpeed, TextFormatting.BLUE), 8);

        combatLevelWidget = new LevelControl(this, BlocklingAttributes.Level.COMBAT, blockling, 15, 102);
        miningLevelWidget = new LevelControl(this, BlocklingAttributes.Level.MINING, blockling, combatLevelWidget.getX(), combatLevelWidget.getY() + LEVEL_XP_GAP);
        woodcuttingLevelWidget = new LevelControl(this, BlocklingAttributes.Level.WOODCUTTING, blockling, combatLevelWidget.getX(), miningLevelWidget.getY() + LEVEL_XP_GAP);
        farmingLevelWidget = new LevelControl(this, BlocklingAttributes.Level.FARMING, blockling, combatLevelWidget.getX(), woodcuttingLevelWidget.getY() + LEVEL_XP_GAP);

        nameField = new TextFieldWidget(font, contentLeft + 11, contentTop + 11, 154, 14, new StringTextComponent(""))
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
                        ITextComponent name = Items.BLOCKLING.get().getName(Items.BLOCKLING.get().getDefaultInstance());
                        blockling.setCustomName(new StringTextComponent(name.getString()), true);
                        setValue(name.getString());
                    }
                }

                super.setFocus(focus);
            }
        };
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
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        GuiUtil.bindTexture(GuiTextures.STATS);
        blit(matrixStack, contentLeft, contentTop, 0, 0, TabbedGui.CONTENT_WIDTH, TabbedGui.CONTENT_HEIGHT);

        GuiUtil.renderEntityOnScreen(centerX, centerY + 10, 35, centerX - mouseX, centerY - mouseY, blockling);

        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.0, 100.0);

        healthBar.render(matrixStack, mouseX, mouseY);

        attackWidget.render(matrixStack, mouseX, mouseY);
        defenceWidget.render(matrixStack, mouseX, mouseY);
        gatheringWidget.render(matrixStack, mouseX, mouseY);
        movementWidget.render(matrixStack, mouseX, mouseY);

        combatLevelWidget.render(matrixStack, mouseX, mouseY);
        miningLevelWidget.render(matrixStack, mouseX, mouseY);
        woodcuttingLevelWidget.render(matrixStack, mouseX, mouseY);
        farmingLevelWidget.render(matrixStack, mouseX, mouseY);

        nameField.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.enableDepthTest();

        matrixStack.popPose();

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        attackWidget.renderTooltip(matrixStack, mouseX, mouseY);
        defenceWidget.renderTooltip(matrixStack, mouseX, mouseY);
        gatheringWidget.renderTooltip(matrixStack, mouseX, mouseY);
        movementWidget.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        mouseClickedNoHandle((int) mouseX, (int) mouseY, button);

        if (nameField.mouseClicked(mouseX, mouseY, button))
        {
            return true;
        }
        else if (attackWidget.mouseClicked((int) mouseX, (int) mouseY, button))
        {
            return true;
        }
        else if (defenceWidget.mouseClicked((int) mouseX, (int) mouseY, button))
        {
            return true;
        }
        else if (gatheringWidget.mouseClicked((int) mouseX, (int) mouseY, button))
        {
            return true;
        }
        else if (movementWidget.mouseClicked((int) mouseX, (int) mouseY, button))
        {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        nameField.mouseReleased(mouseX, mouseY, button);

        mouseReleasedNoHandle((int) mouseX, (int) mouseY, button);

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int mods)
    {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            if (nameField.isFocused())
            {
                nameField.setFocus(false);

                return true;
            }
        }
        else
        {
            nameField.keyPressed(keyCode, scanCode, mods);
        }

        if (!nameField.isFocused() && GuiUtil.isCloseInventoryKey(keyCode))
        {
            onClose();

            return true;
        }

        return super.keyPressed(keyCode, scanCode, scanCode);
    }

    @Override
    public boolean charTyped(char character, int keyCode)
    {
        nameField.charTyped(character, keyCode);

        return super.charTyped(character, keyCode);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
