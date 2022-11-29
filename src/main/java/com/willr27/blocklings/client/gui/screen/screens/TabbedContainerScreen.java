package com.willr27.blocklings.client.gui.screen.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.controls.TabbedControl;
import com.willr27.blocklings.client.gui.screen.BlocklingsContainerScreen;
import com.willr27.blocklings.client.gui.screen.BlocklingsScreen;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A screen that displays some container content and tabs.
 */
@OnlyIn(Dist.CLIENT)
public class TabbedContainerScreen<T extends Container> extends BlocklingsContainerScreen<T>
{
    /**
     * The control that handles rendering the tabs and the content background.
     */
    @Nonnull
    protected final TabbedControl tabbedControl;

    /**
     * @param blockling the blockling.
     * @param tab the associated tab.
     * @param container the container.
     */
    public TabbedContainerScreen(@Nonnull BlocklingEntity blockling, @Nonnull TabbedControl.Tab tab, @Nonnull T container)
    {
        super(blockling, container);

        tabbedControl = new TabbedControl(blockling, tab);
    }

    @Override
    protected void init()
    {
        super.init();

        tabbedControl.resetChildren();
        tabbedControl.setParent(screenControl);
        tabbedControl.setPercentX(0.5f);
        tabbedControl.setPercentY(0.5f);
        // Cast the coords to ints as otherwise container slots will be misaligned.
        tabbedControl.setX((int) tabbedControl.getX());
        tabbedControl.setY((int) tabbedControl.getY());
        tabbedControl.moveY(-5);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
