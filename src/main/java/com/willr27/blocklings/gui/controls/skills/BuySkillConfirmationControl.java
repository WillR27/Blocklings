package com.willr27.blocklings.gui.controls.skills;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.IControl;
import com.willr27.blocklings.skill.Skill;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The control to display the confirmation dialog when buying a skill.
 */
@OnlyIn(Dist.CLIENT)
public class BuySkillConfirmationControl extends Control
{
    /**
     * The skill being bought.
     */
    @Nonnull
    private final SkillControl skillControl;

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
     * @param parent the parent control.
     * @param skillControl the skill being bought.
     * @param message the message to display.
     * @param width the width of the skills control.
     * @param height the height of the skills control.
     * @param areaWidth the width of the area to display the message.
     * @param areaHeight the height of the area to display the message.
     */
    public BuySkillConfirmationControl(@Nonnull IControl parent, @Nonnull SkillControl skillControl, @Nonnull List<String> message, int width, int height, int areaWidth, int areaHeight)
    {
        super(parent, 0, 0, width, height);
        this.skillControl = skillControl;
        this.message = message;
        this.areaWidth = areaWidth;
        this.areaHeight = areaHeight;

        int buttonWidth = 50;
        int buttonHeight = 20;
        int yesX = width / 2 - 30 - buttonWidth / 2;
        int noX = width / 2 + 30 - buttonWidth / 2;
        int buttonY = height / 2 + 10;
        yesButton = new Button(yesX + getX(), buttonY + getY(), buttonWidth, buttonHeight, new StringTextComponent("Yes"), press -> { setIsVisible(true); this.skillControl.skill.tryBuy(); });
        noButton = new Button(noX + getY(), buttonY + getY(), buttonWidth, buttonHeight, new StringTextComponent("No"), press -> { setIsVisible(false); skillControl.isSelected = false; });
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        fill(matrixStack, screenX + width / 2 - areaWidth / 2, screenY + height / 2 - areaHeight / 2, screenX + width / 2 + areaWidth / 2, screenY + height / 2 + areaHeight / 2, 0xbb000000);

        int i = 0;
        for (String str : message)
        {
            drawCenteredString(matrixStack, font, str, (int) (getScreenX() + width / 2), (int) (getScreenY() + height / 2 + i * 11 - (message.size() * 11) - 5), 0xffffff);
            i++;
        }

        matrixStack.translate(getScreenX() - (getScreenX() / getEffectiveScale()), getScreenY() - (getScreenY() / getEffectiveScale()), 0.0f);
        matrixStack.translate(parent.getScreenX() / parent.getEffectiveScale(), parent.getScreenY() / parent.getEffectiveScale(), 0.0);
        yesButton.render(matrixStack, (int) (toLocalX(mouseX) / getEffectiveScale()), (int) (toLocalY(mouseY) / getEffectiveScale()), 0);
        noButton.render(matrixStack, (int) (toLocalX(mouseX) / getEffectiveScale()), (int) (toLocalY(mouseY) / getEffectiveScale()), 0);
        matrixStack.translate((getScreenX() / getEffectiveScale()) - getScreenX(), (getScreenY() / getEffectiveScale()) - getScreenY(), 0.0f);
    }

    @Override
    public void controlKeyPressed(@Nonnull KeyEvent e)
    {
        if (GuiUtil.isCloseInventoryKey(e.keyCode))
        {
            noButton.onPress();

            remove();

            e.setIsHandled(true);
        }
    }

    @Override
    public void controlMouseReleased(@Nonnull MouseButtonEvent e)
    {
        if (yesButton.isMouseOver((int) (toLocalX(e.mouseX) / getEffectiveScale()), (int) (toLocalY(e.mouseY) / getEffectiveScale())))
        {
            yesButton.mouseReleased((int) (toLocalX(e.mouseX) / getEffectiveScale()), (int) (toLocalY(e.mouseY) / getEffectiveScale()), e.button);
            yesButton.onPress();

            setIsVisible(false);
            remove();
        }
        else if (noButton.isMouseOver((int) (toLocalX(e.mouseX) / getEffectiveScale()), (int) (toLocalY(e.mouseY) / getEffectiveScale())))
        {
            noButton.mouseReleased((int) (toLocalX(e.mouseX) / getEffectiveScale()), (int) (toLocalY(e.mouseY) / getEffectiveScale()), e.button);
            noButton.onPress();

            skillControl.isSelected = false;

            setIsVisible(false);
            remove();
        }

        e.setIsHandled(true);
    }
}
