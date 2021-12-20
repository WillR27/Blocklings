package com.willr27.blocklings.entity.entities.blockling;

import com.willr27.blocklings.action.Action;
import com.willr27.blocklings.action.actions.AttackAction;
import com.willr27.blocklings.action.actions.KnownTargetAction;
import com.willr27.blocklings.action.actions.UnknownTargetAction;
import com.willr27.blocklings.item.ToolUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BlocklingActions
{
    public final List<Action> actions = new ArrayList<>();

    public final AttackAction attack;
    public final KnownTargetAction mine;

    public final BlocklingEntity blockling;

    public BlocklingActions(BlocklingEntity blockling)
    {
        this.blockling = blockling;

        Supplier<Float> attackTargetSupplier = () ->
        {
            return (1.0f / blockling.getStats().attackSpeed.getValue()) * 80.0f;
        };

        attack = createAction(blockling, "attack", attackTargetSupplier, attackTargetSupplier);
        mine = new KnownTargetAction(blockling, "mine", () -> 1.0f);
    }

    public UnknownTargetAction createAction(BlocklingEntity blockling, String key)
    {
        UnknownTargetAction action = new UnknownTargetAction(blockling, key);
        actions.add(action);

        return action;
    }

    public KnownTargetAction createAction(BlocklingEntity blockling, String key, Supplier<Float> targetTicksSupplier)
    {
        KnownTargetAction action = new KnownTargetAction(blockling, key, targetTicksSupplier);
        actions.add(action);

        return action;
    }

    public AttackAction createAction(BlocklingEntity blockling, String key, Supplier<Float> targetTicksSupplier, Supplier<Float> handTargetTicksSupplier)
    {
        AttackAction action = new AttackAction(this, blockling, key, targetTicksSupplier, handTargetTicksSupplier);
        actions.add(action);

        return action;
    }

    public void tick()
    {
        actions.forEach(action -> action.tick());
    }
}
