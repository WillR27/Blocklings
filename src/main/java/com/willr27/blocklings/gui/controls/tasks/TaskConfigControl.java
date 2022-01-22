package com.willr27.blocklings.gui.controls.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.IControl;
import com.willr27.blocklings.gui.controls.common.ScrollbarControl;
import com.willr27.blocklings.gui.controls.common.TextFieldControl;
import com.willr27.blocklings.gui.controls.TabbedControl;
import com.willr27.blocklings.task.Task;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

/**
 * A control to display the config options for a task.
 */
@OnlyIn(Dist.CLIENT)
public class TaskConfigControl extends Control
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
    public TaskConfigControl(@Nonnull IControl parent, @Nonnull Task task, int x, int y)
    {
        super(parent, x, y, 176, 166);
        this.task = task;

        contentControl = new Control(this, 9, 43, 140, 115);

        contentScrollbarControl = new ScrollbarControl(this, 155, 33, 12, 125);

        nameTextFieldControl = new TextFieldControl(font, screenX + 11, screenY + 11, 154, 14, new StringTextComponent(""))
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
        currentConfigGui = new TaskTypeConfigControl(this, task, 9, 49, 140, 109, contentScrollbarControl);

        removeChild(tabControl);
        tabControl = new TabControl(this,9, 33, 140);
        tabControl.add("Type", () -> { removeChild(currentConfigGui); currentConfigGui = new TaskTypeConfigControl(this, task, 9, 49, 140, 109, contentScrollbarControl); });

        if (task.isConfigured() && !task.getGoal().whitelists.isEmpty())
        {
            for (GoalWhitelist whitelist : task.getGoal().whitelists)
            {
                if (whitelist.isUnlocked())
                {
                    tabControl.add(whitelist.name.getString(), () -> { removeChild(currentConfigGui); currentConfigGui = new WhitelistConfigControl(this, whitelist, 9, 49, 140, 109, contentScrollbarControl); });
                }
            }
        }
    }

    /**
     * Renders the control.
     *
     * @param matrixStack the matrix stack.
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     * @param partialTicks the partial ticks.
     */
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        GuiUtil.bindTexture(GuiTextures.TASK_CONFIGURE);
        blit(matrixStack, screenX, screenY, 0, 0, TabbedControl.CONTENT_WIDTH, TabbedControl.CONTENT_HEIGHT);

        contentControl.enableScissor();
        GuiUtil.setScissorBounds(contentControl.screenX, contentControl.screenY, contentControl.screenX + contentControl.width, contentControl.screenY + contentControl.height);

        currentConfigGui.render(matrixStack, mouseX, mouseY, partialTicks);

        GuiUtil.disableScissor();

        tabControl.render(matrixStack, mouseX, mouseY);

        contentScrollbarControl.render(matrixStack, mouseX, mouseY);

        if (!nameTextFieldControl.isFocused())
        {
            nameTextFieldControl.setValue(task.getCustomName());
        }

        nameTextFieldControl.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
    }

    /**
     * Renders the control's tooltips.
     *
     * @param matrixStack the matrix stack.
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     */
    public void renderTooltips(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (tabControl.isMouseOver(mouseX, mouseY))
        {
            tabControl.renderTooltips(matrixStack, mouseX, mouseY);
        }
        else if (contentControl.isMouseOver(mouseX, mouseY))
        {
            currentConfigGui.renderTooltips(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        if (nameTextFieldControl.mouseClicked(mouseX, mouseY, button))
        {
            return true;
        }
        else if (tabControl.mouseClicked((int) mouseX, (int) mouseY, button))
        {
            return true;
        }
        else if (contentScrollbarControl.mouseClicked((int) mouseX, (int) mouseY, button))
        {
            return true;
        }
        else if (contentControl.isMouseOver((int) mouseX, (int) mouseY))
        {
            if (currentConfigGui.mouseClicked((int) mouseX, (int) mouseY, button))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        boolean ret = false;

        if (nameTextFieldControl.isFocused() && nameTextFieldControl.mouseReleased(mouseX, mouseY, button))
        {
            ret = true;
        }

        if (tabControl.mouseReleased(mouseX, mouseY, button))
        {
            ret = true;
        }

        if (contentScrollbarControl.mouseReleased(mouseX, mouseY, button))
        {
            ret = true;
        }

        if (contentControl.isMouseOver(mouseX, mouseY))
        {
            if (currentConfigGui.mouseReleased(mouseX, mouseY, button))
            {
                ret = true;
            }
        }

        return ret;
    }

    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, double scroll)
    {
        if (contentControl.isMouseOver(mouseX, mouseY) || contentScrollbarControl.isMouseOver((int) mouseX, (int) mouseY))
        {
            if (contentScrollbarControl.scroll(scroll))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int mods)
    {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            if (nameTextFieldControl.isFocused())
            {
                nameTextFieldControl.setFocus(false);

                return true;
            }
        }
        else
        {
            nameTextFieldControl.keyPressed(keyCode, scanCode, mods);
        }

        return false;
    }

    @Override
    public boolean charTyped(char character, int keyCode)
    {
        return nameTextFieldControl.charTyped(character, keyCode);
    }
}
