package su.nightexpress.dungeons.dungeon.feature.itemfilter;

import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.text.night.NightMessage;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ItemFilterCriteria implements Writeable {

    private static final int MAX_NESTING_DEPTH = 3;

    private final List<String>   names;
    private final List<String>   lores;
    private final List<String>   materialEntries;
    private final Set<Material>  materials;
    private final boolean        materialFilter;

    public ItemFilterCriteria(@NotNull List<String> names, @NotNull List<String> lores, @NotNull List<Material> materials) {
        this(names, lores, Lists.modify(materials, BukkitThing::getAsString), new LinkedHashSet<>(materials));
    }

    private ItemFilterCriteria(@NotNull List<String> names, @NotNull List<String> lores, @NotNull List<String> materialEntries, @NotNull Set<Material> materials) {
        this.names = names;
        this.lores = lores;
        this.materialEntries = materialEntries;
        this.materials = materials;
        this.materialFilter = !materialEntries.isEmpty();
    }

    @NotNull
    public static ItemFilterCriteria read(@NotNull FileConfig config, @NotNull String path) {
        List<String> entries = new ArrayList<>(config.getStringList(path + ".Materials"));
        Set<Material> materials = new LinkedHashSet<>();

        for (String entry : entries) {
            if (entry.contains("*")) {
                Set<Material> matched = expandWildcard(entry);
                if (matched.isEmpty()) {
                    ErrorHandler.error("Wildcard '" + entry + "' does not match any material!", config, path + ".Materials");
                }
                materials.addAll(matched);
                continue;
            }

            Material material = BukkitThing.getMaterial(entry);
            if (material == null) {
                ErrorHandler.error("Invalid material '" + entry + "'!", config, path + ".Materials");
                continue;
            }
            materials.add(material);
        }

        List<String> names = ConfigValue.create(path + ".Names", Collections.emptyList()).read(config);
        List<String> lores = ConfigValue.create(path + ".Lores", Collections.emptyList()).read(config);

        return new ItemFilterCriteria(names, lores, entries, materials);
    }

    @NotNull
    private static Set<Material> expandWildcard(@NotNull String entry) {
        String raw = entry.toLowerCase(Locale.ROOT).trim();
        if (raw.startsWith("minecraft:")) raw = raw.substring("minecraft:".length());

        Pattern pattern = Pattern.compile(Arrays.stream(raw.split("\\*", -1))
                .map(Pattern::quote)
                .collect(Collectors.joining(".*")));

        Set<Material> result = new LinkedHashSet<>();
        for (Material material : Material.values()) {
            if (material.isLegacy()) continue;
            if (pattern.matcher(material.name().toLowerCase(Locale.ROOT)).matches()) {
                result.add(material);
            }
        }
        return result;
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Materials", this.materialEntries);
        config.set(path + ".Names", this.names);
        config.set(path + ".Lores", this.lores);
    }

    public boolean matches(@NotNull ItemStack itemStack) {
        Material material = itemStack.getType();
        // If entries are defined but none resolved (typos), nothing matches instead of everything.
        if (this.materialFilter && !this.materials.contains(material)) return false;

        String name = NightMessage.stripTags(ItemUtil.getNameSerialized(itemStack));
        if (!this.names.isEmpty() && this.names.stream().noneMatch(name::contains)) return false;

        String lore = NightMessage.stripTags(String.join("\n", ItemUtil.getLoreSerialized(itemStack)));
        return this.lores.isEmpty() || this.lores.stream().anyMatch(lore::contains);
    }

    public boolean shouldConfiscate(@NotNull ItemFilterMode mode, @NotNull ItemStack itemStack, boolean scanContainers) {
        return this.shouldConfiscate(mode, itemStack, scanContainers, 0);
    }

    private boolean shouldConfiscate(@NotNull ItemFilterMode mode, @NotNull ItemStack itemStack, boolean scanContainers, int depth) {
        if (mode == ItemFilterMode.NONE) return false;

        boolean matched = this.matches(itemStack);
        if (mode == ItemFilterMode.BAN_SPECIFIC && matched) return true;
        if (mode == ItemFilterMode.ALLOW_SPECIFIC && !matched) return true;

        if (!scanContainers || depth >= MAX_NESTING_DEPTH) return false;

        for (ItemStack nested : getContainedItems(itemStack)) {
            if (this.shouldConfiscate(mode, nested, true, depth + 1)) return true;
        }
        return false;
    }

    @NotNull
    private static List<ItemStack> getContainedItems(@NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return Collections.emptyList();

        List<ItemStack> items = new ArrayList<>();

        if (meta instanceof BlockStateMeta blockMeta && blockMeta.getBlockState() instanceof Container container) {
            for (ItemStack content : container.getInventory().getContents()) {
                if (content != null && !content.getType().isAir()) items.add(content);
            }
        }
        if (meta instanceof BundleMeta bundleMeta) {
            for (ItemStack content : bundleMeta.getItems()) {
                if (content != null && !content.getType().isAir()) items.add(content);
            }
        }
        return items;
    }

    @NotNull
    public List<Material> getMaterials() {
        return new ArrayList<>(this.materials);
    }

    @NotNull
    public List<String> getNames() {
        return this.names;
    }

    @NotNull
    public List<String> getLores() {
        return this.lores;
    }
}