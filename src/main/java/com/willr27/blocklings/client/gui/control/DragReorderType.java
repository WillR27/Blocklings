package com.willr27.blocklings.client.gui.control;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The way in which contents of a panel are reordered via dragging.
 */
@OnlyIn(Dist.CLIENT)
public enum DragReorderType
{
    NONE,
//    SWAP_ON_RELEASE,
//    SWAP_ON_MOVE,
    INSERT_ON_RELEASE,
    INSERT_ON_MOVE,
}
