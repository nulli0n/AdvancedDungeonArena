package su.nightexpress.dungeons.dungeon.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.placeholder.Replacer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public class DungeonVariables {

    private final Map<String, Variable> variableMap;

    public DungeonVariables() {
        this.variableMap = new HashMap<>();
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        Replacer replacer = Replacer.create();

        this.variableMap.forEach((name, variable) -> {
            double value = variable.getValue();

            replacer
                .replace(Placeholders.DUNGEON_VAR_RAW.apply(name), String.valueOf(value))
                .replace(Placeholders.DUNGEON_VAR.apply(name), NumberUtil.format(value));
        });

        return replacer::apply;
    }

    public void clear() {
        this.variableMap.clear();
    }

    @Nullable
    public Variable getVariable(@NotNull String name) {
        return this.variableMap.get(name);
    }

    public boolean hasVariable(@NotNull String name) {
        return this.getVariable(name) != null;
    }

    public void createLimitedVariable(@NotNull String name, double initial, double min, double max) {
        this.createVariable(name, new Variable(initial, true, min, max));
    }

    public void createUnlimitedVariable(@NotNull String name, double initial) {
        this.createVariable(name, new Variable(initial, false, -1, -1));
    }

    private void createVariable(@NotNull String name, @NotNull Variable variable) {
        this.variableMap.put(name, variable);
    }

    public void removeVariable(@NotNull String name) {
        this.variableMap.remove(name);
    }
}
