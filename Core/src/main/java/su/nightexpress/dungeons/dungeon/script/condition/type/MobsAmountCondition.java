package su.nightexpress.dungeons.dungeon.script.condition.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.dungeon.script.number.NumberComparator;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public abstract class MobsAmountCondition extends NumberCompareCondition {

    protected final boolean checkFaction;
    protected final MobFaction faction;

    public MobsAmountCondition(@NotNull NumberComparator comparator, int compareValue, boolean checkFaction, @Nullable MobFaction faction) {
        super(comparator, compareValue);
        this.checkFaction = checkFaction;
        this.faction = faction;
    }

    @NotNull
    protected static <T extends MobsAmountCondition> T read(@NotNull FileConfig config, @NotNull String path, @NotNull Creator<T> creator) {
        boolean checkFaction = ConfigValue.create(path + ".CheckFaction", false).read(config);
        MobFaction faction = ConfigValue.create(path + ".Faction", MobFaction.class, MobFaction.ENEMY).read(config);

        return read(config, path, (comparator, compareValue) -> creator.create(comparator, compareValue, checkFaction, faction));
    }

    @Override
    protected void writeAdditional(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".CheckFaction", this.checkFaction);
        config.set(path + ".Faction", this.faction == null ? null : this.faction.name());
    }

    protected interface Creator<T extends MobsAmountCondition> {

        T create(@NotNull NumberComparator comparator, int compareValue, boolean checkFaction, @Nullable MobFaction faction);
    }
}
