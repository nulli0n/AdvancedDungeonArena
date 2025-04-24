package su.nightexpress.dungeons.dungeon.event.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.dungeon.player.DungeonGamer;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;

public abstract class DungeonPlayerEvent extends DungeonGameEvent implements GamerEvent {

    protected DungeonGamer player;

    public DungeonPlayerEvent(@NotNull DungeonEventType type, @NotNull DungeonInstance dungeon, @Nullable DungeonGamer player) {
        super(type, dungeon);
        this.player = player;
    }

    @Nullable
    @Deprecated
    public DungeonGamer getPlayer() {
        return this.player;
    }

    @Nullable
    @Override
    public DungeonGamer getGamer() {
        return this.player;
    }

    @Override
    public void setGamer(@Nullable DungeonGamer gamer) {
        this.player = gamer;
    }
}
