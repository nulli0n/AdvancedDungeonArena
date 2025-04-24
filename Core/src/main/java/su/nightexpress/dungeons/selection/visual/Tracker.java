package su.nightexpress.dungeons.selection.visual;

import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;

public class Tracker {

    private BlockPos previousPos;
    private boolean  selection;

    public Tracker() {
        this.previousPos = null;
        this.selection = false;
    }

    @Nullable
    public BlockPos getPreviousPos() {
        return previousPos;
    }

    public void setPreviousPos(@Nullable BlockPos previousPos) {
        this.previousPos = previousPos;
    }

    public boolean isSelection() {
        return selection;
    }

    public void setSelection(boolean selection) {
        this.selection = selection;
    }
}
