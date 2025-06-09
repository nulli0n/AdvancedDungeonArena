package su.nightexpress.dungeons.dungeon.criteria.registry.mob.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.criteria.CriterionMob;
import su.nightexpress.dungeons.dungeon.criteria.CriteriaValidators;
import su.nightexpress.dungeons.dungeon.criteria.registry.mob.MobCriteria;

public class MobStageCriteria extends MobCriteria<String> {

    public MobStageCriteria(@NotNull String name) {
        super(CriteriaValidators.STRING, name);
    }

    @Override
    public boolean test(@NotNull CriterionMob mob, @NotNull String stageId) {
        return mob.getBornStageId().equalsIgnoreCase(stageId);
    }
}
