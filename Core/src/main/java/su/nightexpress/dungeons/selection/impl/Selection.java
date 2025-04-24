package su.nightexpress.dungeons.selection.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.selection.SelectionType;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;

public abstract class Selection {

    protected final SelectionType type;

    public Selection(@NotNull SelectionType type) {
        this.type = type;
    }

    @NotNull
    public static Selection create(@NotNull SelectionType type) {
        return switch (type) {
            case CUBOID -> new CuboidSelection();
            case POSITION -> new PositionSelection();
        };
    }

    public abstract void clear();

    public abstract boolean isIncompleted();

    public abstract void onSelect(@NotNull Player player, @NotNull BlockPos pos, @NotNull Action action);

    @NotNull
    public SelectionType getType() {
        return this.type;
    }
}
