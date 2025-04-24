package su.nightexpress.dungeons.api.schema;

import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;

public class SchemaBlock {

    private final BlockPos  blockPos;
    private final BlockData blockData;
    private final Object    nbt;

    public SchemaBlock(@NotNull BlockPos blockPos, @NotNull BlockData blockData, @Nullable Object nbt) {
        this.blockPos = blockPos;
        this.blockData = blockData;
        this.nbt = nbt;
    }

    @NotNull
    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    @NotNull
    public BlockData getBlockData() {
        return this.blockData;
    }

    @Nullable
    public Object getNbt() {
        return this.nbt;
    }
}
