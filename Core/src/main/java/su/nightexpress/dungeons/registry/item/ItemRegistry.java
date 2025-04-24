package su.nightexpress.dungeons.registry.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonsAPI;
import su.nightexpress.dungeons.api.item.ItemProvider;
import su.nightexpress.dungeons.api.item.ItemType;
import su.nightexpress.dungeons.config.Keys;
import su.nightexpress.dungeons.hook.HookId;
import su.nightexpress.dungeons.registry.item.provider.CustomItemProvider;
import su.nightexpress.dungeons.registry.item.provider.DummyItemProvider;
import su.nightexpress.dungeons.registry.item.provider.VanillaItemProvider;
import su.nightexpress.dungeons.util.DungeonUtils;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.PDCUtil;

public class ItemRegistry {

    public static final DummyItemProvider DUMMY = new DummyItemProvider();

    public static boolean isDummy(@NotNull ItemStack itemStack) {
        return PDCUtil.getBoolean(itemStack, Keys.dummyItem).isPresent();
    }

    @NotNull
    public static ItemProvider read(@NotNull FileConfig config, @NotNull String path) {
        ItemType type = config.getEnum(path + ".Type", ItemType.class, ItemType.VANILLA);

        if (type == ItemType.CUSTOM && !DungeonUtils.hasEconomyBridge()) {
            DungeonsAPI.getPlugin().error("Could not load custom item due to missing " + HookId.ECONOMY_BRIDGE + " dependency. Caused by '" + config.getFile().getName() + "' -> '" + path + "'.");
            return DUMMY;
        }

        ItemProvider provider = switch (type) {
            case VANILLA -> VanillaItemProvider.read(config, path);
            case CUSTOM -> CustomItemProvider.read(config, path);
        };

        return provider == null ? DUMMY : provider;
    }

    @NotNull
    public static ItemProvider vanilla(@NotNull ItemStack itemStack) {
        return VanillaItemProvider.fromItem(itemStack);
    }

    @NotNull
    public static ItemProvider fromItem(@NotNull ItemStack itemStack) {
        ItemProvider provider = null;

        if (DungeonUtils.hasEconomyBridge()) {
            provider = CustomItemProvider.fromItem(itemStack);
        }
        if (provider == null) {
            provider = vanilla(itemStack);
        }
        return provider;
    }
}
