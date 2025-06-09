package su.nightexpress.dungeons.dungeon.criteria.registry.mob.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.criteria.CriterionMob;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.dungeon.criteria.CriteriaValidators;
import su.nightexpress.dungeons.dungeon.criteria.registry.mob.MobCriteria;

public class MobFactionCriteria extends MobCriteria<MobFaction> {

    public MobFactionCriteria(@NotNull String name) {
        super(CriteriaValidators.forEnum(MobFaction.class), name);
    }

    @Override
    public boolean test(@NotNull CriterionMob mob, @NotNull MobFaction faction) {
        return mob.isFaction(faction);
    }
}
