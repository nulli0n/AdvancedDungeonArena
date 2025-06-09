package su.nightexpress.dungeons.dungeon.criteria.registry.stage.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.criteria.CriteriaValidators;
import su.nightexpress.dungeons.dungeon.criteria.registry.stage.StageCriteria;
import su.nightexpress.dungeons.dungeon.stage.Stage;

public class StageIdCriteria extends StageCriteria<String> {

    public StageIdCriteria(@NotNull String name) {
        super(CriteriaValidators.STRING, name);
    }

    @Override
    public boolean test(@NotNull Stage stage, @NotNull String value) {
        return stage.getId().equalsIgnoreCase(value);
    }
}
