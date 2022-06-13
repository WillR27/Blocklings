package com.willr27.blocklings.entity.blockling.attribute.attributes.numbers;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.attribute.Attribute;
import com.willr27.blocklings.entity.blockling.attribute.IModifier;
import com.willr27.blocklings.entity.blockling.attribute.Operation;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A simple number attribute.
 */
public abstract class NumberAttribute<T extends Number> extends Attribute<T>
{
    /**
     * The vanilla attribute to update when the number attribute changes.
     */
    @Nullable
    protected net.minecraft.entity.ai.attributes.Attribute vanillaAttribute;

    /**
     * @param id the id of the attribute.
     * @param key the key used to identify the attribute (for things like translation text components).
     * @param blockling the blockling.
     * @param initialValue the initial value of the attribute.
     * @param displayStringValueFunction the function used to provide the string representation of the value.
     * @param displayStringNameSupplier the supplier used to provide the string representation of display name.
     * @param isEnabled whether the attribute is currently enabled.
     */
    public NumberAttribute(@Nonnull String id, @Nonnull String key, @Nonnull BlocklingEntity blockling, T initialValue, @Nullable Function<T, String> displayStringValueFunction, @Nullable Supplier<String> displayStringNameSupplier, boolean isEnabled)
    {
        super(id, key, blockling, displayStringValueFunction, displayStringNameSupplier, isEnabled);
        this.value = initialValue;
    }

    /**
     * @return the vanilla attribute to update when the number attribute changes.
     */
    @Nullable
    public net.minecraft.entity.ai.attributes.Attribute getVanillaAttribute()
    {
        return vanillaAttribute;
    }

    /**
     * Sets the vanilla attribute to update when the number attribute changes.
     */
    public void setVanillaAttribute(@Nullable net.minecraft.entity.ai.attributes.Attribute vanillaAttribute)
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
            ModifiableAttributeInstance vanillaAttributeInstance = blockling.getAttribute(vanillaAttribute);

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

        ModifiableAttributeInstance vanillaAttributeInstance = blockling.getAttribute(vanillaAttribute);

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
                vanillaAttributeInstance.addTransientModifier(new net.minecraft.entity.ai.attributes.AttributeModifier(id, getDisplayStringNameSupplier().get(), getValue().doubleValue(), Operation.vanillaOperation(modifier.getOperation())));
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
