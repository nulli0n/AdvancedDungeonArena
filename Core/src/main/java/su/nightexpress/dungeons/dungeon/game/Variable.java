package su.nightexpress.dungeons.dungeon.game;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.NumberUtil;

import java.util.function.Function;

public class Variable {

    private final double initial;
    private final boolean limited;
    private final double min;
    private final double max;

    private double value;

    public Variable(double initial, boolean limited, double min, double max) {
        this.initial = initial;
        this.limited = limited;
        this.min = min;
        this.max = max;

        this.setValue(initial);
    }

    public void modify(@NotNull Function<Double, Double> function) {
        this.setValue(function.apply(this.value));
    }

    public void reset() {
        this.value = initial;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = this.limited ? NumberUtil.clamp(value, this.min, this.max) : value;
    }

    public double getInitial() {
        return this.initial;
    }

    public boolean isLimited() {
        return this.limited;
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }
}
