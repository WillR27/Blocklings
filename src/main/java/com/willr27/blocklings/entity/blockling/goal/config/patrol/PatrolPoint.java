package com.willr27.blocklings.entity.blockling.goal.config.patrol;

import com.willr27.blocklings.util.IReadWriteNBT;
import com.willr27.blocklings.util.ISyncable;
import com.willr27.blocklings.util.Version;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a point in a patrol path.
 */
public class PatrolPoint implements IReadWriteNBT, ISyncable
{
    /**
     * The list that this point belongs to.
     */
    @Nullable
    private OrderedPatrolPointList patrolPointList;

    /**
     * The x coordinate of the point.
     */
    @Nullable
    private Integer x = null;

    /**
     * The y coordinate of the point.
     */
    @Nullable
    private Integer y = null;

    /**
     * The z coordinate of the point.
     */
    @Nullable
    private Integer z = null;

    /**
     * The time in ticks that the blockling should wait at this point.
     */
    private int waitTime = 20;

    @Nonnull
    @Override
    public CompoundNBT writeToNBT(@Nonnull CompoundNBT tag)
    {
        if (x != null) tag.putInt("x", x);
        if (y != null) tag.putInt("y", y);
        if (z != null) tag.putInt("z", z);
        tag.putInt("wait_time", waitTime);

        return tag;
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT tag, @Nonnull Version tagVersion)
    {
        x = tag.contains("x") ? tag.getInt("x") : null;
        y = tag.contains("y") ? tag.getInt("y") : null;
        z = tag.contains("z") ? tag.getInt("z") : null;
        waitTime = tag.getInt("wait_time");
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        buf.writeBoolean(x != null);
        if (x != null) buf.writeInt(x);
        buf.writeBoolean(y != null);
        if (y != null) buf.writeInt(y);
        buf.writeBoolean(z != null);
        if (z != null) buf.writeInt(z);
        buf.writeInt(waitTime);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        x = buf.readBoolean() ? buf.readInt() : null;
        y = buf.readBoolean() ? buf.readInt() : null;
        z = buf.readBoolean() ? buf.readInt() : null;
        waitTime = buf.readInt();
    }

    /**
     * Sets this point to the values of another point.
     *
     * @param patrolPoint the point to copy.
     */
    public void set(@Nonnull PatrolPoint patrolPoint)
    {
        patrolPointList = patrolPoint.patrolPointList;
        x = patrolPoint.x;
        y = patrolPoint.y;
        z = patrolPoint.z;
        waitTime = patrolPoint.waitTime;
    }

    /**
     * @return whether this point has been configured.
     */
    public boolean isConfigured()
    {
        return x != null && y != null && z != null;
    }

    /**
     * @return whether the given {@link BlockPos} matches the coordinates of this point.
     */
    public boolean equals(@Nonnull BlockPos blockPos)
    {
        return x != null && y != null && z != null && x == blockPos.getX() && y == blockPos.getY() && z == blockPos.getZ();
    }

    /**
     * @return the {@link BlockPos} representation of this point.
     */
    @Nullable
    public BlockPos asBlockPos()
    {
        return (x != null && y != null && z != null) ? new BlockPos(x, y, z) : null;
    }

    /**
     * @return the list that this point belongs to.
     */
    @Nullable
    public OrderedPatrolPointList getPatrolPointList()
    {
        return patrolPointList;
    }

    /**
     * Sets the list that this point belongs to.
     *
     * @param patrolPointList the list that this point belongs to.
     */
    public void setPatrolPointList(@Nullable OrderedPatrolPointList patrolPointList)
    {
        this.patrolPointList = patrolPointList;
    }

    /**
     * @return the x coordinate of the point.
     */
    @Nullable
    public Integer getX()
    {
        return x;
    }

    /**
     * Sets the x coordinate of the point.
     *
     * @param x the x coordinate of the point.
     */
    public void setX(@Nullable Integer x)
    {
        this.x = x;

        if (getPatrolPointList() != null)
        {
            getPatrolPointList().onDataChanged(this);
        }
    }

    /**
     * @return the y coordinate of the point.
     */
    @Nullable
    public Integer getY()
    {
        return y;
    }

    /**
     * Sets the y coordinate of the point.
     *
     * @param y the y coordinate of the point.
     */
    public void setY(@Nullable Integer y)
    {
        this.y = y;

        if (getPatrolPointList() != null)
        {
            getPatrolPointList().onDataChanged(this);
        }
    }

    /**
     * @return the z coordinate of the point.
     */
    @Nullable
    public Integer getZ()
    {
        return z;
    }

    /**
     * Sets the z coordinate of the point.
     *
     * @param z the z coordinate of the point.
     */
    public void setZ(@Nullable Integer z)
    {
        this.z = z;

        if (getPatrolPointList() != null)
        {
            getPatrolPointList().onDataChanged(this);
        }
    }

    /**
     * @return the time in ticks that the blockling should wait at this point.
     */
    public int getWaitTime()
    {
        return waitTime;
    }

    /**
     * Sets the time in ticks that the blockling should wait at this point.
     *
     * @param waitTime the time in ticks that the blockling should wait at this point.
     */
    public void setWaitTime(int waitTime)
    {
        this.waitTime = waitTime;

        if (getPatrolPointList() != null)
        {
            getPatrolPointList().onDataChanged(this);
        }
    }

    @Override
    public String toString()
    {
        return "PatrolPoint{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", waitTime=" + waitTime +
                '}';
    }
}
