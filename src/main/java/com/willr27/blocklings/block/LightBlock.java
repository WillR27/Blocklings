package com.willr27.blocklings.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * An invisible block that emits light.
 * Used by blocklings to emit light.
 */
public class LightBlock extends Block
{
    /**
     * Default constructor.
     */
    public LightBlock()
    {
        super(Block.Properties.of(new Material.Builder(MaterialColor.NONE)
                .replaceable()
                .nonSolid()
                .build())
                .noDrops()
                .noOcclusion()
                .noCollission()
                .lightLevel((blockState) -> 15));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockReader, BlockPos blockPos, CollisionContext selectionContext)
    {
        return Block.box(0, 0, 0, 0, 0, 0);
    }
}
