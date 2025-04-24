package su.nightexpress.dungeons.dungeon.script.number;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public interface NumberComparator {

    @NotNull String getName();

    boolean test(int value, int compareWith);

    @NotNull
    static NumberComparator create(@NotNull String name, @NotNull BiPredicate<Integer, Integer> predicate) {
        return new NumberComparator() {

            @NotNull
            @Override
            public String getName() {
                return name;
            }

            @Override
            public boolean test(int value, int compareWith) {
                return predicate.test(value, compareWith);
            }
        };
    }
}
