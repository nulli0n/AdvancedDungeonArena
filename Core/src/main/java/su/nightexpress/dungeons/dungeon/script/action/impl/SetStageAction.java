package su.nightexpress.dungeons.dungeon.script.action.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.script.action.Action;
import su.nightexpress.dungeons.dungeon.script.action.ActionId;
import su.nightexpress.dungeons.dungeon.stage.Stage;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.FileConfig;

public class SetStageAction implements Action {

    private final String stageId;

    public SetStageAction(String stageId) {
        this.stageId = stageId;
    }

    @Override
    public void perform(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        Stage stage = dungeon.getConfig().getStageById(this.stageId);
        if (stage == null) {
            ErrorHandler.error("Could not set stage '" + this.stageId + "': stage does not exist.", this, dungeon);
            return;
        }

        dungeon.setStage(stage);
    }

    @NotNull
    public static SetStageAction load(@NotNull FileConfig config, @NotNull String path) {
        String stageId = config.getString(path + ".StageId", "null");

        return new SetStageAction(stageId);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".StageId", this.stageId);
    }

    @NotNull
    @Override
    public String getName() {
        return ActionId.SET_STAGE;
    }

    @NotNull
    public String getStageId() {
        return this.stageId;
    }
}
