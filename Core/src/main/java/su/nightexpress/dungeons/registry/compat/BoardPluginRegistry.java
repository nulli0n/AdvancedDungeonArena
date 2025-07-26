package su.nightexpress.dungeons.registry.compat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.api.compat.BoardPlugin;
import su.nightexpress.dungeons.hook.HookId;
import su.nightexpress.dungeons.hook.impl.SunLightHook;
import su.nightexpress.dungeons.hook.impl.TABHook;
import su.nightexpress.nightcore.util.Plugins;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class BoardPluginRegistry {

    private static final Map<String, BoardPlugin> PLUGIN_BY_NAME = new HashMap<>();

    private static DungeonPlugin plugin;

    public static void load(@NotNull DungeonPlugin instance) {
        plugin = instance;
        loadProviders();
    }

    private static void loadProviders() {
        register(HookId.SUNLIGHT, SunLightHook::new);
        register(HookId.TAB, TABHook::new);
    }

    public static void clear() {
        PLUGIN_BY_NAME.clear();
        plugin = null;
    }

    public static void register(@NotNull String name, @NotNull Supplier<BoardPlugin> supplier) {
        if (!Plugins.isInstalled(name)) return;

        PLUGIN_BY_NAME.put(name, supplier.get());

        plugin.info("Registerd '" + name + "' as Board provider.");
    }

    @NotNull
    public static Set<BoardPlugin> getBoardProviders() {
        return new HashSet<>(PLUGIN_BY_NAME.values());
    }

    @Nullable
    public static BoardPlugin getBoardProvider(@NotNull Player player) {
        return getBoardProviders().stream().filter(boardPlugin -> boardPlugin.isBoardEnabled(player)).findFirst().orElse(null);
    }
}
