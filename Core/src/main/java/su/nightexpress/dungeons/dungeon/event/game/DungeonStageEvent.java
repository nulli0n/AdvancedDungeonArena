package su.nightexpress.dungeons.dungeon.event.game;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;
import su.nightexpress.dungeons.dungeon.stage.Stage;

public abstract class DungeonStageEvent extends DungeonGameEvent implements StageEvent {

    private final Stage stage;

    public DungeonStageEvent(@NotNull DungeonEventType type, @NotNull DungeonInstance dungeon, @NotNull Stage stage) {
        super(type, dungeon);
        this.stage = stage;
    }

    @Override
    @NotNull
    public Stage getStage() {
        return this.stage;
    }
}
