package su.nightexpress.dungeons.dungeon.module;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.config.DungeonConfig;
import su.nightexpress.dungeons.dungeon.feature.LevelRequirement;
import su.nightexpress.dungeons.util.DungeonUtils;
import su.nightexpress.economybridge.currency.CurrencyId;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.rankmap.IntRankMap;

import java.util.LinkedHashMap;
import java.util.Map;

public class Features implements Writeable {

    //private final DungeonConfig dungeonConfig;

    private       boolean             permissionRequired;
    private       IntRankMap          entranceCooldown;
    private       IntRankMap          entranceVictoryCooldown;
    private       IntRankMap          entranceDefeatCooldown;
    private final Map<String, Double> entranceCostMap;
    private LevelRequirement levelRequirement;

    public Features(@NotNull DungeonConfig dungeonConfig) {
        //this.dungeonConfig = dungeonConfig;

        this.entranceCooldown = IntRankMap.ranked(0).addValue("admin", 0);
        this.entranceVictoryCooldown = IntRankMap.ranked(0).addValue("admin", 0);
        this.entranceDefeatCooldown = IntRankMap.ranked(0).addValue("admin", 0);
        this.entranceCostMap = new LinkedHashMap<>(); // Linked to keep currency order in placeholders.
        if (DungeonUtils.hasEconomyBridge()) {
            this.entranceCostMap.put(CurrencyId.VAULT, 0D);
        }
    }

    public void load(@NotNull FileConfig config, @NotNull String path) {
        this.setPermissionRequired(config.getBoolean(path + ".Permission_Required"));

        this.entranceCooldown = ConfigValue.create(path + ".Entrance.Cooldown", IntRankMap::read, this.entranceCooldown).read(config);
        this.entranceVictoryCooldown = ConfigValue.create(path + ".Entrance.Victory_Cooldown", IntRankMap::read, this.entranceVictoryCooldown).read(config);
        this.entranceDefeatCooldown = ConfigValue.create(path + ".Entrance.Defeat_Cooldown", IntRankMap::read, this.entranceDefeatCooldown).read(config);

        this.entranceCostMap.clear();
        if (DungeonUtils.hasEconomyBridge()) {
            config.getSection(path + ".Entrance.Payment").forEach(curId -> {
                double price = config.getDouble(path + ".Entrance.Payment." + curId);
                if (price <= 0) return;

                this.entranceCostMap.put(curId.toLowerCase(), price);
            });
        }

        this.levelRequirement = LevelRequirement.read(config, path + ".LevelRequirement");
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Permission_Required", this.permissionRequired);
        this.entranceCooldown.write(config, path + ".Entrance.Cooldown");
        this.entranceVictoryCooldown.write(config, path + ".Entrance.Victory_Cooldown");
        this.entranceDefeatCooldown.write(config, path + ".Entrance.Defeat_Cooldown");
        config.set(path + ".LevelRequirement", this.levelRequirement);

        if (DungeonUtils.hasEconomyBridge()) {
            config.remove(path + "Entrance.Payment");
            this.entranceCostMap.forEach((id, price) -> config.set(path + ".Entrance.Payment." + id, price));
        }
    }

    public boolean hasEntranceCost() {
        return !this.entranceCostMap.isEmpty();
    }

    public boolean isPermissionRequired() {
        return this.permissionRequired;
    }

    public void setPermissionRequired(boolean permissionRequired) {
        this.permissionRequired = permissionRequired;
    }

    @NotNull
    public IntRankMap getEntranceCooldown() {
        return this.entranceCooldown;
    }

    @NotNull
    public IntRankMap getEntranceVictoryCooldown() {
        return this.entranceVictoryCooldown;
    }

    @NotNull
    public IntRankMap getEntranceDefeatCooldown() {
        return this.entranceDefeatCooldown;
    }

    @NotNull
    public Map<String, Double> getEntranceCostMap() {
        return this.entranceCostMap;
    }

    @NotNull
    public LevelRequirement getLevelRequirement() {
        return this.levelRequirement;
    }
}
