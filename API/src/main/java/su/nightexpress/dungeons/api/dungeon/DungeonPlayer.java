package su.nightexpress.dungeons.api.dungeon;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.compat.BoardPlugin;
import su.nightexpress.dungeons.api.compat.GodPlugin;
import su.nightexpress.dungeons.api.type.GameState;

import java.util.function.Consumer;

public interface DungeonPlayer {

    void tick();

    void revive();

    void teleport(@NotNull Location location);

    void handleDeath();

    void addBoard();

    void removeBoard();

    void updateBoard();

    void manageExternalGod(@NotNull Consumer<GodPlugin> consumer);

    void manageExternalBoard(@NotNull Consumer<BoardPlugin> consumer);

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
