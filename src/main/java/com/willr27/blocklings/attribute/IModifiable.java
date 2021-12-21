package com.willr27.blocklings.attribute;

import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public interface IModifiable<T>
{
    void calculate();

    T getValue();

    default String formatValue(String format)
    {
        return String.format(format, getValue());
    }

    T getBaseValue();

    default void incBaseValue(T amount)
    {
        incBaseValue(amount, true);
    }

    void incBaseValue(T amount, boolean sync);

    default void setBaseValue(T baseValue)
    {
        setBaseValue(baseValue, true);
    }

    void setBaseValue(T baseValue, boolean sync);

    default String formatBaseValue(String format)
    {
        return String.format(format, getBaseValue());
    }

    List<IModifier<T>> getModifiers();

    IModifier<T> findModifier(int index);

    int indexOf(IModifier<T> modifier);

    IModifier<T> getModifier(int index);

    void addModifier(IModifier<T> modifier);

    void removeModifier(IModifier<T> modifier);

    TranslationTextComponent createTranslation(String key, Object... objects);
}
