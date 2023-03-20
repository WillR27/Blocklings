package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.control.controls.TabbedUIControl;
import com.willr27.blocklings.client.gui.control.controls.skills.SkillsPanel;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.skill.info.SkillGroupInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A screen to show and configure skills.
 */
@OnlyIn(Dist.CLIENT)
public class SkillsScreen extends TabbedScreen
{
    /**
     * The skill group to show.
     */
    @Nonnull
    private final SkillGroupInfo skillGroupInfo;

    /**
     * @param blockling the blockling associated with the screen.
     * @param skillGroupInfo the skill group to show.
     * @param selectedTab the tab to select when the screen is opened.
     */
    public SkillsScreen(@Nonnull BlocklingEntity blockling, @Nonnull SkillGroupInfo skillGroupInfo, @Nonnull TabbedUIControl.Tab selectedTab)
    {
        super(blockling, selectedTab);
        this.skillGroupInfo = skillGroupInfo;

        tabbedUIControl.backgroundControl.setParent(null, true);
        tabbedUIControl.addChild(tabbedUIControl.backgroundControl, 1, 0);
        tabbedUIControl.backgroundControl.setRenderZ(0.3);
        tabbedUIControl.contentControl.setPadding(1.0);

        SkillsPanel skillsPanel = new SkillsPanel(blockling, skillGroupInfo, tabbedUIControl);
        skillsPanel.setParent(tabbedUIControl.contentControl);
        skillsPanel.setBackgroundColour(0xffffffff);
    }
}
