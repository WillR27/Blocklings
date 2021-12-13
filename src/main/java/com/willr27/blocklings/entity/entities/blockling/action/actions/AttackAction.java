package com.willr27.blocklings.entity.entities.blockling.action.actions;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.entity.entities.blockling.action.BlocklingActions;

import java.util.function.Supplier;

public class AttackAction extends KnownTargetAction
{
    private final KnownTargetAction leftHandAction;
    private final KnownTargetAction rightHandAction;

    private BlocklingHand recentHand = BlocklingHand.BOTH;

    public AttackAction(BlocklingActions actions, BlocklingEntity blockling, String key, Supplier<Float> targetTicksSupplier, Supplier<Float> handTargetTicksSupplier)
    {
        super(blockling, key, targetTicksSupplier);

        Supplier<Float> supplier = () -> handTargetTicksSupplier.get() < 10.0f ? handTargetTicksSupplier.get() : 10.0f;
        leftHandAction = actions.createAction(blockling, key + "_left_hand", supplier);
        rightHandAction = actions.createAction(blockling, key + "_right_hand", supplier);
    }

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

    public void start(BlocklingHand hand)
    {
        super.start();

        if (hand == BlocklingHand.OFF || hand == BlocklingHand.BOTH)
        {
            leftHandAction.start();
        }
        else if (hand == BlocklingHand.MAIN || hand == BlocklingHand.BOTH)
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

    public BlocklingHand getRecentHand()
    {
        return recentHand;
    }

}
