package com.willr27.blocklings.entity.blockling;

import javax.annotation.Nonnull;

/**
 * Enum used to identify the hands being used for actions and inventory slots.
 */
public enum BlocklingHand
{
    NONE,
    OFF,
    MAIN,
    BOTH;

    /**
     * @param main whether to include the main hand.
     * @param off whether to include the off hand.
     * @return the hand equivalent to the given booleans.
     */
    @Nonnull
    public static BlocklingHand fromBooleans(boolean main, boolean off)
    {
        if (main && off)
        {
            return BOTH;
        }
        else if (main)
        {
            return MAIN;
        }
        else if (off)
        {
            return OFF;
        }

        return NONE;
    }
}
