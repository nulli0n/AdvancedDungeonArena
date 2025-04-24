package su.nightexpress.dungeons.dungeon.script.action.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.spot.Spot;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.script.action.Action;
import su.nightexpress.dungeons.dungeon.script.action.ActionId;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public class ResetSpotAction implements Action {

    private final String spotId;

    public ResetSpotAction(String spotId) {
        this.spotId = spotId;
    }

    @NotNull
    public static ResetSpotAction load(@NotNull FileConfig config, @NotNull String path) {
        String spotId = ConfigValue.create(path + ".SpotId", "null").read(config);

        return new ResetSpotAction(spotId);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".SpotId", this.spotId);
    }

    @NotNull
    @Override
    public String getName() {
        return ActionId.RESET_SPOT;
    }

    @Override
    public void perform(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        Spot spot = dungeon.getConfig().getSpotById(this.spotId);
        if (spot == null) {
            ErrorHandler.error("Invalid spot '" + this.spotId + "'!", this, dungeon);
            return;
        }

        dungeon.resetSpotState(spot);
    }
}
