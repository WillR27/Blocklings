package com.willr27.blocklings.entity.blockling.attribute.attributes.numbers;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.attribute.IModifier;
import com.willr27.blocklings.entity.blockling.attribute.ModifiableAttribute;
import com.willr27.blocklings.entity.blockling.attribute.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.w3c.dom.Attr;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A modifiable number attribute.
 */
public abstract class ModifiableNumberAttribute<T extends Number> extends ModifiableAttribute<T>
{
    /**
     * The vanilla attribute to update when the number attribute changes.
     */
    @Nullable
    protected net.minecraft.world.entity.ai.attributes.Attribute vanillaAttribute;

    /**
     * @param id the id of the attribute.
     * @param key the key used to identify the attribute (for things like translation text components).
     * @param blockling the blockling.
     * @param initialBaseValue the initial base value.
     * @param displayStringValueFunction the function used to provide the string representation of the value.
     * @param displayStringNameSupplier the supplier used to provide the string representation of display name.
     * @param isEnabled whether the attribute is currently enabled.
     * @param modifiers the initial list of modifiers associated with the attribute.
     */
    public ModifiableNumberAttribute(@Nonnull String id, @Nonnull String key, @Nonnull BlocklingEntity blockling, T initialBaseValue, @Nullable Function<T, String> displayStringValueFunction, @Nullable Supplier<String> displayStringNameSupplier, boolean isEnabled, @Nonnull IModifier<T>... modifiers)
    {
        super(id, key, blockling, initialBaseValue, displayStringValueFunction, displayStringNameSupplier, isEnabled, modifiers);
    }

    /**
     * @return the vanilla attribute to update when the number attribute changes.
     */
    @Nullable
    public net.minecraft.world.entity.ai.attributes.Attribute getVanillaAttribute()
    {
        return vanillaAttribute;
    }

    /**
     * Sets the vanilla attribute to update when the number attribute changes.
     */
    public void setVanillaAttribute(@Nullable net.minecraft.world.entity.ai.attributes.Attribute vanillaAttribute)
    {
        removeFromVanillaAttribute();

        this.vanillaAttribute = vanillaAttribute;

        updateVanillaAttribute();
    }

    /**
     * Removes the number attribute from the vanilla attribute.
     */
    protected void removeFromVanillaAttribute()
    {
        if (vanillaAttribute != null)
        {
            AttributeInstance vanillaAttributeInstance = blockling.getAttribute(vanillaAttribute);

            if (this instanceof IModifier)
            {
                vanillaAttributeInstance.removeModifier(id);
            }
            else
            {
                // Leave the base value alone, as it could be the case something else has set it.
            }
        }
    }

    /**
     * Updates the vanilla attribute inline with the number attribute.
     */
    protected void updateVanillaAttribute()
    {
        if (vanillaAttribute == null)
        {
            return;
        }

        AttributeInstance vanillaAttributeInstance = blockling.getAttribute(vanillaAttribute);

        // If this attribute is a modifier then add it to the vanilla attribute as a transient modifier.
        if (this instanceof IModifier)
        {
            IModifier<T> modifier = (IModifier<T>) this;

            if (modifier.getAttributes().stream().anyMatch(modifiable -> modifiable instanceof IModifier))
            {
                // Because vanilla attributes only support 1 layer of modifiers, prevent any nested modifiers from being added.
                // Otherwise, the same modifier could be applied twice.
                Blocklings.LOGGER.warn("Tried to add a modifier to a vanilla attribute that is applied to other modifiers.");

                return;
            }

            // Remove the modifier it exists as you can't just set the value again.
            vanillaAttributeInstance.removeModifier(id);

            // Do not apply the modifier if it is not enabled.
            if (isEnabled())
            {
                // Add the attribute modifier with the current value.
                vanillaAttributeInstance.addTransientModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(id, key, getValue().doubleValue(), Operation.vanillaOperation(modifier.getOperation())));
            }
        }
        // If this is just a regular attribute, use it to set the base value for the vanilla attribute.
        else
        {
            vanillaAttributeInstance.setBaseValue(value.doubleValue());
        }
    }

    @Override
    public void onValueChanged()
    {
        super.onValueChanged();

        updateVanillaAttribute();
    }
}
