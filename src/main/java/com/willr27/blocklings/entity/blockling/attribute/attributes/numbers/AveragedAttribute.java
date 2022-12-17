package com.willr27.blocklings.entity.blockling.attribute.attributes.numbers;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.attribute.IModifier;
import com.willr27.blocklings.entity.blockling.attribute.Operation;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An attribute where the final value is divided by the number of modifiers on calculation.
 */
public class AveragedAttribute extends ModifiableFloatAttribute
{
    /**
     * @param id the id of the attribute.
     * @param key the key used to identify the attribute (for things like translation text components).
     * @param blockling the blockling.
     * @param initialBaseValue the initial base value.
     * @param displayStringValueFunction the function used to provide the string representation of the value.
     * @param displayStringNameSupplier the supplier used to provide the string representation of display name.
     * @param isEnabled whether the attribute is currently enabled.
     * @param modifiers the initial list of modifiers associated with the attribute.
     */
    public AveragedAttribute(String id, String key, BlocklingEntity blockling, float initialBaseValue, Function<Float, String> displayStringValueFunction, Supplier<String> displayStringNameSupplier, boolean isEnabled, @Nonnull IModifier<Float>... modifiers)
    {
        super(id, key, blockling, initialBaseValue, displayStringValueFunction, displayStringNameSupplier, isEnabled, modifiers);
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

        value /= (int) modifiers.stream().filter(modifier -> modifier.isEnabled() && modifier.isEffective()).count();

        onValueChanged();
    }
}
