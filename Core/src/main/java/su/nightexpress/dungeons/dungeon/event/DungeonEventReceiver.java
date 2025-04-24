package su.nightexpress.dungeons.dungeon.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;

public interface DungeonEventReceiver {

    void addHandler(@NotNull DungeonEventHandler handler);

    boolean onDungeonEventBroadcastReceive(@NotNull DungeonGameEvent event, @NotNull DungeonEventType eventType, @NotNull DungeonInstance dungeon);
}
