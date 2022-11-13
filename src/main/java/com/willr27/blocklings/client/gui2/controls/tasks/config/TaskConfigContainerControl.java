package com.willr27.blocklings.client.gui2.controls.tasks.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui2.Control;
import com.willr27.blocklings.client.gui2.GuiTextures;
import com.willr27.blocklings.client.gui2.GuiUtil;
import com.willr27.blocklings.client.gui2.IControl;
import com.willr27.blocklings.client.gui2.controls.TabbedControl;
import com.willr27.blocklings.client.gui2.controls.common.ScrollbarControl;
import com.willr27.blocklings.client.gui2.controls.common.TextFieldControl;
import com.willr27.blocklings.client.gui2.controls.tasks.config.configs.ItemsConfigControl;
import com.willr27.blocklings.client.gui2.controls.tasks.config.configs.TaskMiscConfigControl;
import com.willr27.blocklings.client.gui2.controls.tasks.config.configs.WhitelistConfigControl;
import com.willr27.blocklings.client.gui2.screens.TasksScreen;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.Task;
import com.willr27.blocklings.entity.blockling.whitelist.GoalWhitelist;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

/**
 * A control to display the config options for a task.
 */
@OnlyIn(Dist.CLIENT)
public class TaskConfigContainerControl extends Control
{
    /**
     * The task being configured.
     */
    @Nonnull
    public final Task task;

    /**
     * The control representing the area where the config content is displayed.
     */
    @Nonnull
    private final Control contentControl;

    /**
     * The scrollbar used for some content.
     */
    @Nonnull
    private final ScrollbarControl contentScrollbarControl;

    /**
     * The control used to display the content tabs.
     */
    private TabControl tabControl;

    /**
     * The current config gui being displayed.
     */
    private ConfigControl currentConfigGui;

    /**
     * The task name text field control.
     */
    @Nonnull
    public TextFieldControl nameTextFieldControl;

    /**
     * @param parent the parent control.
     * @param task the task being configured.
     * @param x the x position.
     * @param y the y position.
     */
    public TaskConfigContainerControl(@Nonnull IControl parent, @Nonnull Task task, int x, int y)
    {
        super(parent, x, y, 176, 166);
        this.task = task;

        contentControl = new Control(this, 9, 43, 140, 115);

        contentScrollbarControl = new ScrollbarControl(this, 155, 33, 12, 125);

        IControl screen = (IControl) getScreen();
        float scale = screen.getScale();
        nameTextFieldControl = new TextFieldControl(font, (int) ((screenX / scale) + 15), (int) ((screenY / scale) + 14), 158, 13, new StringTextComponent(""))
        {
            private String startingString = "";

            @Override
            public void setFocus(boolean focus)
            {
                if (!focus && !startingString.equals(getValue()))
                {
                    task.setCustomName(getValue());
                }
                else
                {
                    startingString = getValue();
                }

                super.setFocus(focus);
            }
        };
        nameTextFieldControl.setBordered(false);
        nameTextFieldControl.setMaxLength(25);
        nameTextFieldControl.setVisible(true);
        nameTextFieldControl.setTextColor(16777215);
        nameTextFieldControl.setValue(task.getCustomName());

        recreateTabs();
    }

    /**
     * Recreates the tabs.
     */
    public void recreateTabs()
    {
        removeChild(currentConfigGui);
        currentConfigGui = new TaskMiscConfigControl(this, task, 9, 46, 140, 112, contentScrollbarControl);
        currentConfigGui.setIsFocused(true);

        removeChild(tabControl);
        tabControl = new TabControl(this,9, 33, 140);
        tabControl.add(new BlocklingsTranslationTextComponent("task.ui.tab.misc").getString(), () -> { removeChild(currentConfigGui); currentConfigGui = new TaskMiscConfigControl(this, task, 9, 46, 140, 112, contentScrollbarControl); currentConfigGui.setZIndex(2); currentConfigGui.setIsFocused(true); });

        if (task.isConfigured())
        {
            if (!task.getGoal().whitelists.isEmpty())
            {
                for (GoalWhitelist whitelist : task.getGoal().whitelists)
                {
                    if (whitelist.isUnlocked())
                    {
                        tabControl.add(whitelist.name.getString(), () ->
                        {
                            removeChild(currentConfigGui);
                            currentConfigGui = new WhitelistConfigControl(this, whitelist, 9, 46, 140, 112, contentScrollbarControl);
                            currentConfigGui.setZIndex(2);
                            currentConfigGui.setIsFocused(true);
                        });
                    }
                }
            }

            if (task.getType() == BlocklingTasks.STORE_ITEMS)
            {
                tabControl.add("TEST", () ->
                {
                    removeChild(currentConfigGui);
                    currentConfigGui = new ItemsConfigControl(this, 9, 46, 140, 112);
                    currentConfigGui.setScrollbarY(contentScrollbarControl);
                    currentConfigGui.setIsScrollableY(true);
                    currentConfigGui.setZIndex(2);
                    currentConfigGui.setIsFocused(true);
                });
            }
        }
    }

    @Override
    public void tick()
    {
        nameTextFieldControl.tick();
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        GuiUtil.bindTexture(GuiTextures.TASK_CONFIGURE);
        blit(matrixStack, screenX, screenY, 0, 0, TabbedControl.CONTENT_WIDTH, TabbedControl.CONTENT_HEIGHT);

        if (!nameTextFieldControl.isFocused())
        {
            nameTextFieldControl.setValue(task.getCustomName());
        }

        nameTextFieldControl.render(new MatrixStack(), mouseX, mouseY, partialTicks);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
    }

    @Override
    public void globalMouseClicked(@Nonnull MouseButtonEvent e)
    {
        nameTextFieldControl.mouseClicked(e.mouseX / getEffectiveScale(), e.mouseY / getEffectiveScale(), e.button);
    }

    @Override
    public void globalMouseReleased(@Nonnull MouseButtonEvent e)
    {
        nameTextFieldControl.mouseReleased(e.mouseX / getEffectiveScale(), e.mouseY / getEffectiveScale(), e.button);
    }

    @Override
    public void controlMouseScrolled(@Nonnull MouseScrollEvent e)
    {
        e.setIsHandled(contentScrollbarControl.scroll(e.scroll));
    }

    @Override
    public void controlKeyPressed(@Nonnull KeyEvent e)
    {
        if (e.isHandled())
        {
            return;
        }

        if (!nameTextFieldControl.isFocused() && GuiUtil.isCloseInventoryKey(e.keyCode))
        {
            setIsVisible(false);

            TasksScreen tasksScreen = (TasksScreen) screen;
            tasksScreen.setIsVisible(true);

            e.setIsHandled(true);
        }
        else if (e.keyCode == GLFW.GLFW_KEY_ENTER || e.keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            if (nameTextFieldControl.isFocused())
            {
                nameTextFieldControl.setFocus(false);

                e.setIsHandled(true);
            }
        }
        else
        {
            e.setIsHandled(nameTextFieldControl.keyPressed(e.keyCode, e.scanCode, e.modifiers));
        }
    }

    @Override
    public void controlCharTyped(@Nonnull CharEvent e)
    {
        e.setIsHandled(nameTextFieldControl.charTyped(e.character, e.keyCode));

        if (!e.isHandled())
        {
            if (GuiUtil.isCloseInventoryKey(e.keyCode))
            {
                setIsVisible(false);

                e.setIsHandled(true);
            }
        }
    }
}
