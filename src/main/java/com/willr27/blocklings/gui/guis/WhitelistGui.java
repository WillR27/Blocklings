package com.willr27.blocklings.gui.guis;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.gui.widgets.EntryWidget;
import com.willr27.blocklings.gui.widgets.ScrollbarWidget;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WhitelistGui extends ConfigGui
{
    public static final int ENTRY_GAP = 4;

    private final GoalWhitelist whitelist;
    private final FontRenderer font;
    private final int contentLeft, contentTop;
    private final int width, height;
    private final ScrollbarWidget contentScrollbarWidget;

    private final List<EntryWidget> entryWidgets = new ArrayList<>();

    public WhitelistGui(GoalWhitelist whitelist, FontRenderer font, int contentLeft, int contentTop, int width, int height, ScrollbarWidget contentScrollbarWidget)
    {
        this.whitelist = whitelist;
        this.font = font;
        this.contentLeft = contentLeft;
        this.contentTop = contentTop;
        this.width = width;
        this.height = height;
        this.contentScrollbarWidget = contentScrollbarWidget;

        int i = 0;
        for (Map.Entry<ResourceLocation, Boolean> entry : whitelist.entrySet())
        {
            entryWidgets.add(new EntryWidget(whitelist, entry, font, contentLeft + ENTRY_GAP + (i % 4) * (EntryWidget.ENTRY_SELECTED.width + ENTRY_GAP), contentTop + ENTRY_GAP + (i / 4) * (EntryWidget.ENTRY_SELECTED.height + ENTRY_GAP)));
            i++;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        updateEntryPositions();

        for (EntryWidget entryWidget : entryWidgets)
        {
            entryWidget.render(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public void renderTooltips(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        EntryWidget hoveredEntryWidget = getHoveredEntryWidget(mouseX, mouseY);

        if (hoveredEntryWidget != null)
        {
            hoveredEntryWidget.renderTooltip(matrixStack, mouseX, mouseY);
        }
    }

    private void updateEntryPositions()
    {
        contentScrollbarWidget.isDisabled = true;

        for (int i = 0; i < entryWidgets.size(); i++)
        {
            EntryWidget entryWidget = entryWidgets.get(i);
            entryWidget.screenX = contentLeft + ENTRY_GAP + ((i % 4) * (EntryWidget.ENTRY_UNSELECTED.width + ENTRY_GAP));
            entryWidget.screenY = contentTop + ENTRY_GAP + ((i / 4) * (EntryWidget.ENTRY_UNSELECTED.height + ENTRY_GAP));
        }

        if (entryWidgets.size() >= 2)
        {
            int taskWidgetsHeight = entryWidgets.get(entryWidgets.size() - 1).screenY + entryWidgets.get(entryWidgets.size() - 1).height - entryWidgets.get(0).screenY + ENTRY_GAP * 2;
            int taskWidgetsHeightDif = taskWidgetsHeight - height;

            if (taskWidgetsHeightDif > 0)
            {
                contentScrollbarWidget.isDisabled = false;

                for (int i = 0; i < entryWidgets.size(); i++)
                {
                    EntryWidget entryWidget = entryWidgets.get(i);
                    entryWidget.screenY = contentTop + ENTRY_GAP + ((i / 4) * (EntryWidget.ENTRY_UNSELECTED.height + ENTRY_GAP)) - (int) (taskWidgetsHeightDif * contentScrollbarWidget.percentageScrolled());
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        if (entryWidgets.stream().filter(entryWidget -> entryWidget.mouseClicked((int) mouseX, (int) mouseY, state)).findFirst().isPresent())
        {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        if (entryWidgets.stream().filter(entryWidget -> entryWidget.mouseReleased((int) mouseX, (int) mouseY, state)).findFirst().isPresent())
        {
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, state);
    }

    private EntryWidget getHoveredEntryWidget(int mouseX, int mouseY)
    {
        return entryWidgets.stream().filter(entryWidget -> entryWidget.isMouseOver(mouseX, mouseY)).findFirst().orElse(null);
    }
}
