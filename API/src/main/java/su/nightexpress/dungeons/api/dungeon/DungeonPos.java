package su.nightexpress.dungeons.api.dungeon;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.geodata.pos.ChunkPos;

import java.util.Objects;

public class DungeonPos {

    private final String   worldName;
    private final ChunkPos chunkPos;

    public DungeonPos(@NotNull String worldName, @NotNull ChunkPos chunkPos) {
        this.worldName = worldName;
        this.chunkPos = chunkPos;
    }

    @NotNull
    public String getWorldName() {
        return this.worldName;
    }

    @NotNull
    public ChunkPos getChunkPos() {
        return this.chunkPos;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof DungeonPos that)) return false;
        return Objects.equals(worldName, that.worldName) && Objects.equals(chunkPos, that.chunkPos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, chunkPos);
    }
}
