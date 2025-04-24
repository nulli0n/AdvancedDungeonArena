package su.nightexpress.dungeons.dungeon.script.task.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;
import su.nightexpress.dungeons.dungeon.script.task.ProgressFormatter;
import su.nightexpress.dungeons.dungeon.script.task.Task;
import su.nightexpress.dungeons.dungeon.script.task.TaskId;
import su.nightexpress.dungeons.dungeon.stage.StageTask;
import su.nightexpress.dungeons.dungeon.stage.task.TaskProgress;
import su.nightexpress.nightcore.config.FileConfig;

public class KillMobsTask implements Task {

    public KillMobsTask() {

    }

    @NotNull
    public static KillMobsTask load(@NotNull FileConfig config, @NotNull String path) {
        return new KillMobsTask();
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {

    }

    @NotNull
    @Override
    public String getName() {
        return TaskId.KILL_MOBS;
    }

    @NotNull
    @Override
    public ProgressFormatter getFormatter() {
        return ProgressFormatter.NORMAL;
    }

    @Override
    public boolean canBePerPlayer() {
        return false;
    }

    @Override
    public void onTaskAdd(@NotNull DungeonInstance dungeon, @NotNull StageTask stageTask, @NotNull TaskProgress progress) {

    }

    @Override
    public void onTaskRemove(@NotNull DungeonInstance dungeon, @NotNull StageTask stageTask, @NotNull TaskProgress progress) {

    }

    @Override
    public void progress(@NotNull DungeonGameEvent event, @NotNull DungeonInstance dungeon, @NotNull StageTask stageTask, @NotNull TaskProgress progress) {
        if (event.getType() == DungeonEventType.MOB_KILLED) {
            progress.addProgress(1);
        }
    }
}
