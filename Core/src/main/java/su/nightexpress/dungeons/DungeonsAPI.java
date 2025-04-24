package su.nightexpress.dungeons;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.data.DataHandler;
import su.nightexpress.dungeons.dungeon.DungeonManager;
import su.nightexpress.dungeons.dungeon.DungeonSetup;
import su.nightexpress.dungeons.mob.MobManager;
import su.nightexpress.dungeons.user.UserManager;
import su.nightexpress.dungeons.kit.KitManager;
import su.nightexpress.dungeons.nms.DungeonNMS;

public class DungeonsAPI {

    private static DungeonPlugin plugin;

    static void load(@NotNull DungeonPlugin dungeonPlugin) {
        plugin = dungeonPlugin;
    }

    static void clear() {
        plugin = null;
    }

    @NotNull
    public static DungeonPlugin getPlugin() {
        return plugin;
    }

    @NotNull
    public static UserManager getUserManager() {
        return plugin.getUserManager();
    }

    @NotNull
    public static DataHandler getDataHandler() {
        return plugin.getDataHandler();
    }

    @NotNull
    public static DungeonManager getDungeonManager() {
        return plugin.getDungeonManager();
    }

    @NotNull
    public static DungeonSetup getDungeonSetup() {
        return plugin.getDungeonSetup();
    }

    @NotNull
    public static MobManager getMobManager() {
        return plugin.getMobManager();
    }

    @NotNull
    public static KitManager getKitManager() {
        return plugin.getKitManager();
    }

    @NotNull
    public static DungeonNMS getArenaNMS() {
        return plugin.getInternals();
    }
}
