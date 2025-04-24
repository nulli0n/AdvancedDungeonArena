package su.nightexpress.dungeons.dungeon.stage.task;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.dungeon.script.task.ProgressFormatter;

public abstract class AbstractProgress implements TaskProgress {

    protected final ProgressFormatter formatter;
    protected int requiredAmount;

    public AbstractProgress(@NotNull ProgressFormatter formatter, int requiredAmount) {
        this.formatter = formatter;
        this.requiredAmount = requiredAmount;
    }

    @NotNull
    public ProgressFormatter getFormatter() {
        return this.formatter;
    }

    @NotNull
    @Override
    public String format(@Nullable Player player) {
        return this.formatter.format(this, player);
    }

    @Override
    public int countLeftover() {
        return Math.max(0, this.getRequiredAmount() - this.countProgress());
    }

    @Override
    public boolean isEmpty() {
        return this.requiredAmount <= 0;
    }

    @Override
    public boolean isCompleted() {
        return this.isEmpty() || this.countProgress() >= this.getRequiredAmount();
    }

    @Override
    public int getRequiredAmount() {
        return this.requiredAmount;
    }

    @Override
    public void setRequiredAmount(int requiredAmount) {
        this.requiredAmount = requiredAmount;
    }
}
