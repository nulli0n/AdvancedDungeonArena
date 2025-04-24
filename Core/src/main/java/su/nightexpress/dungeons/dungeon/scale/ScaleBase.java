package su.nightexpress.dungeons.dungeon.scale;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;

public interface ScaleBase {

    @NotNull String getName();

    double getBaseValue(@NotNull DungeonInstance instance);

    //double getScaled(@NotNull DungeonInstance instance, double original);
}
