package com.willr27.blocklings.client.gui.controls.tasks.config.configs;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.IControl;
import com.willr27.blocklings.client.gui.controls.common.ScrollbarControl;
import com.willr27.blocklings.client.gui.controls.tasks.config.ConfigControl;
import com.willr27.blocklings.client.gui.controls.tasks.config.EntryControl;
import com.willr27.blocklings.entity.blockling.whitelist.GoalWhitelist;
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
        contentScrollbarControl.setScrollY(0);

        int i = 0;
        for (Map.Entry<ResourceLocation, Boolean> entry : whitelist.entrySet())
        {
            entryControls.add(new EntryControl(this, whitelist, entry, ENTRY_GAP + (i % 4) * (EntryControl.ENTRY_SELECTED.width + ENTRY_GAP), 3 + ENTRY_GAP + (i / 4) * (EntryControl.ENTRY_SELECTED.height + ENTRY_GAP)));
            contentScrollbarControl.setMaxScrollY(3 + ENTRY_GAP + (i / 4) * (EntryControl.ENTRY_SELECTED.height + ENTRY_GAP) + EntryControl.ENTRY_UNSELECTED.height + ENTRY_GAP - height);
            i++;
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        updateEntryPositions();
    }

    private void updateEntryPositions()
    {
        contentScrollbarControl.setIsDisabled(true);

        for (int i = 0; i < entryControls.size(); i++)
        {
            EntryControl entryControl = entryControls.get(i);
            entryControl.setX(ENTRY_GAP + ((i % 4) * (EntryControl.ENTRY_UNSELECTED.width + ENTRY_GAP)));
            entryControl.setY(3 + ENTRY_GAP + ((i / 4) * (EntryControl.ENTRY_UNSELECTED.height + ENTRY_GAP)));
        }

        if (entryControls.size() >= 2)
        {
            int taskControlsHeight = entryControls.get(entryControls.size() - 1).getY() + entryControls.get(entryControls.size() - 1).getHeight() - entryControls.get(0).getY() + ENTRY_GAP * 2 + 3;
            int taskControlsHeightDif = taskControlsHeight - getHeight();

            if (taskControlsHeightDif > 0)
            {
                contentScrollbarControl.setIsDisabled(false);

                for (int i = 0; i < entryControls.size(); i++)
                {
                    EntryControl entryControl = entryControls.get(i);
                    entryControl.setY(3 + ENTRY_GAP + ((i / 4) * (EntryControl.ENTRY_UNSELECTED.height + ENTRY_GAP)) - (int) (taskControlsHeightDif * contentScrollbarControl.percentageScrolled()));
                }
            }
        }
    }
}
