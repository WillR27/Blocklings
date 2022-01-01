package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.attribute.IModifiable;
import com.willr27.blocklings.attribute.IModifier;
import com.willr27.blocklings.attribute.Operation;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.entity.entities.blockling.BlocklingStats;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.screens.guis.TabbedGui;
import com.willr27.blocklings.gui.widgets.TextFieldWidget;
import com.willr27.blocklings.gui.widgets.TexturedWidget;
import com.willr27.blocklings.gui.widgets.Widget;
import com.willr27.blocklings.item.items.Items;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.jline.utils.Log;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

public class StatsScreen extends TabbedScreen
{
    private static final int ICON_SIZE = 11;
    private static final int XP_BAR_WIDTH = 111;
    private static final int XP_BAR_HEIGHT = 5;
    private static final int STAT_ICON_TEXTURE_Y = 166;
    private static final int LEVEL_ICON_TEXTURE_Y = 177;

    private static final int LEFT_ICON_X = 20;
    private static final int TOP_ICON_Y = 51;
    private static final int BOTTOM_ICON_Y = 79;

    private static final int COMBAT_ICON_TEXTURE_X = 0;
    private static final int MINING_ICON_TEXTURE_X = ICON_SIZE;
    private static final int WOODCUTTING_ICON_TEXTURE_X = ICON_SIZE * 2;
    private static final int FARMING_ICON_TEXTURE_X = ICON_SIZE * 3;
    private static final int LEVEL_XP_GAP = 13;
    private static final int LEVEL_ICON_X = 15;
    private static final int COMBAT_ICON_Y = 101;
    private static final int MINING_ICON_Y = COMBAT_ICON_Y + LEVEL_XP_GAP;
    private static final int WOODCUTTING_ICON_Y = COMBAT_ICON_Y + LEVEL_XP_GAP * 2;
    private static final int FARMING_ICON_Y = COMBAT_ICON_Y + LEVEL_XP_GAP * 3;

    private static final int COMBAT_XP_BAR_TEXTURE_Y = 188;
    private static final int MINING_XP_BAR_TEXTURE_Y = COMBAT_XP_BAR_TEXTURE_Y + XP_BAR_HEIGHT * 2;
    private static final int WOODCUTTING_XP_BAR_TEXTURE_Y = COMBAT_XP_BAR_TEXTURE_Y + XP_BAR_HEIGHT * 4;
    private static final int FARMING_XP_BAR_TEXTURE_Y = COMBAT_XP_BAR_TEXTURE_Y + XP_BAR_HEIGHT * 6;
    private static final int XP_BAR_X = 31;
    private static final int COMBAT_XP_BAR_Y = 104;
    private static final int MINING_XP_BAR_Y = COMBAT_XP_BAR_Y + LEVEL_XP_GAP;
    private static final int WOODCUTTING_XP_BAR_Y = COMBAT_XP_BAR_Y + LEVEL_XP_GAP * 2;
    private static final int FARMING_XP_BAR_Y = COMBAT_XP_BAR_Y + LEVEL_XP_GAP * 3;

    private final BlocklingStats stats;

    private HealthBar healthBar;

    private EnumeratingWidget attackWidget;
    private EnumeratingWidget defenceWidget;
    private EnumeratingWidget gatherWidget;
    private EnumeratingWidget movementWidget;

    private TexturedWidget combatIcon;
    private TexturedWidget miningIcon;
    private TexturedWidget woodcuttingIcon;
    private TexturedWidget farmingIcon;

    private XpBar combatXpBar;
    private XpBar miningXpBar;
    private XpBar woodcuttingXpBar;
    private XpBar farmingXpBar;

    private TextFieldWidget nameField;

    public StatsScreen(BlocklingEntity blockling, PlayerEntity player)
    {
        super(blockling, player, "Stats");
        this.stats = blockling.getStats();
    }

    private List<ITextComponent> createModifiableFloatAttributeTooltip(IModifiable<Float> attribute, TextFormatting colour)
    {
        List<ITextComponent> tooltip = new ArrayList<>();

        tooltip.add(new StringTextComponent(colour + attribute.getDisplayStringValueSupplier().get() + " " + TextFormatting.GRAY + attribute.createTranslation("name").getString()));

        appendModifiableFloatAttributeToTooltip(attribute, tooltip, 1);

        return tooltip;
    }

    private void appendModifiableFloatAttributeToTooltip(IModifiable<Float> attribute, List<ITextComponent> tooltip, int depth)
    {
        for (IModifier<Float> modifier : attribute.getModifiers())
        {
            if (!modifier.isEnabled() || ((modifier.getValue() == 0.0f && modifier.getOperation() == Operation.ADD) || (modifier.getValue() == 1.0f && modifier.getOperation() != Operation.ADD)))
            {
                continue;
            }

            String sign = modifier.getValue() < 0.0f && modifier.getOperation() == Operation.ADD ? "" : modifier.getValue() < 1.0f && modifier.getOperation() != Operation.ADD ? "" : "+";
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + generate(() -> " ").limit(depth).collect(joining()) + sign + modifier.getDisplayStringValueSupplier().get() + " " + TextFormatting.DARK_GRAY + modifier.getDisplayStringNameSupplier().get()));

            if (modifier instanceof IModifiable<?>)
            {
                appendModifiableFloatAttributeToTooltip((IModifiable<Float>) modifier, tooltip, depth + 1);
            }
        }
    }

    @Override
    protected void init()
    {
        super.init();

        healthBar = new HealthBar(blockling, font, contentLeft + 20, contentTop + 36);

        attackWidget = new EnumeratingWidget(new BlocklingsTranslationTextComponent("stats.attack.name"), font, contentLeft + LEFT_ICON_X, contentTop + TOP_ICON_Y, ICON_SIZE, ICON_SIZE, false, 60, true, blockling);
        attackWidget.addEnumeration(() -> blockling.getEquipment().isAttackingWith(BlocklingHand.MAIN), stats.mainHandAttackDamage.displayStringValueSupplier, () -> createModifiableFloatAttributeTooltip(stats.mainHandAttackDamage, TextFormatting.DARK_RED), new GuiTexture(GuiUtil.STATS, ICON_SIZE * 10, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));
        attackWidget.addEnumeration(() -> blockling.getEquipment().isAttackingWith(BlocklingHand.OFF), stats.offHandAttackDamage.displayStringValueSupplier, () -> createModifiableFloatAttributeTooltip(stats.offHandAttackDamage, TextFormatting.DARK_RED), new GuiTexture(GuiUtil.STATS, 0, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));
        attackWidget.addEnumeration(() -> true, stats.attackSpeed.displayStringValueSupplier, () -> createModifiableFloatAttributeTooltip(stats.attackSpeed, TextFormatting.DARK_PURPLE), new GuiTexture(GuiUtil.STATS, 0, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE), Color.PINK);

        defenceWidget = new EnumeratingWidget(new BlocklingsTranslationTextComponent("stats.defence.name"), font, contentLeft + LEFT_ICON_X, contentTop + BOTTOM_ICON_Y, ICON_SIZE, ICON_SIZE, false, 60, true, blockling);
        defenceWidget.addEnumeration(() -> true, stats.armour.displayStringValueSupplier, () -> createModifiableFloatAttributeTooltip(stats.armour, TextFormatting.DARK_AQUA), new GuiTexture(GuiUtil.STATS, ICON_SIZE * 5, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));
        defenceWidget.addEnumeration(() -> true, stats.armourToughness.displayStringValueSupplier, () -> createModifiableFloatAttributeTooltip(stats.armourToughness, TextFormatting.AQUA), new GuiTexture(GuiUtil.STATS, ICON_SIZE * 6, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));
        defenceWidget.addEnumeration(() -> true, stats.knockbackResistance.displayStringValueSupplier, () -> createModifiableFloatAttributeTooltip(stats.knockbackResistance, TextFormatting.YELLOW), new GuiTexture(GuiUtil.STATS, ICON_SIZE * 7, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));

        gatherWidget = new EnumeratingWidget(new BlocklingsTranslationTextComponent("stats.gather.name"), font, contentRight - LEFT_ICON_X - ICON_SIZE, contentTop + TOP_ICON_Y, ICON_SIZE, ICON_SIZE, true, 60, true, blockling);
        gatherWidget.addEnumeration(() -> true, stats.miningSpeed.displayStringValueSupplier, () -> createModifiableFloatAttributeTooltip(stats.miningSpeed, TextFormatting.BLUE), new GuiTexture(GuiUtil.STATS, ICON_SIZE * 1, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));
        gatherWidget.addEnumeration(() -> true, stats.woodcuttingSpeed.displayStringValueSupplier, () -> createModifiableFloatAttributeTooltip(stats.woodcuttingSpeed, TextFormatting.DARK_GREEN), new GuiTexture(GuiUtil.STATS, ICON_SIZE * 2, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));
        gatherWidget.addEnumeration(() -> true, stats.farmingSpeed.displayStringValueSupplier, () -> createModifiableFloatAttributeTooltip(stats.farmingSpeed, TextFormatting.YELLOW), new GuiTexture(GuiUtil.STATS, ICON_SIZE * 3, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));

        movementWidget = new EnumeratingWidget(new BlocklingsTranslationTextComponent("stats.movement.name"), font, contentRight - LEFT_ICON_X - ICON_SIZE, contentTop + BOTTOM_ICON_Y, ICON_SIZE, ICON_SIZE, true, 60, true, blockling);
        movementWidget.addEnumeration(() -> true, stats.moveSpeed.displayStringValueSupplier, () -> createModifiableFloatAttributeTooltip(stats.moveSpeed, TextFormatting.BLUE), new GuiTexture(GuiUtil.STATS, ICON_SIZE * 8, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));

        combatIcon = new TexturedWidget(font, contentLeft + LEVEL_ICON_X, contentTop + COMBAT_ICON_Y, ICON_SIZE, ICON_SIZE, COMBAT_ICON_TEXTURE_X, LEVEL_ICON_TEXTURE_Y);
        miningIcon = new TexturedWidget(font, contentLeft + LEVEL_ICON_X, contentTop + MINING_ICON_Y, ICON_SIZE, ICON_SIZE, MINING_ICON_TEXTURE_X, LEVEL_ICON_TEXTURE_Y);
        woodcuttingIcon = new TexturedWidget(font, contentLeft + LEVEL_ICON_X, contentTop + WOODCUTTING_ICON_Y, ICON_SIZE, ICON_SIZE, WOODCUTTING_ICON_TEXTURE_X, LEVEL_ICON_TEXTURE_Y);
        farmingIcon = new TexturedWidget(font, contentLeft + LEVEL_ICON_X, contentTop + FARMING_ICON_Y, ICON_SIZE, ICON_SIZE, FARMING_ICON_TEXTURE_X, LEVEL_ICON_TEXTURE_Y);

        combatXpBar = new XpBar(font, contentLeft + XP_BAR_X, contentTop + COMBAT_XP_BAR_Y, XP_BAR_WIDTH, XP_BAR_HEIGHT, 0, COMBAT_XP_BAR_TEXTURE_Y);
        miningXpBar = new XpBar(font, contentLeft + XP_BAR_X, contentTop + MINING_XP_BAR_Y, XP_BAR_WIDTH, XP_BAR_HEIGHT, 0, MINING_XP_BAR_TEXTURE_Y);
        woodcuttingXpBar = new XpBar(font, contentLeft + XP_BAR_X, contentTop + WOODCUTTING_XP_BAR_Y, XP_BAR_WIDTH, XP_BAR_HEIGHT, 0, WOODCUTTING_XP_BAR_TEXTURE_Y);
        farmingXpBar = new XpBar(font, contentLeft + XP_BAR_X, contentTop + FARMING_XP_BAR_Y, XP_BAR_WIDTH, XP_BAR_HEIGHT, 0, FARMING_XP_BAR_TEXTURE_Y);

        nameField = new TextFieldWidget(font, contentLeft + 11, contentTop + 11, 154, 14, new StringTextComponent(""))
        {
            @Override
            public void setFocus(boolean focus)
            {
                if (!focus)
                {
                    if (!getValue().equals(""))
                    {
                        blockling.setCustomName(new StringTextComponent(getValue()));
                    }
                    else
                    {
                        ITextComponent name = Items.BLOCKLING.get().getName(Items.BLOCKLING.get().getDefaultInstance());
                        blockling.setCustomName(name);
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

    @Override
    public void tick()
    {
        nameField.tick();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        GuiUtil.bindTexture(GuiUtil.STATS);
        blit(matrixStack, contentLeft, contentTop, 0, 0, TabbedGui.CONTENT_WIDTH, TabbedGui.CONTENT_HEIGHT);

        GuiUtil.renderEntityOnScreen(centerX, centerY + 10, 35, centerX - mouseX, centerY - mouseY, blockling);

        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.0, 100.0);

        drawStatIcons(matrixStack, mouseX, mouseY);
        drawXpBars(matrixStack, mouseX, mouseY);

        healthBar.render(matrixStack, mouseX, mouseY);

        nameField.render(matrixStack, mouseX, mouseY, partialTicks);

        matrixStack.popPose();

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        drawTooltips(matrixStack, mouseX, mouseY);
    }

    private void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        attackWidget.renderTooltip(matrixStack, mouseX, mouseY);
        defenceWidget.renderTooltip(matrixStack, mouseX, mouseY);
        gatherWidget.renderTooltip(matrixStack, mouseX, mouseY);
        movementWidget.renderTooltip(matrixStack, mouseX, mouseY);

        List<IReorderingProcessor> tooltip = new ArrayList<>();

        if (combatXpBar.isMouseOver(mouseX, mouseY)) renderTooltip(matrixStack, blockling.getStats().combatXp.createTranslation("required", blockling.getStats().combatXp.getValue(), BlocklingStats.getXpUntilNextLevel(blockling.getStats().combatLevel.getValue())), mouseX, mouseY);
        else if (miningXpBar.isMouseOver(mouseX, mouseY)) renderTooltip(matrixStack, blockling.getStats().miningXp.createTranslation("required", blockling.getStats().miningXp.getValue(), BlocklingStats.getXpUntilNextLevel(blockling.getStats().miningLevel.getValue())), mouseX, mouseY);
        else if (woodcuttingXpBar.isMouseOver(mouseX, mouseY)) renderTooltip(matrixStack, blockling.getStats().woodcuttingXp.createTranslation("required", blockling.getStats().woodcuttingXp.getValue(), BlocklingStats.getXpUntilNextLevel(blockling.getStats().woodcuttingLevel.getValue())), mouseX, mouseY);
        else if (farmingXpBar.isMouseOver(mouseX, mouseY)) renderTooltip(matrixStack, blockling.getStats().farmingXp.createTranslation("required", blockling.getStats().farmingXp.getValue(), BlocklingStats.getXpUntilNextLevel(blockling.getStats().farmingLevel.getValue())), mouseX, mouseY);

        renderTooltip(matrixStack, tooltip, mouseX, mouseY);
    }

    private void drawStatIcons(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        attackWidget.render(matrixStack, mouseX, mouseY);
        defenceWidget.render(matrixStack, mouseX, mouseY);
        gatherWidget.render(matrixStack, mouseX, mouseY);
        movementWidget.render(matrixStack, mouseX, mouseY);

        GuiUtil.bindTexture(GuiUtil.STATS);

        combatIcon.render(matrixStack, mouseX, mouseY);
        miningIcon.render(matrixStack, mouseX, mouseY);
        woodcuttingIcon.render(matrixStack, mouseX, mouseY);
        farmingIcon.render(matrixStack, mouseX, mouseY);
    }

    private void drawXpBars(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        GuiUtil.bindTexture(GuiUtil.STATS);

        combatXpBar.render(matrixStack, mouseX, mouseY, stats.combatXp.getValue(), stats.combatLevel.getValue());
        miningXpBar.render(matrixStack, mouseX, mouseY, stats.miningXp.getValue(), stats.miningLevel.getValue());
        woodcuttingXpBar.render(matrixStack, mouseX, mouseY, stats.woodcuttingXp.getValue(), stats.woodcuttingLevel.getValue());
        farmingXpBar.render(matrixStack, mouseX, mouseY, stats.farmingXp.getValue(), stats.farmingLevel.getValue());

        combatXpBar.renderText(matrixStack, "" + stats.combatLevel.getValue(), 6, -1, false, 0xe03434);
        miningXpBar.renderText(matrixStack, "" + stats.miningLevel.getValue(), 6, -1, false, 0x4870d4);
        woodcuttingXpBar.renderText(matrixStack, "" + stats.woodcuttingLevel.getValue(), 6, -1, false, 0x4db83d);
        farmingXpBar.renderText(matrixStack, "" + stats.farmingLevel.getValue(), 6, -1, false, 0xedcf24);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        if (nameField.mouseClicked(mouseX, mouseY, state))
        {
            return true;
        }
        else if (attackWidget.mouseClicked((int) mouseX, (int) mouseY, state))
        {
            return true;
        }
        else if (defenceWidget.mouseClicked((int) mouseX, (int) mouseY, state))
        {
            return true;
        }
        else if (gatherWidget.mouseClicked((int) mouseX, (int) mouseY, state))
        {
            return true;
        }
        else if (movementWidget.mouseClicked((int) mouseX, (int) mouseY, state))
        {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        nameField.mouseReleased(mouseX, mouseY, state);

        return super.mouseReleased(mouseX, mouseY, state);
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

    private static class XpBar extends TexturedWidget
    {
        public XpBar(FontRenderer font, int x, int y, int width, int height, int textureX, int textureY)
        {
            super(font, x, y, width, height, textureX, textureY);
        }

        public void render(MatrixStack matrixStack, int mouseX, int mouseY, int xp, int level)
        {
            double percentage = xp / (double) BlocklingStats.getXpUntilNextLevel(level);
            int middle = (int)(width * percentage);

            blit(matrixStack, x, y, textureX, textureY + height, width, height);
            blit(matrixStack, x, y, textureX, textureY, middle, height);
        }
    }

    private static class HealthBar extends TexturedWidget
    {
        private final BlocklingEntity blockling;

        public HealthBar(BlocklingEntity blockling, FontRenderer font, int x, int y)
        {
            super(font, x, y, new GuiTexture(GuiUtil.STATS, 0, 228, 134, 5));
            this.blockling = blockling;
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY)
        {
            RenderSystem.color3f(0.5f, 0.5f, 0.5f);
            super.render(matrixStack, mouseX, mouseY);

            RenderSystem.color3f((float) (1.3f - (Math.ceil(blockling.getHealth()) / blockling.getMaxHealth())), 0.3f + (float) (Math.ceil(blockling.getHealth()) / blockling.getMaxHealth()), 0.1f);
            renderTexture(matrixStack, new GuiTexture(GuiUtil.STATS, 0, 228, (int) (134 * (Math.ceil(blockling.getHealth()) / blockling.getMaxHealth())), 5));

            int r = (int) (215 - blockling.getStats().getHealthPercentage() * 150);
            int g = (int) (50 + blockling.getStats().getHealthPercentage() * 180);
            int b = 50;
            String healthText = blockling.getStats().getHealth() + "/" + blockling.getStats().getMaxHealth();
            renderCenteredText(matrixStack, healthText, -width / 2, -1, false, (r << 16) + (g << 8) + b);
        }
    }

    public static class EnumeratingWidget extends Widget
    {
        private static final int TEXT_OFFSET_X = 4;
        private static final int TEXT_OFFSET_Y = 1;

        protected final List<Supplier<Boolean>> conditionSuppliers = new ArrayList<>();
        protected final List<Supplier<String>> valueSuppliers = new ArrayList<>();
        protected final List<Supplier<List<ITextComponent>>> tooltipSuppliers = new ArrayList<>();
        protected final List<GuiTexture> textures = new ArrayList<>();
        protected final List<Color> colours = new ArrayList<>();

        protected final ITextComponent name;
        protected final boolean shouldRightAlignText;
        protected final int enumerationInterval;
        protected final boolean shouldCombineTooltips;
        protected final BlocklingEntity blockling;

        protected int tickCount = 0;
        protected int currentEnumeration = 0;

        public EnumeratingWidget(ITextComponent name, FontRenderer font, int x, int y, int width, int height, boolean shouldRightAlignText, int enumerationInterval, boolean shouldCombineTooltips, BlocklingEntity blockling)
        {
            super(font, x, y, width, height);
            this.name = name;
            this.shouldRightAlignText = shouldRightAlignText;
            this.enumerationInterval = enumerationInterval;
            this.shouldCombineTooltips = shouldCombineTooltips;
            this.blockling = blockling;
        }

        public void addEnumeration(Supplier<Boolean> conditionSupplier, Supplier<String> valueSupplier, Supplier<List<ITextComponent>> tooltipSupplier, GuiTexture texture)
        {
            addEnumeration(conditionSupplier, valueSupplier, tooltipSupplier, texture, Color.WHITE);
        }

        public void addEnumeration(Supplier<Boolean> conditionSupplier, Supplier<String> valueSupplier, Supplier<List<ITextComponent>> tooltipSupplier, GuiTexture texture, Color colour)
        {
            conditionSuppliers.add(conditionSupplier);
            valueSuppliers.add(valueSupplier);
            tooltipSuppliers.add(tooltipSupplier);
            textures.add(texture);
            colours.add(colour);
            currentEnumeration = textures.size() - 1;
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY)
        {
            if (blockling.tickCount - tickCount > enumerationInterval)
            {
                tickCount = blockling.tickCount;
                currentEnumeration = (currentEnumeration + 1) % textures.size();
            }

            while (!conditionSuppliers.get(currentEnumeration).get())
            {
                currentEnumeration = (currentEnumeration + 1) % textures.size();
            }

            RenderSystem.color3f(colours.get(currentEnumeration).getRed() / 255.0f, colours.get(currentEnumeration).getGreen() / 255.0f, colours.get(currentEnumeration).getBlue() / 255.0f);
            renderTexture(matrixStack, textures.get(currentEnumeration));
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
        public boolean mouseClicked(int mouseX, int mouseY, int state)
        {
            super.mouseClicked(mouseX, mouseY, state);

            if (isMouseOver(mouseX, mouseY))
            {
                tickCount = blockling.tickCount;
                currentEnumeration = (currentEnumeration + 1) % textures.size();

                while (!conditionSuppliers.get(currentEnumeration).get())
                {
                    currentEnumeration = (currentEnumeration + 1) % textures.size();
                }

                return true;
            }

            return false;
        }
    }
}
