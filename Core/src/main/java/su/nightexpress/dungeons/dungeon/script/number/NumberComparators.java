package su.nightexpress.dungeons.dungeon.script.number;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class NumberComparators {

    public static final NumberComparator DUMMY = NumberComparator.create("dummy", (value, comapareWith) -> false);

    private static final Map<String, NumberComparator> BY_NAME = new HashMap<>();
    private static final Map<String, NumberComparator> BY_OPERATOR = new HashMap<>();

    public static void load() {
        register(">=", NumberComparator.create("greater_or_equal", (value, compareWith) -> value >= compareWith));
        register(">", NumberComparator.create("greater_than", (value, compareWith) -> value > compareWith));
        register("=", NumberComparator.create("equal", (value, compareWith) -> value.intValue() == compareWith.intValue()));
        register("!=", NumberComparator.create("not_equal", (value, compareWith) -> value.intValue() != compareWith.intValue()));
        register("<=", NumberComparator.create("less_or_equal", (value, compareWith) -> value <= compareWith));
        register("<", NumberComparator.create("less_than", (value, compareWith) -> value < compareWith));
        register("?", DUMMY);
    }

    public static void clear() {
        BY_NAME.clear();
        BY_OPERATOR.clear();
    }

    public static void register(@NotNull String operator, @NotNull NumberComparator comparator) {
        BY_NAME.put(comparator.getName().toLowerCase(), comparator);
        BY_OPERATOR.put(operator.toLowerCase(), comparator);
    }

    @Nullable
    public static NumberComparator getComparator(@NotNull String str) {
        NumberComparator byName = getByName(str);
        return byName == null ? getByOperator(str) : byName;
    }

    @Nullable
    public static NumberComparator getByName(@NotNull String name) {
        return BY_NAME.get(name.toLowerCase());
    }

    @Nullable
    public static NumberComparator getByOperator(@NotNull String operator) {
        return BY_OPERATOR.get(operator.toLowerCase());
    }
}
