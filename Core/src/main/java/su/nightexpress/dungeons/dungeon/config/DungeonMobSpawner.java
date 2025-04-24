package su.nightexpress.dungeons.dungeon.config;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonSpawner;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.Objects;
import java.util.Set;

public class DungeonMobSpawner implements DungeonSpawner {

    private final String id;
    private final Set<BlockPos> positions;

    public DungeonMobSpawner(@NotNull String id, @NotNull Set<BlockPos> positions) {
        this.id = id.toLowerCase();
        this.positions = positions;
    }

    @NotNull
    public static DungeonMobSpawner read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        Set<BlockPos> positions = Lists.modify(config.getStringSet(path + ".Positions"), BlockPos::deserialize);
        positions.removeIf(Objects::isNull);

        return new DungeonMobSpawner(id, positions);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Positions", this.positions.stream().map(BlockPos::serialize).toList());
    }

    @Override
    public boolean isEmpty() {
        return this.positions.isEmpty();
    }

    @Override
    @NotNull
    public BlockPos getRandomPosition() {
        return Rnd.get(this.positions);
    }

    @Override
    @NotNull
    public String getId() {
        return this.id;
    }

    @Override
    @NotNull
    public Set<BlockPos> getPositions() {
        return this.positions;
    }
}
