package com.willr27.blocklings.attribute;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class ModifiableAttribute<T> extends Attribute<T> implements IModifiable<T>
{
    protected final List<IModifier<T>> modifiers = new ArrayList<>();

    protected T baseValue;
    protected T value;

    public ModifiableAttribute(String id, String key, BlocklingEntity blockling, T value, Supplier<String> displayStringValueSupplier, Supplier<String> displayStringNameSupplier)
    {
        super(id, key, blockling, displayStringValueSupplier, displayStringNameSupplier);
        this.baseValue = value;
        this.value = value;
    }

    @Override
    public T getValue()
    {
        return value;
    }

    @Override
    public T getBaseValue()
    {
        return baseValue;
    }

    @Override
    public List<IModifier<T>> getModifiers()
    {
        return new ArrayList<>(modifiers);
    }

    @Override
    public List<IModifier<T>> getEnabledModifiers()
    {
        return modifiers.stream().filter(IModifier::isEnabled).collect(Collectors.toList());
    }

    @Override
    public IModifier<T> findModifier(int index)
    {
        return modifiers.get(index);
    }

    @Override
    public int indexOf(IModifier<T> modifier)
    {
        return modifiers.indexOf(modifier);
    }

    @Override
    public IModifier<T> getModifier(int index)
    {
        return modifiers.get(index);
    }

    @Override
    public void addModifier(IModifier<T> modifier)
    {
        // Don't add if modifier is already applied
        if (modifiers.contains(modifier))
        {
            return;
        }

        // Add total multiplications last
        if (modifier.getOperation() != Operation.MULTIPLY_TOTAL)
        {
            boolean inserted = false;

            for (IModifier<T> existingModifier : modifiers)
            {
                if (existingModifier.getOperation() == Operation.MULTIPLY_TOTAL)
                {
                    modifiers.add(modifiers.indexOf(existingModifier), modifier);
                    inserted = true;

                    break;
                }
            }

            if (!inserted)
            {
                modifiers.add(modifier);
            }
        }
        else
        {
            modifiers.add(modifier);
        }

        // Calculate new value
        calculate();
    }

    @Override
    public void removeModifier(IModifier<T> modifier)
    {
        // Remove if exists
        modifiers.remove(modifier);

        // Recalculate value
        calculate();
    }

    @Override
    public Supplier<String> getDisplayStringValueSupplier()
    {
        return displayStringValueSupplier;
    }

    @Override
    public Supplier<String> getDisplayStringNameSupplier()
    {
        return displayStringNameSupplier;
    }

    @Override
    public void setIsEnabled(boolean isEnabled, boolean sync)
    {
        super.setIsEnabled(isEnabled, sync);

        calculate();
    }
}
