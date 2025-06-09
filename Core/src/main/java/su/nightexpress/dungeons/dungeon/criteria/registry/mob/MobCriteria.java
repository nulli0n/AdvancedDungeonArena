package su.nightexpress.dungeons.dungeon.criteria.registry.mob;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.criteria.CriterionMob;
import su.nightexpress.dungeons.dungeon.criteria.AbstractCriteria;
import su.nightexpress.dungeons.api.criteria.CriteriaValidator;

public abstract class MobCriteria<T> extends AbstractCriteria<T, CriterionMob> {

    public MobCriteria(@NotNull CriteriaValidator<T> parser, @NotNull String name) {
        super(parser, name);
    }
}
