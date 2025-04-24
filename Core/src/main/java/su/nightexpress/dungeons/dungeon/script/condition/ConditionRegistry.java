package su.nightexpress.dungeons.dungeon.script.condition;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.dungeon.script.condition.impl.*;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.HashMap;
import java.util.Map;

public class ConditionRegistry {

    private static final Map<String, Loader> CONDITION_LOADERS = new HashMap<>();

    public static void load() {
        addLoader(ConditionId.TICK_INTERVAL, TickIntervalCondition::read);
        addLoader(ConditionId.CHANCE, ChanceCondition::load);
        addLoader(ConditionId.MOB_ID, MobIdCondition::load);
        addLoader(ConditionId.TASK_ID, TaskIdCondition::load);
        addLoader(ConditionId.STAGE_ID, StageIdCondition::load);

        // TODO Players amount, real_time_before, after, between, + world_time_ the same
        addLoader(ConditionId.ALIVE_MOB_AMOUNT, AliveMobAmountCondition::read);
        addLoader(ConditionId.ALIVE_MOBS_AMOUNT, AliveMobsAmountCondition::read);
        addLoader(ConditionId.KILLED_MOB_AMOUNT, KilledMobAmountCondition::read);
        addLoader(ConditionId.KILLED_MOBS_AMOUNT, KilledMobsAmountCondition::read);
        addLoader(ConditionId.SPAWNED_MOB_AMOUNT, SpawnedMobAmountCondition::read);
        addLoader(ConditionId.SPAWNED_MOBS_AMOUNT, SpawnedMobsAmountCondition::read);
        addLoader(ConditionId.TASK_PRESENT, (config, path) -> TaskPresentCondition.load(config, path, false));
        addLoader(ConditionId.TASK_NOT_PRESENT, (config, path) -> TaskPresentCondition.load(config, path, true));
        addLoader(ConditionId.SPOT_HAS_STATE, (config, path) -> SpotStateCondition.load(config, path, false));
        addLoader(ConditionId.SPOT_NOT_IN_STATE, (config, path) -> SpotStateCondition.load(config, path, true));
        addLoader(ConditionId.TASK_COMPLETED, (config, path) -> TaskCompletedCondition.load(config, path, false));
        addLoader(ConditionId.TASK_INCOMPLETED, (config, path) -> TaskCompletedCondition.load(config, path, true));
    }

    public static void clear() {
        CONDITION_LOADERS.clear();
    }

    @Nullable
    public static Condition loadCondition(@NotNull String name, @NotNull FileConfig config, @NotNull String path) {
        Loader loader = getLoader(name);
        if (loader == null) return null;

        return loader.load(config, path);
    }

    @Nullable
    public static Loader getLoader(@NotNull String name) {
        return CONDITION_LOADERS.get(name.toLowerCase());
    }

    public static void addLoader(@NotNull String name, @NotNull Loader loader) {
        CONDITION_LOADERS.put(name.toLowerCase(), loader);
    }

    public interface Loader {

        Condition load(@NotNull FileConfig config, @NotNull String path);

    }
}
