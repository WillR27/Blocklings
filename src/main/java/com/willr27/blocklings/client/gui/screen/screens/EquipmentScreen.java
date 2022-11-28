package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.control.controls.TabbedControl;
import com.willr27.blocklings.client.gui2.containers.EquipmentContainer;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A screen to display the blockling's equipment.
 */
@OnlyIn(Dist.CLIENT)
public class EquipmentScreen extends TabbedContainerScreen<EquipmentContainer>
{
    /**
     * @param blockling the blockling.
     * @param container the container.
     */
    public EquipmentScreen(@Nonnull BlocklingEntity blockling, @Nonnull EquipmentContainer container)
    {
        super(blockling, TabbedControl.Tab.EQUIPMENT, container);
    }

    @Override
    protected void init()
    {
        super.init();
    }
}
