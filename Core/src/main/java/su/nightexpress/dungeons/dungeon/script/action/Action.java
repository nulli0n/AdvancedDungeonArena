package su.nightexpress.dungeons.dungeon.script.action;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.nightcore.config.Writeable;

public interface Action extends Writeable {

    @NotNull String getName();

    void perform(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event);
}
