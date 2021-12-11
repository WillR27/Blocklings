package com.willr27.blocklings.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.skills.SkillGroup;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.screens.guis.SkillsGui;
import com.willr27.blocklings.gui.screens.guis.TabbedGui;
import com.willr27.blocklings.gui.widgets.TexturedWidget;
import com.willr27.blocklings.skills.info.SkillGroupInfo;
import net.minecraft.entity.player.PlayerEntity;

public class SkillsScreen extends TabbedScreen
{
    private static final int WINDOW_WIDTH = 158;
    private static final int WINDOW_HEIGHT = 138;

    private static final int MAXIMISE_WIDTH = 300;
    private static final int MAXIMISE_HEIGHT = 190;
    private static final int MAXIMISE_X = 180;
    private static final int MAXIMISE_Y = 142;
    private static final int MAXIMISE_TEXTURE_Y = 206;
    private static final int MAXIMISE_SIZE = 11;

    private SkillsGui skillsGui;
    private TexturedWidget maximiseWidget;

    private boolean maximised;

    private int firstOpenDelay = 20;

    private SkillGroup group;

    public SkillsScreen(BlocklingEntity blockling, PlayerEntity player, SkillGroupInfo skillsGroup)
    {
        super(blockling, player, "Skills");
        this.group = blockling.getSkills().getGroup(skillsGroup);
    }

    @Override
    protected void init()
    {
        super.init();

        skillsGui = new SkillsGui(blockling, group, font, WINDOW_WIDTH, WINDOW_HEIGHT, centerX, centerY + 5, width, height);
        if (maximised) skillsGui.resize(MAXIMISE_WIDTH, MAXIMISE_HEIGHT, 1.0f);
        maximiseWidget = new TexturedWidget(font, left + MAXIMISE_X, top + MAXIMISE_Y, MAXIMISE_SIZE, MAXIMISE_SIZE, 0, MAXIMISE_TEXTURE_Y);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (firstOpenDelay > 0) firstOpenDelay--;

        skillsGui.draw(matrixStack, mouseX, mouseY);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GuiUtil.bindTexture(GuiUtil.SKILLS);

        String points = "" + blockling.getStats().skillPoints.getInt();
        if (!maximised)
        {
            blit(matrixStack, contentLeft, contentTop, 0, 0, TabbedGui.CONTENT_WIDTH, TabbedGui.CONTENT_HEIGHT);
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            font.drawShadow(matrixStack, points, left + 184 - font.width(points), top + 7, 0xffffff);
            font.drawShadow(matrixStack, group.info.guiTitle.getString(), left + 36, top + 7, 0xffffff);
        }
        else
        { // TODO: MAKE DYNAMIC
            int left = centerX - MAXIMISE_WIDTH / 2;
            int top = centerY - MAXIMISE_HEIGHT / 2;
            int right = left + MAXIMISE_WIDTH;
            int bottom = top + MAXIMISE_HEIGHT;
            blit(matrixStack, left - 9, top - 13, 0, 0, 120, 108);
            blit(matrixStack, right - 120 + 9, top - 13, 176 - 120, 0, 120, 108);
            blit(matrixStack, left - 9, bottom - 108 + 13, 0, 166 - 108, 120, 108);
            blit(matrixStack, right - 120 + 9, bottom - 108 + 13, 176 - 120, 166 - 108, 120, 108);
            blit(matrixStack, left + 111, top - 13, 30, 0, 78, 30);
            blit(matrixStack, left + 111, bottom - 30 + 13, 30, 166 - 30, 78, 30);
            font.drawShadow(matrixStack, points, right - 11 - font.width(points), top - 6, 0xffffff);
            font.drawShadow(matrixStack, group.info.guiTitle.getString(), left, top - 6, 0xffffff);
        }

        GuiUtil.bindTexture(GuiUtil.SKILLS);

        matrixStack.pushPose();
        matrixStack.translate(0.0f, 0.0f, 30.0f);
        maximiseWidget.textureX = maximiseWidget.isMouseOver(mouseX, mouseY) && !skillsGui.isDragging() ? 0 : MAXIMISE_SIZE;
        if (!maximised) maximiseWidget.render(matrixStack, mouseX, mouseY);
        matrixStack.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        if (firstOpenDelay > 0)
        {
            return true;
        }

        skillsGui.mouseClicked((int) mouseX, (int) mouseY, state);
        return super.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        if (firstOpenDelay > 0)
        {
            return true;
        }

        if (skillsGui.mouseReleased((int) mouseX, (int) mouseY, state))
        {
            return true;
        }

        if (!maximised && !skillsGui.isDragging() && maximiseWidget.isMouseOver((int) mouseX, (int) mouseY))
        {
            skillsGui.resize(MAXIMISE_WIDTH, MAXIMISE_HEIGHT, 1.0f);
            maximised = true;
            return true;
        }

        if (!maximised)
        {
            return super.mouseReleased(mouseX, mouseY, state);
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int i, int j)
    {
        if (skillsGui.keyPressed(keyCode, i, j))
        {
            return true;
        }

        if (keyCode == 256 && maximised)
        {
            maximised = false;
            skillsGui.resize(WINDOW_WIDTH, WINDOW_HEIGHT, 1.0f);
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
}
