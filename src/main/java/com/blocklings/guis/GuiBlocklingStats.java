package com.blocklings.guis;

import com.blocklings.entities.EntityBlockling;
import com.blocklings.util.ResourceLocationBlocklings;
import com.blocklings.util.helpers.EntityHelper;
import com.blocklings.util.helpers.GuiHelper.Tab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiBlocklingStats extends GuiBlocklingBase
{
    private static final ResourceLocation WINDOW = new ResourceLocationBlocklings("textures/guis/inventory" + Tab.STATS.id + ".png");

    private GuiButton taskButton;
    private GuiButton guardButton;
    private GuiButton stateButton;
    private GuiTextFieldCentered nameTextField;

    GuiBlocklingStats(EntityBlockling blockling, EntityPlayer player)
    {
        super(blockling, player);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        buttonList.add(taskButton = new GuiButton(0, width / 2 - 81, height / 2 + 46, 52, 20, blockling.getTask().name));
        buttonList.add(guardButton = new GuiButton(1, width / 2 - 25, height / 2 + 46, 51, 20, blockling.getGuard().name));
        buttonList.add(stateButton = new GuiButton(2, width / 2 + 29, height / 2 + 46, 52, 20, blockling.getState().name));
        nameTextField = new GuiTextFieldCentered(3, fontRenderer, width / 2 - 80, height / 2 - 85, 160, 20)
        {
            public void setFocused(boolean isFocusedIn)
            {
                blockling.setName(nameTextField.getText());
                nameTextField.setText(blockling.getCustomNameTag());
                super.setFocused(isFocusedIn);
            }
        };
        nameTextField.setText(blockling.getCustomNameTag());
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        nameTextField.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();

        setDefaultRenderSettings();

        // Draw background
        mc.getTextureManager().bindTexture(WINDOW);
        drawTexturedModalRect(left, top, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        drawInfo();

        setDefaultRenderSettings();

        int yness = blockling.hasTool() ? 0 : 4;
        drawEntityOnScreen(width / 2, height / 2 + 16 - yness, 55, width / 2 - mouseX,  height / 2 - mouseY - 16 - yness, blockling);

        int size = 11;
        int i = -50, j = -22, k = -72;
        int xx = width / 2 + k;
        int yy = height / 2 + i;
        if (isMouseOver(mouseX, mouseY, xx, yy - (j * 0), size, size))
        {
            int xp = blockling.getCombatXp();
            int nextXp = EntityHelper.getXpUntilNextLevel(blockling.getCombatLevel());
            int speed = blockling.getAttackInterval();
            List<String> info = new ArrayList<>();
            info.add(TextFormatting.GOLD + "Combat");
            info.add(TextFormatting.GRAY + "XP: " + TextFormatting.DARK_AQUA + xp + "/" + nextXp);
            info.add(TextFormatting.GRAY + "Attack Interval: " + TextFormatting.DARK_RED + speed);
            drawHoveringText(info, mouseX, mouseY);
        }
        else if (isMouseOver(mouseX, mouseY, xx, yy - (j * 1), size, size))
        {
            int xp = blockling.getMiningXp();
            int nextXp = EntityHelper.getXpUntilNextLevel(blockling.getMiningLevel());
            int speed = blockling.getMiningInterval();
            List<String> info = new ArrayList<>();
            info.add(TextFormatting.GOLD + "Mining");
            info.add(TextFormatting.GRAY + "XP: " + TextFormatting.DARK_AQUA + xp + "/" + nextXp);
            info.add(TextFormatting.GRAY + "Mining Interval: " + TextFormatting.DARK_RED + speed);
            drawHoveringText(info, mouseX, mouseY);
        }
        else if (isMouseOver(mouseX, mouseY, xx, yy - (j * 2), size, size))
        {
            int xp = blockling.getWoodcuttingXp();
            int nextXp = EntityHelper.getXpUntilNextLevel(blockling.getWoodcuttingLevel());
            int speed = blockling.getChoppingInterval();
            List<String> info = new ArrayList<>();
            info.add(TextFormatting.GOLD + "Woodcutting");
            info.add(TextFormatting.GRAY + "XP: " + TextFormatting.DARK_AQUA + xp + "/" + nextXp);
            info.add(TextFormatting.GRAY + "Chopping Interval: " + TextFormatting.DARK_RED + speed);
            drawHoveringText(info, mouseX, mouseY);
        }
        else if (isMouseOver(mouseX, mouseY, xx, yy - (j * 3), size, size))
        {
            int xp = blockling.getFarmingXp();
            int nextXp = EntityHelper.getXpUntilNextLevel(blockling.getFarmingLevel());
            int speed = blockling.getFarmingInterval();
            List<String> info = new ArrayList<>();
            info.add(TextFormatting.GOLD + "Farming");
            info.add(TextFormatting.GRAY + "XP: " + TextFormatting.DARK_AQUA + xp + "/" + nextXp);
            info.add(TextFormatting.GRAY + "Farming Interval: " + TextFormatting.DARK_RED + speed);
            drawHoveringText(info, mouseX, mouseY);
        }

        xx = width / 2 - k - size;
        yy -= 2;
        if (isMouseOver(mouseX, mouseY, xx, yy - (j * 0), size, size))
        {
            float health = blockling.getHealth();
            float maxHealth = blockling.getMaxHealth();
            double healthPercentage = health / maxHealth;
            TextFormatting tf = healthPercentage < 0.33 ? TextFormatting.DARK_RED : healthPercentage < 0.66 ? TextFormatting.YELLOW : TextFormatting.GREEN;
            List<String> info = new ArrayList<>();
            info.add(TextFormatting.GOLD + "Health");
            info.add(tf + "" + ((int)health) + "/" + ((int)maxHealth));
            drawHoveringText(info, mouseX, mouseY);
        }
        else if (isMouseOver(mouseX, mouseY, xx, yy - (j * 1), size, size))
        {
            List<String> info = new ArrayList<>();
            info.add(TextFormatting.GOLD + "Damage");
            drawHoveringText(info, mouseX, mouseY);
        }
        else if (isMouseOver(mouseX, mouseY, xx, yy - (j * 2), size, size))
        {
            List<String> info = new ArrayList<>();
            info.add(TextFormatting.GOLD + "Attack Interval");
            drawHoveringText(info, mouseX, mouseY);
        }
        else if (isMouseOver(mouseX, mouseY, xx, yy - (j * 3), size, size))
        {
            List<String> info = new ArrayList<>();
            info.add(TextFormatting.GOLD + "Movement Speed");
            drawHoveringText(info, mouseX, mouseY);
        }

        setDefaultRenderSettings();
        nameTextField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawInfo()
    {
        int size = 11;
        int i = -50, j = -22, k = -72;
        int xx = width / 2 + k;
        int yy = height / 2 + i;
        drawTexturedModalRect(xx, yy - (j * 3), 0, TEXTURE_HEIGHT, size, size);
        drawTexturedModalRect(xx, yy - (j * 0), 11, TEXTURE_HEIGHT, size, size);
        drawTexturedModalRect(xx, yy - (j * 1), 22, TEXTURE_HEIGHT, size, size);
        drawTexturedModalRect(xx, yy - (j * 2), 33, TEXTURE_HEIGHT, size, size);

        xx += 15;
        yy += 2;
        int colour = 0xd1d1d1;

        colour = 0xb30000;
        colour = 0xff4d4d;
        String combatLevelString = Integer.toString(blockling.getCombatLevel());
        fontRenderer.drawString(combatLevelString, xx, yy - (j * 0), colour, true);
        colour = 0x2952a3;
        colour = 0x7094db;
        String miningLevelString = Integer.toString(blockling.getMiningLevel());
        fontRenderer.drawString(miningLevelString, xx, yy - (j * 1), colour, true);
        colour = 0x0a9306;
        colour = 0x57a65b;
        String woodcuttingLevelString = Integer.toString(blockling.getWoodcuttingLevel());
        fontRenderer.drawString(woodcuttingLevelString, xx, yy - (j * 2), colour, true);
        colour = 0x894d10;
        colour = 0x9d6d4a;
        String farmingLevelString = Integer.toString(blockling.getFarmingLevel());
        fontRenderer.drawString(farmingLevelString, xx, yy - (j * 3), colour, true);

        setDefaultRenderSettings();
        mc.getTextureManager().bindTexture(WINDOW);

        xx = width / 2 - k - size;
        yy -= 2;
        drawTexturedModalRect(xx, yy - (j * 0), 0, TEXTURE_HEIGHT + size, size, size);
        drawTexturedModalRect(xx, yy - (j * 1), 11, TEXTURE_HEIGHT + size, size, size);
        drawTexturedModalRect(xx, yy - (j * 2), 22, TEXTURE_HEIGHT + size, size, size);
        drawTexturedModalRect(xx, yy - (j * 3), 33, TEXTURE_HEIGHT + size, size, size);

        xx -= 15 - size;
        yy += 2;
        double health = blockling.getHealth();
        double maxHealth = blockling.getMaxHealth();
        double r = 163 - 92 * (health / maxHealth), g = 0 + 171 * (health / maxHealth), b = 0 + 3 * (health / maxHealth);
        colour = (int) r;
        colour = (colour << 8) + (int) g;
        colour = (colour << 8) + (int) b;
        String healthString = Integer.toString((int) health);
        fontRenderer.drawString(healthString, xx - fontRenderer.getStringWidth(healthString), yy - (j * 0), colour, true);
        colour = 0xfbba20;
        String damageString = "" + (int)blockling.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        fontRenderer.drawString(damageString, xx - fontRenderer.getStringWidth(damageString), yy - (j * 1), colour, true);
        String attackSpeedString = "" + blockling.getAttackInterval();
        fontRenderer.drawString(attackSpeedString, xx - fontRenderer.getStringWidth(attackSpeedString), yy - (j * 2), colour, true);
        String speedString = "" + (int)(blockling.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 40.0);
        fontRenderer.drawString(speedString, xx - fontRenderer.getStringWidth(speedString), yy - (j * 3), colour, true);

        size = 11;
        i = -50; j = -22; k = -72;
        xx = width / 2 + k;
        yy = height / 2 + i;
    }

    private static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityBlockling ent)
    {
        float scale2 = ent.getBlocklingScale();
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale) / scale2, (float)scale / scale2, (float)scale / scale2);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
        ent.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
        ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button == taskButton)
        {
            blockling.cycleTask();
            taskButton.displayString = blockling.getTask().name;
        }
        else if (button == guardButton)
        {
            blockling.cycleGuard();
            guardButton.displayString = blockling.getGuard().name;
        }
        else if (button == stateButton)
        {
            blockling.cycleState();
            stateButton.displayString = blockling.getState().name;
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        nameTextField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        switch (keyCode)
        {
            case 28:
                nameTextField.setFocused(false);
            default:
                nameTextField.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void onGuiClosed()
    {
        if (nameTextField != null) nameTextField.setFocused(false);
        blockling.setTask(blockling.getTask());
        blockling.setGuard(blockling.getGuard());
        blockling.setState(blockling.getState());

        super.onGuiClosed();
    }
}
