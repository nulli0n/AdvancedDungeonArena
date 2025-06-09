package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.dungeons.dungeon.script.condition.type.MobAmountCondition;
import su.nightexpress.nightcore.config.FileConfig;

@Deprecated
public class SpawnedMobAmountCondition extends MobAmountCondition {

    public SpawnedMobAmountCondition(@NotNull MobData mobData) {
        super(mobData);
    }

    @NotNull
    public static SpawnedMobAmountCondition read(@NotNull FileConfig config, @NotNull String path) {
        return new SpawnedMobAmountCondition(readMobData(config, path));
    }

    @NotNull
    @Override
    public String getName() {
        return ConditionId.SPAWNED_MOB_AMOUNT;
    }

    @Override
    protected int getDungeonValue(@NotNull DungeonInstance dungeon) {
        return dungeon.getStats().countMobSpawns(stage -> true, mob -> mob.isMob(this.identifier));
    }
}
