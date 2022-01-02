package com.willr27.blocklings.gui.screens.guis;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.attribute.Attribute;
import com.willr27.blocklings.attribute.attributes.numbers.IntAttribute;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingStats;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.widgets.SkillWidget;
import com.willr27.blocklings.gui.widgets.TexturedWidget;
import com.willr27.blocklings.skills.Skill;
import com.willr27.blocklings.skills.SkillGroup;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SkillsGui extends AbstractGui
{
    private static final int TILE_SIZE = 16;
//    private static final int SKILL_SIZE = SkillGuiInfo.AbilityGuiTexture.ICON_SIZE;

    private static final int LINE_INNER_WIDTH = 2;
    private static final int LINE_BORDER_WIDTH = 4;

    public final int backgroundOffsetX, backgroundOffsetY;
    private BlocklingEntity blockling;
    private SkillGroup group;
    private FontRenderer font;
    private int width, height;
    private int centerX, centerY;
    private int left, top, right, bottom;
    private int tilesX, tilesY;
    private int prevMouseX, prevMouseY;
    private int moveX, moveY;
    private boolean mouseDown;
    private boolean dragging;
    private int startX, startY;
    private TexturedWidget windowWidget;
    private SkillsConfirmationGui confirmGui;
    private Skill selectedSkill;
    private int windowWidth;
    private int windowHeight;

    public float scale = 1.0f;
    private int tileSize = TILE_SIZE;

    public SkillsGui(BlocklingEntity blockling, SkillGroup skillGroup, FontRenderer font, int width, int height, int centerX, int centerY, int windowWidth, int windowHeight)
    {
        this.blockling = blockling;
        this.group = skillGroup;
        this.font = font;
        this.centerX = centerX;
        this.centerY = centerY;
        resize(width, height, scale);
        this.backgroundOffsetX = blockling.getRandom().nextInt(1000);
        this.backgroundOffsetY = blockling.getRandom().nextInt(1000);
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;

        moveX = width / 2 - 12;
        moveY = height / 2 - 12;
        confirmGui = new SkillsConfirmationGui();
    }

    public void resize(int width, int height, float scale)
    {
        moveX -= (this.width - width) / 2;
        moveY -= (this.height - height) / 2;

        this.width = width;
        this.height = height;
        this.scale = scale;

        this.tileSize = (int) (TILE_SIZE * scale);

        this.left = centerX - width / 2;
        this.top = centerY - height / 2;
        this.right = left + width;
        this.bottom = top + height;
        this.tilesX = width / tileSize;
        this.tilesY = height / tileSize;

        windowWidget = new TexturedWidget(font, left, top, width, height, 0, 0);

        if (confirmGui != null && !confirmGui.closed)
        {
            confirmGui = new SkillsConfirmationGui(scale, confirmGui);
        }
    }

    public void draw(MatrixStack matrixStack, int mouseX, int mouseY)
    {
//        scale = 2.0f;
//        resize(width, height, scale);

        matrixStack.pushPose();
        matrixStack.scale(scale, scale, 1.0f);

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

        drawBackground(matrixStack);
        drawAbilities(matrixStack, mouseX, mouseY);

//        int x = left + moveX;
//        int y = top + moveY;
//        for (Ability ability : abilityGroup.getAbilities())
//        {
//            AbilityWidget abilityWidget = new AbilityWidget(font, ability.x + x, ability.y + y, ABILITY_SIZE, ABILITY_SIZE, ability.type.textureX * ABILITY_SIZE, 0);
//
//            boolean isHover = false;
//            if (/*confirmGui.closed && */windowWidget.isMouseOver(mouseX, mouseY))
//            {
//                if (abilityWidget.isMouseOver(mouseX, mouseY))
//                {
//                    fill(matrixStack, left, top, right, bottom, 0x55000000);
//                    RenderSystem.color3f(1.0f, 1.0f, 1.0f);
//                    break;
//                }
//            }
//        }

        matrixStack.popPose();

        confirmGui.draw(matrixStack, mouseX, mouseY);

        prevMouseX = mouseX;
        prevMouseY = mouseY;
    }

    private void drawBackground(MatrixStack matrixStack)
    {
        GuiUtil.bindTexture(group.info.backgroundTexture);

        for (int i = -1; i < tilesX + 1; i++)
        {
            for (int j = -1; j < tilesY + 1; j++)
            {
                int x = (int) (left / scale) + ((TILE_SIZE + (moveX % TILE_SIZE)) % TILE_SIZE) + i * TILE_SIZE;
                int y = (int) (top / scale) + ((TILE_SIZE + (moveY % TILE_SIZE)) % TILE_SIZE) + j * TILE_SIZE;

                int i1 = i - (int)Math.floor((moveX / (double) TILE_SIZE)) + backgroundOffsetX;
                int j1 = j - (int)Math.floor((moveY / (double) TILE_SIZE)) + backgroundOffsetY;
                int rand = new Random(new Random(i1).nextInt() * new Random(j1).nextInt()).nextInt((256 / TILE_SIZE) * (256 / TILE_SIZE));

                int tileTextureX = (rand % TILE_SIZE) * TILE_SIZE;
                int tileTextureY = (rand / TILE_SIZE) * TILE_SIZE;

                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GuiUtil.scissor(left, top, width, height);
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

    private void drawAbilities(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GuiUtil.scissor(left, top, width, height);

        GuiUtil.bindTexture(GuiUtil.SKILLS_WIDGETS);

        int x = (int) (left / scale) + moveX;
        int y = (int) (top / scale) + moveY;

        for (Skill skill : group.getSkills())
        {
            for (Skill parent : skill.parents())
            {
                SkillWidget skillWidget = new SkillWidget(font, skill.info.gui.x + x, skill.info.gui.y + y, skill.info.gui.texture.width, skill.info.gui.texture.height, skill.info.general.type.textureX * skill.info.gui.texture.width, 0);
                SkillWidget parentWidget = new SkillWidget(font, parent.info.gui.x + x, parent.info.gui.y + y, parent.info.gui.texture.width, parent.info.gui.texture.height, parent.info.general.type.textureX * parent.info.gui.texture.width, 0);

                skillWidget.connect(matrixStack, parentWidget, LINE_BORDER_WIDTH, 0xff000000, skill.info.gui.connectionType);
            }
        }

        for (Skill skill : group.getSkills())
        {
            for (Skill parent : skill.parents())
            {
                SkillWidget abilityWidget = new SkillWidget(font, skill.info.gui.x + x, skill.info.gui.y + y, skill.info.gui.texture.width, skill.info.gui.texture.height, skill.info.general.type.textureX * skill.info.gui.texture.width, 0);
                SkillWidget parentWidget = new SkillWidget(font, parent.info.gui.x + x, parent.info.gui.y + y, parent.info.gui.texture.width, parent.info.gui.texture.height, parent.info.general.type.textureX * parent.info.gui.texture.width, 0);

                Skill.State state = parent.getState();
                int colour = state.colour.darker().darker().getRGB();
                if (state == Skill.State.BOUGHT) colour = 0xffffff;
//                if (state != AbilityState.LOCKED && abilityGroup.hasConflict(parent)) colour = 0xcc3333;
                abilityWidget.connect(matrixStack, parentWidget, LINE_INNER_WIDTH, 0xff000000 + colour, skill.info.gui.connectionType);

//                matrixStack.pushPose();
//                matrixStack.translate(0.0f, 0.0f, 100.0f);
//                matrixStack.scale(0.25f, 0.25f, 0.25f);
//                fill((abilities.x + x) * 4, (abilities.y + y + 8 + 3) * 4, (abilities.x + x) * 4 + 8, (abilities.y + y + 8 + 3) * 4 + 8, 0xffff0000);
//                matrixStack.popPose();
            }
        }

        boolean foundHover = false;
        for (Skill skill : group.getSkills())
        {
            SkillWidget skillWidget = new SkillWidget(font, skill.info.gui.x + x, skill.info.gui.y + y, skill.info.gui.texture.width, skill.info.gui.texture.height, skill.info.general.type.textureX * skill.info.gui.texture.width, 0);

            boolean isHover = false;
            if (confirmGui.closed && !foundHover && windowWidget.isMouseOver(mouseX, mouseY))
            {
                if (skillWidget.isMouseOver(mouseX, mouseY, scale))
                {
                    drawAbilityHover(matrixStack, skill, skillWidget);
                    foundHover = true;
                    isHover = true;
                }
            }

            matrixStack.pushPose();

            if (isHover)
            {
                matrixStack.translate(0.0f, 0.0f, 20.0f);
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }

            GuiUtil.bindTexture(GuiUtil.SKILLS_WIDGETS);

            Skill.State state = skill.getState();
            Color colour = state.colour;
            if (skill == selectedSkill) RenderSystem.color3f(0.7f, 1.0f, 0.7f);
            else if (skill.hasConflict() && state != Skill.State.LOCKED) RenderSystem.color3f(0.8f, 0.6f, 0.6f);
            else if (state == Skill.State.UNLOCKED && !skill.canBuy()) RenderSystem.color3f(0.8f, 0.6f, 0.6f);
            else
            {
//                if (skillGroup.allParentsHaveState(skill, Skill.State.LOCKED) && skill.getParents().length != 0) colour = colour.darker().darker().darker();
                RenderSystem.color3f(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f);
            }
            if (isHover) skillWidget.render(matrixStack, mouseX, mouseY);
            else skillWidget.render(matrixStack, mouseX, mouseY);

            GuiUtil.bindTexture(skill.info.gui.texture.texture);

            if (state == Skill.State.LOCKED) RenderSystem.color3f(0.0f, 0.0f, 0.0f);
            else if (skill.hasConflict()) RenderSystem.color3f(0.8f, 0.6f, 0.6f);
            else if (state == Skill.State.UNLOCKED && !skill.canBuy()) RenderSystem.color3f(0.8f, 0.6f, 0.6f);
            else  RenderSystem.color3f(1.0f, 1.0f, 1.0f);
            skillWidget = new SkillWidget(font, skill.info.gui.x + x, skill.info.gui.y + y, skill.info.gui.texture.width, skill.info.gui.texture.height, skill.info.gui.texture.x, skill.info.gui.texture.y);
            if (isHover) skillWidget.render(matrixStack, mouseX, mouseY);
            else skillWidget.render(matrixStack, mouseX, mouseY);

            if (isHover)
            {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GuiUtil.scissor(left, top, width, height);
            }

            matrixStack.popPose();
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
    }

    private static final int HOVER_PADDING = 5;
    private static final int DESCRIPTION_START_OFFSET_Y = 4;
    private static final int HOVER_BOX_WIDTH = 200;
    private static final int HOVER_BOX_HEIGHT = 20;
    private static final int NAME_TEXTURE_Y = 166;
    private static final int DESCRIPTION_TEXTURE_Y = NAME_TEXTURE_Y + HOVER_BOX_HEIGHT;
    private static final int OUTER_WIDTH = 2;

    private void drawAbilityHover(MatrixStack matrixStack, Skill skill, SkillWidget skillWidget) // TODO: CLEANUP
    {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        GuiUtil.bindTexture(GuiUtil.SKILLS);
        matrixStack.pushPose();
        matrixStack.translate(0.0f, 0.0f, 10.0f);

        Skill.State state = skill.getState();
        String name = skill.info.general.name.getString();
        int maxWidth = font.width(name) + skill.info.gui.texture.width + HOVER_PADDING - 1;
        List<String> description = GuiUtil.splitText(font, skill.info.general.desc.getString(), Math.max(maxWidth, 130));

        if (state == Skill.State.LOCKED)
        {
            name = new BlocklingsTranslationTextComponent("skill.unknown").getString();
            description.clear();
            description.add("...");
        }
        else
        {
            Map<BlocklingStats.Level, Integer> levelRequirements = skill.info.requirements.levels;
            if (levelRequirements.size() > 0)
            {
                description.add("");
                description.add(new BlocklingsTranslationTextComponent("requirements").getString());

                if (levelRequirements.size() > 0)
                {
                    for (BlocklingStats.Level level : levelRequirements.keySet())
                    {
                        int value = levelRequirements.get(level);
                        Attribute<Integer> attribute = blockling.getStats().getLevelAttribute(level);

                        String colour = attribute.getValue() >= value ? "" + TextFormatting.GREEN : "" + TextFormatting.RED;
                        description.add(colour + attribute.createTranslation("required", value).getString() + " " + TextFormatting.DARK_GRAY + "(" + blockling.getStats().getLevelAttribute(level).getValue() + ")");
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
        maxWidth += HOVER_PADDING * 2;

        int startX = skillWidget.x - 4;
        int endX = startX + maxWidth;

        int nameY = skillWidget.y + 2;
        int descY = nameY + 23;

        RenderSystem.enableDepthTest();

        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        new TexturedWidget(font, startX, descY - DESCRIPTION_START_OFFSET_Y, maxWidth, DESCRIPTION_START_OFFSET_Y, 0, DESCRIPTION_TEXTURE_Y + OUTER_WIDTH).render(matrixStack, 0, 0);
        new TexturedWidget(font, endX, descY - DESCRIPTION_START_OFFSET_Y, OUTER_WIDTH, DESCRIPTION_START_OFFSET_Y, HOVER_BOX_WIDTH - OUTER_WIDTH, DESCRIPTION_TEXTURE_Y + OUTER_WIDTH).render(matrixStack, 0, 0);
        int gap = 10;
        int i = 0;
        for (String str : description)
        {
            GuiUtil.bindTexture(GuiUtil.SKILLS);
            TexturedWidget lineWidget = new TexturedWidget(font, startX, descY + i * gap, maxWidth, gap, 0, DESCRIPTION_TEXTURE_Y + OUTER_WIDTH);
            lineWidget.render(matrixStack, 0, 0);
            new TexturedWidget(font, endX, descY + i * gap, OUTER_WIDTH, gap, HOVER_BOX_WIDTH - OUTER_WIDTH, DESCRIPTION_TEXTURE_Y + OUTER_WIDTH).render(matrixStack, 0, 0);
            lineWidget.renderText(matrixStack, str, -font.width(str) - HOVER_PADDING, 0, true, 0xffffffff);
            i++;
        }
        GuiUtil.bindTexture(GuiUtil.SKILLS);
        new TexturedWidget(font, startX, descY + i * gap - 1, maxWidth, OUTER_WIDTH + 1, 0, DESCRIPTION_TEXTURE_Y + (HOVER_BOX_HEIGHT - OUTER_WIDTH - 1)).render(matrixStack, 0, 0);
        new TexturedWidget(font, endX, descY + i * gap - 1, OUTER_WIDTH, OUTER_WIDTH + 1, HOVER_BOX_WIDTH - OUTER_WIDTH, DESCRIPTION_TEXTURE_Y + (HOVER_BOX_HEIGHT - OUTER_WIDTH - 1)).render(matrixStack, 0, 0);

        TexturedWidget nameWidget = new TexturedWidget(font, startX, nameY, maxWidth, HOVER_BOX_HEIGHT, 0, NAME_TEXTURE_Y);
        TexturedWidget nameWidgetEnd = new TexturedWidget(font, endX, nameY, OUTER_WIDTH, HOVER_BOX_HEIGHT, HOVER_BOX_WIDTH - OUTER_WIDTH, NAME_TEXTURE_Y);

        if (state == Skill.State.LOCKED) RenderSystem.color3f(0.5f, 0.5f, 0.5f);
        else RenderSystem.color3f(skill.info.gui.colour.getRed() / 255f, skill.info.gui.colour.getGreen() / 255f, skill.info.gui.colour.getBlue() / 255f);
        nameWidget.render(matrixStack, 0, 0);
        nameWidgetEnd.render(matrixStack, 0, 0);
        nameWidget.renderText(matrixStack, name, -font.width(name) - (skill.info.gui.texture.width + HOVER_PADDING * 2 - 2), 6, true, 0xffffffff);

        matrixStack.popPose();
        GuiUtil.bindTexture(GuiUtil.SKILLS_WIDGETS);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
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
        }

        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        int x = (int) ((left / scale) + moveX);
        int y = (int) ((top / scale) + moveY);

        if (!confirmGui.closed)
        {
            if (confirmGui.mouseReleased(mouseX, mouseY, state))
            {
                selectedSkill = null;
            }

            return true;
        }

        boolean doneSomething = false;
        if (!dragging)
        {
            if (windowWidget.isMouseOver((int) mouseX, (int) mouseY))
            {
                boolean resetSelectedAbility = true;

                if (selectedSkill != null)
                {
                    for (Skill skill : group.getSkills())
                    {
                        if (skill != selectedSkill)
                        {
                            if (skill.canBuy())
                            {
                                SkillWidget abilityWidget = new SkillWidget(font, skill.info.gui.x + x, skill.info.gui.y + y, skill.info.gui.texture.width, skill.info.gui.texture.height, skill.info.general.type.textureX * skill.info.gui.texture.width, 0);
                                if (abilityWidget.isMouseOver((int) mouseX, (int) mouseY, scale))
                                {
                                    selectedSkill = skill;
                                    resetSelectedAbility = false;
                                    doneSomething = true;
                                    break;
                                }
                            }
                            continue;
                        }

                        int minState = skill.parents().size() == 100 ? 0 : Skill.State.values().length;
                        for (Skill parent : skill.parents())
                        {
                            int parentState = parent.getState().ordinal();
                            if (parentState < minState) minState = parentState;
                        }

                        if (skill.getState().ordinal() < minState)
                        {
                            SkillWidget abilityWidget = new SkillWidget(font, skill.info.gui.x + x, skill.info.gui.y + y, skill.info.gui.texture.width, skill.info.gui.texture.height, skill.info.general.type.textureX * skill.info.gui.texture.width, 0);

                            if (abilityWidget.isMouseOver((int) mouseX, (int) mouseY, scale))
                            {
                                String name = TextFormatting.LIGHT_PURPLE + skill.info.general.name.getString() + TextFormatting.WHITE;
                                confirmGui = new SkillsConfirmationGui(scale, font, skill, GuiUtil.splitText(font, new BlocklingsTranslationTextComponent("skill.buy_confirmation", name).getString(), width < 200 ? width - 10 : width - 50), windowWidth, windowHeight, width, height);
                                resetSelectedAbility = false;
                                doneSomething = true;
                            }
                        }
                    }
                }
                else
                {
                    for (Skill skill : group.getSkills())
                    {
                        if (skill.canBuy())
                        {
                            SkillWidget skillWidget = new SkillWidget(font, skill.info.gui.x + x, skill.info.gui.y + y, skill.info.gui.texture.width, skill.info.gui.texture.height, skill.info.general.type.textureX * skill.info.gui.texture.width, 0);
                            if (skillWidget.isMouseOver((int) mouseX, (int) mouseY, scale))
                            {
                                selectedSkill = skill;
                                resetSelectedAbility = false;
                                doneSomething = true;
                            }
                        }
                    }
                }

                if (resetSelectedAbility)  selectedSkill = null;
            }
        }

        mouseDown = false;
        dragging = false;

        return doneSomething;
    }

    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_)
    {
        if (p_mouseScrolled_5_ > 0)
        {
            scale *= 2.0f;

            if (scale > 2.0f)
            {
                scale = 2.0f;
            }
            else
            {
                moveX /= 2.0f;
                moveY /= 2.0f;
            }
        }
        else
        {
            scale /= 2.0f;

            if (scale < 0.25f)
            {
                scale = 0.25f;
            }
            else
            {
                moveX *= 2.0f;
                moveY *= 2.0f;
            }
        }

        resize(width, height, scale);

        return true;
    }

    public boolean isDragging()
    {
        return dragging;
    }
}
