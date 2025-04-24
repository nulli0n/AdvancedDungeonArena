package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.mob.MobIdentifier;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.dungeons.dungeon.script.condition.type.MobAmountCondition;
import su.nightexpress.dungeons.dungeon.script.number.NumberComparator;
import su.nightexpress.nightcore.config.FileConfig;

public class SpawnedMobAmountCondition extends MobAmountCondition {

    public SpawnedMobAmountCondition(@NotNull NumberComparator comparator, int compareValue, @NotNull MobIdentifier mobId) {
        super(comparator, compareValue, mobId);
    }

    @NotNull
    public static SpawnedMobAmountCondition read(@NotNull FileConfig config, @NotNull String path) {
        return read(config, path, SpawnedMobAmountCondition::new);
    }

    @NotNull
    @Override
    public String getName() {
        return ConditionId.SPAWNED_MOB_AMOUNT;
    }

    @Override
    protected int getDungeonValue(@NotNull DungeonInstance dungeon) {
        return dungeon.getCounters().countMobSpawns(this.identifier);
    }
}
