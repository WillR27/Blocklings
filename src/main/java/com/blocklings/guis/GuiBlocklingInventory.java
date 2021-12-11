package com.blocklings.guis;

import com.blocklings.entities.EntityBlockling;
import com.blocklings.inventories.ContainerInventoryBlockling;
import com.blocklings.inventories.InventoryBlockling;
import com.blocklings.util.ResourceLocationBlocklings;
import com.blocklings.util.helpers.GuiHelper;
import com.blocklings.util.helpers.GuiHelper.Tab;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

class GuiBlocklingInventory extends GuiContainer
{
    private static final ResourceLocation WINDOW = new ResourceLocationBlocklings("textures/guis/inventory" + Tab.INVENTORY.id + ".png");

    private EntityBlockling blockling;
    private EntityPlayer player;

    private int textureWidth = 232;
    private int textureHeight = 166;

    private int left, top;

    GuiBlocklingInventory(InventoryPlayer playerInv, InventoryBlockling blocklingInv, EntityBlockling blockling, EntityPlayer player)
    {
        super(new ContainerInventoryBlockling(blockling, playerInv, blocklingInv));

        this.blockling = blockling;
        this.player = player;

        xSize = 232;
        ySize = 166;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        xSize = 232;
        ySize = 166;

        left = guiLeft;
        top = guiTop + GuiHelper.YOFFSET;
    }

    @Override
    public void updateScreen()
    {
        left = guiLeft;
        top = guiTop + GuiHelper.YOFFSET;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        Tab tab = GuiHelper.getTabAt(mouseX, mouseY, width, height);

        if (tab != null)
        {
            drawHoveringText(tab.name, mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(WINDOW);
        this.drawTexturedModalRect(left, top, 0, 0, textureWidth, textureHeight);

        if (blockling.getUnlockedSlots() < 36)
        {
            int u = blockling.getUnlockedSlots() / 12;
            int xx = left + 89 + 54 * (u - 1);
            int yy = top + 7;
            int xxx = xx + 54 * (3 - u);
            int yyy = yy + 72;
            drawRect(xx, yy, xxx, yyy, 0x55550000);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        Tab tab = GuiHelper.getTabAt(mouseX, mouseY, width, height);

        if (tab != null && blockling.getGuiID() != tab.id)
        {
            blockling.openGui(tab.id, player);
            player.playSound(SoundEvents.BLOCK_STONE_HIT, 0.75f, 0.5f);
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
