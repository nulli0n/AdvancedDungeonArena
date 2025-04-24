package su.nightexpress.dungeons.api.dungeon;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.mob.MobProvider;
import su.nightexpress.dungeons.api.type.GameState;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;

import java.util.Set;
import java.util.UUID;

public interface Dungeon {

    @NotNull World getWorld();

    @NotNull String getId();

    @NotNull GameState getState();

    void handlePlayerJoin(@NotNull DungeonPlayer player);

    void handlePlayerLeave(@NotNull DungeonPlayer player);


    boolean contains(@NotNull Entity entity);

    boolean contains(@NotNull Location location);

    boolean contains(@NotNull Block block);

    boolean contains(@NotNull BlockPos blockPos);



    boolean hasPlayer(@NotNull UUID playerId);

    boolean hasAlivePlayers();

    int countPlayers();

    int countAlivePlayers();

    int countDeadPlayers();

    @NotNull DungeonPlayer getRandomAlivePlayer();

    @Nullable DungeonPlayer getPlayer(@NotNull UUID playerId);

    @NotNull Set<? extends DungeonPlayer> getPlayers();

    @NotNull Set<? extends DungeonPlayer> getAlivePlayers();

    @NotNull Set<? extends DungeonPlayer> getDeadPlayers();

//    @Nullable DungeonPlayer getPlayer(@NotNull UUID playerId);
//
//    boolean isPlaying(@NotNull Player player);
//
//    boolean isPlaying(@NotNull UUID playerId);

    long getTickCount();






    void killMobs();

    boolean isAllyMob(@NotNull LivingEntity entity);

    boolean isEnemyMob(@NotNull LivingEntity entity);

    boolean hasAllyMobs();

    boolean hasEnemyMobs();

    boolean hasMobsOfFaction(@NotNull MobFaction faction);

    @Nullable MobFaction getMobFaction(@NotNull LivingEntity entity);

    void eliminateMob(@NotNull DungeonEntity mob);

    void spawnMob(@NotNull MobProvider provider, @NotNull String mobId, @NotNull MobFaction faction, @NotNull Location location, int level);

    void spawnMob(@NotNull MobProvider provider, @NotNull String mobId, @NotNull MobFaction faction, @NotNull DungeonSpawner spawner, int level, int amount);

    void addMob(@NotNull DungeonEntity mob);

    void removeMob(@NotNull DungeonEntity mob);

    void removeMob(@NotNull UUID mobId);

    boolean hasMob(@NotNull UUID mobId);

    @Nullable DungeonEntity getMob(@NotNull LivingEntity entity);

    @Nullable DungeonEntity getMobById(@NotNull UUID mobId);

    @NotNull Set<? extends DungeonEntity> getAllyMobs();

    @NotNull Set<? extends DungeonEntity> getEnemyMobs();

    @NotNull Set<? extends DungeonEntity> getMobs();

    @NotNull Set<? extends DungeonEntity> getMobs(@NotNull MobFaction faction);
}
