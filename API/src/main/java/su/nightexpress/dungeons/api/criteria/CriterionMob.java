package su.nightexpress.dungeons.api.criteria;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.mob.MobIdentifier;
import su.nightexpress.dungeons.api.mob.MobProvider;
import su.nightexpress.dungeons.api.type.MobFaction;

public interface CriterionMob {

    boolean isMob(@NotNull MobProvider provider, @NotNull String mobId);

    boolean isMob(@NotNull MobIdentifier identifier);

    boolean isId(@NotNull String mobId);

    boolean isProvider(@NotNull MobProvider provider);

    boolean isProvider(@NotNull String providerId);

    boolean isFaction(@NotNull MobFaction faction);

    @NotNull String getProviderId();

    @NotNull String getMobId();

    @NotNull MobFaction getFaction();

    @NotNull String getBornStageId();
}
