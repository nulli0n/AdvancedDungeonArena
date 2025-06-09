package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.dungeons.dungeon.script.condition.type.MobsAmountCondition;
import su.nightexpress.nightcore.config.FileConfig;

@Deprecated
public class KilledMobsAmountCondition extends MobsAmountCondition {

    public KilledMobsAmountCondition(@NotNull MobsData data) {
        super(data);
    }

    @NotNull
    public static KilledMobsAmountCondition read(@NotNull FileConfig config, @NotNull String path) {
        return new KilledMobsAmountCondition(readMobsData(config, path));
    }

    @NotNull
    @Override
    public String getName() {
        return ConditionId.KILLED_MOBS_AMOUNT;
    }

    @Override
    protected int getDungeonValue(@NotNull DungeonInstance dungeon) {
        return dungeon.getStats().countMobKills(stage -> true, byFaction(this.getFactionLookup()));
    }
}
