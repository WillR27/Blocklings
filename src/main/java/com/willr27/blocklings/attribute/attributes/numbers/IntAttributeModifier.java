package com.willr27.blocklings.attribute.attributes.numbers;

import com.willr27.blocklings.attribute.IModifiable;
import com.willr27.blocklings.attribute.IModifier;
import com.willr27.blocklings.attribute.Operation;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A simple int attribute modifier.
 */
public class IntAttributeModifier extends IntAttribute implements IModifier<Integer>
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
     * @param displayStringValueFunction the function used to provide the string representation of the value.
     * @param displayStringNameSupplier the supplier used to provide the string representation of display name.
     * @param isEnabled whether the attribute is currently enabled.
     */
    public IntAttributeModifier(@Nonnull String id, @Nonnull String key, @Nonnull IModifiable<Integer> attribute, @Nonnull BlocklingEntity blockling, int initialValue, @Nonnull Operation operation, @Nullable Function<Integer, String> displayStringValueFunction, @Nullable Supplier<String> displayStringNameSupplier, boolean isEnabled)
    {
        super(id, key, blockling, initialValue, displayStringValueFunction, displayStringNameSupplier, isEnabled);
        this.attribute = attribute;
        this.operation = operation;

        attribute.addModifier(this);
    }

    @Override
    public void setValue(Integer value, boolean sync)
    {
        super.setValue(value, sync);

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
