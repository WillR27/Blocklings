package com.willr27.blocklings.entity.entities.blockling.action.actions;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.action.Action;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class KnownTargetAction extends Action
{
    @Nonnull
    protected final Supplier<Float> targetCountSupplier;

    protected boolean isFinished = false;

    public KnownTargetAction(BlocklingEntity blockling, String key, @Nonnull Supplier<Float> targetTicksSupplier)
    {
        super(blockling, key);
        this.targetCountSupplier = targetTicksSupplier;
    }

    @Override
    public boolean tryStart()
    {
        if (isRunning() || isFinished)
        {
            return false;
        }
        else
        {
            start();

            return true;
        }
    }

    @Override
    public void tick()
    {
        tick(1.0f);
    }

    @Override
    public void tick(float increment)
    {
        super.tick(increment);

        if (isFinished)
        {
            isFinished = false;
        }

        if (isRunning())
        {
            if (count() > targetCountSupplier.get())
            {
                stop();

                isFinished = true;
            }
        }
    }

    public boolean isFinished()
    {
        return isFinished;
    }

    public float percentThroughAction()
    {
        return percentThroughAction(0);
    }

    public float percentThroughActionSq()
    {
        return percentThroughActionSq(0);
    }

    public float percentThroughAction(int tickOffset)
    {
        if (count() + tickOffset < 0)
        {
            return 1.0f;
        }

        return (float) (count() + tickOffset) / (float) targetCountSupplier.get();
    }

    public float percentThroughActionSq(int tickOffset)
    {
        if (count() + tickOffset < 0)
        {
            return 1.0f;
        }

        return (float) ((count() + tickOffset) * (count() + tickOffset)) / (float) (targetCountSupplier.get() * targetCountSupplier.get());
    }
}
