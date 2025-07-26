package su.nightexpress.dungeons.registry.compat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.api.compat.GodPlugin;
import su.nightexpress.dungeons.hook.HookId;
import su.nightexpress.dungeons.hook.impl.EssentialsHook;
import su.nightexpress.dungeons.hook.impl.SunLightHook;
import su.nightexpress.nightcore.util.Plugins;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class GodPluginRegistry {

    private static final Map<String, GodPlugin> PLUGIN_BY_NAME = new HashMap<>();

    private static DungeonPlugin plugin;

    public static void load(@NotNull DungeonPlugin instance) {
        plugin = instance;
        loadProviders();
    }

    private static void loadProviders() {
        register(HookId.SUNLIGHT, SunLightHook::new);
        register(HookId.ESSENTIALS, EssentialsHook::new);
    }

    public static void clear() {
        PLUGIN_BY_NAME.clear();
        plugin = null;
    }

    public static void register(@NotNull String name, @NotNull Supplier<GodPlugin> supplier) {
        if (!Plugins.isInstalled(name)) return;

        PLUGIN_BY_NAME.put(name, supplier.get());

        plugin.info("Registered '" + name + "' as God provider.");
    }

    @NotNull
    public static Set<GodPlugin> getGodProviders() {
        return new HashSet<>(PLUGIN_BY_NAME.values());
    }

    @Nullable
    public static GodPlugin getGodProvider(@NotNull Player player) {
        return getGodProviders().stream().filter(godPlugin -> godPlugin.isGodEnabled(player)).findFirst().orElse(null);
    }
}
