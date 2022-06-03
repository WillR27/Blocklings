package com.willr27.blocklings.gui.controls.skills;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.attribute.Attribute;
import com.willr27.blocklings.attribute.BlocklingAttributes;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.controls.TexturedControl;
import com.willr27.blocklings.skill.Skill;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillControl extends Control
{
    /**
     * The padding at the edge of the description.
     */
    private static final int PADDING = 5;

    private static final int DESCRIPTION_START_OFFSET_Y = 4;
    private static final int HOVER_BOX_WIDTH = 200;
    private static final int HOVER_BOX_HEIGHT = 20;
    private static final int NAME_TEXTURE_Y = 166;
    private static final int DESCRIPTION_TEXTURE_Y = NAME_TEXTURE_Y + HOVER_BOX_HEIGHT;
    private static final int OUTER_WIDTH = 2;

    /**
     * The parent skills control.
     */
    @Nonnull
    public final SkillsControl skillsControl;

    /**
     * The skill.
     */
    @Nonnull
    public final Skill skill;

    /**
     * The parent skills' controls.
     */
    @Nonnull
    public List<SkillControl> parents = new ArrayList<>();

    /**
     * Whether the skill is currently selected.
     */
    public boolean isSelected = false;

    /**
     * @param skillsControl the parent skills control.
     * @param skill the skill.
     */
    public SkillControl(@Nonnull SkillsControl skillsControl, @Nonnull Skill skill)
    {
        super(skillsControl, skill.info.gui.x, skill.info.gui.y, skill.info.gui.iconTexture.width, skill.info.gui.iconTexture.height);
        this.skillsControl = skillsControl;
        this.skill = skill;
   }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        matrixStack.pushPose();
//        matrixStack.scale(skillsControl.scale, skillsControl.scale, 1.0f);

        if (!skillsControl.skillBuyConfirmationControl.isVisible() && skillsControl.isMouseOver(mouseX, mouseY) && isMouseOver(mouseX, mouseY))
        {
            GuiUtil.disableScissor();

            matrixStack.translate(getScreenX() - (getScreenX() / getEffectiveScale()), getScreenY() - (getScreenY() / getEffectiveScale()), 0.0f);
            matrixStack.scale(1.0f / getEffectiveScale(), 1.0f / getEffectiveScale(), 1.0f);

            matrixStack.translate(0.0, 0.0, 10.0);

            renderHover(matrixStack);

            matrixStack.scale(getEffectiveScale(), getEffectiveScale(), 1.0f);
            matrixStack.translate((getScreenX() / getEffectiveScale()) - getScreenX(), (getScreenY() / getEffectiveScale()) - getScreenY(), 0.0f);
        }

        Skill.State state = skill.getState();
        Color colour = state.colour;

        if (isSelected)
        {
            RenderSystem.color3f(0.7f, 1.0f, 0.7f);
        }
        else if (skill.hasConflict() && state != Skill.State.LOCKED)
        {
            RenderSystem.color3f(0.8f, 0.6f, 0.6f);
        }
        else if (state == Skill.State.UNLOCKED && !skill.canBuy())
        {
            RenderSystem.color3f(0.8f, 0.6f, 0.6f);
        }
        else
        {
            RenderSystem.color3f(colour.getRed() / 255.0f, colour.getGreen() / 255.0f, colour.getBlue() / 255.0f);
        }

        renderTexture(matrixStack, skill.info.general.type.texture);

        if (state == Skill.State.LOCKED)
        {
            RenderSystem.color3f(0.0f, 0.0f, 0.0f);
        }
        else if (skill.hasConflict())
        {
            RenderSystem.color3f(0.8f, 0.6f, 0.6f);
        }
        else if (state == Skill.State.UNLOCKED && !skill.canBuy())
        {
            RenderSystem.color3f(0.8f, 0.6f, 0.6f);
        }
        else
        {
            RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        }

        renderTexture(matrixStack, skill.info.gui.iconTexture);

        RenderSystem.color3f(1.0f, 1.0f, 1.0f);

        if (isMouseOver(mouseX, mouseY))
        {
            GuiUtil.enableStackedScissor();
        }

        matrixStack.popPose();
    }

    /**
     * Renders the hovered UI.
     *
     * @param matrixStack the matrix stack.
     */
    public void renderHover(@Nonnull MatrixStack matrixStack)
    {Skill.State state = skill.getState();
        String name = skill.info.general.name.getString();
        int maxWidth = font.width(name) + skill.info.gui.iconTexture.width + PADDING - 1;
        List<String> description = GuiUtil.splitText(font, skill.info.general.desc.getString(), Math.max(maxWidth, 130));

        if (state == Skill.State.LOCKED)
        {
            name = new BlocklingsTranslationTextComponent("skill.unknown").getString();
            description.clear();
            description.add("...");
        }
        else
        {
            Map<BlocklingAttributes.Level, Integer> levelRequirements = skill.info.requirements.levels;
            if (levelRequirements.size() > 0)
            {
                description.add("");
                description.add(new BlocklingsTranslationTextComponent("requirements").getString());

                if (levelRequirements.size() > 0)
                {
                    for (BlocklingAttributes.Level level : levelRequirements.keySet())
                    {
                        int value = levelRequirements.get(level);
                        Attribute<Integer> attribute = skill.blockling.getStats().getLevelAttribute(level);

                        String colour = attribute.getValue() >= value ? "" + TextFormatting.GREEN : "" + TextFormatting.RED;
                        description.add(colour + attribute.createTranslation("required", value).getString() + " " + TextFormatting.DARK_GRAY + "(" + skill.blockling.getStats().getLevelAttribute(level).getValue() + ")");
                    }
                }
            }

            List<Skill> conflicts = skill.conflicts();
            if (!conflicts.isEmpty())
            {
                description.add("");
                description.add(new BlocklingsTranslationTextComponent("conflicts").getString());
                for (Skill conflict : conflicts)
                {
                    description.add(TextFormatting.RED + conflict.info.general.name.getString());
                }
            }
        }

        for (String str : description)
        {
            int width = font.width(str);
            if (width > maxWidth) maxWidth = width;
        }
        maxWidth += PADDING * 2;

        int startX = 4;
        int endX = startX + maxWidth;

        int nameY = 2;
        int descY = nameY + 23;

        RenderSystem.enableDepthTest();
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);

        TexturedControl texturedControl = new TexturedControl(this, startX, descY - DESCRIPTION_START_OFFSET_Y, new GuiTexture(GuiTextures.SKILLS, 0, DESCRIPTION_TEXTURE_Y + OUTER_WIDTH, maxWidth, DESCRIPTION_START_OFFSET_Y));
        matrixStack.scale(texturedControl.getEffectiveScale(), texturedControl.getEffectiveScale(), 1.0f);
        matrixStack.translate((texturedControl.getScreenX() / texturedControl.getEffectiveScale()) - texturedControl.getScreenX(), (texturedControl.getScreenY() / texturedControl.getEffectiveScale()) - texturedControl.getScreenY(), 0.0f);
        texturedControl.render(matrixStack, 0, 0, 0);
        matrixStack.translate(texturedControl.getScreenX() - (texturedControl.getScreenX() / texturedControl.getEffectiveScale()), texturedControl.getScreenY() - (texturedControl.getScreenY() / texturedControl.getEffectiveScale()), 0.0f);
        matrixStack.scale(1.0f / texturedControl.getEffectiveScale(), 1.0f / texturedControl.getEffectiveScale(), 1.0f);
        removeChild(texturedControl);
        texturedControl = new TexturedControl(this, endX, descY - DESCRIPTION_START_OFFSET_Y, new GuiTexture(GuiTextures.SKILLS, HOVER_BOX_WIDTH - OUTER_WIDTH, DESCRIPTION_TEXTURE_Y + OUTER_WIDTH, OUTER_WIDTH, DESCRIPTION_START_OFFSET_Y));
        matrixStack.scale(texturedControl.getEffectiveScale(), texturedControl.getEffectiveScale(), 1.0f);
        matrixStack.translate((texturedControl.getScreenX() / texturedControl.getEffectiveScale()) - texturedControl.getScreenX(), (texturedControl.getScreenY() / texturedControl.getEffectiveScale()) - texturedControl.getScreenY(), 0.0f);
        texturedControl.render(matrixStack, 0, 0, 0);
        matrixStack.translate(texturedControl.getScreenX() - (texturedControl.getScreenX() / texturedControl.getEffectiveScale()), texturedControl.getScreenY() - (texturedControl.getScreenY() / texturedControl.getEffectiveScale()), 0.0f);
        matrixStack.scale(1.0f / texturedControl.getEffectiveScale(), 1.0f / texturedControl.getEffectiveScale(), 1.0f);
        removeChild(texturedControl);
        int gap = 10;
        int i = 0;
        for (String str : description)
        {
            TexturedControl lineControl = new TexturedControl(this, startX, descY + i * gap, new GuiTexture(GuiTextures.SKILLS, 0, DESCRIPTION_TEXTURE_Y + OUTER_WIDTH, maxWidth, gap));
            matrixStack.scale(lineControl.getEffectiveScale(), lineControl.getEffectiveScale(), 1.0f);
            matrixStack.translate((lineControl.getScreenX() / lineControl.getEffectiveScale()) - lineControl.getScreenX(), (lineControl.getScreenY() / lineControl.getEffectiveScale()) - lineControl.getScreenY(), 0.0f);
            lineControl.render(matrixStack, 0, 0, 0);
            matrixStack.translate(lineControl.getScreenX() - (lineControl.getScreenX() / lineControl.getEffectiveScale()), lineControl.getScreenY() - (lineControl.getScreenY() / lineControl.getEffectiveScale()), 0.0f);
            matrixStack.scale(1.0f / lineControl.getEffectiveScale(), 1.0f / lineControl.getEffectiveScale(), 1.0f);
            texturedControl = new TexturedControl(this, endX, descY + i * gap, new GuiTexture(GuiTextures.SKILLS, HOVER_BOX_WIDTH - OUTER_WIDTH, DESCRIPTION_TEXTURE_Y + OUTER_WIDTH, OUTER_WIDTH, gap));
            matrixStack.scale(texturedControl.getEffectiveScale(), texturedControl.getEffectiveScale(), 1.0f);
            matrixStack.translate((texturedControl.getScreenX() / texturedControl.getEffectiveScale()) - texturedControl.getScreenX(), (texturedControl.getScreenY() / texturedControl.getEffectiveScale()) - texturedControl.getScreenY(), 0.0f);
            texturedControl.render(matrixStack, 0, 0, 0);
            matrixStack.translate(texturedControl.getScreenX() - (texturedControl.getScreenX() / texturedControl.getEffectiveScale()), texturedControl.getScreenY() - (texturedControl.getScreenY() / texturedControl.getEffectiveScale()), 0.0f);
            matrixStack.scale(1.0f / texturedControl.getEffectiveScale(), 1.0f / texturedControl.getEffectiveScale(), 1.0f);
            removeChild(texturedControl);
            matrixStack.scale(lineControl.getEffectiveScale(), lineControl.getEffectiveScale(), 1.0f);
            matrixStack.translate((lineControl.getScreenX() / lineControl.getEffectiveScale()) - lineControl.getScreenX(), (lineControl.getScreenY() / lineControl.getEffectiveScale()) - lineControl.getScreenY(), 0.0f);
            lineControl.renderShadowedText(matrixStack, str, PADDING, 0, false, 0xffffffff);
            matrixStack.translate(lineControl.getScreenX() - (lineControl.getScreenX() / lineControl.getEffectiveScale()), lineControl.getScreenY() - (lineControl.getScreenY() / lineControl.getEffectiveScale()), 0.0f);
            matrixStack.scale(1.0f / lineControl.getEffectiveScale(), 1.0f / lineControl.getEffectiveScale(), 1.0f);
            removeChild(lineControl);
            i++;
        }
        texturedControl = new TexturedControl(this, startX, descY + i * gap - 1, new GuiTexture(GuiTextures.SKILLS, 0, DESCRIPTION_TEXTURE_Y + (HOVER_BOX_HEIGHT - OUTER_WIDTH - 1), maxWidth, OUTER_WIDTH + 1));
        matrixStack.scale(texturedControl.getEffectiveScale(), texturedControl.getEffectiveScale(), 1.0f);
        matrixStack.translate((texturedControl.getScreenX() / texturedControl.getEffectiveScale()) - texturedControl.getScreenX(), (texturedControl.getScreenY() / texturedControl.getEffectiveScale()) - texturedControl.getScreenY(), 0.0f);
        texturedControl.render(matrixStack, 0, 0, 0);
        matrixStack.translate(texturedControl.getScreenX() - (texturedControl.getScreenX() / texturedControl.getEffectiveScale()), texturedControl.getScreenY() - (texturedControl.getScreenY() / texturedControl.getEffectiveScale()), 0.0f);
        matrixStack.scale(1.0f / texturedControl.getEffectiveScale(), 1.0f / texturedControl.getEffectiveScale(), 1.0f);
        removeChild(texturedControl);
        texturedControl = new TexturedControl(this, endX, descY + i * gap - 1, new GuiTexture(GuiTextures.SKILLS, HOVER_BOX_WIDTH - OUTER_WIDTH, DESCRIPTION_TEXTURE_Y + (HOVER_BOX_HEIGHT - OUTER_WIDTH - 1), OUTER_WIDTH, OUTER_WIDTH + 1));
        matrixStack.scale(texturedControl.getEffectiveScale(), texturedControl.getEffectiveScale(), 1.0f);
        matrixStack.translate((texturedControl.getScreenX() / texturedControl.getEffectiveScale()) - texturedControl.getScreenX(), (texturedControl.getScreenY() / texturedControl.getEffectiveScale()) - texturedControl.getScreenY(), 0.0f);
        texturedControl.render(matrixStack, 0, 0, 0);
        matrixStack.translate(texturedControl.getScreenX() - (texturedControl.getScreenX() / texturedControl.getEffectiveScale()), texturedControl.getScreenY() - (texturedControl.getScreenY() / texturedControl.getEffectiveScale()), 0.0f);
        matrixStack.scale(1.0f / texturedControl.getEffectiveScale(), 1.0f / texturedControl.getEffectiveScale(), 1.0f);
        removeChild(texturedControl);

        TexturedControl nameControl = new TexturedControl(this, startX, nameY, new GuiTexture(GuiTextures.SKILLS, 0, NAME_TEXTURE_Y, maxWidth, HOVER_BOX_HEIGHT));
        TexturedControl nameControlEnd = new TexturedControl(this, endX, nameY, new GuiTexture(GuiTextures.SKILLS, HOVER_BOX_WIDTH - OUTER_WIDTH, NAME_TEXTURE_Y, OUTER_WIDTH, HOVER_BOX_HEIGHT));

        if (state == Skill.State.LOCKED) RenderSystem.color3f(0.5f, 0.5f, 0.5f);
        else RenderSystem.color3f(skill.info.gui.colour.getRed() / 255f, skill.info.gui.colour.getGreen() / 255f, skill.info.gui.colour.getBlue() / 255f);
        matrixStack.scale(nameControl.getEffectiveScale(), nameControl.getEffectiveScale(), 1.0f);
        matrixStack.translate((nameControl.getScreenX() / nameControl.getEffectiveScale()) - nameControl.getScreenX(), (nameControl.getScreenY() / nameControl.getEffectiveScale()) - nameControl.getScreenY(), 0.0f);
        nameControl.render(matrixStack, 0, 0, 0);
        nameControl.renderShadowedText(matrixStack, name, skill.info.gui.iconTexture.width, 6, false, 0xffffffff);
        matrixStack.translate(nameControl.getScreenX() - (nameControl.getScreenX() / nameControl.getEffectiveScale()), nameControl.getScreenY() - (nameControl.getScreenY() / nameControl.getEffectiveScale()), 0.0f);
        matrixStack.scale(1.0f / nameControl.getEffectiveScale(), 1.0f / nameControl.getEffectiveScale(), 1.0f);

        matrixStack.scale(nameControlEnd.getEffectiveScale(), nameControlEnd.getEffectiveScale(), 1.0f);
        matrixStack.translate((nameControlEnd.getScreenX() / nameControlEnd.getEffectiveScale()) - nameControlEnd.getScreenX(), (nameControlEnd.getScreenY() / nameControlEnd.getEffectiveScale()) - nameControlEnd.getScreenY(), 0.0f);
        nameControlEnd.render(matrixStack, 0, 0, 0);
        matrixStack.translate(nameControlEnd.getScreenX() - (nameControlEnd.getScreenX() / nameControlEnd.getEffectiveScale()), nameControlEnd.getScreenY() - (nameControlEnd.getScreenY() / nameControlEnd.getEffectiveScale()), 0.0f);
        matrixStack.scale(1.0f / nameControlEnd.getEffectiveScale(), 1.0f / nameControlEnd.getEffectiveScale(), 1.0f);
        
        removeChild(nameControl);
        removeChild(nameControlEnd);
    }

    public void renderParentPathBackgrounds(@Nonnull MatrixStack matrixStack)
    {
        parents.forEach(parentControl -> renderPath(matrixStack, parentControl, 4, 0xff000000, skill.info.gui.connectionType));
    }

    public void renderParentPathForegrounds(@Nonnull MatrixStack matrixStack)
    {
        for (SkillControl parentControl : parents)
        {
            Skill.State state = parentControl.skill.getState();
            int colour = state.colour.darker().darker().getRGB();

            if (state == Skill.State.BOUGHT)
            {
                colour = 0xffffff;
            }

            renderPath(matrixStack, parentControl, 2, 0xff000000 + colour, skill.info.gui.connectionType);
        }
    }

    public void renderPath(@Nonnull MatrixStack matrixStack, @Nonnull SkillControl skill, int width, int colour, @Nonnull ConnectionType connectionType)
    {
        renderPath(matrixStack, findPath(skill, connectionType), width, colour);
    }

    private void renderPath(@Nonnull MatrixStack matrixStack, @Nonnull Vec2i[] path, int width, int colour)
    {
        width *= getEffectiveScale();

        for (int i = 0; i < path.length - 1; i++)
        {
            int x1 = path[i].x;
            int y1 = path[i].y;
            int x2 = path[i + 1].x;
            int y2 = path[i + 1].y;

            int dx = x2 - x1;
            int dy = y2 - y1;

            Direction direction = Direction.RIGHT;
            if (dx < 0 ) direction = Direction.LEFT;
            else if (dy < 0) direction = Direction.UP;
            else if (dy > 0) direction = Direction.DOWN;

            int d = (width + 1) / 2;

            if (direction == Direction.RIGHT)
            {
                y1 -= d;
                x2 += d;
                y2 += d;
            }
            else if (direction == Direction.LEFT)
            {
                y1 -= d;
                x2 -= d;
                y2 += d;
            }
            else if (direction == Direction.UP)
            {
                x1 -= d;
                x2 += d;
                y2 -= d;
            }
            else if (direction == Direction.DOWN)
            {
                x1 -= d;
                x2 += d;
                y2 += d;
            }

            if (x1 != x2 && y1 != y2)
            {
                // Hack to ignore gui scale.
                float guiScale = (float) Minecraft.getInstance().getWindow().getGuiScale();
                MatrixStack matrixStack1 = new MatrixStack();
                matrixStack1.scale(1.0f / guiScale, 1.0f / guiScale, 1);
                fill(matrixStack1, x1, y1, x2, y2, colour);
            }
        }
    }

    @Nonnull
    private Vec2i[] findPath(@Nonnull SkillControl ability, @Nonnull ConnectionType connectionType)
    {
        Vec2i[] path = new Vec2i[3];

        int x1 = screenX + getScreenWidth() / 2;
        int y1 = screenY + getScreenHeight() / 2;
        int x2 = ability.screenX + ability.getScreenWidth() / 2;
        int y2 = ability.screenY + ability.getScreenHeight() / 2;

        if (connectionType == ConnectionType.SINGLE_LONGEST_FIRST || connectionType == ConnectionType.SINGLE_SHORTEST_FIRST)
        {
            int dx = x2 - x1;
            int dy = y2 - y1;

            int cx = dx;
            int cy = dy;

            if (connectionType == ConnectionType.SINGLE_LONGEST_FIRST)
            {
                cx = dy;
                cy = dx;
            }

            if (Math.abs(cx) > Math.abs(cy))
            {
                path[1] = new Vec2i(x1 + dx, y1);
            }
            else
            {
                path[1] = new Vec2i(x1, y1 + dy);
            }
        }
        else if (connectionType == ConnectionType.DOUBLE_SHORTEST_SPLIT || connectionType == ConnectionType.DOUBLE_LONGEST_SPLIT)
        {
            path = new Vec2i[4];

            int dx = x2 - x1;
            int dy = y2 - y1;

            int cx = dx;
            int cy = dy;

            if (connectionType == ConnectionType.DOUBLE_SHORTEST_SPLIT)
            {
                cx = dy;
                cy = dx;
            }

            if (Math.abs(cx) > Math.abs(cy))
            {
                path[1] = new Vec2i(x1 + dx / 2, y1);
                path[2] = new Vec2i(x1 + dx / 2, y1 + dy);
            }
            else
            {
                path[1] = new Vec2i(x1, y1 + dy / 2);
                path[2] = new Vec2i(x1 + dx, y1 + dy / 2);
            }
        }

        path[0] = new Vec2i(x1, y1);
        path[path.length - 1] = new Vec2i(x2, y2);

        return path;
    }

    @Override
    public void controlMouseReleased(@Nonnull MouseButtonEvent e)
    {

    }

    @Override
    public void globalMouseReleased(@Nonnull MouseButtonEvent e)
    {
        if (e.isHandled())
        {
            return;
        }

        if (!skillsControl.isMouseOver(e.mouseX, e.mouseY) || !isMouseOver(e.mouseX, e.mouseY))
        {
            isSelected = false;
        }
        else if (!skill.isBought())
        {
            if (isSelected)
            {
                skillsControl.openConfirmationDialog(this);
            }
            else
            {
                isSelected = true;
            }
        }
    }

    private enum Direction
    {
        LEFT, UP, RIGHT, DOWN;
    }

    public enum ConnectionType
    {
        SINGLE_LONGEST_FIRST,
        SINGLE_SHORTEST_FIRST,
        DOUBLE_LONGEST_SPLIT,
        DOUBLE_SHORTEST_SPLIT;
    }

    private static class Vec2i
    {
        public final int x, y;

        public Vec2i(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }
}
