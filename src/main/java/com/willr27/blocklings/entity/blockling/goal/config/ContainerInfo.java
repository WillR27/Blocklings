package com.willr27.blocklings.entity.blockling.goal.config;

import com.willr27.blocklings.util.Version;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A class used to store information about a container.
 */
public class ContainerInfo
{
    /**
     * The position of the container.
     */
    @Nonnull
    private BlockPos blockPos = BlockPos.ZERO;

    /**
     * The block of the container.
     */
    @Nonnull
    private Block block = Blocks.AIR;

    /**
     * The list of sides to interact with in priority order.
     */
    @Nonnull
    private List<Direction> sides = new ArrayList<>();

    /**
     */
    public ContainerInfo()
    {
    }

    /**
     * @param containerInfo the container info to copy.
     */
    public ContainerInfo(@Nonnull ContainerInfo containerInfo)
    {
        this(containerInfo.blockPos, containerInfo.block, containerInfo.sides);
    }

    /**
     * @param blockPos the position of the container.
     * @param block the block of the container.
     * @param sides the list of sides to interact with in priority order.
     */
    public ContainerInfo(@Nonnull BlockPos blockPos, @Nonnull Block block, @Nonnull List<Direction> sides)
    {
        this.blockPos = blockPos;
        this.block = block;
        this.sides.addAll(sides);
    }

    /**
     * Writes the container info to a compound tag.
     *
     * @return the compound tag.
     */
    @Nonnull
    public CompoundNBT writeToNBT()
    {
        CompoundNBT compound = new CompoundNBT();
        compound.putInt("x", getX());
        compound.putInt("y", getY());
        compound.putInt("z", getZ());
        compound.putString("block", Registry.BLOCK.getKey(block).toString());
        compound.putInt("sides", sides.size());

        for (int i = 0; i < sides.size(); i++)
        {
            compound.putInt("side" + i, sides.get(i).ordinal());
        }

        return compound;
    }

    /**
     * Reads a container info from a containerInfoTag tag.
     *
     * @param containerInfoTag the container info tag.
     * @param tagVersion the version of the tag.
     */
    @Nonnull
    public void readFromNBT(@Nonnull CompoundNBT containerInfoTag, @Nonnull Version tagVersion)
    {
        setBlockPos(new BlockPos(containerInfoTag.getInt("x"), containerInfoTag.getInt("y"), containerInfoTag.getInt("z")));
        setBlock(Registry.BLOCK.get(new ResourceLocation(containerInfoTag.getString("block"))));
        int size = containerInfoTag.getInt("sides");

        for (int i = 0; i < size; i++)
        {
            getSides().add(Direction.values()[containerInfoTag.getInt("side" + i)]);
        }
    }

    /**
     * Writes the container info to a buffer.
     *
     * @param buf the buffer to write to.
     */
    public void encode(@Nonnull PacketBuffer buf)
    {
        buf.writeBlockPos(blockPos);
        buf.writeRegistryId(block);
        buf.writeVarInt(sides.size());

        for (Direction side : sides)
        {
            buf.writeEnum(side);
        }
    }

    /**
     * Reads a container info from a buffer.
     *
     * @param buf the buffer to read from.
     * @return the container info.
     */
    @Nonnull
    public void decode(@Nonnull PacketBuffer buf)
    {
        setBlockPos(buf.readBlockPos());
        setBlock(buf.readRegistryId());
        int size = buf.readVarInt();

        for (int i = 0; i < size; i++)
        {
            getSides().add(buf.readEnum(Direction.class));
        }
    }

    /**
     * @return whether the container info is configured with a block and sides to interact with.
     */
    public boolean isConfigured()
    {
        return getBlock() != Blocks.AIR && !getSides().isEmpty();
    }

    /**
     * @return the x coordinate of the container.
     */
    public int getX()
    {
        return blockPos.getX();
    }

    /**
     * Sets the x coordinate of the container.
     */
    public void setX(int x)
    {
        this.blockPos = new BlockPos(x, blockPos.getY(), blockPos.getZ());
    }

    /**
     * @return the y coordinate of the container.
     */
    public int getY()
    {
        return blockPos.getY();
    }

    /**
     * Sets the y coordinate of the container.
     */
    public void setY(int y)
    {
        this.blockPos = new BlockPos(blockPos.getX(), y, blockPos.getZ());
    }

    /**
     * @return the z coordinate of the container.
     */
    public int getZ()
    {
        return blockPos.getZ();
    }

    /**
     * Sets the z coordinate of the container.
     */
    public void setZ(int z)
    {
        this.blockPos = new BlockPos(blockPos.getX(), blockPos.getY(), z);
    }

    /**
     * @return the position of the container.
     */
    @Nonnull
    public BlockPos getBlockPos()
    {
        return blockPos;
    }

    /**
     * Sets the position of the container.
     */
    public void setBlockPos(@Nonnull BlockPos blockPos)
    {
        this.blockPos = blockPos;
    }

    /**
     * @return the block of the container.
     */
    @Nonnull
    public Block getBlock()
    {
        return block;
    }

    /**
     * Sets the block of the container.
     */
    public void setBlock(@Nonnull Block block)
    {
        this.block = block;
    }

    /**
     * @return the list of sides to interact with in priority order.
     */
    @Nonnull
    public List<Direction> getSides()
    {
        return sides;
    }

    /**
     * Sets the list of sides to interact with in priority order.
     */
    public void setSides(@Nonnull List<Direction> sides)
    {
        this.sides = sides;
    }
}
