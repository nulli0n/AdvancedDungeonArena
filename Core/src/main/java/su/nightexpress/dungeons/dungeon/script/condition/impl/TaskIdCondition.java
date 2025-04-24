package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.event.game.TaskEvent;
import su.nightexpress.dungeons.dungeon.script.condition.Condition;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public class TaskIdCondition implements Condition {

    private final String taskId;

    public TaskIdCondition(@NotNull String taskId) {
        this.taskId = taskId;
    }

    @NotNull
    public static TaskIdCondition load(@NotNull FileConfig config, @NotNull String path) {
        String id = ConfigValue.create(path + ".TaskId", "null").read(config);

        return new TaskIdCondition(id);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".TaskId", this.taskId);
    }

    @NotNull
    @Override
    public String getName() {
        return ConditionId.TASK_ID;
    }

    @Override
    public boolean test(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        return event instanceof TaskEvent taskEvent && taskEvent.getStageTask().getId().equalsIgnoreCase(this.taskId);
    }
}
