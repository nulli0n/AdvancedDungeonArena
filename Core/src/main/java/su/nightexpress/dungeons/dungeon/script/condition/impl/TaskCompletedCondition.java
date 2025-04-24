package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.script.condition.Condition;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.dungeons.dungeon.stage.StageTask;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public class TaskCompletedCondition implements Condition {

    private final String taskId;
    private final boolean inverted;

    public TaskCompletedCondition(@NotNull String taskId, boolean inverted) {
        this.taskId = taskId;
        this.inverted = inverted;
    }

    @NotNull
    public static TaskCompletedCondition load(@NotNull FileConfig config, @NotNull String path, boolean inverted) {
        String taskId = ConfigValue.create(path + ".TaskId", "null").read(config);

        return new TaskCompletedCondition(taskId, inverted);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".TaskId", this.taskId);
    }

    @NotNull
    @Override
    public String getName() {
        return this.inverted ? ConditionId.TASK_INCOMPLETED : ConditionId.TASK_COMPLETED;
    }

    @Override
    public boolean test(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        StageTask stageTask = dungeon.getStage().getTaskById(this.taskId);
        if (stageTask == null) {
            ErrorHandler.error("Invalid stage task '" + this.taskId + "'!", this, dungeon);
            return true;
        }

        return this.inverted != dungeon.isTaskCompleted(stageTask);
    }
}
