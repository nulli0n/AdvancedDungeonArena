package su.nightexpress.dungeons.dungeon.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.script.action.Action;
import su.nightexpress.dungeons.dungeon.script.action.ActionInfo;
import su.nightexpress.dungeons.dungeon.script.action.ActionRegistry;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionList;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

import java.util.*;

public class DungeonEventHandler implements Writeable {

    private final String              id;
    private final ConditionList           conditionList;
    private final Map<String, ActionInfo> actionMap;

    private DungeonEventType eventType;

    public DungeonEventHandler(@NotNull String id, @NotNull DungeonEventType eventType, @NotNull ConditionList conditionList, @NotNull Map<String, ActionInfo> actionMap) {
        this.id = id;
        this.setEventType(eventType);
        this.conditionList = conditionList;
        this.actionMap = actionMap;
    }

    @NotNull
    public static DungeonEventHandler read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        DungeonEventType eventType = config.getEnum(path + ".Event", DungeonEventType.class, DungeonEventType.DUNGEON_TICK);
        ConditionList conditionList = ConditionList.read(config, path + ".Conditions");

        Map<String, ActionInfo> actionMap = new LinkedHashMap<>();
        config.getSection(path + ".Actions").forEach(sId -> {
            String actionPath = path + ".Actions." + sId;
            String name = ConfigValue.create(actionPath + ".Type", "null").read(config);
            double chance = ConfigValue.create(actionPath + ".Chance", 100D).read(config);

            Action action = ActionRegistry.loadAction(name, config, actionPath);
            if (action == null) {
                ErrorHandler.error("Invalid action '" + name + "'!", config, path);
                return;
            }

            actionMap.put(sId, new ActionInfo(chance, action));
        });

        return new DungeonEventHandler(id, eventType, conditionList, actionMap);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Event", this.eventType.name());
        config.set(path + ".Conditions", this.conditionList);

        config.remove(path + ".Actions");
        this.actionMap.forEach((id, action) -> {
            String actionPath = path + ".Actions." + id;

            config.set(actionPath + ".Type", action.getAction().getName());
            config.set(actionPath + ".Chance", action.getChance());
            config.set(actionPath, action.getAction());
        });
    }

    public boolean handleEvent(@NotNull DungeonGameEvent event, @NotNull DungeonEventType eventType, @NotNull DungeonInstance dungeon) {
        if (!this.canHandle(eventType)) return false;
        if (!this.conditionList.validate(dungeon, event)) return false;

        //System.out.println("EventListener " + this.getId() + " passed all checks and ready to action");
        this.getActions().forEach(actionInfo -> actionInfo.run(dungeon, event));
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
    public ConditionList getConditionList() {
        return this.conditionList;
    }

    @NotNull
    public List<ActionInfo> getActions() {
        return new ArrayList<>(this.actionMap.values());
    }
}
