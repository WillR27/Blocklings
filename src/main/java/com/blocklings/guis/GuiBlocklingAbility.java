package com.blocklings.guis;

import com.blocklings.abilities.Ability;
import com.blocklings.entities.EntityBlockling;
import com.blocklings.util.ResourceLocationBlocklings;
import com.blocklings.util.helpers.EntityHelper;
import com.blocklings.util.helpers.GuiHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

abstract class GuiBlocklingAbility extends GuiBlocklingBase
{
    protected Random rand = new Random();

    protected ResourceLocation BACKGROUND = new ResourceLocationBlocklings("textures/guis/inventory_overlay.png");
    protected static final ResourceLocation ABILITIES = new ResourceLocationBlocklings("textures/guis/inventory_abilities.png");
    protected static final ResourceLocation ABILITIES2 = new ResourceLocationBlocklings("textures/guis/inventory_abilities2.png");
    protected static final ResourceLocation ABILITIES3 = new ResourceLocationBlocklings("textures/guis/inventory_abilities3.png");
    protected static ResourceLocation WINDOW = new ResourceLocationBlocklings("textures/guis/inventory3.png");

    protected int minScreenX = 0;
    protected int minScreenY = 0;
    protected int maxScreenX = 0;
    protected int maxScreenY = 0;

    protected List<Ability> abilities = new ArrayList<Ability>();

    /**
     * Relative x position for ability screen
     */
    protected int x;
    /**
     * Relative y position for ability screen
     */
    protected int y;

    /**
     * Current ability the mouse is over
     * Will return null if no ability
     */
    protected Ability hoveredAbility;

    /**
     * Needed to ensure screen isn't reset when resizing window/maximising
     * This is because initGui is called again on any screen size change
     */
    protected boolean init = true;

    private boolean haveNotMovedSinceMouseClick = false;
    private int beforeReleaseX, beforeReleaseY;

    private GuiButton buyButton;
    GuiButton reallocateButton;

    protected GuiBlocklingAbility(EntityBlockling blockling, EntityPlayer player)
    {
        super(blockling, player);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        buttonList.add(buyButton = new GuiButton(0, width / 2 - 77, height / 2 + 76, 72, 20, "Buy Ability"));
        buttonList.add(reallocateButton = new GuiButton(1, width / 2 + 5, height / 2 + 76, 72, 20, "Reallocate"));

        if (init)
        {
            int minX = -10000, minY = -10000;
            int maxX = 10000, maxY = 10000;

            for (Ability ability : abilities)
            {
                minX = ability.x + ability.width > minX ? ability.x + ability.width : minX;
                minY = ability.y + ability.height > minY ? ability.y + ability.height : minY;

                maxX = ability.x < maxX ? ability.x : maxX;
                maxY = ability.y < maxY ? ability.y : maxY;
            }

            minScreenX = SCREEN_WIDTH - minX - 35;
            minScreenY = SCREEN_HEIGHT - minY - 35;
            maxScreenX = -maxX + 35;
            maxScreenY = -maxY + 35;

            x = minScreenX + ((maxScreenX - minScreenX) / 2);
            y = minScreenY + ((maxScreenY - minScreenY) / 2);

            init = false;
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        for (Ability ability : abilities)
        {
            boolean hasLevels = true;
            for (String skill : ability.levelRequirements.keySet())
            {
                if (blockling.getLevel(skill) < ability.levelRequirements.get(skill))
                {
                    hasLevels = false;
                    break;
                }
            }

            if (ability.state != Ability.State.ACQUIRED && hasLevels && (ability.parentAbility == null || ability.parentAbility.state == Ability.State.ACQUIRED))
            {
                ability.state = Ability.State.UNLOCKED;
            }
            else if (ability.state == Ability.State.UNLOCKED)
            {
                ability.state = Ability.State.LOCKED;
            }
        }

        buyButton.enabled = selectedAbility != null && blockling.getSkillPoints() >= selectedAbility.skillPointCost;
        reallocateButton.enabled = false;
        for (Ability ability : abilities)
        {
            if (ability.state == Ability.State.ACQUIRED)
            {
                reallocateButton.enabled = true;
                break;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        hoveredAbility = getAbilityAtMouseLocation(mouseX, mouseY);

        updateXY(mouseX, mouseY);

        drawDefaultBackground();
        setDefaultRenderSettings();

        // Draw background
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawTexturedModalRect(screenLeft, screenTop, 16 - Math.abs((x + 10000) % 16), 16 - Math.abs((y + 10000) % 16), SCREEN_WIDTH, SCREEN_HEIGHT);

        drawLines();
        drawAbilities();

        setDefaultRenderSettings();

        // Darken ability area when hovering over ability
        int colour = 0x00ffffff;
        if (getAbilityAtMouseLocation(mouseX, mouseY) != null) colour = 0x6a000000;
        drawRect(screenLeft, screenTop, screenLeft + SCREEN_WIDTH, screenTop + SCREEN_HEIGHT, colour);

        setDefaultRenderSettings();

        // Draw main window
        mc.getTextureManager().bindTexture(WINDOW);

        drawWindow();

        drawHover();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    Ability selectedAbility = null;
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (beforeReleaseX == x && beforeReleaseY == y)
        {
            Ability ability = getAbilityAtMouseLocation(mouseX, mouseY);
            selectedAbility = ability;

            if (ability != null)
            {
                if (ability.state != Ability.State.UNLOCKED || ability.hasConflictingAbility(abilities))
                {
                    selectedAbility = null;
                }
            }

            super.mouseReleased(mouseX, mouseY, state);
        }

        isClicking = false;
        haveNotMovedSinceMouseClick = false;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button == buyButton)
        {
            if (buyButton.enabled)
            {
                selectedAbility.state = Ability.State.ACQUIRED;
                blockling.incrementSkillPoints(-selectedAbility.skillPointCost);
                if (rand.nextFloat() < 0.5f) player.playSound(SoundEvents.BLOCK_NOTE_BELL, 0.75f, 1.0f);
                else player.playSound(SoundEvents.BLOCK_NOTE_CHIME, 0.75f, 1.0f);
            }
        }
        else if (button == reallocateButton)
        {
            for (Ability ability : abilities)
            {
                if (ability.state == Ability.State.ACQUIRED)
                {
                    boolean loseSkillPoint = rand.nextFloat() >= 0.5;
                    if (loseSkillPoint)
                    {
                        int i = 0;
                        int highest = blockling.getCombatLevel();
                        if (blockling.getMiningLevel() > highest) { highest = blockling.getMiningLevel(); i = 1; }
                        if (blockling.getWoodcuttingLevel() > highest) { highest = blockling.getWoodcuttingLevel(); i = 2; }
                        if (blockling.getFarmingLevel() > highest) { i = 3; }

                        int noOfSkillPointsToLose = 0;
                        int highest2 = highest;
                        while (highest2 > 1 && noOfSkillPointsToLose != ability.skillPointCost)
                        {
                            noOfSkillPointsToLose++;
                            highest2-= EntityHelper.SKILL_POINT_INTERVAL;
                        }

                        if (noOfSkillPointsToLose > 0)
                        {
                            blockling.incrementSkillPoints(-noOfSkillPointsToLose);
                            switch (i)
                            {
                                case 0:
                                    blockling.setCombatLevel(highest - (noOfSkillPointsToLose * EntityHelper.SKILL_POINT_INTERVAL));
                                    if (blockling.getCombatLevel() <= 0) blockling.setCombatLevel(1);
                                    blockling.setCombatXp(0);
                                    break;
                                case 1:
                                    blockling.setMiningLevel(highest - (noOfSkillPointsToLose * EntityHelper.SKILL_POINT_INTERVAL));
                                    if (blockling.getMiningLevel() <= 0) blockling.setMiningLevel(1);
                                    blockling.setMiningXp(0);
                                    break;
                                case 2:
                                    blockling.setWoodcuttingLevel(highest - (noOfSkillPointsToLose * EntityHelper.SKILL_POINT_INTERVAL));
                                    if (blockling.getWoodcuttingLevel() <= 0) blockling.setWoodcuttingLevel(1);
                                    blockling.setWoodcuttingXp(0);
                                    break;
                                case 3:
                                    blockling.setFarmingLevel(highest - (noOfSkillPointsToLose * EntityHelper.SKILL_POINT_INTERVAL));
                                    if (blockling.getFarmingLevel() <= 0) blockling.setFarmingLevel(1);
                                    blockling.setFarmingXp(0);
                                    break;
                            }
                        }
                    }

                    ability.state = Ability.State.LOCKED;
                    blockling.incrementSkillPoints(ability.skillPointCost);
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        haveNotMovedSinceMouseClick = true;
        beforeReleaseX = x;
        beforeReleaseY = y;
    }

    protected void drawHover()
    {
        mc.getTextureManager().bindTexture(WINDOW);
        zLevel += 15;

        if (hoveredAbility != null)
        {
            String text1 = hoveredAbility.name;

            LinkedHashMap<String, Integer> desc = new LinkedHashMap<String, Integer>()
            {{
                for (String str : hoveredAbility.description)
                {
                    put(str, 0xffffff);
                }
                put("", 0xffffff);
                put("Skill Point(s): " + Integer.toString(hoveredAbility.skillPointCost), blockling.getSkillPoints() >= hoveredAbility.skillPointCost ? 0xaaffaa : 0xffaaaa);
                for (String skill : hoveredAbility.levelRequirements.keySet())
                {
                    put(skill + " Level: " + Integer.toString(hoveredAbility.levelRequirements.get(skill)), blockling.getLevel(skill) >= hoveredAbility.levelRequirements.get(skill) ? 0xaaffaa : 0xffaaaa);
                }
            }};

            int width1 = fontRenderer.getStringWidth(text1) + 34;
            int width2 = 100;
            for (String string : desc.keySet())
            {
                width2 = width2 < fontRenderer.getStringWidth(string) ? fontRenderer.getStringWidth(string) : width2;
            }

            int startX = actualAbilityX(hoveredAbility) - 5, startY = actualAbilityY(hoveredAbility) + 2;
            int width = 90;

            if (width1 > width2) width = width1;
            else width = width2 + 2;

            for (int i = 0; i < desc.size(); i++)
            {
                if (i == 0)
                {
                    drawTexturedModalRect(startX, startY + 17 + (16 * i), 0, TEXTURE_HEIGHT + 20, width, 20);
                    drawTexturedModalRect(startX + width, startY + 17 + (16 * i), 192, TEXTURE_HEIGHT + 20, 8, 20);
                }
                else if (i == desc.size() - 1)
                {
                    drawTexturedModalRect(startX, startY + 17 + (12 * i) + 2, 0, TEXTURE_HEIGHT + 22, width, 18);
                    drawTexturedModalRect(startX + width, startY + 17 + (12 * i) + 2, 192, TEXTURE_HEIGHT + 22, 8, 18);
                }
                else
                {
                    drawTexturedModalRect(startX, startY + 17 + (12 * i) - 2, 0, TEXTURE_HEIGHT + 22, width, 18);
                    drawTexturedModalRect(startX + width, startY + 17 + (12 * i) - 2, 192, TEXTURE_HEIGHT + 22, 8, 18);
                }
            }
            GlStateManager.color(hoveredAbility.highlightColour.getRed() / 255f, hoveredAbility.highlightColour.getGreen() / 255f, hoveredAbility.highlightColour.getBlue() / 255f);
            drawTexturedModalRect(startX, startY, 0, TEXTURE_HEIGHT, width, 20);
            drawTexturedModalRect(startX + width, startY, 192, TEXTURE_HEIGHT, 8, 20);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

            GlStateManager.translate(0, 0, 25);
            fontRenderer.drawString(text1, startX + 34, startY + 6, 0xffffff, true);
            for (int i = 0; i < desc.size(); i++)
            {
                String string = (String)desc.keySet().toArray()[i];
                fontRenderer.drawString(string, startX + 5, startY + 24 + (12 * i), desc.get(string), true);
            }
            GlStateManager.translate(0, 0, -25);
        }

        zLevel -= 15;
    }

    protected void drawWindow()
    {
        zLevel += 10;
        drawTexturedModalRect(left, top, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        GlStateManager.translate(0, 0, 11);
        String skillPoints = Integer.toString(blockling.getSkillPoints());
        int center = fontRenderer.getStringWidth(skillPoints) / 2;
        if (skillPoints.length() == 2) center = 0;
        fontRenderer.drawString(skillPoints, screenLeft + 12 + center, screenTop - 1, 0x333333);
        fontRenderer.drawString(skillPoints, screenLeft + 11 + center, screenTop - 2, 0xffffff);
        GlStateManager.translate(0, 0, -11);
        zLevel -= 10;
    }

    /**
     * Draw lines between abilities
     * Won't work if parent abilities are lower than children
     * Is inefficient so could do with a rework
     */
    private void drawLines()
    {
        for (Ability ability : abilities)
        {
            for (Ability child : ability.getChildren(abilities))
            {
                int colour1 = ability.state.colour.brighter().getRGB();
                int colour2 = 0xff121212;

                int abilityX = ability.x + (ability.width / 2), abilityY = ability.y + (ability.height / 2);
                int childX = child.x + (child.width / 2), childY = child.y + (child.height / 2);

                int difX = abilityX - childX, difY = abilityY - childY;
                int cornerX = abilityX - difX, cornerY = abilityY - difY;

                int startX = screenLeft + x + abilityX, endX = screenLeft + x + cornerX;
                int startY = screenTop + y + abilityY, endY = screenTop + y + cornerY;

                // Swap start and end x values so always draw left to right
                if (difX > 0)
                {
                    int i = startX;
                    startX = endX;
                    endX = i;
                }
                if (difY > 0)
                {
                    int i = startY;
                    startY = endY;
                    endY = i;
                }

                if (startX < screenLeft)
                    startX = screenLeft;
                else if (startX > screenLeft + SCREEN_WIDTH)
                    startX = screenLeft + SCREEN_WIDTH;
                if (startY < screenTop)
                    startY = screenTop;
                else if (startY > screenTop + SCREEN_HEIGHT + 4)
                    startY = screenTop + SCREEN_HEIGHT + 4;
                if (endX < screenLeft)
                    endX = screenLeft;
                else if (endX > screenLeft + SCREEN_WIDTH)
                    endX = screenLeft + SCREEN_WIDTH;
                if (endY < screenTop)
                    endY = screenTop;
                else if (endY > screenTop + SCREEN_HEIGHT + 4)
                    endY = screenTop + SCREEN_HEIGHT + 4;

                int changeX = 0;
                int changeY = 0;
                if (difX < 0)
                    changeX = -1;

                drawHorizontalLine(startX + changeX, endX + changeX, startY + changeY, colour1);
                if (difX > 0)
                {
                    drawVerticalLine(startX + changeX, endY + changeY, startY + changeY, colour1);
                    drawVerticalLine(startX + changeX, endY + changeY - 2, endY + changeY, colour2);
                }
                else
                {
                    drawVerticalLine(endX + changeX, endY + changeY, startY + changeY, colour1);
                    drawVerticalLine(endX + changeX, endY + changeY - 2, endY + changeY, colour2);
                }

                changeX = -1;
                changeY = -1;
                if (difX < 0)
                    changeX = 0;

                drawHorizontalLine(startX + changeX, endX + changeX, startY + changeY, colour1);
                if (difX > 0)
                {
                    drawVerticalLine(startX + changeX, endY + changeY, startY + changeY, colour1);
                    drawVerticalLine(startX + changeX, endY + changeY - 2, endY + changeY, colour2);
                }
                else
                {
                    drawVerticalLine(endX + changeX, endY + changeY, startY + changeY, colour1);
                    drawVerticalLine(endX + changeX, endY + changeY - 2, endY + changeY, colour2);
                }

                changeX = -2;
                changeY = -2;
                if (difX < 0)
                    changeX = 1;

                drawHorizontalLine(startX + changeX, endX + changeX, startY + changeY, colour2);
                if (difX > 0)
                {
                    drawVerticalLine(startX + changeX, endY + changeY - 2, startY + changeY, colour2);
                }
                else
                {
                    drawVerticalLine(endX + changeX, endY + changeY - 2, startY + changeY, colour2);
                }

                changeX = 1;
                changeY = 1;
                if (difX < 0)
                    changeX = -2;

                drawHorizontalLine(startX + changeX, endX + changeX, startY + changeY, colour2);
                if (difX > 0)
                {
                    drawVerticalLine(startX + changeX, endY + changeY - 2, startY + changeY, colour2);
                }
                else
                {
                    drawVerticalLine(endX + changeX, endY + changeY - 2, startY + changeY, colour2);
                }
            }
        }
    }

    /**
     * Draw all abilities in list
     */
    private void drawAbilities()
    {
        for (Ability ability : abilities)
        {
            drawAbility(ability);
        }
    }

    /**
     * Draw an ability while taking into account the relative position of it
     */
    private void drawAbility(Ability ability)
    {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        RenderHelper.disableStandardItemLighting();

        int startX = 0, startY = 0;
        int startDrawX = 0;
        int startDrawY = 0;
        int difX = 0, difY = 0;

        if (hoveredAbility == null || hoveredAbility != ability)
        {
            if (x + ability.x < 0)
            {
                difX = -(x + ability.x);
                startX = difX;
                startDrawX = difX;
            }

            if (x + ability.x + ability.width > SCREEN_WIDTH)
            {
                difX = -(SCREEN_WIDTH - (x + ability.x + ability.width));
                startX = 0;
                startDrawX = 0;
            }

            if (y + ability.y < 0)
            {
                difY = -(y + ability.y);
                startY = difY;
                startDrawY = difY;
            }

            if (y + ability.y + ability.height > SCREEN_HEIGHT)
            {
                difY = -(SCREEN_HEIGHT - (y + ability.y + ability.height));
                startY = 0;
                startDrawY = 0;
            }
        }

        if (difX <= ability.width && difY <= ability.height)
        {
            Color abilityColour = ability.state.colour;
            if (ability.hasConflictingAbility(abilities)) abilityColour = new Color(0x4E1815);
            if (selectedAbility != null && ability == selectedAbility) abilityColour = new Color(0x45B92F);
            float transparency = 1.0f;
            if (selectedAbility != null && ability == selectedAbility) transparency = 1.0f;
            else if (ability.state == Ability.State.LOCKED) transparency = 0.5f;
            else if (ability.hasConflictingAbility(abilities)) transparency = 0.50f;

            if (hoveredAbility != null)
            {
                int i = hoveredAbility == ability ? 20 : 0;

                zLevel+=i;
                mc.getTextureManager().bindTexture(ABILITIES);
                GlStateManager.color(abilityColour.getRed() / 255f, abilityColour.getGreen() / 255f, abilityColour.getBlue() / 255f);
                drawTexturedModalRect(screenLeft + x + ability.x + startX, screenTop + y + ability.y + startY, ability.shapeX + startDrawX, ability.shapeY + startDrawY, ability.width - difX, ability.height - difY);
                GlStateManager.color(1.0f, 1.0f, 1.0f, transparency);
                mc.getTextureManager().bindTexture(ABILITIES2);
                drawTexturedModalRect(screenLeft + x + ability.x + startX, screenTop + y + ability.y + startY, ability.iconX + startDrawX, ability.iconY + startDrawY, ability.width - difX, ability.height - difY);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                zLevel-=i;
            }
            else
            {
                mc.getTextureManager().bindTexture(ABILITIES);
                GlStateManager.color(abilityColour.getRed() / 255f, abilityColour.getGreen() / 255f, abilityColour.getBlue() / 255f);
                drawTexturedModalRect(screenLeft + x + ability.x + startX, screenTop + y + ability.y + startY, ability.shapeX + startDrawX, ability.shapeY + startDrawY, ability.width - difX, ability.height - difY);
                GlStateManager.color(1.0f, 1.0f, 1.0f, transparency);
                mc.getTextureManager().bindTexture(ABILITIES2);
                drawTexturedModalRect(screenLeft + x + ability.x + startX, screenTop + y + ability.y + startY, ability.iconX + startDrawX, ability.iconY + startDrawY, ability.width - difX, ability.height - difY);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }

    private int totalDifX = 0, totalDifY = 0;
    private int timeMoving = 0;
    private void updateXY(int mouseX, int mouseY)
    {
        if (isClicking && (getAbilityAtMouseLocation(mouseX, mouseY) == null || !haveNotMovedSinceMouseClick || totalDifX > 12 || totalDifY > 12 || timeMoving > 10))
        {
            x += mouseX - prevMouseX;
            y += mouseY - prevMouseY;

            totalDifX = 0;
            totalDifY = 0;
            timeMoving = 0;
            haveNotMovedSinceMouseClick = false;
        }
        else if (isClicking)
        {
            totalDifX += Math.abs(mouseX - prevMouseX);
            totalDifY += Math.abs(mouseY - prevMouseY);
            timeMoving += 1;
        }

        if (x < minScreenX)
            x = minScreenX;
        else if (x > maxScreenX)
            x = maxScreenX;
        if (y < minScreenY)
            y = minScreenY;
        else if (y > maxScreenY)
            y = maxScreenY;

    }

    /**
     * Gets the ability currently underneath the given mouse position
     */
    private Ability getAbilityAtMouseLocation(int mouseX, int mouseY)
    {
        if (isMouseOverScreen(mouseX, mouseY))
        {
            for (Ability ability : abilities)
            {
                if (mouseX >= actualAbilityX(ability) && mouseX < actualAbilityX(ability) + ability.width)
                {
                    if (mouseY >= actualAbilityY(ability) && mouseY < actualAbilityY(ability) + ability.height)
                    {
                        return ability;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Gets the actual x position of the ability on screen
     */
    private int actualAbilityX(Ability ability)
    {
        return screenLeft + x + ability.x;
    }

    /**
     * Gets the actual y position of the ability on screen
     */
    private int actualAbilityY(Ability ability)
    {
        return screenTop + y + ability.y;
    }

    @Override
    public void onGuiClosed()
    {
        blockling.syncAbilities();
        super.onGuiClosed();
    }
}
