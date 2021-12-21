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
    public final Supplier<String> displayStringSupplier;

    public ModifiableIntAttributeModifier(String id, String key, IModifiable<Integer> attribute, BlocklingEntity blockling, int value, Operation operation, Supplier<String> displayStringSupplier)
    {
        super(id, key, blockling, value);
        this.attribute = attribute;
        this.operation = operation;
        this.displayStringSupplier = displayStringSupplier;
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
    public Supplier<String> getDisplayStringSupplier()
    {
        return displayStringSupplier;
    }
}
