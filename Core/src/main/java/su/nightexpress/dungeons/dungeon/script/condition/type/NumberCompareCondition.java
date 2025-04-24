package su.nightexpress.dungeons.dungeon.script.condition.type;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.script.condition.Condition;
import su.nightexpress.dungeons.dungeon.script.number.NumberComparator;
import su.nightexpress.dungeons.dungeon.script.number.NumberComparators;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.FileConfig;

public abstract class NumberCompareCondition implements Condition {

    protected final NumberComparator comparator;
    protected final int compareValue;

    public NumberCompareCondition(@NotNull NumberComparator comparator, int compareValue) {
        this.comparator = comparator;
        this.compareValue = compareValue;
    }

    @NotNull
    protected static <T extends NumberCompareCondition> T read(@NotNull FileConfig config, @NotNull String path, @NotNull Creator<T> creator) {
        String operatorStr = config.getString(path + ".Operator", "null");
        NumberComparator comparator = NumberComparators.getComparator(operatorStr);
        if (comparator == null) {
            ErrorHandler.error("Invalid number comparing operator '" + operatorStr + "'!", config, path);
            comparator = NumberComparators.DUMMY;
        }

        int compareValue = config.getInt(path + ".Value");

        return creator.create(comparator, compareValue);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Operator", this.comparator.getName());
        config.set(path + ".Value", this.compareValue);
        this.writeAdditional(config, path);
    }

    protected abstract void writeAdditional(@NotNull FileConfig config, @NotNull String path);

    @Override
    public boolean test(@NotNull DungeonInstance dungeon, @NotNull DungeonGameEvent event) {
        int dungeonValue = this.getDungeonValue(dungeon);

        return this.comparator.test(dungeonValue, this.compareValue);
    }

    protected abstract int getDungeonValue(@NotNull DungeonInstance dungeon);

    protected interface Creator<T extends NumberCompareCondition> {

        T create(@NotNull NumberComparator comparator, int compareValue);
    }
}
