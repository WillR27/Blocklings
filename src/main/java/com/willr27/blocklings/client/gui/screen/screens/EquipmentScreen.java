package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.control.controls.EntityControl;
import com.willr27.blocklings.client.gui.control.controls.TabbedUIControl;
import com.willr27.blocklings.client.gui.containers.EquipmentContainer;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A screen that displays the blockling's equipment.
 */
@OnlyIn(Dist.CLIENT)
public class EquipmentScreen extends TabbedContainerScreen<EquipmentContainer>
{
    /**
     * @param blockling   the blockling associated with the screen.
     * @param container   the container.
     */
    public EquipmentScreen(@Nonnull BlocklingEntity blockling, @Nonnull EquipmentContainer container)
    {
        super(blockling, container, TabbedUIControl.Tab.EQUIPMENT);

        EntityControl entityControl = new EntityControl();
        entityControl.setParent(tabbedUIControl.contentControl);
        entityControl.setWidth(48);
        entityControl.setHeight(48);
        entityControl.setEntity(blockling);
        entityControl.setEntityScale(0.7f);
        entityControl.setScaleToBoundingBox(true);
        entityControl.setOffsetY(-3.0f);
    }
}
