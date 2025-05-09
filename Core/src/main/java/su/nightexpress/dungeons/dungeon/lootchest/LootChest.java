package su.nightexpress.dungeons.dungeon.lootchest;

import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.scale.ScalableAmount;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;
import su.nightexpress.nightcore.util.random.Rnd;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LootChest extends AbstractFileData<DungeonPlugin> {

    private BlockPos blockPos;
//    private int minItems;
//    private int maxItems;
    private ScalableAmount itemsAmount;
    private boolean uniqueOnly;

    private final Map<String, LootItem> itemByIdMap;

    public LootChest(@NotNull DungeonPlugin plugin, @NotNull File file) {
        super(plugin, file);
        this.itemByIdMap = new HashMap<>();
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        this.setBlockPos(BlockPos.read(config, "Location"));
        this.itemsAmount = ScalableAmount.read(config, "ItemsAmount");
//        this.setMinItems(ConfigValue.create("MinItems", 0).read(config));
//        this.setMaxItems(ConfigValue.create("MaxItems", -1).read(config));
        this.setUniqueOnly(ConfigValue.create("UniqueOnly", false).read(config));

        config.getSection("Items").forEach(sId -> {
            LootItem item = LootItem.read(config, "Items." + sId, sId);
            this.itemByIdMap.put(sId.toLowerCase(), item);
        });

        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Location", this.blockPos);
        config.set("ItemsAmount", this.itemsAmount);
//        config.set("MinItems", this.minItems);
//        config.set("MaxItems", this.maxItems);
        config.set("UniqueOnly", this.uniqueOnly);
        config.remove("Items");

        this.itemByIdMap.forEach((id, item) -> {
            config.set("Items." + id, item);
        });
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return Placeholders.LOOT_CHEST.replacer(this);
    }

    // TODO Highlight loot chest when generated per player?

    public void generateLoot(@NotNull DungeonInstance dungeon) {
        Container container = this.getContainer(dungeon);
        if (container == null) return;

        Inventory inventory = container.getInventory();
        int inventorySize = inventory.getSize();

//        int minLoot = Math.max(0, this.minItems);
//        int maxLoot = this.maxItems < 0 ? inventorySize : this.maxItems;

        int lootCount = this.itemsAmount.getScaledInt(dungeon);// Rnd.get(minLoot, maxLoot);
        if (lootCount <= 0) return;

        Set<Integer> freeSlots = IntStream.range(0, inventorySize).boxed().collect(Collectors.toCollection(HashSet::new));
        Map<LootItem, Double> weightMap = new HashMap<>();
        this.getItems().forEach(item -> {
            weightMap.put(item, item.getWeight());
        });

        inventory.clear();

        while (lootCount > 0 && !freeSlots.isEmpty() && !weightMap.isEmpty()) {
            LootItem item = Rnd.getByWeight(weightMap);

            int slot = Rnd.get(freeSlots);
            inventory.setItem(slot, item.getItem().getItemStack());

            if (this.uniqueOnly) {
                weightMap.remove(item);
            }

            lootCount--;
            freeSlots.remove(slot);
        }
    }

    public void clearLoot(@NotNull DungeonInstance dungeon) {
        Container container = this.getContainer(dungeon);
        if (container == null) return;

        Inventory inventory = container.getInventory();
        inventory.clear();
    }

    @Nullable
    private Container getContainer(@NotNull DungeonInstance dungeon) {
        Block block = this.blockPos.toBlock(dungeon.getWorld());
        if (block.getState() instanceof Container container) {
            return container;
        }

        return null;
    }

    @NotNull
    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public void setBlockPos(@NotNull BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    @NotNull
    public ScalableAmount getItemsAmount() {
        return this.itemsAmount;
    }

    public void setItemsAmount(@NotNull ScalableAmount itemsAmount) {
        this.itemsAmount = itemsAmount;
    }

//    public int getMinItems() {
//        return this.minItems;
//    }
//
//    public void setMinItems(int minItems) {
//        this.minItems = minItems;
//    }
//
//    public int getMaxItems() {
//        return this.maxItems;
//    }
//
//    public void setMaxItems(int maxItems) {
//        this.maxItems = maxItems;
//    }

    public boolean isUniqueOnly() {
        return this.uniqueOnly;
    }

    public void setUniqueOnly(boolean uniqueOnly) {
        this.uniqueOnly = uniqueOnly;
    }

    @NotNull
    public Map<String, LootItem> getItemByIdMap() {
        return this.itemByIdMap;
    }

    @NotNull
    public Set<LootItem> getItems() {
        return new HashSet<>(this.itemByIdMap.values());
    }

    @Nullable
    public LootItem getItemById(@NotNull String id) {
        return this.itemByIdMap.get(id.toLowerCase());
    }

    public void addItem(@NotNull LootItem item) {
        this.itemByIdMap.put(item.getId(), item);
    }
}
