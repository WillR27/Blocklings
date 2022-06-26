package com.willr27.blocklings.client.gui.controls.skills;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.Control;
import com.willr27.blocklings.client.gui.GuiTextures;
import com.willr27.blocklings.client.gui.GuiUtil;
import com.willr27.blocklings.client.gui.IControl;
import com.willr27.blocklings.client.gui.screens.SkillsScreen;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.skill.Skill;
import com.willr27.blocklings.entity.blockling.skill.SkillGroup;
import com.willr27.blocklings.entity.blockling.skill.info.SkillGuiInfo;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The gui used to display the skill tree in a skill group.
 */
@OnlyIn(Dist.CLIENT)
public class SkillsControl extends Control
{
    /**
     * The width and height of the icon for a skill.
     */
    public static final int SKILL_SIZE = SkillGuiInfo.SkillIconTexture.ICON_SIZE;

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
     * The parent skills screen.
     */
    @Nonnull
    private final SkillsScreen skillsScreen;

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
     * The total drag amount in the x-axis.
     */
    private float totalDragX;

    /**
     * The total drag amount in the y-axis.
     */
    private float totalDragY;

    /**
     * Whether the mouse is down for the current frame.
     */
    private boolean mouseDown;

    /**
     * Whether the user is dragging the mouse.
     */
    private boolean dragging;

    /**
     * The list of controls representing each skill.
     */
    @Nonnull
    private final List<SkillControl> skillControls = new ArrayList<>();

    /**
     * The gui used to confirm buying a skill.
     */
    @Nonnull
    public BuySkillConfirmationControl skillBuyConfirmationControl;

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
        this.skillsScreen = (SkillsScreen) getScreen();
        this.group = group;
        this.minimisedX = minimisedX;
        this.minimisedY = minimisedY;
        this.minimisedWidth = minimisedWidth;
        this.minimisedHeight = minimisedHeight;

        minimise();

        this.backgroundOffsetX = blockling.getRandom().nextInt(1000);
        this.backgroundOffsetY = blockling.getRandom().nextInt(1000);

        for (Skill skill : group.getSkills())
        {
            skillControls.add(new SkillControl(this, skill));
        }

        skillsScreen.skillsContainer.removeChild(skillsScreen.skillBuyConfirmationControl);
        skillsScreen.skillBuyConfirmationControl = new BuySkillConfirmationControl(skillsScreen.skillsContainer, skillControls.get(0), GuiUtil.splitText(font, new BlocklingsTranslationTextComponent("skill.buy_confirmation", "").getString(), width < 200 ? width - 10 : width - 50), width, height, width, height);
        skillBuyConfirmationControl = skillsScreen.skillBuyConfirmationControl;
        skillBuyConfirmationControl.setIsVisible(false);

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
        SkillsScreen skillsScreen = (SkillsScreen) getScreen();
        skillsScreen.tabbedControl.setIsVisible(false);

        skillsScreen.skillsContainer.setX(-10);
        skillsScreen.skillsContainer.setY(-10);
        setX(0);
        setY(0);
        skillsScreen.skillsContainer.setWidth((int) ((screen.width + 20)));
        skillsScreen.skillsContainer.setHeight((int) ((screen.height + 20)));
        resize((int) ((screen.width + 20) / getScale()), (int) ((screen.height + 20) / getScale()));

        moveOffsetX = (int) (((width - SKILL_SIZE) / 2));
        moveOffsetY = (int) (((height - SKILL_SIZE) / 2));

        if (skillsScreen.borderControl != null)
        {
            skillsScreen.borderControl.setIsVisible(false);
        }

        if (skillsScreen.tabbedControl != null)
        {
            skillsScreen.tabbedControl.setIsVisible(false);
        }
    }

    /**
     * Minimises the skills gui.
     */
    public void minimise()
    {
        SkillsScreen skillsScreen = (SkillsScreen) getScreen();
        skillsScreen.tabbedControl.setIsVisible(true);

        skillsScreen.skillsContainer.setX(skillsScreen.contentLeft + 9);
        skillsScreen.skillsContainer.setY(skillsScreen.contentTop + 9);
        setX(minimisedX);
        setY(minimisedY);

        skillsScreen.skillsContainer.setWidth(minimisedWidth);
        skillsScreen.skillsContainer.setHeight(minimisedHeight);
        resize((int) (minimisedWidth / getScale()), (int) (minimisedHeight / getScale()));

        moveOffsetX = (int) (((width - SKILL_SIZE) / 2));
        moveOffsetY = (int) (((height - SKILL_SIZE) / 2));

        if (skillsScreen.borderControl != null)
        {
            skillsScreen.borderControl.setIsVisible(true);
        }

        if (skillsScreen.tabbedControl != null)
        {
            skillsScreen.tabbedControl.setIsVisible(true);
        }
    }

    /**
     * Resizes the skills gui.
     *
     * @param width the new width.
     * @param height the new height.
     */
    private void resize(int width, int height)
    {
        setWidth(width);
        setHeight(height);

        int tileSize = (int) (TILE_SIZE);

        this.tilesX = width / tileSize;
        this.tilesY = height / tileSize;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        matrixStack.pushPose();
//        matrixStack.scale(scale, scale, 1.0f);

        updatePan((int) (mouseX / getEffectiveScale()), (int) (mouseY / getEffectiveScale()));

        renderBackground(matrixStack);
        renderSkills(matrixStack, mouseX, mouseY);

        matrixStack.popPose();

        prevMouseX = (int) (mouseX / getEffectiveScale());
        prevMouseY = (int) (mouseY / getEffectiveScale());
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

                totalDragX += (mouseX - prevMouseX);
                totalDragY += (mouseY - prevMouseY);

                if (Math.abs(totalDragX) >= 1.0f)
                {
                    moveOffsetX += (int) totalDragX;
                    totalDragX -= (int) totalDragX;
                }

                if (Math.abs(totalDragY) >= 1.0f)
                {
                    moveOffsetY += (int) totalDragY;
                    totalDragY -= (int) totalDragY;
                }
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
                int x = (int) (getScreenX()) + ((TILE_SIZE + (moveOffsetX % TILE_SIZE)) % TILE_SIZE) + i * TILE_SIZE;
                int y = (int) (getScreenY()) + ((TILE_SIZE + (moveOffsetY % TILE_SIZE)) % TILE_SIZE) + j * TILE_SIZE;

                int i1 = i - (int)Math.floor((moveOffsetX / (double) TILE_SIZE)) + backgroundOffsetX;
                int j1 = j - (int)Math.floor((moveOffsetY / (double) TILE_SIZE)) + backgroundOffsetY;
                int rand = new Random(new Random(i1).nextInt() * new Random(j1).nextInt()).nextInt((256 / TILE_SIZE) * (256 / TILE_SIZE));

                int tileTextureX = (rand % TILE_SIZE) * TILE_SIZE;
                int tileTextureY = (rand / TILE_SIZE) * TILE_SIZE;

                GuiUtil.addScissorBounds(getScreenX(), getScreenY(), getScreenWidth(), getScreenHeight());
                GuiUtil.enableStackedScissor();
                blit(matrixStack, x, y, tileTextureX, tileTextureY, TILE_SIZE, TILE_SIZE);
                GuiUtil.removeScissorBounds(getScreenX(), getScreenY(), getScreenWidth(), getScreenHeight());
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
        GuiUtil.bindTexture(GuiTextures.SKILLS_WIDGETS);

        int x = (int) (getScreenX()) + moveOffsetX;
        int y = (int) (getScreenY()) + moveOffsetY;

        // Update the skill control positions
        for (SkillControl skillControl : skillControls)
        {
            skillControl.setX(skillControl.skill.info.gui.x + moveOffsetX);
            skillControl.setY(skillControl.skill.info.gui.y + moveOffsetY);
        }

        for (SkillControl skillControl : skillControls)
        {
            skillControl.renderParentPathBackgrounds(matrixStack);
        }

        for (SkillControl skillControl : skillControls)
        {
            skillControl.renderParentPathForegrounds(matrixStack);
        }

        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
    }

    @Override
    public void controlMouseClicked(@Nonnull MouseButtonEvent e)
    {
        dragStartX = (int) (e.mouseX / getEffectiveScale());
        dragStartY = (int) (e.mouseY / getEffectiveScale());
        mouseDown = true;
    }

    @Override
    public void globalMouseReleased(@Nonnull MouseButtonEvent e)
    {
        mouseDown = false;
        dragging = false;
    }

    /**
     * Opens the buy skill confirmation dialog for the given skill.
     *
     * @param skillControl the skill attempting to be bought.
     */
    public void openConfirmationDialog(@Nonnull SkillControl skillControl)
    {
        removeChild(skillBuyConfirmationControl);
        String name = TextFormatting.LIGHT_PURPLE + skillControl.skill.info.general.name.getString() + TextFormatting.WHITE;
        skillsScreen.skillsContainer.removeChild(skillsScreen.skillBuyConfirmationControl);
        skillsScreen.skillBuyConfirmationControl = new BuySkillConfirmationControl(skillsScreen.skillsContainer, skillControl, GuiUtil.splitText(font, new BlocklingsTranslationTextComponent("skill.buy_confirmation", name).getString(), skillsScreen.skillsContainer.width < 200 ? skillsScreen.skillsContainer.width - 10 : skillsScreen.skillsContainer.width - 50), skillsScreen.skillsContainer.width, skillsScreen.skillsContainer.height, skillsScreen.skillsContainer.width, skillsScreen.skillsContainer.height);
        skillBuyConfirmationControl = skillsScreen.skillBuyConfirmationControl;
        skillBuyConfirmationControl.setIsFocused(true);
    }

    @Override
    public void controlMouseScrolled(@Nonnull MouseScrollEvent e)
    {
        if (dragging)
        {
            e.setIsHandled(true);

            return;
        }

        int localMouseX = (int) (toLocalX(e.mouseX) / getEffectiveScale());
        int localMouseY = (int) (toLocalY(e.mouseY) / getEffectiveScale());

        // Zoom in
        if (e.scroll > 0)
        {
            float newScale = getScale() * 2.0f;
            newScale = Math.min(newScale, 2.0f);

            if (newScale != getScale())
            {
                moveOffsetX -= localMouseX / 2.0f;
                moveOffsetY -= localMouseY / 2.0f;

                setScale(newScale);

                width /= 2.0f;
                height /= 2.0f;
            }
        }
        // Zoom out
        else
        {
            float newScale = getScale() * 0.5f;
            newScale = Math.max(newScale, 0.25f);

            if (newScale != getScale())
            {
                moveOffsetX += localMouseX;
                moveOffsetY += localMouseY;

                setScale(newScale);

                width *= 2.0f;
                height *= 2.0f;
            }
        }

        resize(width, height);

        e.setIsHandled(true);
    }
}
