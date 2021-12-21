package com.willr27.blocklings.attribute.attributes.numbers;

import com.willr27.blocklings.attribute.IModifiable;
import com.willr27.blocklings.attribute.IModifier;
import com.willr27.blocklings.attribute.Operation;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.NetworkHandler;

import java.util.function.Supplier;

public class FloatAttributeModifier extends FloatAttribute implements IModifier<Float>
{
    public final IModifiable<Float> attribute;
    public final Operation operation;
    public final Supplier<String> displayStringSupplier;

    public FloatAttributeModifier(String id, String key, IModifiable<Float> attribute, BlocklingEntity blockling, float value, Operation operation, Supplier<String> displayStringSupplier)
    {
        super(id, key, blockling, value);
        this.attribute = attribute;
        this.operation = operation;
        this.displayStringSupplier = displayStringSupplier;
    }

    @Override
    public void setValue(float value, boolean sync)
    {
        super.setValue(value, sync);

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
