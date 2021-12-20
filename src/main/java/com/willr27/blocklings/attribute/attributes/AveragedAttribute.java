package com.willr27.blocklings.attribute.attributes;

import com.willr27.blocklings.attribute.attributes.numbers.ModifiableFloatAttribute;
import com.willr27.blocklings.attribute.modifier.AttributeModifier;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;

public class AveragedAttribute extends ModifiableFloatAttribute
{
    public AveragedAttribute(String id, String key, BlocklingEntity blockling, float baseValue)
    {
        super(id, key, blockling, baseValue);
    }

    @Override
    public void calculate()
    {
        value = 0.0f;
        float tempBase = baseValue;
        boolean end = false;

        for (AttributeModifier<Float> modifier : modifiers)
        {
            if (modifier.operation == AttributeModifier.Operation.ADD)
            {
                value += modifier.value;
            }
            else if (modifier.operation == AttributeModifier.Operation.MULTIPLY_BASE)
            {
                tempBase *= modifier.value;
            }
            else if (modifier.operation == AttributeModifier.Operation.MULTIPLY_TOTAL)
            {
                if (!end)
                {
                    value += tempBase;
                    end = true;
                }

                value *= modifier.value;
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
