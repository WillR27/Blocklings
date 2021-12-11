package com.willr27.blocklings.gui.screens.guis;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingGuiInfo;
import com.willr27.blocklings.gui.GuiHandler;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.Tab;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class TabbedGui extends AbstractGui
{
    public static final int OFFSET_Y = -10;

    public static final int UI_WIDTH = 232;
    public static final int UI_HEIGHT = 166;

    public static final int CONTENT_WIDTH = 176;
    public static final int CONTENT_HEIGHT = 166;

    public static final int TAB_GAP = 4;
    public static final int TAB_HEIGHT = 28;

    public static final int LEFT_TAB_OFF_WIDTH = 25;
    public static final int RIGHT_TAB_OFF_WIDTH = 25;
    public static final int LEFT_TAB_ON_WIDTH = 32;
    public static final int RIGHT_TAB_ON_WIDTH = 32;

    public static final int TAB_OFF_OFFSET_X = 3;

    public static final int LEFT_TAB_OFF_TEXTURE_X = 0;
    public static final int RIGHT_TAB_OFF_TEXTURE_X = 26;
    public static final int LEFT_TAB_ON_TEXTURE_X = 52;
    public static final int RIGHT_TAB_ON_TEXTURE_X = 85;

    public static final int ICON_TEXTURE_Y = 28;
    public static final int ICON_SIZE = 22;
    public static final int ICON_OFFSET_X = 3;
    public static final int ICON_OFFSET_Y = 3;

    private BlocklingEntity blockling;
    private PlayerEntity player;
    private int centerX, centerY;
    private int left, top, right, bottom;

    public TabbedGui(BlocklingEntity blockling, PlayerEntity player, int centerX, int centerY)
    {
        this.blockling = blockling;
        this.player = player;
        this.centerX = centerX;
        this.centerY = centerY;
        this.left = centerX - UI_WIDTH / 2;
        this.top = centerY - UI_HEIGHT / 2;
        this.right = left + UI_WIDTH;
        this.bottom = top + UI_HEIGHT;
    }

    public void drawTabs(MatrixStack matrixStack)
    {
        GuiUtil.bindTexture(GuiUtil.TABS);

        int i = 0;
        for (Tab tab : Tab.leftTabs)
        {
//            if (tab == Tab.UTILITY_1 && blockling.getUtilityManager().getUtility1() == null)
//            {
//                i++;
//                continue;
//            }
//            else if (tab == Tab.UTILITY_2 && blockling.getUtilityManager().getUtility2() == null)
//            {
//                i++;
//                continue;
//            }

            int tabTexLocationY = 0;
            int iconTexLocationX = ICON_SIZE * tab.textureX;
            int iconTexLocationY = ICON_SIZE * tab.textureY + ICON_TEXTURE_Y;
            if (isActiveTab(tab))
            {
                blit(matrixStack, getLeftTabOnX(i), getLeftTabOnY(i), LEFT_TAB_ON_TEXTURE_X, tabTexLocationY, LEFT_TAB_ON_WIDTH, TAB_HEIGHT);

//                if (tab == Tab.UTILITY_1)
//                {
//                    RenderHelper.enableGUIStandardItemLighting();
//                    Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(blockling.equipmentInventory.getStackInSlot(EquipmentInventory.UTILITY_SLOT_1), getLeftIconOnX(i) + 3, getLeftIconOnY(i) + 3);
//                    GuiUtil.bindTexture(GuiUtil.TABS);
//                }
//                else if (tab == Tab.UTILITY_2)
//                {
//                    RenderHelper.enableGUIStandardItemLighting();
//                    Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(blockling.equipmentInventory.getStackInSlot(EquipmentInventory.UTILITY_SLOT_2), getLeftIconOnX(i) + 3, getLeftIconOnY(i) + 3);
//                    GuiUtil.bindTexture(GuiUtil.TABS);
//                }
//                else
                {
                    blit(matrixStack, getLeftIconOnX(i), getLeftIconOnY(i), iconTexLocationX, iconTexLocationY, ICON_SIZE, ICON_SIZE);
                }
            }
            else
            {
                blit(matrixStack, getLeftTabOffX(i), getLeftTabOffY(i), LEFT_TAB_OFF_TEXTURE_X, tabTexLocationY, LEFT_TAB_OFF_WIDTH, TAB_HEIGHT);

//                if (tab == Tab.UTILITY_1)
//                {
//                    RenderHelper.enableGUIStandardItemLighting();
//                    Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(blockling.equipmentInventory.getStackInSlot(EquipmentInventory.UTILITY_SLOT_1), getLeftIconOffX(i) + 3, getLeftIconOffY(i) + 3);
//                    GuiUtil.bindTexture(GuiUtil.TABS);
//                }
//                else if (tab == Tab.UTILITY_2)
//                {
//                    RenderHelper.enableGUIStandardItemLighting();
//                    Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(blockling.equipmentInventory.getStackInSlot(EquipmentInventory.UTILITY_SLOT_2), getLeftIconOffX(i) + 3, getLeftIconOffY(i) + 3);
//                    GuiUtil.bindTexture(GuiUtil.TABS);
//                }
//                else
                {
                    blit(matrixStack, getLeftIconOffX(i), getLeftIconOffY(i), iconTexLocationX, iconTexLocationY, ICON_SIZE, ICON_SIZE);
                }
            }
            i++;
        }

        i = 0;
        for (Tab tab : Tab.rightTabs)
        {
            int tabTexLocationY = 0;
            int iconTexLocationX = ICON_SIZE * tab.textureX;
            int iconTexLocationY = ICON_SIZE * tab.textureY + ICON_TEXTURE_Y;
            if (isActiveTab(tab))
            {
                blit(matrixStack, getRightTabOnX(i), getRightTabOnY(i), RIGHT_TAB_ON_TEXTURE_X, tabTexLocationY, RIGHT_TAB_ON_WIDTH, TAB_HEIGHT);
                blit(matrixStack, getRightIconOnX(i), getRightIconOnY(i), iconTexLocationX, iconTexLocationY, ICON_SIZE, ICON_SIZE);
            }
            else
            {
                blit(matrixStack, getRightTabOffX(i), getRightTabOffY(i), RIGHT_TAB_OFF_TEXTURE_X, tabTexLocationY, RIGHT_TAB_OFF_WIDTH, TAB_HEIGHT);
                blit(matrixStack, getRightIconOffX(i), getRightIconOffY(i), iconTexLocationX, iconTexLocationY, ICON_SIZE, ICON_SIZE);
            }
            i++;
        }
    }

    public void drawTooltip(MatrixStack matrixStack, int mouseX, int mouseY, Screen screen)
    {
        Tab hoveredTab = getHoveredTab(mouseX, mouseY);
        if (hoveredTab != null)
        {
//            if (hoveredTab == Tab.UTILITY_1 && blockling.getUtilityManager().getUtility1() == null)
//            {
//                return;
//            }
//            else if (hoveredTab == Tab.UTILITY_2 && blockling.getUtilityManager().getUtility2() == null)
//            {
//                return;
//            }

            screen.renderTooltip(matrixStack, hoveredTab.name, mouseX, mouseY);
        }
    }

    public boolean mouseReleased(int mouseX, int mouseY, int state)
    {
        Tab hoveredTab = getHoveredTab(mouseX, mouseY);
        if (hoveredTab != null)
        {
            int guiId = hoveredTab.guiId;
            int utility = -1;

//            if (hoveredTab == Tab.UTILITY_1)
//            {
//                Utility util = blockling.getUtilityManager().getUtility1();
//                if (util == null)
//                {
//                    return false;
//                }
//                utility = 1;
//            }
//            else if (hoveredTab == Tab.UTILITY_2)
//            {
//                Utility util = blockling.getUtilityManager().getUtility2();
//                if (util == null)
//                {
//                    return false;
//                }
//                utility = 2;
//            }

            GuiHandler.openGui(guiId, blockling, player);

            return true;
        }

        return false;
    }

    private Tab getHoveredTab(int mouseX, int mouseY)
    {
        int i = 0;
        for (Tab tab : Tab.leftTabs)
        {
            if ((isActiveTab(tab) && GuiUtil.isMouseOver(mouseX, mouseY, getLeftIconOffX(i), getLeftIconOffY(i), ICON_SIZE, ICON_SIZE))
             || !isActiveTab(tab) && GuiUtil.isMouseOver(mouseX, mouseY, getLeftIconOnX(i), getLeftIconOnY(i), ICON_SIZE, ICON_SIZE))
            {
                return tab;
            }
            i++;
        }
        i = 0;
        for (Tab tab : Tab.rightTabs)
        {
            if ((isActiveTab(tab) && GuiUtil.isMouseOver(mouseX, mouseY, getRightIconOffX(i), getRightIconOffY(i), ICON_SIZE, ICON_SIZE))
             || !isActiveTab(tab) && GuiUtil.isMouseOver(mouseX, mouseY, getRightIconOnX(i), getRightIconOnY(i), ICON_SIZE, ICON_SIZE))
            {
                return tab;
            }
            i++;
        }

        return null;
    }

    private Tab getActiveTab()
    {
        BlocklingGuiInfo guiInfo = blockling.getGuiInfo();

//        if (guiInfo.utility == -1)
//        {
            for (Tab tab : Tab.values())
            {
                if (tab.guiId == guiInfo.getCurrentGuiId())
                {
                    return tab;
                }
            }
//        }
//        else
//        {
//            return guiInfo.utility == 1 ? Tab.UTILITY_1 : Tab.UTILITY_2;
//        }

        return null;
    }

    private boolean isActiveTab(Tab tab)
    {
        return tab == getActiveTab();
    }

    private int getLeftIconOffX(int i)
    {
        return getLeftTabOffX(i) + ICON_OFFSET_X;
    }
    private int getLeftIconOffY(int i)
    {
        return getLeftTabOffY(i) + ICON_OFFSET_Y;
    }

    private int getRightIconOffX(int i)
    {
        return getRightTabOffX(i) + RIGHT_TAB_OFF_WIDTH - ICON_SIZE - ICON_OFFSET_X;
    }
    private int getRightIconOffY(int i)
    {
        return getRightTabOffY(i) + ICON_OFFSET_Y;
    }

    private int getLeftTabOffX(int i)
    {
        return left + TAB_OFF_OFFSET_X;
    }
    private int getLeftTabOffY(int i)
    {
        return top + 5 + ((TAB_HEIGHT + TAB_GAP) * i);
    }

    private int getRightTabOffX(int i)
    {
        return right - RIGHT_TAB_OFF_WIDTH - TAB_OFF_OFFSET_X;
    }
    private int getRightTabOffY(int i)
    {
        return top + 5 + ((TAB_HEIGHT + TAB_GAP) * i);
    }



    private int getLeftIconOnX(int i)
    {
        return getLeftTabOnX(i) + ICON_OFFSET_X + TAB_OFF_OFFSET_X;
    }
    private int getLeftIconOnY(int i)
    {
        return getLeftTabOnY(i) + ICON_OFFSET_Y;
    }

    private int getRightIconOnX(int i)
    {
        return getRightTabOnX(i) + RIGHT_TAB_ON_WIDTH - ICON_SIZE - ICON_OFFSET_X - TAB_OFF_OFFSET_X;
    }
    private int getRightIconOnY(int i)
    {
        return getRightTabOnY(i) + ICON_OFFSET_Y;
    }

    private int getLeftTabOnX(int i)
    {
        return left - 1;
    }
    private int getLeftTabOnY(int i)
    {
        return top + 5 + ((TAB_HEIGHT + TAB_GAP) * i);
    }

    private int getRightTabOnX(int i)
    {
        return right + 1 - RIGHT_TAB_ON_WIDTH;
    }
    private int getRightTabOnY(int i)
    {
        return top + 5 + ((TAB_HEIGHT + TAB_GAP) * i);
    }
}
