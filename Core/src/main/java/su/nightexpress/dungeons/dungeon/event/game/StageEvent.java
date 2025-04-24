package su.nightexpress.dungeons.dungeon.event.game;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.stage.Stage;

public interface StageEvent {

    @NotNull Stage getStage();
}
