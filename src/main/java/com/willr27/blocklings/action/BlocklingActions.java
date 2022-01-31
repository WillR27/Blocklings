package com.willr27.blocklings.action;

import com.willr27.blocklings.action.actions.AttackAction;
import com.willr27.blocklings.action.actions.KnownTargetAction;
import com.willr27.blocklings.action.actions.UnknownTargetAction;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Used to manage the actions associated with a blockling.
 */
public class BlocklingActions
{
    /**
     * The action used when a blockling attacks.
     */
    @Nonnull
    public final AttackAction attack;

    /**
     * The action used when a blockling gathers a block.
     */
    @Nonnull
    public final KnownTargetAction gather;

    /**
     * The action used to track the combat momentum skill.
     */
    @Nonnull
    public final KnownTargetAction attacksCooldown;

    /**
     * The action used to track the mining momentum skill.
     */
    @Nonnull
    public final KnownTargetAction oresMinedCooldown;

    /**
     * The action used to track the woodcutting momentum skill.
     */
    @Nonnull
    public final KnownTargetAction logsChoppedCooldown;

    /**
     * The action used to track the farming momentum skill.
     */
    @Nonnull
    public final KnownTargetAction cropsHarvestedCooldown;

    /**
     * The list of actions that are automatically ticked.
     */
    @Nonnull
    private final List<Action> actions = new ArrayList<>();

    /**
     * The blockling.
     */
    @Nonnull
    public final BlocklingEntity blockling;

    /**
     * @param blockling the blockling.
     */
    public BlocklingActions(@Nonnull BlocklingEntity blockling)
    {
        this.blockling = blockling;

        Supplier<Float> attackTargetSupplier = () ->
        {
            return (1.0f / blockling.getStats().attackSpeed.getValue()) * 80.0f;
        };

        attack = createAction("attack", attackTargetSupplier, attackTargetSupplier);
        gather = new KnownTargetAction(blockling, "gather", () -> 1.0f);
        attacksCooldown = createAction("attacks_cooldown", () -> 100.0f);
        oresMinedCooldown = createAction("ores_mined_cooldown", () -> 100.0f);
        logsChoppedCooldown = createAction("logs_chopped_cooldown", () -> 100.0f);
        cropsHarvestedCooldown = createAction("crops_harvested_cooldown", () -> 100.0f);
    }

    /**
     * Creates an unknown target action and adds it to the list of actions to tick automatically.
     *
     * @param key the key used to identify the action and for the underlying attribute.
     * @return the unknown target action.
     */
    public @Nonnull UnknownTargetAction createAction(@Nonnull String key)
    {
        UnknownTargetAction action = new UnknownTargetAction(blockling, key);
        actions.add(action);

        return action;
    }

    /**
     * Creates known target action and adds it to the list of actions to tick automatically.
     *
     * @param key the key used to identify the action and for the underlying attribute.
     * @param targetCountSupplier the supplier used to get the target count.
     * @return the known target action.
     */
    public @Nonnull KnownTargetAction createAction(@Nonnull String key, @Nonnull Supplier<Float> targetCountSupplier)
    {
        KnownTargetAction action = new KnownTargetAction(blockling, key, targetCountSupplier);
        actions.add(action);

        return action;
    }

    /**
     * Creates an attack action and adds it to the list of actions to tick automatically.
     *
     * @param key the key used to identify the action and for the underlying attribute.
     * @param targetCountSupplier the supplier used to get the target count.
     * @param handTargetCountSupplier the supplier used to get the target count for the hands.
     * @return the unknown target action.
     */
    public @Nonnull AttackAction createAction(@Nonnull String key, @Nonnull Supplier<Float> targetCountSupplier, @Nonnull Supplier<Float> handTargetCountSupplier)
    {
        AttackAction action = new AttackAction(this, blockling, key, targetCountSupplier, handTargetCountSupplier);
        actions.add(action);

        return action;
    }

    /**
     * Ticks each action stored in the actions list.
     */
    public void tick()
    {
        actions.forEach(action -> action.tick());
    }
}
