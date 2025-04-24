package su.nightexpress.dungeons.dungeon.event.game;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;

public class DungeonTickEvent extends DungeonGameEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    public DungeonTickEvent(@NotNull DungeonInstance dungeon) {
        super(DungeonEventType.DUNGEON_TICK, dungeon);
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
