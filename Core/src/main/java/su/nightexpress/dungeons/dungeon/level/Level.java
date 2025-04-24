package su.nightexpress.dungeons.dungeon.level;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.event.DungeonEventHandler;
import su.nightexpress.dungeons.dungeon.event.DungeonEventReceiver;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.geodata.pos.ExactPos;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

public class Level extends AbstractFileData<DungeonPlugin> implements DungeonEventReceiver {

    private final Map<String, DungeonEventHandler> eventHandlers;

    private String displayName;
    private String   description;
    private ExactPos spawnPos;

    public Level(@NotNull DungeonPlugin plugin, @NotNull File file) {
        super(plugin, file);
        this.eventHandlers = new LinkedHashMap<>();
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        this.setDisplayName(config.getString("Name", "null"));
        this.setDescription(config.getString("Description", ""));

        this.setSpawnPos(ExactPos.read(config, "SpawnPos"));

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
        this.spawnPos.write(config, "SpawnPos");

        config.remove("EventHandlers");
        this.eventHandlers.forEach((id, handler) -> config.set("EventHandlers." + id, handler));
    }

    @Override
    public void addHandler(@NotNull DungeonEventHandler handler) {
        this.eventHandlers.put(handler.getId(), handler);
    }

    @Override
    public boolean onDungeonEventBroadcastReceive(@NotNull DungeonGameEvent event, @NotNull DungeonEventType eventType, @NotNull DungeonInstance dungeon) {
        if (!dungeon.isLevel(this)) return false;

        //System.out.println("Level event received: " + this.getDisplayName() +" / " + eventType.name());
        this.getEventHandlers().forEach(listener -> listener.handleEvent(event, eventType, dungeon));
        return true;
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return Placeholders.LEVEL.replacer(this);
    }

    @NotNull
    public Location getSpawnLocation(@NotNull World world) {
        return this.spawnPos.toLocation(world);
    }

    @NotNull
    public Set<DungeonEventHandler> getEventHandlers() {
        return new HashSet<>(this.eventHandlers.values());
    }

    @NotNull
    public Map<String, DungeonEventHandler> getEventHandlerMap() {
        return this.eventHandlers;
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

    @NotNull
    public ExactPos getSpawnPos() {
        return this.spawnPos;
    }

    public void setSpawnPos(@NotNull ExactPos spawnPos) {
        this.spawnPos = spawnPos;
    }
}
