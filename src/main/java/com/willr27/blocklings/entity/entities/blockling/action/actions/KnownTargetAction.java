package com.willr27.blocklings.entity.entities.blockling.action.actions;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.action.Action;

import java.util.function.Supplier;

public class KnownTargetAction extends Action
{
    protected final Supplier<Integer> targetTicksSupplier;

    protected boolean isFinished = false;

    public KnownTargetAction(BlocklingEntity blockling, String key, Supplier<Integer> targetTicksSupplier)
    {
        super(blockling, key);
        this.targetTicksSupplier = targetTicksSupplier;
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
        super.tick();

        if (isFinished)
        {
            isFinished = false;
        }

       if (isRunning())
       {
           if (elapsedTicks() > targetTicksSupplier.get())
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
        if (elapsedTicks() + tickOffset < 0)
        {
            return 1.0f;
        }

        return (float) (elapsedTicks() + tickOffset) / (float) targetTicksSupplier.get();
    }

    public float percentThroughActionSq(int tickOffset)
    {
        if (elapsedTicks() + tickOffset < 0)
        {
            return 1.0f;
        }

        return (float) ((elapsedTicks() + tickOffset) * (elapsedTicks() + tickOffset)) / (float) (targetTicksSupplier.get() * targetTicksSupplier.get());
    }
}
