package su.nightexpress.dungeons.dungeon.script.task.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.mob.MobIdentifier;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.event.game.DungeonMobKilledEvent;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.script.task.ProgressFormatter;
import su.nightexpress.dungeons.dungeon.script.task.Task;
import su.nightexpress.dungeons.dungeon.script.task.TaskId;
import su.nightexpress.dungeons.dungeon.stage.StageTask;
import su.nightexpress.dungeons.dungeon.stage.task.TaskProgress;
import su.nightexpress.nightcore.config.FileConfig;

public class KillMobTask implements Task {

    private final MobIdentifier identifier;

    public KillMobTask(@NotNull MobIdentifier identifier) {
        this.identifier = identifier;
    }

    @NotNull
    public static KillMobTask load(@NotNull FileConfig config, @NotNull String path) {
        MobIdentifier mobId = MobIdentifier.read(config, path + ".MobId");

        return new KillMobTask(mobId);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".MobId", this.identifier);
    }

    @NotNull
    @Override
    public String getName() {
        return TaskId.KILL_MOB;
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
        if (event instanceof DungeonMobKilledEvent mobKilledEvent) {
            if (mobKilledEvent.getDungeonMob().isMob(this.identifier)) {
                progress.addProgress(1);
            }
        }
    }
}
