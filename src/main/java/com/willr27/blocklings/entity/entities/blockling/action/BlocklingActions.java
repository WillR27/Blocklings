package com.willr27.blocklings.entity.entities.blockling.action;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.action.actions.AttackAction;
import com.willr27.blocklings.entity.entities.blockling.action.actions.KnownTargetAction;
import com.willr27.blocklings.entity.entities.blockling.action.actions.UnknownTargetAction;

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

        attack = createAction(blockling, "attacking", () -> blockling.getStats().combatInterval.getInt(), () -> blockling.getStats().combatInterval.getInt());
        mine = createAction(blockling, "mining", () -> blockling.getStats().miningInterval.getInt());
    }

    public UnknownTargetAction createAction(BlocklingEntity blockling, String key)
    {
        UnknownTargetAction action = new UnknownTargetAction(blockling, key);
        actions.add(action);

        return action;
    }

    public KnownTargetAction createAction(BlocklingEntity blockling, String key, Supplier<Integer> targetTicksSupplier)
    {
        KnownTargetAction action = new KnownTargetAction(blockling, key, targetTicksSupplier);
        actions.add(action);

        return action;
    }

    public AttackAction createAction(BlocklingEntity blockling, String key, Supplier<Integer> targetTicksSupplier, Supplier<Integer> handTargetTicksSupplier)
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
