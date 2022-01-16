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
    public static final int SKILL_SIZE = SkillGuiInfo.SkillGuiTexture.ICON_SIZE;
    private static final int TILE_SIZE = 16;

    public final int backgroundOffsetX, backgroundOffsetY;

    private BlocklingEntity blockling;
    private SkillGroup group;
    private int tilesX, tilesY;
    private int prevMouseX, prevMouseY;
    private int moveX, moveY;
    private boolean mouseDown;
    private boolean dragging;
    private int startX, startY;
    private TexturedControl windowWidget;
    private SkillsConfirmationGui confirmGui;

    private final int minimisedX;
    private final int minimisedY;
    private final int minimisedWidth;
    private final int minimisedHeight;

    public float scale = 1.0f;
    private int tileSize = TILE_SIZE;

    private final List<SkillControl> skillControls = new ArrayList<>();

    public SkillsControl(IControl parent, BlocklingEntity blockling, SkillGroup group, int minimisedX, int minimisedY, int minimisedWidth, int minimisedHeight)
    {
        super(parent, minimisedX, minimisedY, minimisedWidth, minimisedHeight);
        this.blockling = blockling;
        this.group = group;
        this.minimisedX = minimisedX;
        this.minimisedY = minimisedY;
        this.minimisedWidth = minimisedWidth;
        this.minimisedHeight = minimisedHeight;

        minimise();

        this.backgroundOffsetX = blockling.getRandom().nextInt(1000);
        this.backgroundOffsetY = blockling.getRandom().nextInt(1000);

        confirmGui = new SkillsConfirmationGui();

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

        moveX = (int) (((width - SKILL_SIZE) / 2) / scale);
        moveY = (int) (((height - SKILL_SIZE) / 2) / scale);
    }

    /**
     * Minimises the skills gui.
     */
    public void minimise()
    {
        setX(minimisedX);
        setY(minimisedY);
        resize(minimisedWidth, minimisedHeight, scale);

        moveX = (int) (((width - SKILL_SIZE) / 2) / scale);
        moveY = (int) (((height - SKILL_SIZE) / 2) / scale);
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

        this.tileSize = (int) (TILE_SIZE * scale);

        this.tilesX = width / tileSize;
        this.tilesY = height / tileSize;

        removeChild(windowWidget);
        addChild(windowWidget = new TexturedControl(font, getScreenX(), getScreenY(), width, height, 0, 0));

        if (confirmGui != null && !confirmGui.closed)
        {
            confirmGui = new SkillsConfirmationGui(scale, confirmGui);
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

        confirmGui.draw(matrixStack, mouseX, mouseY);

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
            int difX = Math.abs(mouseX - startX);
            int difY = Math.abs(mouseY - startY);

            boolean drag = difX > 4 || difY > 4;

            if (drag || dragging)
            {
                dragging = true;

                moveX += (mouseX - prevMouseX) / scale;
                moveY += (mouseY - prevMouseY) / scale;
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
                int x = (int) (getScreenX() / scale) + ((TILE_SIZE + (moveX % TILE_SIZE)) % TILE_SIZE) + i * TILE_SIZE;
                int y = (int) (getScreenY() / scale) + ((TILE_SIZE + (moveY % TILE_SIZE)) % TILE_SIZE) + j * TILE_SIZE;

                int i1 = i - (int)Math.floor((moveX / (double) TILE_SIZE)) + backgroundOffsetX;
                int j1 = j - (int)Math.floor((moveY / (double) TILE_SIZE)) + backgroundOffsetY;
                int rand = new Random(new Random(i1).nextInt() * new Random(j1).nextInt()).nextInt((256 / TILE_SIZE) * (256 / TILE_SIZE));

                int tileTextureX = (rand % TILE_SIZE) * TILE_SIZE;
                int tileTextureY = (rand / TILE_SIZE) * TILE_SIZE;

                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GuiUtil.scissor(getScreenX(), getScreenY(), width, height);
                blit(matrixStack, x, y, tileTextureX, tileTextureY, TILE_SIZE, TILE_SIZE);
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }
        }

//        matrixStack.popPose();
//        fill(matrixStack, left, top - 1, right, top - 2, 0xffffffff);
//        fill(matrixStack, left - 2, top, left - 1, bottom, 0xffffffff);
//        fill(matrixStack, right + 2, top, right + 1, bottom, 0xffffffff);
//        fill(matrixStack, left, bottom + 1, right, bottom + 2, 0xffffffff);
//        matrixStack.pushPose();
//        matrixStack.scale(scale, scale, 1.0f);
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

        int x = (int) (getScreenX() / scale) + moveX;
        int y = (int) (getScreenY() / scale) + moveY;

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

            if (confirmGui.closed && windowWidget.isMouseOver(mouseX, mouseY))
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

    public boolean keyPressed(int keyCode, int i, int j)
    {
        return confirmGui.keyPressed(keyCode, i, j);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        if (!confirmGui.closed)
        {
            confirmGui.mouseClicked(mouseX, mouseY, state);

            return true;
        }

        if (windowWidget.isMouseOver((int) mouseX, (int) mouseY))
        {
            startX = (int) mouseX;
            startY = (int) mouseY;
            mouseDown = true;

            return true;
        }

        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        int x = (int) ((getScreenX() / scale) + moveX);
        int y = (int) ((getScreenY() / scale) + moveY);

        boolean result = false;

        if (!confirmGui.closed)
        {
            confirmGui.mouseReleased(mouseX, mouseY, button);

            result = true;
        }

        if (!dragging)
        {
            if (windowWidget.isMouseOver((int) mouseX, (int) mouseY))
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

    public void openConfirmationDialog(@Nonnull Skill skill)
    {
        String name = TextFormatting.LIGHT_PURPLE + skill.info.general.name.getString() + TextFormatting.WHITE;
        confirmGui = new SkillsConfirmationGui(scale, font, skill, GuiUtil.splitText(font, new BlocklingsTranslationTextComponent("skill.buy_confirmation", name).getString(), width < 200 ? width - 10 : width - 50), width, height, width, height);
    }

    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, double scroll)
    {
        int localMouseX = toLocalX(mouseX);
        int localMouseY = toLocalY(mouseY);

        int midMoveX = (int) (moveX + (SKILL_SIZE / 2 * scale));
        int midMoveY = (int) (moveY + (SKILL_SIZE / 2 * scale));

        int mouseMoveX = (int) ((int) (moveX + (localMouseX * scale)) - ((width - SKILL_SIZE) / 2) * scale);
        int mouseMoveY = (int) ((int) (moveY + (localMouseY * scale)) - ((height - SKILL_SIZE) / 2) * scale);

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
                moveX -= (width / 2) / scale;
                moveY -= (height / 2) / scale;
                moveX -= difMoveX1 / scale;
                moveY -= difMoveY1 / scale;
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
                moveX += (width / 4) / scale;
                moveY += (height / 4) / scale;
                moveX += difMoveX2;
                moveY += difMoveY2;
            }
        }

        resize(width, height, scale);

        return true;
    }
}
