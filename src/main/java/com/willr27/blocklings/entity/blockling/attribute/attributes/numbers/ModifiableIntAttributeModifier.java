package com.willr27.blocklings.entity.blockling.attribute.attributes.numbers;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.attribute.IModifiable;
import com.willr27.blocklings.entity.blockling.attribute.IModifier;
import com.willr27.blocklings.entity.blockling.attribute.Operation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A modifiable float attribute modifier.
 * Used to create trees of modifiers with a single root attribute.
 */
public class ModifiableIntAttributeModifier extends ModifiableIntAttribute implements IModifier<Integer>
{
    /**
     * The attributes the modifier is associated with.
     */
    @Nonnull
    public final List<IModifiable<Integer>> attributes = new ArrayList<>();

    /**
     * The operation to be performed on the associated attribute and the modifier.
     */
    @Nonnull
    public final Operation operation;

    /**
     * @param id the id of the attribute.
     * @param key the key used to identify the attribute (for things like translation text components).
     * @param blockling the blockling.
     * @param initialValue the initial value of the attribute.
     * @param operation the operation to be performed on the associated attribute and the modifier.
     * @param displayStringValueFunction the function used to provide the string representation of the value.
     * @param displayStringNameSupplier the supplier used to provide the string representation of display name.
     * @param isEnabled whether the attribute is currently enabled.
     * @param modifiers the initial list of modifiers associated with the attribute.
     */
    public ModifiableIntAttributeModifier(@Nonnull String id, @Nonnull String key, @Nonnull BlocklingEntity blockling, int initialValue, @Nonnull Operation operation, @Nullable Function<Integer, String> displayStringValueFunction, @Nullable Supplier<String> displayStringNameSupplier, boolean isEnabled, @Nonnull IModifier<Integer>... modifiers)
    {
        super(id, key, blockling, initialValue, displayStringValueFunction, displayStringNameSupplier, isEnabled, modifiers);
        this.operation = operation;
    }

    @Override
    public void calculate()
    {
        super.calculate();

        attributes.forEach(IModifiable::calculate);
    }

    @Override
    public void setBaseValue(Integer baseValue, boolean sync)
    {
        super.setBaseValue(value, sync);

        attributes.forEach(IModifiable::calculate);
    }

    @Override
    @Nonnull
    public List<IModifiable<Integer>> getAttributes()
    {
        return attributes;
    }

    @Override
    @Nonnull
    public Operation getOperation()
    {
        return operation;
    }

    @Override
    public boolean isEffective()
    {
        return !((getOperation() == Operation.ADD && getValue() == 0) || ((getOperation() == Operation.MULTIPLY_BASE || getOperation() == Operation.MULTIPLY_TOTAL) && getValue() == 1));
    }

    @Override
    public void setIsEnabled(boolean isEnabled, boolean sync)
    {
        super.setIsEnabled(isEnabled, sync);

        attributes.forEach(IModifiable::calculate);
    }
}
