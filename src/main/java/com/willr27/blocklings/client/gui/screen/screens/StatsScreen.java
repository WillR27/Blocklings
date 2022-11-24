package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.GuiTextures;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * The screen to display the blockling's .
 */
@OnlyIn(Dist.CLIENT)
public class StatsScreen extends TabbedScreen
{
    /**
     * @param blockling the blockling.
     */
    public StatsScreen(@Nonnull BlocklingEntity blockling)
    {
        super(blockling, GuiTextures.Stats.STATS_BACKGROUND);
    }
}
