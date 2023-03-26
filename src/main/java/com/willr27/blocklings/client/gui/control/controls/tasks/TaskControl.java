package com.willr27.blocklings.client.gui.control.controls.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.TextBlockControl;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui.control.controls.panels.GridPanel;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseClickedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.properties.GridDefinition;
import com.willr27.blocklings.client.gui.screen.screens.TasksScreen;
import com.willr27.blocklings.client.gui.texture.Texture;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.Task;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a task.
 */
@OnlyIn(Dist.CLIENT)
public class TaskControl extends Control
{
    /**
     * The associated task (null if used to add a task).
     */
    @Nonnull
    public final Task task;

    /**
     * @param task the associated task.
     */
    public TaskControl(@Nonnull Task task, @Nonnull TasksScreen tasksScreen)
    {
        super();
        this.task = task;

        setWidthPercentage(1.0);
        setFitHeightToContent(true);
        setDraggableY(true);

        GridPanel gridControl = new GridPanel()
        {
            @Override
            protected void onMouseClicked(@Nonnull MouseClickedEvent e)
            {

            }
        };
        gridControl.setParent(this);
        gridControl.setWidthPercentage(1.0);
        gridControl.setFitHeightToContent(true);
        gridControl.addRowDefinition(GridDefinition.RATIO, 1.0);
        gridControl.addColumnDefinition(GridDefinition.AUTO, 1.0);
        gridControl.addColumnDefinition(GridDefinition.RATIO, 1.0);
        gridControl.addColumnDefinition(GridDefinition.AUTO, 1.0);
        gridControl.setHoverable(false);

        Control iconBackgroundControl = new TexturedControl(Textures.Tasks.TASK_ICON_BACKGROUND_RAISED, Textures.Tasks.TASK_ICON_BACKGROUND_PRESSED);
        gridControl.addChild(iconBackgroundControl, 0, 0);

        Control iconControl = new TexturedControl(task.getType().texture)
        {
            /**
             * The icon texture.
             */
            @Nonnull
            private Texture iconTexture = getBackgroundTexture();

            @Override
            public void onHoverEnter()
            {
                iconTexture = !isDraggingOrAncestor() ? Textures.Tasks.TASK_CONFIG_ICON : iconTexture;
            }

            @Override
            public void onHoverExit()
            {
                iconTexture = task.getType().texture;
            }

            @Override
            public void onDragStart(double mouseX, double mouseY)
            {
                iconTexture = task.getType().texture;
            }

            @Override
            public void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                renderTextureAsBackground(matrixStack, iconTexture);
            }

            @Override
            public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
            {
                renderTooltip(matrixStack, mouseX, mouseY, getPixelScaleX(), getPixelScaleY(), new BlocklingsTranslationTextComponent("task.ui.configure"));
            }

            @Override
            protected void onMouseClicked(@Nonnull MouseClickedEvent e)
            {

            }

            @Override
            protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
            {
                if (!isDraggingOrAncestor())
                {
                    tasksScreen.openConfig(task);
                }
            }
        };
        iconControl.setParent(iconBackgroundControl);

        GridPanel taskNameBackgroundGrid = new GridPanel()
        {
            @Override
            public void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                renderTextureAsBackground(matrixStack, Textures.Tasks.TASK_NAME_BACKGROUND);
            }

            @Override
            protected void onMouseClicked(@Nonnull MouseClickedEvent e)
            {

            }
        };
        gridControl.addChild(taskNameBackgroundGrid, 0, 1);
        taskNameBackgroundGrid.addRowDefinition(GridDefinition.RATIO, 1.0);
        taskNameBackgroundGrid.addColumnDefinition(GridDefinition.FIXED, 4.0);
        taskNameBackgroundGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);
        taskNameBackgroundGrid.addColumnDefinition(GridDefinition.FIXED, 16.0);
        taskNameBackgroundGrid.setWidthPercentage(1.0);
        taskNameBackgroundGrid.setHeight(Textures.Tasks.TASK_NAME_BACKGROUND.height);
        taskNameBackgroundGrid.setHoverable(false);

        Control stateIconControl = new TexturedControl(Textures.Common.NODE_UNPRESSED, Textures.Common.NODE_PRESSED)
        {
            @Override
            public void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                if (task.isConfigured())
                {
                    if (task.getGoal().getState() == BlocklingGoal.State.DISABLED)
                    {
                        RenderSystem.color3f(1.0f, 0.0f, 0.0f);
                    }
                    else if (task.getGoal().getState() == BlocklingGoal.State.IDLE)
                    {
                        RenderSystem.color3f(1.0f, 0.8f, 0.0f);
                    }
                    if (task.getGoal().getState() == BlocklingGoal.State.ACTIVE)
                    {
                        RenderSystem.color3f(0.0f, 0.7f, 0.0f);
                    }
                }
                else
                {
                    RenderSystem.color3f(0.6f, 0.6f, 0.6f);
                }

                if (task.isConfigured() && task.getGoal().getState() == BlocklingGoal.State.DISABLED)
                {
                    renderTextureAsBackground(matrixStack, getPressedBackgroundTexture());
                }
                else
                {
                    renderTextureAsBackground(matrixStack, getBackgroundTexture());
                }

                RenderSystem.color3f(1.0f, 1.0f, 1.0f);
            }

            @Override
            public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
            {
                if (task.isConfigured())
                {
                    if (task.getGoal().getState() == BlocklingGoal.State.DISABLED)
                    {
                        renderTooltip(matrixStack, mouseX, mouseY, new BlocklingsTranslationTextComponent("task.ui.enable"));
                    }
                    else
                    {
                        renderTooltip(matrixStack, mouseX, mouseY, new BlocklingsTranslationTextComponent("task.ui.disable"));
                    }
                }
            }

            @Override
            protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
            {
                if (isPressed() && !isDraggingOrAncestor())
                {
                    if (task.isConfigured())
                    {
                        if (task.getGoal().getState() == BlocklingGoal.State.DISABLED)
                        {
                            task.getGoal().setState(BlocklingGoal.State.IDLE);
                        }
                        else
                        {
                            task.getGoal().setState(BlocklingGoal.State.DISABLED);
                        }
                    }
                }

                e.setIsHandled(true);
            }
        };
        taskNameBackgroundGrid.addChild(stateIconControl, 0, 2);
        stateIconControl.setHorizontalAlignment(0.0);
        stateIconControl.setVerticalAlignment(0.5);

        TextBlockControl taskNameControl = new TextBlockControl()
        {
            @Override
            public void onTick()
            {
                setText(new StringTextComponent(task.getCustomName()));
            }

            @Override
            protected void onMouseClicked(@Nonnull MouseClickedEvent e)
            {

            }
        };
        taskNameBackgroundGrid.addChild(taskNameControl, 0, 1);
        taskNameControl.setWidthPercentage(1.0);
        taskNameControl.setFitHeightToContent(true);
        taskNameControl.setVerticalAlignment(0.5);
        taskNameControl.setInteractive(false);
        taskNameControl.onTick();

        Control removeControl = new TexturedControl(Textures.Tasks.TASK_REMOVE_ICON)
        {
            @Override
            protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                if (isPressed() && !isDragging() && getPressedBackgroundTexture() != null)
                {
                    renderTextureAsBackground(matrixStack, getPressedBackgroundTexture(), 4, 4);
                }
                else
                {
                    renderTextureAsBackground(matrixStack, getBackgroundTexture(), 4, 4);
                }
            }

            @Override
            public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
            {
                renderTooltip(matrixStack, mouseX, mouseY, new BlocklingsTranslationTextComponent("task.ui.remove"));
            }

            @Override
            protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
            {
                if (isPressed() && !isDraggingOrAncestor())
                {
                    getParent().removeChild(this);
                    task.blockling.getTasks().removeTask(task);
                }
            }
        };
        gridControl.addChild(removeControl, 0, 2);
        removeControl.setFitWidthToContent(false);
        removeControl.setFitHeightToContent(false);
        removeControl.setWidth(17);
        removeControl.setHeight(20);
    }

    @Override
    public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
    {
        List<IReorderingProcessor> tooltip = new ArrayList<>();
        tooltip.add(new StringTextComponent(TextFormatting.GOLD + task.getCustomName()).getVisualOrderText());
        tooltip.addAll(GuiUtil.get().split(task.getType().desc, 200).stream().collect(Collectors.toList()));

        renderTooltip(matrixStack, mouseX, mouseY, tooltip);
    }
}
