package com.willr27.blocklings.entity.entities.blockling;

public enum BlocklingHand
{
    NONE,
    OFF,
    MAIN,
    BOTH;

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
