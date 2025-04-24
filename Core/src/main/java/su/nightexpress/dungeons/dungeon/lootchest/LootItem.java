package su.nightexpress.dungeons.dungeon.lootchest;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.item.ItemProvider;
import su.nightexpress.dungeons.registry.item.ItemRegistry;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

public class LootItem implements Writeable {

    private final String id;
    private final double weight;
    private final ItemProvider item;

    public LootItem(@NotNull String id, double weight, @NotNull ItemProvider item) {
        this.id = id.toLowerCase();
        this.weight = weight;
        this.item = item;
    }

    @NotNull
    public static LootItem read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        double weight = ConfigValue.create(path + ".Weight", 0D).read(config);
        ItemProvider provider = ItemRegistry.read(config, path + ".Item");

        return new LootItem(id, weight, provider);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Weight", this.weight);
        config.set(path + ".Item", this.item);
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    public double getWeight() {
        return this.weight;
    }

    @NotNull
    public ItemProvider getItem() {
        return this.item;
    }
}
