package su.nightexpress.dungeons.registry.item.provider;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.item.ItemType;
import su.nightexpress.dungeons.registry.item.AbstractItemProvider;
import su.nightexpress.nightcore.config.FileConfig;

public class DummyItemProvider extends AbstractItemProvider {

    public DummyItemProvider() {
        super(ItemType.VANILLA);
    }

    @Override
    protected void writeAdditional(@NotNull FileConfig config, @NotNull String path) {

    }

    @Override
    public boolean isDummy() {
        return true;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    @Nullable
    public ItemStack createItemStack() {
        return this.getDummyItem();
    }

    @Override
    @NotNull
    public String getItemType() {
        return "dummy";
    }
}
