package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.control.controls.config.BlockSideSelectionControl;
import com.willr27.blocklings.client.gui.screen.BlocklingsScreen;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;

public class TestScreen extends BlocklingsScreen
{
    /**
     * @param blockling the blockling associated with the screen.
     */
    public TestScreen(@Nonnull BlocklingEntity blockling)
    {
        super(blockling);

        BlockSideSelectionControl blockSideSelectionControl = new BlockSideSelectionControl();
        blockSideSelectionControl.setParent(screenControl);
        blockSideSelectionControl.setBlock(Blocks.FURNACE);
        blockSideSelectionControl.setBackgroundColour(0xffffffff);
        blockSideSelectionControl.setWidth(100);
        blockSideSelectionControl.setHeight(100);
        blockSideSelectionControl.setBlockScale(0.5f);
        blockSideSelectionControl.setRenderZ(50.0);
        blockSideSelectionControl.setHorizontalAlignment(0.5);
        blockSideSelectionControl.setVerticalAlignment(0.5);
    }
}
