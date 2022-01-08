package com.willr27.blocklings.attribute.attributes.numbers;

import com.willr27.blocklings.attribute.IModifiable;
import com.willr27.blocklings.attribute.IModifier;
import com.willr27.blocklings.attribute.Operation;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * A modifiable float attribute modifier.
 * Used to create trees of modifiers with a single root attribute.
 */
public class ModifiableIntAttributeModifier extends ModifiableIntAttribute implements IModifier<Integer>
{
    /**
     * The attribute the modifier is associated with.
     */
    @Nonnull
    public final IModifiable<Integer> attribute;

    /**
     * The operation to be performed on the associated attribute and the modifier.
     */
    @Nonnull
    public final Operation operation;

    /**
     * @param id the id of the attribute.
     * @param key the key used to identify the attribute (for things like translation text components).
     * @param attribute the attribute the modifier is associated with.
     * @param blockling the blockling.
     * @param initialValue the initial value of the attribute.
     * @param operation the operation to be performed on the associated attribute and the modifier.
     * @param displayStringValueSupplier the supplier used to provide the string representation of the value.
     * @param displayStringNameSupplier the supplier used to provide the string representation of display name.
     * @param isEnabled whether the attribute is currently enabled.
     */
    public ModifiableIntAttributeModifier(@Nonnull String id, @Nonnull String key, @Nonnull IModifiable<Integer> attribute, @Nonnull BlocklingEntity blockling, int initialValue, @Nonnull Operation operation, @Nullable Supplier<String> displayStringValueSupplier, @Nullable Supplier<String> displayStringNameSupplier, boolean isEnabled)
    {
        super(id, key, blockling, initialValue, displayStringValueSupplier, displayStringNameSupplier, isEnabled);
        this.attribute = attribute;
        this.operation = operation;

        attribute.addModifier(this);
    }

    @Override
    public void calculate()
    {
        super.calculate();

        attribute.calculate();
    }

    @Override
    public void setBaseValue(Integer baseValue, boolean sync)
    {
        super.setBaseValue(value, sync);

        attribute.calculate();
    }

    @Override
    @Nonnull
    public IModifiable<Integer> getAttribute()
    {
        return attribute;
    }

    @Override
    @Nonnull
    public Operation getOperation()
    {
        return operation;
    }

    @Override
    public void setIsEnabled(boolean isEnabled, boolean sync)
    {
        super.setIsEnabled(isEnabled, sync);

        attribute.calculate();
    }
}
