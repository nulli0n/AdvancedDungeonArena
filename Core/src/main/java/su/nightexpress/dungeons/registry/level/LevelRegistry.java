package su.nightexpress.dungeons.registry.level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.hook.HookId;
import su.nightexpress.dungeons.registry.level.provider.AuroraLevelsProvider;
import su.nightexpress.dungeons.registry.level.provider.MMOCoreLevelProvider;
import su.nightexpress.nightcore.util.Plugins;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class LevelRegistry {

    private static final Map<String, LevelProvider> BY_ID_MAP = new HashMap<>();

    private static DungeonPlugin plugin;

    public static void load(@NotNull DungeonPlugin dungeonPlugin) {
        plugin = dungeonPlugin;

        loadIntegration(HookId.MMOCORE, MMOCoreLevelProvider::new);
        loadIntegration(HookId.AURORA_LEVELS, AuroraLevelsProvider::new);
    }

    private static void loadIntegration(@NotNull String pluginName, @NotNull Supplier<LevelProvider> supplier) {
        if (!Plugins.isInstalled(pluginName)) return;

        register(supplier.get());
    }

    public static void clear() {
        BY_ID_MAP.clear();
        plugin = null;
    }

    public static void register(@NotNull LevelProvider provider) {
        BY_ID_MAP.put(provider.getName().toLowerCase(), provider);
        plugin.info("Registered level provider: '" + provider.getName() + "'.");
    }

    @Nullable
    public static LevelProvider getProvider(@NotNull String name) {
        return BY_ID_MAP.get(name.toLowerCase());
    }

    @NotNull
    public static Map<String, LevelProvider> getByIdMap() {
        return BY_ID_MAP;
    }

    @NotNull
    public static Set<LevelProvider> getProviders() {
        return new HashSet<>(BY_ID_MAP.values());
    }
}
