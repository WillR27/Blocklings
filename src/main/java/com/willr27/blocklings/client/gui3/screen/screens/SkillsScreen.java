package com.willr27.blocklings.client.gui3.screen.screens;

import com.willr27.blocklings.client.gui3.control.controls.TabbedUIControl;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.skill.SkillGroup;
import com.willr27.blocklings.entity.blockling.skill.info.SkillGroupInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A screen to display the blockling's skills.
 */
@OnlyIn(Dist.CLIENT)
public class SkillsScreen extends TabbedScreen
{
    /**
     * The skill group to display.
     */
    @Nonnull
    private final SkillGroup skillGroup;

    /**
     * @param blockling the blockling.
     * @param tab the associated tab.
     * @param skillGroupInfo the skill group info of the skill group to display.
     */
    public SkillsScreen(@Nonnull BlocklingEntity blockling, @Nonnull TabbedUIControl.Tab tab, @Nonnull SkillGroupInfo skillGroupInfo)
    {
        super(blockling, tab);
        this.skillGroup = blockling.getSkills().getGroup(skillGroupInfo);
    }

    @Override
    protected void init()
    {
        super.init();
    }
}
