package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.event.game.StageEvent;
import su.nightexpress.dungeons.dungeon.script.condition.Condition;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public class StageIdCondition implements Condition {

    private final String stageId;

    public StageIdCondition(@NotNull String stageId) {
        this.stageId = stageId;
    }

    @NotNull
    public static StageIdCondition load(@NotNull FileConfig config, @NotNull String path) {
        String id = ConfigValue.create(path + ".StageId", "null").read(config);

        return new StageIdCondition(id);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".StageId", this.stageId);
    }

    @NotNull
    @Override
    public String getName() {
        return ConditionId.STAGE_ID;
    }

    @Override
    public boolean test(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        return event instanceof StageEvent stageEvent && stageEvent.getStage().getId().equalsIgnoreCase(this.stageId);
    }
}
