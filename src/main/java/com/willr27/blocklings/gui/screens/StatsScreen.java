package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.attribute.*;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.screens.guis.TabbedGui;
import com.willr27.blocklings.gui.widgets.TextFieldWidget;
import com.willr27.blocklings.gui.widgets.TexturedWidget;
import com.willr27.blocklings.gui.widgets.Widget;
import com.willr27.blocklings.item.items.Items;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

/**
 * The screen to display all the blockling's stats like levels, combat stats, gathering stats, health etc.
 */
public class StatsScreen extends TabbedScreen
{
    private static final int LEFT_ICON_X = 20;
    private static final int TOP_ICON_Y = 51;
    private static final int BOTTOM_ICON_Y = 79;

    private static final int LEVEL_XP_GAP = 13;

    /**
     * The blocklings' attributes.
     */
    @Nonnull
    private final BlocklingAttributes stats;

    /**
     * The widget to display the blockling's health.
     */
    private HealthBarWidget healthBar;

    /**
     * The widget to display the attack stats.
     */
    private EnumeratingStatWidget attackWidget;

    /**
     * The widget to display the defence stats.
     */
    private EnumeratingStatWidget defenceWidget;

    /**
     * The widget to display the gathering stats.
     */
    private EnumeratingStatWidget gatherWidget;

    /**
     * The widget to display the combat stats.
     */
    private EnumeratingStatWidget movementWidget;

    /**
     * The widget to display the combat level info.
     */
    private LevelWidget combatLevelWidget;

    /**
     * The widget to display the mining level info.
     */
    private LevelWidget miningLevelWidget;

    /**
     * The widget to display the woodcutting level info.
     */
    private LevelWidget woodcuttingLevelWidget;

    /**
     * The widget to display the farming level info.
     */
    private LevelWidget farmingLevelWidget;

    /**
     * The text field widget used to change the blockling's name.
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

        healthBar = new HealthBarWidget(blockling, contentLeft + 20, contentTop + 36);

        attackWidget = new EnumeratingStatWidget(new BlocklingsTranslationTextComponent("stats.attack.name"), contentLeft + LEFT_ICON_X, contentTop + TOP_ICON_Y, false, 60, true, blockling);
        attackWidget.addStat(() -> blockling.getEquipment().isAttackingWith(BlocklingHand.MAIN), () -> stats.mainHandAttackDamage.displayStringValueFunction.apply(stats.mainHandAttackDamage.getValue()), () -> createModifiableFloatAttributeTooltip(stats.mainHandAttackDamage, TextFormatting.DARK_RED), 12);
        attackWidget.addStat(() -> blockling.getEquipment().isAttackingWith(BlocklingHand.OFF), () -> stats.offHandAttackDamage.displayStringValueFunction.apply(stats.offHandAttackDamage.getValue()), () -> createModifiableFloatAttributeTooltip(stats.offHandAttackDamage, TextFormatting.DARK_RED), 10);
        attackWidget.addStat(() -> true, () -> stats.attackSpeed.displayStringValueFunction.apply(stats.attackSpeed.getValue()), () -> createModifiableFloatAttributeTooltip(stats.attackSpeed, TextFormatting.DARK_PURPLE),11);

        defenceWidget = new EnumeratingStatWidget(new BlocklingsTranslationTextComponent("stats.defence.name"), contentLeft + LEFT_ICON_X, contentTop + BOTTOM_ICON_Y, false, 60, true, blockling);
        defenceWidget.addStat(() -> true, () -> stats.armour.displayStringValueFunction.apply(stats.armour.getValue()), () -> createModifiableFloatAttributeTooltip(stats.armour, TextFormatting.DARK_AQUA), 5);
        defenceWidget.addStat(() -> true, () -> stats.armourToughness.displayStringValueFunction.apply(stats.armourToughness.getValue()), () -> createModifiableFloatAttributeTooltip(stats.armourToughness, TextFormatting.AQUA), 6);
        defenceWidget.addStat(() -> true, () -> stats.knockbackResistance.displayStringValueFunction.apply(stats.knockbackResistance.getValue()), () -> createModifiableFloatAttributeTooltip(stats.knockbackResistance, TextFormatting.YELLOW), 7);

        gatherWidget = new EnumeratingStatWidget(new BlocklingsTranslationTextComponent("stats.gather.name"), contentRight - LEFT_ICON_X - EnumeratingStatWidget.ICON_SIZE, contentTop + TOP_ICON_Y, true, 60, true, blockling);
        gatherWidget.addStat(() -> true, () -> stats.miningSpeed.displayStringValueFunction.apply(stats.miningSpeed.getValue()), () -> createModifiableFloatAttributeTooltip(stats.miningSpeed, TextFormatting.BLUE), 1);
        gatherWidget.addStat(() -> true, () -> stats.woodcuttingSpeed.displayStringValueFunction.apply(stats.woodcuttingSpeed.getValue()), () -> createModifiableFloatAttributeTooltip(stats.woodcuttingSpeed, TextFormatting.DARK_GREEN), 2);
        gatherWidget.addStat(() -> true, () -> stats.farmingSpeed.displayStringValueFunction.apply(stats.farmingSpeed.getValue()), () -> createModifiableFloatAttributeTooltip(stats.farmingSpeed, TextFormatting.YELLOW), 3);

        movementWidget = new EnumeratingStatWidget(new BlocklingsTranslationTextComponent("stats.movement.name"), contentRight - LEFT_ICON_X - EnumeratingStatWidget.ICON_SIZE, contentTop + BOTTOM_ICON_Y, true, 60, true, blockling);
        movementWidget.addStat(() -> true, () -> stats.moveSpeed.displayStringValueFunction.apply(stats.moveSpeed.getValue()), () -> createModifiableFloatAttributeTooltip(stats.moveSpeed, TextFormatting.BLUE), 8);

        combatLevelWidget = new LevelWidget(BlocklingAttributes.Level.COMBAT, blockling, contentLeft + 15, contentTop + 102);
        miningLevelWidget = new LevelWidget(BlocklingAttributes.Level.MINING, blockling, combatLevelWidget.x, combatLevelWidget.y + LEVEL_XP_GAP);
        woodcuttingLevelWidget = new LevelWidget(BlocklingAttributes.Level.WOODCUTTING, blockling, combatLevelWidget.x, miningLevelWidget.y + LEVEL_XP_GAP);
        farmingLevelWidget = new LevelWidget(BlocklingAttributes.Level.FARMING, blockling, combatLevelWidget.x, woodcuttingLevelWidget.y + LEVEL_XP_GAP);

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
        gatherWidget.render(matrixStack, mouseX, mouseY);
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
        gatherWidget.renderTooltip(matrixStack, mouseX, mouseY);
        movementWidget.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        onMouseClicked((int) mouseX, (int) mouseY, button);

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
        else if (gatherWidget.mouseClicked((int) mouseX, (int) mouseY, button))
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

        onMouseReleased((int) mouseX, (int) mouseY, button);

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int i, int j)
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
            nameField.keyPressed(keyCode, i, j);
        }

        if (!nameField.isFocused() && GuiUtil.isCloseInventoryKey(keyCode))
        {
            onClose();

            return true;
        }

        return super.keyPressed(keyCode, i, i);
    }

    @Override
    public boolean charTyped(char cah, int code)
    {
        nameField.charTyped(cah, code);

        return super.charTyped(cah, code);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    /**
     * A widget for displaying the blockling's health as a health bar.
     */
    private static class HealthBarWidget extends TexturedWidget
    {
        /**
         * The blockling.
         */
        @Nonnull
        private final BlocklingEntity blockling;

        /**
         * @param blockling the blockling.
         * @param x the x position.
         * @param y the y position.
         */
        public HealthBarWidget(@Nonnull BlocklingEntity blockling, int x, int y)
        {
            super(x, y, new GuiTexture(GuiTextures.STATS, 0, 228, 134, 5));
            this.blockling = blockling;
        }

        @Override
        public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
        {
            RenderSystem.color3f(0.5f, 0.5f, 0.5f);
            super.render(matrixStack, mouseX, mouseY);

            RenderSystem.color3f((float) (1.3f - (Math.ceil(blockling.getHealth()) / blockling.getMaxHealth())), 0.3f + (float) (Math.ceil(blockling.getHealth()) / blockling.getMaxHealth()), 0.1f);
            renderTexture(matrixStack, new GuiTexture(GuiTextures.STATS, 0, 228, (int) (134 * (Math.ceil(blockling.getHealth()) / blockling.getMaxHealth())), 5));

            int r = (int) (215 - blockling.getStats().getHealthPercentage() * 150);
            int g = (int) (50 + blockling.getStats().getHealthPercentage() * 180);
            int b = 50;
            String healthText = blockling.getStats().getHealth() + "/" + blockling.getStats().getMaxHealth();
            renderCenteredText(matrixStack, healthText, -width / 2, -1, false, (r << 16) + (g << 8) + b);

            if (isMouseOver(mouseX, mouseY))
            {
                List<ITextComponent> tooltip = StatsScreen.createModifiableFloatAttributeTooltip(blockling.getStats().maxHealth, TextFormatting.DARK_GREEN);
                tooltip.add(0, new StringTextComponent(TextFormatting.GOLD + new Attribute.AttributeTranslationTextComponent("health.name").getString()));

                screen.renderTooltip(matrixStack, tooltip.stream().map(ITextComponent::getVisualOrderText).collect(Collectors.toList()), mouseX, mouseY);
            }
        }
    }

    /**
     * A widget used to cycle through a set of icons and stats.
     */
    public static class EnumeratingStatWidget extends Widget
    {
        public static final int ICON_SIZE = 11;

        private static final int STAT_ICON_TEXTURE_Y = 166;

        private static final int TEXT_OFFSET_X = 4;
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
         * @param name the name for the set of stats.
         * @param x the x position.
         * @param y the y position.
         * @param shouldRightAlignText whether the text should be aligned right to left and on the left-hand side of the icon.
         * @param enumerationInterval the interval between each stat.
         * @param shouldCombineTooltips whether to combine all the tooltips into a single large tooltip.
         * @param blockling the blockling.
         */
        public EnumeratingStatWidget(@Nonnull ITextComponent name, int x, int y, boolean shouldRightAlignText, int enumerationInterval, boolean shouldCombineTooltips, @Nonnull BlocklingEntity blockling)
        {
            super(x, y, ICON_SIZE, ICON_SIZE);
            this.name = name;
            this.shouldRightAlignText = shouldRightAlignText;
            this.enumerationInterval = enumerationInterval;
            this.shouldCombineTooltips = shouldCombineTooltips;
            this.blockling = blockling;
        }

        /**
         * Adds a stat to the widget.
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
         * Adds a stat to the widget.
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
        public void render(MatrixStack matrixStack, int mouseX, int mouseY)
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
            renderText(matrixStack, valueSuppliers.get(currentEnumeration).get(), TEXT_OFFSET_X, TEXT_OFFSET_Y, shouldRightAlignText, 0xffe100);
        }

        public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY)
        {
            if (isMouseOver(mouseX, mouseY))
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
        }

        private List<ITextComponent> prependNameToTooltip(List<ITextComponent> tooltip)
        {
//            tooltip.add(0, new StringTextComponent("").getVisualOrderText());
            tooltip.add(0, name);

            return tooltip;
        }

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

            return tooltip;
        }

        @Override
        public boolean isMouseOver(int mouseX, int mouseY)
        {
            if (shouldRightAlignText)
            {
                return GuiUtil.isMouseOver(mouseX, mouseY, x - (font.width(valueSuppliers.get(currentEnumeration).get()) + TEXT_OFFSET_X + 3), y, width + font.width(valueSuppliers.get(currentEnumeration).get()) + TEXT_OFFSET_X + 3, height);
            }
            else
            {
                return GuiUtil.isMouseOver(mouseX, mouseY, x, y, width + font.width(valueSuppliers.get(currentEnumeration).get()) + TEXT_OFFSET_X + 3, height);
            }
        }

        @Override
        public boolean mouseClicked(int mouseX, int mouseY, int button)
        {
            super.mouseClicked(mouseX, mouseY, button);

            if (isMouseOver(mouseX, mouseY))
            {
                tickCount = blockling.tickCount;
                currentEnumeration = (currentEnumeration + 1) % iconTextures.size();

                while (!conditionSuppliers.get(currentEnumeration).get())
                {
                    currentEnumeration = (currentEnumeration + 1) % iconTextures.size();
                }

                return true;
            }

            return false;
        }
    }

    /**
     * A widget to render and handle a level.
     */
    public static class LevelWidget extends Widget
    {
        /**
         * The width and height of the icon next to the xp bar.
         */
        public static final int ICON_SIZE = 11;

        /**
         * The level.
         */
        @Nonnull
        private final BlocklingAttributes.Level level;

        /**
         * The blockling.
         */
        @Nonnull
        private final BlocklingEntity blockling;

        /**
         * The xp bar widget.
         */
        @Nonnull
        private final XpBarWidget xpBar;

        public LevelWidget(@Nonnull BlocklingAttributes.Level level, @Nonnull BlocklingEntity blockling, int x, int y)
        {
            super(x, y, ICON_SIZE + XpBarWidget.WIDTH + ICON_SIZE, ICON_SIZE);
            this.level = level;
            this.blockling = blockling;
            this.xpBar = new XpBarWidget(level, blockling, x + ICON_SIZE + 5, y + (ICON_SIZE - XpBarWidget.HEIGHT) / 2);
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY)
        {
            renderTexture(matrixStack, getTexture());
            xpBar.render(matrixStack, mouseX, mouseY);

            if (isMouseOver(mouseX, mouseY))
            {
                screen.renderTooltip(matrixStack, new StringTextComponent(blockling.getStats().getLevelXpAttribute(level).getValue() + "/" + BlocklingAttributes.getXpUntilNextLevel(blockling.getStats().getLevelAttribute(level).getValue())), mouseX, mouseY);
            }
        }

        /**
         * @return the texture for the current level.
         */
        @Nonnull
        private GuiTexture getTexture()
        {
            int level = blockling.getStats().getLevelAttribute(this.level).getValue();

            if (level >= 99)
            {
                return new GuiTexture(GuiTextures.STATS, ICON_SIZE * this.level.ordinal(), 166, ICON_SIZE, ICON_SIZE);
            }

            return new GuiTexture(GuiTextures.STATS, (level / 20) * (ICON_SIZE * 4) + (this.level.ordinal() * ICON_SIZE), 177, ICON_SIZE, ICON_SIZE);
        }

        private static class XpBarWidget extends Widget
        {
            /**
             * The width of the xp bar.
             */
            public static final int WIDTH = 111;

            /**
             * The height of the xp bar.
             */
            public static final int HEIGHT = 5;

            /**
             * The level.
             */
            @Nonnull
            private final BlocklingAttributes.Level level;

            /**
             * The blockling.
             */
            @Nonnull
            private final BlocklingEntity blockling;

            /**
             * The texture used for the background of the xp bar.
             */
            @Nonnull
            private final GuiTexture backgroundTexture;

            /**
             * @param level the level.
             * @param blockling the blockling.
             */
            public XpBarWidget(@Nonnull BlocklingAttributes.Level level, @Nonnull BlocklingEntity blockling, int x, int y)
            {
                super(x, y, WIDTH, HEIGHT);
                this.level = level;
                this.blockling = blockling;
                this.backgroundTexture = new GuiTexture(GuiTextures.STATS, 0, 193 + level.ordinal() * height * 2, width, height);
            }

            @Override
            public void render(MatrixStack matrixStack, int mouseX, int mouseY)
            {
                float percentage = blockling.getStats().getLevelXpAttribute(level).getValue() / (float) BlocklingAttributes.getXpUntilNextLevel(blockling.getStats().getLevelAttribute(level).getValue());
                int middle = (int) (width * percentage);

                renderTexture(matrixStack, backgroundTexture);
                renderTexture(matrixStack, new GuiTexture(GuiTextures.STATS, 0, 188 + level.ordinal() * height * 2, middle, height));
                renderText(matrixStack, "" + blockling.getStats().getLevelAttribute(level).getValue(), 6, 0, false, getTextColour());
            }

            /**
             * @return the colour of the text for the level.
             */
            private int getTextColour()
            {
                switch (level)
                {
                    case COMBAT: return 0xe03434;
                    case MINING: return 0x4870d4;
                    case WOODCUTTING: return 0x4db83d;
                    case FARMING: return 0xedcf24;
                    default: return 0xffffff;
                }
            }
        }
    }
}
