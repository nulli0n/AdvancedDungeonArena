package su.nightexpress.dungeons.dungeon.script.task;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.dungeon.stage.task.TaskProgress;

public interface ProgressFormatter {

    // TODO Lang formats

    ProgressFormatter NORMAL = (progress, target) -> {
        int current = progress.countProgress(target);
        int required = progress.getRequiredAmount(target);

        return current + "/" + required;
    };

    ProgressFormatter TIME_DIGITAL = (progress, target) -> {
        int time = progress.countLeftover(target);
        int minutes = time / 60;
        int seconds = time % 60;

        return String.format("%02d:%02d", minutes, seconds);
    };

    @NotNull String format(@NotNull TaskProgress progress, @Nullable Player target);
}
