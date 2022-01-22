package com.willr27.blocklings.gui.controls.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.gui.IControl;
import com.willr27.blocklings.gui.controls.tasks.EntryControl;
import com.willr27.blocklings.gui.controls.common.ScrollbarControl;
import com.willr27.blocklings.gui.controls.tasks.ConfigControl;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A control used to display a whitelist.
 */
@OnlyIn(Dist.CLIENT)
public class WhitelistConfigControl extends ConfigControl
{
    /**
     * The gap between each whitelist entry.
     */
    public static final int ENTRY_GAP = 4;

    /**
     * The scrollbar control to use with the whitelist.
     */
    @Nonnull
    private final ScrollbarControl contentScrollbarControl;

    /**
     * The list of individual entry controls in the whitelist.
     */
    @Nonnull
    private final List<EntryControl> entryControls = new ArrayList<>();

    /**
     * @param parent the parent control.
     * @param whitelist the whitelist to display.
     * @param x the x position.
     * @param y the y position.
     * @param width the width.
     * @param height the height.
     * @param contentScrollbarControl the scrollbar control to use.
     */
    public WhitelistConfigControl(@Nonnull IControl parent, @Nonnull GoalWhitelist whitelist, int x, int y, int width, int height, @Nonnull ScrollbarControl contentScrollbarControl)
    {
        super(parent, x, y, width, height);
        this.contentScrollbarControl = contentScrollbarControl;

        int i = 0;
        for (Map.Entry<ResourceLocation, Boolean> entry : whitelist.entrySet())
        {
            entryControls.add(new EntryControl(this, whitelist, entry, ENTRY_GAP + (i % 4) * (EntryControl.ENTRY_SELECTED.width + ENTRY_GAP), ENTRY_GAP + (i / 4) * (EntryControl.ENTRY_SELECTED.height + ENTRY_GAP)));
            i++;
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        updateEntryPositions();

        for (EntryControl entryControl : entryControls)
        {
            entryControl.render(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public void renderTooltips(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        EntryControl hoveredEntryControl = getHoveredEntryControl(mouseX, mouseY);

        if (hoveredEntryControl != null)
        {
            hoveredEntryControl.renderTooltip(matrixStack, mouseX, mouseY);
        }
    }

    private void updateEntryPositions()
    {
        contentScrollbarControl.isDisabled = true;

        for (int i = 0; i < entryControls.size(); i++)
        {
            EntryControl entryControl = entryControls.get(i);
            entryControl.setX(ENTRY_GAP + ((i % 4) * (EntryControl.ENTRY_UNSELECTED.width + ENTRY_GAP)));
            entryControl.setY(ENTRY_GAP + ((i / 4) * (EntryControl.ENTRY_UNSELECTED.height + ENTRY_GAP)));
        }

        if (entryControls.size() >= 2)
        {
            int taskControlsHeight = entryControls.get(entryControls.size() - 1).screenY + entryControls.get(entryControls.size() - 1).height - entryControls.get(0).screenY + ENTRY_GAP * 2;
            int taskControlsHeightDif = taskControlsHeight - height;

            if (taskControlsHeightDif > 0)
            {
                contentScrollbarControl.isDisabled = false;

                for (int i = 0; i < entryControls.size(); i++)
                {
                    EntryControl entryControl = entryControls.get(i);
                    entryControl.setY(ENTRY_GAP + ((i / 4) * (EntryControl.ENTRY_UNSELECTED.height + ENTRY_GAP)) - (int) (taskControlsHeightDif * contentScrollbarControl.percentageScrolled()));
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        if (entryControls.stream().filter(entryControl -> entryControl.mouseClicked((int) mouseX, (int) mouseY, button)).findFirst().isPresent())
        {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        if (entryControls.stream().filter(entryControl -> entryControl.mouseReleased((int) mouseX, (int) mouseY, button)).findFirst().isPresent())
        {
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * @return the entry control under the mouse.
     */
    private EntryControl getHoveredEntryControl(int mouseX, int mouseY)
    {
        return entryControls.stream().filter(entryControl -> entryControl.isMouseOver(mouseX, mouseY)).findFirst().orElse(null);
    }
}
