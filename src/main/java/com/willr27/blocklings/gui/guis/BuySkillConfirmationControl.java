package com.willr27.blocklings.gui.guis;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.IControl;
import com.willr27.blocklings.skills.Skill;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The control to display the confirmation dialog when buying a skill.
 */
public class BuySkillConfirmationControl extends Control
{
    /**
     * The skill being bought.
     */
    @Nonnull
    private final Skill skill;

    /**
     * The message associated with the skill.
     */
    @Nonnull
    private final List<String> message;

    /**
     * The width of the area to display the message.
     */
    private final int areaWidth;

    /**
     * The height of the area to display the message.
     */
    private final int areaHeight;

    /**
     * Whether the dialog is closed.
     */
    public boolean closed;

    /**
     * The yes button.
     */
    @Nonnull
    private final Button yesButton;

    /**
     * The no button.
     */
    @Nonnull
    private final Button noButton;

    /**
     * @param control the control to copy.
     */
    public BuySkillConfirmationControl(@Nonnull BuySkillConfirmationControl control)
    {
        this(control.parent, control.skill, control.message, control.width, control.height, control.areaWidth, control.areaHeight);
    }

    /**
     * @param parent the parent control.
     * @param skill the skill being bought.
     * @param message the message to display.
     * @param width the width of the skills control.
     * @param height the height of the skills control.
     * @param areaWidth the width of the area to display the message.
     * @param areaHeight the height of the area to display the message.
     */
    public BuySkillConfirmationControl(@Nonnull IControl parent, Skill skill, List<String> message, int width, int height, int areaWidth, int areaHeight)
    {
        super(parent, 0, 0, width, height);
        this.skill = skill;
        this.message = message;
        this.areaWidth = areaWidth;
        this.areaHeight = areaHeight;
        this.closed = false;

        int buttonWidth = 50;
        int buttonHeight = 20;
        int yesX = width / 2 - 30 - buttonWidth / 2;
        int noX = width / 2 + 30 - buttonWidth / 2;
        int buttonY = height / 2 + 10;
        yesButton = new Button(yesX + screenX, buttonY + screenY, buttonWidth, buttonHeight, new StringTextComponent("Yes"), press -> { closed = true; skill.tryBuy(); });
        noButton = new Button(noX + screenX, buttonY + screenY, buttonWidth, buttonHeight, new StringTextComponent("No"), press -> { closed = true; });
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (!closed)
        {
            fill(matrixStack, screenX + width / 2 - areaWidth / 2, screenY + height / 2 - areaHeight / 2, screenX + width / 2 + areaWidth / 2, screenY + height / 2 + areaHeight / 2, 0xbb000000);

            int i = 0;
            for (String str : message)
            {
                drawCenteredString(matrixStack, font, str, screenX + width / 2, screenY + height / 2 + i * 11 - (message.size() * 11) - 5, 0xffffff);
                i++;
            }

            yesButton.render(matrixStack, mouseX, mouseY, 0);
            noButton.render(matrixStack, mouseX, mouseY, 0);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int mods)
    {
        if (!closed && GuiUtil.isCloseInventoryKey(keyCode))
        {
            if (noButton != null)
            {
                noButton.onPress();

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        if (yesButton.isMouseOver(mouseX, mouseY))
        {
            yesButton.mouseReleased(mouseX, mouseY, button);
            yesButton.onPress();

            return true;
        }
        else if (noButton.isMouseOver(mouseX, mouseY))
        {
            noButton.mouseReleased(mouseX, mouseY, button);
            noButton.onPress();

            return true;
        }

        return false;
    }
}
