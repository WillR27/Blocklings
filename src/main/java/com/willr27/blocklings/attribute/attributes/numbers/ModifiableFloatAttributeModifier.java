package com.willr27.blocklings.attribute.attributes.numbers;

import com.willr27.blocklings.attribute.IModifiable;
import com.willr27.blocklings.attribute.IModifier;
import com.willr27.blocklings.attribute.Operation;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;

import java.util.function.Supplier;

public class ModifiableFloatAttributeModifier extends ModifiableFloatAttribute implements IModifier<Float>
{
    public final IModifiable<Float> attribute;
    public final Operation operation;

    public ModifiableFloatAttributeModifier(String id, String key, IModifiable<Float> attribute, BlocklingEntity blockling, float value, Operation operation, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
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
    public void setBaseValue(Float baseValue, boolean sync)
    {
        super.setBaseValue(value, sync);

        attribute.calculate();
    }

    @Override
    public Operation getOperation()
    {
        return operation;
    }
}
