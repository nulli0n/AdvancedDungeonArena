package su.nightexpress.dungeons.dungeon.event.game;

import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.dungeon.player.DungeonGamer;

public interface GamerEvent {

    @Nullable DungeonGamer getGamer();

    void setGamer(@Nullable DungeonGamer gamer);
}
