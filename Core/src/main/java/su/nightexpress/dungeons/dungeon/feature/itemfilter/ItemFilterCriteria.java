package su.nightexpress.dungeons.dungeon.feature.itemfilter;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.text.night.NightMessage;

import java.util.Collections;
import java.util.List;

public class ItemFilterCriteria implements Writeable {

    private final List<String> names;
    private final List<String> lores;
    private final List<Material> materials;

    public ItemFilterCriteria(@NotNull List<String> names, @NotNull List<String> lores, @NotNull List<Material> materials) {
        this.names = names;
        this.lores = lores;
        this.materials = materials;
    }

    @NotNull
    public static ItemFilterCriteria read(@NotNull FileConfig config, @NotNull String path) {
        List<Material> materials = Lists.modify(config.getStringList(path + ".Materials"), BukkitThing::getMaterial);
        List<String> names = ConfigValue.create(path + ".Names", Collections.emptyList()).read(config);
        List<String> lores = ConfigValue.create(path + ".Lores", Collections.emptyList()).read(config);

        return new ItemFilterCriteria(names, lores, materials);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Materials", Lists.modify(this.materials, BukkitThing::getAsString));
        config.set(path + ".Names", this.names);
        config.set(path + ".Lores", this.lores);
    }

    public boolean matches(@NotNull ItemStack itemStack) {
        Material material = itemStack.getType();
        if (!this.materials.isEmpty() && !this.materials.contains(material)) return false;

        String name = NightMessage.stripTags(ItemUtil.getNameSerialized(itemStack));
        if (!this.names.isEmpty() && this.names.stream().noneMatch(name::contains)) return false;

        String lore = NightMessage.stripTags(String.join("\n", ItemUtil.getLoreSerialized(itemStack)));
        return this.lores.isEmpty() || this.lores.stream().anyMatch(lore::contains);
    }

    @NotNull
    public List<Material> getMaterials() {
        return this.materials;
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
