package su.nightexpress.dungeons.config;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;

public class Keys {

    public static NamespacedKey dummyItem;
    public static NamespacedKey dungeonWand;
    public static NamespacedKey kitModifier;
    public static NamespacedKey kitItem;
    public static NamespacedKey mobTemplateId;
    public static NamespacedKey mobFaction;
    public static NamespacedKey mobDungeonId;

    public static void load(@NotNull DungeonPlugin plugin) {
        dummyItem = new NamespacedKey(plugin, "dummy_item");
        dungeonWand = new NamespacedKey(plugin, "dungeon_wand");
        kitModifier = new NamespacedKey(plugin, "kit_modifier");
        kitItem = new NamespacedKey(plugin, "kit_item");
        mobTemplateId = new NamespacedKey(plugin, "mob_internal_id");
        mobFaction = new NamespacedKey(plugin, "mob_faction");
        mobDungeonId = new NamespacedKey(plugin, "mob_dungeon_id");
    }

    public static void clear() {
        dummyItem = null;
        dungeonWand = null;
        kitModifier = null;
        kitItem = null;
        mobTemplateId = null;
        mobFaction = null;
        mobDungeonId = null;
    }
}
