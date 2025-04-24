package su.nightexpress.dungeons.dungeon.game;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonEntity;
import su.nightexpress.dungeons.api.mob.MobIdentifier;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.dungeon.mob.DungeonMob;

import java.util.HashMap;
import java.util.Map;

public class DungeonCounters {

    private final DungeonInstance dungeon;

    private final Map<MobIdentifier, MobCounter> mobCounterById;
    private final Map<MobFaction, MobCounter>    mobCounterByFaction;

    public DungeonCounters(@NotNull DungeonInstance dungeon) {
        this.dungeon = dungeon;

        this.mobCounterById = new HashMap<>();
        this.mobCounterByFaction = new HashMap<>();
    }

    public void clear() {
        this.mobCounterById.clear();
        this.mobCounterByFaction.clear();
    }

    @NotNull
    private MobCounter getMobCounter(@NotNull MobIdentifier mobId) {
        return this.mobCounterById.computeIfAbsent(mobId, k -> new MobCounter());
    }

    @NotNull
    private MobCounter getMobCounter(@NotNull MobFaction faction) {
        return this.mobCounterByFaction.computeIfAbsent(faction, k -> new MobCounter());
    }

    public int countAliveMobs() {
        return (int) this.dungeon.getMobs().stream().filter(DungeonMob::isAlive).count();
    }

    public int countAliveMobs(@NotNull MobFaction faction) {
        return (int) this.dungeon.getMobs().stream().filter(mob -> mob.isFaction(faction) && mob.isAlive()).count();
    }

    public int countAliveMobs(@NotNull MobIdentifier mobId) {
        return (int) this.dungeon.getMobs().stream().filter(mob -> mob.isAlive() && mob.isMob(mobId)).count();
    }

    public int countMobKills() {
        return this.mobCounterById.values().stream().mapToInt(MobCounter::getKilledAmount).sum();
    }

    public int countMobKills(@NotNull MobIdentifier mobId) {
        return this.getMobCounter(mobId).getKilledAmount();
    }

    public int countMobKills(@NotNull MobFaction faction) {
        return this.getMobCounter(faction).getKilledAmount();
    }

    public void addMobKill(@NotNull DungeonEntity mob) {
        this.getMobCounter(mob.getIdentifier()).addKill();
        this.getMobCounter(mob.getFaction()).addKill();
    }

    public int countMobSpawns() {
        return this.mobCounterById.values().stream().mapToInt(MobCounter::getSpawnedAmount).sum();
    }

    public int countMobSpawns(@NotNull MobIdentifier mobId) {
        return this.getMobCounter(mobId).getSpawnedAmount();
    }

    public int countMobSpawns(@NotNull MobFaction faction) {
        return this.getMobCounter(faction).getSpawnedAmount();
    }

    public void addMobSpawn(@NotNull DungeonEntity mob) {
        this.getMobCounter(mob.getIdentifier()).addSpawn();
        this.getMobCounter(mob.getFaction()).addSpawn();
    }
}
