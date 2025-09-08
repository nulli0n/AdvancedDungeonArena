package su.nightexpress.dungeons.dungeon.script.condition;

import org.jetbrains.annotations.NotNull;

public class ConditionInfo {

    private final boolean cached;
    private final Condition condition;

    public ConditionInfo(boolean cached, @NotNull Condition condition) {
        this.cached = cached;
        this.condition = condition;
    }

    public boolean isCached() {
        return this.cached;
    }

    @NotNull
    public Condition getCondition() {
        return this.condition;
    }
}
