package su.nightexpress.dungeons.dungeon.script.condition.type;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.mob.MobIdentifier;
import su.nightexpress.dungeons.dungeon.script.number.NumberComparator;
import su.nightexpress.nightcore.config.FileConfig;

@Deprecated
public abstract class MobAmountCondition extends NumberCompareCondition {

    protected final MobIdentifier identifier;

    public record MobData(NumberComparator comparator, double compareValue, MobIdentifier identifier){}

    protected MobAmountCondition(@NotNull MobData mobData) {
        this(mobData.comparator, mobData.compareValue, mobData.identifier);
    }

    public MobAmountCondition(@NotNull NumberComparator comparator, double compareValue, @NotNull MobIdentifier identifier) {
        super(comparator, compareValue);
        this.identifier = identifier;
    }

    @NotNull
    public static MobData readMobData(@NotNull FileConfig config, @NotNull String path) {
        NumberData numberData = readNumberData(config, path);
        MobIdentifier mobId = MobIdentifier.read(config, path + ".MobId");

        return new MobData(numberData.comparator(), numberData.compareValue(), mobId);
    }

    @Override
    protected void writeAdditional(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".MobId", this.identifier.serialize());
    }
}
