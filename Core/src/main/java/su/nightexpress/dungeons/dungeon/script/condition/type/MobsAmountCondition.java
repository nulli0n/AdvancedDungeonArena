package su.nightexpress.dungeons.dungeon.script.condition.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.criteria.CriterionMob;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.dungeon.script.number.NumberComparator;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.function.Predicate;

@Deprecated
public abstract class MobsAmountCondition extends NumberCompareCondition {

    protected final boolean checkFaction;
    protected final MobFaction faction;

    public record MobsData(NumberComparator comparator, double compareValue, boolean checkFaction, MobFaction faction){}

    protected MobsAmountCondition(@NotNull MobsData data) {
        this(data.comparator, data.compareValue, data.checkFaction, data.faction);
    }

    public MobsAmountCondition(@NotNull NumberComparator comparator, double compareValue, boolean checkFaction, @Nullable MobFaction faction) {
        super(comparator, compareValue);
        this.checkFaction = checkFaction;
        this.faction = faction;
    }

    @NotNull
    public static MobsData readMobsData(@NotNull FileConfig config, @NotNull String path) {
        NumberData numberData = readNumberData(config, path);
        boolean checkFaction = ConfigValue.create(path + ".CheckFaction", false).read(config);
        MobFaction faction = ConfigValue.create(path + ".Faction", MobFaction.class, MobFaction.ENEMY).read(config);

        return new MobsData(numberData.comparator(), numberData.compareValue(), checkFaction, faction);
    }

    @Override
    protected void writeAdditional(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".CheckFaction", this.checkFaction);
        config.set(path + ".Faction", this.faction == null ? null : this.faction.name());
    }

    @Nullable
    public MobFaction getFactionLookup() {
        return this.checkFaction ? this.faction : null;
    }

    public static Predicate<CriterionMob> byFaction(@Nullable MobFaction faction) {
        return mob -> faction == null || mob.isFaction(faction);
    }
}
