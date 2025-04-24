package su.nightexpress.dungeons.dungeon.script.task.impl;

import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.script.task.ProgressFormatter;
import su.nightexpress.dungeons.dungeon.script.task.TaskId;
import su.nightexpress.dungeons.dungeon.script.task.type.AreaTask;
import su.nightexpress.dungeons.dungeon.stage.StageTask;
import su.nightexpress.dungeons.dungeon.stage.task.TaskProgress;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;

public class MoveToTask extends AreaTask {

    public MoveToTask(int radius, int height, @NotNull BlockPos targetPos) {
        super(radius, height, targetPos);
    }

    @NotNull
    public static MoveToTask load(@NotNull FileConfig config, @NotNull String path) {
        return load(config, path, MoveToTask::new);
    }

    @NotNull
    @Override
    public String getName() {
        return TaskId.MOVE_TO;
    }

    @NotNull
    @Override
    public ProgressFormatter getFormatter() {
        return ProgressFormatter.NORMAL;
    }

    @Override
    public void onTaskAdd(@NotNull DungeonInstance dungeon, @NotNull StageTask stageTask, @NotNull TaskProgress progress) {
        super.onTaskAdd(dungeon, stageTask, progress);

        progress.setRequiredAmount(dungeon.countAlivePlayers());
    }

    @Override
    protected void onTaskProgress(@NotNull DungeonGameEvent event, @NotNull DungeonInstance dungeon, @NotNull StageTask stageTask, @NotNull TaskProgress progress) {
        progress.resetProgress();
        progress.setRequiredAmount(dungeon.countAlivePlayers());

        boolean allIn = dungeon.hasAlivePlayers() && dungeon.getAlivePlayers().stream().allMatch(gamer -> {
            if (this.isInside(gamer)) {
                progress.addProgress(1);
                return true;
            }
            return false;
        });

        this.setAreaColor(stageTask, allIn ? Color.LIME : Color.RED);
    }
}
