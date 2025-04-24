package su.nightexpress.dungeons.mob.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

public class AttributeScale implements Writeable {

    private final double initial;
    private final double perLevel;
    private final double minValue;
    private final double maxValue;

    public AttributeScale(double initial, double perLevel, double minValue, double maxValue) {
        this.initial = initial;
        this.perLevel = perLevel;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @NotNull
    public static AttributeScale read(@NotNull FileConfig config, @NotNull String path) {
        double initial = ConfigValue.create(path + ".Initial", 0D).read(config);
        double perLevel = ConfigValue.create(path + ".PerLevel", 0D).read(config);
        double min = ConfigValue.create(path + ".Min", 0D).read(config);
        double max = ConfigValue.create(path + ".Max", 0D).read(config);

        return new AttributeScale(initial, perLevel, min, max);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Initial", this.initial);
        config.set(path + ".PerLevel", this.perLevel);
        config.set(path + ".Min", this.minValue);
        config.set(path + ".Max", this.maxValue);
    }

    public double getInitial() {
        return this.initial;
    }

    public double getPerLevel() {
        return this.perLevel;
    }

    public double getMinValue() {
        return this.minValue;
    }

    public double getMaxValue() {
        return this.maxValue;
    }
}
