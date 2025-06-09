package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.dungeons.dungeon.script.condition.type.MobsAmountCondition;
import su.nightexpress.dungeons.dungeon.script.number.NumberComparator;
import su.nightexpress.nightcore.config.FileConfig;

@Deprecated
public class AliveMobsAmountCondition extends MobsAmountCondition {

    public AliveMobsAmountCondition(@NotNull MobsData data) {
        super(data);
    }

    public AliveMobsAmountCondition(@NotNull NumberComparator comparator, int compareValue, boolean checkFaction, @Nullable MobFaction faction) {
        super(comparator, compareValue, checkFaction, faction);
    }

    @NotNull
    public static AliveMobsAmountCondition read(@NotNull FileConfig config, @NotNull String path) {
        return new AliveMobsAmountCondition(readMobsData(config, path));
    }

    @NotNull
    @Override
    public String getName() {
        return ConditionId.ALIVE_MOBS_AMOUNT;
    }

    @Override
    protected int getDungeonValue(@NotNull DungeonInstance dungeon) {
        return dungeon.countMobs(byFaction(this.getFactionLookup()));
    }
}
