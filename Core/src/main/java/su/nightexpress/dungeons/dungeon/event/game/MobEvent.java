package su.nightexpress.dungeons.dungeon.event.game;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonEntity;

public interface MobEvent {

    @NotNull DungeonEntity getDungeonMob();
}
