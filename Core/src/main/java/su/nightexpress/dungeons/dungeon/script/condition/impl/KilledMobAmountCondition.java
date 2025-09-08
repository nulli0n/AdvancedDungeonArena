package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.dungeons.dungeon.script.condition.type.MobAmountCondition;
import su.nightexpress.nightcore.config.FileConfig;

@Deprecated
public class KilledMobAmountCondition extends MobAmountCondition {

    public KilledMobAmountCondition(@NotNull MobData mobData) {
        super(mobData);
    }

    @NotNull
    public static KilledMobAmountCondition read(@NotNull FileConfig config, @NotNull String path) {
        return new KilledMobAmountCondition(readMobData(config, path));
    }

    @NotNull
    @Override
    public String getName() {
        return ConditionId.KILLED_MOB_AMOUNT;
    }

    @Override
    protected double getDungeonValue(@NotNull DungeonInstance dungeon) {
        //return dungeon.getStats().queryMobStats(MobFilter.byKey(this.identifier)).stream().mapToInt(MobStats::getKilledAmount).sum();

        return dungeon.getStats().countMobKills(stage -> true, mob -> mob.isMob(this.identifier));
    }
}
