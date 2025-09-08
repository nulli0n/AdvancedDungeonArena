package su.nightexpress.dungeons.dungeon.script.number;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public interface NumberComparator {

    @NotNull String getName();

    boolean test(double value, double compareWith);

    @NotNull
    static NumberComparator create(@NotNull String name, @NotNull BiPredicate<Double, Double> predicate) {
        return new NumberComparator() {

            @NotNull
            @Override
            public String getName() {
                return name;
            }

            @Override
            public boolean test(double value, double compareWith) {
                return predicate.test(value, compareWith);
            }
        };
    }
}
