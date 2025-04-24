package su.nightexpress.dungeons.dungeon.config;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.api.dungeon.DungeonPos;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.level.Level;
import su.nightexpress.dungeons.dungeon.lootchest.LootChest;
import su.nightexpress.dungeons.dungeon.module.Features;
import su.nightexpress.dungeons.dungeon.module.GameSettings;
import su.nightexpress.dungeons.dungeon.reward.Reward;
import su.nightexpress.dungeons.dungeon.spot.Spot;
import su.nightexpress.dungeons.dungeon.stage.Stage;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.geodata.Cuboid;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;
import su.nightexpress.nightcore.util.geodata.pos.ExactPos;
import su.nightexpress.nightcore.util.text.tag.Tags;

import java.io.File;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class DungeonConfig extends AbstractFileData<DungeonPlugin> {

    public static final String FILE_NAME = "config.yml";

    private final Map<String, DungeonMobSpawner> spawnerByIdMap;
    private final Map<String, Stage>             stageByIdMap;
    private final Map<String, Level>             levelByIdMap;
    private final Map<String, Reward>            rewardByIdMap;
    private final Map<String, LootChest>         lootChestByIdMap;
    private final Map<String, Spot>              spotByIdMap;

    private final Features     features;
    private final GameSettings gameSettings;

    private DungeonInstance instance;

    private boolean broken;
    private String  worldName;
    private Cuboid  cuboid;

    private String       startLevelId;
    private String       startStageId;
    private String       name;
    private List<String> description;
    private String       prefix;
    private NightItem    icon;
    private ExactPos     lobbyPos;

    public DungeonConfig(@NotNull DungeonPlugin plugin, @NotNull File file, @NotNull String id) {
        super(plugin, file, id);
        this.spawnerByIdMap = new HashMap<>();
        this.stageByIdMap = new HashMap<>();
        this.levelByIdMap = new HashMap<>();
        this.rewardByIdMap = new HashMap<>();
        this.lootChestByIdMap = new HashMap<>();
        this.spotByIdMap = new HashMap<>();
        this.features = new Features(this);
        this.gameSettings = new GameSettings(this);
        this.lobbyPos = ExactPos.empty();
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        this.setWorldName(String.valueOf(config.getString("WorldName")));
        this.setStartLevelId(String.valueOf(config.getString("StartLevel")));
        this.setStartStageId(String.valueOf(config.getString("StartStage")));
        this.setName(config.getString("Name", StringUtil.capitalizeUnderscored(this.getId())));
        this.setDescription(config.getStringList("Description"));
        this.setPrefix(config.getString("Prefix", Tags.GRAY.wrap("[" + Placeholders.DUNGEON_NAME + "]")));
        this.setIcon(config.getCosmeticItem("Icon"));
        this.setLobbyPos(ExactPos.read(config, "LobbyPos"));

        BlockPos cMin = BlockPos.read(config, "Cuboid.Min");
        BlockPos cMax = BlockPos.read(config, "Cuboid.Max");
        this.setCuboid(new Cuboid(cMin, cMax));

        config.getSection("Spawners").forEach(sId -> {
            DungeonMobSpawner spawner = DungeonMobSpawner.read(config, "Spawners." + sId, sId);
            this.addSpawner(spawner);
        });

        this.loadStages();
        this.loadLevels();
        this.loadRewards();
        this.loadLootChests();
        this.loadSpots();

        this.features.load(config, "Features");
        this.gameSettings.load(config, "Game");

        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("WorldName", this.worldName);
        config.set("StartLevel", this.startLevelId);
        config.set("StartStage", this.startStageId);
        config.set("Name", this.name);
        config.set("Description", this.description);
        config.set("Prefix", this.prefix);
        config.set("Icon", this.getIcon());
        this.lobbyPos.write(config, "LobbyPos");

        this.cuboid.getMin().write(config, "Cuboid.Min");
        this.cuboid.getMax().write(config, "Cuboid.Max");

        this.writeSpawners(config);

        //config.remove("Features");
        //config.remove("Game");
        config.set("Features", this.features);
        config.set("Game", this.gameSettings);
    }

    public void saveSpawners() {
        this.writeSection(this::writeSpawners);
    }

    private void writeSection(@NotNull Consumer<FileConfig> consumer) {
        FileConfig config = this.getConfig();
        consumer.accept(config);
        config.saveChanges();
    }

    private void writeSpawners(@NotNull FileConfig config) {
        config.remove("Spawners");
        this.spawnerByIdMap.forEach((id, spawner) -> config.set("Spawners." + id, spawner));
    }

    @NotNull
    public String getFolderPath() {
        return this.file.getParentFile().getAbsolutePath();
    }

    @NotNull
    public String getStagesPath() {
        return this.getSubPath(Config.DIR_STAGES);
    }

    @NotNull
    public String getLevelsPath() {
        return this.getSubPath(Config.DIR_LEVELS);
    }

    @NotNull
    public String getRewardsPath() {
        return this.getSubPath(Config.DIR_REWARDS);
    }

    @NotNull
    public String getLootChestsPath() {
        return this.getSubPath(Config.DIR_LOOT_CHESTS);
    }

    @NotNull
    public String getSpotsPath() {
        return this.getSubPath(Config.DIR_SPOTS);
    }

    @NotNull
    private String getSubPath(@NotNull String sub) {
        return this.getFolderPath() + sub;
    }

    private void loadStages() {
        this.loadData(this.getStagesPath(), Stage::new, this::addStage);
        this.plugin.info("Loaded " + this.stageByIdMap.size() + " stages for the '" + this.getId() + "' dungeon.");
    }

    private void loadLevels() {
        this.loadData(this.getLevelsPath(), Level::new, this::addLevel);
        this.plugin.info("Loaded " + this.levelByIdMap.size() + " levels for the '" + this.getId() + "' dungeon.");
    }

    private void loadRewards() {
        this.loadData(this.getRewardsPath(), Reward::new, this::addReward);
        this.plugin.info("Loaded " + this.rewardByIdMap.size() + " rewards for the '" + this.getId() + "' dungeon.");
    }

    private void loadLootChests() {
        this.loadData(this.getLootChestsPath(), LootChest::new, this::addLootChest);
        this.plugin.info("Loaded " + this.lootChestByIdMap.size() + " loot chests for the '" + this.getId() + "' dungeon.");
    }

    private void loadSpots() {
        this.loadData(this.getSpotsPath(), Spot::new, this::addSpot);
        this.plugin.info("Loaded " + this.spotByIdMap.size() + " spots for the '" + this.getId() + "' dungeon.");
    }

    private <T extends AbstractFileData<DungeonPlugin>> void loadData(@NotNull String path, @NotNull BiFunction<DungeonPlugin, File, T> function, @NotNull Consumer<T> onLoad) {
        for (File file : this.getFilesInDirectory(path)) {
            T spot = function.apply(this.plugin, file);
            if (spot.load()) {
                onLoad.accept(spot);
            }
            else this.plugin.error("Could not load: '" + file.getPath() + "'.");
        }
    }

    @NotNull
    private List<File> getFilesInDirectory(@NotNull String path) {
        File dir = new File(path);
        dir.mkdirs();

        return FileUtil.getConfigFiles(dir.getAbsolutePath(), true);
    }

    public boolean isBroken() {
        return this.broken;
    }

    public void validate() {
        this.broken = false;
        FileConfig config = this.getConfig();

        if (this.getStartLevel() == null) {
            ErrorHandler.error("Invalid start level '" + this.startLevelId + "'!", config);
            this.broken = true;
        }

        if (this.getStartStage() == null) {
            ErrorHandler.error("Invalid start stage '" + this.startStageId + "'!", config);
            this.broken = true;
        }

        if (this.cuboid.isEmpty()) {
            ErrorHandler.error("No protection defined!", config);
            this.broken = true;
        }

        if (this.lobbyPos.isEmpty()) {
            ErrorHandler.error("No lobby position set!", config);
            this.broken = true;
        }
        else if (!this.cuboid.contains(this.lobbyPos)) {
            ErrorHandler.error("Lobby position is outside of the protection!", config);
            this.broken = true;
        }

        this.getSpawners().forEach(spawner -> {
            spawner.getPositions().forEach(blockPos -> {
                if (!this.cuboid.contains(blockPos)) {
                    ErrorHandler.error("Position '" + blockPos + "' of spawner '" + spawner.getId() + "' is outside of the dungeon protection.", config, "Spawners -> " + spawner.getId());
                }
            });
        });

        this.getLevels().forEach(level -> {
            if (!this.cuboid.contains(level.getSpawnPos().toBlockPos())) {
                ErrorHandler.error("Spawn position of level '" + level.getId() + "' is outside of the dungeon protection.", level.getConfig());
                this.broken = true;
            }
        });

        this.getLootChests().forEach(lootChest -> {
            if (!this.cuboid.contains(lootChest.getBlockPos())) {
                ErrorHandler.error("Block position of the '" + lootChest.getId() + "' loot chest is outside of the dungeon protection.", lootChest.getConfig());
            }
        });

        if (this.broken) {
            this.plugin.error("Dungeon '" + this.getId() + "' has configuration problems and can't be used until fixed.");
        }
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return Placeholders.DUNGEON_CONFIG.replacer(this);
    }

    public boolean isInProtection(@NotNull BlockPos blockPos) {
        return this.cuboid.contains(blockPos);
    }

    public boolean isInProtection(@NotNull Block block) {
        return this.isInProtection(BlockPos.from(block));
    }

    public boolean isInProtection(@NotNull Location location) {
        return this.cuboid.contains(location);
    }

    public boolean isInProtection(@NotNull Entity entity) {
        return this.isInProtection(entity.getLocation());
    }

    public boolean isWorld(@NotNull World world) {
        return this.worldName.equalsIgnoreCase(world.getName());
    }

    @NotNull
    public Set<DungeonPos> getDungeonPositions() {
        return this.cuboid.getIntersectingChunkPositions().stream().map(pos -> new DungeonPos(this.worldName, pos)).collect(Collectors.toSet());
    }




    @NotNull
    public DungeonInstance getInstance() {
        if (this.instance == null) throw new IllegalStateException("Dungeon instance is not set!");

        return this.instance;
    }

    public void setInstance(@NotNull DungeonInstance instance) {
        this.instance = instance;
    }

    @NotNull
    public Features features() {
        return this.features;
    }

    @NotNull
    public GameSettings gameSettings() {
        return this.gameSettings;
    }



    @NotNull
    public String getWorldName() {
        return this.worldName;
    }

    public void setWorldName(@NotNull String worldName) {
        this.worldName = worldName;
    }

    public Level getStartLevel() {
        return this.getLevelById(this.startLevelId);
    }

    public Stage getStartStage() {
        return this.getStageById(this.startStageId);
    }

    @NotNull
    public String getStartLevelId() {
        return this.startLevelId;
    }

    public void setStartLevelId(@NotNull String startLevelId) {
        this.startLevelId = startLevelId;
    }

    @NotNull
    public String getStartStageId() {
        return startStageId;
    }

    public void setStartStageId(@NotNull String startStageId) {
        this.startStageId = startStageId;
    }

    @NotNull
    public Cuboid getCuboid() {
        return this.cuboid;
    }

    public void setCuboid(@NotNull Cuboid cuboid) {
        this.cuboid = cuboid;
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
    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
    }

    @NotNull
    public NightItem getIcon() {
        return this.icon.copy();
    }

    public void setIcon(@NotNull NightItem icon) {
        this.icon = icon.copy().setHideComponents(true);
    }

    @NotNull
    public ExactPos getLobbyPos() {
        return this.lobbyPos;
    }

    public void setLobbyPos(@NotNull ExactPos lobbyPos) {
        this.lobbyPos = lobbyPos;
    }

    public void addSpawner(@NotNull DungeonMobSpawner spawner) {
        this.spawnerByIdMap.put(spawner.getId(), spawner);
    }

    @NotNull
    public Map<String, DungeonMobSpawner> getSpawnerByIdMap() {
        return this.spawnerByIdMap;
    }

    @NotNull
    public Set<DungeonMobSpawner> getSpawners() {
        return new HashSet<>(this.spawnerByIdMap.values());
    }

    @Nullable
    public DungeonMobSpawner getSpawnerById(@NotNull String id) {
        return this.spawnerByIdMap.get(id.toLowerCase());
    }

    public void addStage(@NotNull Stage stage) {
        this.stageByIdMap.put(stage.getId(), stage);
    }

    @NotNull
    public Map<String, Stage> getStageByIdMap() {
        return this.stageByIdMap;
    }

    @NotNull
    public Set<Stage> getStages() {
        return new HashSet<>(this.stageByIdMap.values());
    }

    @Nullable
    public Stage getStageById(@NotNull String id) {
        return this.stageByIdMap.get(id.toLowerCase());
    }

    @NotNull
    public Map<String, Level> getLevelByIdMap() {
        return this.levelByIdMap;
    }

    @NotNull
    public Set<Level> getLevels() {
        return new HashSet<>(this.levelByIdMap.values());
    }

    @Nullable
    public Level getLevelById(@NotNull String id) {
        return this.levelByIdMap.get(id.toLowerCase());
    }

    public void addLevel(@NotNull Level level) {
        this.levelByIdMap.put(level.getId(), level);
    }


    @NotNull
    public Map<String, Reward> getRewardByIdMap() {
        return this.rewardByIdMap;
    }

    @NotNull
    public Set<Reward> getRewards() {
        return new HashSet<>(this.rewardByIdMap.values());
    }

    @Nullable
    public Reward getRewardById(@NotNull String id) {
        return this.rewardByIdMap.get(id.toLowerCase());
    }

    public void addReward(@NotNull Reward reward) {
        this.rewardByIdMap.put(reward.getId(), reward);
    }

    public void removeReward(@NotNull String id) {
        this.rewardByIdMap.remove(id);
    }


    @NotNull
    public Map<String, LootChest> getLootChestByIdMap() {
        return this.lootChestByIdMap;
    }

    @NotNull
    public Set<LootChest> getLootChests() {
        return new HashSet<>(this.lootChestByIdMap.values());
    }

    @Nullable
    public LootChest getLootChestById(@NotNull String id) {
        return this.lootChestByIdMap.get(id.toLowerCase());
    }

    public void addLootChest(@NotNull LootChest lootChest) {
        this.lootChestByIdMap.put(lootChest.getId(), lootChest);
    }

    public void removeLootChest(@NotNull String id) {
        this.lootChestByIdMap.remove(id);
    }



    @NotNull
    public Map<String, Spot> getSpotByIdMap() {
        return this.spotByIdMap;
    }

    @NotNull
    public Set<Spot> getSpots() {
        return new HashSet<>(this.spotByIdMap.values());
    }

    @Nullable
    public Spot getSpotById(@NotNull String id) {
        return this.spotByIdMap.get(id.toLowerCase());
    }

    public void addSpot(@NotNull Spot spot) {
        this.spotByIdMap.put(spot.getId(), spot);
    }

    public void removeSpot(@NotNull String id) {
        this.spotByIdMap.remove(id);
    }
}
