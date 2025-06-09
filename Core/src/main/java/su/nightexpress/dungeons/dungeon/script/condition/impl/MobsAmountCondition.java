package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.criteria.CriteriaProvider;
import su.nightexpress.dungeons.api.criteria.CriterionMob;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.dungeons.dungeon.script.condition.type.MobsCondition;
import su.nightexpress.dungeons.dungeon.script.number.NumberComparator;
import su.nightexpress.dungeons.dungeon.stage.Stage;
import su.nightexpress.nightcore.config.FileConfig;

public class MobsAmountCondition extends MobsCondition {

    public MobsAmountCondition(@NotNull NumberComparator comparator,
                               int compareValue,
                               @NotNull CriteriaProvider<CriterionMob> mobCriterias,
                               @NotNull CriteriaProvider<Stage> stageCriterias) {
        super(comparator, compareValue, mobCriterias, stageCriterias);
    }

    @NotNull
    public static MobsAmountCondition read(@NotNull FileConfig config, @NotNull String path) {
        NumberData data = readNumberData(config, path);
        CriteriaData criteriaData = readCriteriaData(config, path);

        return new MobsAmountCondition(data.comparator(), data.compareValue(), criteriaData.mobCriterias(), criteriaData.stageCriterias());
    }

    @NotNull
    @Override
    public String getName() {
        return ConditionId.MOBS_AMOUNT;
    }

    @Override
    protected int getDungeonValue(@NotNull DungeonInstance dungeon) {
        return dungeon.countMobs(this.mobCriterias.getPredicate(mob -> mob.isFaction(MobFaction.ENEMY)));
    }
}
