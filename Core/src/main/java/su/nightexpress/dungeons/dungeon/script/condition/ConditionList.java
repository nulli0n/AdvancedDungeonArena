package su.nightexpress.dungeons.dungeon.script.condition;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConditionList implements Writeable {

    private final Map<String, Condition> conditions;

    private ValidationType validationType;

    public ConditionList(@NotNull ValidationType validationType, @NotNull Map<String, Condition> conditions) {
        this.validationType = validationType;
        this.conditions = conditions;
    }

    @NotNull
    public static ConditionList read(@NotNull FileConfig config, @NotNull String path) {
        ValidationType type = config.getEnum(path + ".Validate", ValidationType.class, ValidationType.ALL);
        Map<String, Condition> conditions = new LinkedHashMap<>();

        config.getSection(path + ".List").forEach(sId -> {
            String conditionPath = path + ".List." + sId;
            String name = String.valueOf(config.getString(conditionPath + ".Type"));

            Condition condition = ConditionRegistry.loadCondition(name, config, conditionPath);
            if (condition == null) {
                ErrorHandler.error("Invalid condition '" + name + "'", config, conditionPath);
                return;
            }

            //conditions.add(condition);
            conditions.put(sId, condition);
        });

        return new ConditionList(type, conditions);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Validate", this.validationType.name());
        config.remove(path + ".List");

        this.conditions.forEach((id, condition) -> {
            config.set(path + ".List." + id + ".Type", condition.getName());
            condition.write(config, path + ".List." + id);
        });
    }

    public boolean validate(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        return switch (this.validationType) {
            case ALL -> this.getConditions().stream().allMatch(condition -> condition.test(dungeon, event));
            case ANY -> this.getConditions().stream().anyMatch(condition -> condition.test(dungeon, event));
            case NONE -> this.getConditions().stream().noneMatch(condition -> condition.test(dungeon, event));
        };
    }

    public void addCondition(@NotNull String name, @NotNull Condition condition) {
        this.conditions.put(name, condition);
    }

    public void clearConditions() {
        this.conditions.clear();
    }

    @NotNull
    public ValidationType getValidationType() {
        return this.validationType;
    }

    public void setValidationType(@NotNull ValidationType validationType) {
        this.validationType = validationType;
    }

    @NotNull
    public List<Condition> getConditions() {
        return new ArrayList<>(this.conditions.values());
    }
}
