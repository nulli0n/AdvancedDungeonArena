package su.nightexpress.dungeons.dungeon.criteria.registry.stage;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.criteria.AbstractCriteria;
import su.nightexpress.dungeons.api.criteria.CriteriaValidator;
import su.nightexpress.dungeons.dungeon.stage.Stage;

public abstract class StageCriteria<T> extends AbstractCriteria<T, Stage> {

    public StageCriteria(@NotNull CriteriaValidator<T> parser, @NotNull String name) {
        super(parser, name);
    }
}
