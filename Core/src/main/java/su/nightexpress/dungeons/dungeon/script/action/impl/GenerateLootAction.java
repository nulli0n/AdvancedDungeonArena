package su.nightexpress.dungeons.dungeon.script.action.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.lootchest.LootChest;
import su.nightexpress.dungeons.dungeon.script.action.Action;
import su.nightexpress.dungeons.dungeon.script.action.ActionId;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public class GenerateLootAction implements Action {

    private final boolean specific;
    private final String[] lootChestIds;

    public GenerateLootAction(boolean specific, String[] lootChestIds) {
        this.specific = specific;
        this.lootChestIds = lootChestIds;
    }

    @NotNull
    public static GenerateLootAction load(@NotNull FileConfig config, @NotNull String path) {
        boolean specific = ConfigValue.create(path + ".Specific", false).read(config);
        String[] lootChestIds = ConfigValue.create(path + ".LootChestIds", new String[]{"null"}).read(config);

        return new GenerateLootAction(specific, lootChestIds);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Specific", this.specific);
        config.setStringArray(path + ".LootChestIds", this.lootChestIds);
    }

    @NotNull
    @Override
    public String getName() {
        return ActionId.GENERATE_LOOT;
    }

    @Override
    public void perform(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        if (!this.specific) {
            dungeon.refillLootChests();
            return;
        }

        for (String lootId : this.lootChestIds) {
            LootChest lootChest = dungeon.getConfig().getLootChestById(lootId);
            if (lootChest == null) {
                ErrorHandler.error("Invalid loot chest '" + lootId + "'!", this, dungeon);
                continue;
            }

            dungeon.refillLootChest(lootChest);
        }
    }
}
