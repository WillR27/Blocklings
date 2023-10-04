package com.willr27.blocklings.client.gui.control.controls.config;

import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui.properties.Flow;
import com.willr27.blocklings.entity.blockling.goal.config.whitelist.GoalWhitelist;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * A control used to display a whitelist.
 */
@OnlyIn(Dist.CLIENT)
public class WhitelistConfigControl extends Control
{
    /**
     * @param whitelist the whitelist to display.
     */
    public WhitelistConfigControl(@Nonnull GoalWhitelist whitelist)
    {
        super();

        setWidthPercentage(1.0);
        setFitHeightToContent(true);

        FlowPanel flowPanel = new FlowPanel();
        flowPanel.setParent(this);
        flowPanel.setFlow(Flow.TOP_LEFT_LEFT_TO_RIGHT);
        flowPanel.setWidthPercentage(1.0);
        flowPanel.setFitHeightToContent(true);
        flowPanel.setHorizontalSpacing(4.0);
        flowPanel.setVerticalSpacing(4.0);

        for (Map.Entry<ResourceLocation, Boolean> entry : whitelist.entrySet())
        {
            EntryControl entryControl = new EntryControl(whitelist, entry);
            entryControl.setParent(flowPanel);
        }
    }
}
