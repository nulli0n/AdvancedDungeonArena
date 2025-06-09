package su.nightexpress.dungeons.dungeon.criteria.registry;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.dungeon.criteria.registry.mob.MobCriteria;
import su.nightexpress.dungeons.dungeon.criteria.registry.mob.MobCriterias;
import su.nightexpress.dungeons.dungeon.criteria.registry.stage.StageCriteria;
import su.nightexpress.dungeons.dungeon.criteria.registry.stage.StageCriterias;

public class CriteriaRegistry {

    public static final CriteriaHolder<MobCriteria<?>> MOB = new CriteriaHolder<>();
    public static final CriteriaHolder<StageCriteria<?>> STAGE = new CriteriaHolder<>();

    public static void load(@NotNull DungeonPlugin plugin) {
        MobCriterias.setup(MOB);
        StageCriterias.setup(STAGE);
    }

    public static void clear() {
        MOB.clear();
        STAGE.clear();
    }
}
