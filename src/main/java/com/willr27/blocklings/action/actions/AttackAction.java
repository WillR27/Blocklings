package com.willr27.blocklings.action.actions;

import com.willr27.blocklings.action.BlocklingActions;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * An action used when a blockling attacks a target.
 */
public class AttackAction extends KnownTargetAction
{
    /**
     * The action for the left hand.
     */
    @Nonnull
    private final KnownTargetAction leftHandAction;

    /**
     * The action for the right hand.
     */
    @Nonnull
    private final KnownTargetAction rightHandAction;

    /**
     * Which hand was most recently used to attack.
     */
    private BlocklingHand recentHand = BlocklingHand.BOTH;

    /**
     * @param actions the blockling actions.
     * @param blockling the blockling.
     * @param key the key used to identify the action and for the underlying attribute.
     * @param targetCountSupplier the supplier used to get the target count.
     * @param handTargetCountSupplier the supplier used to get the target count for the hands.
     */
    public AttackAction(@Nonnull BlocklingActions actions, @Nonnull BlocklingEntity blockling, @Nonnull String key, @Nonnull Supplier<Float> targetCountSupplier, @Nonnull Supplier<Float> handTargetCountSupplier)
    {
        super(blockling, key, targetCountSupplier);

        // Cap the animation to a minimum of 10 ticks.
        Supplier<Float> supplier = () -> handTargetCountSupplier.get() < 10.0f ? handTargetCountSupplier.get() : 10.0f;
        leftHandAction = actions.createAction(key + "_left_hand", supplier);
        rightHandAction = actions.createAction(key + "_right_hand", supplier);
    }

    /**
     * Starts the action if it is not already being performed.
     *
     * @param hand the hand to try start the action for.
     * @return true if the action was started.
     */
    public boolean tryStart(BlocklingHand hand)
    {
        if (super.tryStart())
        {
            start(hand);

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Starts the action whether it's running or not.
     * Also sets the recent hand to the one given.
     *
     * @param hand the hand to start the action for.
     */
    public void start(BlocklingHand hand)
    {
        super.start();

        if (hand == BlocklingHand.BOTH)
        {
            leftHandAction.start();
            rightHandAction.start();
        }
        else if (hand == BlocklingHand.OFF)
        {
            leftHandAction.start();
        }
        else if (hand == BlocklingHand.MAIN)
        {
            rightHandAction.start();
        }

        recentHand = hand;
    }

    @Override
    public void start()
    {
        super.start();

        start(BlocklingHand.BOTH);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (leftHandAction.isRunning() && rightHandAction.isRunning())
        {
            recentHand = BlocklingHand.BOTH;
        }
        else if (leftHandAction.isRunning())
        {
            recentHand = BlocklingHand.OFF;
        }
        else if (rightHandAction.isRunning())
        {
            recentHand = BlocklingHand.MAIN;
        }
    }

    @Override
    public void stop()
    {
        super.stop();

        leftHandAction.stop();
        rightHandAction.stop();
    }

    /**
     * @param hand the hand to check.
     * @return true if the given hand's action is running.
     */
    public boolean isRunning(BlocklingHand hand)
    {
        if (hand == BlocklingHand.OFF)
        {
            return leftHandAction.isRunning();
        }
        else
        {
            return rightHandAction.isRunning();
        }
    }

    /**
     * @param hand the hand to check.
     * @return true if the given hand's action has finished.
     */
    public boolean isFinished(BlocklingHand hand)
    {
        if (hand == BlocklingHand.OFF)
        {
            return leftHandAction.isFinished();
        }
        else
        {
            return rightHandAction.isFinished();
        }
    }

    /**
     * @return the percent through the current hand's action.
     */
    public float percentThroughHandAction()
    {
        if (leftHandAction.isRunning())
        {
            return leftHandAction.percentThroughAction();
        }
        else
        {
            return rightHandAction.percentThroughAction();
        }
    }

    /**
     * @param targetCount the target count.
     * @return the percent through the current hand's action.
     */
    public float percentThroughHandAction(float targetCount)
    {
        if (leftHandAction.isRunning())
        {
            return leftHandAction.percentThroughAction(targetCount);
        }
        else
        {
            return rightHandAction.percentThroughAction(targetCount);
        }
    }

    /**
     * @return the most recent hand used to attack.
     */
    @Nonnull
    public BlocklingHand getRecentHand()
    {
        return recentHand;
    }
}
