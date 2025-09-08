package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.dungeons.dungeon.script.condition.type.MobsAmountCondition;
import su.nightexpress.nightcore.config.FileConfig;

@Deprecated
public class SpawnedMobsAmountCondition extends MobsAmountCondition {

    public SpawnedMobsAmountCondition(@NotNull MobsData data) {
        super(data);
    }

    @NotNull
    public static SpawnedMobsAmountCondition read(@NotNull FileConfig config, @NotNull String path) {
        return new SpawnedMobsAmountCondition(readMobsData(config, path));
    }

    @NotNull
    @Override
    public String getName() {
        return ConditionId.SPAWNED_MOBS_AMOUNT;
    }

    @Override
    protected double getDungeonValue(@NotNull DungeonInstance dungeon) {
        return dungeon.getStats().countMobSpawns(stage -> true, byFaction(this.getFactionLookup()));
    }
}
