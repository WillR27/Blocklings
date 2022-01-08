package com.willr27.blocklings.attribute.attributes.numbers;

import com.willr27.blocklings.attribute.IModifiable;
import com.willr27.blocklings.attribute.IModifier;
import com.willr27.blocklings.attribute.Operation;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * A simple float attribute modifier.
 */
public class FloatAttributeModifier extends FloatAttribute implements IModifier<Float>
{
    /**
     * The attribute the modifier is associated with.
     */
    @Nonnull
    public final IModifiable<Float> attribute;

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
     */
    public FloatAttributeModifier(@Nonnull String id, @Nonnull String key, @Nonnull IModifiable<Float> attribute, @Nonnull BlocklingEntity blockling, float initialValue, @Nonnull Operation operation, @Nullable Supplier<String> displayStringValueSupplier, @Nullable Supplier<String> displayStringNameSupplier)
    {
        super(id, key, blockling, initialValue, displayStringValueSupplier, displayStringNameSupplier);
        this.attribute = attribute;
        this.operation = operation;
    }

    @Override
    public void setValue(Float value, boolean sync)
    {
        super.setValue(value, sync);

        attribute.calculate();
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
