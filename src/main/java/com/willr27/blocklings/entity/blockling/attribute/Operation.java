package com.willr27.blocklings.entity.blockling.attribute;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import javax.annotation.Nonnull;

/**
 * The operation performed by an attribute modifier.
 */
public enum Operation
{
    ADD,
    MULTIPLY_BASE,
    MULTIPLY_TOTAL;

    /**
     * @return the vanilla equivalent of the operation.
     */
    @Nonnull
    public static AttributeModifier.Operation vanillaOperation(@Nonnull Operation operation)
    {
        switch (operation)
        {
            case MULTIPLY_BASE:
                return AttributeModifier.Operation.MULTIPLY_BASE;
            case MULTIPLY_TOTAL:
                return AttributeModifier.Operation.MULTIPLY_TOTAL;
            default:
                return AttributeModifier.Operation.ADDITION;
        }
    }
}
