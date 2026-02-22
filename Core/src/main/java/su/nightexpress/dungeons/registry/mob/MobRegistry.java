package su.nightexpress.dungeons.registry.mob;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.api.mob.MobProvider;
import su.nightexpress.dungeons.hook.HookId;
import su.nightexpress.dungeons.registry.mob.provider.DungeonMobProvider;
import su.nightexpress.dungeons.registry.mob.provider.MythicMobProvider;
import su.nightexpress.nightcore.util.Plugins;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class MobRegistry {

    private static final Map<String, MobProvider> BY_ID_MAP = new HashMap<>();

    private static DungeonPlugin plugin;

    public static void load(@NotNull DungeonPlugin dungeonPlugin) {
        plugin = dungeonPlugin;

        register(new DungeonMobProvider(dungeonPlugin));
        loadIntegration(HookId.MYTHIC_MOBS, MythicMobProvider::new);
    }

    private static void loadIntegration(@NotNull String pluginName, @NotNull Supplier<MobProvider> provider) {
        if (!Plugins.isInstalled(pluginName)) return;

        register(provider.get());
    }

    public static void clear() {
        BY_ID_MAP.clear();
        plugin = null;
    }

    public static void register(@NotNull MobProvider provider) {
        BY_ID_MAP.put(provider.getName(), provider);
        plugin.info("Registered mob provider: " + provider.getName());
    }

    @Nullable
    public static MobProvider getProviderByName(@NotNull String name) {
        return BY_ID_MAP.get(name.toLowerCase());
    }

    @NotNull
    public static Map<String, MobProvider> getProviderByIdMap() {
        return BY_ID_MAP;
    }

    @NotNull
    public static Set<MobProvider> getProviders() {
        return new HashSet<>(BY_ID_MAP.values());
    }

    @Nullable
    public static MobProvider getProvider(@NotNull LivingEntity entity) {
        return getProviders().stream().filter(provider -> provider.isProducedBy(entity)).findFirst().orElse(null);
    }
}
