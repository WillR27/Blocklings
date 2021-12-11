package com.willr27.blocklings.gui.screens.guis;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.skills.Skill;
import com.willr27.blocklings.skills.SkillGroup;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class SkillsConfirmationGui extends AbstractGui
{
    private FontRenderer font;
    private Skill skill;
    private List<String> message;
    private int windowWidth;
    private int windowHeight;
    private int areaWidth;
    private int areaHeight;
    public boolean closed;
    public boolean yes;
    private Button yesButton;
    private Button noButton; // TODO: TIDY CLASS

    public SkillsConfirmationGui()
    {
        closed = true;
    }

    public SkillsConfirmationGui(float scale, SkillsConfirmationGui gui)
    {
        this(scale, gui.font, gui.skill, gui.message, gui.windowWidth, gui.windowHeight, gui.areaWidth, gui.areaHeight);
    }

    public SkillsConfirmationGui(float scale, FontRenderer font, Skill skill, List<String> message, int windowWidth, int windowHeight, int areaWidth, int areaHeight)
    {
        this.font = font;
        this.skill = skill;
        this.message = message;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.areaWidth = areaWidth;
        this.areaHeight = areaHeight;
        this.closed = false;

        int width = 50;
        int height = 20;
        int yesX = windowWidth / 2 - 30 - width / 2;
        int noX = windowWidth / 2 + 30 - width / 2;
        int y = windowHeight / 2 + 10;
        yesButton = new Button(yesX, y, width, height, new StringTextComponent("Yes"), press -> { yes = true; closed = true; skill.tryBuy(); });
        noButton = new Button(noX, y, width, height, new StringTextComponent("No"), press -> { yes = false; closed = true; });
    }

    public void draw(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (!closed)
        {
            fill(matrixStack, windowWidth / 2 - areaWidth / 2, windowHeight / 2 - areaHeight / 2 - 5, windowWidth / 2 + areaWidth / 2, windowHeight / 2 + areaHeight / 2 - 5, 0xbb000000);

            int i = 0;
            for (String str : message)
            {
                drawCenteredString(matrixStack, font, str, windowWidth / 2, windowHeight / 2 + i * 11 - (message.size() * 11) - 5, 0xffffff);
                i++;
            }

            yesButton.render(matrixStack, mouseX, mouseY, 0);
            noButton.render(matrixStack, mouseX, mouseY, 0);
        }
    }

    public boolean keyPressed(int keyCode, int i, int j)
    {
        if (!closed && keyCode == 256)
        {
            if (noButton != null)
            {
                noButton.onPress();
                return true;
            }
        }

        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
//        if (yesButton.isMouseOver(mouseX, mouseY))
//        {
//            yesButton.mouseClicked(mouseX, mouseY, state);
//        }
//        else if (noButton.isMouseOver(mouseX, mouseY))
//        {
//            noButton.mouseClicked(mouseX, mouseY, state);
//        }

        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        if (yesButton.isMouseOver(mouseX, mouseY))
        {
            yesButton.mouseReleased(mouseX, mouseY, state);
            yesButton.onPress();
            return true;
        }
        else if (noButton.isMouseOver(mouseX, mouseY))
        {
            noButton.mouseReleased(mouseX, mouseY, state);
            noButton.onPress();
            return true;
        }

        return false;
    }
}
