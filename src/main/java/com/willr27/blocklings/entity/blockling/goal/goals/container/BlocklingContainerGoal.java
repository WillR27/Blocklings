package com.willr27.blocklings.entity.blockling.goal.goals.container;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.BlocklingTargetGoal;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

/**
 * A base class for handling goals that involve moving to and interacting with containers.
 */
public abstract class BlocklingContainerGoal extends BlocklingTargetGoal<TileEntity>
{
    /**
     * @param id        the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks     the blockling tasks.
     */
    public BlocklingContainerGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    /**
     * @return returns the given tile entity as an inventory, null if the cast fails.
     */
    @Nullable
    public IInventory tileEntityAsInventory(@Nonnull TileEntity tileEntity)
    {
        return (IInventory) tileEntity;
    }

    /**
     * @return returns the target tile entity as an inventory, null if the cast fails.
     */
    @Nullable
    public IInventory targetAsInventory()
    {
        return tileEntityAsInventory(getTarget());
    }
}
