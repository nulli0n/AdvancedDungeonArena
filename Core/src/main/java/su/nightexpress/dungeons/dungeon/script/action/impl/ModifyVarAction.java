package su.nightexpress.dungeons.dungeon.script.action.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.game.DungeonVariables;
import su.nightexpress.dungeons.dungeon.game.Variable;
import su.nightexpress.dungeons.dungeon.script.action.Action;
import su.nightexpress.dungeons.dungeon.script.action.ActionId;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public class ModifyVarAction implements Action {

    private final String    varName;
    private final Operation operation;
    private final double    value;

    public enum Operation {
        PLUS, MINUS, MULTIPLY, DIVIDE
    }

    public ModifyVarAction(@NotNull String varName, @NotNull Operation operation, double value) {
        this.varName = varName.toLowerCase();
        this.operation = operation;
        this.value = value;
    }

    @NotNull
    public static ModifyVarAction load(@NotNull FileConfig config, @NotNull String path) {
        String varName = ConfigValue.create(path + ".Variable", "null").read(config);
        Operation operation = ConfigValue.create(path + ".Operation", Operation.class, Operation.PLUS).read(config);
        double amount = ConfigValue.create(path + ".Value", 0D).read(config);

        return new ModifyVarAction(varName, operation, amount);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Variable", this.varName);
        config.set(path + ".Operation", this.operation);
        config.set(path + ".Value", this.value);
    }

    @NotNull
    @Override
    public String getName() {
        return ActionId.MODIFY_VAR;
    }

    @Override
    public void perform(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        DungeonVariables variables = dungeon.getVariables();
        Variable variable = variables.getVariable(this.varName);

        if (variable == null) {
            ErrorHandler.error("Variable '" + this.varName + "' does not exist.", this, dungeon);
            return;
        }

        variable.modify(this::modifyAmount);
    }

    private double modifyAmount(double origin) {
        return switch (this.operation) {
            case PLUS -> origin + this.value;
            case MINUS -> origin - this.value;
            case MULTIPLY -> origin * this.value;
            case DIVIDE -> origin / this.value;
        };
    }
}
