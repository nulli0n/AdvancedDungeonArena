package su.nightexpress.dungeons.dungeon.criteria.registry.mob;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.criteria.registry.CriteriaHolder;
import su.nightexpress.dungeons.dungeon.criteria.registry.mob.impl.MobFactionCriteria;
import su.nightexpress.dungeons.dungeon.criteria.registry.mob.impl.MobIdCriteria;
import su.nightexpress.dungeons.dungeon.criteria.registry.mob.impl.MobProviderCriteria;
import su.nightexpress.dungeons.dungeon.criteria.registry.mob.impl.MobStageCriteria;

public class MobCriterias {

    public static final MobIdCriteria       ID         = new MobIdCriteria("id");
    public static final MobProviderCriteria PROVIER    = new MobProviderCriteria("provider");
    public static final MobFactionCriteria  FACTION    = new MobFactionCriteria("faction");
    public static final MobStageCriteria    BORN_STAGE = new MobStageCriteria("born_stage");

    public static void setup(@NotNull CriteriaHolder<MobCriteria<?>> holder) {
        holder.register(ID);
        holder.register(PROVIER);
        holder.register(FACTION);
        holder.register(BORN_STAGE);
    }
}
