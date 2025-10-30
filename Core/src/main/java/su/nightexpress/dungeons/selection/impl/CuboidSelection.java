package su.nightexpress.dungeons.selection.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.selection.SelectionType;
import su.nightexpress.nightcore.util.geodata.Cuboid;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;

public class CuboidSelection extends Selection {

    private BlockPos first;
    private BlockPos second;

    public CuboidSelection() {
        super(SelectionType.CUBOID);
    }

    @Override
    public void clear() {
        this.setFirst(null);
        this.setSecond(null);
    }

    @Override
    public boolean isIncompleted() {
        return this.first == null || this.second == null;
    }

    @Override
    public void onSelect(@NotNull Player player, @NotNull BlockPos pos, @NotNull Action action) {
        int value;
        if (action == Action.LEFT_CLICK_BLOCK) {
            this.setFirst(pos);
            value = 1;
        }
        else {
            this.setSecond(pos);
            value = 2;
        }

        Lang.SELECTION_INFO_CUBOID.message().send(player, replacer -> replacer.replace(Placeholders.GENERIC_VALUE, value));
    }

    @Nullable
    public BlockPos getFirst() {
        return this.first;
    }

    public void setFirst(@Nullable BlockPos first) {
        this.first = first;
    }

    @Nullable
    public BlockPos getSecond() {
        return this.second;
    }

    public void setSecond(@Nullable BlockPos second) {
        this.second = second;
    }

    @Nullable
    public Cuboid toCuboid() {
        return this.isIncompleted() ? null : new Cuboid(this.first, this.second);
    }
}
