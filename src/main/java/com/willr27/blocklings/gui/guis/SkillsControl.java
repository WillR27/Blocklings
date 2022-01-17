package com.willr27.blocklings.gui.guis;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.IControl;
import com.willr27.blocklings.gui.widgets.SkillControl;
import com.willr27.blocklings.gui.widgets.TexturedControl;
import com.willr27.blocklings.skills.Skill;
import com.willr27.blocklings.skills.SkillGroup;
import com.willr27.blocklings.skills.info.SkillGuiInfo;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The gui used to display the skill tree in a skill group.
 */
public class SkillsControl extends Control
{
    /**
     * The width and height of the icon for a skill.
     */
    public static final int SKILL_SIZE = SkillGuiInfo.SkillGuiTexture.ICON_SIZE;

    /**
     * The width and height of a tile in the background.
     */
    private static final int TILE_SIZE = 16;

    /**
     * The current x offset of the background.
     */
    private final int backgroundOffsetX;

    /**
     * The current y offset of the background.
     */
    private final int backgroundOffsetY;

    /**
     * The skill group to display.
     */
    @Nonnull
    private final SkillGroup group;

    /**
     * The x position when minimised.
     */
    private final int minimisedX;

    /**
     * The y position when minimised.
     */
    private final int minimisedY;

    /**
     * The width when minimised.
     */
    private final int minimisedWidth;

    /**
     * The height when minimised.
     */
    private final int minimisedHeight;

    /**
     * How many tiles fit in the x-axis.
     */
    private int tilesX;

    /**
     * How many tiles fit in the y-axis.
     */
    private int tilesY;

    /**
     * The x position the user started dragging at.
     */
    private int dragStartX;

    /**
     * The y position the user started dragging at.
     */
    private int dragStartY;

    /**
     * The x offset from panning around the skills.
     */
    private int moveOffsetX;

    /**
     * The y offset from panning around the skills.
     */
    private int moveOffsetY;

    /**
     * The mouse x position from the previous frame.
     */
    private int prevMouseX;

    /**
     * The mouse y position from the previous frame.
     */
    private int prevMouseY;

    /**
     * Whether the mouse is down for the current frame.
     */
    private boolean mouseDown;

    /**
     * Whether the user is dragging the mouse.
     */
    private boolean dragging;

    /**
     * The current scale to display the skills at.
     */
    public float scale = 1.0f;

    /**
     * The list of controls representing each skill.
     */
    @Nonnull
    private final List<SkillControl> skillControls = new ArrayList<>();

    /**
     * The gui used to confirm buying a skill.
     */
    private SkillsConfirmationGui confirmSkillBuyGui;

    /**
     * @param parent the parent control.
     * @param blockling the blockling.
     * @param group the skill group to display.
     * @param minimisedX the x position when minimised.
     * @param minimisedY the y position when minimised.
     * @param minimisedWidth the width when minimised.
     * @param minimisedHeight the height when minimised.
     */
    public SkillsControl(@Nonnull IControl parent, @Nonnull BlocklingEntity blockling, @Nonnull SkillGroup group, int minimisedX, int minimisedY, int minimisedWidth, int minimisedHeight)
    {
        super(parent, minimisedX, minimisedY, minimisedWidth, minimisedHeight);
        this.group = group;
        this.minimisedX = minimisedX;
        this.minimisedY = minimisedY;
        this.minimisedWidth = minimisedWidth;
        this.minimisedHeight = minimisedHeight;

        minimise();

        this.backgroundOffsetX = blockling.getRandom().nextInt(1000);
        this.backgroundOffsetY = blockling.getRandom().nextInt(1000);

        confirmSkillBuyGui = new SkillsConfirmationGui();

        for (Skill skill : group.getSkills())
        {
            skillControls.add(new SkillControl(this, skill));
        }

        for (SkillControl skillControl : skillControls)
        {
            for (SkillControl skillControl2 : skillControls)
            {
                if (skillControl.skill.parents().contains(skillControl2.skill))
                {
                    skillControl.parents.add(skillControl2);
                }
            }
        }
    }

    /**
     * Maximises the skills gui.
     */
    public void maximise()
    {
        setX(-parent.getScreenX() - 10);
        setY(-parent.getScreenY() - 10);
        resize(screen.width + 20, screen.height + 20, scale);

        moveOffsetX = (int) (((width - SKILL_SIZE) / 2) / scale);
        moveOffsetY = (int) (((height - SKILL_SIZE) / 2) / scale);
    }

    /**
     * Minimises the skills gui.
     */
    public void minimise()
    {
        setX(minimisedX);
        setY(minimisedY);
        resize(minimisedWidth, minimisedHeight, scale);

        moveOffsetX = (int) (((width - SKILL_SIZE) / 2) / scale);
        moveOffsetY = (int) (((height - SKILL_SIZE) / 2) / scale);
    }

    /**
     * Resizes the skills gui.
     *
     * @param width the new width.
     * @param height the new height.
     * @param scale the new scale.
     */
    private void resize(int width, int height, float scale)
    {
        this.width = width;
        this.height = height;
        this.scale = scale;

        int tileSize = (int) (TILE_SIZE * scale);

        this.tilesX = width / tileSize;
        this.tilesY = height / tileSize;

        if (confirmSkillBuyGui != null && !confirmSkillBuyGui.closed)
        {
            confirmSkillBuyGui = new SkillsConfirmationGui(scale, confirmSkillBuyGui);
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        matrixStack.pushPose();
        matrixStack.scale(scale, scale, 1.0f);

        updatePan(mouseX, mouseY);

        renderBackground(matrixStack);
        renderSkills(matrixStack, mouseX, mouseY);

        matrixStack.popPose();

        confirmSkillBuyGui.draw(matrixStack, mouseX, mouseY);

        prevMouseX = mouseX;
        prevMouseY = mouseY;
    }

    /**
     * Pans around the skills if the mouse is being dragged.
     *
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     */
    private void updatePan(int mouseX, int mouseY)
    {
        if (mouseDown)
        {
            // Try to pan around the window
            int difX = Math.abs(mouseX - dragStartX);
            int difY = Math.abs(mouseY - dragStartY);

            boolean drag = difX > 4 || difY > 4;

            if (drag || dragging)
            {
                dragging = true;

                moveOffsetX += (mouseX - prevMouseX) / scale;
                moveOffsetY += (mouseY - prevMouseY) / scale;
            }
        }
    }

    /**
     * Renders the tiled background.
     *
     * @param matrixStack the matrix stack.
     */
    private void renderBackground(@Nonnull MatrixStack matrixStack)
    {
        GuiUtil.bindTexture(group.info.backgroundTexture);

        for (int i = -1; i < tilesX + 1; i++)
        {
            for (int j = -1; j < tilesY + 1; j++)
            {
                int x = (int) (getScreenX() / scale) + ((TILE_SIZE + (moveOffsetX % TILE_SIZE)) % TILE_SIZE) + i * TILE_SIZE;
                int y = (int) (getScreenY() / scale) + ((TILE_SIZE + (moveOffsetY % TILE_SIZE)) % TILE_SIZE) + j * TILE_SIZE;

                int i1 = i - (int)Math.floor((moveOffsetX / (double) TILE_SIZE)) + backgroundOffsetX;
                int j1 = j - (int)Math.floor((moveOffsetY / (double) TILE_SIZE)) + backgroundOffsetY;
                int rand = new Random(new Random(i1).nextInt() * new Random(j1).nextInt()).nextInt((256 / TILE_SIZE) * (256 / TILE_SIZE));

                int tileTextureX = (rand % TILE_SIZE) * TILE_SIZE;
                int tileTextureY = (rand / TILE_SIZE) * TILE_SIZE;

                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GuiUtil.scissor(getScreenX(), getScreenY(), width, height);
                blit(matrixStack, x, y, tileTextureX, tileTextureY, TILE_SIZE, TILE_SIZE);
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }
        }
    }

    /**
     * Renders the skills and their connections.
     *
     * @param matrixStack the matrix stack.
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     */
    private void renderSkills(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        enableScissor();

        GuiUtil.bindTexture(GuiTextures.SKILLS_WIDGETS);

        int x = (int) (getScreenX() / scale) + moveOffsetX;
        int y = (int) (getScreenY() / scale) + moveOffsetY;

        // Update the skill control positions
        for (SkillControl skillControl : skillControls)
        {
            skillControl.screenX = (skillControl.getX() + x);
            skillControl.screenY = (skillControl.getY() + y);
        }

        for (SkillControl skillControl : skillControls)
        {
            skillControl.renderParentPathBackgrounds(matrixStack);
        }

        for (SkillControl skillControl : skillControls)
        {
            skillControl.renderParentPathForegrounds(matrixStack);
        }

        for (SkillControl skillControl : skillControls)
        {
            boolean isHover = false;

            if (confirmSkillBuyGui.closed && isMouseOver(mouseX, mouseY))
            {
                if (skillControl.isMouseOver(mouseX, mouseY, scale))
                {
                    isHover = true;
                }
            }

            if (isHover)
            {
                matrixStack.pushPose();
                matrixStack.translate(0.0f, 0.0f, 20.0f);
                disableScissor();

                skillControl.renderHover(matrixStack);

                enableScissor();
                matrixStack.popPose();
            }
            else
            {
                skillControl.render(matrixStack);
            }
        }

        disableScissor();

        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int mods)
    {
        return confirmSkillBuyGui.keyPressed(keyCode, scanCode, mods);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        if (!confirmSkillBuyGui.closed)
        {
            confirmSkillBuyGui.mouseClicked(mouseX, mouseY, button);

            return true;
        }

        if (isMouseOver(mouseX, mouseY))
        {
            dragStartX = mouseX;
            dragStartY = mouseY;
            mouseDown = true;

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        boolean result = false;

        if (!confirmSkillBuyGui.closed)
        {
            confirmSkillBuyGui.mouseReleased(mouseX, mouseY, button);

            result = true;
        }

        if (!dragging)
        {
            if (isMouseOver((int) mouseX, (int) mouseY))
            {
                if (skillControls.stream().anyMatch(skillControl -> skillControl.mouseReleased((int) mouseX, (int) mouseY, button)))
                {
                    result = true;
                }
            }
        }

        mouseDown = false;
        dragging = false;

        return result;
    }

    /**
     * Opens the buy skill confirmation dialog for the given skill.
     *
     * @param skill the skill attempting to be bought.
     */
    public void openConfirmationDialog(@Nonnull Skill skill)
    {
        String name = TextFormatting.LIGHT_PURPLE + skill.info.general.name.getString() + TextFormatting.WHITE;
        confirmSkillBuyGui = new SkillsConfirmationGui(scale, font, skill, GuiUtil.splitText(font, new BlocklingsTranslationTextComponent("skill.buy_confirmation", name).getString(), width < 200 ? width - 10 : width - 50), width, height, width, height);
    }

    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, double scroll)
    {
        int localMouseX = toLocalX(mouseX);
        int localMouseY = toLocalY(mouseY);

        int midMoveX = (int) (moveOffsetX + (SKILL_SIZE / 2 * scale));
        int midMoveY = (int) (moveOffsetY + (SKILL_SIZE / 2 * scale));

        int mouseMoveX = (int) ((int) (moveOffsetX + (localMouseX * scale)) - ((width - SKILL_SIZE) / 2) * scale);
        int mouseMoveY = (int) ((int) (moveOffsetY + (localMouseY * scale)) - ((height - SKILL_SIZE) / 2) * scale);

        // I have literally no idea why this works, but it does (although with some precision issues)
        int difMoveX1 = (int) ((mouseMoveX - midMoveX) / scale);
        int difMoveY1 = (int) ((mouseMoveY - midMoveY) / scale);
        int difMoveX2 = (int) ((mouseMoveX - midMoveX) / scale / scale);
        int difMoveY2 = (int) ((mouseMoveY - midMoveY) / scale / scale);

        // Zoom in
        if (scroll > 0)
        {
            scale *= 2.0f;

            if (scale > 2.0f)
            {
                scale = 2.0f;
            }
            else
            {
                moveOffsetX -= (width / 2) / scale;
                moveOffsetY -= (height / 2) / scale;
                moveOffsetX -= difMoveX1 / scale;
                moveOffsetY -= difMoveY1 / scale;
            }
        }
        // Zoom out
        else
        {
            scale /= 2.0f;

            if (scale < 0.25f)
            {
                scale = 0.25f;
            }
            else
            {
                moveOffsetX += (width / 4) / scale;
                moveOffsetY += (height / 4) / scale;
                moveOffsetX += difMoveX2;
                moveOffsetY += difMoveY2;
            }
        }

        resize(width, height, scale);

        return true;
    }
}
