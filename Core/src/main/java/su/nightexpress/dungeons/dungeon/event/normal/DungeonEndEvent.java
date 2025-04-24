package su.nightexpress.dungeons.dungeon.event.normal;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.type.GameResult;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.AbstractDungeonEvent;

public class DungeonEndEvent extends AbstractDungeonEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    private final GameResult result;

    public DungeonEndEvent(@NotNull DungeonInstance dungeon, @NotNull GameResult result) {
        super(dungeon);
        this.result = result;
    }

    @NotNull
    public GameResult getResult() {
        return this.result;
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
