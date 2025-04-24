package su.nightexpress.dungeons.dungeon.event.game;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;
import su.nightexpress.dungeons.dungeon.stage.Stage;

public class DungeonStageStartEvent extends DungeonStageEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    public DungeonStageStartEvent(@NotNull DungeonInstance dungeon, @NotNull Stage stage) {
        super(DungeonEventType.STAGE_STARTED, dungeon, stage);
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
