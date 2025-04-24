package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.dungeons.dungeon.spot.Spot;
import su.nightexpress.dungeons.dungeon.spot.SpotState;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.script.condition.Condition;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public class SpotStateCondition implements Condition {

    private final String spotId;
    private final String stateId;
    private final boolean inverted;

    public SpotStateCondition(@NotNull String spotId, @NotNull String stateId, boolean inverted) {
        this.spotId = spotId;
        this.stateId = stateId;
        this.inverted = inverted;
    }

    @NotNull
    public static SpotStateCondition load(@NotNull FileConfig config, @NotNull String path, boolean inverted) {
        String spotId = ConfigValue.create(path + ".SpotId", "null").read(config);
        String stateId = ConfigValue.create(path + ".StateId", Placeholders.DEFAULT).read(config);

        return new SpotStateCondition(spotId, stateId, inverted);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".SpotId", this.spotId);
        config.set(path + ".StateId", this.stateId);
    }

    @NotNull
    @Override
    public String getName() {
        return this.inverted ? ConditionId.SPOT_NOT_IN_STATE : ConditionId.SPOT_HAS_STATE;
    }

    @Override
    public boolean test(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        Spot spot = dungeon.getConfig().getSpotById(this.spotId);
        if (spot == null) {
            ErrorHandler.error("Invalid spot '" + this.spotId + "'!", this, dungeon);
            return false;
        }
        if (!spot.getId().equalsIgnoreCase(this.spotId)) return false;

        SpotState state = spot.getState(this.stateId);
        if (state == null) {
            ErrorHandler.error("Invalid spot state '" + this.stateId + "'!", this, dungeon);
            return false;
        }

        return this.inverted != spot.getLastState().equalsIgnoreCase(this.stateId);
    }
}
