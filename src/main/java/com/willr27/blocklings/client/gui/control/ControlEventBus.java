package com.willr27.blocklings.client.gui.control;

import com.willr27.blocklings.util.event.EventBus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * An event bus for controls that will forward events to the appropriate subscribers.
 */
@OnlyIn(Dist.CLIENT)
public class ControlEventBus extends EventBus<BaseControl>
{

}

