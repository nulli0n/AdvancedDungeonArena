package su.nightexpress.dungeons.dungeon;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.api.item.ItemProvider;
import su.nightexpress.dungeons.dungeon.config.DungeonConfig;
import su.nightexpress.dungeons.dungeon.config.DungeonMobSpawner;
import su.nightexpress.dungeons.dungeon.level.Level;
import su.nightexpress.dungeons.dungeon.lootchest.LootChest;
import su.nightexpress.dungeons.dungeon.lootchest.LootItem;
import su.nightexpress.dungeons.dungeon.reward.Reward;
import su.nightexpress.dungeons.dungeon.scale.ScalableAmount;
import su.nightexpress.dungeons.dungeon.spot.Spot;
import su.nightexpress.dungeons.dungeon.spot.SpotState;
import su.nightexpress.dungeons.dungeon.stage.Stage;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.registry.item.ItemRegistry;
import su.nightexpress.dungeons.selection.impl.CuboidSelection;
import su.nightexpress.dungeons.selection.impl.PositionSelection;
import su.nightexpress.dungeons.selection.impl.Selection;
import su.nightexpress.dungeons.util.DungeonUtils;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.geodata.Cuboid;
import su.nightexpress.nightcore.util.geodata.DimensionType;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;
import su.nightexpress.nightcore.util.geodata.pos.ExactPos;
import su.nightexpress.nightcore.util.text.tag.Tags;

import java.io.File;
import java.util.*;

public class DungeonSetup extends AbstractManager<DungeonPlugin> {

    public DungeonSetup(@NotNull DungeonPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {

    }

    @Override
    protected void onShutdown() {

    }

    @Nullable
    private Cuboid getSelectedCuboid(@NotNull Player player) {
        Selection selection = this.plugin.getSelectionManager().getSelection(player);
        return selection instanceof CuboidSelection cube ? cube.toCuboid() : null;
    }

    @NotNull
    private Level injectLevel(@NotNull DungeonConfig config, @NotNull String id, @NotNull ExactPos blockPos) {
        File file = new File(config.getLevelsPath(), id + FileConfig.EXTENSION);
        Level level = new Level(this.plugin, file);

        level.setDisplayName(StringUtil.capitalizeUnderscored(id));
        level.setDescription("Level description.");
        level.setSpawnPos(blockPos);
        DungeonUtils.setLevelDefaults(level);
        level.save();
        level.load();
        config.addLevel(level);

        return level;
    }

    @NotNull
    private Stage injectStage(@NotNull DungeonConfig config, @NotNull String id) {
        File file = new File(config.getStagesPath(), id + FileConfig.EXTENSION);
        Stage stage = new Stage(this.plugin, file);

        stage.setDisplayName(StringUtil.capitalizeUnderscored(id));
        stage.setDescription("Stage description.");
        DungeonUtils.setStageDefaults(stage);
        stage.save();
        stage.load();
        config.addStage(stage);

        return stage;
    }

    public boolean createDungeon(@NotNull Player player, @NotNull String name) {
        Cuboid cuboid = this.getSelectedCuboid(player);
        if (cuboid == null) {
            Lang.SETUP_SELECTION_NO_CUBOID.getMessage().send(player);
            return false;
        }

        if (this.plugin.getDungeonManager().getDungeons().stream().anyMatch(other -> cuboid.isIntersectingWith(other.getCuboid()))) {
            Lang.SETUP_SELECTION_DUNGEON_OVERLAP.getMessage().send(player);
            return false;
        }

        String id = StringUtil.transformForID(name);
        if (id.isBlank()) {
            Lang.SETUP_ERROR_INVALID_NAME.getMessage().send(player);
            return false;
        }

        if (this.plugin.getDungeonManager().getDungeonById(id) != null) {
            Lang.SETUP_DUNGEON_EXISTS.getMessage().send(player);
            return false;
        }

        File dir = new File(this.plugin.getDataFolder() + Config.DIR_DUNGEONS + name);
        dir.mkdirs();

        File file = new File(dir.getAbsolutePath(), DungeonConfig.FILE_NAME);
        DungeonConfig dungeonConfig = new DungeonConfig(this.plugin, file, id);

        dungeonConfig.setWorldName(player.getWorld().getName());
        dungeonConfig.setCuboid(cuboid);
        dungeonConfig.setName(StringUtil.capitalizeUnderscored(id));
        dungeonConfig.setPrefix(Tags.GRAY.wrap("[" + Tags.WHITE.wrap(Placeholders.DUNGEON_NAME) + "]") + " ");
        dungeonConfig.setIcon(NightItem.fromType(Material.SPAWNER));
        dungeonConfig.setStartLevelId(Placeholders.DEFAULT);
        dungeonConfig.setStartStageId(Placeholders.DEFAULT);
        dungeonConfig.save();

        ExactPos spawnPos = ExactPos.from(cuboid.getCenter());

        this.injectLevel(dungeonConfig, Placeholders.DEFAULT, spawnPos);
        this.injectStage(dungeonConfig, Placeholders.DEFAULT);

        this.plugin.getDungeonManager().loadDungeon(dungeonConfig);
        this.plugin.getSelectionManager().stopSelection(player);

        Lang.SETUP_DUNGEON_CREATED.getMessage().send(player, replacer -> replacer.replace(dungeonConfig.replacePlaceholders()));
        return true;
    }

    public boolean setProtectionFromSelection(@NotNull Player player, @NotNull DungeonConfig config) {
        Cuboid cuboid = this.getSelectedCuboid(player);
        if (cuboid == null || !config.isWorld(player.getWorld())) {
            Lang.SETUP_SELECTION_NO_CUBOID.getMessage().send(player);
            return false;
        }

        if (this.plugin.getDungeonManager().getDungeons().stream().anyMatch(other -> other != config && cuboid.isIntersectingWith(other.getCuboid()))) {
            Lang.SETUP_SELECTION_DUNGEON_OVERLAP.getMessage().send(player);
            return false;
        }

        this.plugin.getDungeonManager().removeDungeonPositions(config);
        config.setCuboid(cuboid);
        config.save();
        config.validate();
        this.plugin.getDungeonManager().updateDungeonPositions(config);
        this.plugin.getSelectionManager().stopSelection(player);

        Lang.SETUP_PROTECTION_SET.getMessage().send(player, replacer -> replacer.replace(config.replacePlaceholders()));
        return true;
    }

    public boolean setLobby(@NotNull Player player, @NotNull DungeonConfig config) {
        ExactPos pos = ExactPos.from(player.getLocation());
        if (!config.getCuboid().contains(pos) || !config.isWorld(player.getWorld())) {
            Lang.SETUP_SELECTION_POSITION_OUT_OF_PROTECTION.getMessage().send(player, replacer -> replacer.replace(config.replacePlaceholders()));
            return false;
        }

        config.setLobbyPos(pos);
        config.save();
        config.validate();

        Lang.SETUP_LOBBY_SET.getMessage().send(player, replacer -> replacer.replace(config.replacePlaceholders()));
        return true;
    }

    public boolean createSpawner(@NotNull Player player, @NotNull DungeonConfig config, @NotNull String name) {
        Selection selection = this.plugin.getSelectionManager().getSelection(player);
        Set<BlockPos> positions = selection instanceof PositionSelection pos ? pos.getPositions() : Collections.emptySet();

        if (!config.isWorld(player.getWorld()) || positions.stream().anyMatch(blockPos -> !config.isInProtection(blockPos))) {
            Lang.SETUP_SELECTION_POSITION_OUT_OF_PROTECTION.getMessage().send(player, replacer -> replacer.replace(config.replacePlaceholders()));
            return false;
        }

        if (positions.isEmpty()) {
            Lang.SETUP_SELECTION_NO_POSITIONS.getMessage().send(player, replacer -> replacer.replace(config.replacePlaceholders()));
            return false;
        }

        String id = StringUtil.transformForID(name);
        if (id.isBlank()) {
            Lang.SETUP_ERROR_INVALID_NAME.getMessage().send(player);
            return false;
        }

        positions = Lists.modify(positions, blockPos -> new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ()));

        DungeonMobSpawner spawner = new DungeonMobSpawner(id, positions);
        config.addSpawner(spawner);
        config.saveSpawners();
        config.validate();

        this.plugin.getSelectionManager().stopSelection(player);

        Lang.SETUP_SPAWNER_CREATED.getMessage().send(player, replacer -> replacer.replace(Placeholders.GENERIC_NAME, id));
        return true;
    }

    public boolean createLevel(@NotNull Player player, @NotNull DungeonConfig config, @NotNull String name) {
        ExactPos blockPos = ExactPos.from(player.getLocation());
        if (!config.isWorld(player.getWorld()) || !config.isInProtection(blockPos.toBlockPos())) {
            Lang.SETUP_SELECTION_POSITION_OUT_OF_PROTECTION.getMessage().send(player, replacer -> replacer.replace(config.replacePlaceholders()));
            return false;
        }

        String id = StringUtil.transformForID(name);
        if (id.isBlank()) {
            Lang.SETUP_ERROR_INVALID_NAME.getMessage().send(player);
            return false;
        }

        if (config.getLevelById(id) != null) {
            Lang.SETUP_LEVEL_EXISTS.getMessage().send(player);
            return false;
        }

        Level level = this.injectLevel(config, id, blockPos);

        Lang.SETUP_LEVEL_CREATED.getMessage().send(player, replacer -> replacer.replace(level.replacePlaceholders()));
        return true;
    }

    public boolean setLevelSpawn(@NotNull Player player, @NotNull DungeonConfig config, @NotNull String name) {
        ExactPos blockPos = ExactPos.from(player.getLocation());
        if (!config.isWorld(player.getWorld()) || !config.isInProtection(blockPos.toBlockPos())) {
            Lang.SETUP_SELECTION_POSITION_OUT_OF_PROTECTION.getMessage().send(player, replacer -> replacer.replace(config.replacePlaceholders()));
            return false;
        }

        Level level = config.getLevelById(name);
        if (level == null) {
            Lang.SETUP_LEVEL_INVALID.getMessage().send(player);
            return false;
        }

        level.setSpawnPos(blockPos);
        level.save();
        config.validate();

        Lang.SETUP_LEVEL_SPAWN_SET.getMessage().send(player, replacer -> replacer.replace(level.replacePlaceholders()));
        return true;
    }

    public boolean createStage(@NotNull Player player, @NotNull DungeonConfig config, @NotNull String name) {
        String id = StringUtil.transformForID(name);
        if (id.isBlank()) {
            Lang.SETUP_ERROR_INVALID_NAME.getMessage().send(player);
            return false;
        }

        if (config.getStageById(id) != null) {
            Lang.SETUP_STAGE_EXISTS.getMessage().send(player);
            return false;
        }

        Stage stage = this.injectStage(config, id);

        Lang.SETUP_STAGE_CREATED.getMessage().send(player, replacer -> replacer.replace(stage.replacePlaceholders()));
        return true;
    }

    public boolean createReward(@NotNull Player player, @NotNull DungeonConfig config, @NotNull String name) {
        String id = StringUtil.transformForID(name);
        if (id.isBlank()) {
            Lang.SETUP_ERROR_INVALID_NAME.getMessage().send(player);
            return false;
        }

        if (config.getRewardById(id) != null) {
            Lang.SETUP_REWARD_EXISTS.getMessage().send(player);
            return false;
        }

        File file = new File(config.getRewardsPath(), id + FileConfig.EXTENSION);
        Reward reward = new Reward(this.plugin, file);
        reward.setName(StringUtil.capitalizeUnderscored(name));
        reward.setDescription(new ArrayList<>());
        reward.setItems(new ArrayList<>());
        reward.setCommands(new ArrayList<>());
        reward.save();
        reward.load();
        config.addReward(reward);

        Lang.SETUP_REWARD_CREATED.getMessage().send(player, replacer -> replacer.replace(reward.replacePlaceholders()));
        return true;
    }

    public boolean removeReward(@NotNull CommandSender sender, @NotNull DungeonConfig config, @NotNull String name) {
        Reward reward = config.getRewardById(name);
        if (reward == null) {
            Lang.SETUP_REWARD_INVALID.getMessage().send(sender);
            return false;
        }

        config.removeReward(reward.getId());
        reward.getFile().delete();

        Lang.SETUP_REWARD_REMOVED.getMessage().send(sender, replacer -> replacer.replace(reward.replacePlaceholders()));
        return true;
    }

    public boolean addRewardItem(@NotNull Player player, @NotNull DungeonConfig config, @NotNull String rewardId) {
        Reward reward = config.getRewardById(rewardId);
        if (reward == null) {
            Lang.SETUP_REWARD_INVALID.getMessage().send(player);
            return false;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType().isAir()) {
            Lang.SETUP_GENERIC_NO_ITEM.getMessage().send(player);
            return false;
        }

        ItemProvider provider = ItemRegistry.fromItem(itemStack);
        if (!provider.canProduceItem()) {
            Lang.SETUP_GENERIC_BAD_ITEM.getMessage().send(player);
            return false;
        }

        reward.getItems().add(provider);
        reward.save();

        Lang.SETUP_REWARD_ITEM_ADDED.getMessage().send(player, replacer -> replacer
            .replace(Placeholders.GENERIC_NAME, ItemUtil.getSerializedName(itemStack))
            .replace(reward.replacePlaceholders()));
        return true;
    }

    public boolean createLootChest(@NotNull Player player, @NotNull DungeonConfig config, @NotNull String name) {
        String id = StringUtil.transformForID(name);
        if (id.isBlank()) {
            Lang.SETUP_ERROR_INVALID_NAME.getMessage().send(player);
            return false;
        }

        if (config.getLootChestById(id) != null) {
            Lang.SETUP_LOOT_CHEST_EXISTS.getMessage().send(player);
            return false;
        }

        Block block = player.getTargetBlock(null, 20);
        if (!(block.getState() instanceof Container)) {
            Lang.SETUP_LOOT_CHEST_NOT_CONTAINER.getMessage().send(player);
            return false;
        }

        File file = new File(config.getLootChestsPath(), id + FileConfig.EXTENSION);
        LootChest lootChest = new LootChest(this.plugin, file);
        lootChest.setBlockPos(BlockPos.from(block));
        lootChest.setItemsAmount(new ScalableAmount("0", "6", true, new HashMap<>()));
        lootChest.setUniqueOnly(false);
        lootChest.save();
        lootChest.load();
        config.addLootChest(lootChest);

        Lang.SETUP_LOOT_CHEST_CREATED.getMessage().send(player, replacer -> replacer.replace(lootChest.replacePlaceholders()));
        return true;
    }

    public boolean removeLootChest(@NotNull CommandSender sender, @NotNull DungeonConfig config, @NotNull String name) {
        LootChest lootChest = config.getLootChestById(name);
        if (lootChest == null) {
            Lang.SETUP_LOOT_CHEST_INVALID.getMessage().send(sender);
            return false;
        }

        config.removeLootChest(lootChest.getId());
        lootChest.getFile().delete();

        Lang.SETUP_LOOT_CHEST_REMOVED.getMessage().send(sender, replacer -> replacer.replace(lootChest.replacePlaceholders()));
        return true;
    }

    public boolean addLootChestItem(@NotNull Player player, @NotNull DungeonConfig config, @NotNull String lootId, @NotNull String itemName, double weight) {
        LootChest lootChest = config.getLootChestById(lootId);
        if (lootChest == null) {
            Lang.SETUP_LOOT_CHEST_INVALID.getMessage().send(player);
            return false;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType().isAir()) {
            Lang.SETUP_GENERIC_NO_ITEM.getMessage().send(player);
            return false;
        }

        ItemProvider provider = ItemRegistry.fromItem(itemStack);
        if (!provider.canProduceItem()) {
            Lang.SETUP_GENERIC_BAD_ITEM.getMessage().send(player);
            return false;
        }

        String itemId = StringUtil.transformForID(itemName);
        if (itemId.isBlank()) {
            Lang.SETUP_ERROR_INVALID_NAME.getMessage().send(player);
            return false;
        }

        lootChest.addItem(new LootItem(itemId, weight, provider));
        lootChest.save();

        Lang.SETUP_LOOT_CHEST_ITEM_ADDED.getMessage().send(player, replacer -> replacer
            .replace(Placeholders.GENERIC_NAME, ItemUtil.getSerializedName(itemStack))
            .replace(lootChest.replacePlaceholders()));
        return true;
    }



    public boolean createSpot(@NotNull Player player, @NotNull DungeonConfig config, @NotNull String name) {
        Cuboid cuboid = this.getSelectedCuboid(player);
        if (cuboid == null || !config.isWorld(player.getWorld())) {
            Lang.SETUP_SELECTION_NO_CUBOID.getMessage().send(player);
            return false;
        }

        if (!cuboid.includedIn(config.getCuboid(), DimensionType._3D)) {
            Lang.SETUP_SELECTION_CUBOID_OUT_OF_PROTECTION.getMessage().send(player, replacer -> replacer.replace(config.replacePlaceholders()));
            return false;
        }

        String id = StringUtil.transformForID(name);
        if (id.isBlank()) {
            Lang.SETUP_ERROR_INVALID_NAME.getMessage().send(player);
            return false;
        }

        if (config.getSpotById(id) != null) {
            Lang.SETUP_SPOT_EXISTS.getMessage().send(player);
            return false;
        }


        World world = player.getWorld();
        List<Block> blocks = cuboid.getBlocks(world);

        File file = new File(config.getSpotsPath(), id + FileConfig.EXTENSION);
        Spot spot = new Spot(this.plugin, file);
        SpotState state = new SpotState(Placeholders.DEFAULT);

        spot.setName(StringUtil.capitalizeUnderscored(id));
        spot.addStateOrUpdate(state, world, blocks);
        spot.save();
        spot.load();
        config.addSpot(spot);

        Lang.SETUP_SPOT_CREATED.getMessage().send(player, replacer -> replacer.replace(spot.replacePlaceholders()));
        return true;
    }

    public boolean removeSpot(@NotNull CommandSender sender, @NotNull DungeonConfig config, @NotNull String name) {
        Spot spot = config.getSpotById(name);
        if (spot == null) {
            Lang.SETUP_SPOT_INVALID.getMessage().send(sender);
            return false;
        }

        config.removeSpot(spot.getId());
        spot.getFile().delete();

        Lang.SETUP_LOOT_CHEST_REMOVED.getMessage().send(sender, replacer -> replacer.replace(spot.replacePlaceholders()));
        return true;
    }

    public boolean addSpotState(@NotNull Player player, @NotNull DungeonConfig config, @NotNull String spotId, @NotNull String name) {
        Cuboid cuboid = this.getSelectedCuboid(player);
        if (cuboid == null || !config.isWorld(player.getWorld())) {
            Lang.SETUP_SELECTION_NO_CUBOID.getMessage().send(player);
            return false;
        }

        if (!cuboid.includedIn(config.getCuboid(), DimensionType._3D)) {
            Lang.SETUP_SELECTION_CUBOID_OUT_OF_PROTECTION.getMessage().send(player, replacer -> replacer.replace(config.replacePlaceholders()));
            return false;
        }

        Spot spot = config.getSpotById(spotId);
        if (spot == null) {
            Lang.SETUP_SPOT_INVALID.getMessage().send(player);
            return false;
        }

        String id = StringUtil.transformForID(name);
        if (id.isBlank()) {
            Lang.SETUP_ERROR_INVALID_NAME.getMessage().send(player);
            return false;
        }

        SpotState state = new SpotState(id);
        World world = player.getWorld();
        List<Block> blocks = cuboid.getBlocks(world);

        spot.addStateOrUpdate(state, world, blocks);
        spot.save();

        Lang.SETUP_SPOT_STATE_ADDED.getMessage().send(player, replacer -> replacer.replace(spot.replacePlaceholders()).replace(state.replacePlaceholders()));
        return true;
    }
}
