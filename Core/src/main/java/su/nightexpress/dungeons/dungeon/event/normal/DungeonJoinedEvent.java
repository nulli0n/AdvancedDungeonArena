package su.nightexpress.dungeons.dungeon.event.normal;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonPlayer;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.AbstractDungeonEvent;

public class DungeonJoinedEvent extends AbstractDungeonEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    private final DungeonPlayer dungeonPlayer;

    public DungeonJoinedEvent(@NotNull DungeonInstance dungeon, @NotNull DungeonPlayer dungeonPlayer) {
        super(dungeon);
        this.dungeonPlayer = dungeonPlayer;
    }

    @NotNull
    public DungeonPlayer getDungeonPlayer() {
        return this.dungeonPlayer;
    }

    @NotNull
    public Player getPlayer() {
        return this.dungeonPlayer.getPlayer();
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
