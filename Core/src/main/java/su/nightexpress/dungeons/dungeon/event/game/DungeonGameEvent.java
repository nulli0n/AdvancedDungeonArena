package su.nightexpress.dungeons.dungeon.event.game;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.AbstractDungeonEvent;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;

public abstract class DungeonGameEvent extends AbstractDungeonEvent {

    protected final DungeonEventType type;

    public DungeonGameEvent(@NotNull DungeonEventType type, @NotNull DungeonInstance dungeon) {
        super(dungeon);
        this.type = type;
    }

    @NotNull
    public DungeonEventType getType() {
        return this.type;
    }
}
