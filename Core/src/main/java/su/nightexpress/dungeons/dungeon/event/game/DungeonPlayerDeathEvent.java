package su.nightexpress.dungeons.dungeon.event.game;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.dungeon.player.DungeonGamer;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;

public class DungeonPlayerDeathEvent extends DungeonPlayerEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    public DungeonPlayerDeathEvent(@NotNull DungeonInstance dungeon, @Nullable DungeonGamer player) {
        super(DungeonEventType.PLAYER_DEATH, dungeon, player);
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
