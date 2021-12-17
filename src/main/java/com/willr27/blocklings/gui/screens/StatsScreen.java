package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingStats;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.screens.guis.TabbedGui;
import com.willr27.blocklings.gui.widgets.TextFieldWidget;
import com.willr27.blocklings.gui.widgets.TexturedWidget;
import com.willr27.blocklings.gui.widgets.Widget;
import com.willr27.blocklings.item.items.Items;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class StatsScreen extends TabbedScreen
{
    private static final int ICON_SIZE = 11;
    private static final int XP_BAR_WIDTH = 111;
    private static final int XP_BAR_HEIGHT = 5;
    private static final int STAT_ICON_TEXTURE_Y = 166;
    private static final int LEVEL_ICON_TEXTURE_Y = 177;

    private static final int HEALTH_ICON_TEXTURE_X = 0;
    private static final int ARMOUR_ICON_TEXTURE_X = ICON_SIZE;
    private static final int DAMAGE_ICON_TEXTURE_X = ICON_SIZE * 2;
    private static final int SPEED_ICON_TEXTURE_X = ICON_SIZE * 3;
    private static final int LEFT_ICON_X = 20;
    private static final int TOP_ICON_Y = 51;
    private static final int BOTTOM_ICON_Y = 79;

    private static final int COMBAT_ICON_TEXTURE_X = 0;
    private static final int MINING_ICON_TEXTURE_X = ICON_SIZE;
    private static final int WOODCUTTING_ICON_TEXTURE_X = ICON_SIZE * 2;
    private static final int FARMING_ICON_TEXTURE_X = ICON_SIZE * 3;
    private static final int LEVEL_XP_GAP = 13;
    private static final int LEVEL_ICON_X = 15;
    private static final int COMBAT_ICON_Y = 102;
    private static final int MINING_ICON_Y = COMBAT_ICON_Y + LEVEL_XP_GAP;
    private static final int WOODCUTTING_ICON_Y = COMBAT_ICON_Y + LEVEL_XP_GAP * 2;
    private static final int FARMING_ICON_Y = COMBAT_ICON_Y + LEVEL_XP_GAP * 3;

    private static final int COMBAT_XP_BAR_TEXTURE_Y = 188;
    private static final int MINING_XP_BAR_TEXTURE_Y = COMBAT_XP_BAR_TEXTURE_Y + XP_BAR_HEIGHT * 2;
    private static final int WOODCUTTING_XP_BAR_TEXTURE_Y = COMBAT_XP_BAR_TEXTURE_Y + XP_BAR_HEIGHT * 4;
    private static final int FARMING_XP_BAR_TEXTURE_Y = COMBAT_XP_BAR_TEXTURE_Y + XP_BAR_HEIGHT * 6;
    private static final int XP_BAR_X = 31;
    private static final int COMBAT_XP_BAR_Y = 103;
    private static final int MINING_XP_BAR_Y = COMBAT_XP_BAR_Y + LEVEL_XP_GAP;
    private static final int WOODCUTTING_XP_BAR_Y = COMBAT_XP_BAR_Y + LEVEL_XP_GAP * 2;
    private static final int FARMING_XP_BAR_Y = COMBAT_XP_BAR_Y + LEVEL_XP_GAP * 3;

    private final BlocklingStats stats;

    private HealthBar healthBar;

    private EnumeratingWidget attackWidget;
    private EnumeratingWidget defenceWidget;
    private EnumeratingWidget gatheringWidget;
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

    private List<IReorderingProcessor> attackDamageTooltip()
    {
        List<IReorderingProcessor> tooltip = new ArrayList<>();

        tooltip.add(new StringTextComponent(TextFormatting.DARK_RED + new BlocklingsTranslationTextComponent("stats.attack_damage", stats.getAttackDamage()).getString()).getVisualOrderText());

        return tooltip;
    }

    private List<IReorderingProcessor> attackSpeedTooltip()
    {
        List<IReorderingProcessor> tooltip = new ArrayList<>();

        tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + new BlocklingsTranslationTextComponent("stats.attack_speed", stats.combatSpeed.getValue()).getString()).getVisualOrderText());

        return tooltip;
    }

    private List<IReorderingProcessor> armourTooltip()
    {
        List<IReorderingProcessor> tooltip = new ArrayList<>();

        tooltip.add(new StringTextComponent(TextFormatting.DARK_AQUA + new BlocklingsTranslationTextComponent("stats.armour", stats.getArmour()).getString()).getVisualOrderText());

        return tooltip;
    }

    private List<IReorderingProcessor> armourToughnessTooltip()
    {
        List<IReorderingProcessor> tooltip = new ArrayList<>();

        tooltip.add(new StringTextComponent(TextFormatting.AQUA + new BlocklingsTranslationTextComponent("stats.armour_toughness", stats.getArmourToughness()).getString()).getVisualOrderText());

        return tooltip;
    }

    private List<IReorderingProcessor> knockbackResistanceTooltip()
    {
        List<IReorderingProcessor> tooltip = new ArrayList<>();

        tooltip.add(new StringTextComponent(TextFormatting.YELLOW + new BlocklingsTranslationTextComponent("stats.knockback_resistance", stats.getKnockbackResistance()).getString()).getVisualOrderText());

        return tooltip;
    }

    private List<IReorderingProcessor> miningSpeedTooltip()
    {
        List<IReorderingProcessor> tooltip = new ArrayList<>();

        tooltip.add(new StringTextComponent(TextFormatting.BLUE + new BlocklingsTranslationTextComponent("stats.mining_speed", stats.miningSpeed.getValue()).getString()).getVisualOrderText());

        return tooltip;
    }

    private List<IReorderingProcessor> woodcuttingSpeedTooltip()
    {
        List<IReorderingProcessor> tooltip = new ArrayList<>();

        tooltip.add(new StringTextComponent(TextFormatting.DARK_GREEN + new BlocklingsTranslationTextComponent("stats.woodcutting_speed", stats.woodcuttingSpeed.getValue()).getString()).getVisualOrderText());

        return tooltip;
    }

    private List<IReorderingProcessor> farmingSpeedTooltip()
    {
        List<IReorderingProcessor> tooltip = new ArrayList<>();

        tooltip.add(new StringTextComponent(TextFormatting.YELLOW + new BlocklingsTranslationTextComponent("stats.farming_speed", stats.farmingSpeed.getValue()).getString()).getVisualOrderText());

        return tooltip;
    }

    private List<IReorderingProcessor> moveSpeedTooltip()
    {
        List<IReorderingProcessor> tooltip = new ArrayList<>();

        tooltip.add(new StringTextComponent(TextFormatting.AQUA + new BlocklingsTranslationTextComponent("stats.move_speed", stats.moveSpeed.getValue()).getString()).getVisualOrderText());

        return tooltip;
    }

    @Override
    protected void init()
    {
        super.init();

        healthBar = new HealthBar(blockling, font, contentLeft + 20, contentTop + 36);

        attackWidget = new EnumeratingWidget(new BlocklingsTranslationTextComponent("stats.attack.name"), font, contentLeft + LEFT_ICON_X, contentTop + TOP_ICON_Y, ICON_SIZE, ICON_SIZE, false, 60, true, blockling);
        attackWidget.addEnumeration(() -> "" + stats.getAttackDamage(), this::attackDamageTooltip, new GuiTexture(GuiUtil.STATS, 0, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));
        attackWidget.addEnumeration(() -> String.format("%.1f", stats.combatSpeed.getValue()), this::attackSpeedTooltip, new GuiTexture(GuiUtil.STATS, 0, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE), Color.PINK);

        defenceWidget = new EnumeratingWidget(new BlocklingsTranslationTextComponent("stats.defence.name"), font, contentLeft + LEFT_ICON_X, contentTop + BOTTOM_ICON_Y, ICON_SIZE, ICON_SIZE, false, 60, true, blockling);
        defenceWidget.addEnumeration(() -> "" + stats.getArmour(), this::armourTooltip, new GuiTexture(GuiUtil.STATS, ICON_SIZE * 5, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));
        defenceWidget.addEnumeration(() -> "" + stats.getArmourToughness(), this::armourToughnessTooltip, new GuiTexture(GuiUtil.STATS, ICON_SIZE * 6, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));
        defenceWidget.addEnumeration(() -> "" + stats.getKnockbackResistance(), this::knockbackResistanceTooltip, new GuiTexture(GuiUtil.STATS, ICON_SIZE * 7, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));

        gatheringWidget = new EnumeratingWidget(new BlocklingsTranslationTextComponent("stats.gathering.name"), font, contentRight - LEFT_ICON_X - ICON_SIZE, contentTop + TOP_ICON_Y, ICON_SIZE, ICON_SIZE, true, 60, true, blockling);
        gatheringWidget.addEnumeration(() -> String.format("%.1f", stats.miningSpeed.getValue()), this::miningSpeedTooltip, new GuiTexture(GuiUtil.STATS, ICON_SIZE * 1, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));
        gatheringWidget.addEnumeration(() -> String.format("%.1f", stats.woodcuttingSpeed.getValue()), this::woodcuttingSpeedTooltip, new GuiTexture(GuiUtil.STATS, ICON_SIZE * 2, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));
        gatheringWidget.addEnumeration(() -> String.format("%.1f", stats.farmingSpeed.getValue()), this::farmingSpeedTooltip, new GuiTexture(GuiUtil.STATS, ICON_SIZE * 3, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));

        movementWidget = new EnumeratingWidget(new BlocklingsTranslationTextComponent("stats.movement.name"), font, contentRight - LEFT_ICON_X - ICON_SIZE, contentTop + BOTTOM_ICON_Y, ICON_SIZE, ICON_SIZE, true, 60, true, blockling);
        movementWidget.addEnumeration(() -> String.format("%.1f", stats.moveSpeed.getValue()), this::moveSpeedTooltip, new GuiTexture(GuiUtil.STATS, ICON_SIZE * 8, STAT_ICON_TEXTURE_Y, ICON_SIZE, ICON_SIZE));

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

        nameField.tick();
        nameField.render(matrixStack, mouseX, mouseY, partialTicks);

        matrixStack.popPose();

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        drawTooltips(matrixStack, mouseX, mouseY);
    }

    private void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        attackWidget.renderTooltip(matrixStack, mouseX, mouseY);
        defenceWidget.renderTooltip(matrixStack, mouseX, mouseY);
        gatheringWidget.renderTooltip(matrixStack, mouseX, mouseY);
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
        gatheringWidget.render(matrixStack, mouseX, mouseY);
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

        combatXpBar.renderText(matrixStack, "" + stats.combatLevel.getValue(), 6, -1, false, 0xff4d4d);
        miningXpBar.renderText(matrixStack, "" + stats.miningLevel.getValue(), 6, -1, false, 0x7094db);
        woodcuttingXpBar.renderText(matrixStack, "" + stats.woodcuttingLevel.getValue(), 6, -1, false, 0x57a65b);
        farmingXpBar.renderText(matrixStack, "" + stats.farmingLevel.getValue(), 6, -1, false, 0x9d6d4a);
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
        else if (gatheringWidget.mouseClicked((int) mouseX, (int) mouseY, state))
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

            String healthText = blockling.getStats().getHealth() + "/" + blockling.getStats().getMaxHealth();
            renderCenteredText(matrixStack, healthText, -width / 2, -1, false, 0xffffff);
        }
    }

    public static class EnumeratingWidget extends Widget
    {
        private static final int TEXT_OFFSET_X = 4;
        private static final int TEXT_OFFSET_Y = 1;

        protected final List<Supplier<String>> valueSuppliers = new ArrayList<>();
        protected final List<Supplier<List<IReorderingProcessor>>> tooltipSuppliers = new ArrayList<>();
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

        public void addEnumeration(Supplier<String> valueSupplier, Supplier<List<IReorderingProcessor>> tooltipSupplier, GuiTexture texture)
        {
            addEnumeration(valueSupplier, tooltipSupplier, texture, Color.WHITE);
        }

        public void addEnumeration(Supplier<String> valueSupplier, Supplier<List<IReorderingProcessor>> tooltipSupplier, GuiTexture texture, Color colour)
        {
            textures.add(texture);
            valueSuppliers.add(valueSupplier);
            tooltipSuppliers.add(tooltipSupplier);
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
                    screen.renderTooltip(matrixStack, prependNameToTooltip(combineTooltips()), mouseX, mouseY);
                }
                else
                {
                    screen.renderTooltip(matrixStack, prependNameToTooltip(tooltipSuppliers.get(currentEnumeration).get()), mouseX, mouseY);
                }
            }
        }

        private List<IReorderingProcessor> prependNameToTooltip(List<IReorderingProcessor> tooltip)
        {
//        tooltip.add(0, new StringTextComponent("").getVisualOrderText());
            tooltip.add(0, name.getVisualOrderText());

            return tooltip;
        }

        private List<IReorderingProcessor> combineTooltips()
        {
            List<IReorderingProcessor> tooltip = new ArrayList<>();

            for (Supplier<List<IReorderingProcessor>> tooltipSupplier : tooltipSuppliers)
            {
                tooltip.addAll(tooltipSupplier.get());
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

                return true;
            }

            return false;
        }
    }
}
