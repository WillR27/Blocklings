package com.willr27.blocklings.entity.blockling.goal;

import com.willr27.blocklings.util.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a 3x3 area around a block.
 */
public class BlockChunk
{
    /**
     * The block pos in the center of the 3x3 area.
     */
    @Nonnull
    private BlockPos centerPos;

    /**
     *
     */
    @Nonnull
    private World world;

    /**
     * The map of the positions and the block states at the time of construction.
     */
    @Nonnull
    private Map<BlockPos, BlockState> blocks = new HashMap<>();

    /**
     * @param centerPos the block pos in the center of the 3x3 area.
     * @param world the world the block is in.
     */
    public BlockChunk(@Nonnull BlockPos centerPos, @Nonnull World world)
    {
        this.centerPos = centerPos;
        this.world = world;

        blocks.put(centerPos, world.getBlockState(centerPos));

        for (BlockPos surroundingPos : BlockUtil.getSurroundingBlockPositions(centerPos))
        {
            blocks.put(surroundingPos, world.getBlockState(surroundingPos));
        }
    }

    /**
     * NOTE: Currently just checks for the type of block changing.
     *
     * @return true if the block or surrounding blocks have changed.
     */
    public boolean hasChanged()
    {
        for (Map.Entry<BlockPos, BlockState> entry : blocks.entrySet())
        {
            if (world.getBlockState(entry.getKey()).getBlock() != entry.getValue().getBlock())
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return centerPos.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return getClass() == obj.getClass() && hashCode() == obj.hashCode();
    }
}
