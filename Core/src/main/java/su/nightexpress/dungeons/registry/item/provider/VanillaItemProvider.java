package su.nightexpress.dungeons.registry.item.provider;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.item.ItemType;
import su.nightexpress.dungeons.registry.item.AbstractItemProvider;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.text.NightMessage;

public class VanillaItemProvider extends AbstractItemProvider {

    private final ItemStack itemStack;

    public VanillaItemProvider(@NotNull ItemStack itemStack) {
        super(ItemType.VANILLA);
        this.itemStack = itemStack;
    }

    @NotNull
    public static VanillaItemProvider fromItem(@NotNull ItemStack itemStack) {
        return new VanillaItemProvider(itemStack);
    }

    @Nullable
    public static VanillaItemProvider read(@NotNull FileConfig config, @NotNull String path) {
        ItemTag tag = ItemTag.read(config, path + ".Tag");

        ItemStack itemStack = ItemNbt.fromTag(tag);
        if (itemStack == null) return null;

        return new VanillaItemProvider(itemStack);
    }

    @Override
    public void writeAdditional(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Tag", ItemNbt.getTag(this.itemStack));
    }

    @Override
    public boolean isValid() {
        return !this.itemStack.getType().isAir();
    }

    @Override
    @Nullable
    public ItemStack createItemStack() {
        return new ItemStack(this.itemStack);
    }

    @Override
    @NotNull
    public String getItemType() {
        String display = StringUtil.transformForID(NightMessage.stripTags(ItemUtil.getSerializedName(this.itemStack)));
        if (!display.isBlank()) return display;

        return BukkitThing.toString(this.itemStack.getType());
    }
}
