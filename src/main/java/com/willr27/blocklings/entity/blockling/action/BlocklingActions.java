package com.willr27.blocklings.entity.blockling.action;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.action.actions.AttackAction;
import com.willr27.blocklings.entity.blockling.action.actions.KnownTargetAction;
import com.willr27.blocklings.entity.blockling.action.actions.UnknownTargetAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Used to manage the actions associated with a blockling.
 */
public class BlocklingActions
{
    /**
     * The action used as a timer that occurs every second.
     */
    @Nonnull
    public final KnownTargetAction ticks20;

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
     * The action used to track the regeneration cooldown.
     */
    @Nonnull
    public final KnownTargetAction regenerationCooldown;

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
     * The action used to track a log blockling's regeneration.
     */
    @Nonnull
    public final KnownTargetAction logRegenerationCooldown;

    /**
     * The list of all actions.
     */
    @Nonnull
    private final List<Action> actions = new ArrayList<>();

    /**
     * The list of actions that are automatically ticked.
     */
    @Nonnull
    private final List<Action> actionsToAutoTick = new ArrayList<>();

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

        // Basically 5 ticks + 50 ticks divided by the attack speed.
        Supplier<Float> attackTargetSupplier = () ->
        {
            return 5.0f + (50.0f / blockling.getStats().attackSpeed.getValue());
        };

        ticks20 = createAction("ticks_20", Action.Authority.SERVER, () -> 20.0f, true);
        attack = createAction("attack", Action.Authority.BOTH, attackTargetSupplier, attackTargetSupplier, true);
        gather = createAction("gather", Action.Authority.BOTH, () -> 1.0f, false);
        gather.setCount(-1.0f, false);
        regenerationCooldown = createAction("regeneration_cooldown", Action.Authority.BOTH, () -> 400.0f, true);
        attacksCooldown = createAction("attacks_cooldown", Action.Authority.BOTH, () -> 100.0f, true);
        oresMinedCooldown = createAction("ores_mined_cooldown", Action.Authority.BOTH, () -> 100.0f, true);
        logsChoppedCooldown = createAction("logs_chopped_cooldown", Action.Authority.BOTH, () -> 100.0f, true);
        cropsHarvestedCooldown = createAction("crops_harvested_cooldown", Action.Authority.BOTH, () -> 100.0f, true);
        logRegenerationCooldown = createAction("log_regeneration_cooldown", Action.Authority.SERVER, () -> 200.0f, true);
    }

    /**
     * Creates an unknown target action and adds it to the list of actions to tick automatically.
     *
     * @param key the key used to identify the action and for the underlying attribute.
     * @param authority the side that has authority over the value of the action.
     * @param autoTick whether to automatically tick the action.
     * @return the unknown target action.
     */
    public @Nonnull
    UnknownTargetAction createAction(@Nonnull String key, @Nonnull Action.Authority authority, boolean autoTick)
    {
        UnknownTargetAction action = new UnknownTargetAction(blockling, key, authority);
        actions.add(action);

        if (autoTick)
        {
            actionsToAutoTick.add(action);
        }

        return action;
    }

    /**
     * Creates known target action and adds it to the list of actions to tick automatically.
     *
     * @param key the key used to identify the action and for the underlying attribute.
     * @param authority the side that has authority over the value of the action.
     * @param targetCountSupplier the supplier used to get the target count.
     * @param autoTick whether to automatically tick the action.
     * @return the known target action.
     */
    public @Nonnull KnownTargetAction createAction(@Nonnull String key, @Nonnull Action.Authority authority, @Nonnull Supplier<Float> targetCountSupplier, boolean autoTick)
    {
        KnownTargetAction action = new KnownTargetAction(blockling, key, authority, targetCountSupplier);
        actions.add(action);

        if (autoTick)
        {
            actionsToAutoTick.add(action);
        }

        return action;
    }

    /**
     * Creates an attack action and adds it to the list of actions to tick automatically.
     *
     * @param key the key used to identify the action and for the underlying attribute.
     * @param authority the side that has authority over the value of the action.
     * @param targetCountSupplier the supplier used to get the target count.
     * @param handTargetCountSupplier the supplier used to get the target count for the hands.
     * @param autoTick whether to automatically tick the action.
     * @return the unknown target action.
     */
    public @Nonnull AttackAction createAction(@Nonnull String key, @Nonnull Action.Authority authority, @Nonnull Supplier<Float> targetCountSupplier, @Nonnull Supplier<Float> handTargetCountSupplier, boolean autoTick)
    {
        AttackAction action = new AttackAction(this, blockling, key, targetCountSupplier, handTargetCountSupplier);
        actions.add(action);

        if (autoTick)
        {
            actionsToAutoTick.add(action);
        }

        return action;
    }

    /**
     * @return finds the action with the given key, null if not found.
     */
    @Nullable
    public Action find(@Nonnull String key)
    {
        return actions.stream().filter(action -> action.key.equals(key)).findFirst().orElse(null);
    }

    /**
     * Ticks each action stored in the actions list.
     */
    public void tick()
    {
        // Auto tick the actions on their preferred side.
        // In the case of BOTH, only tick server side to prevent double ticking.
        actionsToAutoTick.stream().filter(action -> action.isCorrectSide() && (action.authority != Action.Authority.BOTH || !blockling.level.isClientSide)).forEach(Action::tick);
    }
}
