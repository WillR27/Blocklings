package com.willr27.blocklings.entity.blockling.action.actions;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.action.Action;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * An action where the target count is known/easy to supply.
 */
public class KnownTargetAction extends Action
{
    /**
     * The supplier used to get target count.
     */
    @Nonnull
    protected final Supplier<Float> targetCountSupplier;

    /**
     * Whether the action has finished in the last tick.
     */
    protected boolean isFinished = false;

    /**
     * @param blockling the blockling.
     * @param key the key used to identify the action and for the underlying attribute.
     * @param authority the side that has authority over the value of the action.
     * @param targetCountSupplier the supplier used to get the target count.
     */
    public KnownTargetAction(@Nonnull BlocklingEntity blockling, @Nonnull String key, @Nonnull Authority authority, @Nonnull Supplier<Float> targetCountSupplier)
    {
        super(blockling, key, authority);
        this.targetCountSupplier = targetCountSupplier;
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
            if (getCount() > targetCountSupplier.get())
            {
                stop();

                isFinished = true;

                callCallbacks();
            }
        }
    }

    /**
     * @return true if the action has finished and hasn't ticked since finishing.
     */
    public boolean isFinished()
    {
        return isFinished;
    }

    /**
     * @return the percentage towards the target count.
     */
    public float percentThroughAction()
    {
        return percentThroughAction(0);
    }

    @Override
    public float percentThroughAction(float targetCount)
    {
        if (getCount() + targetCount < 0)
        {
            return 1.0f;
        }

        return (getCount() + targetCount) / targetCountSupplier.get();
    }
}
