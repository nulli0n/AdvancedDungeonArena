package su.nightexpress.dungeons.dungeon.stats;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.criteria.CriterionMob;
import su.nightexpress.dungeons.api.dungeon.DungeonEntity;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class DungeonStats {

    private final DungeonInstance         dungeon;
    private final Map<String, StageStats> stats;

    public DungeonStats(@NotNull DungeonInstance dungeon) {
        this.dungeon = dungeon;
        this.stats = new HashMap<>();
    }

    public void clear() {
        this.stats.clear();
    }

    @NotNull
    public StageStats getStageStats(@NotNull Stage stage) {
        return this.getStageStats(stage.getId());
    }

    @NotNull
    public StageStats getStageStats(@NotNull String stageId) {
        return this.stats.computeIfAbsent(stageId.toLowerCase(), k -> new StageStats());
    }


    @NotNull
    public List<StageStats> queryStageStats(@NotNull Predicate<Stage> stageTest) {
        return this.dungeon.getConfig().getStages().stream().filter(stageTest).map(this::getStageStats).toList();
    }

    @NotNull
    public List<MobStats> queryMobStats(@NotNull Predicate<CriterionMob> mobTest) {
        return this.queryMobStats(stage -> true, mobTest);
    }

    @NotNull
    public List<MobStats> queryMobStats(@NotNull Predicate<Stage> stageTest, @NotNull Predicate<CriterionMob> mobTest) {
        return this.queryStageStats(stageTest).stream()
            .flatMap(stageStats -> stageStats.queryMobStats(mobTest).stream())
            .toList();
    }


    public int countMobKills(@NotNull Predicate<Stage> stageTest, @NotNull Predicate<CriterionMob> predicate) {
        return this.countMobs(stageTest, predicate, MobStats::getKilledAmount);
    }

    public int countMobSpawns(@NotNull Predicate<Stage> stageTest, @NotNull Predicate<CriterionMob> predicate) {
        return this.countMobs(stageTest, predicate, MobStats::getSpawnedAmount);
    }

    public int countMobs(@NotNull Predicate<Stage> stageTest, @NotNull Predicate<CriterionMob> predicate, @NotNull Function<MobStats, Integer> function) {
        return this.queryMobStats(stageTest, predicate).stream().mapToInt(function::apply).sum();
    }

    public void addMobKill(@NotNull DungeonEntity mob) {
        this.getStageStats(this.dungeon.getStage()).addMobKill(mob);
    }

    public void addMobSpawn(@NotNull DungeonEntity mob) {
        this.getStageStats(this.dungeon.getStage()).addMobSpawn(mob);
    }
}
