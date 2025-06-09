package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.dungeons.dungeon.script.condition.type.MobAmountCondition;
import su.nightexpress.nightcore.config.FileConfig;

@Deprecated
public class AliveMobAmountCondition extends MobAmountCondition {

    public AliveMobAmountCondition(@NotNull MobData mobData) {
        super(mobData);
    }

    @NotNull
    public static AliveMobAmountCondition read(@NotNull FileConfig config, @NotNull String path) {
        return new AliveMobAmountCondition(readMobData(config, path));
    }

    @NotNull
    @Override
    public String getName() {
        return ConditionId.ALIVE_MOB_AMOUNT;
    }

    @Override
    protected int getDungeonValue(@NotNull DungeonInstance dungeon) {
        return dungeon.countMobs(mob -> mob.isMob(this.identifier));
    }
}
