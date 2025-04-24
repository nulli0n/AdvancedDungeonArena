package su.nightexpress.dungeons.dungeon.script.action.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.script.action.Action;
import su.nightexpress.dungeons.dungeon.script.action.ActionId;
import su.nightexpress.dungeons.dungeon.stage.Stage;
import su.nightexpress.dungeons.dungeon.stage.StageTask;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.FileConfig;

public class RemoveTaskAction implements Action {

    private final String taskId;

    public RemoveTaskAction(@NotNull String taskId) {
        this.taskId = taskId;
    }

    @NotNull
    public static RemoveTaskAction load(@NotNull FileConfig config, @NotNull String path) {
        String taskId = config.getString(path + ".TaskId", "null");
        return new RemoveTaskAction(taskId);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".TaskId", this.taskId);
    }

    @Override
    public void perform(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        Stage stage = dungeon.getStage();
        StageTask stageTask = stage.getTaskById(this.taskId);
        if (stageTask == null) {
            ErrorHandler.error("Invalid task '" + this.taskId + "'!", this, dungeon);
            return;
        }

        dungeon.removeTask(stageTask);
    }

    @NotNull
    @Override
    public String getName() {
        return ActionId.REMOVE_TASK;
    }
}
