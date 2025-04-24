package su.nightexpress.dungeons.dungeon.script.condition;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.nightcore.config.Writeable;

public interface Condition extends Writeable {

    @NotNull String getName();

    boolean test(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event);
}
