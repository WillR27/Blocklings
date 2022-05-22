package com.willr27.blocklings.gui.controls.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.gui.*;
import com.willr27.blocklings.gui.controls.TexturedControl;
import com.willr27.blocklings.task.Task;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A control to display a task in the task list.
 */
@OnlyIn(Dist.CLIENT)
public class TaskControl extends Control
{
    /**
     * The width of the control.
     */
    public static final int WIDTH = 136;

    /**
     * The height of the control.
     */
    public static final int HEIGHT = 20;

    /**
     * The task to display.
     */
    @Nonnull
    public final Task task;

    /**
     * Whether the control is add or remove.
     */
    public final boolean isAddControl;

    /**
     * The task name control.
     */
    @Nonnull
    private final TexturedControl nameControl;

    /**
     * The task icon control.
     */
    @Nonnull
    private final TaskIconControl iconControl;

    /**
     * The task state control.
     */
    @Nonnull
    private final TaskStateControl stateControl;

    /**
     * The task add/remove control.
     */
    @Nonnull
    private final TaskAddRemoveControl addRemoveControl;

    /**
     * @param parent the parent control.
     * @param task the task to display.
     * @param isAddControl whether the control is for adding or removing.
     * @param onConfigure the callback called when the configure button is pressed.
     */
    public TaskControl(@Nonnull IControl parent, @Nonnull Task task, boolean isAddControl, @Nonnull Consumer<Task> onConfigure)
    {
        super(parent, 0, 0, WIDTH, HEIGHT);
        this.task = task;
        this.isAddControl = isAddControl;

        setMargins(4, 4, 4, isAddControl ? 4 : 0);

        nameControl = new TexturedControl(this, getX() + 20, getY(), new GuiTexture(GuiTextures.TASKS, 40, 166, 96, HEIGHT))
        {
            @Override
            public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
            {
                super.render(matrixStack, mouseX, mouseY, partialTicks);

                renderShadowedText(matrixStack, GuiUtil.trimWithEllipses(font, task.getCustomName(), width - 24), 4, 6, false, 0xffffff);
            }

            @Override
            public void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
            {
                parent.renderTooltip(matrixStack, mouseX, mouseY);
            }

            @Override
            public void controlMouseClicked(@Nonnull MouseButtonEvent e)
            {
                e.setIsHandled(false);
            }
        };
        iconControl = new TaskIconControl(this, isAddControl, onConfigure);
        stateControl = new TaskStateControl(this);
        addRemoveControl = new TaskAddRemoveControl(this, isAddControl);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (isDragging())
        {
            matrixStack.translate(0.0, 0.0, 10.0);
        }
    }

    @Override
    public void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (!isAddControl)
        {
            List<IReorderingProcessor> text = new ArrayList<>();
            text.add(new StringTextComponent(TextFormatting.GOLD + task.getCustomName()).getVisualOrderText());
            text.add(new StringTextComponent("").getVisualOrderText());
            text.addAll(GuiUtil.splitText(font, task.getType().desc.getString(), 150).stream().map(s -> new StringTextComponent(s).getVisualOrderText()).collect(Collectors.toList()));

            screen.renderTooltip(matrixStack, text, mouseX, mouseY);
        }
    }
}
