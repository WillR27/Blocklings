package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.widgets.Widget;
import com.willr27.blocklings.skills.SkillGroup;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.screens.guis.SkillsGui;
import com.willr27.blocklings.gui.screens.guis.TabbedGui;
import com.willr27.blocklings.skills.info.SkillGroupInfo;
import net.minecraft.entity.player.PlayerEntity;

public class SkillsScreen extends TabbedScreen
{
    /**
     * The width of the inside section of the gui where the skill tree is rendered.
     */
    private static final int WINDOW_WIDTH = 158;

    /**
     * The height of the inside section of the gui where the skill tree is rendered.
     */
    private static final int WINDOW_HEIGHT = 138;

    /**
     * The gui displayed inside the window area that handles the skill tree rendering and interaction.
     */
    private SkillsGui skillsGui;

    /**
     * The widget used for the maximise button.
     */
    private MaximiseWidget maximiseWidget;

    private int firstOpenDelay = 20;

    private SkillGroup group;

    public SkillsScreen(BlocklingEntity blockling, PlayerEntity player, SkillGroupInfo skillsGroup)
    {
        super(blockling);
        this.group = blockling.getSkills().getGroup(skillsGroup);
    }

    @Override
    protected void init()
    {
        super.init();

        skillsGui = new SkillsGui(blockling, group, font, WINDOW_WIDTH, WINDOW_HEIGHT, centerX, centerY + 5, width, height);
        maximiseWidget = new MaximiseWidget(left + 180, top + 142);

        if (maximiseWidget.isMaximised)
        {
            skillsGui.resize(getMaximisedWidth(), getMaximisedHeight(), 1.0f);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (firstOpenDelay > 0) firstOpenDelay--;

        skillsGui.draw(matrixStack, mouseX, mouseY);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GuiUtil.bindTexture(GuiTextures.SKILLS);

        if (!maximiseWidget.isMaximised)
        {
            blit(matrixStack, contentLeft, contentTop, 0, 0, TabbedGui.CONTENT_WIDTH, TabbedGui.CONTENT_HEIGHT);
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            font.drawShadow(matrixStack, group.info.guiTitle.getString(), left + 36, top + 7, 0xffffff);
            RenderSystem.enableDepthTest();
        }
        else
        { // TODO: MAKE DYNAMIC
            int left = centerX - getMaximisedWidth() / 2;
            int top = centerY - getMaximisedHeight() / 2;
            int right = left + getMaximisedWidth();
            int bottom = top + getMaximisedHeight();
            blit(matrixStack, left - 9, top - 13, 0, 0, 120, 108);
            blit(matrixStack, right - 120 + 9, top - 13, 176 - 120, 0, 120, 108);
            blit(matrixStack, left - 9, bottom - 108 + 13, 0, 166 - 108, 120, 108);
            blit(matrixStack, right - 120 + 9, bottom - 108 + 13, 176 - 120, 166 - 108, 120, 108);
            blit(matrixStack, left + 111, top - 13, 30, 0, 78, 30);
            blit(matrixStack, left + 111, bottom - 30 + 13, 30, 166 - 30, 78, 30);
            font.drawShadow(matrixStack, group.info.guiTitle.getString(), left, top - 6, 0xffffff);
            RenderSystem.enableDepthTest();
        }

        maximiseWidget.render(matrixStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        if (firstOpenDelay > 0)
        {
            return true;
        }

        boolean result = false;

        if (skillsGui.mouseClicked((int) mouseX, (int) mouseY, state))
        {
            result = true;
        }

        if (maximiseWidget.mouseClicked((int) mouseX, (int) mouseY, state))
        {
            result = true;
        }

        if (result)
        {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        if (firstOpenDelay > 0)
        {
            return true;
        }

        boolean result = false;

        if (maximiseWidget.mouseReleased((int) mouseX, (int) mouseY, state))
        {
            skillsGui.resize(getMaximisedWidth(), getMaximisedHeight(), 1.0f);

            result = true;
        }

        if (skillsGui.mouseReleased((int) mouseX, (int) mouseY, state))
        {
            result = true;
        }

        if (!result && super.mouseReleased(mouseX, mouseY, state))
        {
            result = true;
        }

        return result;
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
            if (maximiseWidget.isMaximised)
            {
                maximiseWidget.isMaximised = false;
                skillsGui.resize(WINDOW_WIDTH, WINDOW_HEIGHT, 1.0f);
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
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_)
    {
        if (skillsGui.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_))
        {
            return true;
        }

        return super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    private int getMaximisedWidth()
    {
        return width + 20;
    }

    private int getMaximisedHeight()
    {
        return height + 20;
    }

    private static final class MaximiseWidget extends Widget
    {
        private static final GuiTexture DEFAULT_TEXTURE = new GuiTexture(GuiTextures.SKILLS, 0, 206, 11, 11);
        private static final GuiTexture HOVERED_TEXTURE = new GuiTexture(GuiTextures.SKILLS, 11, 206, DEFAULT_TEXTURE.width, DEFAULT_TEXTURE.height);

        /**
         * Whether the skills gui is maximised.
         */
        public boolean isMaximised = false;

        /**
         * @param x the x position.
         * @param y the y position.
         */
        public MaximiseWidget(int x, int y)
        {
            super(x, y, 11, 11);
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY)
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
        public boolean mouseReleased(int mouseX, int mouseY, int state)
        {
            boolean result = false;

            if (!isMaximised && isPressed && isMouseOver(mouseX, mouseY))
            {
                isMaximised = true;

                result = true;
            }

            super.mouseReleased(mouseX, mouseY, state);

            return result;
        }
    }
}
