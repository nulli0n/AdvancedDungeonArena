package su.nightexpress.dungeons.dungeon.event.game;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;
import su.nightexpress.dungeons.dungeon.stage.StageTask;
import su.nightexpress.dungeons.dungeon.stage.task.TaskProgress;

public abstract class DungeonTaskEvent extends DungeonGameEvent implements TaskEvent {

    private final StageTask stageTask;
    private final TaskProgress progress;

    public DungeonTaskEvent(@NotNull DungeonEventType type, @NotNull DungeonInstance dungeon, @NotNull StageTask stageTask, @NotNull TaskProgress progress) {
        super(type, dungeon);
        this.stageTask = stageTask;
        this.progress = progress;
    }

    @Override
    @NotNull
    public StageTask getStageTask() {
        return this.stageTask;
    }

    @Override
    @NotNull
    public TaskProgress getProgress() {
        return this.progress;
    }
}
