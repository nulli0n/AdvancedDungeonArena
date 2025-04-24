package su.nightexpress.dungeons.api.dungeon;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;

import java.util.Set;

public interface DungeonSpawner extends Writeable {

    boolean isEmpty();

    @NotNull
    BlockPos getRandomPosition();

    @NotNull String getId();

    @NotNull Set<BlockPos> getPositions();
}
