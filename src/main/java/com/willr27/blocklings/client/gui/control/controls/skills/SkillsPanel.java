package com.willr27.blocklings.client.gui.control.controls.skills;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.ButtonControl;
import com.willr27.blocklings.client.gui.control.controls.TabbedUIControl;
import com.willr27.blocklings.client.gui.control.controls.TextBlockControl;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui.control.controls.panels.CanvasPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.StackPanel;
import com.willr27.blocklings.client.gui.control.event.events.input.KeyPressedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseScrolledEvent;
import com.willr27.blocklings.client.gui.properties.Direction;
import com.willr27.blocklings.client.gui.properties.Position;
import com.willr27.blocklings.client.gui.properties.Visibility;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.ScissorBounds;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.attribute.Attribute;
import com.willr27.blocklings.entity.blockling.attribute.BlocklingAttributes;
import com.willr27.blocklings.entity.blockling.skill.Skill;
import com.willr27.blocklings.entity.blockling.skill.SkillGroup;
import com.willr27.blocklings.entity.blockling.skill.info.SkillGroupInfo;
import com.willr27.blocklings.entity.blockling.skill.info.SkillGuiInfo;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import com.willr27.blocklings.util.DoubleUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static com.willr27.blocklings.entity.blockling.skill.info.SkillGuiInfo.ConnectionType.*;

/**
 * Displays a set of skills in a dynamic window.
 */
@OnlyIn(Dist.CLIENT)
public class SkillsPanel extends CanvasPanel
{
    /**
     * The blockling.
     */
    @Nonnull
    private final BlocklingEntity blockling;

    /**
     * The skill group to show.
     */
    @Nonnull
    private final SkillGroupInfo skillGroupInfo;

    /**
     * The skill group.
     */
    @Nonnull
    private final SkillGroup skillGroup;

    /**
     * The skills canvas.
     */
    @Nonnull
    private final SkillsCanvas skillsCanvas;

    /**
     * The minimised parent.
     */
    @Nullable
    private BaseControl minimisedParent = null;

    /**
     * The tabbed UI control.
     */
    @Nonnull
    private final TabbedUIControl tabbedUIControl;

    /**
     * The maximise button.
     */
    @Nonnull
    private final Control maximiseButton;

    /**
     * The confirmation control.
     */
    @Nonnull
    private final ConfirmationControl confirmationControl = new ConfirmationControl();

    /**
     * @param blockling the blockling.
     * @param skillGroupInfo the skill group.
     * @param tabbedUIControl the tabbed UI control.
     */
    public SkillsPanel(@Nonnull BlocklingEntity blockling, @Nonnull SkillGroupInfo skillGroupInfo, @Nonnull TabbedUIControl tabbedUIControl)
    {
        super();
        this.blockling = blockling;
        this.skillGroupInfo = skillGroupInfo;
        this.skillGroup = blockling.getSkills().getGroup(skillGroupInfo);
        this.tabbedUIControl = tabbedUIControl;

        setWidthPercentage(1.0);
        setHeightPercentage(1.0);
        setShouldBlockDrag(false);

        skillsCanvas = new SkillsCanvas();
        skillsCanvas.setParent(this);
        skillsCanvas.setX((158.0 / 2.0) / getInnerScale().x);
        skillsCanvas.setY((148.0 / 2.0) / getInnerScale().y);

        List<SkillControl> skillControls = new ArrayList<>();

        // Create the skill controls.
        for (Skill skill : skillGroup.getSkills())
        {
            SkillControl skillControl = new SkillControl(skill, confirmationControl);
            skillControl.setParent(skillsCanvas);

            skillControls.add(skillControl);
        }

        // Add all the parent skill controls to each child skill control.
        for (SkillControl skillControl : skillControls)
        {
            skillControl.getParentSkillControls().addAll(skillControls.stream().filter(s -> skillControl.skill.info.parents().contains(s.skill.info)).collect(Collectors.toList()));

            for (SkillControl parentSkillControl : skillControl.getParentSkillControls())
            {
                PathControl pathControl = new PathControl(parentSkillControl, skillControl);
                skillsCanvas.insertChildFirst(pathControl);
            }
        }

        maximiseButton = new TexturedControl(Textures.Skills.MAXIMISE_BUTTON)
        {
            @Override
            public void onHoverEnter()
            {
                setBackgroundTexture(Textures.Skills.MAXIMISE_BUTTON_HOVERED);
            }

            @Override
            public void onHoverExit()
            {
                setBackgroundTexture(Textures.Skills.MAXIMISE_BUTTON);
            }

            @Override
            protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
            {
                if (isPressed())
                {
                    maximise();
                    setVisibility(Visibility.COLLAPSED);

                    e.setIsHandled(true);
                }
            }
        };
        maximiseButton.setParent(this);
        maximiseButton.setX(141);
        maximiseButton.setY(131);
        maximiseButton.setRenderZ(0.4);

        confirmationControl.setParent(this);
    }

    @Override
    public void onKeyPressed(@Nonnull KeyPressedEvent e)
    {
        if (GuiUtil.get().isCloseKey(e.keyCode) && getParent() == getScreen())
        {
            minimise();

            e.setIsHandled(true);
        }
    }

    /**
     * Minimises the panel.
     */
    public void minimise()
    {
        setParent(minimisedParent);
        skillsCanvas.setInnerScale(1.0, 1.0);
        skillsCanvas.setX((158.0 / 2.0) / getInnerScale().x);
        skillsCanvas.setY((148.0 / 2.0) / getInnerScale().y);
        maximiseButton.setVisibility(Visibility.VISIBLE);
        tabbedUIControl.setVisibility(Visibility.VISIBLE);
    }

    /**
     * Maximises the panel.
     */
    public void maximise()
    {
        minimisedParent = getParent();
        setParent(getScreen());
        skillsCanvas.setInnerScale(1.0, 1.0);
        skillsCanvas.setX((getScreen().getWidth() / 2.0) / getInnerScale().x);
        skillsCanvas.setY((getScreen().getHeight() / 2.0) / getInnerScale().y);
        maximiseButton.setVisibility(Visibility.COLLAPSED);
        tabbedUIControl.setVisibility(Visibility.COLLAPSED);
    }

    /**
     * A movable canvas that displays skills.
     */
    private class SkillsCanvas extends CanvasPanel
    {
        /**
         * The starting position of the drag.
         */
        private double prevDragMouseX = 0.0;

        /**
         * The starting position of the drag.
         */
        private double prevDragMouseY = 0.0;

        /**
         * Whether the canvas has been moved by the user.
         */
        private boolean hasMoved = false;

        /**
         * The bonus random value for the background tiles.
         */
        private int bonusRand = 0;

        /**
         */
        public SkillsCanvas()
        {
            super();

            bonusRand = new Random().nextInt(1000);

            setFitWidthToContent(true);
            setFitHeightToContent(true);
            setDraggableX(true);
            setDraggableY(true);
            setClipContentsToBounds(true);
            setDragZ(null);
            setShouldSnapToPixelCoords(true);
            setBackgroundColour(0xffff0000);
        }

        @Override
        protected void arrange()
        {
            super.arrange();

            clampScale();
            clampToEdges();
        }

        @Override
        public void forwardRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
        {
            if (getVisibility() != Visibility.VISIBLE)
            {
                return;
            }

            RenderSystem.color3f(1.0f, 1.0f, 1.0f);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.enableDepthTest();

            matrixStack.pushPose();
            matrixStack.translate(0.0f, 0.0f, isDraggingOrAncestor() ? getDraggedControl().getDragZ() : getRenderZ());

            applyScissor(scissorStack);
            onRenderUpdate(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
            onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

            matrixStack.popPose();

            List<PathControl> pathControls = getChildrenCopy().stream().filter(c -> c instanceof PathControl).map(c -> (PathControl)c).collect(Collectors.toList());

            for (PathControl child : pathControls)
            {
                child.shouldRenderHighlight = false;
                child.forwardRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
            }

            for (PathControl child : pathControls)
            {
                child.shouldRenderHighlight = true;
                child.forwardRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
            }

            for (BaseControl child : getChildrenCopy().stream().filter(c -> !(c instanceof PathControl)).collect(Collectors.toList()))
            {
                child.forwardRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
            }

            undoScissor(scissorStack);
        }

        @Override
        protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
        {
            renderRectangle(matrixStack, getMinPixelX(), getMinPixelY(), (int) (getMaxPixelX() - getMinPixelX()), (int) (getMaxPixelY() - getMinPixelY()), getBackgroundColourInt());
            renderRectangle(matrixStack, toPixelX(-5.0), toPixelY(-5.0), (int) 10, (int) 10, 0xff00ff00);

            double pixelTileWidth = 16.0 * getChildPixelScaleX();
            double pixelTileHeight = 16.0 * getChildPixelScaleY();
            double pixelDifX = getPixelX() - getParent().getPixelX();
            double pixelDifY = getPixelY() - getParent().getPixelY();
            double tileOffsetX = ((pixelDifX % (pixelTileWidth)) + (pixelTileWidth)) % (pixelTileWidth);
            double tileOffsetY = ((pixelDifY % (pixelTileHeight)) + (pixelTileHeight)) % (pixelTileHeight);
            double renderStartLocationX = getPixelX() - pixelDifX + tileOffsetX;
            double renderStartLocationY = getPixelY() - pixelDifY + tileOffsetY;
            int tilesInX = (int) Math.ceil(getParent().getPixelWidth() / (pixelTileWidth));
            int tilesInY = (int) Math.ceil(getParent().getPixelHeight() / (pixelTileHeight));

            for (int i = -1; i < tilesInX; i++)
            {
                for (int j = -1; j < tilesInY; j++)
                {
                    double renderLocationX = renderStartLocationX + (i * pixelTileWidth);
                    double renderLocationY = renderStartLocationY + (j * pixelTileHeight);
                    int x = (int) -Math.floor(pixelDifX / pixelTileWidth) + i;
                    int y = (int) -Math.floor(pixelDifY / pixelTileHeight) + j;
                    Random random = new Random(new Random(x).nextInt() * new Random(y).nextInt() + bonusRand);

                    renderTexture(matrixStack, skillGroup.info.backgroundTexture.randomTile(random), renderLocationX, renderLocationY, getChildPixelScaleX(), getChildPixelScaleY());
                }
            }
        }

        @Override
        protected void applyScissor(@Nonnull ScissorStack scissorStack)
        {
            if (shouldClipContentsToBounds())
            {
                scissorStack.push(new ScissorBounds((int) Math.round(getMinPixelX()), (int) Math.round(getMinPixelY()), (int) Math.round(getMaxPixelX() - getMinPixelX()), (int) Math.round(getMaxPixelY() - getMinPixelY())));
                scissorStack.enable();
            }
            else
            {
                scissorStack.disable();
            }
        }

        @Override
        public void onDragStart(double mouseX, double mouseY)
        {
            prevDragMouseX = mouseX;
            prevDragMouseY = mouseY;
        }

        @Override
        public void onDrag(double mouseX, double mouseY, float partialTicks)
        {
            double mouseDifX = mouseX - prevDragMouseX;
            double mouseDifY = mouseY - prevDragMouseY;

            setPixelX(Math.round(getPixelX() + mouseDifX));
            setPixelY(Math.round(getPixelY() + mouseDifY));
            clampToEdges();

            prevDragMouseX = mouseX;
            prevDragMouseY = mouseY;

            setHasMoved(true);
        }

        @Override
        public void onPressStart()
        {
            setBackgroundColour(0xffffff00);
        }

        @Override
        public void onPressEnd()
        {
            setBackgroundColour(0xffff0000);
        }

        @Override
        public void onMouseScrolled(@Nonnull MouseScrolledEvent e)
        {
            double localMouseXBefore = toLocalX(e.mouseX);
            double localMouseYBefore = toLocalY(e.mouseY);

            boolean dec = e.amount < 0;
            double scaleX = (getChildPixelScaleX() + (dec ? -1.0 : 1.0)) / getPixelScaleX();
            double scaleY = (getChildPixelScaleY() + (dec ? -1.0 : 1.0)) / getPixelScaleY();

            setInnerScale(scaleX, scaleY);
            clampScale();

            double localMouseXAfter = toLocalX(e.mouseX);
            double localMouseYAfter = toLocalY(e.mouseY);

            setX(getX() - (localMouseXBefore - localMouseXAfter) * getInnerScale().x);
            setY(getY() - (localMouseYBefore - localMouseYAfter) * getInnerScale().y);

            arrange();
            clampToEdges();
        }

        @Override
        public void setMinX(double minX)
        {
            super.setMinX(minX - getParent().getPixelWidth() / getChildPixelScaleX());
        }

        @Override
        public void setMinY(double minY)
        {
            super.setMinY(minY - getParent().getPixelHeight() / getChildPixelScaleY());
        }

        @Override
        public void setMaxX(double maxX)
        {
            super.setMaxX(maxX + getParent().getPixelWidth() / getChildPixelScaleX());
        }

        @Override
        public void setMaxY(double maxY)
        {
            super.setMaxY(maxY + getParent().getPixelHeight() / getChildPixelScaleY());
        }

        @Override
        public boolean contains(double pixelX, double pixelY)
        {
            return pixelX >= getMinPixelX() && pixelX <= getMaxPixelX() && pixelY >= getMinPixelY() && pixelY <= getMaxPixelY();
        }

        /**
         * Clamps the canvas to the edges of the parent.
         */
        private void clampToEdges()
        {
            double newPixelX = DoubleUtil.clamp(getPixelX(), getParent().getPixelX() + getParent().getPixelWidth() - (getMaxPixelX() - toPixelX(0.0)), getParent().getPixelX() + (toPixelX(0.0) - getMinPixelX()));
            double newPixelY = DoubleUtil.clamp(getPixelY(), getParent().getPixelY() + getParent().getPixelHeight() - (getMaxPixelY() - toPixelY(0.0)), getParent().getPixelY() + (toPixelY(0.0) - getMinPixelY()));

            setPixelX(newPixelX);
            setPixelY(newPixelY);
        }

        /**
         * Clamps the scale of the canvas to the maximum gui scale.
         */
        private void clampScale()
        {
            double scaleX = DoubleUtil.clamp(Math.round(getChildPixelScaleX()), 1.0, GuiUtil.get().getMaxGuiScale() + 2.0) / getPixelScaleX();
            double scaleY = DoubleUtil.clamp(Math.round(getChildPixelScaleY()), 1.0, GuiUtil.get().getMaxGuiScale() + 2.0) / getPixelScaleY();

            setInnerScale(scaleX, scaleY);
        }

        /**
         * @return whether the canvas has been moved by the user.
         */
        public boolean isHasMoved()
        {
            return hasMoved;
        }

        /**
         * Sets whether the canvas has been moved by the user.
         *
         * @param hasMoved whether the canvas has been moved by the user.
         */
        public void setHasMoved(boolean hasMoved)
        {
            this.hasMoved = hasMoved;
        }
    }

    /**
     * Displays an individual skill.
     */
    private class SkillControl extends Control
    {
        /**
         * The skill to display.
         */
        @Nonnull
        private final Skill skill;

        /**
         * The list of parent skill controls.
         */
        @Nonnull
        private final List<SkillControl> parentSkillControls = new ArrayList<>();

        /**
         * Whether the skill is selected.
         */
        private boolean isSelected = false;

        /**
         * The confirmation control.
         */
        @Nonnull
        private final ConfirmationControl confirmationControl;

        /**
         * The info panel.
         */
        @Nonnull
        private final StackPanel info;

        /**
         * @param skill the skill to display.
         */
        public SkillControl(@Nonnull Skill skill, @Nonnull ConfirmationControl confirmationControl)
        {
            super();
            this.skill = skill;
            this.confirmationControl = confirmationControl;

            skill.onStateChanged.subscribe((e) ->
            {
                recreateInfo();
            });

            setWidth(skill.info.general.type.texture.width);
            setHeight(skill.info.general.type.texture.height);
            setX(skill.info.gui.x - getWidth() / 2.0);
            setY(skill.info.gui.y - getHeight() / 2.0);

            info = new StackPanel()
            {
                @Override
                protected void applyScissor(@Nonnull ScissorStack scissorStack)
                {
                    scissorStack.disable();
                }

                @Override
                protected void undoScissor(@Nonnull ScissorStack scissorStack)
                {

                }
            };
            info.setParent(this);
            info.setMinWidth(130.0);
            info.setFitWidthToContent(true);
            info.setFitHeightToContent(true);
            info.setInteractive(false);
            info.setPadding(4.0, 2.0, 0.0, 0.0);
            info.setVisibility(Visibility.COLLAPSED);

            recreateInfo();

            TexturedControl type = new TexturedControl(skill.info.general.type.texture)
            {
                @Override
                protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
                {
                    matrixStack.translate(0.0, 0.0, 0.2);

                    Skill.State state = skill.getState();
                    Color colour = state.colour;

                    if (isSelected)
                    {
                        RenderSystem.color3f(0.8f, 1.0f, 0.8f);
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

                    super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
                }
            };
            type.setParent(this);
            type.setInteractive(false);
            type.setClipContentsToBounds(null);

            TexturedControl icon = new TexturedControl(skill.info.gui.iconTexture)
            {
                @Override
                protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
                {
                    matrixStack.translate(0.0, 0.0, 0.2);

                    Skill.State state = skill.getState();

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

                    super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
                }
            };
            icon.setParent(this);
            icon.setHorizontalAlignment(0.5);
            icon.setVerticalAlignment(0.5);
            icon.setInteractive(false);
            icon.setClipContentsToBounds(null);
        }

        /**
         * Recreates the info panel.
         */
        private void recreateInfo()
        {
            info.clearChildren();

            Control nameBackground = new Control()
            {
                @Override
                protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
                {
                    super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

                    if (skill.areParentsBought())
                    {
                        RenderSystem.color3f(skill.info.gui.colour.getRed() / 255.0f, skill.info.gui.colour.getGreen() / 255.0f, skill.info.gui.colour.getBlue() / 255.0f);
                    }
                    else
                    {
                        RenderSystem.color3f(0.5f, 0.5f, 0.5f);
                    }

                    renderTextureAsBackground(matrixStack, Textures.Skills.SKILL_NAME_BACKGROUND.width((int) (getWidth() - 2)), 0, getPadding().top);
                    renderTextureAsBackground(matrixStack, Textures.Skills.SKILL_NAME_BACKGROUND.width(2).dx(Textures.Skills.SKILL_NAME_BACKGROUND.width - 2), getWidth() - 2, getPadding().top);
                }
            };
            nameBackground.setParent(info);
            nameBackground.setWidthPercentage(1.0);
            nameBackground.setHeight(Textures.Skills.SKILL_NAME_BACKGROUND.height - 1);
            nameBackground.setPaddingLeft(24.0);
            nameBackground.setPaddingRight(5.0);
            nameBackground.setRenderZ(0.2);
            nameBackground.setClipContentsToBounds(false);

            TextBlockControl nameText = new TextBlockControl()
            {
                @Override
                protected void onRenderUpdate(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
                {
                    if (skill.areParentsBought())
                    {
                        setText(skill.info.general.name);
                    }
                    else
                    {
                        setText(new BlocklingsTranslationTextComponent("skill.unknown"));
                    }
                }
            };
            nameText.setParent(nameBackground);
            nameText.setText(skill.info.general.name);
            nameText.setClipContentsToBounds(false);
            nameText.setVerticalAlignment(0.55);
            nameText.setFitWidthToContent(true);

            Control emptyLine = new Control()
            {
                @Override
                protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
                {
                    super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

                    renderTextureAsBackground(matrixStack, Textures.Skills.SKILL_DESC_BACKGROUND.width((int) (getParent().getWidthWithoutPadding() - 2)).height((int) getHeight() + 1).dy(2), 0, getPadding().top);
                    renderTextureAsBackground(matrixStack, Textures.Skills.SKILL_DESC_BACKGROUND.width(2).height((int) getHeight() + 1).dx(Textures.Skills.SKILL_DESC_BACKGROUND.width - 2).dy(2), getParent().getWidthWithoutPadding() - 2, getPadding().top);
                }
            };
            emptyLine.setParent(info);
            emptyLine.setWidthPercentage(1.0);
            emptyLine.setHeight(2.0);
            emptyLine.setClipContentsToBounds(false);

            for (String string : getSkillDescription())
            {
                Control line = new Control()
                {
                    @Override
                    protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
                    {
                        super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

                        renderTextureAsBackground(matrixStack, Textures.Skills.SKILL_DESC_BACKGROUND.width((int) (getParent().getWidthWithoutPadding() - 2)).height((int) getHeight()).dy(2), 0, getPadding().top);
                        renderTextureAsBackground(matrixStack, Textures.Skills.SKILL_DESC_BACKGROUND.width(2).height((int) getHeight()).dx(Textures.Skills.SKILL_DESC_BACKGROUND.width - 2).dy(2), getParent().getWidthWithoutPadding() - 2, getPadding().top);
                    }
                };
                line.setParent(info);
                line.setFitWidthToContent(true);
                line.setFitHeightToContent(true);
                line.setClipContentsToBounds(false);
                line.setPadding(5.0, 1.0, 5.0, 0.0);

                TextBlockControl text = new TextBlockControl();
                text.setParent(line);
                text.setText(string);
                text.setFitWidthToContent(true);
                text.setClipContentsToBounds(false);
            }

            Control endLine = new Control()
            {
                @Override
                protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
                {
                    super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

                    renderTextureAsBackground(matrixStack, Textures.Skills.SKILL_DESC_BACKGROUND.width((int) (getParent().getWidthWithoutPadding() - 2)).height((int) getHeight()).dy((int) (Textures.Skills.SKILL_DESC_BACKGROUND.height - getHeight())), 0, getPadding().top);
                    renderTextureAsBackground(matrixStack, Textures.Skills.SKILL_DESC_BACKGROUND.width(2).height((int) getHeight()).dx(Textures.Skills.SKILL_DESC_BACKGROUND.width - 2).dy((int) (Textures.Skills.SKILL_DESC_BACKGROUND.height - getHeight())), getParent().getWidthWithoutPadding() - 2, getPadding().top);
                }
            };
            endLine.setParent(info);
            endLine.setWidthPercentage(1.0);
            endLine.setHeight(3.0);
            endLine.setClipContentsToBounds(false);
        }

        /**
         * @return the skill description.
         */
        @Nonnull
        private List<String> getSkillDescription()
        {
            Skill.State state = skill.getState();
            String name = skill.info.general.name.getString();
            List<String> description = GuiUtil.get().split(skill.info.general.desc.getString(), 150);

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

            return description;
        }

        @Override
        public void onHoverEnter()
        {
            setClipContentsToBounds(false);
            setRenderZ(1.0);
            info.setVisibility(Visibility.VISIBLE);
        }

        @Override
        public void onHoverExit()
        {
            setClipContentsToBounds(true);
            setRenderZ(0.0);
            info.setVisibility(Visibility.COLLAPSED);
        }

        @Override
        public void onUnfocused()
        {
            isSelected = false;
        }

        @Override
        protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
        {
            if (isPressed() && skill.getState() == Skill.State.UNLOCKED && skill.canBuy())
            {
                if (isSelected)
                {
                    confirmationControl.setSkill(skill);
                    confirmationControl.setVisibility(Visibility.VISIBLE);

                    isSelected = false;
                }
                else
                {
                    isSelected = true;
                }

                e.setIsHandled(true);
            }
        }

        /**
         * @return the parent skill controls.
         */
        @Nonnull
        public List<SkillControl> getParentSkillControls()
        {
            return parentSkillControls;
        }
    }

    /**
     * Displays a path between two skills.
     */
    private class PathControl extends Control
    {
        /**
         * The start skill control.
         */
        @Nonnull
        private final SkillsPanel.SkillControl startSkillControl;

        /**
         * The end skill control.
         */
        @Nonnull
        private final SkillsPanel.SkillControl endSkillControl;

        /**
         * The connection type.
         */
        @Nonnull
        private final SkillGuiInfo.ConnectionType connectionType;

        /**
         * The path.
         */
        @Nonnull
        private Path path = new Path();

        /**
         * The size of the outer line.
         */
        private double outerLineSize = 4.0;

        /**
         * The size of the inner line.
         */
        private double innerLineSize = 2.0;

        /**
         * The speed the highlight moves in screen pixels per second.
         */
        private final double highlightSpeed = 1.5;

        /**
         * The distance along the path the highlight is currently at.
         */
        private double highlightDistanceAlongPath = -1.0;

        /**
         * Whether the highlight should be rendered.
         */
        private boolean shouldRenderHighlight = false;

        /**
         * @param startSkillControl the start skill control.
         * @param endSkillControl the end skill control.
         */
        public PathControl(@Nonnull SkillsPanel.SkillControl startSkillControl, @Nonnull SkillsPanel.SkillControl endSkillControl)
        {
            super();
            this.startSkillControl = startSkillControl;
            this.endSkillControl = endSkillControl;
            this.connectionType = endSkillControl.skill.info.gui.connectionType;

            setInteractive(false);
        }

        @Override
        public void onTick()
        {
            if (highlightDistanceAlongPath == -1.0 && random.nextInt(100) == 0)
            {
                highlightDistanceAlongPath = 0.0;
            }
        }

        @Override
        protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
        {
            if (shouldRenderHighlight)
            {
                renderHighlight(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

                return;
            }

            super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);

            path = new Path();

            if (connectionType == SINGLE_SHORTEST_FIRST || connectionType == SINGLE_LONGEST_FIRST)
            {
                double startX = startSkillControl.getPixelMidX();
                double startY = startSkillControl.getPixelMidY();
                double endX = endSkillControl.getPixelMidX();
                double endY = endSkillControl.getPixelMidY();

                double xDiff = endX - startX;
                double yDiff = endY - startY;

                if (Math.abs(xDiff) >= Math.abs(yDiff) && connectionType == SINGLE_LONGEST_FIRST)
                {
                    path.add(new Position(startX, startY));
                    path.add(new Position(endX, startY));
                    path.add(new Position(endX, endY));
                }
                else
                {
                    path.add(new Position(startX, startY));
                    path.add(new Position(startX, endY));
                    path.add(new Position(endX, endY));
                }
            }
            else
            {
                double startX = startSkillControl.getPixelMidX();
                double startY = startSkillControl.getPixelMidY();
                double endX = endSkillControl.getPixelMidX();
                double endY = endSkillControl.getPixelMidY();

                double xDiff = endX - startX;
                double yDiff = endY - startY;

                if (Math.abs(xDiff) >= Math.abs(yDiff) && connectionType == DOUBLE_LONGEST_SPLIT)
                {
                    path.add(new Position(startX, startY));
                    path.add(new Position(endX - xDiff / 2.0, startY));
                    path.add(new Position(endX - xDiff / 2.0, endY - yDiff / 2.0));
                    path.add(new Position(endX, endY - yDiff / 2.0));
                    path.add(new Position(endX, endY));
                }
                else
                {
                    path.add(new Position(startX, startY));
                    path.add(new Position(startX, endY - yDiff / 2.0));
                    path.add(new Position(endX - xDiff / 2.0, endY - yDiff / 2.0));
                    path.add(new Position(endX - xDiff / 2.0, endY));
                    path.add(new Position(endX, endY));
                }
            }

            double outerLineWidth = outerLineSize * getPixelScaleX();
            double outerLineHeight = outerLineSize * getPixelScaleY();
            double innerLineWidth = innerLineSize * getPixelScaleX();
            double innerLineHeight = innerLineSize * getPixelScaleY();

            if (path.size() > 1)
            {
                for (int i = 0; i < path.size() - 1; i++)
                {
                    Position start = path.get(i);
                    Position end = path.get(i + 1);

                    double outerStartX = Math.min(start.x, end.x) - outerLineWidth / 2.0;
                    double outerStartY = Math.min(start.y, end.y) - outerLineHeight / 2.0;
                    double outerEndX = Math.max(end.x, start.x) + outerLineWidth / 2.0;
                    double outerEndY = Math.max(end.y, start.y) + outerLineHeight / 2.0;
                    double outerWidth = outerStartX == outerEndX ? outerLineWidth : outerEndX - outerStartX;
                    double outerHeight = outerStartY == outerEndY ? outerLineHeight : outerEndY - outerStartY;

                    double innerStartX = Math.min(start.x, end.x) - innerLineWidth / 2.0;
                    double innerStartY = Math.min(start.y, end.y) - innerLineHeight / 2.0;
                    double innerEndX = Math.max(end.x, start.x) + innerLineWidth / 2.0;
                    double innerEndY = Math.max(end.y, start.y) + innerLineHeight / 2.0;
                    double innerWidth = innerStartX == innerEndX ? innerLineWidth : innerEndX - innerStartX;
                    double innerHeight = innerStartY == innerEndY ? innerLineHeight : innerEndY - innerStartY;

                    Skill.State state = endSkillControl.skill.getState();
                    int colour = 0xffffffff;

                    if (state == Skill.State.LOCKED)
                    {
                        colour = 0xff444444;
                    }

                    renderRectangle(matrixStack, outerStartX, outerStartY, (int) outerWidth, (int) outerHeight, 0xff000000);
                    matrixStack.pushPose();
                    matrixStack.translate(0.0, 0.0, 0.1);
                    renderRectangle(matrixStack, innerStartX, innerStartY, (int) innerWidth, (int) innerHeight, colour);
                    matrixStack.popPose();
                }
            }
        }

        /**
         * Renders the highlight along the path.
         *
         * @param matrixStack the matrix stack.
         * @param scissorStack the scissor stack.
         * @param mouseX the mouse x position.
         * @param mouseY the mouse y position.
         * @param partialTicks
         */
        private void renderHighlight(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
        {
            double outerLineWidth = outerLineSize * getPixelScaleX();
            double outerLineHeight = outerLineSize * getPixelScaleY();
            double innerLineWidth = innerLineSize * getPixelScaleX();
            double innerLineHeight = innerLineSize * getPixelScaleY();

            Position highlightPosition = path.atPercent((highlightDistanceAlongPath * getPixelScaleX()) / path.length());
            double highlightX = highlightPosition.x - outerLineWidth / 2.0;
            double highlightY = highlightPosition.y - outerLineHeight / 2.0;

            Skill.State state = endSkillControl.skill.getState();
            int colour = 0xeeeeee;

            if (state == Skill.State.BOUGHT)
            {
                colour = 0xffee99; // TODO: Do this properly.
            }
            else if (state == Skill.State.LOCKED)
            {
                colour = 0x555555;
            }

            matrixStack.pushPose();
            matrixStack.translate(0.0, 0.0, 0.2);
            renderRectangle(matrixStack, highlightX, highlightY, (int) outerLineWidth, (int) outerLineHeight,0x30000000 + colour);
            renderRectangle(matrixStack, highlightX, highlightY + innerLineHeight / 2.0, (int) outerLineWidth, (int) (outerLineHeight - innerLineHeight),0x20000000 + colour);
            renderRectangle(matrixStack, highlightX + innerLineWidth / 2.0, highlightY, (int) (outerLineWidth - innerLineWidth), (int) outerLineHeight,0x20000000 + colour);
            matrixStack.popPose();

            if (highlightDistanceAlongPath != -1.0)
            {
                highlightDistanceAlongPath += highlightSpeed * partialTicks;
            }

            if ((highlightDistanceAlongPath * getPixelScaleX()) > path.length())
            {
                highlightDistanceAlongPath = -1.0;
            }
        }

        @Override
        protected void applyScissor(@Nonnull ScissorStack scissorStack)
        {
            if (shouldClipContentsToBounds())
            {
                scissorStack.enable();
            }
            else
            {
                scissorStack.disable();
            }
        }

        @Override
        protected void undoScissor(@Nonnull ScissorStack scissorStack)
        {
            if (shouldClipContentsToBounds())
            {
                scissorStack.disable();
            }
        }
    }

    /**
     * A control for the confirmation screen.
     */
    private class ConfirmationControl extends Control
    {
        /**
         * The skill to buy.
         */
        @Nullable
        private Skill skill;

        /**
         * The message container.
         */
        private final StackPanel messageContainer;

        /**
         */
        public ConfirmationControl()
        {
            super();

            setWidthPercentage(1.0);
            setHeightPercentage(1.0);
            setBackgroundColour(0xaa000000);
            setRenderZ(0.4);

            StackPanel container = new StackPanel();
            container.setParent(this);
            container.setHorizontalAlignment(0.5);
            container.setHorizontalContentAlignment(0.5);
            container.setVerticalAlignment(0.5);
            container.setFitWidthToContent(true);
            container.setFitHeightToContent(true);
            container.setDirection(Direction.TOP_TO_BOTTOM);
            container.setSpacing(20.0);

            messageContainer = new StackPanel();
            messageContainer.setParent(container);
            messageContainer.setDirection(Direction.TOP_TO_BOTTOM);
            messageContainer.setFitWidthToContent(true);
            messageContainer.setFitHeightToContent(true);
            messageContainer.setSpacing(1.0);

            StackPanel buttonContainer = new StackPanel();
            buttonContainer.setParent(container);
            buttonContainer.setDirection(Direction.LEFT_TO_RIGHT);
            buttonContainer.setHorizontalContentAlignment(0.5);
            buttonContainer.setSpacing(10.0);
            buttonContainer.setFitWidthToContent(true);
            buttonContainer.setFitHeightToContent(true);

            ButtonControl yesButton = new ButtonControl()
            {
                @Override
                public void onMouseReleased(@Nonnull MouseReleasedEvent e)
                {
                    if (isPressed())
                    {
                        skill.tryBuy();
                        ConfirmationControl.this.setVisibility(Visibility.COLLAPSED);

                        e.setIsHandled(true);
                    }
                }
            };
            yesButton.setParent(buttonContainer);
            yesButton.setWidth(50);
            yesButton.setHeight(20);
            yesButton.setBackgroundColour(randomColour());
            yesButton.textBlock.setText(new BlocklingsTranslationTextComponent("skill.buy.yes"));

            ButtonControl noButton = new ButtonControl()
            {
                @Override
                public void onMouseReleased(@Nonnull MouseReleasedEvent e)
                {
                    if (isPressed())
                    {
                        ConfirmationControl.this.setVisibility(Visibility.COLLAPSED);

                        e.setIsHandled(true);
                    }
                }
            };
            noButton.setParent(buttonContainer);
            noButton.setWidth(50);
            noButton.setHeight(20);
            noButton.setBackgroundColour(randomColour());
            noButton.textBlock.setText(new BlocklingsTranslationTextComponent("skill.buy.no"));

            setVisibility(Visibility.COLLAPSED);
        }

        @Override
        public void onKeyPressed(@Nonnull KeyPressedEvent e)
        {
            if (GuiUtil.get().isCloseKey(e.keyCode) && getVisibility() == Visibility.VISIBLE)
            {
                setVisibility(Visibility.COLLAPSED);

                e.setIsHandled(true);
            }
        }

        @Override
        public void setVisibility(@Nonnull Visibility visibility)
        {
            super.setVisibility(visibility);

            if (visibility == Visibility.VISIBLE)
            {
                setFocused(true);
            }
        }

        @Override
        public void setWidth(double width)
        {
            super.setWidth(width);

            if (skill != null)
            {
                messageContainer.clearChildren();

                for (String line : GuiUtil.get().split(new BlocklingsTranslationTextComponent("skill.buy_confirmation", TextFormatting.LIGHT_PURPLE + skill.info.general.name.getString() + TextFormatting.WHITE).getString(), (int) getWidth() - 20))
                {
                    TextBlockControl textBlock = new TextBlockControl();
                    textBlock.setParent(messageContainer);
                    textBlock.setText(line);
                    textBlock.setHorizontalAlignment(0.5);
                    textBlock.setFitWidthToContent(true);
                }
            }
        }

        /**
         * @return the skill to buy.
         */
        @Nullable
        public Skill getSkill()
        {
            return skill;
        }

        /**
         * Sets the skill to buy.
         */
        public void setSkill(@Nullable Skill skill)
        {
            this.skill = skill;
        }
    }

    /**
     * A path between two skills.
     */
    private class Path extends ArrayList<Position>
    {
        /**
         * @return the position at the given percent along the path.
         */
        @Nonnull
        private Position atPercent(double percent)
        {
            if (size() < 2)
            {
                return new Position(0.0, 0.0);
            }

            double percentLength = length() * percent;

            for (int i = 0; i < size() - 1; i++)
            {
                double edgeLength = length(i);

                if (percentLength <= edgeLength)
                {
                    Position start = get(i);
                    Position end = get(i + 1);

                    double percentAlongEdge = percentLength / edgeLength;

                    return new Position(start.x + (end.x - start.x) * percentAlongEdge, start.y + (end.y - start.y) * percentAlongEdge);
                }

                percentLength -= edgeLength;
            }

            return get(0);
        }

        /**
         * @param i the index of the edge to get the length of.
         * @return the length of the edge at the given index.
         */
        private double length(int i)
        {
            if (i < 0 || i >= size() - 1)
            {
                return 0.0;
            }

            Position start = get(i);
            Position end = get(i + 1);

            if (start.x == end.x)
            {
                return Math.abs(start.y - end.y);
            }
            else if (start.y == end.y)
            {
                return Math.abs(start.x - end.x);
            }

            return 0.0;
        }

        /**
         * @return the length of the path.
         */
        private double length()
        {
            if (size() < 2)
            {
                return 0.0;
            }

            double length = 0.0;

            for (int i = 0; i < size() - 1; i++)
            {
                length += length(i);
            }

            return length;
        }
    }
}
