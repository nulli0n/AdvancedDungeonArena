package su.nightexpress.dungeons.dungeon.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.script.action.Action;
import su.nightexpress.dungeons.dungeon.script.action.ActionInfo;
import su.nightexpress.dungeons.dungeon.script.action.ActionRegistry;
import su.nightexpress.dungeons.dungeon.script.condition.Condition;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionInfo;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionRegistry;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.dungeons.util.PredicateParser;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

import java.util.*;
import java.util.stream.Collectors;

public class DungeonEventHandler implements Writeable {

    private final String                     id;
    private final Map<String, ConditionInfo> conditions;
    private final Map<String, ActionInfo>    actionMap;

    private DungeonEventType eventType;

    public DungeonEventHandler(@NotNull String id, @NotNull DungeonEventType eventType, @NotNull Map<String, ConditionInfo> conditions, @NotNull Map<String, ActionInfo> actionMap) {
        this.id = id;
        this.setEventType(eventType);
        this.conditions = conditions;
        this.actionMap = actionMap;
    }

    @NotNull
    public static DungeonEventHandler read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        DungeonEventType eventType = config.getEnum(path + ".Event", DungeonEventType.class, DungeonEventType.DUNGEON_TICK);

        // --------- CONVERT OLD CONDITIONS - START ---------
        if (config.contains(path + ".Conditions.Validate")) {
            String validationType = config.getString(path + ".Validate", "ALL");
            String boolOperator = validationType.equalsIgnoreCase("ANY") ? " ||" : " && ";
            String conditionPrefix = validationType.equalsIgnoreCase("NONE") ? "!" : "";

            Map<String, Condition> oldConditions = new HashMap<>();
            config.getSection(path + ".Conditions.List").forEach(sId -> {
                String conditionPath = path + ".Conditions.List." + sId;
                String name = String.valueOf(config.getString(conditionPath + ".Type"));

                Condition condition = ConditionRegistry.loadCondition(name, config, conditionPath);
                if (condition == null) return;

                oldConditions.put(sId, condition);
            });
            if (!oldConditions.isEmpty()) {
                String convertedConditon = oldConditions.keySet().stream().map(name -> conditionPrefix + name).collect(Collectors.joining(boolOperator));

                config.getSection(path + ".Actions").forEach(sId -> {
                    config.addMissing(path + ".Actions." + sId + ".RunIf", convertedConditon);
                });
            }

            config.remove(path + ".Conditions");
            oldConditions.forEach((name, condition) -> {
                config.set(path + ".Conditions." + name + ".Type", condition.getName());
                config.set(path + ".Conditions." + name, condition);
            });
        }
        // --------- CONVERT OLD CONDITIONS - END ---------

        Map<String, ConditionInfo> conditions = new LinkedHashMap<>();
        config.getSection(path + ".Conditions").forEach(sId -> {
            String conditionPath = path + ".Conditions." + sId;
            String name = String.valueOf(config.getString(conditionPath + ".Type"));

            Condition condition = ConditionRegistry.loadCondition(name, config, conditionPath);
            if (condition == null) {
                ErrorHandler.error("Invalid condition '" + name + "'", config, conditionPath);
                return;
            }

            boolean cached = ConfigValue.create(conditionPath + ".Cached", false).read(config);

            conditions.put(sId, new ConditionInfo(cached, condition));
        });

        Map<String, ActionInfo> actionMap = new LinkedHashMap<>();
        config.getSection(path + ".Actions").forEach(sId -> {
            String actionPath = path + ".Actions." + sId;

            String name = ConfigValue.create(actionPath + ".Type", "null").read(config);
            double chance = config.getDouble(actionPath + ".Chance", 100D); // Do not generate config entry unless explicitly set by user.
            String condition = config.getString(actionPath + ".RunIf");

            Action action = ActionRegistry.loadAction(name, config, actionPath);
            if (action == null) {
                ErrorHandler.error("Invalid action '" + name + "'!", config, path);
                return;
            }

            actionMap.put(sId, new ActionInfo(condition, chance, action));
        });

        return new DungeonEventHandler(id, eventType, conditions, actionMap);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Event", this.eventType.name());

        config.remove(path + ".Conditions");
        this.conditions.forEach((name, info) -> {
            String conditionPath = path + ".Conditions." + name;

            config.set(conditionPath + ".Type", info.getCondition().getName());
            config.set(conditionPath + ".Cached", info.isCached());
            config.set(conditionPath, info.getCondition());
        });

        config.remove(path + ".Actions");
        this.actionMap.forEach((id, action) -> {
            String actionPath = path + ".Actions." + id;

            config.set(actionPath + ".Type", action.getAction().getName());
            config.set(actionPath + ".RunIf", action.getRunIfCondition());
            config.set(actionPath + ".Chance", action.getChance());
            config.set(actionPath, action.getAction());
        });
    }

    public boolean handleEvent(@NotNull DungeonGameEvent event, @NotNull DungeonEventType eventType, @NotNull DungeonInstance dungeon) {
        if (!this.canHandle(eventType)) return false;

        PredicateParser parser = new PredicateParser();
        this.conditions.forEach((name, condition) -> {
            if (condition.isCached()) {
                boolean result = condition.getCondition().test(dungeon, event);
                parser.register(name,  o -> result);
            }
            else {
                parser.register(name, o -> condition.getCondition().test(dungeon, event));
            }
        });

        this.getActions().forEach(actionInfo -> {
            String runIfCondition = actionInfo.getRunIfCondition();
            if (runIfCondition != null && !parser.parse(runIfCondition).test(null)) {
                return;
            }
            actionInfo.run(dungeon, event);
        });
        return true;
    }

    public boolean canHandle(@NotNull DungeonEventType eventType) {
        return this.eventType == eventType;
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public DungeonEventType getEventType() {
        return this.eventType;
    }

    public void setEventType(@NotNull DungeonEventType eventType) {
        this.eventType = eventType;
    }

    @NotNull
    public List<ActionInfo> getActions() {
        return new ArrayList<>(this.actionMap.values());
    }

    @NotNull
    public List<ConditionInfo> getConditions() {
        return new ArrayList<>(this.conditions.values());
    }
}
