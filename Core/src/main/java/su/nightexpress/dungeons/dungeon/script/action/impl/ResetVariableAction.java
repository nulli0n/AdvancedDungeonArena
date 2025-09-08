package su.nightexpress.dungeons.dungeon.script.action.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.game.Variable;
import su.nightexpress.dungeons.dungeon.script.action.Action;
import su.nightexpress.dungeons.dungeon.script.action.ActionId;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.List;

public class ResetVariableAction implements Action {

    private final List<String> varNames;

    public ResetVariableAction(@NotNull List<String> varNames) {
        this.varNames = varNames;
    }

    @NotNull
    public static ResetVariableAction load(@NotNull FileConfig config, @NotNull String path) {
        List<String> varNames = config.getStringList(path + ".Variables");

        return new ResetVariableAction(varNames);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Variables", this.varNames);
    }

    @NotNull
    @Override
    public String getName() {
        return ActionId.RESET_VARIABLE;
    }

    @Override
    public void perform(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        this.varNames.forEach(name -> {
            Variable variable = dungeon.getVariables().getVariable(name);
            if (variable == null) {
                ErrorHandler.error("Could not reset '" + name + "' variable: Variable not defined.", this, dungeon);
                return;
            }

            variable.reset();
        });
    }
}
