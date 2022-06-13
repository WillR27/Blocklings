package com.willr27.blocklings.entity.blockling.attribute;

import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An attribute that can have attribute modifiers applied to it.
 *
 * @param <T> the type of the value of the attribute.
 */
public interface IModifiable<T>
{
    /**
     * Calculates the attribute using the associated modifiers and base value.
     */
    void calculate();

    /**
     * @return the underlying attribute value.
     */
    T getValue();

    /**
     * @param format the format string.
     * @return the attribute value formatted into the given string.
     */
    @Nonnull
    String formatValue(@Nonnull String format);

    /**
     * @return the underlying attribute base value.
     */
    T getBaseValue();

    /**
     * Increments the base value by the given amount.
     * Syncs to the client/server.
     *
     * @param amount the amount to increment the base value by.
     */
    default void incrementBaseValue(T amount)
    {
        incrementBaseValue(amount, true);
    }

    /**
     * Increments the base value by the given amount.
     * Syncs to the client/server if sync is true.
     *
     * @param amount the amount to increment the base value by.
     * @param sync whether to sync to the client/server.
     */
    void incrementBaseValue(T amount, boolean sync);

    /**
     * Sets the base value to the given value.
     * Syncs to the client/server.
     *
     * @param baseValue the value to set the base value to.
     */
    default void setBaseValue(T baseValue)
    {
        setBaseValue(baseValue, true);
    }

    /**
     * Sets the base value to the given value.
     * Syncs to the client/server if sync is true.
     *
     * @param baseValue the value to set the base value to.
     * @param sync whether to sync to the client/server.
     */
    void setBaseValue(T baseValue, boolean sync);

    /**
     * @param format the format string.
     * @return the attribute base value formatted into the given string.
     */
    @Nonnull
    default String formatBaseValue(@Nonnull String format)
    {
        return String.format(format, getBaseValue());
    }

    /**
     * @return the list of modifiers applied to the attribute.
     */
    @Nonnull
    List<IModifier<T>> getModifiers();

    /**
     * @return the list of enabled modifiers applied to the attribute.
     */
    @Nonnull
    List<IModifier<T>> getEnabledModifiers();

    /**
     * @param index the index to retrieve from.
     * @return the modifier at the given index of the modifiers list.
     */
    @Nonnull
    IModifier<T> getModifier(int index);

    /**
     * @param modifier the modifier to find the index of.
     * @return the index of the modifier in the modifiers list.
     */
    int indexOf(@Nonnull IModifier<T> modifier);

    /**
     * Adds the given modifier to the list of modifiers on the attribute.
     *
     * @param modifier the modifier to add.
     */
    void addModifier(@Nonnull IModifier<T> modifier);

    /**
     * Adds the given modifiers to the list of modifiers on the attribute.
     *
     * @param modifiers the modifiers to add.
     */
    void addModifiers(@Nonnull IModifier<T>... modifiers);

    /**
     * Removes the given modifier from the list of modifiers on the attribute if it exists.
     *
     * @param modifier the modifier to remove.
     */
    void removeModifier(@Nonnull IModifier<T> modifier);

    /**
     * @return the function used to provide the string representation of the value.
     */
    @Nonnull
    Function<T, String> getDisplayStringValueFunction();

    /**
     * @return the supplier used to provide the string representation of display name.
     */
    @Nonnull
    Supplier<String> getDisplayStringNameSupplier();

    /**
     * @param key the key to prefix to the attribute key.
     * @param objects the objects to be passed on to the string format.
     * @return a translation text component for the attribute.
     */
    @Nonnull
    TranslationTextComponent createTranslation(@Nonnull String key, @Nonnull Object... objects);
}
