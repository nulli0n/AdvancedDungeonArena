package su.nightexpress.dungeons.dungeon.criteria.registry.stage;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.criteria.registry.CriteriaHolder;
import su.nightexpress.dungeons.dungeon.criteria.registry.stage.impl.StageIdCriteria;

public class StageCriterias {

    public static final StageIdCriteria STAGE_ID = new StageIdCriteria("name");

    public static void setup(@NotNull CriteriaHolder<StageCriteria<?>> holder) {
        holder.register(STAGE_ID);
    }
}
