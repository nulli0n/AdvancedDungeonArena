package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.script.condition.Condition;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.nightcore.config.FileConfig;

public class TickIntervalCondition implements Condition {

    private long interval;

    public TickIntervalCondition(long interval) {
        this.interval = interval;
    }

    @NotNull
    public static TickIntervalCondition read(@NotNull FileConfig config, @NotNull String path) {
        long interval = config.getLong(path + ".Interval");

        return new TickIntervalCondition(interval);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Interval", this.interval);
    }

    @NotNull
    @Override
    public String getName() {
        return ConditionId.TICK_INTERVAL;
    }

    @Override
    public boolean test(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        return this.interval > 0 && dungeon.getTickCount() % this.interval == 0;
    }

    public long getInterval() {
        return this.interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }
}
