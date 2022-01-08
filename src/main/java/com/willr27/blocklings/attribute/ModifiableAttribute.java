package com.willr27.blocklings.attribute;

import com.willr27.blocklings.attribute.attributes.numbers.FloatAttribute;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * An attribute that can have modifiers applied to it.
 *
 * @param <T> the type of the value of the attribute.
 */
public abstract class ModifiableAttribute<T> extends Attribute<T> implements IModifiable<T>
{
    /**
     * The list of modifiers applied to the attribute.
     */
    @Nonnull
    protected final List<IModifier<T>> modifiers = new ArrayList<>();

    /**
     * The base value of the attribute.
     */
    protected T baseValue;

    /**
     * @param id the id of the attribute.
     * @param key the key used to identify the attribute (for things like translation text components).
     * @param blockling the blockling.
     * @param initialBaseValue the initial base value.
     * @param displayStringValueSupplier the supplier used to provide the string representation of the value.
     * @param displayStringNameSupplier the supplier used to provide the string representation of display name.
     */
    public ModifiableAttribute(@Nonnull String id, @Nonnull String key, @Nonnull BlocklingEntity blockling, T initialBaseValue, @Nullable Supplier<String> displayStringValueSupplier, @Nullable Supplier<String> displayStringNameSupplier)
    {
        super(id, key, blockling, displayStringValueSupplier, displayStringNameSupplier);
        this.baseValue = initialBaseValue;
        this.value = initialBaseValue;
    }

    @Override
    public T getBaseValue()
    {
        return baseValue;
    }

    @Override
    @Nonnull
    public List<IModifier<T>> getModifiers()
    {
        return new ArrayList<>(modifiers);
    }

    @Override
    @Nonnull
    public List<IModifier<T>> getEnabledModifiers()
    {
        return modifiers.stream().filter(IModifier::isEnabled).collect(Collectors.toList());
    }

    @Override
    @Nonnull
    public IModifier<T> getModifier(int index)
    {
        return modifiers.get(index);
    }

    @Override
    public int indexOf(@Nonnull IModifier<T> modifier)
    {
        return modifiers.indexOf(modifier);
    }

    @Override
    public void addModifier(@Nonnull IModifier<T> modifier)
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
    public void removeModifier(@Nonnull IModifier<T> modifier)
    {
        // Remove if exists
        modifiers.remove(modifier);

        // Recalculate value
        calculate();
    }

    @Override
    @Nonnull
    public Supplier<String> getDisplayStringValueSupplier()
    {
        return displayStringValueSupplier;
    }

    @Override
    @Nonnull
    public Supplier<String> getDisplayStringNameSupplier()
    {
        return displayStringNameSupplier;
    }

    /**
     * The message used to sync the attribute value to the client/server.
     */
    public static abstract class BaseValueMessage<T, M extends BlocklingMessage<M>> extends Attribute.ValueMessage<T, M>
    {
        /**
         * Empty constructor used ONLY for decoding.
         */
        public BaseValueMessage()
        {
            super();
        }

        /**
         * @param blockling the blockling.
         * @param index the index of the attribute.
         * @param baseValue the base value of the attribute.
         */
        public BaseValueMessage(@Nonnull BlocklingEntity blockling, int index, T baseValue)
        {
            super(blockling, index, baseValue);
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
        {
            ((ModifiableAttribute<T>) blockling.getStats().attributes.get(index)).setBaseValue(value, false);
        }
    }
}
