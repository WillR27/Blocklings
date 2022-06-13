package com.willr27.blocklings.entity.blockling.attribute;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
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
     * @param displayStringValueFunction the function used to provide the string representation of the value.
     * @param displayStringNameSupplier the supplier used to provide the string representation of display name.
     * @param isEnabled whether the attribute is currently enabled.
     * @param modifiers the initial list of modifiers associated with the attribute.
     */
    public ModifiableAttribute(@Nonnull String id, @Nonnull String key, @Nonnull BlocklingEntity blockling, T initialBaseValue, @Nullable Function<T, String> displayStringValueFunction, @Nullable Supplier<String> displayStringNameSupplier, boolean isEnabled, @Nonnull IModifier<T>... modifiers)
    {
        super(id, key, blockling, displayStringValueFunction, displayStringNameSupplier, isEnabled);
        this.baseValue = initialBaseValue;
        this.value = initialBaseValue;
        this.modifiers.addAll(Arrays.asList(modifiers));
        this.modifiers.forEach(modifier -> modifier.getAttributes().add(this));
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
        // Don't add if modifier is already applied.
        if (modifiers.contains(modifier))
        {
            Blocklings.LOGGER.warn("Tried to add modifier \"" + modifier.getDisplayStringNameSupplier().get() + "\" that is already applied to attribute \"" + getDisplayStringNameSupplier().get() + "\".");

            return;
        }

        // Add this attribute to the list of attributes the modifier is associated with.
        modifier.getAttributes().add(this);

        // Add total multiplications last.
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

        // Calculate new value.
        calculate();
    }

    @Override
    public void addModifiers(@Nonnull IModifier<T>... modifiers)
    {
        Arrays.stream(modifiers).forEach(this::addModifier);
    }

    @Override
    public void removeModifier(@Nonnull IModifier<T> modifier)
    {
        modifiers.remove(modifier);
        modifier.getAttributes().remove(this);

        // Recalculate value.
        calculate();
    }

    @Override
    @Nonnull
    public Function<T, String> getDisplayStringValueFunction()
    {
        return displayStringValueFunction;
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
