package com.willr27.blocklings.gui.guis;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.task.Task;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.gui.controls.ScrollbarWidget;
import com.willr27.blocklings.gui.controls.TabWidget;
import com.willr27.blocklings.gui.controls.TextFieldWidget;
import com.willr27.blocklings.gui.controls.Widget;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

public class TaskConfigGui extends AbstractGui
{
    public final Task task;
    public final FontRenderer font;
    public final Screen screen;

    private int centerX, centerY;
    private int left, top;
    private int contentLeft, contentTop;

    private Widget contentWidget;
    private ScrollbarWidget contentScrollbarWidget;
    private TabWidget tabWidget;
    private ConfigGui configGui;

    public TextFieldWidget nameField;

    public TaskConfigGui(Task task, FontRenderer font, Screen screen)
    {
        this.task = task;
        this.font = font;
        this.screen = screen;

        centerX = screen.width / 2;
        centerY = screen.height / 2 + TabbedGui.OFFSET_Y;

        left = centerX - TabbedGui.GUI_WIDTH / 2;
        top = centerY - TabbedGui.GUI_HEIGHT / 2;

        contentLeft = centerX - TabbedGui.CONTENT_WIDTH / 2;
        contentTop = top;

        contentWidget = new Widget(font, contentLeft + 9, contentTop + 43, 140, 115);

        contentScrollbarWidget = new ScrollbarWidget(font, contentLeft + 155, contentTop + 33, 12, 125);

        nameField = new TextFieldWidget(font, contentLeft + 11, contentTop + 11, 154, 14, new StringTextComponent(""))
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
        nameField.setMaxLength(25);
        nameField.setVisible(true);
        nameField.setTextColor(16777215);
        nameField.setValue(task.getCustomName());

        recreateTabs();
    }

    public void recreateTabs()
    {
        configGui = new TaskTypeGui(task, font, contentLeft + 9, contentTop + 49, 140, 109, contentScrollbarWidget, this);

        tabWidget = new TabWidget(task, font, contentLeft + 9, contentTop + 33, 140);
        tabWidget.add("Type", () -> configGui = new TaskTypeGui(task, font, contentLeft + 9, contentTop + 49, 140, 109, contentScrollbarWidget, this));

        if (task.isConfigured() && !task.getGoal().whitelists.isEmpty())
        {
            for (GoalWhitelist whitelist : task.getGoal().whitelists)
            {
                if (whitelist.isUnlocked())
                {
                    tabWidget.add(whitelist.name.getString(), () -> configGui = new WhitelistGui(whitelist, font, contentLeft + 9, contentTop + 49, 140, 109, contentScrollbarWidget));
                }
            }
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        GuiUtil.bindTexture(GuiTextures.TASK_CONFIGURE);
        blit(matrixStack, contentLeft, contentTop, 0, 0, TabbedGui.CONTENT_WIDTH, TabbedGui.CONTENT_HEIGHT);

        contentWidget.enableScissor();
        GuiUtil.setScissorBounds(contentWidget.screenX, contentWidget.screenY, contentWidget.screenX + contentWidget.width, contentWidget.screenY + contentWidget.height);

        configGui.render(matrixStack, mouseX, mouseY, partialTicks);

        GuiUtil.disableScissor();

        tabWidget.render(matrixStack, mouseX, mouseY);

        contentScrollbarWidget.render(matrixStack, mouseX, mouseY);

        if (!nameField.isFocused())
        {
            nameField.setValue(task.getCustomName());
        }

        nameField.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
    }

    public void renderTooltips(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (tabWidget.isMouseOver(mouseX, mouseY))
        {
            tabWidget.renderTooltips(matrixStack, mouseX, mouseY, screen);
        }
        else if (contentWidget.isMouseOver(mouseX, mouseY))
        {
            configGui.renderTooltips(matrixStack, mouseX, mouseY);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        if (nameField.mouseClicked(mouseX, mouseY, state))
        {
            return true;
        }
        else if (tabWidget.mouseClicked((int) mouseX, (int) mouseY, state))
        {
            return true;
        }
        else if (contentScrollbarWidget.mouseClicked((int) mouseX, (int) mouseY, state))
        {
            return true;
        }
        else if (contentWidget.isMouseOver((int) mouseX, (int) mouseY))
        {
            if (configGui.mouseClicked((int) mouseX, (int) mouseY, state))
            {
                return true;
            }
        }

        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        boolean ret = false;

        if (nameField.isFocused() && nameField.mouseReleased(mouseX, mouseY, state))
        {
            ret = true;
        }

        if (tabWidget.mouseReleased((int) mouseX, (int) mouseY, state))
        {
            ret = true;
        }

        if (contentScrollbarWidget.mouseReleased((int) mouseX, (int) mouseY, state))
        {
            ret = true;
        }

        if (contentWidget.isMouseOver((int) mouseX, (int) mouseY))
        {
            if (configGui.mouseReleased((int) mouseX, (int) mouseY, state))
            {
                ret = true;
            }
        }

        return ret;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scroll)
    {
        if (contentWidget.isMouseOver((int) mouseX, (int) mouseY) || contentScrollbarWidget.isMouseOver((int) mouseX, (int) mouseY))
        {
            if (contentScrollbarWidget.scroll(scroll))
            {
                return true;
            }
        }

        return false;
    }

    public boolean keyPressed(int keyCode, int i, int j)
    {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            if (nameField.isFocused())
            {
                nameField.setFocus(false);

                return true;
            }
        }
        else
        {
            nameField.keyPressed(keyCode, i, j);
        }

        return false;
    }

    public boolean charTyped(char cah, int code)
    {
        return nameField.charTyped(cah, code);
    }
}
