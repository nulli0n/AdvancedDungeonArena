package su.nightexpress.dungeons.dungeon.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.dungeon.DungeonManager;
import su.nightexpress.nightcore.manager.AbstractListener;

public class DungeonGenericListener extends AbstractListener<DungeonPlugin> {

    private final DungeonManager manager;

    public DungeonGenericListener(@NotNull DungeonPlugin plugin, @NotNull DungeonManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.manager.leaveInstance(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldLoad(WorldLoadEvent event) {
        this.manager.getInstances().forEach(dungeonInstance -> dungeonInstance.activate(event.getWorld()));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldUnload(WorldUnloadEvent event) {
        this.manager.getInstances().forEach(dungeonInstance -> dungeonInstance.deactivate(event.getWorld()));
    }
}
