package su.nightexpress.dungeons.dungeon.event.game;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.level.Level;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;

public class DungeonLevelStartEvent extends DungeonLevelEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    public DungeonLevelStartEvent(@NotNull DungeonInstance dungeon, @NotNull Level level) {
        super(DungeonEventType.LEVEL_STARTED, dungeon, level);
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
