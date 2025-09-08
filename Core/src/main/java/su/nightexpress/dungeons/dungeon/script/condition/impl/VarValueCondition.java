package su.nightexpress.dungeons.dungeon.script.condition.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.game.Variable;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionId;
import su.nightexpress.dungeons.dungeon.script.condition.type.NumberCompareCondition;
import su.nightexpress.dungeons.dungeon.script.number.NumberComparator;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public class VarValueCondition extends NumberCompareCondition {

    private final String varName;

    public VarValueCondition(@NotNull String varName, @NotNull NumberComparator comparator, double compareValue) {
        super(comparator, compareValue);
        this.varName = varName;
    }

    @NotNull
    public static VarValueCondition load(@NotNull FileConfig config, @NotNull String path) {
        NumberData numberData = readNumberData(config, path);
        String varName = ConfigValue.create(path + ".Variable", "null").read(config);

        return new VarValueCondition(varName, numberData.comparator(), numberData.compareValue());
    }

    @Override
    protected void writeAdditional(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Variable", this.varName);
    }

    @NotNull
    @Override
    public String getName() {
        return ConditionId.VAR_VALUE;
    }

    @Override
    protected double getDungeonValue(@NotNull DungeonInstance dungeon) {
        Variable variable = dungeon.getVariables().getVariable(this.varName);
        if (variable == null) {
            ErrorHandler.error("Could not compare '" + this.varName + "' variable value: Variable not found.", this, dungeon);
            return 0D;
        }

        return variable.getValue();
    }
}
