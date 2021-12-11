package com.willr27.blocklings.entity.entities.blockling.action.actions;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.action.BlocklingActions;

import java.util.function.Supplier;

public class AttackAction extends KnownTargetAction
{
    private final KnownTargetAction leftHandAction;
    private final KnownTargetAction rightHandAction;

    private Hand recentHand = Hand.BOTH;

    public AttackAction(BlocklingActions actions, BlocklingEntity blockling, String key, Supplier<Integer> targetTicksSupplier, Supplier<Integer> handTargetTicksSupplier)
    {
        super(blockling, key, targetTicksSupplier);

        Supplier<Integer> supplier = () -> handTargetTicksSupplier.get() < 10 ? handTargetTicksSupplier.get() : 10;
        leftHandAction = actions.createAction(blockling, key + "_left_hand", supplier);
        rightHandAction = actions.createAction(blockling, key + "_right_hand", supplier);
    }

    public boolean tryStart(Hand hand)
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

    public void start(Hand hand)
    {
        super.start();

        if (hand == Hand.LEFT || hand == Hand.BOTH)
        {
            leftHandAction.start();
        }
        else if (hand == Hand.RIGHT || hand == Hand.BOTH)
        {
            rightHandAction.start();
        }

        recentHand = hand;
    }

    @Override
    public void start()
    {
        super.start();

        start(Hand.BOTH);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (leftHandAction.isRunning() && rightHandAction.isRunning())
        {
            recentHand = Hand.BOTH;
        }
        else if (leftHandAction.isRunning())
        {
            recentHand = Hand.LEFT;
        }
        else if (rightHandAction.isRunning())
        {
            recentHand = Hand.RIGHT;
        }
    }

    @Override
    public void stop()
    {
        super.stop();

        leftHandAction.stop();
        rightHandAction.stop();
    }

    public boolean isRunning(Hand hand)
    {
        if (hand == Hand.LEFT)
        {
            return leftHandAction.isRunning();
        }
        else
        {
            return rightHandAction.isRunning();
        }
    }

    public boolean isFinished(Hand hand)
    {
        if (hand == Hand.LEFT)
        {
            return leftHandAction.isFinished();
        }
        else
        {
            return rightHandAction.isFinished();
        }
    }

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

    public float percentThroughHandActionSq()
    {
        if (leftHandAction.isRunning())
        {
            return leftHandAction.percentThroughActionSq();
        }
        else
        {
            return rightHandAction.percentThroughActionSq();
        }
    }

    public float percentThroughHandAction(int tickOffset)
    {
        if (leftHandAction.isRunning())
        {
            return leftHandAction.percentThroughAction(tickOffset);
        }
        else
        {
            return rightHandAction.percentThroughAction(tickOffset);
        }
    }

    public float percentThroughHandActionSq(int tickOffset)
    {
        if (leftHandAction.isRunning())
        {
            return leftHandAction.percentThroughActionSq(tickOffset);
        }
        else
        {
            return rightHandAction.percentThroughActionSq(tickOffset);
        }
    }

    public Hand getRecentHand()
    {
        return recentHand;
    }

    public enum Hand
    {
        LEFT,
        RIGHT,
        BOTH
    }
}
