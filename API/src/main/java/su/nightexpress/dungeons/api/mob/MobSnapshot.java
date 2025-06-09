package su.nightexpress.dungeons.api.mob;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.criteria.CriterionMob;
import su.nightexpress.dungeons.api.type.MobFaction;

import java.util.Objects;

public class MobSnapshot implements CriterionMob {

    private final String     providerId;
    private final String     mobId;
    private final MobFaction faction;
    private final String     bornStageId;

    public MobSnapshot(String providerId, String mobId, MobFaction faction, String bornStageId) {
        this.providerId = providerId;
        this.mobId = mobId;
        this.faction = faction;
        this.bornStageId = bornStageId;
    }

    @Override
    public boolean isMob(@NotNull MobProvider provider, @NotNull String mobId) {
        return this.isProvider(provider) && this.isId(mobId);
    }

    @Override
    public boolean isMob(@NotNull MobIdentifier identifier) {
        return this.isProvider(identifier.getProviderId()) && this.isId(identifier.getMobId());
    }

    @Override
    public boolean isId(@NotNull String mobId) {
        return this.mobId.equalsIgnoreCase(mobId);
    }

    @Override
    public boolean isProvider(@NotNull MobProvider provider) {
        return this.isProvider(provider.getName());
    }

    @Override
    public boolean isProvider(@NotNull String providerId) {
        return this.providerId.equalsIgnoreCase(providerId);
    }

    @Override
    public boolean isFaction(@NotNull MobFaction faction) {
        return this.faction == faction;
    }

    @NotNull
    public String getProviderId() {
        return this.providerId;
    }

    @NotNull
    public String getMobId() {
        return this.mobId;
    }

    @NotNull
    public MobFaction getFaction() {
        return this.faction;
    }

    @NotNull
    public String getBornStageId() {
        return this.bornStageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MobSnapshot that)) return false;
        return Objects.equals(providerId, that.providerId) && Objects.equals(mobId, that.mobId) && faction == that.faction && Objects.equals(bornStageId, that.bornStageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerId, mobId, faction, bornStageId);
    }

    @Override
    public String toString() {
        return "MobSnapshot{" +
            "providerId='" + providerId + '\'' +
            ", mobId='" + mobId + '\'' +
            ", faction=" + faction +
            ", stageId='" + bornStageId + '\'' +
            '}';
    }
}
