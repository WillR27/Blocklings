package com.blocklings.guis;

import com.blocklings.entities.EntityBlockling;
import com.blocklings.util.ResourceLocationBlocklings;
import com.blocklings.util.helpers.GuiHelper;
import net.minecraft.entity.player.EntityPlayer;

class GuiBlocklingMining extends GuiBlocklingAbility
{
    GuiBlocklingMining(EntityBlockling blockling, EntityPlayer player)
    {
        super(blockling, player);

        abilities = blockling.miningAbilities.abilities;
    }

    @Override
    public void initGui()
    {
        if (init)
        {
            WINDOW = new ResourceLocationBlocklings("textures/guis/inventory" + GuiHelper.Tab.MINING.id + ".png");
        }

        super.initGui();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        abilities = blockling.miningAbilities.abilities;
    }
}
