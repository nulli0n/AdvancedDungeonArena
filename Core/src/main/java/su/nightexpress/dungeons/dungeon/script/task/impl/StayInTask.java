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

public class StayInTask extends AreaTask {

    public StayInTask(int radius, int height, @NotNull BlockPos targetPos) {
        super(radius, height, targetPos);
    }

    @NotNull
    public static StayInTask load(@NotNull FileConfig config, @NotNull String path) {
        return load(config, path, StayInTask::new);
    }

    @NotNull
    @Override
    public String getName() {
        return TaskId.STAY_IN;
    }

    @NotNull
    @Override
    public ProgressFormatter getFormatter() {
        return ProgressFormatter.TIME_DIGITAL;
    }

    @Override
    protected void onTaskProgress(@NotNull DungeonGameEvent event, @NotNull DungeonInstance dungeon, @NotNull StageTask stageTask, @NotNull TaskProgress progress) {
        boolean allIn = dungeon.hasAlivePlayers() && dungeon.getAlivePlayers().stream().allMatch(this::isInside);

        if (allIn) {
            progress.addProgress(1);
        }

        this.setAreaColor(stageTask, allIn ? Color.LIME : Color.RED);
    }
}
