package com.willr27.blocklings.entity.blockling.task.config;

import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.controls.SingleSelectorStrip;
import com.willr27.blocklings.client.gui.control.controls.config.ItemsConfigurationControl;
import com.willr27.blocklings.client.gui.control.event.events.SelectionChangedEvent;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.entity.blockling.goal.config.iteminfo.OrderedItemInfoSet;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import com.willr27.blocklings.util.Version;
import com.willr27.blocklings.util.event.EventHandler;
import com.willr27.blocklings.util.event.IEvent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Used to configure an item configuration type property.
 */
public class ItemConfigurationTypeProperty extends Property
{
    /**
     * Invoked when the type of item configuration is changed.
     */
    @Nonnull
    public final EventHandler<Type> onTypeChanged = new EventHandler<>();

    /**
     * The current type of item configuration.
     */
    private Type type = Type.SIMPLE;

    /**
     * @param id the id of the property (used for syncing between serialising\deserialising).
     * @param goal the associated task's goal.
     * @param name the name of the property.
     * @param desc the description of the property.
     */
    public ItemConfigurationTypeProperty(@Nonnull String id, @Nonnull BlocklingGoal goal, @Nonnull ITextComponent name, @Nonnull ITextComponent desc)
    {
        super(id, goal, name, desc);
    }

    @Override
    public CompoundNBT writeToNBT(@Nonnull CompoundNBT propertyTag)
    {
        propertyTag.putInt("type", type.ordinal());

        return super.writeToNBT(propertyTag);
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT propertyTag, @Nonnull Version tagVersion)
    {
        setType(Type.values()[propertyTag.getInt("type")], false);

        super.readFromNBT(propertyTag, tagVersion);
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeInt(type.ordinal());
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        setType(Type.values()[buf.readInt()], false);
    }

    @Nonnull
    @Override
    public BaseControl createControl()
    {
        SingleSelectorStrip<Type> selector = new SingleSelectorStrip<>();
        selector.setOptions(Arrays.asList(Type.values()));
        selector.setWidthPercentage(1.0);
        selector.setSelectedOption(getType());
        selector.eventBus.subscribe((BaseControl c, SelectionChangedEvent<Type> e) ->
        {
            setType(e.newItem, true);
        });

        return selector;
    }

    /**
     * @return the current type of item configuration.
     */
    @Nonnull
    public Type getType()
    {
        return type;
    }

    /**
     * Sets the current type of item configuration and syncs to the client/server.
     *
     * @param type the new type.
     */
    public void setType(@Nonnull Type type)
    {
        setType(type, true);
    }

    /**
     * Sets the current type of item configuration.
     *
     * @param type the new type.
     * @param sync whether to sync to the client/server.
     */
    public void setType(@Nonnull Type type, boolean sync)
    {
        if (this.type.equals(type))
        {
            return;
        }

        this.type = type;

        onTypeChanged.handle(type);

        if (sync)
        {
            new TaskPropertyMessage(this).sync();
        }
    }

    /**
     * The different types of item configuration.
     */
    public enum Type implements IEvent
    {
        SIMPLE,
        ADVANCED;

        /**
         * Creates a new {@link ItemsConfigurationControl} based on the type.
         *
         * @param itemInfoSet the {@link OrderedItemInfoSet} to use.
         * @param isTakeItems whether the items are being taken or deposited.
         * @return the new {@link ItemsConfigurationControl}.
         */
        @Nonnull
        public ItemsConfigurationControl createItemsConfigurationControl(@Nonnull OrderedItemInfoSet itemInfoSet, boolean isTakeItems)
        {
            switch (this)
            {
                case ADVANCED:
                    return new ItemsConfigurationControl.AdvancedItemsConfigurationControl(itemInfoSet, isTakeItems);
                default:
                    return new ItemsConfigurationControl.SimpleItemsConfigurationControl(itemInfoSet, isTakeItems);
            }
        }

        @Override
        public String toString()
        {
            return new BlocklingsTranslationTextComponent("task.property.item_configuration_type." + name().toLowerCase()).getString();
        }
    }
}
