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

public class AddTaskAction implements Action {

    private final String taskId;
    private final boolean replace;

    public AddTaskAction(@NotNull String taskId, boolean replace) {
        this.taskId = taskId;
        this.replace = replace;
    }

    @NotNull
    public static AddTaskAction load(@NotNull FileConfig config, @NotNull String path) {
        String taskId = config.getString(path + ".TaskId", "null");
        boolean replace = config.getBoolean(path + ".Replace", false);

        return new AddTaskAction(taskId, replace);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".TaskId", this.taskId);
        config.set(path + ".Replace", this.replace);
    }

    @Override
    public void perform(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        Stage stage = dungeon.getStage();
        StageTask stageTask = stage.getTaskById(this.taskId);
        if (stageTask == null) {
            ErrorHandler.error("Invalid task '" + this.taskId + "'!", this, dungeon);
            return;
        }

        if (this.replace || !dungeon.hasTask(stageTask)) {
            dungeon.addTask(stageTask);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return ActionId.ADD_TASK;
    }
}
