package su.nightexpress.dungeons.dungeon.event.game;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonEntity;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;

public class DungeonMobSpawnedEvent extends DungeonMobEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    public DungeonMobSpawnedEvent(@NotNull DungeonInstance dungeon, @NotNull DungeonEntity dungeonMob) {
        super(DungeonEventType.MOB_SPAWNED, dungeon, dungeonMob);
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
