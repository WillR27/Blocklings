package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.attribute.Attribute;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingStats;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.screens.guis.TabbedGui;
import com.willr27.blocklings.gui.widgets.TextFieldWidget;
import com.willr27.blocklings.gui.widgets.TexturedWidget;
import com.willr27.blocklings.item.items.Items;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

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
    private static final int TOP_ICON_Y = 40;
    private static final int BOTTOM_ICON_Y = 68;

    private static final int COMBAT_ICON_TEXTURE_X = 0;
    private static final int MINING_ICON_TEXTURE_X = ICON_SIZE;
    private static final int WOODCUTTING_ICON_TEXTURE_X = ICON_SIZE * 2;
    private static final int FARMING_ICON_TEXTURE_X = ICON_SIZE * 3;
    private static final int LEVEL_XP_GAP = 16;
    private static final int LEVEL_ICON_X = 15;
    private static final int COMBAT_ICON_Y = 90;
    private static final int MINING_ICON_Y = COMBAT_ICON_Y + LEVEL_XP_GAP;
    private static final int WOODCUTTING_ICON_Y = COMBAT_ICON_Y + LEVEL_XP_GAP * 2;
    private static final int FARMING_ICON_Y = COMBAT_ICON_Y + LEVEL_XP_GAP * 3;

    private static final int COMBAT_XP_BAR_TEXTURE_Y = 188;
    private static final int MINING_XP_BAR_TEXTURE_Y = COMBAT_XP_BAR_TEXTURE_Y + XP_BAR_HEIGHT * 2;
    private static final int WOODCUTTING_XP_BAR_TEXTURE_Y = COMBAT_XP_BAR_TEXTURE_Y + XP_BAR_HEIGHT * 4;
    private static final int FARMING_XP_BAR_TEXTURE_Y = COMBAT_XP_BAR_TEXTURE_Y + XP_BAR_HEIGHT * 6;
    private static final int XP_BAR_X = 31;
    private static final int COMBAT_XP_BAR_Y = 93;
    private static final int MINING_XP_BAR_Y = COMBAT_XP_BAR_Y + LEVEL_XP_GAP;
    private static final int WOODCUTTING_XP_BAR_Y = COMBAT_XP_BAR_Y + LEVEL_XP_GAP * 2;
    private static final int FARMING_XP_BAR_Y = COMBAT_XP_BAR_Y + LEVEL_XP_GAP * 3;

    private BlocklingStats stats;

    private TexturedWidget healthIcon;
    private TexturedWidget armourIcon;
    private TexturedWidget damageIcon;
    private TexturedWidget speedIcon;

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

    @Override
    protected void init()
    {
        super.init();

        healthIcon = new TexturedWidget(font, contentLeft + LEFT_ICON_X, contentTop + TOP_ICON_Y, ICON_SIZE, ICON_SIZE, HEALTH_ICON_TEXTURE_X, STAT_ICON_TEXTURE_Y);
        armourIcon = new TexturedWidget(font, contentLeft + LEFT_ICON_X, contentTop + BOTTOM_ICON_Y, ICON_SIZE, ICON_SIZE, ARMOUR_ICON_TEXTURE_X, STAT_ICON_TEXTURE_Y);
        damageIcon = new TexturedWidget(font, contentRight - LEFT_ICON_X - ICON_SIZE, contentTop + TOP_ICON_Y, ICON_SIZE, ICON_SIZE, DAMAGE_ICON_TEXTURE_X, STAT_ICON_TEXTURE_Y);
        speedIcon = new TexturedWidget(font, contentRight - LEFT_ICON_X - ICON_SIZE, contentTop + BOTTOM_ICON_Y, ICON_SIZE, ICON_SIZE, SPEED_ICON_TEXTURE_X, STAT_ICON_TEXTURE_Y);

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
//        nameField.setEnableBackgroundDrawing(true);
        nameField.setVisible(true);
        nameField.setTextColor(16777215);
        nameField.setValue(blockling.getCustomName().getString());
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        GuiUtil.bindTexture(GuiUtil.STATS);
        blit(matrixStack, contentLeft, contentTop, 0, 0, TabbedGui.CONTENT_WIDTH, TabbedGui.CONTENT_HEIGHT);

        drawStatIcons(matrixStack, mouseX, mouseY);
        drawXpBars(matrixStack, mouseX, mouseY);
        GuiUtil.renderEntityOnScreen(centerX, centerY, 40, centerX - mouseX, centerY - mouseY, blockling);

        nameField.tick();
        nameField.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        drawTooltips(matrixStack, mouseX, mouseY);
    }

    private void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        List<IReorderingProcessor> tooltip = new ArrayList<>();

        if (combatXpBar.isMouseOver(mouseX, mouseY)) renderTooltip(matrixStack, blockling.getStats().combatXp.createTranslation("required", blockling.getStats().combatXp.getValue(), BlocklingStats.getXpUntilNextLevel(blockling.getStats().combatLevel.getValue())), mouseX, mouseY);
        else if (miningXpBar.isMouseOver(mouseX, mouseY)) renderTooltip(matrixStack, blockling.getStats().miningXp.createTranslation("required", blockling.getStats().miningXp.getValue(), BlocklingStats.getXpUntilNextLevel(blockling.getStats().miningLevel.getValue())), mouseX, mouseY);
        else if (woodcuttingXpBar.isMouseOver(mouseX, mouseY)) renderTooltip(matrixStack, blockling.getStats().woodcuttingXp.createTranslation("required", blockling.getStats().woodcuttingXp.getValue(), BlocklingStats.getXpUntilNextLevel(blockling.getStats().woodcuttingLevel.getValue())), mouseX, mouseY);
        else if (farmingXpBar.isMouseOver(mouseX, mouseY)) renderTooltip(matrixStack, blockling.getStats().farmingXp.createTranslation("required", blockling.getStats().farmingXp.getValue(), BlocklingStats.getXpUntilNextLevel(blockling.getStats().farmingLevel.getValue())), mouseX, mouseY);

        else if (combatIcon.isMouseOver(mouseX, mouseY))
        {
            tooltip.add(new Attribute.AttributeTranslationTextComponent("combat.speed", blockling.getStats().combatSpeed.getValue()).getVisualOrderText());
        }
        else if (miningIcon.isMouseOver(mouseX, mouseY))
        {
            tooltip.add(new Attribute.AttributeTranslationTextComponent("mining.speed", blockling.getStats().miningSpeed.getValue()).getVisualOrderText());
            tooltip.add(new Attribute.AttributeTranslationTextComponent("mining.range", blockling.getStats().miningRange.getValue()).getVisualOrderText());
        }
        else if (woodcuttingIcon.isMouseOver(mouseX, mouseY))
        {
            tooltip.add(new Attribute.AttributeTranslationTextComponent("woodcutting.speed", blockling.getStats().woodcuttingInterval.getValue()).getVisualOrderText());
            tooltip.add(new Attribute.AttributeTranslationTextComponent("woodcutting.range", blockling.getStats().woodcuttingRange.getValue()).getVisualOrderText());
        }
        else if (farmingIcon.isMouseOver(mouseX, mouseY))
        {
            tooltip.add(new Attribute.AttributeTranslationTextComponent("farming.speed", blockling.getStats().farmingInterval.getValue()).getVisualOrderText());
            tooltip.add(new Attribute.AttributeTranslationTextComponent("farming.range", blockling.getStats().farmingRange.getValue()).getVisualOrderText());
        }

        renderTooltip(matrixStack, tooltip, mouseX, mouseY);
    }

    private void drawStatIcons(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        GuiUtil.bindTexture(GuiUtil.STATS);

        healthIcon.render(matrixStack, mouseX, mouseY);
        armourIcon.render(matrixStack, mouseX, mouseY);
        damageIcon.render(matrixStack, mouseX, mouseY);
        speedIcon.render(matrixStack, mouseX, mouseY);

        combatIcon.render(matrixStack, mouseX, mouseY);
        miningIcon.render(matrixStack, mouseX, mouseY);
        woodcuttingIcon.render(matrixStack, mouseX, mouseY);
        farmingIcon.render(matrixStack, mouseX, mouseY);

        healthIcon.renderText(matrixStack, Integer.toString((int)blockling.getHealth()), 4, 1, false, 0xffe100);
        armourIcon.renderText(matrixStack, Integer.toString((int)stats.getArmour()), 4, 1, false, 0xffe100);
        damageIcon.renderText(matrixStack, Integer.toString((int)stats.getAttackDamage()), 4, 1, true, 0xffe100);
        speedIcon.renderText(matrixStack, Integer.toString((int)(stats.getMovementSpeed() * 40.0)), 4, 1, true, 0xffe100);
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
        farmingXpBar.renderText(matrixStack, "" + stats.farmingLevel.getValue
                (), 6, -1, false, 0x9d6d4a);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        nameField.mouseClicked(mouseX, mouseY, state);

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

    private class XpBar extends TexturedWidget
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
}
