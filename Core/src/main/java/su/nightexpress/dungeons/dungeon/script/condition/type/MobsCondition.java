package su.nightexpress.dungeons.dungeon.script.condition.type;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.criteria.CriteriaProvider;
import su.nightexpress.dungeons.api.criteria.CriterionMob;
import su.nightexpress.dungeons.dungeon.criteria.CriteriaMap;
import su.nightexpress.dungeons.dungeon.criteria.registry.CriteriaRegistry;
import su.nightexpress.dungeons.dungeon.script.number.NumberComparator;
import su.nightexpress.dungeons.dungeon.stage.Stage;
import su.nightexpress.nightcore.config.FileConfig;

public abstract class MobsCondition extends NumberCompareCondition {

    protected final CriteriaProvider<CriterionMob> mobCriterias;
    protected final CriteriaProvider<Stage>        stageCriterias;

    public record CriteriaData(CriteriaProvider<CriterionMob> mobCriterias, CriteriaProvider<Stage> stageCriterias) {}

    public MobsCondition(@NotNull NumberComparator comparator,
                               int compareValue,
                               @NotNull CriteriaProvider<CriterionMob> mobCriterias,
                               @NotNull CriteriaProvider<Stage> stageCriterias) {
        super(comparator, compareValue);
        this.mobCriterias = mobCriterias;
        this.stageCriterias = stageCriterias;
    }

    @NotNull
    public static CriteriaData readCriteriaData(@NotNull FileConfig config, @NotNull String path) {
        var mobCriterias = CriteriaMap.read(config, path + ".MobCriteria", CriteriaRegistry.MOB);
        var stageCriterias = CriteriaMap.read(config, path + ".StageCriteria", CriteriaRegistry.STAGE);

        return new CriteriaData(mobCriterias, stageCriterias);
    }

    @Override
    protected void writeAdditional(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".MobCriteria", this.mobCriterias);
        config.set(path + ".StageCriteria", this.stageCriterias);
    }
}
