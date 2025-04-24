package su.nightexpress.dungeons.dungeon.stage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.script.task.ProgressFormatter;
import su.nightexpress.dungeons.dungeon.script.task.Task;
import su.nightexpress.dungeons.dungeon.script.task.TaskParams;
import su.nightexpress.dungeons.dungeon.script.task.TaskRegistry;
import su.nightexpress.dungeons.dungeon.stage.task.GlobalProgress;
import su.nightexpress.dungeons.dungeon.stage.task.PersonalProgress;
import su.nightexpress.dungeons.dungeon.stage.task.TaskProgress;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

public class StageTask implements Writeable {

    private final String     id;
    private final Task       task;
    private final TaskParams params;

    public StageTask(@NotNull String id, @NotNull Task task, @NotNull TaskParams params) {
        this.id = id.toLowerCase();
        this.task = task;
        this.params = params;
    }

    @Nullable
    public static StageTask read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        String type = config.getString(path + ".Type");
        if (type == null) {
            ErrorHandler.error("Task type not defined!", config, path);
            return null;
        }

        Task task = TaskRegistry.loadTask(type, config, path);
        if (task == null) {
            ErrorHandler.error("Invalid task type '" + type + "'!", config, path);
            return null;
        }

        TaskParams params = TaskParams.read(config, path);
        return new StageTask(id, task, params);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Type", this.task.getName());
        config.set(path, this.params);
        config.set(path, this.task);
    }

    @NotNull
    public TaskProgress createProgress(@NotNull DungeonInstance dungeon) {
        ProgressFormatter formatter = this.task.getFormatter();
        int requiredAmount = this.params.getAmount().roll();

        if (this.params.isPerPlayer() && this.task.canBePerPlayer()) {
            int players = dungeon.getAlivePlayers().size();
            int totalAmount = requiredAmount * players;

            return new PersonalProgress(dungeon, formatter, totalAmount, requiredAmount);
        }

        return new GlobalProgress(formatter, requiredAmount);
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public Task getTask() {
        return this.task;
    }

    @NotNull
    public TaskParams getParams() {
        return this.params;
    }
}
