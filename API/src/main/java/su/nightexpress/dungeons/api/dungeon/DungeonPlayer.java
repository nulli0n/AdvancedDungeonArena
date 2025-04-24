package su.nightexpress.dungeons.api.dungeon;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.type.GameState;

public interface DungeonPlayer {

    void tick();

    void revive();

    void handleDeath();

    void addBoard();

    void removeBoard();

    void updateBoard();

    boolean isAlive();

    boolean isDead();

    boolean isReady();

    boolean isInLobby();

    boolean isInGame();

    boolean hasExtraLives();

    long getDeathTime();

    @NotNull Player getPlayer();

    @NotNull Dungeon getDungeon();

    @NotNull GameState getState();

    void setState(@NotNull GameState state);

    int getLives();

    void setLives(int lives);

    void addExtraLive();

    void takeExtraLive();
}
