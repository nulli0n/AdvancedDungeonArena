package su.nightexpress.dungeons.dungeon.stats;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.criteria.CriterionMob;
import su.nightexpress.dungeons.api.dungeon.DungeonEntity;
import su.nightexpress.dungeons.api.mob.MobSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class StageStats {

    private final Map<MobSnapshot, MobStats> mobStats;

    public StageStats() {
        this.mobStats = new HashMap<>();
    }

    public void clear() {
        this.mobStats.clear();
    }

    @NotNull
    public MobStats getMobStats(@NotNull DungeonEntity entity) {
        MobSnapshot snapshot = entity.getSnapshot();
        return this.mobStats.computeIfAbsent(snapshot, k -> new MobStats());
    }

    @NotNull
    public List<MobStats> queryMobStats(@NotNull Predicate<CriterionMob> predicate) {
        return this.mobStats.entrySet().stream().filter(entry -> predicate.test(entry.getKey())).map(Map.Entry::getValue).toList();
    }

    public int countMobKills(@NotNull Predicate<CriterionMob> predicate) {
        return this.countMobs(predicate, MobStats::getKilledAmount);
    }

    public int countMobSpawns(@NotNull Predicate<CriterionMob> predicate) {
        return this.countMobs(predicate, MobStats::getSpawnedAmount);
    }

    public int countMobs(@NotNull Predicate<CriterionMob> predicate, @NotNull Function<MobStats, Integer> function) {
        return this.queryMobStats(predicate).stream().mapToInt(function::apply).sum();
    }

    public void addMobKill(@NotNull DungeonEntity mob) {
        this.getMobStats(mob).addKill();
    }

    public void addMobSpawn(@NotNull DungeonEntity mob) {
        this.getMobStats(mob).addSpawn();
    }
}
