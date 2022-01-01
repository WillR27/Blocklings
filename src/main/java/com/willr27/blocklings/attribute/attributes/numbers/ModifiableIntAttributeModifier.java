package com.willr27.blocklings.attribute.attributes.numbers;

import com.willr27.blocklings.attribute.IModifiable;
import com.willr27.blocklings.attribute.IModifier;
import com.willr27.blocklings.attribute.Operation;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;

import java.util.function.Supplier;

public class ModifiableIntAttributeModifier extends ModifiableIntAttribute implements IModifier<Integer>
{
    public final IModifiable<Integer> attribute;
    public final Operation operation;

    public ModifiableIntAttributeModifier(String id, String key, IModifiable<Integer> attribute, BlocklingEntity blockling, int value, Operation operation, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        super(id, key, blockling, value, displayStringValueSupplier, displayStringNameSupplier);
        this.attribute = attribute;
        this.operation = operation;
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
