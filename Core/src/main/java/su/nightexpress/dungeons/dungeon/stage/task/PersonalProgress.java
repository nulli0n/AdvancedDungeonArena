package su.nightexpress.dungeons.dungeon.stage.task;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.dungeon.DungeonPlayer;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.script.task.ProgressFormatter;

import java.util.*;

public class PersonalProgress extends AbstractProgress {

    private final Map<UUID, Integer> countMap;
    private final int perPlayerAmount;

    public PersonalProgress(@NotNull DungeonInstance dungeon, @NotNull ProgressFormatter formatter, int requiredAmount, int perPlayerAmount) {
        super(formatter, requiredAmount);
        this.countMap = new HashMap<>();
        this.perPlayerAmount = perPlayerAmount;

        // Fill map so it adjust progress properly for players by their IDs.
        dungeon.getPlayers().forEach(player -> this.countMap.put(player.getPlayer().getUniqueId(), 0));
    }

    @Override
    public void onPlayerJoined(@NotNull DungeonPlayer player) {
        this.countMap.put(player.getPlayer().getUniqueId(), 0);
        this.requiredAmount += this.perPlayerAmount;
        System.out.println("PLayer joined, task progress increased to " + this.requiredAmount);
    }

    @Override
    public void onPlayerLeft(@NotNull DungeonPlayer player) {
        this.countMap.remove(player.getPlayer().getUniqueId());
        this.requiredAmount -= this.perPlayerAmount;
        System.out.println("PLayer left, task progress decreased to " + this.requiredAmount);
    }

    @Override
    public int getRequiredAmount(@Nullable Player player) {
        return player == null ? this.getRequiredAmount() : this.perPlayerAmount;
    }

    @Override
    public void addProgress(int amount) {
        this.addProgress(null, amount);
    }

    public void addProgress(@Nullable Player player, int amount) {
        if (player == null) {
            this.getPlayerIds().forEach(id -> this.setProgress(id, this.getProgress(id) + amount));
            return;
        }

        //int has = this.getProgress(player);
        //if (has >= this.perPlayerAmount) return;

        //this.setProgress(player, has + amount);
        this.setProgress(player, this.getProgress(player) + amount);
    }

    @Override
    public void resetProgress() {
        this.resetProgress(null);
    }

    @Override
    public void resetProgress(@Nullable Player player) {
        if (player == null) {
            this.getPlayerIds().forEach(id -> this.setProgress(id, 0));
            return;
        }

        this.setProgress(player, 0);
    }

    @Override
    public int countProgress() {
        return this.countProgress(null);
    }

    @Override
    public int countProgress(@Nullable Player player) {
        if (player == null) {
            return this.countMap.values().stream().mapToInt(has -> Math.min(has, this.perPlayerAmount)).sum();
        }

        return this.getProgress(player);
    }

    @Override
    public int countLeftover(@Nullable Player player) {
        if (player == null) {
            return this.countLeftover();
        }

        return this.getLeftover(player.getUniqueId());
    }

    @NotNull
    public Set<UUID> getPlayerIds() {
        return new HashSet<>(this.countMap.keySet());
    }

    public int getProgress(@NotNull Player player) {
        return this.getProgress(player.getUniqueId());
    }

    public void setProgress(@NotNull Player player, int amount) {
        this.setProgress(player.getUniqueId(), amount);
    }

    public int getProgress(@NotNull UUID playerId) {
        return this.countMap.getOrDefault(playerId, 0);
    }

    public void setProgress(@NotNull UUID playerId, int amount) {
        amount = Math.max(0, amount);

        this.countMap.put(playerId, Math.min(this.perPlayerAmount, amount));
    }

    public int getLeftover(@NotNull UUID playerId) {
        return Math.max(0, this.perPlayerAmount - this.getProgress(playerId));
    }
}
