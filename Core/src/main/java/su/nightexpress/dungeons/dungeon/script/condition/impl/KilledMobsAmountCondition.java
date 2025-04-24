package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.dungeons.dungeon.script.condition.type.MobsAmountCondition;
import su.nightexpress.dungeons.dungeon.script.number.NumberComparator;
import su.nightexpress.nightcore.config.FileConfig;

public class KilledMobsAmountCondition extends MobsAmountCondition {

    public KilledMobsAmountCondition(@NotNull NumberComparator comparator, int compareValue, boolean checkFaction, @Nullable MobFaction faction) {
        super(comparator, compareValue, checkFaction, faction);
    }

    @NotNull
    public static KilledMobsAmountCondition read(@NotNull FileConfig config, @NotNull String path) {
        return read(config, path, KilledMobsAmountCondition::new);
    }

    @NotNull
    @Override
    public String getName() {
        return ConditionId.KILLED_MOBS_AMOUNT;
    }

    @Override
    protected int getDungeonValue(@NotNull DungeonInstance dungeon) {
        if (this.checkFaction && this.faction != null) {
            return dungeon.getCounters().countMobKills(this.faction);
        }
        return dungeon.getCounters().countMobKills();
    }
}
