package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.*;
import com.willr27.blocklings.gui.controls.TabbedControl;
import com.willr27.blocklings.gui.controls.TexturedControl;
import com.willr27.blocklings.gui.controls.skills.BuySkillConfirmationControl;
import com.willr27.blocklings.gui.controls.skills.SkillsControl;
import com.willr27.blocklings.skill.SkillGroup;
import com.willr27.blocklings.skill.info.SkillGroupInfo;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * The screen that displays the skill trees for each category.
 */
@OnlyIn(Dist.CLIENT)
public class SkillsScreen extends TabbedScreen
{
    /**
     * The control containing the skills control, to ensure it doesn't move when scaling.
     */
    public Control skillsContainer;

    /**
     * The gui displayed inside the window area that handles the skill tree rendering and interaction.
     */
    public SkillsControl skillsControl;

    /**
     * The control used for the maximise button.
     */
    public MaximiseControl maximiseControl;

    /**
     * The control used to render the gui border.
     */
    public TexturedControl borderControl;

    /**
     * The skill group to display.
     */
    @Nonnull
    private final SkillGroup group;

    /**
     * The gui used to confirm buying a skill.
     */
    @Nonnull
    public BuySkillConfirmationControl skillBuyConfirmationControl;

    /**
     * @param blockling the blockling.
     * @param group the skill group to display.
     */
    public SkillsScreen(@Nonnull BlocklingEntity blockling, @Nonnull SkillGroupInfo group)
    {
        super(blockling);
        this.group = blockling.getSkills().getGroup(group);
    }

    @Override
    protected void init()
    {
        boolean isVisible = true;

        if (tabbedControl != null)
        {
            isVisible = tabbedControl.isVisible();
        }

        super.init();

        tabbedControl.setIsVisible(isVisible);

        removeChild(skillsContainer);
        skillsContainer = new Control(this, contentLeft + 9, contentTop + 19, 158, 138);

        removeChild(skillsControl);
        skillsControl = new SkillsControl(skillsContainer, blockling, group, 0, 0, 158, 138);
        skillsControl.setIsFocused(true);

        boolean isMaximised = false;

        if (maximiseControl != null && maximiseControl.isMaximised)
        {
            isMaximised = true;

            skillsControl.maximise();
        }

        removeChild(maximiseControl);
        maximiseControl = new MaximiseControl(skillsContainer, 142, 122)
        {
            @Override
            public void controlMouseReleased(@Nonnull MouseButtonEvent e)
            {
                if (!isMaximised && isPressed())
                {
                    isMaximised = true;

                    skillsControl.maximise();
                }

                e.setIsHandled(true);
            }
        };
        maximiseControl.isMaximised = isMaximised;

        if (borderControl != null)
        {
            isVisible = borderControl.isVisible();
        }

        removeChild(borderControl);
        borderControl = new TexturedControl(this, contentLeft, contentTop, new GuiTexture(GuiTextures.SKILLS, 0, 0, TabbedControl.CONTENT_WIDTH, TabbedControl.CONTENT_HEIGHT))
        {
            @Override
            public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
            {
                RenderSystem.enableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

                super.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        };
        borderControl.setIsInteractive(false);
        borderControl.setIsVisible(isVisible);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (!maximiseControl.isMaximised)
        {
            font.drawShadow(matrixStack, group.info.guiTitle.getString(), left + 36, top + 7, 0xffffff);
            RenderSystem.enableDepthTest();
        }
    }

    @Override
    public void globalKeyPressed(@Nonnull KeyEvent e)
    {
        if (!skillBuyConfirmationControl.isVisible() && GuiUtil.isCloseInventoryKey(e.keyCode))
        {
            if (maximiseControl.isMaximised)
            {
                maximiseControl.isMaximised = false;
                skillsControl.minimise();
            }
            else
            {
                onClose();
            }
        }
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    /**
     * The control used to toggle the maximised version of the skills gui.
     */
    public static class MaximiseControl extends Control
    {
        /**
         * The texture used when the mouse is not over the control.
         */
        private static final GuiTexture DEFAULT_TEXTURE = new GuiTexture(GuiTextures.SKILLS, 0, 206, 11, 11);

        /**
         * The texture used when the mouse is over the control.
         */
        private static final GuiTexture HOVERED_TEXTURE = new GuiTexture(GuiTextures.SKILLS, DEFAULT_TEXTURE.width, 206, DEFAULT_TEXTURE.width, DEFAULT_TEXTURE.height);

        /**
         * Whether the skills gui is maximised.
         */
        public boolean isMaximised = false;

        /**
         * @param parent the parent control.
         * @param x the local x position.
         * @param y the local y position.
         */
        public MaximiseControl(@Nonnull IControl parent, int x, int y)
        {
            super(parent, x, y, DEFAULT_TEXTURE.width, DEFAULT_TEXTURE.height);
        }

        @Override
        public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
        {
            if (isMaximised)
            {
                return;
            }

            if (isMouseOver(mouseX, mouseY))
            {
                renderTexture(matrixStack, HOVERED_TEXTURE);
            }
            else
            {
                renderTexture(matrixStack, DEFAULT_TEXTURE);
            }
        }
    }
}
