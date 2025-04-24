package su.nightexpress.dungeons.dungeon.script.condition.type;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.mob.MobIdentifier;
import su.nightexpress.dungeons.dungeon.script.number.NumberComparator;
import su.nightexpress.nightcore.config.FileConfig;

public abstract class MobAmountCondition extends NumberCompareCondition {

    protected final MobIdentifier identifier;

    public MobAmountCondition(@NotNull NumberComparator comparator, int compareValue, @NotNull MobIdentifier identifier) {
        super(comparator, compareValue);
        this.identifier = identifier;
    }

    @NotNull
    protected static <T extends MobAmountCondition> T read(@NotNull FileConfig config, @NotNull String path, @NotNull Creator<T> creator) {
        MobIdentifier mobId = MobIdentifier.read(config, path + ".MobId");

        return read(config, path, (comparator, compareValue) -> creator.create(comparator, compareValue, mobId));
    }

    @Override
    protected void writeAdditional(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".MobId", this.identifier.serialize());
    }

    protected interface Creator<T extends MobAmountCondition> {

        T create(@NotNull NumberComparator comparator, int compareValue, @NotNull MobIdentifier mobId);
    }
}
