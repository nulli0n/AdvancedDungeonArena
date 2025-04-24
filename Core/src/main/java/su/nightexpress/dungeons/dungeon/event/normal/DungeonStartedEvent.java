package su.nightexpress.dungeons.dungeon.event.normal;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.AbstractDungeonEvent;

public class DungeonStartedEvent extends AbstractDungeonEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    public DungeonStartedEvent(@NotNull DungeonInstance dungeon) {
        super(dungeon);
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
