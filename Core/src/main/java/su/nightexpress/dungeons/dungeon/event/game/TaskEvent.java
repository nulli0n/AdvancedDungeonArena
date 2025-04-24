package su.nightexpress.dungeons.dungeon.event.game;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.stage.StageTask;
import su.nightexpress.dungeons.dungeon.stage.task.TaskProgress;

public interface TaskEvent {

    @NotNull StageTask getStageTask();

    @NotNull TaskProgress getProgress();
}
