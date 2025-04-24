package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.script.condition.Condition;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.random.Rnd;

public class ChanceCondition implements Condition {

    private final double chance;

    public ChanceCondition(double chance) {
        this.chance = chance;
    }

    @NotNull
    public static ChanceCondition load(@NotNull FileConfig config, @NotNull String path) {
        double chance = ConfigValue.create(path + ".Chance", 50D).read(config);

        return new ChanceCondition(chance);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Chance", this.chance);
    }

    @NotNull
    @Override
    public String getName() {
        return ConditionId.CHANCE;
    }

    @Override
    public boolean test(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        return Rnd.chance(this.chance);
    }
}
