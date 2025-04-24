package su.nightexpress.dungeons.dungeon.event.game;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.level.Level;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;

public abstract class DungeonLevelEvent extends DungeonGameEvent {

    private final Level level;

    public DungeonLevelEvent(@NotNull DungeonEventType type, @NotNull DungeonInstance dungeon, @NotNull Level level) {
        super(type, dungeon);
        this.level = level;
    }

    @NotNull
    public Level getLevel() {
        return this.level;
    }
}
