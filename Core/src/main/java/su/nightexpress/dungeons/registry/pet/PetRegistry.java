package su.nightexpress.dungeons.registry.pet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.hook.HookId;
import su.nightexpress.dungeons.registry.pet.provider.AdvancedPetsProvider;
import su.nightexpress.dungeons.registry.pet.provider.CombatPetsProvider;
import su.nightexpress.dungeons.registry.pet.provider.MCPetsProvider;
import su.nightexpress.nightcore.util.Plugins;

import java.util.*;
import java.util.function.Supplier;

public class PetRegistry {

    private static final Map<String, PetProvider> BY_ID_MAP = new HashMap<>();

    private static DungeonPlugin plugin;

    public static void load(@NotNull DungeonPlugin dungeonPlugin) {
        plugin = dungeonPlugin;

        loadIntegration(HookId.ADVANCED_PETS, AdvancedPetsProvider::new);
        loadIntegration(HookId.MC_PETS, MCPetsProvider::new);
        loadIntegration(HookId.COMBAT_PETS, CombatPetsProvider::new);
    }

    private static void loadIntegration(@NotNull String pluginName, @NotNull Supplier<PetProvider> provider) {
        if (!Plugins.isInstalled(pluginName)) return;

        register(provider.get());
    }

    public static void clear() {
        BY_ID_MAP.clear();
        plugin = null;
    }

    public static void register(@NotNull PetProvider provider) {
        BY_ID_MAP.put(provider.getName(), provider);
        plugin.info("Registered pet provider: " + provider.getName());
    }

    @Nullable
    public static PetProvider getProvider(@NotNull String name) {
        return BY_ID_MAP.get(name.toLowerCase());
    }

    @NotNull
    public static Map<String, PetProvider> getByIdMap() {
        return BY_ID_MAP;
    }

    @NotNull
    public static Set<PetProvider> getProviders() {
        return new HashSet<>(BY_ID_MAP.values());
    }
}
