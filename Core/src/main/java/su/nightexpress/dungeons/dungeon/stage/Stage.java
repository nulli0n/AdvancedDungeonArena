package su.nightexpress.dungeons.dungeon.stage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.event.DungeonEventHandler;
import su.nightexpress.dungeons.dungeon.event.DungeonEventReceiver;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.StringUtil;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

public class Stage extends AbstractFileData<DungeonPlugin> implements DungeonEventReceiver {

    private final Map<String, StageTask>           taskMap;
    private final Map<String, DungeonEventHandler> handlerMap;

    private String displayName;
    private String description;

    public Stage(@NotNull DungeonPlugin plugin, @NotNull File file) {
        super(plugin, file);

        this.taskMap = new LinkedHashMap<>();
        this.handlerMap = new LinkedHashMap<>();
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        this.setDisplayName(config.getString("Name", StringUtil.capitalizeUnderscored(this.getId())));
        this.setDescription(config.getString("Description", ""));

        config.getSection("Tasks").forEach(sId -> {
            StageTask stageTask = StageTask.read(config, "Tasks." + sId, sId);
            if (stageTask == null) {
                ErrorHandler.error("Stage task '" + sId + "' not loaded due to errors.", config, "Tasks." + sId);
                return;
            }

            this.taskMap.put(stageTask.getId(), stageTask);
        });

        config.getSection("EventHandlers").forEach(sId -> {
            DungeonEventHandler handler = DungeonEventHandler.read(config, "EventHandlers." + sId, sId);
            this.addHandler(handler);
        });

        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Name", this.displayName);
        config.set("Description", this.description);

        config.remove("Tasks");
        this.taskMap.forEach((id, stageTask) -> config.set("Tasks." + id, stageTask));

        config.remove("EventHandlers");
        this.handlerMap.forEach((id, handler) -> config.set("EventHandlers." + id, handler));
    }

    @Override
    public boolean onDungeonEventBroadcastReceive(@NotNull DungeonGameEvent event, @NotNull DungeonEventType eventType, @NotNull DungeonInstance dungeon) {
        if (!dungeon.isStage(this)) return false;

        this.getEventHandlers().forEach(listener -> listener.handleEvent(event, eventType, dungeon));
        return true;
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return Placeholders.STAGE.replacer(this);
    }

    @Override
    public void addHandler(@NotNull DungeonEventHandler handler) {
        this.handlerMap.put(handler.getId(), handler);
    }

    @NotNull
    public Set<DungeonEventHandler> getEventHandlers() {
        return new HashSet<>(this.handlerMap.values());
    }

    @NotNull
    public Set<StageTask> getTasks() {
        return new HashSet<>(this.taskMap.values());
    }

    @Nullable
    public StageTask getTaskById(@NotNull String id) {
        return this.taskMap.get(id.toLowerCase());
    }

    @NotNull
    public Map<String, DungeonEventHandler> getHandlerMap() {
        return this.handlerMap;
    }

    @NotNull
    public Map<String, StageTask> getTaskMap() {
        return this.taskMap;
    }

    @NotNull
    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(@NotNull String displayName) {
        this.displayName = displayName;
    }

    @NotNull
    public String getDescription() {
        return this.description;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }
}
