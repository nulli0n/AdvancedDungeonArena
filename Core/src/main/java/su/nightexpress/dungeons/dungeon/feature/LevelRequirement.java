package su.nightexpress.dungeons.dungeon.feature;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.registry.level.LevelProvider;
import su.nightexpress.dungeons.registry.level.LevelRegistry;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

public class LevelRequirement implements Writeable {

    private final String provider;
    private final int minLevel;
    private final int maxLevel;

    public LevelRequirement(@NotNull String provider, int minLevel, int maxLevel) {
        this.provider = provider.toLowerCase();
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    @NotNull
    public static LevelRequirement read(@NotNull FileConfig config, @NotNull String path) {
        String provider = ConfigValue.create(path + ".Provider", "null").read(config);
        int minLevel = ConfigValue.create(path + ".MinLevel", -1).read(config);
        int maxLevel = ConfigValue.create(path + ".MaxLevel", -1).read(config);

        return new LevelRequirement(provider, minLevel, maxLevel);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Provider", this.provider);
        config.set(path + ".MinLevel", this.minLevel);
        config.set(path + ".MaxLevel", this.maxLevel);
    }

    public boolean isGoodLevel(@NotNull Player player) {
        LevelProvider levelProvider = LevelRegistry.getProvider(this.provider);
        if (levelProvider == null) return true;

        int playerLevel = levelProvider.getLevel(player);
        boolean underMin = !this.hasMinValue() || playerLevel >= this.minLevel;
        boolean underMax = !this.hasMaxValue() || playerLevel <= this.maxLevel;

        return underMin && underMax;
    }

    public boolean isRequired() {
        return this.hasMinValue() || this.hasMaxValue();
    }

    public boolean hasMinValue() {
        return this.minLevel > 0;
    }

    public boolean hasMaxValue() {
        return this.maxLevel > 0;
    }

    @NotNull
    public String getProvider() {
        return this.provider;
    }

    public int getMinLevel() {
        return this.minLevel;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }
}
