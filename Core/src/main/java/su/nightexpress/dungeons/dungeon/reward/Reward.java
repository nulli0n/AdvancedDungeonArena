package su.nightexpress.dungeons.dungeon.reward;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.api.item.ItemProvider;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.player.DungeonGamer;
import su.nightexpress.dungeons.registry.item.ItemRegistry;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.placeholder.Replacer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class Reward extends AbstractFileData<DungeonPlugin> {

    private String name;
    private List<String> description;
    private List<ItemProvider> items;
    private List<String> commands;

    public Reward(@NotNull DungeonPlugin plugin, @NotNull File file) {
        super(plugin, file);
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        this.setName(ConfigValue.create("Name", StringUtil.capitalizeUnderscored(this.getId())).read(config));
        this.setDescription(ConfigValue.create("Description", new ArrayList<>()).read(config));

        this.items = new ArrayList<>();
        config.getSection("Items").forEach(sId -> {
            ItemProvider provider = ItemRegistry.read(config, "Items." + sId);
            this.items.add(provider);
        });

        this.setCommands(ConfigValue.create("Commands", new ArrayList<>()).read(config));

        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Name", this.name);
        config.set("Description", this.description);
        config.remove("Items");

        int index = 0;
        for (ItemProvider provider : this.items) {
            config.set("Items." + (index++), provider);
        }

        config.set("Commands", this.commands);
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return Placeholders.REWARD.replacer(this);
    }

    public void give(@NotNull DungeonInstance dungeon, @NotNull DungeonGamer gamer) {
        Player player = gamer.getPlayer();

        this.items.forEach(itemProvider -> {
            if (!itemProvider.canProduceItem()) return;

            Players.addItem(player, itemProvider.getItemStack());
        });

        Players.dispatchCommands(player, Replacer.create().replace(dungeon.replacePlaceholders()).apply(this.commands));
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public List<String> getDescription() {
        return this.description;
    }

    public void setDescription(@NotNull List<String> description) {
        this.description = description;
    }

    @NotNull
    public List<ItemProvider> getItems() {
        return this.items;
    }

    public void setItems(@NotNull List<ItemProvider> items) {
        this.items = items;
    }

    @NotNull
    public List<String> getCommands() {
        return this.commands;
    }

    public void setCommands(@NotNull List<String> commands) {
        this.commands = commands;
    }
}
