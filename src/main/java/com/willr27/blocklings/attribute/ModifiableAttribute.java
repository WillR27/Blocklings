package com.willr27.blocklings.attribute;

import com.willr27.blocklings.attribute.modifier.AttributeModifier;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ModifiableAttribute<T> extends Attribute<T>
{
    protected final List<AttributeModifier<T>> modifiers = new ArrayList<>();

    public ModifiableAttribute(String id, String key, BlocklingEntity blockling)
    {
        super(id, key, blockling);
    }

    public abstract void calculate();

    public abstract T getBaseValue();

    public abstract void incBaseValue(T amount);

    public abstract void incBaseValue(T amount, boolean sync);

    public abstract void setBaseValue(T baseValue);

    public abstract void setBaseValue(T baseValue, boolean sync);

    public AttributeModifier<T> findModifier(int index)
    {
        return modifiers.get(index);
    }

    public int indexOf(AttributeModifier<T> modifier)
    {
        return modifiers.indexOf(modifier);
    }

    public AttributeModifier<T> getModifier(int index)
    {
        return modifiers.get(index);
    }

    public void addModifier(AttributeModifier<T> modifier)
    {
        // Don't add if modifier is already applied
        if (modifiers.contains(modifier))
        {
            return;
        }

        // Add total multiplications last
        if (modifier.operation != AttributeModifier.Operation.MULTIPLY_TOTAL)
        {
            modifiers.add(0, modifier);
        }
        else
        {
            modifiers.add(modifier);
        }

        // Calculate new value
        calculate();
    }

    public void removeModifier(AttributeModifier<T> modifier)
    {
        // Remove if exists
        modifiers.remove(modifier);

        // Recalculate value
        calculate();
    }
}
