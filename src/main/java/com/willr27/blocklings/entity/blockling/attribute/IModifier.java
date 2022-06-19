package com.willr27.blocklings.entity.blockling.attribute;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An attribute that can be applied modifiable attributes to alter the value.
 *
 * @param <T> the type of the value of the attribute.
 */
public interface IModifier<T>
{
    /**
     * @return the underlying attribute value.
     */
    T getValue();

    /**
     * @return the attributes associated with the modifier.
     */
    @Nonnull
    List<IModifiable<T>> getAttributes();

    /**
     * @return the operation to perform on the attribute and modifier.
     */
    @Nonnull
    Operation getOperation();

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
     * @return true if the value and operation actually effect the attribute.
     */
    boolean isEffective();

    /**
     * @return true if the attribute is enabled.
     */
    boolean isEnabled();

    /**
     * Sets whether the attribute is enabled.
     * Syncs to the client/server if sync is true.
     *
     * @param isEnabled whether the attribute is enabled.
     * @param sync whether to sync to the client/server.
     */
    void setIsEnabled(boolean isEnabled, boolean sync);
}
