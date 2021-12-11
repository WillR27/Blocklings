package com.blocklings.guis;

import com.blocklings.entities.EntityBlockling;
import com.blocklings.util.ResourceLocationBlocklings;
import com.blocklings.util.helpers.GuiHelper;
import net.minecraft.entity.player.EntityPlayer;

class GuiBlocklingFarming extends GuiBlocklingAbility
{
     GuiBlocklingFarming(EntityBlockling blockling, EntityPlayer player)
    {
        super(blockling, player);

        abilities = blockling.farmingAbilities.abilities;
        BACKGROUND = new ResourceLocationBlocklings("textures/guis/inventory_overlay_3.png");
    }

    @Override
    public void initGui()
    {
        if (init)
        {
            WINDOW = new ResourceLocationBlocklings("textures/guis/inventory" + GuiHelper.Tab.FARMING.id + ".png");
        }

        super.initGui();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        abilities = blockling.farmingAbilities.abilities;
    }
}
