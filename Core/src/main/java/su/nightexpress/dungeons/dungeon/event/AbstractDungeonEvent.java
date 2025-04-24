package su.nightexpress.dungeons.dungeon.event;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;

public abstract class AbstractDungeonEvent extends Event {

    protected final DungeonInstance dungeon;

    public AbstractDungeonEvent(@NotNull DungeonInstance dungeon) {
        this.dungeon = dungeon;
    }

    @NotNull
    public DungeonInstance getDungeon() {
        return this.dungeon;
    }
}
