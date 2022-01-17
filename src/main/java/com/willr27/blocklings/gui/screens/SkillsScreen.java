package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.*;
import com.willr27.blocklings.gui.guis.SkillsControl;
import com.willr27.blocklings.gui.guis.TabbedGui;
import com.willr27.blocklings.gui.widgets.TexturedControl;
import com.willr27.blocklings.skills.SkillGroup;
import com.willr27.blocklings.skills.info.SkillGroupInfo;
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
     * The gui displayed inside the window area that handles the skill tree rendering and interaction.
     */
    private SkillsControl skillsGui;

    /**
     * The control used for the maximise button.
     */
    private MaximiseControl maximiseControl;

    /**
     * The control used to render the gui border.
     */
    private TexturedControl borderControl;

    /**
     * The skill group to display.
     */
    @Nonnull
    private final SkillGroup group;

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
        super.init();

        skillsGui = new SkillsControl(this, blockling, group, 9, 19, 158, 138);
        maximiseControl = new MaximiseControl(this, 151, 141);

        skillsGui.addChild(borderControl = new TexturedControl(contentLeft, contentTop, new GuiTexture(GuiTextures.SKILLS, 0, 0, TabbedGui.CONTENT_WIDTH, TabbedGui.CONTENT_HEIGHT)));

        if (maximiseControl.isMaximised)
        {
            skillsGui.maximise();
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        skillsGui.render(matrixStack, mouseX, mouseY);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GuiUtil.bindTexture(GuiTextures.SKILLS);

        if (!maximiseControl.isMaximised)
        {
            borderControl.render(matrixStack, mouseX, mouseY);

            super.render(matrixStack, mouseX, mouseY, partialTicks);

            font.drawShadow(matrixStack, group.info.guiTitle.getString(), left + 36, top + 7, 0xffffff);
            RenderSystem.enableDepthTest();
        }

        maximiseControl.render(matrixStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        mouseClickedNoHandle((int) mouseX, (int) mouseY, button);

        if (skillsGui.mouseClicked((int) mouseX, (int) mouseY, button))
        {
            return true;
        }
        else if (maximiseControl.mouseClicked((int) mouseX, (int) mouseY, button))
        {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (skillsGui.mouseReleased((int) mouseX, (int) mouseY, button))
        {
            return true;
        }
        else if (maximiseControl.mouseReleased((int) mouseX, (int) mouseY, button))
        {
            skillsGui.maximise();

            return true;
        }

        mouseReleasedNoHandle((int) mouseX, (int) mouseY, button);

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int i, int j)
    {
        if (skillsGui.keyPressed(keyCode, i, j))
        {
            return true;
        }

        if (GuiUtil.isCloseInventoryKey(keyCode))
        {
            if (maximiseControl.isMaximised)
            {
                maximiseControl.isMaximised = false;
                skillsGui.minimise();
            }
            else
            {
                onClose();
            }

            return true;
        }

        return super.keyPressed(keyCode, i, j);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll)
    {
        if (skillsGui.mouseScrolled((int) mouseX, (int) mouseY, scroll))
        {
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    /**
     * The control used to toggle the maximised version of the skills gui.
     */
    private static final class MaximiseControl extends Control
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
        private boolean isMaximised = false;

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
        public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
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

        @Override
        public boolean mouseReleased(int mouseX, int mouseY, int button)
        {
            if (!isMaximised && isPressed() && isMouseOver(mouseX, mouseY))
            {
                isMaximised = true;

                return true;
            }

            return false;
        }
    }
}
