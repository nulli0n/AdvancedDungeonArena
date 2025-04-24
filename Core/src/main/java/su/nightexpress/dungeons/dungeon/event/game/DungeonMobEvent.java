package su.nightexpress.dungeons.dungeon.event.game;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonEntity;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;

public abstract class DungeonMobEvent extends DungeonGameEvent implements MobEvent {

    private final DungeonEntity dungeonMob;

    public DungeonMobEvent(@NotNull DungeonEventType type, @NotNull DungeonInstance dungeon, @NotNull DungeonEntity dungeonMob) {
        super(type, dungeon);
        this.dungeonMob = dungeonMob;
    }

    @NotNull
    @Override
    public DungeonEntity getDungeonMob() {
        return this.dungeonMob;
    }
}
