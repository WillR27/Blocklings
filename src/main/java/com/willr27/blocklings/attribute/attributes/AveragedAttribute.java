package com.willr27.blocklings.attribute.attributes;

import com.willr27.blocklings.attribute.IModifier;
import com.willr27.blocklings.attribute.attributes.numbers.ModifiableFloatAttribute;
import com.willr27.blocklings.attribute.Operation;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;

import java.util.function.Supplier;

public class AveragedAttribute extends ModifiableFloatAttribute
{
    public AveragedAttribute(String id, String key, BlocklingEntity blockling, float baseValue, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        super(id, key, blockling, baseValue, displayStringValueSupplier, displayStringNameSupplier);
    }

    @Override
    public void calculate()
    {
        value = 0.0f;
        float tempBase = baseValue;
        boolean end = false;

        for (IModifier<Float> modifier : modifiers)
        {
            if (modifier.getOperation() == Operation.ADD)
            {
                value += modifier.getValue();
            }
            else if (modifier.getOperation() == Operation.MULTIPLY_BASE)
            {
                tempBase *= modifier.getValue();
            }
            else if (modifier.getOperation() == Operation.MULTIPLY_TOTAL)
            {
                if (!end)
                {
                    value += tempBase;
                    end = true;
                }

                value *= modifier.getValue();
            }
        }

        if (!end)
        {
            value += tempBase;
        }

        value /= modifiers.size();

        callUpdateCallbacks();
    }
}
